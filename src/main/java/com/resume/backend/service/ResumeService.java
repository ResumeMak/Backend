package com.resume.backend.service;

import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public interface ResumeService {

    Map<String,Object> generateResumeResponse(String userResumeDescription);

}
