package com.qorporation.qluster.conn.cassandra.streamer;

import java.lang.reflect.ParameterizedType;

import com.qorporation.qluster.entity.ComponentStreamer;
import com.qorporation.qluster.service.entity.EntityService;

import org.apache.cassandra.thrift.ColumnOrSuperColumn;

public abstract class CassandraComponentStreamer<C> extends ComponentStreamer<C, ColumnOrSuperColumn, ColumnOrSuperColumn> {

	protected EntityService service = null;
	protected Class<C> type = null;
	protected ParameterizedType subType = null;
	
	public CassandraComponentStreamer(EntityService service, Class<C> type, ParameterizedType subType) {
		this.service = service;
		this.type = type;
		this.subType = subType;
	}
	
}
