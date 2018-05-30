package org.tako.jpa.demo.feed.api.model;

import java.io.Serializable;

import org.tako.jpa.core.model.BaseEntity;


public class PostComment extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 6973404244365476749L;

	private Long commentId;
	
	private Feed feed;
	
	public PostComment() {
		super();
	}
	
	public PostComment(final String name, final String description) {
		this();
		this.name = name;
		this.description = description;
	}
	
	public PostComment(final String name, final String code, final Long commentId) {
		this(name, code);
		this.commentId = commentId;
	}

	public Feed getFeed() {
		return feed;
	}

	public void setFeed(Feed feed) {
		this.feed = feed;
	}

	public Long getCommentId() {
		return commentId;
	}

	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}
}
