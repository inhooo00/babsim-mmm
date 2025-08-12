package shop.babsim.babsim.place.csv.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoggingChunkListener implements ChunkListener {

    @Override
    public void beforeChunk(ChunkContext context) {
        log.info("🟡 청크 시작");
    }

    @Override
    public void afterChunk(ChunkContext context) {
        log.info("🟢 청크 성공");
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        log.error("🔴 청크 실패");
    }
}

