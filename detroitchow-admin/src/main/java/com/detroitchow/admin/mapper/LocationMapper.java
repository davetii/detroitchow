package com.detroitchow.admin.mapper;

import com.detroitchow.admin.dto.LocationDto;
import com.detroitchow.admin.entity.Location;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class LocationMapper {

    private final MenuMapper menuMapper;
    private final GooglePlacesMapper googlePlacesMapper;

    public LocationMapper(MenuMapper menuMapper, GooglePlacesMapper googlePlacesMapper) {
        this.menuMapper = menuMapper;
        this.googlePlacesMapper = googlePlacesMapper;
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
                .status(location.getStatus() != null ? location.getStatus().name() : null)
                .address1(location.getAddress1())
                .address2(location.getAddress2())
                .city(location.getCity())
                .locality(location.getLocality())
                .zip(location.getZip())
                .region(location.getRegion())
                .country(location.getCountry())
                .phone1(location.getPhone1())
                .phone2(location.getPhone2())
                .lat(location.getLat() != null ? Double.parseDouble(location.getLat()) : null)
                .lng(location.getLng() != null ? Double.parseDouble(location.getLng()) : null)
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
                .googlePlaces(location.getGooglePlaces() != null ? 
                        googlePlacesMapper.toDto(location.getGooglePlaces()) : null)
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

        Location.LocationStatus status = null;
        if (dto.getStatus() != null) {
            try {
                status = Location.LocationStatus.valueOf(dto.getStatus());
            } catch (IllegalArgumentException e) {
                status = Location.LocationStatus.active;
            }
        }

        return Location.builder()
                .locationid(dto.getLocationid())
                .name(dto.getName())
                // ... rest of fields
                .build();
    }
}
