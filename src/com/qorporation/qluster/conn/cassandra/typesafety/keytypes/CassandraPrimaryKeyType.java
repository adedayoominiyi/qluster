package com.qorporation.qluster.conn.cassandra.typesafety.keytypes;

import com.qorporation.qluster.conn.cassandra.streamer.CassandraPrimaryKeyStreamer;
import com.qorporation.qluster.entity.typesafety.PrimaryKeyType;

public interface CassandraPrimaryKeyType<T, P extends CassandraPrimaryKeyStreamer<T>> extends PrimaryKeyType<T> {

}
