package com.example.cocktail.Repository;
import com.example.cocktail.Model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository <Member,Integer>{
    public Member findByAccount(String account );
}
