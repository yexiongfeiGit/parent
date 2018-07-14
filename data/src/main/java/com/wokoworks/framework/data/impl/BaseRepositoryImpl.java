package com.wokoworks.framework.data.impl;

import com.wokoworks.framework.data.BaseRepository;
import com.wokoworks.framework.data.Page;
import com.wokoworks.framework.data.Sort;
import com.wokoworks.framework.data.impl.condition.Conditions;
import com.wokoworks.framework.data.impl.sqlbuilder.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 0x0001
 */
@Slf4j
public abstract class BaseRepositoryImpl<T, K> implements BaseRepository<T, K>, SqlExecuteProvider<T> {
    @Getter
    private JdbcTemplate jdbcTemplate;

    private final String idColumnName;

    @Getter
    private final JdbcTemplateUtils<T> jdbcTemplateUtils;

    @Getter
    private final List<PropertyDescriptor> descriptorList;

    public BaseRepositoryImpl() {
        idColumnName = getIdColumnName();
        final Class<T> beanClass = getBeanClass();
        this.jdbcTemplateUtils = new JdbcTemplateUtils<>(new BeanPropertyRowMapper<>(beanClass));

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
            PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();

            descriptorList = Arrays.stream(descriptors).filter(d -> d.getWriteMethod() != null).filter(d -> d.getReadMethod() != null)
                .collect(Collectors.toList());
        } catch (IntrospectionException e) {
            throw new IllegalStateException(e);
        }
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.jdbcTemplateUtils.setJdbcTemplate(jdbcTemplate);
    }

    // --------------------------- abstract methods

    /**
     * 获取bean类
     *
     * @return
     */
    public abstract Class<T> getBeanClass();
    // ----------------------------

    @Override
    public Optional<T> findById(K id) {
        return select().where(Conditions.equals(idColumnName, id)).findOne();
    }

    @Override
    public List<T> findInIds(Collection<? extends K> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return select().where(Conditions.in(idColumnName, ids)).find();
    }

    @Override
    public int deleteById(K id) {
        return delete().where(Conditions.equals(idColumnName, id)).delete();
    }

    @Override
    public int totalCount() {
        return select().findCount();
    }

    @Override
    public int save(T t, KeyHolder keyHolder) {
        return new InsertBuilder<>(this).insert(t, keyHolder);
    }

    @Override
    public int[] batchSave(List<T> list, KeyHolder keyHolder) {
        return new InsertBuilder<>(this).batch(list, keyHolder);
    }

    @Override
    public List<T> findAll(Sort... sorts) {
        final SelectBuilder<T> select = select();
        if (sorts != null) {
            select.orderBy(sorts);
        }
        return select.find();
    }

    @Override
    public Page<T> findAllWithPage(List<Sort> sorts, int pageNo, int limit) {
        final SelectBuilder<T> select = select();
        if (sorts != null) {
            select.orderBy(sorts);
        }
        return select.page(pageNo, limit);
    }

    // ------------------------------- protected methods

    protected final int findCount(CharSequence sql, Object... args) {
        return jdbcTemplateUtils.findCount(sql, args);
    }


    protected final List<T> find(CharSequence sql, Object... args) {
        return jdbcTemplateUtils.find(sql, args);
    }

    protected final Optional<T> findOne(CharSequence sql, Object... args) {
        return jdbcTemplateUtils.findOne(sql, args);
    }

    protected final int update(CharSequence sql, Object... args) {
        return jdbcTemplateUtils.update(sql, args);
    }


    public String getIdColumnName() {
        return "id";
    }

    protected final SelectBuilder<T> select() {
        return new SelectBuilder<>(this);
    }

    protected UpdateBuilder<T> update() {
        return new UpdateBuilder<>(this);
    }

    protected DeleteBuilder<T> delete() {
        return new DeleteBuilder<>(this);
    }


}