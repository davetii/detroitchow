package com.detroitchow.admin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "locations", schema = "detroitchow", 
        indexes = {
                @Index(name = "idx_locations_status", columnList = "status"),
                @Index(name = "idx_locations_city", columnList = "city"),
                @Index(name = "idx_locations_region", columnList = "region"),
                @Index(name = "idx_locations_country", columnList = "country"),
                @Index(name = "idx_locations_locality", columnList = "locality"),
                @Index(name = "idx_locations_status_city", columnList = "status, city")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Location implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "locationid", length = 50)
    private String locationid;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", length = 2000)
    private String description;


    @Column(name = "operating_status", nullable = false, length = 20)
    private String operatingStatus;

    @Column(name = "address1", length = 500)
    private String address1;

    @Column(name = "address2", length = 500)
    private String address2;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "locality", length = 100)
    private String locality;

    @Column(name = "zip", length = 100)
    private String zip;

    @Column(name = "region", length = 100)
    private String region;

    @Column(name = "country", length = 2)
    private String country;

    @Column(name = "phone1", length = 20)
    private String phone1;

    @Column(name = "phone2", length = 20)
    private String phone2;

    @Column(name = "lat", length = 100)
    private String lat;

    @Column(name = "lng", length = 100)
    private String lng;

    @Column(name = "website", length = 2000)
    private String website;

    @Column(name = "facebook", length = 2000)
    private String facebook;

    @Column(name = "twitter", length = 2000)
    private String twitter;

    @Column(name = "instagram", length = 2000)
    private String instagram;

    @Column(name = "opentable", length = 2000)
    private String opentable;

    @Column(name = "tripadvisor", length = 2000)
    private String tripadvisor;

    @Column(name = "yelp", length = 2000)
    private String yelp;

    @Column(name = "hours", length = 2000)
    private String hours;

    @Column(name = "contact_text", length = 2000)
    private String contactText;

    @Column(name = "create_date")
    private OffsetDateTime createDate;

    @Column(name = "create_user", length = 100)
    private String createUser;

    @Column(name = "updated_date")
    private OffsetDateTime updatedDate;

    @Column(name = "update_user", length = 100)
    private String updateUser;

    @PrePersist
    protected void onCreate() {
        if (createDate == null) {
            createDate = OffsetDateTime.now();
        }
        updatedDate = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = OffsetDateTime.now();
    }


}
