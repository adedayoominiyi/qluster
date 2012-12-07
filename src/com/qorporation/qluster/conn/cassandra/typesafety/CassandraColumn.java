package com.qorporation.qluster.conn.cassandra.typesafety;

import com.qorporation.qluster.conn.cassandra.typesafety.keytypes.ColumnIndexType;

public interface CassandraColumn<K, C extends ColumnIndexType<K, ?>> extends CassandraBackedDefinition {

}
