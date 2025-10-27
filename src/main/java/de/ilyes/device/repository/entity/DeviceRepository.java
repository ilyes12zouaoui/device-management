package de.ilyes.device.repository.entity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DeviceRepository extends JpaRepository<DeviceEntity, Long> {
  @Query(
      "SELECT d FROM DeviceEntity d "
          + "WHERE (COALESCE(:brand, '') = '' OR LOWER(d.brand) LIKE LOWER(:brand)) "
          + "AND (COALESCE(:name, '') = '' OR LOWER(d.name) LIKE LOWER(:name))")
  Page<DeviceEntity> findByBrandOrName(String brand, String name, Pageable pageable);
}
