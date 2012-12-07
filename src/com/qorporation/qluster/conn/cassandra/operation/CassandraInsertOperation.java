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
import com.qorporation.qluster.conn.cassandra.CassandraBackedEntityManager;
import com.qorporation.qluster.conn.cassandra.CassandraConnection;
import com.qorporation.qluster.conn.cassandra.typesafety.CassandraBackedDefinition;
import com.qorporation.qluster.entity.Definition;
import com.qorporation.qluster.entity.Entity;
import com.qorporation.qluster.entity.typesafety.FieldKey;
import com.qorporation.qluster.transaction.Transaction;
import com.qorporation.qluster.util.Serialization;

public class CassandraInsertOperation<T extends CassandraBackedDefinition, E extends Definition<? extends Connection>> extends CassandraOperation<T, Boolean, Void> {
	
	private String key = null;
	private FieldKey<List<Entity<E>>> listing = null;
	private List<Entity<E>> additions = null;

	public CassandraInsertOperation(CassandraBackedEntityManager<T> manager, Transaction transaction, String key, FieldKey<List<Entity<E>>> listing, List<Entity<E>> additions) {
		super(manager, transaction);
		this.key = key;
		this.listing = listing;
		this.additions = additions;
	}
	
	@Override
	protected Triple<Boolean, Boolean, Void> op() {
		CassandraConnection conn = this.manager.acquireConnection();
		
		boolean success = false;

		try {
			List<ColumnOrSuperColumn> cols = new ArrayList<ColumnOrSuperColumn>(this.additions.size());
			
			Long ms = System.currentTimeMillis();
			for (Entity<E> addition: this.additions) {
				ColumnOrSuperColumn csc = new ColumnOrSuperColumn();
				csc.super_column = new SuperColumn();
				csc.super_column.name = Serialization.serialize(this.listing.getName());
				csc.super_column.addToColumns(new Column(new TimeUUID().getBytes(), Serialization.serialize(addition.getKey()), ms));
				cols.add(csc);
			}
			
			conn.insert(this.key, this.manager.getColumnFamily(), cols, ConsistencyLevel.QUORUM);
			
			success = true;
		} finally {
			conn.release();
		}
		
		return new Triple<Boolean, Boolean, Void>(success, success, null);
	}

}
