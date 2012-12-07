package com.qorporation.qluster.conn.cassandra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.SlicePredicate;

import com.qorporation.qluster.conn.Connection;
import com.qorporation.qluster.conn.ConnectionPool;
import com.qorporation.qluster.conn.cache.CacheConnection;
import com.qorporation.qluster.conn.cassandra.operation.CassandraDeleteOperation;
import com.qorporation.qluster.conn.cassandra.operation.CassandraGetOperation;
import com.qorporation.qluster.conn.cassandra.operation.CassandraInsertOperation;
import com.qorporation.qluster.conn.cassandra.operation.CassandraSaveOperation;
import com.qorporation.qluster.conn.cassandra.streamer.CassandraEntityStreamer;
import com.qorporation.qluster.conn.cassandra.streamer.CassandraOpenEntityStreamer;
import com.qorporation.qluster.conn.cassandra.typesafety.CassandraBackedDefinition;
import com.qorporation.qluster.conn.cassandra.typesafety.CassandraOpenColumn;
import com.qorporation.qluster.conn.cassandra.typesafety.CassandraOpenSuperColumn;

import com.qorporation.qluster.entity.Definition;
import com.qorporation.qluster.entity.Entity;
import com.qorporation.qluster.entity.Manager;
import com.qorporation.qluster.entity.Node;
import com.qorporation.qluster.entity.Operation;
import com.qorporation.qluster.entity.ProxyEntity;
import com.qorporation.qluster.entity.typesafety.FieldKey;
import com.qorporation.qluster.entity.typesafety.PrimaryKey;
import com.qorporation.qluster.service.entity.EntityService;
import com.qorporation.qluster.transaction.Transaction;
import com.qorporation.qluster.util.ErrorControl;

public class CassandraBackedEntityManager<T extends CassandraBackedDefinition> extends Manager<T> {

	protected ConnectionPool<CacheConnection> cachePool = null;
	protected ConnectionPool<CassandraConnection> cassandraPool = null;
	
	protected String columnFamily = null;
	protected SlicePredicate fullSlice = null;
	
	protected CassandraEntityStreamer<T> entityStreamer = null;

	@SuppressWarnings("unchecked")
	public void setup(EntityService entityService, Class<T> entityType, List<FieldKey<?>> keys, PrimaryKey<?, ?> primaryKey) {
		super.setup(entityService, entityType, keys, primaryKey);

		this.cachePool = this.entityService.getConnectionService().getPool(
				CacheConnection.class, ConnectionPool.class);
		this.cassandraPool = this.entityService.getConnectionService().getPool(
				CassandraConnection.class, ConnectionPool.class);

		this.columnFamily = entityType.getSimpleName();
		
		if (CassandraOpenColumn.class.isAssignableFrom(entityType) || CassandraOpenSuperColumn.class.isAssignableFrom(entityType)) {
			this.entityStreamer = new CassandraOpenEntityStreamer<T>(entityService, entityType, primaryKey);
		} else {
			this.entityStreamer = new CassandraEntityStreamer<T>(entityService, entityType, keys, primaryKey);
		}
		
		try {
			this.fullSlice = new SlicePredicate();
			this.fullSlice.column_names = new ArrayList<byte[]>();
			for (FieldKey<?> key: keys) {
				this.fullSlice.column_names.add(key.getName().getBytes("UTF-8"));
			}
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
	}
	
	public CassandraConnection acquireConnection() { return this.cassandraPool.acquire(); }
	public String getColumnFamily() { return this.columnFamily; }
	public SlicePredicate getFullSlice() { return this.fullSlice; }
	public CassandraEntityStreamer<T> getStreamer() { return this.entityStreamer; }
	
	@Override
	public Entity<T> wrap(String key, Node<T> node) {
		return new Entity<T>(this.entityService, this.entityType, key, node);
	}
	
	public Entity<T> wrapColumns(String key, List<ColumnOrSuperColumn> cols) {
		Map<String, Object> parsed = this.entityStreamer.parse(cols);
		return wrap(key, new Node<T>(this.def, this, parsed));
	}

	@Override
	public Entity<T> create(String key) {
		if (key == null) {
			key = this.entityStreamer.getPrimaryStreamer().generateKey();
		}
		
		return wrap(key, new Node<T>(this.def, this, new HashMap<String, Object>()));
	}
	
	@Override
	public Operation<Boolean, Map<String, Entity<T>>> get(List<String> keys, Transaction transaction) {
		CassandraGetOperation<T> op = new CassandraGetOperation<T>(this, transaction, keys);
		return op;
	}

	@Override
	public Operation<Boolean, Void> save(Entity<T> entity, Transaction transaction) {
		CassandraSaveOperation<T> op = new CassandraSaveOperation<T>(this, transaction, entity);
		return op;
	}

	@Override
	public Operation<Boolean, Void> delete(String key, Transaction transaction) {
		CassandraDeleteOperation<T> op = new CassandraDeleteOperation<T>(this, transaction, key);
		return op;
	}

	@Override
	public Entity<T> getProxy(String key) {
		return new ProxyEntity<T>(this.entityService, this.entityType, key);
	}

	@SuppressWarnings("unchecked")
	public <E extends Definition<? extends Connection>> void insert(String key, FieldKey<List<Entity<E>>> listing, Entity<E> addition) {
		this.insert(key, listing, Arrays.asList(addition));
	}
	
	public <E extends Definition<? extends Connection>> void insert(String key, FieldKey<List<Entity<E>>> listing, List<Entity<E>> additions) {
		this.insert(key, listing, additions, this.entityService.getGlobalTransaction()).execute();
	}
	
	public <E extends Definition<? extends Connection>> Operation<Boolean, Void> insert(String key, FieldKey<List<Entity<E>>> listing, List<Entity<E>> additions, Transaction transaction) {
		CassandraInsertOperation<T, E> op = new CassandraInsertOperation<T, E>(this, transaction, key, listing, additions);
		return op;	
	}

}
