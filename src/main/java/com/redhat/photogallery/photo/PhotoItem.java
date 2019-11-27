package com.redhat.photogallery.photo;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class PhotoItem extends PanacheEntity {

    public String name;
    public String category;
    @JsonbTransient
    public byte[] file;

    @Override
    public String toString() {
        return "PhotoItem [id=" + id + ", name=" + name + ", category=" + category + "]";
    }

}
