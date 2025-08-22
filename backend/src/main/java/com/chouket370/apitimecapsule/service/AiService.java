package com.chouket370.apitimecapsule.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import okhttp3.RequestBody;



import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    @Value("${groq.api.key}")
    private String API_KEY;

    private static final String SYSTEM_PROMPT = """
        You are an AI assistant for a Time Capsule Email Scheduler app.
        You ONLY help users write, improve, or personalize future email messages.
        You NEVER answer questions outside this domain.
        When a user asks to create, generate, write, or improve a message:
        - If they provide a topic, create a professional, clear, and friendly email.
        - If they provide an existing email, improve its tone, clarity, and grammar.
        Always keep the email format realistic and ready for scheduling.
    """;

    public String processEmailRequest(String userInput) throws IOException {
        OkHttpClient client = new OkHttpClient();
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> payload = Map.of(
                "model", "llama3-8b-8192",
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content", userInput)
                ),
                "temperature", 0.7
        );

        String json = mapper.writeValueAsString(payload);

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                json);

        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No error body";
                throw new IOException("Unexpected code: " + response.code() + " - " + errorBody);
            }
            String responseBody = response.body().string();

            return new org.json.JSONObject(responseBody)
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
        }
    }
}
