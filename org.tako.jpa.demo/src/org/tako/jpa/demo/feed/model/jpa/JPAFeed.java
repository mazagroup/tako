package org.tako.jpa.demo.feed.model.jpa;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.tako.jpa.core.model.JPABaseEntity;


@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@Table(name="tk_demo_feed_feed")
public class JPAFeed extends JPABaseEntity implements Serializable {
	private static final long serialVersionUID = -8154233623279419488L;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy="feed")
	private Set<JPAPost> posts = new java.util.HashSet<JPAPost>();
	
	public JPAFeed() {
		super();
	}
	
	public Set<JPAPost> getPosts() {
		return posts;
	}
	public void setPosts(Set<JPAPost> posts) {
		this.posts = posts;
	}
}
