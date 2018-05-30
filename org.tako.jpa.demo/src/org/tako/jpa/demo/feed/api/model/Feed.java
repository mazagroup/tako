package org.tako.jpa.demo.feed.api.model;

import java.io.Serializable;
import java.util.Set;

import org.tako.jpa.core.model.BaseEntity;


public class Feed extends BaseEntity implements Serializable {
	private static final long serialVersionUID = -6491114766416287416L;
	private Set<Post> posts;
	
	public Feed() {
		super();
	}
	
	public Feed(final String name, final String description) {
		this.name = name;
		this.code = description;
	}

	public Set<Post> getPosts() {
		return posts;
	}

	public void setPosts(Set<Post> posts) {
		this.posts = posts;
	}
}
