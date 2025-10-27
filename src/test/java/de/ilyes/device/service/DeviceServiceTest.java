package de.ilyes.device.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import de.ilyes.device.exception.DeviceCannotBeDeletedWhileStatusInUseException;
import de.ilyes.device.exception.DeviceNotFoundException;
import de.ilyes.device.mapper.DeviceMapper;
import de.ilyes.device.mapper.dto.*;
import de.ilyes.device.repository.entity.DeviceEntity;
import de.ilyes.device.repository.entity.DeviceRepository;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

class DeviceServiceTest {

  @Mock private DeviceRepository deviceRepository;

  @Mock private DeviceMapper deviceMapper;

  @InjectMocks private DeviceService deviceService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void create_WithValidInput_ShouldReturnCreatedDevice() {
    DeviceCreationDto creationDto = new DeviceCreationDto();
    DeviceEntity entity = new DeviceEntity();
    DeviceDto dto = new DeviceDto();

    when(deviceMapper.toEntity(creationDto)).thenReturn(entity);
    when(deviceRepository.save(entity)).thenReturn(entity);
    when(deviceMapper.toDto(entity)).thenReturn(dto);

    DeviceDto result = deviceService.create(creationDto);

    assertNotNull(result);
    verify(deviceRepository).save(entity);
  }

  @Test
  void getById_WhenDeviceExists_ShouldReturnDevice() {
    Long id = 1L;
    DeviceEntity entity = new DeviceEntity();
    DeviceDto dto = new DeviceDto();

    when(deviceRepository.findById(id)).thenReturn(Optional.of(entity));
    when(deviceMapper.toDto(entity)).thenReturn(dto);

    DeviceDto result = deviceService.getById(id);

    assertNotNull(result);
    verify(deviceRepository).findById(id);
  }

  @Test
  void getById_WhenDeviceDoesNotExist_ShouldThrowDeviceNotFoundException() {
    Long id = 1L;

    when(deviceRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(DeviceNotFoundException.class, () -> deviceService.getById(id));
    verify(deviceRepository).findById(id);
  }

  @Test
  void update_WithValidInput_ShouldUpdateDevice() {
    Long id = 1L;
    DeviceUpdateDto updateDto = new DeviceUpdateDto();
    DeviceEntity entity = new DeviceEntity();
    DeviceDto dto = new DeviceDto();

    when(deviceRepository.findById(id)).thenReturn(Optional.of(entity));
    when(deviceRepository.save(entity)).thenReturn(entity);
    when(deviceMapper.toDto(entity)).thenReturn(dto);

    DeviceDto result = deviceService.update(id, updateDto);

    assertNotNull(result);
    verify(deviceRepository).save(entity);
  }

  @Test
  void delete_WhenDeviceNotInUse_ShouldDeleteDevice() {
    Long id = 1L;
    DeviceEntity entity = new DeviceEntity();
    entity.setDeviceState(DeviceState.AVAILABLE);

    when(deviceRepository.findById(id)).thenReturn(Optional.of(entity));

    deviceService.delete(id);

    verify(deviceRepository).deleteById(id);
  }

  @Test
  void delete_WhenDeviceInUse_ShouldThrowException() {
    Long id = 1L;
    DeviceEntity entity = new DeviceEntity();
    entity.setDeviceState(DeviceState.IN_USE);

    when(deviceRepository.findById(id)).thenReturn(Optional.of(entity));

    assertThrows(
        DeviceCannotBeDeletedWhileStatusInUseException.class, () -> deviceService.delete(id));
    verify(deviceRepository, never()).deleteById(id);
  }

  @Test
  void patchDevice_WithValidInput_ShouldPatchDevice() {
    Long id = 1L;
    DevicePatchDto patchDto = new DevicePatchDto();
    DeviceEntity entity = new DeviceEntity();
    DeviceDto dto = new DeviceDto();

    when(deviceRepository.findById(id)).thenReturn(Optional.of(entity));
    when(deviceRepository.save(entity)).thenReturn(entity);
    when(deviceMapper.toDto(entity)).thenReturn(dto);

    DeviceDto result = deviceService.patchDevice(id, patchDto);

    assertNotNull(result);
    verify(deviceRepository).save(entity);
  }

  @Test
  void findByBrandOrName_WithValidParameters_ShouldReturnMatchingDevices() {
    String brand = "Brand";
    String name = "Name";
    PageRequest pageable = PageRequest.of(0, 10);
    DeviceEntity entity = new DeviceEntity();
    DeviceDto dto = new DeviceDto();
    Page<DeviceEntity> page = new PageImpl<>(Collections.singletonList(entity));

    when(deviceRepository.findByBrandOrName(brand, name, pageable)).thenReturn(page);
    when(deviceMapper.toDto(entity)).thenReturn(dto);

    Page<DeviceDto> result = deviceService.findByBrandOrName(brand, name, pageable);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
    verify(deviceRepository).findByBrandOrName(brand, name, pageable);
  }
}
