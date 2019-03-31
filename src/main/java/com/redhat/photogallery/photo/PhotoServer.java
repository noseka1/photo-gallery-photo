package com.redhat.photogallery.photo;

import com.redhat.photogallery.common.Server;
import com.redhat.photogallery.common.VertxInit;

public class PhotoServer {

	private static final int LISTEN_PORT = 8080;

	public static void main(String[] args) {
		VertxInit.createClusteredVertx(vertx -> {
			vertx.deployVerticle(new Server(LISTEN_PORT, new PhotoComponent()));
		});
	}
}
