package org.tako.jpa.demo.feed.api.model;

import java.io.Serializable;
import java.util.Set;

import org.tako.jpa.core.model.BaseEntity;
import org.tako.jpa.demo.feed.model.jpa.JPAPostComment;


public class Post extends BaseEntity implements Serializable {
	private static final long serialVersionUID = -5135269128249876038L;

	private Long linkId;
	
	private Feed feed;
	
	private Set<PostComment> comments = new java.util.HashSet<PostComment>();
	
	public Post() {
		super();
	}
	
	public Post(final String name, final String description) {
		this();
		this.name = name;
		this.description = description;
	}

	public Long getLinkId() {
		return linkId;
	}

	public void setLinkId(Long linkId) {
		this.linkId = linkId;
	}

	public Feed getFeed() {
		return feed;
	}

	public void setFeed(Feed feed) {
		this.feed = feed;
	}

	public Set<PostComment> getComments() {
		return comments;
	}

	public void setComments(Set<PostComment> comments) {
		this.comments = comments;
	}
}
