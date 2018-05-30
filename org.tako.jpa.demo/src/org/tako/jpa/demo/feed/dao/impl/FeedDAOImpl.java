package org.tako.jpa.demo.feed.dao.impl;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import org.amdatu.jta.Transactional;
import org.osgi.service.log.LogService;
import org.tako.jpa.core.api.ApplicationException;
import org.tako.jpa.core.api.dao.NoSuchModelException;
import org.tako.jpa.core.commons.util.Validator;
import org.tako.jpa.core.commons.util.jpa.JPAEntityUtil;
import org.tako.jpa.core.model.JPABaseEntity;
import org.tako.jpa.core.service.impl.BaseServiceImpl;
import org.tako.jpa.demo.content.api.model.Comment;
import org.tako.jpa.demo.content.model.jpa.JPAComment;
import org.tako.jpa.demo.feed.api.dao.IFeedDAOService;
import org.tako.jpa.demo.feed.api.model.Feed;
import org.tako.jpa.demo.feed.api.model.Post;
import org.tako.jpa.demo.feed.api.model.PostComment;
import org.tako.jpa.demo.feed.model.jpa.JPAFeed;
import org.tako.jpa.demo.feed.model.jpa.JPAPost;
import org.tako.jpa.demo.feed.model.jpa.JPAPostComment;

@SuppressWarnings("restriction")
@Transactional
public class FeedDAOImpl extends BaseServiceImpl implements IFeedDAOService {
	private volatile LogService logger;
	
	private volatile EntityManager em;
	
	@Override
	public LogService getLogger() {
		return logger;
	}

	public void setLogger(LogService logger) {
		this.logger = logger;
	}

	@Override
	public EntityManager getEm() {
		return em;
	}	
	
	@Override
	public List<Feed> findAll(int start, int end) throws ApplicationException {
		List<Feed> result = new ArrayList<>();
		try {
			List<JPABaseEntity> resultList = super.findAll(JPAFeed.class,start,end);
			result = JPAEntityUtil.copy(resultList, Feed.class);
		}
		catch (Exception e) {
			throw processException(e);
		}
		return result;		
	}
	
	public List<Feed> findAll() throws ApplicationException {
		List<Feed> result = new ArrayList<>();
		try {
			List<JPABaseEntity> resultList = super.findAll(JPAFeed.class);
			result = JPAEntityUtil.copy(resultList, Feed.class);
		}
		catch (Exception e) {
			throw processException(e);
		}
		return result;		
	}
	
	
	
	@Override
	public long countAll() throws ApplicationException {
		long res = 0;
		try {
			res = super.countAll(JPAFeed.class);
		}
		catch (Exception e) {
			throw processException(e);
		}
		return res;
	}	
	
	@Override
	public Feed getByPrimary(Long pk) throws ApplicationException, NoSuchModelException {
		JPAFeed jpaEntity = getByPrimary_(pk);
		return JPAEntityUtil.copy(jpaEntity, Feed.class);
	}
	
	public JPAFeed getByPrimary_(Long pk) throws ApplicationException, NoSuchModelException {
		return  (JPAFeed) super.findByPrimaryKey(JPAFeed.class, pk);
	}

	@Override
	public Feed provide(Feed record)
			  throws ApplicationException, NoSuchModelException {
		Feed existingProblem = getByCode(record.getCode());
		if (Validator.isNull(existingProblem))
		{
			JPABaseEntity res = super.add(JPAEntityUtil.copy(record, JPAFeed.class));
			existingProblem = JPAEntityUtil.copy(res, Feed.class);
		}
		else {
			record.setId(existingProblem.getId());
			existingProblem = update(record);
		}

		return existingProblem;
	}
	
	@Override
	public Feed update(Feed record) throws ApplicationException, NoSuchModelException {
		JPAFeed prblm = (JPAFeed) super.findByPrimaryKey(JPAFeed.class,record.getId());
		JPABaseEntity res = super.update(prblm);
		Feed dto = JPAEntityUtil.copy(res, Feed.class);
		return dto;	
	}	
	
	
	@Override
	public Feed delete(Long id) throws ApplicationException, NoSuchModelException {
		JPAFeed jpaEntity = (JPAFeed) super.findByPrimaryKey(JPAFeed.class, id);
		super.remove(jpaEntity);
		return JPAEntityUtil.copy(jpaEntity, Feed.class);
	}
	
