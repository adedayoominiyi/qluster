package com.qorporation.qluster.conn.cassandra.operation;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ConsistencyLevel;

import com.qorporation.qluster.common.Triple;
import com.qorporation.qluster.conn.cassandra.CassandraBackedEntityManager;
import com.qorporation.qluster.conn.cassandra.CassandraConnection;
import com.qorporation.qluster.conn.cassandra.typesafety.CassandraBackedDefinition;
import com.qorporation.qluster.entity.Entity;
import com.qorporation.qluster.entity.Manager;
import com.qorporation.qluster.entity.operation.interfaces.PrefetchableOperation;
import com.qorporation.qluster.transaction.Transaction;

public class CassandraGetOperation<T extends CassandraBackedDefinition> extends CassandraOperation<T, Boolean, Map<String, Entity<T>>> implements PrefetchableOperation<T> {

	private List<String> keys = null;
	
	public CassandraGetOperation(CassandraBackedEntityManager<T> manager, Transaction transaction, List<String> keys) {
		super(manager, transaction);
		this.keys = keys;
	}
	
	@Override
	protected Triple<Boolean, Boolean, Map<String, Entity<T>>> op() {
		CassandraConnection conn = this.manager.acquireConnection();
		
		Map<String, Entity<T>> ret = new HashMap<String, Entity<T>>();
		
		try {
			Map<String, List<ColumnOrSuperColumn>> data = 
				conn.get(keys, this.manager.getColumnFamily(), 
											null, 
											this.manager.getFullSlice(), 
											ConsistencyLevel.ONE);

			for (String key: keys) {				
				List<ColumnOrSuperColumn> cols = data.get(key);
				if (cols.size() > 0) {
					Entity<T> entity = this.manager.wrapColumns(key, cols);
					ret.put(key, entity);
				}
			}
		} finally {
			conn.release();
		}
		
		return new Triple<Boolean, Boolean, Map<String, Entity<T>>>(true, true, ret);
	}

	@Override
	public Collection<? extends Entity<T>> getEntities() {
		return this.getValue().values();
	}

	@Override
	public Manager<T> getManager() {
		return this.manager;
	}

}
