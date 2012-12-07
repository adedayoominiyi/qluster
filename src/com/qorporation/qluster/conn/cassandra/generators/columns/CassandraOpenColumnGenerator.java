package com.qorporation.qluster.conn.cassandra.generators.columns;

import java.lang.reflect.ParameterizedType;

import org.w3c.dom.Element;

import com.qorporation.qluster.conn.cassandra.generators.CassandraSchemaColumnGenerator;
import com.qorporation.qluster.conn.cassandra.typesafety.CassandraOpenColumn;
import com.qorporation.qluster.util.Reflection;

public class CassandraOpenColumnGenerator extends CassandraSchemaColumnGenerator<CassandraOpenColumn<?, ?, ?>> {

	@Override
	public void generate(ParameterizedType def, Element column) {
		Class<?> type = Reflection.getParamType(def, 0);
		ParameterizedType typeParam = Reflection.getParamSubType(def, 0);
		String compareType = this.getStreamer(type, typeParam).compareWithType();
		
		column.setAttribute("CompareWith", compareType);
	}

}