	@Override
	public void removeAll() throws ApplicationException, NoSuchModelException {
		List<Feed> comments = findAll();
		comments.stream()
			.forEach(c -> {
				try {
					JPAFeed jpaEntity = (JPAFeed) super.findByPrimaryKey(JPAFeed.class, c.getId());
					super.remove(jpaEntity);
				} catch (ApplicationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchModelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
	}	
	
	@Override
	public Feed getByCode(String code) throws ApplicationException {
		JPAFeed jpaEntity = (JPAFeed) super.findWithAttribute(JPAFeed.class, String.class,"code", code);
		return JPAEntityUtil.copy(jpaEntity, Feed.class);
	}


	@Override
	public Feed getByName(String name) throws ApplicationException {
		JPAFeed jpaEntity = (JPAFeed) super.findWithAttribute(JPAFeed.class, String.class,"name", name);
		return JPAEntityUtil.copy(jpaEntity, Feed.class);
	}
	
	@Override
	public Post addPostToFeed(Feed record, Post post) throws ApplicationException, NoSuchModelException {
		JPAFeed feed = getByPrimary_(record.getId());
		JPAPost postEntity = JPAEntityUtil.copy(post, JPAPost.class);
		postEntity.setFeed(feed);
		feed.getPosts().add(postEntity);
		return JPAEntityUtil.copy(postEntity, Post.class);
	}
	
	//-- Posts
	@Override
	public List<Post> findAllPosts(int start, int end) throws ApplicationException {
		List<Post> result = new ArrayList<>();
		try {
			List<JPABaseEntity> resultList = super.findAll(JPAPost.class,start,end);
			result = JPAEntityUtil.copy(resultList, Post.class);
		}
		catch (Exception e) {
			throw processException(e);
		}
		return result;		
	}
	
	
	
	@Override
	public long countAllPosts() throws ApplicationException {
		long res = 0;
		try {
			res = super.countAll(JPAPost.class);
		}
		catch (Exception e) {
			throw processException(e);
		}
		return res;
	}	
	
	@Override
	public Post getPostByPrimary(Long pk) throws ApplicationException, NoSuchModelException {
		JPAPost jpaEntity = getPostByPrimary_(pk);
		return JPAEntityUtil.copy(jpaEntity, Post.class);
	}
	
	public JPAPost getPostByPrimary_(Long pk) throws ApplicationException, NoSuchModelException {
		return  (JPAPost) super.findByPrimaryKey(JPAPost.class, pk);
	}

	@Override
	public Post providePost(Post record)
			  throws ApplicationException, NoSuchModelException {
		Post existingProblem = getPostByCode(record.getCode());
		if (Validator.isNull(existingProblem))
		{
			JPABaseEntity res = super.add(JPAEntityUtil.copy(record, JPAPost.class));
			existingProblem = JPAEntityUtil.copy(res, Post.class);
		}
		else {
			record.setId(existingProblem.getId());
			existingProblem = updatePost(record);
		}

		return existingProblem;
	}
	
	@Override
	public Post updatePost(Post record) throws ApplicationException, NoSuchModelException {
		JPAPost prblm = (JPAPost) super.findByPrimaryKey(JPAPost.class,record.getId());
		JPABaseEntity res = super.update(prblm);
		Post dto = JPAEntityUtil.copy(res, Post.class);
		return dto;	
	}	
	
	
	@Override
	public Post deletePost(Long id) throws ApplicationException, NoSuchModelException {
		JPAPost jpaEntity = (JPAPost) super.findByPrimaryKey(JPAPost.class, id);
		super.remove(jpaEntity);
		return JPAEntityUtil.copy(jpaEntity, Post.class);
	}
	

	
	@Override
	public Post getPostByCode(String code) throws ApplicationException {
		JPAPost jpaEntity = (JPAPost) super.findWithAttribute(JPAPost.class, String.class,"code", code);
		return JPAEntityUtil.copy(jpaEntity, Post.class);
	}

	public JPAPost getPostByCode_(String code) throws ApplicationException {
		return (JPAPost) super.findWithAttribute(JPAPost.class, String.class,"code", code);
	}

	@Override
	public Post getPostByName(String name) throws ApplicationException {
		JPAPost jpaEntity = (JPAPost) super.findWithAttribute(JPAPost.class, String.class,"name", name);
		return JPAEntityUtil.copy(jpaEntity, Post.class);
	}
	
	@Override
	public PostComment addCommentToPost(Post post, PostComment comment) throws ApplicationException, NoSuchModelException {
		JPAPost postEntity = getPostByCode_(post.getCode());
		JPAPostComment commentEntity = JPAEntityUtil.copy(comment, JPAPostComment.class);
		commentEntity.setPost(postEntity);
		postEntity.getComments().add(commentEntity);
		return JPAEntityUtil.copy(commentEntity, PostComment.class);
	}
	
	@Override
	public void createDefaults() throws ApplicationException {
	}
}