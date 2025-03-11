package com.resume.analyzer.Services;

import com.resume.analyzer.Model.Settings;
import lombok.Getter;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Service;

@Service
public class AIChatClient {


    @Getter
    private static ChatClient chatClient;
    private static AIChatClient instance;

    private AIChatClient() {
    }

    public static AIChatClient getInstance(Settings settings){
        if (instance==null){
            instance=new AIChatClient();
        }
        chatClient=instance.updateChatClient(settings);
        return instance;
    }

    public String test() {
        try {
            if (chatClient == null) {
                return "Error: ChatClient not configured. Please set API key and other settings.";
            }
            chatClient.prompt().user("Hi").call().chatResponse();
            return "Success";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private ChatClient updateChatClient(Settings settings) {
        try {
            OpenAiApi openAiApi = OpenAiApi.builder()
                    .baseUrl(settings.getBaseUrl())
                    .apiKey(settings.getApiKey())
                    .build();
            OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                    .model(settings.getModel())
                    .temperature(settings.getSamplingRate())
                    .build();
            OpenAiChatModel chatModel = OpenAiChatModel.builder()
                    .openAiApi(openAiApi)
                    .defaultOptions(chatOptions)
                    .build();
            return ChatClient.create(chatModel);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create ChatClient: " + e.getMessage());
        }
    }
}