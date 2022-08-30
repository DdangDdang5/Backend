package com.sparta.ddang.domain.member.service;

import com.sparta.ddang.domain.member.entity.Member;
import com.sparta.ddang.domain.member.entity.MemberDetails;
import com.sparta.ddang.domain.member.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberDetailsServcie implements UserDetailsService {
    private final MemberRepository memberRepository;

    public MemberDetailsServcie(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> member = memberRepository.findByEmail(username);
        return member
                .map(MemberDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("nickname not found"));
    }
}
