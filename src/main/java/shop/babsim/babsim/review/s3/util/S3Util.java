package shop.babsim.babsim.review.s3.util;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class S3Util {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    public String getFileUrl(String fileName) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, fileName);
    }

    public List<String> getFileUrl(List<String> fileNames) {
        if (fileNames == null) return List.of();

        return fileNames.stream()
                .map(this::getFileUrl)
                .toList();
    }
}