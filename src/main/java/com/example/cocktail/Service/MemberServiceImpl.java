package com.example.cocktail.Service;

import com.example.cocktail.Bcrypt.BCrypt;
import com.example.cocktail.DTO.MemberDTO;
import com.example.cocktail.JwtUtil;
import com.example.cocktail.Model.Member;
import com.example.cocktail.Repository.MemberRepository;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public MemberDTO registMember(String account, String password, String role) {
        if (memberRepository.findByAccount(account) != null) {
            throw new RuntimeException("帳號已存在");
        }
        if (role == null || role.isEmpty()) {
            role = "USER";
        }
        if ("admin".equals(account)) {
            role = "ADMIN";
        }

        // 只在所有檢查完成後才保存到資料庫
        Member newMember = new Member();
        newMember.setAccount(account);
        newMember.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        newMember.setRole(role.toUpperCase());

        memberRepository.save(newMember);

        // 將Member實體轉換為 DTO
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setAccount(newMember.getAccount());
        memberDTO.setRole(newMember.getRole());

        return memberDTO;
    }

    @Override
    public MemberDTO loginMember(String account, String password) {
        Member member = memberRepository.findByAccount(account);
        if (member == null) {
            throw new RuntimeException("帳號不存在");
        }
        if (!BCrypt.checkpw(password, member.getPassword())) {
            throw new RuntimeException("密碼錯誤");
        }
        // 將實體轉換為DTO
        MemberDTO memberdto = new MemberDTO();
        memberdto.setAccount(member.getAccount());
        memberdto.setRole(member.getRole());
        return memberdto; // 確保返回用戶對象
    }

    @Override
    public Optional<Member> findMemberByAccount(String account) {
        return Optional.ofNullable(memberRepository.findByAccount(account));
    }

    @Override
    public boolean isAdmin(String token) {
        Claims claims = jwtUtil.validateToken(token);
        String role = claims.get("role", String.class);
        return "ADMIN".equals(role);
    }

    @Override
    public void checkEditPermission(String token, String accountOwner) {
        Claims claims = jwtUtil.validateToken(token);
        String role = claims.get("role", String.class);
        // 提取Token中的sub 字段，通常是用戶的帳號或 ID
        // 判斷當前操作的用戶是否為操作對象的擁有者
        String account = claims.getSubject();
        if ("ADMIN".equalsIgnoreCase(role)) {
            // 如果是ADMIN直接通過
            return;
        }
        // 如果是USER，檢查是否為擁有者
        if (!account.equals(accountOwner)) {
            throw new RuntimeException("無權限更新該酒譜");
        }
    }
}
