package com.qorporation.qluster.conn.cassandra.streamer.component;

import java.lang.reflect.ParameterizedType;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;

import com.qorporation.qluster.common.TimeUUID;
import com.qorporation.qluster.conn.cassandra.streamer.CassandraColumnIndexStreamer;
import com.qorporation.qluster.conn.cassandra.streamer.CassandraComponentStreamer;
import com.qorporation.qluster.conn.cassandra.streamer.CassandraPrimaryKeyStreamer;
import com.qorporation.qluster.service.entity.EntityService;

public class TimeUUIDStreamer extends CassandraComponentStreamer<TimeUUID> implements CassandraPrimaryKeyStreamer<TimeUUID>, CassandraColumnIndexStreamer<TimeUUID> {

	public TimeUUIDStreamer(EntityService service, Class<TimeUUID> type,
			ParameterizedType subType) {
		super(service, type, subType);
	}

	@Override
	public TimeUUID read(ColumnOrSuperColumn data) {
		return TimeUUID.fromBytes(data.column.value);
	}

	@Override
	public ColumnOrSuperColumn write(Object comp, byte[] name, long ts) {
		ColumnOrSuperColumn c = new ColumnOrSuperColumn();
		c.column = new Column(name, ((TimeUUID) comp).getBytes(), ts);
		return c;
	}
	
	@Override
	public String compareWithType() {
		return "TimeUUIDType";
	}

	@Override
	public String generateKey() {
		return (new TimeUUID()).toString();
	}

}
