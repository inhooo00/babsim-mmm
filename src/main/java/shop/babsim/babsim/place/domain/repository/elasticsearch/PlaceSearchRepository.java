package shop.babsim.babsim.place.domain.repository.elasticsearch;


import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import shop.babsim.babsim.place.domain.PlaceDocument;

public interface PlaceSearchRepository extends ElasticsearchRepository<PlaceDocument, Long>
        ,PlaceSearchCustomRepository {
}
