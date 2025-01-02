package com.example.cocktail.Controller;

import com.example.cocktail.DTO.MemberDTO;
import com.example.cocktail.JwtUtil;
import com.example.cocktail.Response.ResponseMember;
import com.example.cocktail.Model.Member;
import com.example.cocktail.Service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/lastwine")
@Tag(name = "Member", description = "API")
public class MemberController {

    @Autowired
    private MemberService memberService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/regist")
    @Operation(summary = "會員註冊")
    public ResponseEntity<ResponseMember> registMember(@RequestBody Member member, HttpServletResponse response) {
        try {
            MemberDTO memberDTO = memberService.registMember(member.getAccount(), member.getPassword(), member.getRole());
            // 生成Token
            String token = jwtUtil.generateToken(member.getAccount(), member.getRole());
            // 將Token放入Cookie
            addTokenToCookie(response, token);

            ResponseMember responseMember = new ResponseMember();
            responseMember.setMesg("註冊成功");
            responseMember.setToken(token);
            responseMember.setMember(memberDTO);
            responseMember.setRole(member.getRole());

            return ResponseEntity.ok(responseMember);
        } catch (RuntimeException e) {
            ResponseMember responseMember = new ResponseMember();
            responseMember.setMesg("註冊失敗: " + e.getMessage());
            return ResponseEntity.status(400).body(responseMember);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "會員登入")
    public ResponseEntity<ResponseMember> loginMember(@RequestBody Member member, HttpServletResponse response) {
        try {
            MemberDTO oldMember = memberService.loginMember(member.getAccount(), member.getPassword());
            //生成Token
            String token = jwtUtil.generateToken(oldMember.getAccount(), oldMember.getRole());
            // 將Token 放入 Cookie
            addTokenToCookie(response, token);

            ResponseMember responseMember = new ResponseMember();
            responseMember.setMesg("登入成功");
            responseMember.setToken(token);
            responseMember.setMember(oldMember);
            responseMember.setRole(oldMember.getRole());

            return ResponseEntity.ok(responseMember);
        } catch (RuntimeException e) {
            ResponseMember responseMember = new ResponseMember();
            responseMember.setMesg("登入失敗: " + e.getMessage());
            return ResponseEntity.status(401).body(responseMember);
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "會員登出")
    public ResponseEntity<ResponseMember> logoutMember(HttpServletResponse response) {
        try {
            // 清除Cookie中的Token
            Cookie cookie = new Cookie("token", null);
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge(0); // 過期時間設置為0
            response.addCookie(cookie);

            ResponseMember responseMember = new ResponseMember();
            responseMember.setMesg("登出成功");
            return ResponseEntity.ok(responseMember);
        } catch (RuntimeException e) {
            ResponseMember responseMember = new ResponseMember();
            responseMember.setMesg("登出失敗");
            return ResponseEntity.status(400).body(responseMember);
        }
    }

    // 將Token添加到Cookie的方法
    private void addTokenToCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        response.addCookie(cookie);
    }
}
