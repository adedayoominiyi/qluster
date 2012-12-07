package com.qorporation.qluster.conn.cassandra.operation;

import java.util.ArrayList;
import java.util.List;

import org.apache.cassandra.thrift.Column;
import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.cassandra.thrift.SuperColumn;

import com.qorporation.qluster.common.TimeUUID;
import com.qorporation.qluster.common.Triple;
import com.qorporation.qluster.conn.Connection;
import com.qorporation.qluster.conn.cassandra.CassandraBackedOpenEntityManager;
import com.qorporation.qluster.conn.cassandra.CassandraConnection;
import com.qorporation.qluster.conn.cassandra.typesafety.CassandraBackedDefinition;
import com.qorporation.qluster.entity.Definition;
import com.qorporation.qluster.transaction.Transaction;
import com.qorporation.qluster.util.Serialization;

public class CassandraInsertOpenOperation<T extends CassandraBackedDefinition, E extends Definition<? extends Connection>> extends CassandraOpenOperation<T, Boolean, Void> {
	
	private String key = null;
	private String superColumn = null;
	private List<byte[]> additions = null;


	public CassandraInsertOpenOperation(CassandraBackedOpenEntityManager<T> manager, Transaction transaction, String key, String superColumn, List<byte[]> additions) {
		super(manager, transaction);
		this.key = key;
		this.superColumn = superColumn;
		this.additions = additions;
	}
	
	@Override
	protected Triple<Boolean, Boolean, Void> op() {
		CassandraConnection conn = this.manager.acquireConnection();
		
		boolean success = false;

		try {
			List<ColumnOrSuperColumn> cols = new ArrayList<ColumnOrSuperColumn>(this.additions.size());
			
			Long ms = System.currentTimeMillis();
			for (byte[] addition: this.additions) {
				ColumnOrSuperColumn csc = new ColumnOrSuperColumn();
				csc.super_column = new SuperColumn();
				csc.super_column.name = Serialization.serialize(this.superColumn);
				csc.super_column.addToColumns(new Column(new TimeUUID().getBytes(), addition, ms));
				cols.add(csc);
			}
			
			conn.insert(key, this.openManager.getColumnFamily(), cols, ConsistencyLevel.QUORUM);
			
			success = true;
		} finally {
			conn.release();
		}
		
		return new Triple<Boolean, Boolean, Void>(success, success, null);
	}

}
