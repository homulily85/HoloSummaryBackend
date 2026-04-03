package com.holosumary.holosummary.repository;

import com.holosumary.holosummary.model.Talent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TalentRepository extends JpaRepository<Talent, String> {
    List<Talent> findAll();
}
