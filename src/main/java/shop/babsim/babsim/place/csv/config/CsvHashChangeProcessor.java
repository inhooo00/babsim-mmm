package shop.babsim.babsim.place.csv.config;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import shop.babsim.babsim.place.csv.dto.PlaceCsvData;
import shop.babsim.babsim.place.domain.Place;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import shop.babsim.babsim.place.domain.PlaceCsvHash;
import shop.babsim.babsim.place.domain.repository.PlaceCsvHashRepository;

@Component
@RequiredArgsConstructor
public class CsvHashChangeProcessor implements ItemProcessor<PlaceCsvData, Place> {

    private final PlaceCsvHashRepository hashRepository;

    @Override
    public Place process(PlaceCsvData item) throws Exception {
        String newHash = hashRow(item);
        String placeId = item.getPlaceId();

        Optional<PlaceCsvHash> existingHash = hashRepository.findById(placeId);

        if (existingHash.isEmpty()) {
            // ✅ 신규 → Place + 해시 모두 저장
            hashRepository.save(new PlaceCsvHash(placeId, newHash));
            return Place.from(item);
        }

        if (existingHash.get().getRowHash().equals(newHash)) {
            // ✅ 변경 없음
            return null;
        }

        // ✅ 변경된 행 → 해시 갱신 + Place 갱신
        PlaceCsvHash updated = existingHash.get();
        updated.setRowHash(newHash);
        hashRepository.save(updated);

        return Place.from(item);
    }


    private String hashRow(PlaceCsvData item) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");

        String rowData = String.join("|",
                nullSafe(item.getProvince()),
                nullSafe(item.getCity()),
                nullSafe(item.getCategory()),
                nullSafe(item.getBusinessName()),
                nullSafe(item.getContactNumber()),
                nullSafe(item.getAddress()),
                nullSafe(item.getMenu1()),
                nullSafe(item.getPrice1()),
                nullSafe(item.getMenu2()),
                nullSafe(item.getPrice2()),
                nullSafe(item.getPlaceId()),
                nullSafe(item.getPeriods()),
                nullSafe(item.getWeekdayDescriptions()),
                nullSafe(item.getPhotoUrls()),
                String.valueOf(item.getLatitude()),
                String.valueOf(item.getLongitude())
        );

        byte[] digest = md.digest(rowData.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(digest);
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }
}
