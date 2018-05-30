package org.tako.jpa.demo.feed.model.jpa;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.tako.jpa.core.model.JPABaseEntity;


@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@Table(name="tako_demo_feed_comment_a")
public class JPAPostComment extends JPABaseEntity implements Serializable {
	private static final long serialVersionUID = 8538105534304495532L;
	
	@ManyToOne(targetEntity = JPAPost.class)
	@JoinColumn
	private JPAPost post;

	private Long commentId;
	

	public JPAPostComment() {
		super();
	}

	public JPAPostComment(String name, String description, Long commentId) {
		this();
		this.name = name;
		this.description = description;
		this.commentId = commentId;
	}

	public JPAPost getPost() {
		return post;
	}

	public void setPost(JPAPost post) {
		this.post = post;
	}

	public Long getCommentId() {
		return commentId;
	}

	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}
}

