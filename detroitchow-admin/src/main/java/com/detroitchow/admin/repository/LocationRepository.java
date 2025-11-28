package com.detroitchow.admin.repository;

import com.detroitchow.admin.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, String> {

    @Query("SELECT l FROM Location l WHERE l.operatingStatus in ('active', 'temporarily_closed')")
    List<Location> getAllLocations();
    Optional<Location> findByLocationid(String locationid);
}
