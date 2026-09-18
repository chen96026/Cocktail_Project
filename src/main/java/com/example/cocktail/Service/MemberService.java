package com.example.cocktail.Service;

import com.example.cocktail.Bcrypt.BCrypt;
import com.example.cocktail.DTO.MemberDTO;
import com.example.cocktail.JwtUtil;
import com.example.cocktail.Model.Member;
import com.example.cocktail.Repository.MemberRepository;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    public MemberService(MemberRepository memberRepository, JwtUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * @param account  帳號
     * @param password 密碼（明碼，儲存前以 BCrypt 雜湊）
     * @param role     角色，未帶則預設 USER
     * @return 註冊成功的會員資料
     */
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

        return new MemberDTO(newMember.getAccount(), newMember.getRole());
    }

    /**
     * @param account  帳號
     * @param password 密碼（明碼）
     * @return 登入成功的會員資料
     */
    public MemberDTO loginMember(String account, String password) {
        Member member = memberRepository.findByAccount(account);
        if (member == null) {
            throw new RuntimeException("帳號不存在");
        }
        if (!BCrypt.checkpw(password, member.getPassword())) {
            throw new RuntimeException("密碼錯誤");
        }
        return new MemberDTO(member.getAccount(), member.getRole());
    }

    /**
     * @param token JWT
     * @return 是否為管理員
     */
    public boolean isAdmin(String token) {
        Claims claims = jwtUtil.validateToken(token);
        String role = claims.get("role", String.class);
        return "ADMIN".equals(role);
    }

    /**
     * 檢查編輯權限：ADMIN 直接通過，USER 僅能編輯自己的資料
     *
     * @param token        JWT
     * @param accountOwner 操作對象的擁有者帳號
     */
    public void checkEditPermission(String token, String accountOwner) {
        Claims claims = jwtUtil.validateToken(token);
        String role = claims.get("role", String.class);
        // 提取Token中的sub 字段，通常是用戶的帳號或 ID
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
