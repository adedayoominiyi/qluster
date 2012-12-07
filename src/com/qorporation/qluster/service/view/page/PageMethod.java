package com.qorporation.qluster.service.view.page;

import java.lang.reflect.Method;
import java.util.List;

import com.google.code.regex.NamedPattern;

import com.qorporation.qluster.entity.definition.User;
import com.qorporation.qluster.service.view.ViewAuthenticator;
import com.qorporation.qluster.service.view.ViewHandler;
import com.qorporation.qluster.service.view.ViewMethod;

public class PageMethod extends ViewMethod<PageView, User> {

	public PageMethod(Method method, ViewHandler<PageView, User> handler,
			List<NamedPattern> patterns, ViewAuthenticator<User> authenticator) {
		super(method, handler, patterns, authenticator);
	}

}
