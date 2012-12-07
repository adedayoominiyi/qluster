package com.qorporation.qluster.conn.cassandra.typesafety;

import com.qorporation.qluster.conn.cassandra.typesafety.keytypes.ColumnIndexType;

public interface CassandraOpenSuperColumn<S, A extends ColumnIndexType<S, ?>, K, B extends ColumnIndexType<K, ?>, V> extends CassandraBackedDefinition {

}