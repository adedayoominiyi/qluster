package com.qorporation.qluster.conn.cassandra.streamer;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.cassandra.thrift.ColumnOrSuperColumn;

import com.qorporation.qluster.conn.cassandra.CassandraConnection;
import com.qorporation.qluster.conn.cassandra.typesafety.CassandraBackedDefinition;
import com.qorporation.qluster.conn.cassandra.typesafety.CassandraOpenColumn;
import com.qorporation.qluster.conn.cassandra.typesafety.CassandraOpenSuperColumn;
import com.qorporation.qluster.entity.Entity;
import com.qorporation.qluster.entity.typesafety.FieldKey;
import com.qorporation.qluster.entity.typesafety.PrimaryKey;
import com.qorporation.qluster.service.entity.EntityService;
import com.qorporation.qluster.util.ErrorControl;
import com.qorporation.qluster.util.Reflection;
import com.qorporation.qluster.util.Serialization;

public class CassandraOpenEntityStreamer<T extends CassandraBackedDefinition> extends CassandraEntityStreamer<T> {

	private CassandraComponentStreamer<?> valueStreamer = null;
	
	@SuppressWarnings("unchecked")
	public CassandraOpenEntityStreamer(EntityService entityService, Class<T> entityType, PrimaryKey<?, ?> primaryKey) {
		super(entityService, entityType, new LinkedList<FieldKey<?>>(), primaryKey);
		
		try {
			for (Type iface: entityType.getGenericInterfaces()) {	
				if (!ParameterizedType.class.isAssignableFrom(iface.getClass())) continue;
				
				String typeName = ((ParameterizedType) iface).getRawType().toString();
				if (!typeName.contains(CassandraOpenColumn.class.getSimpleName())
					&& !typeName.contains(CassandraOpenSuperColumn.class.getSimpleName())) continue;
				
				int paramLength = ((ParameterizedType) iface).getActualTypeArguments().length - 1;
				
			    Class<?> type = Reflection.getParamType(iface, paramLength);
			    ParameterizedType subType = Reflection.getParamSubType(iface, paramLength);
				
				Class<? extends CassandraComponentStreamer<?>> cls = this.entityService.getStreamer(CassandraConnection.class, CassandraComponentStreamer.class, type);
				Constructor<? extends CassandraComponentStreamer<?>> ctor = cls.getConstructor(EntityService.class, Class.class, ParameterizedType.class);
				valueStreamer = ctor.newInstance(this.entityService, type, subType);
			}
		} catch (Exception e) {
			ErrorControl.logException(e);
			System.exit(-1);
		}
	}
	
	@Override
	public List<ColumnOrSuperColumn> stream(Entity<?> entity) {
		List<ColumnOrSuperColumn> streamed = new ArrayList<ColumnOrSuperColumn>();
		
		long ts = System.currentTimeMillis();
		for (Entry<String, Object> e: entity.getNode().getMap().entrySet()) {
			if (entity.getNode().getDirty(e.getKey()) != null) {
				streamed.add(valueStreamer.write(e.getValue(), Serialization.serialize(e.getKey()), ts));
			}
		}
		
		return streamed;
	}

	@Override
	public Map<String, Object> parse(List<ColumnOrSuperColumn> cols) {
		Map<String, Object> parsed = new HashMap<String, Object>();
		
		for (ColumnOrSuperColumn csc: cols) {
			if (csc.column != null) {
				String name = Serialization.deserializeString(csc.column.name);
				parsed.put(name, this.valueStreamer.read(csc));
			} else {
				String name = Serialization.deserializeString(csc.super_column.name);
				parsed.put(name, this.valueStreamer.read(csc));
			}
		}
		
		return parsed;
	}
}
