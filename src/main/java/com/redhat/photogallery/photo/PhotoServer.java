package com.redhat.photogallery.photo;

import com.redhat.photogallery.common.Server;

import io.vertx.core.logging.SLF4JLogDelegateFactory;
import io.vertx.reactivex.core.Vertx;

public class PhotoServer {

	private static final int LISTEN_PORT = 8080;

	public static void main(String[] args) {
		System.setProperty("vertx.logger-delegate-factory-class-name", SLF4JLogDelegateFactory.class.getName());
		Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(new Server(LISTEN_PORT, new PhotoComponent()));
	}
}
