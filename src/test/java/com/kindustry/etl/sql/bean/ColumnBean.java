package com.kindustry.etl.sql.bean;

/**
 * 字段
 * */
public class ColumnBean {

	enum ColumnType{
		String{
			@Override
			public boolean isMatch(String type) {
				return type.contains("varchar");
			}
			@Override
			public Class getDataType() {
				return String.class;
			}
		},
		Double{
			@Override
			public boolean isMatch(String type) {
				return type.contains("decimal");
			}
			@Override
			public Class getDataType() {
				return Double.class;
			}
		},
		Integer{
			@Override
			public boolean isMatch(String type) {
				return type.contains("int");
			}
			@Override
			public Class getDataType() {
				return Integer.class;
			}
		},
		Date{
			@Override
			public boolean isMatch(String type) {
				return type.contains("date") || type.contains("time");
			}
			@Override
			public Class getDataType() {
				return java.util.Date.class;
			}
			
			@Override
			public String getValue(Object value) {
				return super.getValue(value);
			}
		},
		Boolean{
			@Override
			public boolean isMatch(String type) {
				return type.contains("boolean");
			}
			@Override
			public Class getDataType() {
				return Boolean.class;
			}
		};
		
		public abstract boolean isMatch(String type);
		
		public abstract Class getDataType();
		
		public String getValue(Object value){
			return value.toString();
		}
		
		
		public static ColumnType getType(String type){
			for (ColumnType ctype : values()){
				if (ctype.isMatch(type)){
					return ctype;
				}
			}
			return String;
		}
	}
	
	/**类型*/
	private Class type;
	
	private ColumnType dataType;
	
	private String name;
	
	public ColumnBean(String columnSql) {
		String[] array = columnSql.split("\\s+");
		name = array[0];
		dataType = ColumnType.getType(array[1]);
	}

	public String getName() {
		return name;
	}

	public ColumnType getDataType() {
		return dataType;
	}
	
	public Class getType() {
		return type;
	}
	
	/**得到数据*/
	public String getData (Object value){
		return dataType.getValue(value);
	}
}
