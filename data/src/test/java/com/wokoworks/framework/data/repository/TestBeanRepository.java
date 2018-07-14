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

    /**
     * 通过名字和年龄查询
     *
     * @param name 需要查询到名字
     * @param age  需要查询的年龄
     * @return 符合条件的记录
     */
    List<TestBean> findByNameAndAge(String name, int age);

    /**
     * 查询年龄小于指定值的对象
     *
     * @param age 年龄
     * @return
     */
    List<TestBean> findByAgeLessThen(int age);

    /**
     * 查询年龄小于等于某个值的对象
     *
     * @param age 年龄
     * @return
     */
    List<TestBean> findByAgeLessThenEqual(int age);

    /**
     * 查询年龄大于某个值的对象
     *
     * @param age 年龄
     * @return
     */
    List<TestBean> findByAgeGreaterThen(int age);

    /**
     * 查询年龄大于等于某个值的用户
     *
     * @param age 年龄
     * @return
     */
    List<TestBean> findByAgeGreaterThenEqual(int age);
}
