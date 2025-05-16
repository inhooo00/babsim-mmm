package shop.babsim.babsim.member.block.api.dto.response;

import java.util.List;
import lombok.Builder;
import shop.babsim.babsim.global.dto.PageInfoResDto;

@Builder
public record BlockListResDto(
        List<BlockInfoResDto> blockedUsers,
        PageInfoResDto pageInfo
) {
    public static BlockListResDto of(List<BlockInfoResDto> blockedUsers, PageInfoResDto pageInfo) {
        return BlockListResDto.builder()
                .blockedUsers(blockedUsers)
                .pageInfo(pageInfo)
                .build();
    }

}
