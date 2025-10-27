package de.ilyes.device.repository.entity;

import de.ilyes.device.mapper.dto.DeviceState;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.time.ZonedDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "devices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "device_id_seq")
  @SequenceGenerator(name = "device_id_seq", sequenceName = "device_id_seq", allocationSize = 1)
  private Long id;

  @NotBlank(message = "Device name is mandatory")
  private String name;

  @NotBlank(message = "Device brand is mandatory")
  private String brand;

  @NotNull(message = "Device deviceState is mandatory")
  @Enumerated(EnumType.STRING)
  private DeviceState deviceState;

  @Column(updatable = false)
  @NotNull(message = "Device creationTime is mandatory")
  private ZonedDateTime creationTime;

  @CreationTimestamp private Instant createdOn;

  @UpdateTimestamp private Instant lastUpdatedOn;
}
