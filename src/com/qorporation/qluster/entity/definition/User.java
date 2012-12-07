package com.qorporation.qluster.entity.definition;

import com.qorporation.qluster.conn.sql.typesafety.SQLTable;
import com.qorporation.qluster.conn.sql.typesafety.keytypes.annotation.Indexed;
import com.qorporation.qluster.conn.sql.typesafety.keytypes.annotation.Unique;
import com.qorporation.qluster.entity.typesafety.FieldKey;

public class User extends SQLTable {
	
	@Indexed
	@Unique
	public static FieldKey<String> name;
	
	public static FieldKey<String> password;
	public static FieldKey<String> email;
	public static FieldKey<Boolean> isAdmin;

}
