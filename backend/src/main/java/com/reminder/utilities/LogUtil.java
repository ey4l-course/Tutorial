package com.reminder.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.reminder.Users.model.RequestContextDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class LogUtil {
    private final ObjectMapper objectMapper;
    private final Logger logger;

    public LogUtil (ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
        this.logger = LoggerFactory.getLogger(LogUtil.class);
    }

    public String logRequest(RequestContextDTO contextDTO) {
        String uuid;
        uuid = "USER_ERROR".equals(contextDTO.getCategory()) || "EXCEPTION".equals(contextDTO.getCategory()) ? UUID.randomUUID().toString() : null;
        Map<String, Object> logPayload = new HashMap<>();
        if (uuid != null)
            logPayload.put("uuid", uuid);
        logPayload.put("category", contextDTO.getCategory());
        logPayload.put("method", contextDTO.getMethod());
        logPayload.put("uri", contextDTO.getEntryRoute());
        logPayload.put("ipAddress", contextDTO.getIp());
        logPayload.put("userAgent", contextDTO.getUserAgent());
        logPayload.put("userName", contextDTO.getUserName());
        logPayload.put("outcome", contextDTO.getOutcome());
        logPayload.put("statusCode", contextDTO.getStatusCode());
        logPayload.put("statusMessage", contextDTO.getStatusMessage());
        logPayload.put("requestTime", contextDTO.getStartProcess());
        logPayload.put("requestDuration", contextDTO.getEndProcess().toEpochMilli()-contextDTO.getStartProcess().toEpochMilli());
        logPayload.put("developerInformation", contextDTO.getDebug());
        logger.info("requestTrace: {}", toJson(logPayload));
        return uuid;
    }

    public void testLog (String msg){
        Logger logger = LoggerFactory.getLogger("[TEST]");
        logger.error(msg);
    }

    private String toJson(Map<String, Object> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            return "Error serializing request log: " + e.getMessage();
        }
    }
}
