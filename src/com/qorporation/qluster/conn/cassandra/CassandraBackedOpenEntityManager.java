package com.qorporation.qluster.conn.cassandra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.thrift.ColumnOrSuperColumn;
import org.apache.cassandra.thrift.SlicePredicate;
import org.apache.cassandra.thrift.SliceRange;

import com.qorporation.qluster.conn.Connection;
import com.qorporation.qluster.conn.cassandra.operation.CassandraGetOpenOperation;
import com.qorporation.qluster.conn.cassandra.operation.CassandraInsertOpenOperation;
import com.qorporation.qluster.conn.cassandra.operation.CassandraMapOperation;
import com.qorporation.qluster.conn.cassandra.typesafety.CassandraBackedDefinition;
import com.qorporation.qluster.entity.Definition;
import com.qorporation.qluster.entity.Node;
import com.qorporation.qluster.entity.OpenEntity;
import com.qorporation.qluster.entity.OpenProxyEntity;
import com.qorporation.qluster.entity.Operation;
import com.qorporation.qluster.entity.typesafety.FieldKey;
import com.qorporation.qluster.entity.typesafety.PrimaryKey;
import com.qorporation.qluster.service.entity.EntityService;
import com.qorporation.qluster.transaction.Transaction;
import com.qorporation.qluster.util.ErrorControl;

public class CassandraBackedOpenEntityManager<T extends CassandraBackedDefinition> extends CassandraBackedEntityManager<T> {

	public void setup(EntityService entityService, Class<T> entityType, List<FieldKey<?>> keys, PrimaryKey<?, ?> primaryKey) {
		super.setup(entityService, entityType, keys, primaryKey);
		
		try {
			this.fullSlice = new SlicePredicate();
			this.fullSlice.slice_range = new SliceRange();
			this.fullSlice.slice_range.start = new byte[]{};
			this.fullSlice.slice_range.finish = new byte[]{};
		} catch (Exception e) {
			ErrorControl.logException(e);
		}
	}
	
	public OpenEntity<T> wrapOpen(String key, Node<T> node) {
		return new OpenEntity<T>(this.entityService, this.entityType, key, node);
	}
	
	public OpenEntity<T> wrapOpenColumns(String key, List<ColumnOrSuperColumn> cols) {
		Map<String, Object> parsed = this.entityStreamer.parse(cols);
		return wrapOpen(key, new Node<T>(this.def, this, parsed));
	}
	
	public OpenEntity<T> getOpen(String key) { return this.getOpen(Arrays.asList(key)).get(key); }
	public Map<String, OpenEntity<T>> getOpen(List<String> keys) { return this.getOpen(keys, this.entityService.getGlobalTransaction()).getValue(); }
	public Operation<Boolean, Map<String, OpenEntity<T>>> getOpen(List<String> keys, Transaction transaction) {
		CassandraGetOpenOperation<T> op = new CassandraGetOpenOperation<T>(this, transaction, keys);
		return op;
	}
	
	public OpenEntity<T> getOpenProxy(String key) {
		return new OpenProxyEntity<T>(this.entityService, this.entityType, key);
	}

	public void materializeOpen(List<OpenEntity<T>> proxies) {
		List<String> keys = new ArrayList<String>(proxies.size());
		for (OpenEntity<T> e: proxies) keys.add(e.getKey());
		
		Map<String, OpenEntity<T>> materialized = this.getOpen(keys);
		for (int i = 0; i < proxies.size(); i++) {
			proxies.set(i, materialized.get(proxies.get(i)));
		}
	}
	
	@SuppressWarnings("serial")
	public <E extends Definition<? extends Connection>> void map(String key, String superColumn, final String col, final byte[] addition) {
		this.map(key, superColumn, new HashMap<String, byte[]>() {{
			put(col, addition);
		}});
	}
	
	public <E extends Definition<? extends Connection>> void map(String key, String superColumn, Map<String, byte[]> additions) {
		this.map(key, superColumn, additions, this.entityService.getGlobalTransaction()).execute();
	}
	
	public <E extends Definition<? extends Connection>> Operation<Boolean, Void> map(String key, String superColumn, Map<String, byte[]> additions, Transaction transaction) {
		CassandraMapOperation<T> op = new CassandraMapOperation<T>(this, transaction, key, superColumn, additions);
		return op;
	}
	
	public <E extends Definition<? extends Connection>> void insert(String key, String superColumn, byte[] addition) {
		this.insert(key, superColumn, Arrays.asList(addition));
	}
	
	public <E extends Definition<? extends Connection>> void insert(String key, String superColumn, List<byte[]> additions) {
		this.insert(key, superColumn, additions, this.entityService.getGlobalTransaction()).execute();		
	}
	
	public <E extends Definition<? extends Connection>> Operation<Boolean, Void> insert(String key, String superColumn, List<byte[]> additions, Transaction transaction) {
		CassandraInsertOpenOperation<T, E> op = new CassandraInsertOpenOperation<T, E>(this, transaction, key, superColumn, additions);
		return op;
	}
	
}
