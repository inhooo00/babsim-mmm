package shop.babsim.babsim.place.csv;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class CsvJobRunner {

    private final JobLauncher jobLauncher;
    private final Job csvJob;

    @Value("${shop.csv-path}")
    private String shopCsvPath;

    @PostConstruct
    public void runJob() throws Exception {
        try (InputStream is = new ClassPathResource(shopCsvPath).getInputStream()) {
            String hash = calculateMD5Hash(is);

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("fileHash", hash)
                    .toJobParameters();

            jobLauncher.run(csvJob, jobParameters);
        } catch (JobInstanceAlreadyCompleteException e) {
            System.out.println("⚠️ 이미 처리된 파일입니다. Job 실행 생략");
        }
    }

    private String calculateMD5Hash(InputStream is) throws Exception {
        byte[] content = is.readAllBytes();
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(content);
        return Base64.getEncoder().encodeToString(hashBytes);
    }
}
