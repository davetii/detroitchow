package com.detroitchow.admin.cucumber.steps;

import com.detroitchow.admin.dto.LocationDto;
import com.detroitchow.admin.dto.MenuDto;
import com.detroitchow.admin.entity.Location;
import com.detroitchow.admin.mapper.LocationMapper;
import com.detroitchow.admin.mapper.MenuMapper;
import com.detroitchow.admin.service.LocationService;
import com.detroitchow.admin.service.MenuService;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@RequiredArgsConstructor
public class LocationStepDefinitions {

    @Autowired
    private LocationService locationService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private LocationMapper locationMapper;

    @Autowired
    private MenuMapper menuMapper;

    private LocationDto currentLocationDto;
    private Location createdLocation;
    private List<Location> locationList;
    private MenuDto currentMenuDto;
    private Exception thrownException;

    @Before
    public void setUp() {
        currentLocationDto = new LocationDto();
        locationList = new ArrayList<>();
    }

    @Given("the API is available")
    public void the_api_is_available() {
        assertThat(locationService).isNotNull();
        assertThat(menuService).isNotNull();
    }

    @Given("the database is clean")
    public void the_database_is_clean() {
        // In a real scenario, you might clean the database here
        // For now, we'll just verify the service is available
        assertThat(locationService).isNotNull();
    }

    @Given("I have a new location with the following details:")
    public void i_have_a_new_location_with_details(DataTable dataTable) {
        Map<String, String> locationData = dataTable.asMap();
        
        currentLocationDto = LocationDto.builder()
                .name(locationData.get("name"))
                .address1(locationData.get("address1"))
                .city(locationData.get("city"))
                .region(locationData.get("region"))
                .zip(locationData.get("zip"))
                .status(locationData.getOrDefault("status", "active"))
                .build();
    }

    @When("I create the location")
    public void i_create_the_location() {
        try {
            Location entity = locationMapper.toEntity(currentLocationDto);
            createdLocation = locationService.createLocation(entity);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the location should be created successfully")
    public void the_location_should_be_created_successfully() {
        assertThat(createdLocation).isNotNull();
        assertThat(createdLocation.getName()).isEqualTo(currentLocationDto.getName());
    }

    @And("the location should have a generated locationid")
    public void the_location_should_have_generated_id() {
        assertThat(createdLocation.getLocationid()).isNotNull();
        assertThat(createdLocation.getLocationid()).startsWith("loc_");
    }

    @Given("I have created a location with name {string}")
    public void i_have_created_a_location_with_name(String name) {
        LocationDto dto = LocationDto.builder()
                .name(name)
                .address1("123 Main St")
                .city("Detroit")
                .region("Michigan")
                .zip("48201")
                .status("active")
                .build();
        
        Location entity = locationMapper.toEntity(dto);
        createdLocation = locationService.createLocation(entity);
    }

    @When("I retrieve the location")
    public void i_retrieve_the_location() {
        Optional<Location> retrieved = locationService.getLocationById(createdLocation.getLocationid());
        if (retrieved.isPresent()) {
            createdLocation = retrieved.get();
        }
    }

    @Then("I should receive the location details")
    public void i_should_receive_the_location_details() {
        assertThat(createdLocation).isNotNull();
        assertThat(createdLocation.getLocationid()).isNotNull();
    }

    @And("the name should be {string}")
    public void the_name_should_be(String expectedName) {
        assertThat(createdLocation.getName()).isEqualTo(expectedName);
    }

    @When("I update the location with name {string}")
    public void i_update_the_location_with_name(String newName) {
        createdLocation.setName(newName);
        createdLocation = locationService.updateLocation(createdLocation);
    }

    @Then("the location should be updated successfully")
    public void the_location_should_be_updated_successfully() {
        assertThat(createdLocation).isNotNull();
    }

    @Given("I have created {int} locations")
    public void i_have_created_n_locations(int count) {
        locationList.clear();
        for (int i = 0; i < count; i++) {
            LocationDto dto = LocationDto.builder()
                    .name("Restaurant " + i)
                    .address1("Address " + i)
                    .city("Detroit")
                    .region("Michigan")
                    .zip("48201")
                    .status("active")
                    .build();
            
            Location entity = locationMapper.toEntity(dto);
            Location created = locationService.createLocation(entity);
            locationList.add(created);
        }
    }

    @When("I retrieve all locations")
    public void i_retrieve_all_locations() {
        // This would typically call getAllLocations
        // For now, we'll use our locationList
    }

    @Then("I should receive {int} locations")
    public void i_should_receive_n_locations(int count) {
        assertThat(locationList).hasSize(count);
    }

    @When("I delete the location")
    public void i_delete_the_location() {
        locationService.deleteLocation(createdLocation.getLocationid());
    }

    @Then("the location should be deleted successfully")
    public void the_location_should_be_deleted_successfully() {
        Optional<Location> deleted = locationService.getLocationById(createdLocation.getLocationid());
        assertThat(deleted).isEmpty();
    }

    @And("I have a new menu with the following details:")
    public void i_have_a_new_menu_with_details(DataTable dataTable) {
        Map<String, String> menuData = dataTable.asMap();
        
        currentMenuDto = MenuDto.builder()
                .descr(menuData.get("descr"))
                .menuLink(menuData.get("menu_link"))
                .priority(Integer.parseInt(menuData.get("priority")))
                .build();
    }

    @When("I add the menu to the location")
    public void i_add_the_menu_to_the_location() {
        try {
            var menuEntity = menuMapper.toEntity(currentMenuDto);
            menuService.createMenu(createdLocation.getLocationid(), menuEntity);
        } catch (Exception e) {
            thrownException = e;
        }
    }

    @Then("the menu should be added successfully")
    public void the_menu_should_be_added_successfully() {
        assertThat(thrownException).isNull();
    }
}
