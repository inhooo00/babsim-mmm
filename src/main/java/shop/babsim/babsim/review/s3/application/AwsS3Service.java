package shop.babsim.babsim.review.s3.application;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AwsS3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    private final AmazonS3 amazonS3;

    public List<String> uploadFile(List<MultipartFile> multipartFiles){
        List<String> fullUrlList = new ArrayList<>();

        multipartFiles.forEach(file -> {
            String fileName = createFileName(file.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            try(InputStream inputStream = file.getInputStream()) {
                amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
            } catch (IOException e){
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
            }

            String fullUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, fileName);
            fullUrlList.add(fullUrl);
        });

        return fullUrlList;
    }

    public String createFileName(String fileName){
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName){
        try{
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일" + fileName + ") 입니다.");
        }
    }

    public void deleteFile(String fileName){
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
        System.out.println(bucket);
    }

    public String getFileUrls(String fileNames) {
        if (fileNames == null || fileNames.isEmpty()) {
            return null;
        }

        return Arrays.stream(fileNames.split(","))
                .map(fileName -> String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, fileName))
                .collect(Collectors.joining(","));
    }

    public List<String> uploadBase64Images(List<String> base64Images) {
        if (base64Images == null || base64Images.isEmpty()) return List.of();

        List<String> uploadedUrls = new ArrayList<>();

        for (String base64 : base64Images) {
            try {
                String contentType = "image/jpeg";
                String base64Data = base64;

                if (base64.contains(",")) {
                    String[] parts = base64.split(",", 2);
                    String meta = parts[0];
                    base64Data = parts[1];
                    contentType = meta.substring(meta.indexOf(":") + 1, meta.indexOf(";"));
                }

                byte[] imageBytes = Base64.getDecoder().decode(base64Data);
                String fileName = UUID.randomUUID() + getExtensionFromContentType(contentType);

                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(imageBytes.length);
                metadata.setContentType(contentType);

                try (InputStream inputStream = new ByteArrayInputStream(imageBytes)) {
                    amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
                }

                String fullUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, fileName);
                uploadedUrls.add(fullUrl);

            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Base64 업로드 실패: " + e.getMessage());
            }
        }

        return uploadedUrls;
    }

    private String getExtensionFromContentType(String contentType) {
        return switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            default -> "";
        };
    }
}