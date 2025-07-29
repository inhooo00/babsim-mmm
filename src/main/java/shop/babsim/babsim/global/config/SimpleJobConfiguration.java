package shop.babsim.babsim.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import shop.babsim.babsim.place.csv.CsvReader;
import shop.babsim.babsim.place.csv.CsvScheduleWriter;
import shop.babsim.babsim.place.csv.config.CsvHashChangeProcessor;
import shop.babsim.babsim.place.csv.dto.PlaceCsvData;
import shop.babsim.babsim.place.domain.Place;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SimpleJobConfiguration {
    private final CsvReader csvReader;
    private final CsvScheduleWriter csvScheduleWriter;
    private final CsvHashChangeProcessor csvHashChangeProcessor;

    @Bean
    public Job shopDataLoadJob(JobRepository jobRepository, Step shopDataLoadStep) {
        return new JobBuilder("shopInformationLoadJob", jobRepository)
                .start(shopDataLoadStep)
                .build();
    }

    @Bean
    public Step shopDataLoadStep(
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager
    ) {
        return new StepBuilder("shopDataLoadStep", jobRepository)
                .<PlaceCsvData, Place>chunk(100, platformTransactionManager)
                .reader(csvReader.csvScheduleReader())
                .processor(csvHashChangeProcessor)
                .writer(csvScheduleWriter)
                .build();
    }
}