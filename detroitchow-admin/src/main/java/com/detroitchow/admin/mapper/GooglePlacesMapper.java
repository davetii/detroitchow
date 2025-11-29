package com.detroitchow.admin.mapper;

import com.detroitchow.admin.dto.GooglePlacesDto;
import com.detroitchow.admin.entity.GooglePlaces;
import org.springframework.stereotype.Component;

@Component
public class GooglePlacesMapper {

    /**
     * Convert GooglePlaces entity to GooglePlacesDto
     */
    public GooglePlacesDto toDto(GooglePlaces googlePlaces) {
        if (googlePlaces == null) {
            return null;
        }

        return GooglePlacesDto.builder()
                .id(googlePlaces.getId())
                .locationid(googlePlaces.getLocationid())
                .placeId(googlePlaces.getPlaceId())
                .lat(googlePlaces.getLat())
                .lng(googlePlaces.getLng())
                .phone1(googlePlaces.getPhone1())
                .phone2(googlePlaces.getPhone2())
                .formattedAddress(googlePlaces.getFormattedAddress())
                .website(googlePlaces.getWebsite())
                .googleUrl(googlePlaces.getGoogleUrl())
                .businessStatus(googlePlaces.getBusinessStatus())
                .txtsearchJson(googlePlaces.getTxtsearchJson())
                .detailJson(googlePlaces.getDetailJson())
                .storeJson(googlePlaces.getStoreJson())
                .premisePlaceId(googlePlaces.getPremisePlaceId())
                .storePlaceId(googlePlaces.getStorePlaceId())
                .name(googlePlaces.getName())
                .build();
    }

    /**
     * Convert GooglePlacesDto to GooglePlaces entity
     */
    public GooglePlaces toEntity(GooglePlacesDto dto) {
        if (dto == null) {
            return null;
        }

        return GooglePlaces.builder()
                .id(dto.getId())
                .locationid(dto.getLocationid())
                .placeId(dto.getPlaceId())
                .lat(dto.getLat())
                .lng(dto.getLng())
                .phone1(dto.getPhone1())
                .phone2(dto.getPhone2())
                .formattedAddress(dto.getFormattedAddress())
                .website(dto.getWebsite())
                .googleUrl(dto.getGoogleUrl())
                .businessStatus(dto.getBusinessStatus())
                .txtsearchJson(dto.getTxtsearchJson())
                .detailJson(dto.getDetailJson())
                .storeJson(dto.getStoreJson())
                .premisePlaceId(dto.getPremisePlaceId())
                .storePlaceId(dto.getStorePlaceId())
                .name(dto.getName())
                .build();
    }
}
