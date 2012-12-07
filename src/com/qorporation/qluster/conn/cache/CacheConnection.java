package com.qorporation.qluster.conn.cache;

import com.qorporation.qluster.config.Config;
import com.qorporation.qluster.conn.Connection;
import com.qorporation.qluster.util.ErrorControl;

public class CacheConnection extends Connection {
	
    public CacheConnection(Config config, String poolKey) {
    	super(config, poolKey);
		setupTransport();
	}

	private void setupTransport() {        
        try {
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
    }
	
	@Override
	protected void close() {
	}
	
}
