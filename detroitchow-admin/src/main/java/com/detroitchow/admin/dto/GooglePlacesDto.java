package com.detroitchow.admin.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GooglePlacesDto {

    private Integer id;
    private String locationid;
    private String placeId;
    private String lat;
    private String lng;
    private String phone1;
    private String phone2;
    private String formattedAddress;
    private String website;
    private String googleUrl;
    private String businessStatus;
    private JsonNode txtsearchJson;
    private JsonNode detailJson;
    private JsonNode storeJson;
    private String premisePlaceId;
    private String storePlaceId;
}
