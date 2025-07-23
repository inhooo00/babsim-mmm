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

        if (existingHash.isPresent() && existingHash.get().getRowHash().equals(newHash)) {
            return null;
        }

        hashRepository.save(new PlaceCsvHash(placeId, newHash));

        return Place.from(item);
    }


    private String hashRow(PlaceCsvData item) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(item.toString().getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(digest);
    }
}
