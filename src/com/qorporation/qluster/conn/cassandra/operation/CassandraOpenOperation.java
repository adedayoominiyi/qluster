package com.qorporation.qluster.conn.cassandra.operation;

import com.qorporation.qluster.conn.cassandra.CassandraBackedOpenEntityManager;
import com.qorporation.qluster.conn.cassandra.typesafety.CassandraBackedDefinition;
import com.qorporation.qluster.transaction.Transaction;

public abstract class CassandraOpenOperation<T extends CassandraBackedDefinition, R, V> extends CassandraOperation<T, R, V> {
	
	protected CassandraBackedOpenEntityManager<T> openManager = null;

	public CassandraOpenOperation(CassandraBackedOpenEntityManager<T> manager,
			Transaction transaction) {
		super(manager, transaction);
		this.openManager = manager;
	}

}
