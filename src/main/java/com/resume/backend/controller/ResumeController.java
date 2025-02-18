package com.resume.backend.controller;

import java.io.IOException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.resume.backend.service.ResumeService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/resume")
public class ResumeController {
    
    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService){
        this.resumeService = resumeService;
    }

    @PostMapping("/generate")
    public ResponseEntity<Map<String,Object>> getResumeData(@ModelAttribute("prompt") ResumeRequest resumeRequest) throws IOException {
        if(resumeRequest.userDescription()==null || resumeRequest.userDescription().isEmpty()){
            System.out.println("USER DESCRIPTION CANNOT BE NULL OR EMPTY");
        }
        System.out.println(resumeRequest.userDescription());
        Map<String,Object> response = resumeService.generateResumeResponse(resumeRequest.userDescription());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}