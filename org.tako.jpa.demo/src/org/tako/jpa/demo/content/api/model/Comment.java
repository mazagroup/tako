package org.tako.jpa.demo.content.api.model;

import java.io.Serializable;

import org.tako.jpa.core.model.BaseEntity;


public class Comment extends BaseEntity implements Serializable {
	private static final long serialVersionUID = -8157919539809554346L;

	public Comment() {
		super();
	}
	
	public Comment(final String name, final String description) {
		this();
		this.name = name;
		this.description = description;
	}
}
