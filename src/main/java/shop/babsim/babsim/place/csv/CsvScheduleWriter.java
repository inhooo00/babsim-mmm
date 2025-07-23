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
public class CsvScheduleWriter implements ItemWriter<Place> {

    private final PlaceRepository placeRepository;
//    private final PlaceSearchRepository placeSearchRepository;

    @Override
    public void write(Chunk<? extends Place> chunk) {
        List<? extends Place> places = chunk.getItems();
        placeRepository.saveAll(places);
        log.info("✅ 변경된 Place {}건 저장", places.size());
    }
}
