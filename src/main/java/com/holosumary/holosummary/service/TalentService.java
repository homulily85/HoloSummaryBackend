package com.holosumary.holosummary.service;

import com.holosumary.holosummary.model.Talent;
import com.holosumary.holosummary.repository.TalentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TalentService {
    private final TalentRepository talentRepository;

    public List<Talent> getTalentList() {
        return talentRepository.findAll();
    }
}
