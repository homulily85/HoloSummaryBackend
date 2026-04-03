package com.holosumary.holosummary.service;

import com.holosumary.holosummary.model.Talent;
import com.holosumary.holosummary.repository.TalentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TalentService {
    private final TalentRepository talentRepository;

    public TalentService(TalentRepository talentRepository) {
        this.talentRepository = talentRepository;
    }

    public List<Talent> getTalentList() {
        return talentRepository.findAll();
    }
}
