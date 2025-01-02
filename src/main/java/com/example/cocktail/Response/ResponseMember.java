package com.example.cocktail.Response;

import com.example.cocktail.DTO.MemberDTO;
import com.example.cocktail.Model.Member;

public class ResponseMember {

    private String mesg;
    private MemberDTO member;
    private String token;
    private String role;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public MemberDTO getMember() {
        return member;
    }

    public void setMember(MemberDTO member) {
        this.member = member;
    }

    public String getMesg() {
        return mesg;
    }

    public void setMesg(String mesg) {
        this.mesg = mesg;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
