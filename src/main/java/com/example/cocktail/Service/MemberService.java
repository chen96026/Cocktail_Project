package com.example.cocktail.Service;

import com.example.cocktail.DTO.MemberDTO;
import com.example.cocktail.Model.Member;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface MemberService {
    public MemberDTO registMember (String account, String password, String role);
    public MemberDTO loginMember (String account, String password);
    public Optional<Member> findMemberByAccount(String account);
    public boolean isAdmin(String token);
    public void checkEditPermission(String token, String accountOwner);
}
