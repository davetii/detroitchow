package com.detroitchow.admin.repository;

import com.detroitchow.admin.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, String> {

    @Query("SELECT m FROM Menu m WHERE m.locationid = :locationid ORDER BY m.priority ASC")
    List<Menu> findByLocationidOrderByPriority(@Param("locationid") String locationid);
    void deleteByLocationid(String locationid);
}
