package com.qorporation.qluster.conn.cassandra.typesafety.keytypes.primary;

import com.qorporation.qluster.common.TimeUUID;
import com.qorporation.qluster.conn.cassandra.streamer.component.TimeUUIDStreamer;
import com.qorporation.qluster.conn.cassandra.typesafety.keytypes.CassandraPrimaryKeyType;

public class PrimaryTimeUUID implements CassandraPrimaryKeyType<TimeUUID, TimeUUIDStreamer> {

}
