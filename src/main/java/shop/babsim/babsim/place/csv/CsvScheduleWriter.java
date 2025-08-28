package shop.babsim.babsim.place.csv;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.babsim.babsim.place.csv.dto.PlaceCsvData;
import shop.babsim.babsim.place.domain.Place;
import shop.babsim.babsim.place.domain.PlaceDocument;
import shop.babsim.babsim.place.domain.repository.PlaceRepository;
//import shop.babsim.babsim.place.domain.repository.elasticsearch.PlaceSearchRepository;

@Slf4j
@Component
@EnableAsync
@RequiredArgsConstructor
@Transactional
public class CsvScheduleWriter implements ItemWriter<Place> {

    private final PlaceRepository placeRepository;
//    private final PlaceSearchRepository placeSearchRepository;

    @Override
    public void write(Chunk<? extends Place> chunk) {
        List<? extends Place> places = chunk.getItems()
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<String> idsToDelete = places.stream()
                .map(Place::getPlaceId)
                .collect(Collectors.toList());

        // 기존 데이터 삭제
        placeRepository.deleteByPlaceIdIn(idsToDelete);

        // 새 데이터 저장
        placeRepository.saveAll(places);

        log.info("✅ 변경된 Place {}건 삭제 후 재저장 완료", places.size());
    }
}