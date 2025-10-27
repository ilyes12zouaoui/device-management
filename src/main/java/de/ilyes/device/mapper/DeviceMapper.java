package de.ilyes.device.mapper;

import de.ilyes.device.mapper.dto.DeviceCreationDto;
import de.ilyes.device.mapper.dto.DeviceDto;
import de.ilyes.device.mapper.dto.DevicePatchDto;
import de.ilyes.device.mapper.dto.DeviceUpdateDto;
import de.ilyes.device.repository.entity.DeviceEntity;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface DeviceMapper {
  DeviceDto toDto(DeviceEntity entity);

  DeviceEntity toEntity(DeviceCreationDto dto);

  void updateEntity(DeviceUpdateDto dto, @MappingTarget DeviceEntity entity);

  @BeanMapping(
      nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
  void patchEntity(DevicePatchDto dto, @MappingTarget DeviceEntity entity);
}
