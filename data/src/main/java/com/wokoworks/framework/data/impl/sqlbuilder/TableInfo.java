package com.wokoworks.framework.data.impl.sqlbuilder;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import com.wokoworks.framework.data.impl.BaseRepositoryImpl;
import org.springframework.dao.InvalidDataAccessApiUsageException;

import java.beans.PropertyDescriptor;
import java.sql.ResultSetMetaData;
import java.util.*;

class TableInfo<T> {
    private final Map<String, PropertyDescriptor> columnPropertyMap = new HashMap<>();
    private final Object[] emptyArgs = new Object[0];

    TableInfo(List<String> columnNames, List<PropertyDescriptor> descriptorList) {
        Converter<String, String> convert = CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE);
        for (String columnName : columnNames) {
            for (PropertyDescriptor propertyDescriptor : descriptorList) {
                final String name = convert.convert(propertyDescriptor.getName());
                if (columnName.equalsIgnoreCase(name)) {
                    columnPropertyMap.put(columnName.toLowerCase(), propertyDescriptor);
                }
            }
        }
    }

    List<String> getEffectiveColumnNames() {
        return Collections.unmodifiableList(new ArrayList<>(columnPropertyMap.keySet()));
    }

    Object getValueByColumnName(T obj, String columnName) {
        final PropertyDescriptor pd = columnPropertyMap.get(columnName.toLowerCase());
        try {
            return pd.getReadMethod().invoke(obj, emptyArgs);
        } catch(ReflectiveOperationException e) {
            throw new InvalidDataAccessApiUsageException(e.getMessage(), e);
        }
    }

    static <T, K> TableInfo<T> newTableInfo(BaseRepositoryImpl<T, K> repository) {
        return repository.getJdbcTemplate().query("SELECT * FROM " + repository.getTableName() + " LIMIT 0", rs -> {
            final ResultSetMetaData metadata = rs.getMetaData();
            final int columnCount = metadata.getColumnCount();
            List<String> columnNames = new ArrayList<>(columnCount);
            for (int i = 0; i < columnCount; i++) {
                columnNames.add(metadata.getColumnName(i + 1));
            }
            return new TableInfo(columnNames, repository.getDescriptorList());
        });
    }
}