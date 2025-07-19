package shop.babsim.babsim.place.csv;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.Transactional;
import shop.babsim.babsim.place.csv.dto.PlaceCsvData;
import shop.babsim.babsim.place.domain.Place;
import shop.babsim.babsim.place.domain.PlaceDocument;
import shop.babsim.babsim.place.domain.repository.PlaceRepository;
//import shop.babsim.babsim.place.domain.repository.elasticsearch.PlaceSearchRepository;

@Slf4j
@Configuration
@EnableAsync
@RequiredArgsConstructor
@Transactional
public class CsvScheduleWriter implements ItemWriter<PlaceCsvData> {

    private final PlaceRepository placeRepository;
//    private final PlaceSearchRepository placeSearchRepository;

    @Override
    public void write(Chunk<? extends PlaceCsvData> chunk) {
        List<Place> places = chunk.getItems().stream()
                .map(Place::from)
                .collect(Collectors.toList());

        // ✅ 중복 제거 또는 업데이트 처리
        for (Place place : places) {
            if (placeRepository.existsByPlaceId(place.getPlaceId())) {
                placeRepository.deleteByPlaceId(place.getPlaceId());
                log.info("🔄 기존 placeId 삭제 후 갱신: {}", place.getPlaceId());
            }
        }

        placeRepository.saveAll(places);
        log.info("✅ RDB 저장 완료: {}개 데이터", places.size());
    }
}
