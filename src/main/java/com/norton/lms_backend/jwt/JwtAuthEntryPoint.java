package com.norton.lms_backend.jwt;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
      throws IOException, ServletException {
    ObjectMapper objectMapper = new ObjectMapper();
    response.setContentType("application/json");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    String msg = (String) request.getAttribute("exception");

    if (msg == null) {
      msg = authException.getMessage();
    }

    Map<String, Object> body = new LinkedHashMap<>();
    body.put("timestamp", Instant.now().toString());
    body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
    body.put("error", "Unauthorized");
    body.put("message", msg);
    body.put("path", request.getRequestURI());

    objectMapper.writeValue(response.getOutputStream(), body);
  }
}