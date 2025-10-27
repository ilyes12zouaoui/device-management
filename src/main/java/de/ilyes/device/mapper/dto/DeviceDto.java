package de.ilyes.device.mapper.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import de.ilyes.device.config.DateFormatPatternConstant;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceDto {

  @Schema(example = "1")
  private Long id;

  @Schema(example = "Smartphone")
  private String name;

  @Schema(example = "SmartphoneBrand")
  private String brand;

  @Schema(example = "AVAILABLE")
  private DeviceState deviceState;

  @Schema(example = "2025-10-26T18:06:06.752Z")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = DateFormatPatternConstant.ZONED_DATE_TIME_FORMAT)
  private ZonedDateTime creationTime;
}
