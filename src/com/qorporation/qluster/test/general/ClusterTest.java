package com.qorporation.qluster.test.general;

import com.qorporation.qluster.common.FutureResponse;
import com.qorporation.qluster.service.cluster.protocol.GraphProtocol;
import com.qorporation.qluster.test.TestRunner;
import com.qorporation.qluster.util.ErrorControl;

public class ClusterTest extends TestRunner {

	public void test() {
		try {
			GraphProtocol graphProtocol = this.logicService.getClusterService().getProtocol(GraphProtocol.class);
			FutureResponse<Object> res = graphProtocol.test("testing");
			this.logger.info(String.format("cluster response: %s", res.get().toString()));
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
	}
	
}
