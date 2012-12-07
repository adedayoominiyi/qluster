package com.qorporation.qluster.service.cluster.constructor;

import com.qorporation.qluster.common.functional.F1;
import com.qorporation.qluster.service.cluster.ClusterRing;
import com.qorporation.qluster.service.cluster.layer.ClusterLayer;

public class ClusterRingConstructor implements F1<Class<? extends ClusterLayer>, ClusterRing> {

	@Override
	public ClusterRing f(Class<? extends ClusterLayer> a) {
		return new ClusterRing(a);
	}

}
