package shop.babsim.babsim.place.csv.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LoggingStepListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("▶ Step 시작: {}", stepExecution.getStepName());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("■ Step 종료 상태: {}", stepExecution.getStatus());
        log.info("    - readCount: {}", stepExecution.getReadCount());
        log.info("    - writeCount: {}", stepExecution.getWriteCount());
        log.info("    - skipCount: {}", stepExecution.getSkipCount());
        return stepExecution.getExitStatus();
    }
}
