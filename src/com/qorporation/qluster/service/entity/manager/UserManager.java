package com.qorporation.qluster.service.entity.manager;

/*
import com.qorporation.qluster.conn.cassandra.CassandraBackedEntityManager;
import com.qorporation.qluster.entity.definition.User;

public class UserManager extends CassandraBackedEntityManager<User> {
	
}
*/

import com.qorporation.qluster.conn.sql.SQLBackedEntityManager;
import com.qorporation.qluster.entity.definition.User;

public class UserManager extends SQLBackedEntityManager<User> {
	
}