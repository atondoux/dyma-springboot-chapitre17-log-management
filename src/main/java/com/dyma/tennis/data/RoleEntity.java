package com.dyma.tennis.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "dyma_role", schema = "public")
public class RoleEntity {

    @Id
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    public RoleEntity() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
