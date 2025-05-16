package shop.babsim.babsim.member.block.api.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record BlockListResDto(
        List<BlockInfoResDto> blockedUsers
) {
    public static BlockListResDto from(List<BlockInfoResDto> blockedUsers) {
        return BlockListResDto.builder()
                .blockedUsers(blockedUsers)
                .build();
    }
}
