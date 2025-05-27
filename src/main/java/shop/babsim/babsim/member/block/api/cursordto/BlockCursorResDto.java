package shop.babsim.babsim.member.block.api.cursordto;

import java.util.List;
import shop.babsim.babsim.member.block.api.dto.response.BlockInfoResDto;

public record BlockCursorResDto(
        List<BlockInfoResDto> data,
        Long nextCursor,
        boolean hasNext
) {
    public static BlockCursorResDto of(List<BlockInfoResDto> data, Long nextCursor) {
        return new BlockCursorResDto(data, nextCursor, nextCursor != null);
    }
}
