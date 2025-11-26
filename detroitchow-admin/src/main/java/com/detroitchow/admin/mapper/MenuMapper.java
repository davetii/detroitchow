package com.detroitchow.admin.mapper;

import com.detroitchow.admin.dto.MenuDto;
import com.detroitchow.admin.entity.Menu;
import org.springframework.stereotype.Component;

@Component
public class MenuMapper {

    /**
     * Convert Menu entity to MenuDto
     */
    public MenuDto toDto(Menu menu) {
        if (menu == null) {
            return null;
        }

        return MenuDto.builder()
                .menuLink(menu.getMenuLink())
                .descr(menu.getDescr())
                .priority(menu.getPriority())
                .image(menu.getImage())
                .createDate(menu.getCreateDate())
                .createUser(menu.getCreateUser())
                .updatedDate(menu.getUpdatedDate())
                .updateUser(menu.getUpdateUser())
                .build();
    }

    /**
     * Convert MenuDto to Menu entity
     */
    public Menu toEntity(MenuDto dto) {
        if (dto == null) {
            return null;
        }

        return Menu.builder()
                .menuLink(dto.getMenuLink())
                .descr(dto.getDescr())
                .priority(dto.getPriority())
                .image(dto.getImage())
                .createDate(dto.getCreateDate())
                .createUser(dto.getCreateUser())
                .updatedDate(dto.getUpdatedDate())
                .updateUser(dto.getUpdateUser())
                .build();
    }
}
