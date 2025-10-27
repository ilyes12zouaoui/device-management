package de.ilyes.device.mapper.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceUpdateDto {

  @Schema(example = "Smartphone")
  @NotBlank(message = "Device name must not be blank")
  private String name;

  @Schema(example = "SmartphoneBrand")
  @NotBlank(message = "Device brand must not be blank")
  private String brand;

  @Schema(example = "INACTIVE")
  @NotNull(message = "Device deviceState must not be null")
  private DeviceState deviceState;
}
