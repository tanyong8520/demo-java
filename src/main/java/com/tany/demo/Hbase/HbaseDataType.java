package com.tany.demo.Hbase;

public enum HbaseDataType {
    /** 对应java类型java.lang.Integer 范围(-2147483648，2147483647)*/
    INTEGER("INTEGER"),
    /** 对应java类型java.lang.Double */
    DOUBLE("DOUBLE"),
    /** 对应java类型java.lang.String */
    VARCHAR("VARCHAR"),
    /** 对应java类型java.lang.BigDecimal 范围(整数15位，小数4位)*/
    DECIMAL("DECIMAL(20,6)"),
    /** 对应java类型java.sql.Date */
    DATE("DATE"),
    /** 对应java类型java.lang.Long 范围(-9233372036854775808，9223372036854775807)*/
    BIG_INT("BIGINT"),
    /** 对应java类型java.lang.Byte 范围(-128，127)*/
    TINY_INT("tinyint");

    private String value;

    @Override
    public String toString() {
        return value;
    }

    private HbaseDataType(String value) {
        this.setValue(value);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
