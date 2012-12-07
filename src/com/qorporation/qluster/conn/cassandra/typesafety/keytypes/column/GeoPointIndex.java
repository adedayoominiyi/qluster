package com.qorporation.qluster.conn.cassandra.typesafety.keytypes.column;

import com.qorporation.qluster.common.GeoPoint;
import com.qorporation.qluster.conn.cassandra.streamer.component.GeoPointStreamer;
import com.qorporation.qluster.conn.cassandra.typesafety.keytypes.ColumnIndexType;

public class GeoPointIndex implements ColumnIndexType<GeoPoint, GeoPointStreamer> {

}
