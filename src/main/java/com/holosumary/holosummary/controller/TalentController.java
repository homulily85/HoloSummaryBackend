package com.holosumary.holosummary.controller;

import com.holosumary.holosummary.model.Talent;
import com.holosumary.holosummary.service.TalentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class TalentController {
    private final TalentService talentService;

    @GetMapping("/talents")
    public ResponseEntity<List<Talent>> getTalentList() {
        return ResponseEntity.ok().body(talentService.getTalentList());
    }
}
