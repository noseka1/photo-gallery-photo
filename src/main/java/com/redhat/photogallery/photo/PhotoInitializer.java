package com.redhat.photogallery.photo;

import io.quarkus.runtime.StartupEvent;
import org.apache.commons.io.IOUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.transaction.Transactional;
import java.io.IOException;

@ApplicationScoped
public class PhotoInitializer {

    @Transactional
    void onStart(@Observes StartupEvent event) throws IOException {
        final PhotoItem odie = PhotoItem.findById(1L);
        odie.file = IOUtils.toByteArray(PhotoInitializer.class.getResourceAsStream("/Odie_the_Dog.png"));

        final PhotoItem garfield = PhotoItem.findById(2L);
        garfield.file = IOUtils.toByteArray(PhotoInitializer.class.getResourceAsStream("/Garfield_the_Cat.png"));

        final PhotoItem empireState = PhotoItem.findById(3L);
        empireState.file = IOUtils.toByteArray(PhotoInitializer.class.getResourceAsStream("/Empire_State_Building.jpg"));
    }
}
