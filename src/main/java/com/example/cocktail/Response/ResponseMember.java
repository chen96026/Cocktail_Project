package com.example.cocktail.Response;

import com.example.cocktail.DTO.MemberDTO;

/**
 * 註冊／登入／登出的回應內容
 */
public record ResponseMember(String mesg, MemberDTO member, String token, String role) {

    /**
     * @param mesg 回應訊息
     * @return 只帶訊息、不含會員資料與 token 的回應
     */
    public static ResponseMember message(String mesg) {
        return new ResponseMember(mesg, null, null, null);
    }
}
