package com.redhat.photogallery.photo;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.photogallery.common.Constants;
import com.redhat.photogallery.common.data.DataStore;
import com.redhat.photogallery.common.data.PhotoItem;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.MessageProducer;

@Path("/photos")
public class PhotoComponent {

    private static final Logger LOG = LoggerFactory.getLogger(PhotoComponent.class);

    private DataStore<PhotoItem> dataStore = new DataStore<>();

    MessageProducer<JsonObject> topic;

    @Inject
    public void injectEventBus(EventBus eventBus) {
        topic = eventBus.<JsonObject>publisher(Constants.PHOTOS_TOPIC_NAME);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String createPhoto(PhotoItem item) {
        item.setId(dataStore.generateId());

        dataStore.putItem(item);
        LOG.info("Added {} into the data store", item);

        topic.write(JsonObject.mapFrom(item));
        LOG.info("Published {} on topic {}", item, topic.address());

        return item.getId();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response readAllPhotos() {
        List<PhotoItem> items = dataStore.getAllItems();
        LOG.info("Returned all {} items", items.size());
        return Response.ok(new GenericEntity<List<PhotoItem>>(items){}).build();
    }

}