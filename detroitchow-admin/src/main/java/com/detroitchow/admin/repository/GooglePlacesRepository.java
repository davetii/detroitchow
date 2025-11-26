package com.detroitchow.admin.repository;

import com.detroitchow.admin.entity.GooglePlaces;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GooglePlacesRepository extends JpaRepository<GooglePlaces, Integer> {

    @Query("SELECT gp FROM GooglePlaces gp WHERE gp.locationid = :locationid")
    Optional<GooglePlaces> findByLocationid(@Param("locationid") String locationid);

    @Query("SELECT gp FROM GooglePlaces gp WHERE gp.placeId = :placeId")
    Optional<GooglePlaces> findByPlaceId(@Param("placeId") String placeId);

    void deleteByLocationid(String locationid);
}
