package de.ilyes.device.config.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class CorrelationIdRequestFilter implements Filter {
  public static final String CORRELATION_ID_MDC_KEY = "correlationId";
  public static final String CORRELATION_ID_HEADER_KEY = "X-Correlation-ID";

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    String correlationId = httpRequest.getHeader(CORRELATION_ID_HEADER_KEY);
    if (correlationId == null) {
      correlationId = UUID.randomUUID().toString();
    }
    MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {
    Filter.super.destroy();
    MDC.remove(CORRELATION_ID_MDC_KEY);
  }
}
