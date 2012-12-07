package com.qorporation.qluster.conn.cassandra.typesafety;

import com.qorporation.qluster.conn.cassandra.typesafety.keytypes.ColumnIndexType;

public interface CassandraOpenColumn<K, C extends ColumnIndexType<K, ?>, V> extends CassandraBackedDefinition {

}
