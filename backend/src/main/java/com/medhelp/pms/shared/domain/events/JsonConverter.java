package com.medhelp.pms.shared.domain.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JsonConverter {

    private final ObjectMapper objectMapper;

    public JsonConverter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Convert object to JSON Map (for JSONB storage)
     */
    public Map<String, Object> convertToJson(Object object) {
        if (object == null) {
            return new HashMap<>();
        }

        try {
            // Convert to JSON string first
            String jsonString = objectMapper.writeValueAsString(object);

            // Then convert to Map
            return objectMapper.readValue(jsonString, Map.class);
        } catch (JsonProcessingException e) {
            log.error("Error converting object to JSON: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to convert object to JSON", e);
        }
    }

    /**
     * Convert JSON Map back to object
     */
    public <T> T convertFromJson(Map<String, Object> json, Class<T> clazz) {
        if (json == null || json.isEmpty()) {
            return null;
        }

        try {
            return objectMapper.convertValue(json, clazz);
        } catch (IllegalArgumentException e) {
            log.error("Error converting JSON to object: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to convert JSON to object", e);
        }
    }

    /**
     * Convert object to JSON string (for logging/debugging)
     */
    public String toJsonString(Object object) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Error converting object to JSON string: {}", e.getMessage(), e);
            return object != null ? object.toString() : "null";
        }
    }
}