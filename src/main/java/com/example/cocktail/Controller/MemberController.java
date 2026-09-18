package com.example.cocktail.Controller;

import com.example.cocktail.DTO.MemberDTO;
import com.example.cocktail.JwtUtil;
import com.example.cocktail.Model.Member;
import com.example.cocktail.Response.ResponseMember;
import com.example.cocktail.Service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lastwine")
@Tag(name = "Member", description = "API")
public class MemberController {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    public MemberController(MemberService memberService, JwtUtil jwtUtil) {
        this.memberService = memberService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/regist")
    @Operation(summary = "會員註冊")
    public ResponseEntity<ResponseMember> registMember(@RequestBody Member member, HttpServletResponse response) {
        try {
            MemberDTO memberDTO = memberService.registMember(
                    member.getAccount(), member.getPassword(), member.getRole());
            // 生成Token 並放入Cookie
            String token = jwtUtil.generateToken(memberDTO.account(), memberDTO.role());
            addTokenToCookie(response, token);

            return ResponseEntity.ok(new ResponseMember("註冊成功", memberDTO, token, memberDTO.role()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(ResponseMember.message("註冊失敗: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    @Operation(summary = "會員登入")
    public ResponseEntity<ResponseMember> loginMember(@RequestBody Member member, HttpServletResponse response) {
        try {
            MemberDTO oldMember = memberService.loginMember(member.getAccount(), member.getPassword());
            // 生成Token 並放入Cookie
            String token = jwtUtil.generateToken(oldMember.account(), oldMember.role());
            addTokenToCookie(response, token);

            return ResponseEntity.ok(new ResponseMember("登入成功", oldMember, token, oldMember.role()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(ResponseMember.message("登入失敗: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "會員登出")
    public ResponseEntity<ResponseMember> logoutMember(HttpServletResponse response) {
        // 清除Cookie中的Token
        Cookie cookie = new Cookie("token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 過期時間設置為0
        response.addCookie(cookie);

        return ResponseEntity.ok(ResponseMember.message("登出成功"));
    }

    /**
     * @param response HTTP 回應
     * @param token    要寫入 Cookie 的 JWT
     */
    private void addTokenToCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        response.addCookie(cookie);
    }
}
