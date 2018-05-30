package org.tako.jpa.demo.itests;

import static org.amdatu.testing.configurator.TestConfigurator.cleanUp;
import static org.amdatu.testing.configurator.TestConfigurator.configure;
import static org.amdatu.testing.configurator.TestConfigurator.createServiceDependency;
import static org.amdatu.testing.configurator.TestUtils.getService;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;

import org.tako.jpa.core.api.ApplicationException;
import org.tako.jpa.core.api.dao.NoSuchModelException;
import org.tako.jpa.demo.content.api.dao.ICommentDAOService;
import org.tako.jpa.demo.content.api.model.Comment;
import org.tako.jpa.demo.feed.api.dao.IFeedDAOService;
import org.tako.jpa.demo.feed.api.model.Feed;
import org.tako.jpa.demo.feed.api.model.Post;
import org.tako.jpa.demo.feed.api.model.PostComment;
import org.tako.jpa.demo.feed.model.jpa.JPAFeed;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@SuppressWarnings("restriction")
public class FeedTest {
	private  ICommentDAOService commentService;
	private  EntityManager commentEM;
	private  IFeedDAOService feedService;
	private  EntityManager feedEM;

	
	@Before
	public void setUp() throws Exception {		
		configure(this)
		.add(createServiceDependency().setService(IFeedDAOService.class).setRequired(true))	
		.add(createServiceDependency().setService(ICommentDAOService.class).setRequired(true))	
		.apply();
				
		TimeUnit.SECONDS.sleep(1);
		
		commentService = getService(ICommentDAOService.class);
		assertNotNull(commentService);
		feedService = getService(IFeedDAOService.class);
		assertNotNull(feedService);
		
		commentEM = getService(EntityManager.class,String.format("(%s=%s)",org.osgi.service.jpa.EntityManagerFactoryBuilder.JPA_UNIT_NAME,"tako_demo_content"));
		assertNotNull(commentEM);
		feedEM = getService(EntityManager.class,String.format("(%s=%s)",org.osgi.service.jpa.EntityManagerFactoryBuilder.JPA_UNIT_NAME,"tako_demo_feed"));
		assertNotNull(feedEM);
		
		//Reset
		commentService.removeAll();
		feedService.removeAll();
		
		//Create feed, posts, comments
		Feed feed = feedService.provide(new Feed("Test Feed", "Takos"));
		Post post = feedService.addPostToFeed(feed, new Post("Hello world","say hello"));
		
		Comment comment = commentService.provide(new Comment("MK", "First comment on post"));
		PostComment postComment = feedService.addCommentToPost(post, new PostComment("hello back", "hello back", comment.getId()));
	}
	
	@Test
	public void test() throws ApplicationException, NoSuchModelException {
		//Comment comment = commentService.provide(new Comment("MK", "First comment on post"));
		//assertNotNull(comment);
	}

	@After
	public void after() {
		cleanUp(this);
	}
    
}
