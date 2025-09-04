package com.reminder.exception;

import com.reminder.Users.model.RequestContextDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Component
public class ContextExceptionResolver implements HandlerExceptionResolver, Ordered {

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    @Override
    public ModelAndView resolveException(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) {

        RequestContextDTO contextDTO = (RequestContextDTO) request.getAttribute("context");
        if (contextDTO != null && contextDTO.getOutcome() == null) {
            int status = mapExceptionToStatus(ex);
            contextDTO.setOutcome("[REJECTED] status: " + status + ", " + ex.getMessage());
        }

        // Return null so that other resolvers (DefaultHandlerExceptionResolver, etc.)
        // can still apply their normal logic
        return null;
    }

    private int mapExceptionToStatus(Exception ex) {
        if (ex instanceof HttpMessageNotReadableException) {
            return HttpServletResponse.SC_BAD_REQUEST; // 400
        }
        if (ex instanceof MissingServletRequestParameterException ||
                ex instanceof MissingPathVariableException) {
            return HttpServletResponse.SC_BAD_REQUEST; // 400
        }
        if (ex instanceof HttpRequestMethodNotSupportedException) {
            return HttpServletResponse.SC_METHOD_NOT_ALLOWED; // 405
        }
        if (ex instanceof NoHandlerFoundException) {
            return HttpServletResponse.SC_NOT_FOUND; // 404
        }
        // fallback
        return HttpServletResponse.SC_INTERNAL_SERVER_ERROR; // 500
    }
}
