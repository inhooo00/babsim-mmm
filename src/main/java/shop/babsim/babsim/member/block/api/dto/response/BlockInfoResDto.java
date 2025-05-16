package shop.babsim.babsim.member.block.api.dto.response;

import lombok.Builder;
import shop.babsim.babsim.member.block.domain.Block;

@Builder
public record BlockInfoResDto(
        Long blockerId,
        String blockerName,
        Long blockedId,
        String blockedName
) {
    public static BlockInfoResDto from(Block block) {
        return BlockInfoResDto.builder()
                .blockerId(block.getBlocker().getId())
                .blockerName(block.getBlocker().getName())
                .blockedId(block.getBlocked().getId())
                .blockedName(block.getBlocked().getName())
                .build();
    }
}
