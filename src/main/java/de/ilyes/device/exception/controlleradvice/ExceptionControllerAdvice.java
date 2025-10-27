package de.ilyes.device.exception.controlleradvice;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import de.ilyes.device.config.DateFormatPatternConstant;
import de.ilyes.device.config.filter.CorrelationIdRequestFilter;
import de.ilyes.device.exception.DeviceTechnicalException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionControllerAdvice {

  private static final Logger log = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

  public static final String INTERNAL_ERROR_CODE = "IED-001";
  public static final String VALIDATION_DEVICE_ERROR_CODE = "VED-001";
  public static final String INVALID_REQUEST_BODY_ERROR_CODE = "IRBED-001";

  protected static final String VALIDATION_ERROR_TYPE = "VALIDATION_ERROR";
  protected static final String INVALID_REQUEST_BODY_ERROR_TYPE = "INVALID_REQUEST_BODY_ERROR";
  protected static final String INTERNAL_ERROR_TYPE = "INTERNAL_ERROR";

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponseDto> handleValidationExceptions(
      MethodArgumentNotValidException ex) {

    final String[] errorMessage = {""};

    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            (error) -> {
              String fieldName = ((FieldError) error).getField();
              String msg = error.getDefaultMessage();
              errorMessage[0] = errorMessage[0] + fieldName + " : " + msg;
            });

    log.error(errorMessage[0]);
    ErrorResponseDto errorResponseDto =
        ErrorResponseDto.builder()
            .errorType(VALIDATION_ERROR_TYPE)
            .errorCode(VALIDATION_DEVICE_ERROR_CODE)
            .errorMessage(errorMessage[0])
            .correlationId(MDC.get(CorrelationIdRequestFilter.CORRELATION_ID_MDC_KEY))
            .build();

    return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({HttpMessageNotReadableException.class, MismatchedInputException.class})
  public ResponseEntity<ErrorResponseDto>
      handleMismatchedInputExceptionAndHttpMessageNotReadableException(RuntimeException ex) {
    String errorMessage = formatErrorMessageForMismatchedInputException(ex);
    log.error(ex.getMessage());
    ErrorResponseDto errorResponseDto =
        ErrorResponseDto.builder()
            .errorType(INVALID_REQUEST_BODY_ERROR_TYPE)
            .errorCode(INVALID_REQUEST_BODY_ERROR_CODE)
            .errorMessage(errorMessage)
            .correlationId(MDC.get(CorrelationIdRequestFilter.CORRELATION_ID_MDC_KEY))
            .build();

    return new ResponseEntity<>(errorResponseDto, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorResponseDto> handleRuntimeException(RuntimeException ex) {
    log.error(ex.getMessage());
    ErrorResponseDto errorResponseDto =
        ErrorResponseDto.builder()
            .errorType(INTERNAL_ERROR_TYPE)
            .errorCode(INTERNAL_ERROR_CODE)
            .errorMessage(ex.getMessage())
            .correlationId(MDC.get(CorrelationIdRequestFilter.CORRELATION_ID_MDC_KEY))
            .build();

    return new ResponseEntity<>(errorResponseDto, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(DeviceTechnicalException.class)
  public ResponseEntity<ErrorResponseDto> handleRuntimeException(DeviceTechnicalException ex) {
    log.error(ex.getErrorMessage());
    ErrorResponseDto errorResponseDto =
        ErrorResponseDto.builder()
            .errorType(ex.getErrorType())
            .errorCode(ex.getErrorCode())
            .errorMessage(ex.getErrorMessage())
            .correlationId(MDC.get(CorrelationIdRequestFilter.CORRELATION_ID_MDC_KEY))
            .build();

    return new ResponseEntity<>(
        errorResponseDto,
        ex.getHttpStatus() != null ? ex.getHttpStatus() : HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private String formatErrorMessageForMismatchedInputException(RuntimeException ex) {
    String errorMessage;
    if (ex.getMessage().contains("java.time.ZonedDateTime")) {
      errorMessage =
          "Date time input format should be "
              + DateFormatPatternConstant.ZONED_DATE_TIME_FORMAT
              + ". Example "
              + ZonedDateTime.now()
                  .minusDays(2)
                  .format(
                      DateTimeFormatter.ofPattern(
                          DateFormatPatternConstant.ZONED_DATE_TIME_FORMAT));
    } else if (ex.getMessage().contains(".DeviceState")) {
      errorMessage =
          "Wrong value for deviceState field. values accepted are: [AVAILABLE, INACTIVE, IN_USE]";
    } else {
      errorMessage = ex.getMessage();
    }
    return errorMessage;
  }
}
