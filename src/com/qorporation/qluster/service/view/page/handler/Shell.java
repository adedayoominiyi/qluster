package com.qorporation.qluster.service.view.page.handler;

import com.qorporation.qluster.annotation.AuthenticationPolicy;
import com.qorporation.qluster.annotation.Routing;
import com.qorporation.qluster.annotation.AuthenticationPolicy.AuthenticationLevel;
import com.qorporation.qluster.service.view.page.PageHandler;
import com.qorporation.qluster.service.view.page.PageRequest;

public class Shell extends PageHandler {
	
	@AuthenticationPolicy(level=AuthenticationLevel.PUBLIC)
	@Routing(patterns={"/shell"})
	public void index(PageRequest request) {		
		request.renderTemplate("shell/index.html");
	}
	
}
