package com.qorporation.qluster.service.cluster.layer;

import com.qorporation.qluster.service.ServiceManager;

public abstract class ClusterLayer {
	
	public abstract void activate(ServiceManager serviceManager);
	public abstract void deactivate(ServiceManager serviceManager);

}
