package com.resume.backend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class ResumeServiceImple implements ResumeService {

    private ChatClient chatClient;

    public ResumeServiceImple(ChatClient.Builder builder) {
        this.chatClient = builder.build();
        if (chatClient == null) {
            System.out.println("chatClient is not initialized");
        }
    }

    @Override
    public Map<String,Object> generateResumeResponse(String userResumeDescription) {

        String promptString = "";
        try {
            promptString = this.loadPromptFromFile("prompt.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (promptString.isEmpty()) {
            System.out.println("Prompt file is empty or not found.");
        } else {
            System.out.println(promptString);
        }
        String promptContent = this.putValuesToTemplate(promptString, Map.of("userDescription", userResumeDescription));
        Prompt prompt = new Prompt(promptContent);
        String response = chatClient.prompt(prompt).call().content();
        Map<String,Object> mapresponse = parseMultiJsonObject(response);
        System.out.println(mapresponse);
        return mapresponse;
    }

    String loadPromptFromFile(String filename) throws IOException {
        Path path = new ClassPathResource(filename).getFile().toPath();
        return Files.readString(path);
    }

    String putValuesToTemplate(String template, Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            template = template.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        return template;
    }

    public static Map<String, Object> parseMultiJsonObject(String response) {
        Map<String, Object> resultMap = new HashMap<>();

        int thinkStart = response.indexOf("<think>") + 7;
        int thinkEnd = response.indexOf("</think>");
        if (thinkStart != -1 && thinkEnd != -1) {
            String thinkContent = response.substring(thinkStart, thinkEnd).trim();
            resultMap.put("think", thinkContent);
        } else {
            resultMap.put("think", null);
        }

        int jsonStart = response.indexOf("```json") + 7;
        int jsonEnd = response.lastIndexOf("```");
        if (jsonStart != -1 && jsonEnd != -1 && jsonStart < jsonEnd) {
            String jsonContent = response.substring(jsonStart, jsonEnd).trim();
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                @SuppressWarnings("unchecked")
                Map<String, Object> dataContent = objectMapper.readValue(jsonContent, Map.class);
                resultMap.put("data", dataContent);
            } catch (Exception e) {
                resultMap.put("data", " ");
                System.err.println("Invalid JSON format in the response: " + e.getMessage());
            }
        } else {
            resultMap.put("data", " ");
        }

        return resultMap;
    }
}
