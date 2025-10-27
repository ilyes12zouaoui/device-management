package de.ilyes.device.mapper.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import de.ilyes.device.config.DateFormatPatternConstant;
import de.ilyes.device.config.validation.BetweenPresentAndPastMonths;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceCreationDto {

  @Schema(example = "Smartphone")
  @NotBlank(message = "Device name must not be blank")
  private String name;

  @Schema(example = "SmartphoneBrand")
  @NotBlank(message = "Device brand must not be blank")
  private String brand;

  @Schema(example = "AVAILABLE")
  @NotNull(message = "Device deviceState must not be null")
  private DeviceState deviceState;

  @Schema(example = "2025-10-26T18:06:06.752Z")
  @NotNull(message = "Device creationTime must not be null")
  @BetweenPresentAndPastMonths(
      numberOfMonths = 6,
      message = "Device creationDate must be between now and past 6 month.")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = DateFormatPatternConstant.ZONED_DATE_TIME_FORMAT)
  private ZonedDateTime creationTime;
}
