package de.ilyes.device.exception.controlleradvice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponseDto {

  @Schema(example = "Name and brand cannot be updated while the deviceStatus is IN_USE.")
  private String errorMessage;

  @Schema(example = "IDU-001")
  private String errorCode;

  @Schema(example = "INVALID_DEVICE_UPDATE")
  private String errorType;

  @Schema(example = "d290f1ee-6c54-4b01-90e6-d701748f0851")
  private String correlationId;
}
