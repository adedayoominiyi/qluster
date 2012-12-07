package com.qorporation.qluster.service.geo;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.qorporation.qluster.common.Pair;
import com.qorporation.qluster.config.Config;
import com.qorporation.qluster.service.Service;
import com.qorporation.qluster.service.ServiceManager;
import com.qorporation.qluster.service.external.ExternalService;
import com.qorporation.qluster.util.ErrorControl;
import com.qorporation.qluster.util.RelativePath;

import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;

@SuppressWarnings("unused")
public class GeoService extends Service {
	public static final Pair<Double, Double> DEFAULT_LAT_LON = new Pair<Double, Double>(100.0D, 100.0D);
	
	private ExternalService externalService = null;
	private LookupService lookupService = null;
	
	@Override
    public void init(ServiceManager serviceManager, Config config) {
		this.externalService = serviceManager.getService(ExternalService.class);
		
    	try {
    		File dataFile = new File(RelativePath.root().getAbsolutePath()
    				.concat(File.separator)
    				.concat("data")
    				.concat(File.separator)
    				.concat("geoip")
    				.concat(File.separator)
    				.concat("GeoIPCity.dat"));
    		
    		this.lookupService = new LookupService(dataFile.getPath(), LookupService.GEOIP_MEMORY_CACHE);
    	} catch (Exception e) {
    		ErrorControl.logException(e);
    	}
    }
    
    public Location lookupIP(String ip) {
    	return this.lookupService.getLocation(ip);
    }
	
}
