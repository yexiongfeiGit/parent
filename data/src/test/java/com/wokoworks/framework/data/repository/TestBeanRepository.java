package com.wokoworks.framework.data.repository;

import com.wokoworks.framework.data.BaseRepository;

import java.util.List;

public interface TestBeanRepository extends BaseRepository<TestBean, Integer> {

    /**
     * pass id Update name
     *
     * @param name The name that needs to be set
     * @param id   Need to update the object id
     * @return Number of updates
     */
    int updateNameById(String name, int id);

    /**
     * Inquiry through name or age
     *
     * @param name The name that needs to be query
     * @param age  Age to query
     * @return Eligible records
     */
    List<TestBean> findByNameOrAge(String name, int age);

    /**
     * Inquiry through names and age
     *
     * @param name Need to query the name
     * @param age  Age to query
     * @return Eligible records
     */
    List<TestBean> findByNameAndAge(String name, int age);


    /**
     * Inquiry through names and age
     *
     * @param name Need to query the name
     * @param age  Age to query
     * @return Eligible records
     */
    List<TestBean> findByNameAndAge1(String name, int age);

    /**
     * Query the object of age less than the specified value
     *
     * @param age age
     * @return
     */
    List<TestBean> findByAgeLessThen(int age);

    /**
     * Query objects that age less than or equal to a certain value
     *
     * @param age age
     * @return
     */
    List<TestBean> findByAgeLessThenEqual(int age);

    /**
     * Query objects with greater age than a certain value
     *
     * @param age age
     * @return
     */
    List<TestBean> findByAgeGreaterThen(int age);

    /**
     * Users who are older than or equal to a certain value
     *
     * @param age age
     * @return
     */
    List<TestBean> findByAgeGreaterThenEqual(int age);

    /**
     * Query the name of the name like a certain value
     * @param name
     * @return
     */
    List<TestBean> findByNameLike(String name);

    /**
     * The query name is not like a user with a certain value
     * @param name
     * @return
     */
    List<TestBean> findByNameNotLike(String name);
}
