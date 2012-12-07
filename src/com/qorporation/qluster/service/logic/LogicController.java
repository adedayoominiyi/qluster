package com.qorporation.qluster.service.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qorporation.qluster.service.ServiceManager;

public class LogicController {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected ServiceManager serviceManager = null;
	protected LogicService logicService = null;
	
	public void setup(ServiceManager serviceManager, LogicService logicService) {
		this.serviceManager = serviceManager;
		this.logicService = logicService;
	}

	public void init() {
	}
	
}
