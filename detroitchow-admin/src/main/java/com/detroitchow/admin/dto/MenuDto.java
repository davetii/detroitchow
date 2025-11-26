package com.detroitchow.admin.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MenuDto {

    private String menuLink;
    private String descr;
    private Integer priority;
    private String image;
    private OffsetDateTime createDate;
    private String createUser;
    private OffsetDateTime updatedDate;
    private String updateUser;
}
