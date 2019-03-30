package com.redhat.photogallery.photo;

import com.redhat.photogallery.common.ServerComponent;
import com.redhat.photogallery.common.Constants;
import com.redhat.photogallery.common.DataStore;

import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.eventbus.EventBus;
import io.vertx.reactivex.core.eventbus.MessageProducer;
import io.vertx.reactivex.core.http.HttpServerResponse;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import io.vertx.reactivex.ext.web.handler.BodyHandler;

public class PhotoComponent implements ServerComponent {

	private static final Logger LOG = LoggerFactory.getLogger(PhotoComponent.class);

	private DataStore<PhotoItem> dataStore = new DataStore<>();

	MessageProducer<PhotoItem> topic;

	@Override
	public void registerRoutes(Router router) {
		router.post("/photos").handler(BodyHandler.create()).handler(this::createPhoto);
		router.get("/photos").handler(this::readAllPhotos);
	}

	@Override
	public void injectEventBus(EventBus eventBus) {
		topic = eventBus.<PhotoItem>publisher(Constants.PHOTOS_TOPIC_NAME);
	}

	private void createPhoto(RoutingContext rc) {
		PhotoItem addItem;
		try {
			addItem = rc.getBodyAsJson().mapTo(PhotoItem.class);
		} catch (Exception e) {
			LOG.error("Failed parse item {}", rc.getBodyAsString(), e);
			throw e;
		}
		PhotoItem item = dataStore.insertItem(addItem);
		HttpServerResponse response = rc.response();
		response.putHeader("content-type", "application/json");
		response.end(Json.encodePrettily(item.getId()));
		LOG.info("Inserted {} into data store", item);
		topic.write(item);
		LOG.info("Published item {} on topic {}", item, topic.address());
	}

	private void readAllPhotos(RoutingContext rc) {
		HttpServerResponse response = rc.response();
		response.putHeader("content-type", "application/json");
		response.end(Json.encodePrettily(dataStore.getAllItems()));
		LOG.info("Returned all {} items", dataStore.getAllItems().size());
	}

}