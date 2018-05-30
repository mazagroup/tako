package org.tako.jpa.demo.feed.api.dao;

import java.util.List;

import org.tako.jpa.core.api.ApplicationException;
import org.tako.jpa.core.api.dao.NoSuchModelException;
import org.tako.jpa.core.api.service.IBaseService;
import org.tako.jpa.demo.feed.api.model.Feed;
import org.tako.jpa.demo.feed.api.model.Post;
import org.tako.jpa.demo.feed.api.model.PostComment;

public interface IFeedDAOService extends IBaseService {
	public List<Feed> findAll(int start, int end) throws ApplicationException;
	
	public long countAll() throws ApplicationException;
	
	public Feed provide(Feed record) throws ApplicationException, NoSuchModelException;
	
	public Feed update(Feed record) throws ApplicationException, NoSuchModelException;
	
	public Feed delete(Long id) throws ApplicationException, NoSuchModelException;
	
	public void removeAll() throws ApplicationException, NoSuchModelException;
	
	public Feed getByPrimary(Long pk) throws ApplicationException, NoSuchModelException;

	public Feed getByCode(String code) throws ApplicationException;

	public Feed getByName(String name) throws ApplicationException;
	
	public Post addPostToFeed(Feed record, Post post) throws ApplicationException, NoSuchModelException;
	
	
	public List<Post> findAllPosts(int start, int end) throws ApplicationException;
	
	public long countAllPosts() throws ApplicationException;
	
	public Post providePost(Post record) throws ApplicationException, NoSuchModelException;
	
	public Post updatePost(Post record) throws ApplicationException, NoSuchModelException;
	
	public Post deletePost(Long id) throws ApplicationException, NoSuchModelException;
	
	public Post getPostByPrimary(Long pk) throws ApplicationException, NoSuchModelException;

	public Post getPostByCode(String code) throws ApplicationException;

	public Post getPostByName(String name) throws ApplicationException;
	
	public PostComment addCommentToPost(Post post, PostComment comment) throws ApplicationException, NoSuchModelException;
}
