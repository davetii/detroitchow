package com.detroitchow.admin.repository;

import com.detroitchow.admin.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, String> {

    @Query("SELECT l FROM Location l WHERE l.status = :status")
    Page<Location> findByStatus(@Param("status") Location.LocationStatus status, Pageable pageable);

    @Query("SELECT l FROM Location l WHERE l.city = :city")
    List<Location> findByCity(@Param("city") String city);

    @Query("SELECT l FROM Location l WHERE l.region = :region")
    List<Location> findByRegion(@Param("region") String region);

    @Query("SELECT l FROM Location l WHERE l.country = :country")
    List<Location> findByCountry(@Param("country") String country);

    @Query("SELECT l FROM Location l WHERE l.status = :status AND l.city = :city")
    Page<Location> findByStatusAndCity(@Param("status") Location.LocationStatus status, 
                                       @Param("city") String city, Pageable pageable);

    Optional<Location> findByLocationid(String locationid);
}
