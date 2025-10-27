package de.ilyes.device.mapper.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DevicePatchDto {

  @Schema(example = "Smartphone")
  private String name;

  @Schema(example = "SmartphoneBrand")
  private String brand;

  @Schema(example = "INACTIVE")
  private DeviceState deviceState;
}
