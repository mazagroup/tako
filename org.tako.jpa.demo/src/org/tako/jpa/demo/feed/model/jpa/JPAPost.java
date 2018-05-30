package org.tako.jpa.demo.feed.model.jpa;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.tako.jpa.core.model.JPABaseEntity;


@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@Table(name="tako_demo_feed_post")
public class JPAPost extends JPABaseEntity implements Serializable {
	private static final long serialVersionUID = 8538105534304495532L;
	
	@ManyToOne(targetEntity = JPAFeed.class)
	@JoinColumn
	private JPAFeed feed;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy="post")
	private Set<JPAPostComment> comments = new java.util.HashSet<JPAPostComment>();

	public JPAPost() {
		super();
	}

	public JPAPost(String name, String description) {
		this();
		this.name = name;
		this.code = description;
	}

	public JPAFeed getFeed() {
		return feed;
	}

	public void setFeed(JPAFeed feed) {
		this.feed = feed;
	}

	public Set<JPAPostComment> getComments() {
		if (comments == null)
			comments = new java.util.HashSet<JPAPostComment>();
		return comments;
	}

	public void setComments(Set<JPAPostComment> comments) {
		this.comments = comments;
	}
}

