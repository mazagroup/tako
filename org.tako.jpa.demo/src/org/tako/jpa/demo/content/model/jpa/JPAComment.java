package org.tako.jpa.demo.content.model.jpa;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.tako.jpa.core.model.JPABaseEntity;


@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@Table(name="tk_demo_content_comment_b")
public class JPAComment extends JPABaseEntity implements Serializable {
	private static final long serialVersionUID = -762904109277081158L;
}
