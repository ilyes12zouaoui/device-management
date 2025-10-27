package de.ilyes.device.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceTechnicalException extends RuntimeException {
  private String errorMessage;
  private String errorCode;
  private String errorType;
  private HttpStatus httpStatus;
}
