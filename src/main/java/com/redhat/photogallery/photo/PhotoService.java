package com.redhat.photogallery.photo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.photogallery.common.Constants;
import com.redhat.photogallery.common.data.PhotoCreatedMessage;

import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.MessageProducer;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@Path("/photos")
public class PhotoService {

    private static final Logger LOG = LoggerFactory.getLogger(PhotoService.class);

    private MessageProducer<JsonObject> topic;

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

        PhotoCreatedMessage message = createPhotoCreatedMessage(item);
        topic.write(JsonObject.mapFrom(message));
        LOG.info("Published {} on topic {}", message, topic.address());

        return item.id;
    }

    @POST
    @Path("/upload")
    @Consumes("multipart/form-data")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Long uploadFile(MultipartFormDataInput input) throws IOException {
        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<InputPart> inputParts = uploadForm.get("file");
        Long id = Long.valueOf(uploadForm.get("id").get(0).getBodyAsString());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        for (InputPart inputPart : inputParts) {
            InputStream inputStream = inputPart.getBody(InputStream.class, null);
            outputStream.write(IOUtils.toByteArray(inputStream));
        }

        PhotoItem photoItem = PhotoItem.findById(id);
        photoItem.file = outputStream.toByteArray();
        return id;
    }

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_OCTET_STREAM})
    public Response file(@PathParam("id") Long id) {
        PhotoItem item = PhotoItem.findById(id);
        if (item != null) {
            return Response.ok(item.file).build();
        } else {
            final String message = String.format("Entity not found for id: %d", id);
            return Response.status(NOT_FOUND).entity(message).build();
        }
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response readAllPhotos() {
        List<PhotoItem> items = PhotoItem.listAll();
        LOG.info("Returned all {} items", items.size());
        return Response.ok(items).build();
    }

    private PhotoCreatedMessage createPhotoCreatedMessage(PhotoItem item) {
        PhotoCreatedMessage msg = new PhotoCreatedMessage();
        msg.setId(item.id);
        msg.setName(item.name);
        msg.setCategory(item.category);
        return msg;
    }

}