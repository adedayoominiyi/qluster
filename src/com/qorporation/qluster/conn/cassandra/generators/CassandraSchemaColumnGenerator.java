package com.qorporation.qluster.conn.cassandra.generators;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.qorporation.qluster.conn.cassandra.streamer.CassandraColumnIndexStreamer;
import com.qorporation.qluster.conn.cassandra.streamer.CassandraComponentStreamer;
import com.qorporation.qluster.conn.cassandra.typesafety.CassandraBackedDefinition;
import com.qorporation.qluster.service.entity.EntityService;
import com.qorporation.qluster.util.ErrorControl;

public abstract class CassandraSchemaColumnGenerator<T extends CassandraBackedDefinition> {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected EntityService entityService = null;
	protected Map<Class<?>, Class<? extends CassandraComponentStreamer<?>>> streamers = null;
	
	protected void setup(EntityService entityService, Map<Class<?>, Class<? extends CassandraComponentStreamer<?>>> streamers) {
		this.entityService = entityService;
		this.streamers = streamers;
	}
	
	@SuppressWarnings("unchecked")
	protected CassandraColumnIndexStreamer<?> getStreamer(Class<?> type, ParameterizedType paramType) {
		try {
			Class<? extends CassandraColumnIndexStreamer<?>> cls = (Class<? extends CassandraColumnIndexStreamer<?>>) this.streamers.get(type);
			Constructor<? extends CassandraColumnIndexStreamer<?>> ctor = cls.getConstructor(EntityService.class, Class.class, ParameterizedType.class);
			CassandraColumnIndexStreamer<?> instance = ctor.newInstance(this.entityService, type, paramType);
			return instance;
		} catch (Exception e) {
			ErrorControl.fatal(String.format("Trying to use illegal type as column index: %s", type.getSimpleName()));
			return null;
		}
	}
	
	protected abstract void generate(ParameterizedType definitionType, Element column);
	
}
