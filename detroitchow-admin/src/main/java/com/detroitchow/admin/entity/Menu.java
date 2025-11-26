package com.detroitchow.admin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.time.OffsetDateTime;

@Entity
@Table(name = "menus", schema = "detroitchow")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Menu implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "menu_link", length = 2000)
    private String menuLink;

    @Column(name = "locationid", length = 50, insertable = false, updatable = false)
    private String locationid;

    @Column(name = "image", length = 2000)
    private String image;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "descr", length = 500)
    private String descr;

    @Column(name = "create_date")
    private OffsetDateTime createDate;

    @Column(name = "create_user", length = 100)
    private String createUser;

    @Column(name = "updated_date")
    private OffsetDateTime updatedDate;

    @Column(name = "update_user", length = 100)
    private String updateUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locationid", referencedColumnName = "locationid", nullable = false)
    @JsonIgnore
    private Location location;

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
