package com.qorporation.qluster.service.entity.backend;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qorporation.qluster.config.Config;
import com.qorporation.qluster.conn.cassandra.CassandraBackedOpenEntityManager;
import com.qorporation.qluster.conn.cassandra.CassandraConnection;
import com.qorporation.qluster.conn.cassandra.CassandraBackedEntityManager;
import com.qorporation.qluster.conn.cassandra.generators.CassandraSchemaGenerator;
import com.qorporation.qluster.conn.cassandra.streamer.CassandraComponentStreamer;
import com.qorporation.qluster.conn.cassandra.typesafety.CassandraBackedDefinition;
import com.qorporation.qluster.entity.ComponentStreamer;
import com.qorporation.qluster.entity.Definition;
import com.qorporation.qluster.entity.Manager;
import com.qorporation.qluster.entity.typesafety.FieldKey;
import com.qorporation.qluster.entity.typesafety.PrimaryKey;
import com.qorporation.qluster.service.entity.EntityService;
import com.qorporation.qluster.util.ClassWalker;
import com.qorporation.qluster.util.ClassWalkerFilter;
import com.qorporation.qluster.util.ErrorControl;
import com.qorporation.qluster.util.Reflection;
import com.qorporation.qluster.util.RelativePath;

public class CassandraBackend extends EntityBackend<CassandraConnection> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private EntityService entityService;
    
	private Map<Class<? extends CassandraBackedDefinition>, Manager<? extends CassandraBackedDefinition>> managers = null;
	private Map<Class<?>, Class<? extends CassandraComponentStreamer<?>>> componentStreamers = null;
	
	public CassandraBackend(EntityService entityService) {
		this.logger.info("Loading cassandra entity backend");
		
		this.entityService = entityService;
	}
	
	@SuppressWarnings("unchecked")
	private Map<Class<? extends CassandraBackedDefinition>, Class<? extends CassandraBackedEntityManager<?>>> findManagers() {
		this.logger.info("Finding entity managers");
		Map<Class<? extends CassandraBackedDefinition>, Class<? extends CassandraBackedEntityManager<?>>> managers = new HashMap<Class<? extends CassandraBackedDefinition>, Class<? extends CassandraBackedEntityManager<?>>>();
		
		Iterator<Class<?>> itr = new ClassWalker(ClassWalkerFilter.extending(CassandraBackedEntityManager.class),
												ClassWalkerFilter.excluding(CassandraBackedOpenEntityManager.class));
		
		while (itr.hasNext()) {
			Class<? extends CassandraBackedEntityManager<?>> cls = (Class<? extends CassandraBackedEntityManager<?>>) itr.next();
			
			this.logger.info(String.format("Found entity manager: %s", cls.getName()));
			
		    Class<?> paramType = Reflection.getParamType(cls, 0);
	    	Class<? extends CassandraBackedDefinition> definition = (Class<? extends CassandraBackedDefinition>) paramType;
	    	managers.put(definition, cls);
		}
		
		return managers;
	}

	@SuppressWarnings({ "unchecked" })
	private void setupEntities() {
		this.managers = new HashMap<Class<? extends CassandraBackedDefinition>, Manager<? extends CassandraBackedDefinition>>();
		
		Map<Class<? extends CassandraBackedDefinition>, Class<? extends CassandraBackedEntityManager<?>>> managers = findManagers();
		Map<Class<? extends CassandraBackedDefinition>, List<FieldKey<?>>> keysByClass = new HashMap<Class<? extends CassandraBackedDefinition>, List<FieldKey<?>>>();
		Map<Class<? extends CassandraBackedDefinition>, PrimaryKey<?, ?>> primaryKeys = new HashMap<Class<? extends CassandraBackedDefinition>, PrimaryKey<?, ?>>();
		
		this.logger.info("Setting up entities");
		Iterator<Class<?>> itr = new ClassWalker(ClassWalkerFilter.isConcreteClass(),
												ClassWalkerFilter.extending(CassandraBackedDefinition.class));
		
		while (itr.hasNext()) {
			Class<? extends CassandraBackedDefinition> cls = (Class<? extends CassandraBackedDefinition>) itr.next();
			
			this.logger.info(String.format("Setting up entity: %s", cls.getName()));
			
			List<FieldKey<?>> keys = new ArrayList<FieldKey<?>>();
			PrimaryKey<?, ?> primaryKey = null;
			
			for (Field field: cls.getDeclaredFields()) {
				try {
					if (field.getType().equals(FieldKey.class)) {
					    Class<?> paramType = Reflection.getParamType(field, 0);
					    ParameterizedType paramSubType = Reflection.getParamSubType(field, 0);
					    FieldKey<?> key = new FieldKey(cls, paramType, paramSubType, field.getName(), field.getAnnotations());
						field.set(null, key);
						keys.add(key);
					} else if (field.getType().equals(PrimaryKey.class)) {
					    Class<?> paramType = Reflection.getParamType(field, 0);
					    ParameterizedType paramSubType = Reflection.getParamSubType(field, 0);
					    primaryKey = new PrimaryKey(cls, paramType, paramSubType, field.getName());
						field.set(null, primaryKey);						
					}
				} catch (Exception e) {
					ErrorControl.logException(e);
				}
			}
			
			keysByClass.put(cls, keys);
			primaryKeys.put(cls, primaryKey);
			
			CassandraBackedEntityManager em = null;
			Class<? extends CassandraBackedEntityManager<?>> manager = managers.get(cls);
			if (manager != null) {
				try {
					em = manager.newInstance();
					this.logger.info(String.format("Using manager: %s for: %s", manager.getName(), cls.getName()));
				} catch (Exception e) {
					ErrorControl.logException(e);
					em = null;
				}
			}
			
			if (em == null) {
				em = new CassandraBackedEntityManager();
				this.logger.info(String.format("Using generic manager for: %s", cls.getName()));
			}
			
			this.managers.put(cls, em);
		}
		
		for (Entry<Class<? extends CassandraBackedDefinition>, Manager<? extends CassandraBackedDefinition>> entry: this.managers.entrySet()) {
			Class<? extends Definition> cls = entry.getKey();
			Manager em = entry.getValue();
			List<FieldKey<?>> keys = keysByClass.get(cls);
			PrimaryKey<?, ?> primaryKey = primaryKeys.get(cls);
			em.setup(this.entityService, cls, keys, primaryKey);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void setupStreamers() {
		this.componentStreamers = new HashMap<Class<?>, Class<? extends CassandraComponentStreamer<?>>>();
		
		Iterator<Class<?>> itr = new ClassWalker(ClassWalkerFilter.extending(CassandraComponentStreamer.class));
		
		while (itr.hasNext()) {
			Class<? extends CassandraComponentStreamer<?>> cls = (Class<? extends CassandraComponentStreamer<?>>) itr.next();
		    Class<?> paramType = Reflection.getParamType(cls, 0);
		    
		    this.logger.info(String.format("Found component streamer: %s", cls.getName()));
		    this.componentStreamers.put(paramType, cls);
		}
	}
	
	private void generateSchema(Config config) {
		File schemaFile = new File(RelativePath.root().getAbsolutePath()
								.concat(File.separator)
								.concat("storage-conf.xml"));
		
		CassandraSchemaGenerator generator = new CassandraSchemaGenerator(this.entityService, 
													this.managers.keySet(), 
													this.componentStreamers);

		generator.generate(schemaFile, config);
	}

	@Override
	public Map<Class<?>, Class<? extends ComponentStreamer<?, ?, ?>>> loadStreamers(Config config) {
		setupStreamers();
		
		Map<Class<?>, Class<? extends ComponentStreamer<?, ?, ?>>> ret = new HashMap<Class<?>, Class<? extends ComponentStreamer<?, ?, ?>>>();
		for (Entry<Class<?>, Class<? extends CassandraComponentStreamer<?>>> e: this.componentStreamers.entrySet()) {
			ret.put(e.getKey(), e.getValue());
		}
		return ret;
	}
	
	@Override
	public Map<Class<? extends Definition<?>>, Manager<? extends Definition<?>>> loadManagers(Config config) {
		setupEntities();
		
		Map<Class<? extends Definition<?>>, Manager<? extends Definition<?>>> ret = new HashMap<Class<? extends Definition<?>>, Manager<? extends Definition<?>>>();
		for (Entry<Class<? extends CassandraBackedDefinition>, Manager<? extends CassandraBackedDefinition>> e: this.managers.entrySet()) {
			ret.put(e.getKey(), e.getValue());
		}
		return ret;
	}
	
	@Override
	public void finalizeInitialization(Config config) {
		generateSchema(config);
	}

}
