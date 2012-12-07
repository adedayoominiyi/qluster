package com.qorporation.qluster.entity.definition.authentication;

import com.qorporation.qluster.common.TimeUUID;
import com.qorporation.qluster.conn.cassandra.typesafety.CassandraColumn;
import com.qorporation.qluster.conn.cassandra.typesafety.keytypes.column.StringIndex;
import com.qorporation.qluster.conn.cassandra.typesafety.keytypes.primary.PrimaryTimeUUID;
import com.qorporation.qluster.entity.Entity;
import com.qorporation.qluster.entity.definition.User;
import com.qorporation.qluster.entity.typesafety.FieldKey;
import com.qorporation.qluster.entity.typesafety.PrimaryKey;

public class UserToken implements CassandraColumn<String, StringIndex> {
	
	public static PrimaryKey<TimeUUID, PrimaryTimeUUID> token;
	
	public static FieldKey<String> key;
	public static FieldKey<Entity<User>> user;

}
