package de.ilyes.device.service;

import de.ilyes.device.exception.DeviceCannotBeDeletedWhileStatusInUseException;
import de.ilyes.device.exception.DeviceCannotBeUpdatedWhileStatusInUseException;
import de.ilyes.device.exception.DeviceNotFoundException;
import de.ilyes.device.mapper.DeviceMapper;
import de.ilyes.device.mapper.dto.DeviceCreationDto;
import de.ilyes.device.mapper.dto.DeviceDto;
import de.ilyes.device.mapper.dto.DevicePatchDto;
import de.ilyes.device.mapper.dto.DeviceState;
import de.ilyes.device.mapper.dto.DeviceUpdateDto;
import de.ilyes.device.repository.entity.DeviceEntity;
import de.ilyes.device.repository.entity.DeviceRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DeviceService {
  private static final Logger log = LoggerFactory.getLogger(DeviceService.class);

  private final DeviceRepository deviceRepository;
  private final DeviceMapper deviceMapper;

  public DeviceService(DeviceRepository deviceRepository, DeviceMapper deviceMapper) {
    this.deviceRepository = deviceRepository;
    this.deviceMapper = deviceMapper;
  }

  public DeviceDto create(DeviceCreationDto dto) {
    log.info("Creating a new device");
    DeviceEntity entity = deviceMapper.toEntity(dto);
    DeviceEntity saved = deviceRepository.save(entity);
    DeviceDto result = deviceMapper.toDto(saved);
    log.info("Device created with id: {}", result.getId());
    return result;
  }

  public DeviceDto getById(Long id) {
    log.info("Fetching device by id: {}", id);
    DeviceDto result =
        deviceRepository
            .findById(id)
            .map(deviceMapper::toDto)
            .orElseThrow(DeviceNotFoundException::new);
    log.info("Device found with id: {}", result.getId());
    return result;
  }

  public DeviceDto update(Long id, DeviceUpdateDto dto) {
    log.info("Updating device id: {}", id);
    Optional<DeviceEntity> optional = deviceRepository.findById(id);
    DeviceEntity deviceEntity = optional.orElseThrow(DeviceNotFoundException::new);
    deviceEntity.setId(id);
    verifyIfNameOrBrandUpdateWhileStatusInUse(
        deviceEntity, dto.getName(), dto.getBrand(), dto.getDeviceState());
    deviceMapper.updateEntity(dto, deviceEntity);
    DeviceEntity updated = deviceRepository.save(deviceEntity);
    DeviceDto result = deviceMapper.toDto(updated);
    log.info("Device updated with id: {}", result.getId());
    return result;
  }

  public void delete(Long id) {
    log.info("Deleting device by id: {}", id);
    Optional<DeviceEntity> optional = deviceRepository.findById(id);
    DeviceEntity deviceEntity = optional.orElseThrow(DeviceNotFoundException::new);
    if (DeviceState.IN_USE.equals(deviceEntity.getDeviceState())) {
      throw new DeviceCannotBeDeletedWhileStatusInUseException();
    }
    deviceRepository.deleteById(id);
    log.info("Device deleted, id: {}", id);
  }

  public DeviceDto patchDevice(Long id, DevicePatchDto dto) {
    log.info("Patching device id: {}", id);
    Optional<DeviceEntity> optional = deviceRepository.findById(id);
    DeviceEntity deviceEntity = optional.orElseThrow(DeviceNotFoundException::new);
    verifyIfNameOrBrandUpdateWhileStatusInUse(
        deviceEntity, dto.getName(), dto.getBrand(), dto.getDeviceState());
    deviceMapper.patchEntity(dto, deviceEntity);
    DeviceEntity saved = deviceRepository.save(deviceEntity);
    DeviceDto result = deviceMapper.toDto(saved);
    log.info("Device patched with id: {}", result.getId());
    return result;
  }

  public Page<DeviceDto> findByBrandOrName(String brand, String name, Pageable pageable) {
    log.info("Finding devices");
    Page<DeviceDto> result =
        deviceRepository.findByBrandOrName(brand, name, pageable).map(deviceMapper::toDto);
    log.info("Devices found: {}", result.getTotalElements());
    return result;
  }

  private void verifyIfNameOrBrandUpdateWhileStatusInUse(
      DeviceEntity deviceEntity, String name, String brand, DeviceState deviceState) {
    if (DeviceState.IN_USE.equals(deviceEntity.getDeviceState())) {
      if (deviceState != null && !DeviceState.IN_USE.equals(deviceState)) {
        return;
      }
      boolean nameChanged = name != null && !name.equals(deviceEntity.getName());
      boolean brandChanged = brand != null && !brand.equals(deviceEntity.getBrand());
      if (nameChanged || brandChanged) {
        throw new DeviceCannotBeUpdatedWhileStatusInUseException();
      }
    }
  }
}
