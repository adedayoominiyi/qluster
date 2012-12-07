package com.qorporation.qluster.conn.cassandra.generators.columns;

import java.lang.reflect.ParameterizedType;

import org.w3c.dom.Element;

import com.qorporation.qluster.conn.cassandra.generators.CassandraSchemaColumnGenerator;
import com.qorporation.qluster.conn.cassandra.typesafety.CassandraSuperColumn;
import com.qorporation.qluster.util.Reflection;

public class CassandraSuperColumnGenerator extends CassandraSchemaColumnGenerator<CassandraSuperColumn<?, ?, ?, ?>> {

	@Override
	public void generate(ParameterizedType def, Element column) {
		Class<?> type = Reflection.getParamType(def, 0);
		ParameterizedType typeParam = Reflection.getParamSubType(def, 0);
		String compareType = this.getStreamer(type, typeParam).compareWithType();
		
		Class<?> subType = Reflection.getParamType(def, 2);
		ParameterizedType subTypeParam = Reflection.getParamSubType(def, 2);
		String subCompareType = this.getStreamer(subType, subTypeParam).compareWithType();
		
		column.setAttribute("CompareWith", compareType);
		column.setAttribute("CompareSubcolumnsWith", subCompareType);
		column.setAttribute("ColumnType", "Super");
	}

}
