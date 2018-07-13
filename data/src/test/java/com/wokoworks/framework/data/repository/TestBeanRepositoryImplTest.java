package com.wokoworks.framework.data.repository;

import com.wokoworks.framework.data.BaseRepositoryTest;
import com.wokoworks.framework.data.Sort;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.*;
import java.util.stream.Collectors;


public class TestBeanRepositoryImplTest extends BaseRepositoryTest {
    @Autowired
    private TestBeanRepository testBeanRepository;

    @Test
    public void findById() {
        final Optional<TestBean> optional = testBeanRepository.findById(1);
        Assert.assertTrue("not found", optional.isPresent());

        optional.ifPresent(bean -> {
            Assert.assertEquals("name equal", "小明", bean.getName());
            Assert.assertEquals("age equal", 28, bean.getAge());
            Assert.assertEquals("dt equal", 1, bean.getDt());
        });

        final Optional<TestBean> notFound = testBeanRepository.findById(100);
        Assert.assertFalse("not found test", notFound.isPresent());
    }

    @Test
    public void findInIds() {
        final List<TestBean> beans = testBeanRepository.findInIds(Arrays.asList(1, 2, 100));
        Assert.assertEquals("length equal", 2, beans.size());

        final Set<Integer> ids = beans.stream().map(TestBean::getId).collect(Collectors.toSet());
        Assert.assertTrue("id check", ids.containsAll(Arrays.asList(1, 2)));

        final Set<String> names = beans.stream().map(TestBean::getName).collect(Collectors.toSet());
        Assert.assertTrue("name check", names.containsAll(Arrays.asList("小明", "小红")));

        final Set<Integer> ages = beans.stream().map(TestBean::getAge).collect(Collectors.toSet());
        Assert.assertTrue("age check", ages.containsAll(Arrays.asList(28, 18)));

    }

    @Test
    public void deleteById() {
        final int id = 1;

        final Optional<TestBean> idBean = testBeanRepository.findById(id);
        Assert.assertTrue("id check", idBean.isPresent());

        final int effectRow = testBeanRepository.deleteById(id);
        Assert.assertEquals("effect row", 1, effectRow);

        final Optional<TestBean> deletedBean = testBeanRepository.findById(id);
        Assert.assertFalse("delete bean", deletedBean.isPresent());

        final int deleteEffectRow = testBeanRepository.deleteById(id);
        Assert.assertEquals("effect row", 0, deleteEffectRow);
    }

    @Test
    public void totalCount() {
        final int count = testBeanRepository.totalCount();

        Assert.assertEquals("table row count", 2, count);
    }

    @Test
    public void saveNoId() {
        final TestBean testBean = new TestBean();
        testBean.setName("小白");
        testBean.setAge(20);
        testBean.setDt(System.currentTimeMillis());

        final int effectRow = testBeanRepository.save(testBean);

        Assert.assertEquals("effect row", 1, effectRow);
    }

    @Test
    public void saveGetId() {
        final TestBean testBean = createBean(System.currentTimeMillis());

        final KeyHolder keyHolder = new GeneratedKeyHolder();
        final int effectRow = testBeanRepository.save(testBean, keyHolder);

        Assert.assertEquals("effect row", 1, effectRow);

        final int id = keyHolder.getKey().intValue();
        Assert.assertTrue("generator id", id > 0);

        testBean.setId(id);

        final Optional<TestBean> optionalBean = testBeanRepository.findById(id);
        Assert.assertTrue("find saved object", optionalBean.isPresent());

        optionalBean.ifPresent(bean -> {
            Assert.assertEquals("db and create equal", testBean, bean);
        });
    }

    @Test
    public void batchSaveNoId() {
        List<TestBean> testBeans = new ArrayList<>();
        long seed = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            testBeans.add(createBean(seed++));
        }

        final int[] results = testBeanRepository.batchSave(testBeans);
        Assert.assertNotNull("effect row result", results);

        final int[] expectArray = new int[results.length];
        Arrays.fill(expectArray, 1);

        Assert.assertArrayEquals("results", expectArray, results);

    }

    @Test
    public void batchSaveGetId() {
        List<TestBean> testBeans = new ArrayList<>();
        long seed = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            testBeans.add(createBean(seed++));
        }

        final KeyHolder keyHolder = new GeneratedKeyHolder();

        final int[] results = testBeanRepository.batchSave(testBeans, keyHolder);
        Assert.assertNotNull("effect row result", results);

        final int[] expectArray = new int[results.length];
        Arrays.fill(expectArray, 1);

        Assert.assertArrayEquals("results", expectArray, results);
        Assert.assertEquals("generator id size", testBeans.size(), keyHolder.getKeyList().size());


        final Set<Integer> ids = keyHolder.getKeyList().stream().map(Map::values)
            .map(v -> v.iterator().next())
            .map(id -> (Number) id)
            .map(Number::intValue)
            .collect(Collectors.toSet());

        final List<TestBean> dbBeans = testBeanRepository.findInIds(ids);
        Assert.assertEquals("db beans size equal", testBeans.size(), dbBeans.size());

        testBeanRepository.setIds(testBeans, keyHolder, TestBean::setId);

        int matchCount = 0;
        for (TestBean testBean : testBeans) {
            for (TestBean dbBean : dbBeans) {
                if (dbBean.getId() == testBean.getId()) {
                    Assert.assertEquals("db bean equal create bean", testBean, dbBean);
                    matchCount++;
                }
            }
        }

        Assert.assertEquals("db and create all match", testBeans.size(), matchCount);

    }

    private TestBean createBean(long seed) {
        final TestBean testBean = new TestBean();
        testBean.setName("name" + seed);
        testBean.setAge((int) (seed % 20));
        testBean.setDt(seed);
        return testBean;
    }

    @Test
    public void findAll() {
        // 不排序
        {
            final List<TestBean> beans = testBeanRepository.findAll();
            Assert.assertEquals("all db size equal", 2, beans.size());
        }

        // age asc
        {
            final List<TestBean> ageBeans = testBeanRepository.findAll(Sort.of("age", Sort.Direction.ASC));
            Assert.assertEquals("all db size equal", 2, ageBeans.size());
            int prevAge = ageBeans.get(0).getAge();
            for (TestBean ageBean : ageBeans) {
                Assert.assertTrue("sort assert", prevAge <= ageBean.getAge());
                prevAge = ageBean.getAge();
            }
        }

        // age desc

        {
            final List<TestBean> ageBeansDesc = testBeanRepository.findAll(Sort.of("age", Sort.Direction.DESC));
            Assert.assertEquals("all db size equal", 2, ageBeansDesc.size());
            int prevAge = ageBeansDesc.get(0).getAge();
            for (TestBean ageBean : ageBeansDesc) {
                Assert.assertTrue("sort assert", prevAge >= ageBean.getAge());
                prevAge = ageBean.getAge();
            }
        }

        // 多个列排序
        {
            final List<TestBean> beans = testBeanRepository.findAll(
                Sort.of("age", Sort.Direction.DESC),
                Sort.of("dt", Sort.Direction.ASC)
            );
            Assert.assertEquals("all db size equal", 2, beans.size());
            int prevAge = beans.get(0).getAge();
            long prevDt = beans.get(0).getDt();
            for (TestBean ageBean : beans) {
                Assert.assertTrue("sort assert", prevAge >= ageBean.getAge() && prevDt <= ageBean.getDt());
                prevAge = ageBean.getAge();
                prevDt = ageBean.getDt();
            }
        }

    }

    @Test
    public void findAllWithPage() {
    }
}