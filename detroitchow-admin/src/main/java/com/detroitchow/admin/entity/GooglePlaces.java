package com.detroitchow.admin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "google_places", schema = "detroitchow")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GooglePlaces implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "locationid", length = 50, insertable = false, updatable = false)
    private String locationid;

    @Column(name = "place_id", length = 500, nullable = false)
    private String placeId;

    @Column(name = "lat", length = 50)
    private String lat;

    @Column(name = "lng", length = 50)
    private String lng;

    @Column(name = "phone1", length = 500)
    private String phone1;

    @Column(name = "phone2", length = 500)
    private String phone2;

    @Column(name = "formatted_address", length = 1000)
    private String formattedAddress;

    @Column(name = "website", length = 1000)
    private String website;

    @Column(name = "google_url", length = 1000)
    private String googleUrl;

    @Column(name = "business_status", length = 50)
    private String businessStatus;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "txtsearch_json", columnDefinition = "jsonb")
    private JsonNode txtsearchJson;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "detail_json", columnDefinition = "jsonb")
    private JsonNode detailJson;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "store_json", columnDefinition = "jsonb")
    private JsonNode storeJson;

    @Column(name = "premise_place_id", length = 500)
    private String premisePlaceId;

    @Column(name = "store_place_id", length = 500)
    private String storePlaceId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locationid", referencedColumnName = "locationid", nullable = false)
    @JsonIgnore
    private Location location;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
