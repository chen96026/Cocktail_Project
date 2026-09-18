package com.example.cocktail.Repository;
import com.example.cocktail.Model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository <Member,Integer>{
    public Member findByAccount(String account );
}
