package com.redhat.photogallery.photo;

import com.redhat.photogallery.common.DataStoreItem;

public class PhotoItem extends DataStoreItem {

	private String name;
	private String category;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override
	public String toString() {
		return "PhotoItem [name=" + name + ", category=" + category + ", id=" + id + "]";
	}
}
