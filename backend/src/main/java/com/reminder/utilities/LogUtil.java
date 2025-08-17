package com.reminder.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.reminder.Users.model.RequestContextDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/*
Logging policy:
Unexpected exceptions are logged with full trace for debugging.
Exceptions that imply misuse or user errors are logged as info and may be integrated with monitoring system later.
 */

@Component
public class LogUtil {
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(LogUtil.class);

    public LogUtil (ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }


    public String error (Exception e){
        String uuid = UUID.randomUUID().toString();
        StackTraceElement origin = Arrays.stream(e.getStackTrace())
                .filter(frame -> frame.getClassName().startsWith("com.reminder"))
                .findFirst()
                .orElse(e.getStackTrace()[0]);
        String originStr = origin.getClassName() + ":" + origin.getMethodName() + "() at line: " + origin.getLineNumber();
        Logger logger = LoggerFactory.getLogger("[ERROR]");
        logger.error(String.format("%s,Unexpected error occurred at: %s; Cause: %s\n%s", uuid, originStr, e.getMessage(), e));
        return uuid;
    }

    public String infoLog (String user, String msg){
        String uuid = UUID.randomUUID().toString();
        Logger logger = LoggerFactory.getLogger("[INFO]");
        logger.info(String.format("%s,User:%s,%s", uuid, user, msg));
        return uuid;
    }

    public void criticalLog (String msg){
        Logger logger = LoggerFactory.getLogger("[CRITICAL]");
        logger.error(""); //TODO: complete method - find out how to log fatal
    }

    public String securityLog (String msg){
        String uuid = UUID.randomUUID().toString();
        Logger logger = LoggerFactory.getLogger("[SECURITY]");
        logger.info(String.format("%s,User:%s", uuid, msg));
        return uuid;
    }

    public String logRequest(RequestContextDTO contextDTO) {
        String uuid = UUID.randomUUID().toString();
        Map<String, Object> logPayload = new HashMap<>();
        logPayload.put("method", contextDTO.getMethod());
        logPayload.put("uri", contextDTO.getEntryRoute());
        logPayload.put("IpAddress", contextDTO.getIp());
        logPayload.put("userAgent", contextDTO.getUserAgent());
        logPayload.put("Outcome", contextDTO.getOutcome());
        logPayload.put("requestTime", contextDTO.getStartProcess());
        logPayload.put("requestDuration", contextDTO.getEndProcess().toEpochMilli()-contextDTO.getStartProcess().toEpochMilli());
        Logger logger = LoggerFactory.getLogger("[SECURITY]");
        logger.info("RequestTrace: {}", toJson(logPayload));
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
