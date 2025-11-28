package com.detroitchow.admin.mapper;

import com.detroitchow.admin.dto.LocationDto;
import com.detroitchow.admin.entity.Location;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class LocationMapper {

    private final MenuMapper menuMapper;

    public LocationMapper(MenuMapper menuMapper) {
        this.menuMapper = menuMapper;
    }

    /**
     * Convert Location entity to LocationDto
     */
    public LocationDto toDto(Location location) {
        if (location == null) {
            return null;
        }

        return LocationDto.builder()
                .locationid(location.getLocationid())
                .name(location.getName())
                .description(location.getDescription())
                .operatingStatus(location.getOperatingStatus())
                .address1(location.getAddress1())
                .address2(location.getAddress2())
                .city(location.getCity())
                .locality(location.getLocality())
                .zip(location.getZip())
                .region(location.getRegion())
                .country(location.getCountry())
                .phone1(location.getPhone1())
                .phone2(location.getPhone2())
                .lat(location.getLat())
                .lng(location.getLng())
                .website(location.getWebsite())
                .facebook(location.getFacebook())
                .twitter(location.getTwitter())
                .instagram(location.getInstagram())
                .opentable(location.getOpentable())
                .tripadvisor(location.getTripadvisor())
                .yelp(location.getYelp())
                .hours(location.getHours())
                .contactText(location.getContactText())
                .menus(location.getMenus() != null ?
                        location.getMenus().stream()
                                .map(menuMapper::toDto)
                                .collect(Collectors.toList()) : null)
                .createDate(location.getCreateDate())
                .createUser(location.getCreateUser())
                .updatedDate(location.getUpdatedDate())
                .updateUser(location.getUpdateUser())
                .build();
    }

    /**
     * Convert LocationDto to Location entity
     */
    public Location toEntity(LocationDto dto) {
        if (dto == null) {
            return null;
        }

        return Location.builder()
                .locationid(dto.getLocationid())
                .name(dto.getName())
                .description(dto.getDescription())
                .operatingStatus(dto.getOperatingStatus())
                .address1(dto.getAddress1())
                .address2(dto.getAddress2())
                .city(dto.getCity())
                .locality(dto.getLocality())
                .zip(dto.getZip())
                .region(dto.getRegion())
                .country(dto.getCountry())
                .phone1(dto.getPhone1())
                .phone2(dto.getPhone2())
                .hours(dto.getHours())
                .contactText(dto.getContactText())
                .website(dto.getWebsite())
                .twitter(dto.getTwitter())
                .instagram(dto.getInstagram())
                .opentable(dto.getOpentable())
                .tripadvisor(dto.getTripadvisor())
                .yelp(dto.getYelp())
                .lat(dto.getLat())
                .lng(dto.getLng())
                .facebook(dto.getFacebook())
                .build();
    }
}
