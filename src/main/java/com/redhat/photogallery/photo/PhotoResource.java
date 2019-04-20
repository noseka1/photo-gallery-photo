package com.redhat.photogallery.photo;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
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
import com.redhat.photogallery.common.data.PhotoMessage;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.MessageProducer;

@Path("/photos")
public class PhotoResource {

    private static final Logger LOG = LoggerFactory.getLogger(PhotoResource.class);

    private MessageProducer<JsonObject> topic;

    @Inject
    EntityManager entityManager;

    @Inject
    public void injectEventBus(EventBus eventBus) {
        topic = eventBus.<JsonObject>publisher(Constants.PHOTOS_TOPIC_NAME);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Long createPhoto(PhotoItem item) {

        item.persist();
        LOG.info("Added {} into the data store", item);

        PhotoMessage photoMessage = createPhotoMessage(item);
        topic.write(JsonObject.mapFrom(photoMessage));
        LOG.info("Published {} on topic {}", photoMessage, topic.address());

        return item.id;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response readAllPhotos() {
        Query query = entityManager.createQuery("FROM PhotoItem");
        @SuppressWarnings("unchecked")
        List<PhotoItem> items = query.getResultList();
        LOG.info("Returned all {} items", items.size());
        return Response.ok(new GenericEntity<List<PhotoItem>>(items){}).build();
    }

    private PhotoMessage createPhotoMessage(PhotoItem item) {
        PhotoMessage msg = new PhotoMessage();
        msg.setId(item.id);
        msg.setName(item.name);
        msg.setCategory(item.category);
        return msg;
    }

}