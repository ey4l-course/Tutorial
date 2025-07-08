package com.reminder.Budget.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

/*
Logging policy:
Unexpected exceptions are logged with full trace for debugging.
Exceptions that imply misuse or user errors are logged as info and may be integrated with monitoring system later.
 */

@Component
public class LogUtil {
    public static String error (Exception e){
        String uuid = UUID.randomUUID().toString();
        StackTraceElement origin = Arrays.stream(e.getStackTrace())
                .filter(frame -> frame.getClassName().startsWith("com.reminder"))
                .findFirst()
                .orElse(e.getStackTrace()[0]);
        String originStr = origin.getClassName() + ":" + origin.getMethodName() + "() at line: " + origin.getLineNumber();
        Logger logger = LoggerFactory.getLogger("[ERROR]");
        logger.error(String.format("%s,Unexpected error occurred at: %s; Cause: %s", uuid, originStr, e.getMessage()));
        return uuid;
    }

    public static String infoLog (String user, String msg){
        String uuid = UUID.randomUUID().toString();
        Logger logger = LoggerFactory.getLogger("[INFO]");
        logger.info(String.format("%s,User:%s,%s", uuid, user, msg));
        return uuid;
    }
}
