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
import shop.babsim.babsim.place.domain.repository.elasticsearch.PlaceSearchRepository;

@Slf4j
@Configuration
@EnableAsync
@RequiredArgsConstructor
@Transactional
public class CsvScheduleWriter implements ItemWriter<PlaceCsvData> {

    private final PlaceRepository placeRepository;
    private final PlaceSearchRepository placeSearchRepository;

    @Override
    public void write(Chunk<? extends PlaceCsvData> chunk) {
        List<Place> places = chunk.getItems().stream()
                .map(Place::from)
                .collect(Collectors.toList());

        placeRepository.saveAll(places);
        log.info("✅ RDB 저장 완료: {}개 데이터", places.size());

        List<PlaceDocument> placeDocuments = chunk.getItems().stream()
                .map(PlaceDocument::from)
                .collect(Collectors.toList());

        saveToElasticSearchAsync(placeDocuments);
    }

    // ✅ ElasticSearch 저장을 비동기 처리
    @Async
    public void saveToElasticSearchAsync(List<PlaceDocument> placeDocuments) {
        try {
            placeSearchRepository.saveAll(placeDocuments);
            log.info("✅ ElasticSearch 저장 완료: {}개 데이터", placeDocuments.size());
        } catch (Exception e) {
            log.error("❌ ElasticSearch 저장 실패: {}", e.getMessage());
        }
    }
}
