package com.qorporation.qluster.service.view.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.qorporation.qluster.entity.definition.User;
import com.qorporation.qluster.service.entity.EntityService;
import com.qorporation.qluster.service.logic.LogicService;
import com.qorporation.qluster.service.view.ViewAuthenticator;
import com.qorporation.qluster.service.view.ViewManager;
import com.qorporation.qluster.transaction.Transaction;

public class APIManager extends ViewManager<APIView, User> {

	public APIManager(EntityService entityService, LogicService logicService, ViewAuthenticator<User> authenticator) {
		super(entityService, logicService, authenticator);
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response) {
		Transaction transaction = this.entityService.startGlobalTransaction();
		
		APIRequest apiRequest = new APIRequest(request, response);
		handle(apiRequest);
		
		transaction.finish();
	}
	
}
