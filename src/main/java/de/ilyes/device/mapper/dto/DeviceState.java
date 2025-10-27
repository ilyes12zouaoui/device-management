package de.ilyes.device.mapper.dto;

import lombok.Getter;

@Getter
public enum DeviceState {
  AVAILABLE("AVAILABLE"),
  IN_USE("IN_USE"),
  INACTIVE("INACTIVE");

  private final String value;

  DeviceState(String value) {
    this.value = value;
  }
}
