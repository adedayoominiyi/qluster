package com.qorporation.qluster.service.view.api;

import java.lang.reflect.Method;
import java.util.List;

import com.google.code.regex.NamedPattern;

import com.qorporation.qluster.annotation.AuthenticationPolicy;
import com.qorporation.qluster.entity.Entity;
import com.qorporation.qluster.entity.definition.User;
import com.qorporation.qluster.service.view.ViewAuthenticator;
import com.qorporation.qluster.service.view.ViewHandler;
import com.qorporation.qluster.service.view.ViewMethod;
import com.qorporation.qluster.service.view.ViewRequest;

public class APIMethod extends ViewMethod<APIView, User> {

	public APIMethod(Method method, ViewHandler<APIView, User> handler,
			List<NamedPattern> patterns, ViewAuthenticator<User> authenticator) {
		super(method, handler, patterns, authenticator);
	}
	
	public boolean authenticate(ViewRequest<APIView, User> req) {
		Entity<User> user = req.getUser();
		
		if (user == null) {
			user = authenticator.fromToken(req.getParameter("key", null), req.getParameter("token", null));
			req.setUser(user);
		}
		
		return authenticator.authenticate(method.getAnnotation(AuthenticationPolicy.class).level(), user);
	}

}
