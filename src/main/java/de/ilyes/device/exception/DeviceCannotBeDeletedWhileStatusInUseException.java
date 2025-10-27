package de.ilyes.device.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
@Data
public class DeviceCannotBeDeletedWhileStatusInUseException extends DeviceTechnicalException {
  public static final String INVALID_DEVICE_DELETE_ERROR_CODE = "IDD-001";
  protected static final String INVALID_DEVICE_DELETE_ERROR_TYPE = "INVALID_DEVICE_DELETE";
  protected static final String INVALID_DEVICE_DELETE_ERROR_MESSAGE =
      "device cannot be deleted while deviceStatus is IN_USE.";

  public DeviceCannotBeDeletedWhileStatusInUseException() {
    super(
        INVALID_DEVICE_DELETE_ERROR_MESSAGE,
        INVALID_DEVICE_DELETE_ERROR_CODE,
        INVALID_DEVICE_DELETE_ERROR_TYPE,
        HttpStatus.BAD_REQUEST);
  }
}
