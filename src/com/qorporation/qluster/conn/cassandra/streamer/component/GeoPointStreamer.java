package com.qorporation.qluster.conn.cassandra.streamer.component;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.SuperColumn;

import com.qorporation.qluster.common.GeoPoint;
import com.qorporation.qluster.conn.cassandra.streamer.CassandraColumnIndexStreamer;
import com.qorporation.qluster.conn.cassandra.streamer.CassandraComponentStreamer;
import com.qorporation.qluster.service.entity.EntityService;
import com.qorporation.qluster.util.Serialization;

public class GeoPointStreamer extends CassandraComponentStreamer<GeoPoint> implements CassandraColumnIndexStreamer<GeoPoint> {

	public GeoPointStreamer(EntityService service, Class<GeoPoint> type,
			ParameterizedType subType) {
		super(service, type, subType);
	}

	@Override
	public GeoPoint read(ColumnOrSuperColumn data) {
		Double lat = Serialization.deserializeDouble(data.super_column.columns.get(0).value);
		Double lon = Serialization.deserializeDouble(data.super_column.columns.get(1).value);
		
		return new GeoPoint(lat, lon);
	}

	@Override
	public ColumnOrSuperColumn write(Object comp, byte[] name, long ts) {
		ColumnOrSuperColumn c = new ColumnOrSuperColumn();
		
		List<Column> cols = new ArrayList<Column>(2);
		cols.add(new Column(Serialization.serialize("lat"), Serialization.serialize(((GeoPoint) comp).getLat()), ts));
		cols.add(new Column(Serialization.serialize("lon"), Serialization.serialize(((GeoPoint) comp).getLng()), ts));
		
		c.super_column = new SuperColumn(name, cols);
		return c;
	}
	
	@Override
	public String compareWithType() {
		return "BytesType";
	}

}
