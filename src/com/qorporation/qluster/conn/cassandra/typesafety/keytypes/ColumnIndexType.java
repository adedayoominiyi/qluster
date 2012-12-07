package com.qorporation.qluster.conn.cassandra.typesafety.keytypes;

import com.qorporation.qluster.conn.cassandra.streamer.CassandraColumnIndexStreamer;

public interface ColumnIndexType<T, C extends CassandraColumnIndexStreamer<T>> {

}
