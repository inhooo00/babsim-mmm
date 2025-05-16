package shop.babsim.babsim.member.block.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.babsim.babsim.member.block.api.dto.request.BlockUserReqDto;
import shop.babsim.babsim.member.block.domain.Block;
import shop.babsim.babsim.member.block.domain.repository.BlockRepository;
import shop.babsim.babsim.member.block.exception.AlreadyBlockedException;
import shop.babsim.babsim.member.block.exception.BlockNotFoundException;
import shop.babsim.babsim.member.block.exception.SelfBlockException;
import shop.babsim.babsim.member.domain.Member;
import shop.babsim.babsim.member.domain.repository.MemberRepository;
import shop.babsim.babsim.member.exception.MemberNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlockService {

    private final BlockRepository blockRepository;
    private final MemberRepository memberRepository;

    // 유저 차단
    @Transactional
    public void blockUser(String email, BlockUserReqDto blockUserReqDto) {
        Member blocker = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException("차단하는 유저를 찾을 수 없습니다."));

        Member blocked = memberRepository.findById(blockUserReqDto.blockedId())
                .orElseThrow(() -> new MemberNotFoundException("차단되는 유저를 찾을 수 없습니다."));

        if (blocker.getId().equals(blockUserReqDto.blockedId())) {
            throw new SelfBlockException();
        }

        if (blockRepository.existsByBlockerIdAndBlockedId(blocker.getId(), blockUserReqDto.blockedId())) {
            throw new AlreadyBlockedException();
        }

        Block block = Block.builder()
                .blocker(blocker)
                .blocked(blocked)
                .build();
        blockRepository.save(block);
    }

    // 유저 차단 해제
    @Transactional
    public void unblockUser(String email, BlockUserReqDto blockUserReqDto) {
        Member blocker = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        Block blockedUser = blockRepository.findAllByBlockerId(blocker.getId()).stream()
                .filter(block -> block.getBlocked().getId().equals(blockUserReqDto.blockedId()))
                .findFirst()
                .orElseThrow(BlockNotFoundException::new);

        blockRepository.delete(blockedUser);
    }

    // 특정 유저가 차단했는지 확인
    public boolean isUserBlocked(String email, Long blockedId) {
        Member blocker = memberRepository.findByEmail(email)
                .orElseThrow(MemberNotFoundException::new);

        return blockRepository.existsByBlockerIdAndBlockedId(blocker.getId(), blockedId);
    }
}
