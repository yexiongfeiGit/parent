package com.wokoworks.framework.data.repository;

import com.wokoworks.framework.data.BaseRepository;

import java.util.List;

public interface TestBeanRepository extends BaseRepository<TestBean, Integer> {

    /**
     * 通过id更新名字
     *
     * @param name 需要设置的名称
     * @param id   需要更新对象的id
     * @return 更新条数
     */
    int updateNameById(String name, int id);

    /**
     * 通过名字或者年龄查询
     *
     * @param name 需要查询的名称
     * @param age  需要查询的年龄
     * @return 符合条件的记录
     */
    List<TestBean> findByNameOrAge(String name, int age);
}
