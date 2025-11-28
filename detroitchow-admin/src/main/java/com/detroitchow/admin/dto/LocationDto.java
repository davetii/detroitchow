package com.detroitchow.admin.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationDto {

    private String locationid;
    private String name;
    private String description;
    private String operatingStatus;
    private String address1;
    private String address2;
    private String city;
    private String locality;
    private String zip;
    private String region;
    private String country;
    private String phone1;
    private String phone2;
    private String lat;
    private String lng;
    private String website;
    private String facebook;
    private String twitter;
    private String instagram;
    private String opentable;
    private String tripadvisor;
    private String yelp;
    private String hours;
    private String contactText;
    private OffsetDateTime createDate;
    private String createUser;
    private OffsetDateTime updatedDate;
    private String updateUser;
}
