package com.wokoworks.framework.data.repository;

import com.wokoworks.framework.data.impl.BaseRepositoryImpl;
import org.springframework.stereotype.Repository;

@Repository
public class TestBeanRepositoryImpl extends BaseRepositoryImpl<TestBean, Integer> implements TestBeanRepository {

    @Override
    public String getTableName() {
        return "test_bean";
    }

    @Override
    public Class<TestBean> getBeanClass() {
        return TestBean.class;
    }
}
