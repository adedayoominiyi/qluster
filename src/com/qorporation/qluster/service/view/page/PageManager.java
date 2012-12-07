package com.qorporation.qluster.service.view.page;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;

import com.qorporation.qluster.entity.definition.User;
import com.qorporation.qluster.service.entity.EntityService;
import com.qorporation.qluster.service.logic.LogicService;
import com.qorporation.qluster.service.view.ViewAuthenticator;
import com.qorporation.qluster.service.view.ViewManager;
import com.qorporation.qluster.transaction.Transaction;
import com.qorporation.qluster.util.ErrorControl;
import com.qorporation.qluster.util.RelativePath;

public class PageManager extends ViewManager<PageView, User> {

	private Configuration templateConfig;
	
	public PageManager(EntityService entityService, LogicService logicService, ViewAuthenticator<User> authenticator) {
		super(entityService, logicService, authenticator);
		setupTemplates();
	}
	
	private void setupTemplates() {
		try {
			File templateDir = new File(RelativePath.root().getAbsolutePath()
												.concat(File.separator)
												.concat("templates"));
			
			this.templateConfig = new Configuration();
			this.templateConfig.setLocale(java.util.Locale.CANADA);
			this.templateConfig.setNumberFormat("0.##");  
			
			FileTemplateLoader ftl = new FileTemplateLoader(templateDir);
			this.templateConfig.setTemplateLoader(ftl);
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response) {
		Transaction transaction = this.entityService.startGlobalTransaction();
		
		PageRequest pageRequest = new PageRequest(this.entityService, this.logicService, this.templateConfig, request, response);
		handle(pageRequest);
		
		transaction.finish();
	}
	
}
