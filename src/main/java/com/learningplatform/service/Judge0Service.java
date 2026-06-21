package com.learningplatform.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

@Service
public class Judge0Service {

    @Value("${judge0.api.url}")
    private String apiUrl;

    private final ObjectMapper mapper = new ObjectMapper();

    // ── Code Submit karo ──
    public String submitCode(String sourceCode,
                             int languageId,
                             String stdin) throws Exception 
    {

        // Base64 encode
        String encodedCode = Base64.getEncoder()
            .encodeToString(sourceCode.getBytes("UTF-8"));
        String encodedInput = Base64.getEncoder()
            .encodeToString(
                (stdin == null ? "" : stdin)
                .getBytes("UTF-8"));

        String body = "{"
            + "\"source_code\":\"" + encodedCode + "\","
            + "\"language_id\":" + languageId + ","
            + "\"stdin\":\"" + encodedInput + "\""
            + "}";

        // HTTP Connection — Java 8 compatible!
        URL url = new URL(
            apiUrl + "?base64_encoded=true&wait=false");
        HttpURLConnection conn = 
            (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty(
            "Content-Type", "application/json");
        conn.setDoOutput(true);

        // Body bhejo
        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes("UTF-8"));
        }

        // Response pado
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
            new InputStreamReader(
                conn.getInputStream(), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        System.out.println("Submit Response: " 
            + response.toString());

        JsonNode json = mapper.readTree(
            response.toString());
        return json.get("token").asText();
    }

    // ── Result Fetch karo ──
    public JsonNode getResult(String token) throws Exception {

        for (int i = 0; i < 15; i++) {
            Thread.sleep(1500);

            URL url = new URL(
                apiUrl + "/" + token + 
                "?base64_encoded=true");
            HttpURLConnection conn = 
                (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                    conn.getInputStream(), "UTF-8"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            }

            JsonNode result = mapper.readTree(
                response.toString());

            int statusId = result
                .get("status").get("id").asInt();

            System.out.println("Status: " + statusId 
                + " — " + result.get("status")
                .get("description").asText());

            // 3+ matlab done
            if (statusId > 2) return result;
        }

        throw new RuntimeException(
            "Code execution timeout!");
    }

    // ── Base64 Decode ──
    public String decodeOutput(JsonNode result,
                               String field) {
        try {
            JsonNode node = result.get(field);
            if (node == null || node.isNull()) return "";
            return new String(
                Base64.getDecoder()
                    .decode(node.asText()),
                "UTF-8").trim();
        } catch (Exception e) {
            return "";
        }
    }
}