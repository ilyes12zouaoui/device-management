package de.ilyes.device.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ilyes.device.exception.DeviceCannotBeDeletedWhileStatusInUseException;
import de.ilyes.device.exception.DeviceCannotBeUpdatedWhileStatusInUseException;
import de.ilyes.device.exception.DeviceNotFoundException;
import de.ilyes.device.exception.controlleradvice.ExceptionControllerAdvice;
import de.ilyes.device.mapper.dto.*;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class DeviceResourceTest {

  @Container @ServiceConnection
  static PostgreSQLContainer postgreSQLContainer =
      new PostgreSQLContainer(DockerImageName.parse("postgres:18.0-alpine3.22"));

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @Test
  void createDevice_WithValidInput_ShouldReturnCreatedDevice() throws Exception {

    DeviceCreationDto dto = new DeviceCreationDto();
    dto.setName("Test Device");
    dto.setBrand("Test Brand");
    dto.setDeviceState(DeviceState.AVAILABLE);
    dto.setCreationTime(ZonedDateTime.now());

    String requestBody = objectMapper.writeValueAsString(dto);

    MvcResult result =
        mockMvc
            .perform(
                post("/api/v1/devices")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
            .andExpect(status().isOk())
            .andReturn();

    DeviceDto responseBody =
        objectMapper.readValue(result.getResponse().getContentAsString(), DeviceDto.class);

    assertThat(responseBody.getName()).isEqualTo(dto.getName());
    assertThat(responseBody.getBrand()).isEqualTo(dto.getBrand());
    assertThat(responseBody.getDeviceState()).isEqualTo(dto.getDeviceState());
    assertThat(responseBody.getCreationTime().withNano(0))
        .isEqualTo(dto.getCreationTime().withNano(0));
  }

  @Test
  void createDevice_WithInvalidInput_ShouldThrowValidationException() throws Exception {

    DeviceCreationDto dto = new DeviceCreationDto();
    dto.setName("");
    dto.setBrand("Test Brand");
    dto.setDeviceState(DeviceState.AVAILABLE);
    dto.setCreationTime(ZonedDateTime.now());

    String requestBody = objectMapper.writeValueAsString(dto);

    mockMvc
        .perform(
            post("/api/v1/devices").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.errorCode").value(ExceptionControllerAdvice.VALIDATION_DEVICE_ERROR_CODE));
  }

  @Test
  void createDevice_WithBusinessRuleViolation_ShouldThrowException() throws Exception {

    DeviceCreationDto dto = new DeviceCreationDto();
    dto.setName("Test Device");
    dto.setBrand("Test Brand");
    dto.setDeviceState(DeviceState.IN_USE);
    dto.setCreationTime(ZonedDateTime.now().minusMonths(7));

    String requestBody = objectMapper.writeValueAsString(dto);

    mockMvc
        .perform(
            post("/api/v1/devices").contentType(MediaType.APPLICATION_JSON).content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.errorCode").value(ExceptionControllerAdvice.VALIDATION_DEVICE_ERROR_CODE));
  }

  @Test
  void getDeviceById_WhenDeviceExists_ShouldReturnDevice() throws Exception {

    DeviceCreationDto dto = new DeviceCreationDto();
    dto.setName("Test Device");
    dto.setBrand("Test Brand");
    dto.setDeviceState(DeviceState.AVAILABLE);
    dto.setCreationTime(ZonedDateTime.now().withNano(0));

    String requestBody = objectMapper.writeValueAsString(dto);

    MvcResult createResult =
        mockMvc
            .perform(
                post("/api/v1/devices")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
            .andExpect(status().isOk())
            .andReturn();

    DeviceDto createdDevice =
        objectMapper.readValue(createResult.getResponse().getContentAsString(), DeviceDto.class);
    Long deviceId = createdDevice.getId();

    MvcResult getResult =
        mockMvc
            .perform(get("/api/v1/devices/{id}", deviceId))
            .andExpect(status().isOk())
            .andReturn();

    DeviceDto responseBody =
        objectMapper.readValue(getResult.getResponse().getContentAsString(), DeviceDto.class);
    assertThat(responseBody.getId()).isEqualTo(deviceId);
    assertThat(responseBody.getName()).isEqualTo(dto.getName());
    assertThat(responseBody.getBrand()).isEqualTo(dto.getBrand());
    assertThat(responseBody.getDeviceState()).isEqualTo(dto.getDeviceState());
    assertThat(responseBody.getCreationTime().withNano(0)).isEqualTo(dto.getCreationTime());
  }

  @Test
  void getDeviceById_WhenDeviceDoesNotExist_ShouldThrowNotFoundException() throws Exception {

    mockMvc
        .perform(get("/api/v1/devices/{id}", 123))
        .andExpect(status().isNotFound())
        .andExpect(
            jsonPath("$.errorCode").value(DeviceNotFoundException.DEVICE_NOT_FOUND_ERROR_CODE));
  }

  @Test
  void findDevicesByBrandOrName_WithValidParameters_ShouldReturnMatchingDevices() throws Exception {

    DeviceCreationDto device1 = new DeviceCreationDto();
    device1.setName("Device One");
    device1.setBrand("Brand A");
    device1.setDeviceState(DeviceState.AVAILABLE);
    device1.setCreationTime(ZonedDateTime.now());

    DeviceCreationDto device2 = new DeviceCreationDto();
    device2.setName("Device Two");
    device2.setBrand("Brand B");
    device2.setDeviceState(DeviceState.AVAILABLE);
    device2.setCreationTime(ZonedDateTime.now());

    mockMvc
        .perform(
            post("/api/v1/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(device1)))
        .andExpect(status().isOk());

    mockMvc
        .perform(
            post("/api/v1/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(device2)))
        .andExpect(status().isOk());

    mockMvc
        .perform(get("/api/v1/devices").queryParam("name", "Device One"))
        .andExpect(status().isOk())
        .andReturn();

    mockMvc
        .perform(get("/api/v1/devices").queryParam("name", "Device One"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].name").value("Device One"))
        .andExpect(jsonPath("$.content[0].brand").value("Brand A"))
        .andExpect(jsonPath("$.totalElements").value(1));
  }

  @Test
  void updateDevice_WithValidInput_ShouldUpdateDevice() throws Exception {

    DeviceCreationDto creationDto = new DeviceCreationDto();
    creationDto.setName("Initial Device");
    creationDto.setBrand("Initial Brand");
    creationDto.setDeviceState(DeviceState.AVAILABLE);
    creationDto.setCreationTime(ZonedDateTime.now().withNano(0));

    String creationRequest = objectMapper.writeValueAsString(creationDto);

    MvcResult createResult =
        mockMvc
            .perform(
                post("/api/v1/devices")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(creationRequest))
            .andExpect(status().isOk())
            .andReturn();

    DeviceDto createdDevice =
        objectMapper.readValue(createResult.getResponse().getContentAsString(), DeviceDto.class);
    Long deviceId = createdDevice.getId();

    DeviceUpdateDto updateDto = new DeviceUpdateDto();
    updateDto.setName("Updated Device");
    updateDto.setBrand("Updated Brand");
    updateDto.setDeviceState(DeviceState.AVAILABLE);

    String updateRequest = objectMapper.writeValueAsString(updateDto);

    MvcResult updateResult =
        mockMvc
            .perform(
                put("/api/v1/devices/{id}", deviceId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updateRequest))
            .andExpect(status().isOk())
            .andReturn();

    DeviceDto updatedDevice =
        objectMapper.readValue(updateResult.getResponse().getContentAsString(), DeviceDto.class);
    assertThat(updatedDevice.getId()).isEqualTo(deviceId);
    assertThat(updatedDevice.getName()).isEqualTo(updateDto.getName());
    assertThat(updatedDevice.getBrand()).isEqualTo(updateDto.getBrand());
  }

  @Test
  void updateDevice_WithInvalidInput_ShouldThrowValidationException() throws Exception {

    DeviceCreationDto creationDto = new DeviceCreationDto();
    creationDto.setName("Initial Device");
    creationDto.setBrand("Initial Brand");
    creationDto.setDeviceState(DeviceState.AVAILABLE);
    creationDto.setCreationTime(ZonedDateTime.now().withNano(0));

    String creationRequest = objectMapper.writeValueAsString(creationDto);

    MvcResult createResult =
        mockMvc
            .perform(
                post("/api/v1/devices")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(creationRequest))
            .andExpect(status().isOk())
            .andReturn();

    DeviceDto createdDevice =
        objectMapper.readValue(createResult.getResponse().getContentAsString(), DeviceDto.class);
    Long deviceId = createdDevice.getId();

    DeviceUpdateDto dto = new DeviceUpdateDto();
    dto.setName("");
    dto.setBrand("Updated Brand");
    dto.setDeviceState(DeviceState.AVAILABLE);

    String requestBody = objectMapper.writeValueAsString(dto);

    mockMvc
        .perform(
            put("/api/v1/devices/{id}", deviceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.errorCode").value(ExceptionControllerAdvice.VALIDATION_DEVICE_ERROR_CODE));
    ;
  }

  @Test
  void updateDevice_WithBusinessRuleViolation_ShouldThrowException() throws Exception {

    DeviceCreationDto creationDto = new DeviceCreationDto();
    creationDto.setName("Initial Device");
    creationDto.setBrand("Initial Brand");
    creationDto.setDeviceState(DeviceState.IN_USE);
    creationDto.setCreationTime(ZonedDateTime.now().withNano(0));

    String creationRequest = objectMapper.writeValueAsString(creationDto);

    MvcResult createResult =
        mockMvc
            .perform(
                post("/api/v1/devices")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(creationRequest))
            .andExpect(status().isOk())
            .andReturn();

    DeviceDto createdDevice =
        objectMapper.readValue(createResult.getResponse().getContentAsString(), DeviceDto.class);
    Long deviceId = createdDevice.getId();

    DeviceUpdateDto dto = new DeviceUpdateDto();
    dto.setName("Updated Device");
    dto.setBrand("Updated Brand");
    dto.setDeviceState(DeviceState.IN_USE);

    String requestBody = objectMapper.writeValueAsString(dto);

    mockMvc
        .perform(
            put("/api/v1/devices/{id}", deviceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.errorCode")
                .value(
                    DeviceCannotBeUpdatedWhileStatusInUseException
                        .INVALID_DEVICE_UPDATE_ERROR_CODE));
  }

  @Test
  void deleteDevice_WhenDeviceInUse_ShouldThrowBadRequestException() throws Exception {

    DeviceCreationDto creationDto = new DeviceCreationDto();
    creationDto.setName("Device to Delete");
    creationDto.setBrand("Delete Brand");
    creationDto.setDeviceState(DeviceState.AVAILABLE);
    creationDto.setCreationTime(ZonedDateTime.now().withNano(0));

    String creationRequest = objectMapper.writeValueAsString(creationDto);

    MvcResult createResult =
        mockMvc
            .perform(
                post("/api/v1/devices")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(creationRequest))
            .andExpect(status().isOk())
            .andReturn();

    DeviceDto createdDevice =
        objectMapper.readValue(createResult.getResponse().getContentAsString(), DeviceDto.class);
    Long deviceId = createdDevice.getId();

    mockMvc.perform(delete("/api/v1/devices/{id}", deviceId)).andExpect(status().isNoContent());
  }

  @Test
  void deleteDevice_WhenDeviceNotInUse_ShouldDeleteSuccessfully() throws Exception {

    DeviceCreationDto dto = new DeviceCreationDto();
    dto.setName("Test Device");
    dto.setBrand("Test Brand");
    dto.setDeviceState(DeviceState.IN_USE);
    dto.setCreationTime(ZonedDateTime.now());

    String requestBody = objectMapper.writeValueAsString(dto);

    MvcResult createResult =
        mockMvc
            .perform(
                post("/api/v1/devices")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
            .andExpect(status().isOk())
            .andReturn();

    DeviceDto createdDevice =
        objectMapper.readValue(createResult.getResponse().getContentAsString(), DeviceDto.class);
    Long deviceId = createdDevice.getId();

    mockMvc
        .perform(delete("/api/v1/devices/{id}", deviceId))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.errorCode")
                .value(
                    DeviceCannotBeDeletedWhileStatusInUseException
                        .INVALID_DEVICE_DELETE_ERROR_CODE));
  }

  @Test
  void patchDevice_WithValidInput_ShouldPatchDeviceSuccessfully() throws Exception {

    DeviceCreationDto creationDto = new DeviceCreationDto();
    creationDto.setName("Device to Patch");
    creationDto.setBrand("Patch Brand");
    creationDto.setDeviceState(DeviceState.AVAILABLE);
    creationDto.setCreationTime(ZonedDateTime.now().withNano(0));

    String creationRequest = objectMapper.writeValueAsString(creationDto);

    MvcResult createResult =
        mockMvc
            .perform(
                post("/api/v1/devices")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(creationRequest))
            .andExpect(status().isOk())
            .andReturn();

    DeviceDto createdDevice =
        objectMapper.readValue(createResult.getResponse().getContentAsString(), DeviceDto.class);
    Long deviceId = createdDevice.getId();
    DevicePatchDto patchDto = new DevicePatchDto();
    patchDto.setDeviceState(DeviceState.INACTIVE);

    String patchRequest = objectMapper.writeValueAsString(patchDto);

    MvcResult patchResult =
        mockMvc
            .perform(
                patch("/api/v1/devices/{id}", deviceId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(patchRequest))
            .andExpect(status().isOk())
            .andReturn();

    DeviceDto patchedDevice =
        objectMapper.readValue(patchResult.getResponse().getContentAsString(), DeviceDto.class);
    assertThat(patchedDevice.getDeviceState()).isEqualTo(patchDto.getDeviceState());
  }

  @Test
  void patchDevice_WithInvalidInput_ShouldThrowValidationException() throws Exception {

    DeviceCreationDto creationDto = new DeviceCreationDto();
    creationDto.setName("Test Device");
    creationDto.setBrand("Test Brand");
    creationDto.setDeviceState(DeviceState.IN_USE);
    creationDto.setCreationTime(ZonedDateTime.now());

    String creationRequest = objectMapper.writeValueAsString(creationDto);

    MvcResult createResult =
        mockMvc
            .perform(
                post("/api/v1/devices")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(creationRequest))
            .andExpect(status().isOk())
            .andReturn();

    DeviceDto createdDevice =
        objectMapper.readValue(createResult.getResponse().getContentAsString(), DeviceDto.class);
    Long deviceId = createdDevice.getId();

    DeviceUpdateDto patchDto = new DeviceUpdateDto();
    patchDto.setName("Updated Device");

    String patchRequest = objectMapper.writeValueAsString(patchDto);

    mockMvc
        .perform(
            patch("/api/v1/devices/{id}", deviceId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchRequest))
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath("$.errorCode")
                .value(
                    DeviceCannotBeUpdatedWhileStatusInUseException
                        .INVALID_DEVICE_UPDATE_ERROR_CODE));
  }
}
