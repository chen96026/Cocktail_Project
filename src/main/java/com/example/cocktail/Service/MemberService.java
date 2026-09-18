package com.example.cocktail.Service;

import com.example.cocktail.DTO.MemberDTO;

public interface MemberService {
    public MemberDTO registMember (String account, String password, String role);
    public MemberDTO loginMember (String account, String password);
    public boolean isAdmin(String token);
    public void checkEditPermission(String token, String accountOwner);
}
