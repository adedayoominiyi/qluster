package com.qorporation.qluster.conn.cassandra.typesafety.keytypes.primary;

import com.qorporation.qluster.conn.cassandra.streamer.component.StringStreamer;
import com.qorporation.qluster.conn.cassandra.typesafety.keytypes.CassandraPrimaryKeyType;

public class PrimaryString implements CassandraPrimaryKeyType<String, StringStreamer> {

}
