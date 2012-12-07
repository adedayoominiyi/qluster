package com.qorporation.qluster.conn.cassandra.operation;

import org.apache.cassandra.thrift.ConsistencyLevel;

import com.qorporation.qluster.common.Triple;
import com.qorporation.qluster.conn.cassandra.CassandraBackedEntityManager;
import com.qorporation.qluster.conn.cassandra.CassandraConnection;
import com.qorporation.qluster.conn.cassandra.typesafety.CassandraBackedDefinition;
import com.qorporation.qluster.entity.Entity;
import com.qorporation.qluster.transaction.Transaction;

public class CassandraSaveOperation<T extends CassandraBackedDefinition> extends CassandraOperation<T, Boolean, Void> {

	private Entity<T> entity = null;
	
	public CassandraSaveOperation(CassandraBackedEntityManager<T> manager, Transaction transaction, Entity<T> entity) {
		super(manager, transaction);
		this.entity = entity;
	}

	@Override
	protected Triple<Boolean, Boolean, Void> op() {
		CassandraConnection conn = this.manager.acquireConnection();
		
		boolean success = false;

		try {
			String key = this.entity.getKey();
			conn.insert(key, this.manager.getColumnFamily(), 
							this.manager.getStreamer().stream(entity), 
							ConsistencyLevel.QUORUM);
			
			this.entity.mark();
			
			success = true;
		} finally {
			conn.release();
		}
		
		return new Triple<Boolean, Boolean, Void>(success, success, null);
	}
	
}
