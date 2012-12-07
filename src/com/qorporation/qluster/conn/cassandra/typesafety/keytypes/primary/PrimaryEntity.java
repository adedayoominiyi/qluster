package com.qorporation.qluster.conn.cassandra.typesafety.keytypes.primary;

import com.qorporation.qluster.conn.cassandra.streamer.component.EntityStreamer;
import com.qorporation.qluster.conn.cassandra.typesafety.keytypes.CassandraPrimaryKeyType;
import com.qorporation.qluster.entity.Definition;
import com.qorporation.qluster.entity.Entity;

public class PrimaryEntity<T extends Definition<?>> implements CassandraPrimaryKeyType<Entity<T>, EntityStreamer<T>> {

}
