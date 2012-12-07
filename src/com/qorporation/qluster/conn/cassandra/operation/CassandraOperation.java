package com.qorporation.qluster.conn.cassandra.operation;

import com.qorporation.qluster.conn.cassandra.CassandraBackedEntityManager;
import com.qorporation.qluster.conn.cassandra.typesafety.CassandraBackedDefinition;
import com.qorporation.qluster.entity.Operation;
import com.qorporation.qluster.transaction.Transaction;

public abstract class CassandraOperation<T extends CassandraBackedDefinition, R, V> extends Operation<R, V> {

	protected CassandraBackedEntityManager<T> manager = null;
	
	public CassandraOperation(CassandraBackedEntityManager<T> manager,
			Transaction transaction) {
		super(transaction);
		this.manager = manager;
	}

}
