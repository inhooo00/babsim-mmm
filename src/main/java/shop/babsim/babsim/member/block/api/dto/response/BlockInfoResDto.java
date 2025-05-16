package shop.babsim.babsim.member.block.api.dto.response;

import lombok.Builder;
import shop.babsim.babsim.member.block.domain.Block;

@Builder
public record BlockInfoResDto(
        Long blockedId,
        String blockedNickname
) {
    public static BlockInfoResDto from(Block block) {
        return BlockInfoResDto.builder()
                .blockedId(block.getBlocked().getId())
                .blockedNickname(block.getBlocked().getNickname())
                .build();
    }
}
