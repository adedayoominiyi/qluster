package com.qorporation.qluster.conn.cassandra.streamer.component;

import java.lang.reflect.ParameterizedType;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;

import com.qorporation.qluster.conn.cassandra.streamer.CassandraComponentStreamer;
import com.qorporation.qluster.conn.cassandra.streamer.CassandraPrimaryKeyStreamer;
import com.qorporation.qluster.entity.Definition;
import com.qorporation.qluster.entity.Entity;
import com.qorporation.qluster.entity.ProxyEntity;
import com.qorporation.qluster.service.entity.EntityService;
import com.qorporation.qluster.util.ErrorControl;
import com.qorporation.qluster.util.Serialization;

public class EntityStreamer<T extends Definition<?>> extends CassandraComponentStreamer<Entity<T>> implements CassandraPrimaryKeyStreamer<Entity<T>> {

	private Class<T> entityType = null;
	
	@SuppressWarnings("unchecked")
	public EntityStreamer(EntityService service, Class<Entity<T>> type,
			ParameterizedType subType) {
		super(service, type, subType);
		
		this.entityType = (Class<T>) subType.getActualTypeArguments()[0];
	}

	@Override
	public Entity<T> read(ColumnOrSuperColumn data) {
		String id = Serialization.deserializeString(data.column.getValue());
		return new ProxyEntity<T>(service, entityType, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ColumnOrSuperColumn write(Object comp, byte[] name, long ts) {
		ColumnOrSuperColumn c = new ColumnOrSuperColumn();
		c.column = new Column(name, Serialization.serialize(((Entity<T>) comp).getKey()), ts);
		return c;
	}

	@Override
	public String generateKey() {
		ErrorControl.fatal(String.format("Attempted to use %s to generate key", this.type.getName()));
		return null;		
	}

}
