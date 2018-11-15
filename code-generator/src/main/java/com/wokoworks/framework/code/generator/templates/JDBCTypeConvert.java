package com.wokoworks.framework.code.generator.templates;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.JDBCType;
import java.sql.Ref;
import java.sql.Struct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JDBCTypeConvert {

    private static final Map<JDBCType, Type> TYPE_MAP = new HashMap<>();

    static {
        TYPE_MAP.put(JDBCType.BIT, boolean.class);
        TYPE_MAP.put(JDBCType.TINYINT, short.class);
        TYPE_MAP.put(JDBCType.SMALLINT, short.class);
        TYPE_MAP.put(JDBCType.INTEGER, int.class);
        TYPE_MAP.put(JDBCType.BIGINT, long.class);
        TYPE_MAP.put(JDBCType.FLOAT, float.class);
        TYPE_MAP.put(JDBCType.REAL, float.class);
        TYPE_MAP.put(JDBCType.DOUBLE, double.class);
        TYPE_MAP.put(JDBCType.NUMERIC, BigDecimal.class);
        TYPE_MAP.put(JDBCType.DECIMAL, BigDecimal.class);
        TYPE_MAP.put(JDBCType.CHAR, char.class);
        TYPE_MAP.put(JDBCType.VARCHAR, String.class);
        TYPE_MAP.put(JDBCType.LONGVARCHAR, String.class);
        TYPE_MAP.put(JDBCType.DATE, Date.class);
        TYPE_MAP.put(JDBCType.TIME, Date.class);
        TYPE_MAP.put(JDBCType.TIMESTAMP, Date.class);
        TYPE_MAP.put(JDBCType.BINARY, byte[].class);
        TYPE_MAP.put(JDBCType.VARBINARY, byte[].class);
        TYPE_MAP.put(JDBCType.LONGVARBINARY, byte[].class);
        TYPE_MAP.put(JDBCType.NULL, Object.class);
        TYPE_MAP.put(JDBCType.OTHER, Object.class);
        TYPE_MAP.put(JDBCType.JAVA_OBJECT, Object.class);
//      TYPE_MAP.put(JDBCType.DISTINCT, D)
        TYPE_MAP.put(JDBCType.STRUCT, Struct.class);
        TYPE_MAP.put(JDBCType.ARRAY, Object[].class);
        TYPE_MAP.put(JDBCType.BLOB, byte[].class);
        TYPE_MAP.put(JDBCType.CLOB, char[].class);
        TYPE_MAP.put(JDBCType.REF, Ref.class);
//      TYPE_MAP.put(JDBCType.DATALINK, datalink)
        TYPE_MAP.put(JDBCType.BOOLEAN, boolean.class);
        /* JDBC 4.0 Types */

//            /**
//             * Identifies the SQL type {@code ROWID}.
//             */
//            ROWID(Types.ROWID),
//            /**
//             * Identifies the generic SQL type {@code NCHAR}.
//             */
//            NCHAR(Types.NCHAR),
//            /**
//             * Identifies the generic SQL type {@code NVARCHAR}.
//             */
//            NVARCHAR(Types.NVARCHAR),
//            /**
//             * Identifies the generic SQL type {@code LONGNVARCHAR}.
//             */
//            LONGNVARCHAR(Types.LONGNVARCHAR),
//            /**
//             * Identifies the generic SQL type {@code NCLOB}.
//             */
//            NCLOB(Types.NCLOB),
//            /**
//             * Identifies the generic SQL type {@code SQLXML}.
//             */
//            SQLXML(Types.SQLXML),
//
//            /* JDBC 4.2 Types */
//
//            /**
//             * Identifies the generic SQL type {@code REF_CURSOR}.
//             */
//            REF_CURSOR(Types.REF_CURSOR),
//
//            /**
//             * Identifies the generic SQL type {@code TIME_WITH_TIMEZONE}.
//             */
//            TIME_WITH_TIMEZONE(Types.TIME_WITH_TIMEZONE),
//
//            /**
//             * Identifies the generic SQL type {@code TIMESTAMP_WITH_TIMEZONE}.
//             */
//            TIMESTAMP_WITH_TIMEZONE(Types.TIMESTAMP_WITH_TIMEZONE);
    }

    public Type getType(JDBCType jdbcType) {
        return TYPE_MAP.get(jdbcType);
    }

    public static JDBCTypeConvert getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        static final JDBCTypeConvert instance = new JDBCTypeConvert();
    }
}
