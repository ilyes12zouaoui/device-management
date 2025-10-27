package de.ilyes.device.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class DeviceCannotBeUpdatedWhileStatusInUseException extends DeviceTechnicalException {
  public static final String INVALID_DEVICE_UPDATE_ERROR_CODE = "IDU-001";
  protected static final String INVALID_DEVICE_UPDATE_ERROR_TYPE = "INVALID_DEVICE_UPDATE";
  protected static final String INVALID_DEVICE_UPDATE_ERROR_MESSAGE =
      "Name and brand cannot be updated while the deviceStatus is IN_USE.";

  public DeviceCannotBeUpdatedWhileStatusInUseException() {
    super(
        INVALID_DEVICE_UPDATE_ERROR_MESSAGE,
        INVALID_DEVICE_UPDATE_ERROR_CODE,
        INVALID_DEVICE_UPDATE_ERROR_TYPE,
        HttpStatus.BAD_REQUEST);
  }
}
