package shop.babsim.babsim.place.domain.repository.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import shop.babsim.babsim.place.api.dto.response.PlaceSearchResDto;
import shop.babsim.babsim.place.domain.PlaceDocument;

@Repository
@RequiredArgsConstructor
public class PlaceSearchRepositoryImpl implements PlaceSearchCustomRepository {

    private final ElasticsearchClient elasticsearchClient;

    @Override
    public Page<PlaceSearchResDto> searchByMenuOrBusinessName(String keyword, Pageable pageable) {
        try {
            SearchResponse<PlaceDocument> response = elasticsearchClient.search(
                    s -> s.index("place")
                            .query(q -> q
                                    .bool(b -> b
                                            .should(s1 -> s1.match(m -> m.field("menu1").query(keyword)))
                                            .should(s2 -> s2.match(m -> m.field("menu2").query(keyword)))
                                            .should(s3 -> s3.match(m -> m.field("businessName").query(keyword)))
                                    )
                            )
                            .from((int) pageable.getOffset())
                            .size(pageable.getPageSize()),
                    PlaceDocument.class
            );

            // ✅ 결과 매핑 수정
            List<PlaceSearchResDto> result = response.hits().hits().stream()
                    .map(Hit::source).filter(Objects::nonNull) // ✅ PlaceDocument로 변환
                    .map(document -> new PlaceSearchResDto(document.getBusinessName())) // ✅ 이름만 매핑
                    .toList();

            // ✅ 페이징 처리에서 TotalHits에서 value 값만 추출
            long totalHits = response.hits().total() != null ? response.hits().total().value() : 0;

            return PageableExecutionUtils.getPage(result, pageable, () -> totalHits);

        } catch (Exception e) {
            throw new RuntimeException("Failed to search data in Elasticsearch", e);
        }
    }
}
