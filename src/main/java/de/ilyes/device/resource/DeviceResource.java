package de.ilyes.device.resource;

import de.ilyes.device.exception.controlleradvice.ErrorResponseDto;
import de.ilyes.device.mapper.dto.DeviceCreationDto;
import de.ilyes.device.mapper.dto.DeviceDto;
import de.ilyes.device.mapper.dto.DevicePatchDto;
import de.ilyes.device.mapper.dto.DeviceUpdateDto;
import de.ilyes.device.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/devices")
public class DeviceResource {

  private final DeviceService deviceService;

  public DeviceResource(DeviceService deviceService) {
    this.deviceService = deviceService;
  }

  @Operation(summary = "Create a new device")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Device created",
        content = @Content(schema = @Schema(implementation = DeviceDto.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Validation error",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
  })
  @PostMapping
  public ResponseEntity<DeviceDto> create(@Valid @RequestBody DeviceCreationDto dto) {
    DeviceDto created = deviceService.create(dto);
    return ResponseEntity.ok(created);
  }

  @Operation(summary = "Get a device by its ID")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Device found",
        content = @Content(schema = @Schema(implementation = DeviceDto.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Device not found",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
  })
  @GetMapping("/{id}")
  public ResponseEntity<DeviceDto> getById(@PathVariable Long id) {
    DeviceDto dto = deviceService.getById(id);
    return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
  }

  @Operation(summary = "Find devices by brand or name (paginated)")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Devices found",
        content = @Content(schema = @Schema(implementation = Page.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
  })
  @GetMapping
  public ResponseEntity<Page<DeviceDto>> findByBrandOrName(
      @RequestParam(required = false) String brand,
      @RequestParam(required = false) String name,
      Pageable pageable) {
    Page<DeviceDto> devices = deviceService.findByBrandOrName(brand, name, pageable);
    return ResponseEntity.ok(devices);
  }

  @Operation(summary = "Update a device by its ID")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Device updated",
        content = @Content(schema = @Schema(implementation = DeviceDto.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Validation error",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Device not found",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
  })
  @PutMapping("/{id}")
  public ResponseEntity<DeviceDto> update(
      @PathVariable Long id, @Valid @RequestBody DeviceUpdateDto dto) {
    DeviceDto updated = deviceService.update(id, dto);
    return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
  }

  @Operation(summary = "Delete a device by its ID")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Device deleted"),
    @ApiResponse(
        responseCode = "404",
        description = "Device not found",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    deviceService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Partially update a device by its ID")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Device patched",
        content = @Content(schema = @Schema(implementation = DeviceDto.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Validation error",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
    @ApiResponse(
        responseCode = "404",
        description = "Device not found",
        content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
  })
  @PatchMapping("/{id}")
  public ResponseEntity<DeviceDto> patch(
      @PathVariable Long id, @Valid @RequestBody DevicePatchDto dto) {
    DeviceDto patched = deviceService.patchDevice(id, dto);
    return patched != null ? ResponseEntity.ok(patched) : ResponseEntity.notFound().build();
  }
}
