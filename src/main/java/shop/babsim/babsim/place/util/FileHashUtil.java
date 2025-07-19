package shop.babsim.babsim.place.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.springframework.util.DigestUtils;

public class FileHashUtil {
    public static String md5(File file) {
        try (InputStream is = new FileInputStream(file)) {
            return DigestUtils.md5DigestAsHex(is);
        } catch (Exception e) {
            throw new RuntimeException("파일 해시 계산 실패: " + file.getAbsolutePath(), e);
        }
    }
}
