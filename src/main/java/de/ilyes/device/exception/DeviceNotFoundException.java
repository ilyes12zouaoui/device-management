package de.ilyes.device.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class DeviceNotFoundException extends DeviceTechnicalException {
  public static final String DEVICE_NOT_FOUND_ERROR_CODE = "DNF-001";
  protected static final String DEVICE_NOT_FOUND_ERROR_TYPE = "DEVICE_NOT_FOUND";
  protected static final String DEVICE_NOT_FOUND_ERROR_MESSAGE =
      "device with the given ID was not found.";

  public DeviceNotFoundException() {
    super(
        DEVICE_NOT_FOUND_ERROR_MESSAGE,
        DEVICE_NOT_FOUND_ERROR_CODE,
        DEVICE_NOT_FOUND_ERROR_TYPE,
        HttpStatus.NOT_FOUND);
  }
}
