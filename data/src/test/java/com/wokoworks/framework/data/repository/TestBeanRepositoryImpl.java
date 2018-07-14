package com.wokoworks.framework.data.repository;

import com.wokoworks.framework.data.impl.BaseRepositoryImpl;
import com.wokoworks.framework.data.impl.condition.Conditions;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    @Override
    public int updateNameById(String name, int id) {
        return update().set("name", name)
            .where(Conditions.equals("id", id))
            .update();
    }

    @Override
    public List<TestBean> findByNameOrAge(String name, int age) {
        return select().where(Conditions.equals("name", name).or(Conditions.equals("age", age)))
            .find();
    }
}
