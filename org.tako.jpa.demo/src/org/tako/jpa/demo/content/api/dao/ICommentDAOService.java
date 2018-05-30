package org.tako.jpa.demo.content.api.dao;

import java.util.List;

import org.tako.jpa.core.api.ApplicationException;
import org.tako.jpa.core.api.dao.NoSuchModelException;
import org.tako.jpa.core.api.service.IBaseService;
import org.tako.jpa.demo.content.api.model.Comment;

public interface ICommentDAOService extends IBaseService {
	public List<Comment> findAll(int start, int end) throws ApplicationException;
	
	public List<Comment> findAll() throws ApplicationException ;
	
	public long countAll() throws ApplicationException;
	
	public Comment provide(Comment record) throws ApplicationException, NoSuchModelException;
	
	public Comment update(Comment record) throws ApplicationException, NoSuchModelException;
	
	public Comment delete(Long id) throws ApplicationException, NoSuchModelException;
	
	public void removeAll() throws ApplicationException, NoSuchModelException;
	
	public Comment getByPrimary(Long pk) throws ApplicationException, NoSuchModelException;

	public Comment getByCode(String code) throws ApplicationException;

	public Comment getByName(String name) throws ApplicationException;
}
