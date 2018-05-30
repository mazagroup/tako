package org.tako.jpa.demo.content.dao.impl;

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
import org.tako.jpa.demo.content.api.dao.ICommentDAOService;
import org.tako.jpa.demo.content.api.model.Comment;
import org.tako.jpa.demo.content.model.jpa.JPAComment;
import org.tako.jpa.demo.feed.api.dao.IFeedDAOService;

@SuppressWarnings("restriction")
@Transactional
public class CommentDAOImpl extends BaseServiceImpl implements ICommentDAOService {
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
	public List<Comment> findAll(int start, int end) throws ApplicationException {
		List<Comment> result = new ArrayList<>();
		try {
			List<JPABaseEntity> resultList = super.findAll(JPAComment.class,start,end);
			result = JPAEntityUtil.copy(resultList, Comment.class);
		}
		catch (Exception e) {
			throw processException(e);
		}
		return result;		
	}
	
	@Override
	public List<Comment> findAll() throws ApplicationException {
		List<Comment> result = new ArrayList<>();
		try {
			List<JPABaseEntity> resultList = super.findAll(JPAComment.class);
			result = JPAEntityUtil.copy(resultList, Comment.class);
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
			res = super.countAll(JPAComment.class);
		}
		catch (Exception e) {
			throw processException(e);
		}
		return res;
	}	
	
	@Override
	public Comment getByPrimary(Long pk) throws ApplicationException, NoSuchModelException {
		JPAComment jpaEntity = getByPrimary_(pk);
		return JPAEntityUtil.copy(jpaEntity, Comment.class);
	}
	
	public JPAComment getByPrimary_(Long pk) throws ApplicationException, NoSuchModelException {
		return  (JPAComment) super.findByPrimaryKey(JPAComment.class, pk);
	}

	@Override
	public Comment provide(Comment record)
			  throws ApplicationException, NoSuchModelException {
		Comment existingProblem = getByCode(record.getCode());
		if (Validator.isNull(existingProblem))
		{
			JPABaseEntity res = super.add(JPAEntityUtil.copy(record, JPAComment.class));
			existingProblem = JPAEntityUtil.copy(res, Comment.class);
		}
		else {
			record.setId(existingProblem.getId());
			existingProblem = update(record);
		}

		return existingProblem;
	}
	
	@Override
	public Comment update(Comment record) throws ApplicationException, NoSuchModelException {
		JPAComment prblm = (JPAComment) super.findByPrimaryKey(JPAComment.class,record.getId());
		JPABaseEntity res = super.update(prblm);
		Comment dto = JPAEntityUtil.copy(res, Comment.class);
		return dto;	
	}	
	
	
	@Override
	public Comment delete(Long id) throws ApplicationException, NoSuchModelException {
		JPAComment jpaEntity = (JPAComment) super.findByPrimaryKey(JPAComment.class, id);
		super.remove(jpaEntity);
		return JPAEntityUtil.copy(jpaEntity, Comment.class);
	}
	
	
	@Override
	public void removeAll() throws ApplicationException, NoSuchModelException {
		List<Comment> comments = findAll();
		comments.stream()
			.forEach(c -> {
				try {
					JPAComment jpaEntity = (JPAComment) super.findByPrimaryKey(JPAComment.class, c.getId());
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
	public Comment getByCode(String code) throws ApplicationException {
		JPAComment jpaEntity = (JPAComment) super.findWithAttribute(JPAComment.class, String.class,"code", code);
		return JPAEntityUtil.copy(jpaEntity, Comment.class);
	}


	@Override
	public Comment getByName(String name) throws ApplicationException {
		JPAComment jpaEntity = (JPAComment) super.findWithAttribute(JPAComment.class, String.class,"name", name);
		return JPAEntityUtil.copy(jpaEntity, Comment.class);
	}
	
	@Override
	public void createDefaults() throws ApplicationException {
	}
}