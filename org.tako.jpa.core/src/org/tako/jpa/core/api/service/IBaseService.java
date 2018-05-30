package org.tako.jpa.core.api.service;

import org.tako.jpa.core.api.ApplicationException;

public interface IBaseService {
	public void createDefaults() throws ApplicationException, Exception;
}