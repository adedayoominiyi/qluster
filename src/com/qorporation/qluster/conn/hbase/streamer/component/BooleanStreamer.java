package com.qorporation.qluster.conn.hbase.streamer.component;

import java.lang.reflect.ParameterizedType;

import com.qorporation.qluster.conn.hbase.streamer.HBaseComponentStreamer;
import com.qorporation.qluster.service.entity.EntityService;
import com.qorporation.qluster.util.Serialization;

public class BooleanStreamer extends HBaseComponentStreamer<Boolean> {

	public BooleanStreamer(EntityService service, Class<Boolean> type,
			ParameterizedType subType) {
		super(service, type, subType);
	}

	@Override
	public Boolean read(byte[] data) {
		return Serialization.deserializeBoolean(data);
	}

	@Override
	public byte[] write(Object comp, byte[] name, long ts) {
		return Serialization.serialize((Boolean) comp);
	}
	
}
