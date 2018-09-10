package com.wokoworks.framework.data.repository;

import com.wokoworks.framework.commons.vo.Pair;
import com.wokoworks.framework.data.BaseRepositoryTest;
import com.wokoworks.framework.data.Page;
import com.wokoworks.framework.data.Sort;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class TestBeanRepositoryImplTest extends BaseRepositoryTest {
    @Autowired
    private TestBeanRepository testBeanRepository;

    @Test
    public void findById() {
        final Optional<TestBean> optional = testBeanRepository.findById(1);
        Assert.assertTrue("not found", optional.isPresent());

        optional.ifPresent(bean -> {
            Assert.assertEquals("name equals", "小明", bean.getName());
            Assert.assertEquals("age equals", 28, bean.getAge());
            Assert.assertEquals("dt equals", 2, bean.getDt());
        });

        final Optional<TestBean> notFound = testBeanRepository.findById(100);
        Assert.assertFalse("not found test", notFound.isPresent());
    }

    @Test
    public void findInIds() {
        {
            {
                final List<TestBean> beans = testBeanRepository.findInIds(null);
                Assert.assertTrue("beans is empty", beans.isEmpty());
            }

            {
                final List<TestBean> beans = testBeanRepository.findInIds(Collections.emptyList());
                Assert.assertTrue("beans is empty", beans.isEmpty());
            }
        }
        {
            final List<TestBean> beans = testBeanRepository.findInIds(Arrays.asList(1, 2, 100));
            Assert.assertEquals("length equals", 2, beans.size());

            final Set<Integer> ids = beans.stream().map(TestBean::getId).collect(Collectors.toSet());
            Assert.assertTrue("id check", ids.containsAll(Arrays.asList(1, 2)));

            final Set<String> names = beans.stream().map(TestBean::getName).collect(Collectors.toSet());
            Assert.assertTrue("name check", names.containsAll(Arrays.asList("小明", "小红")));

            final Set<Integer> ages = beans.stream().map(TestBean::getAge).collect(Collectors.toSet());
            Assert.assertTrue("age check", ages.containsAll(Arrays.asList(28, 18)));
        }

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

        Assert.assertEquals("table row count", 3, count);
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
            Assert.assertEquals("db and create equals", testBean, bean);
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
        Assert.assertEquals("db beans size equals", testBeans.size(), dbBeans.size());

        testBeanRepository.setIds(testBeans, keyHolder, TestBean::setId);

        int matchCount = 0;
        for (TestBean testBean : testBeans) {
            for (TestBean dbBean : dbBeans) {
                if (dbBean.getId() == testBean.getId()) {
                    Assert.assertEquals("db bean equals create bean", testBean, dbBean);
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
            Assert.assertEquals("all db size equals", 3, beans.size());
        }

        // age asc
        {
            final List<TestBean> ageBeans = testBeanRepository.findAll(Sort.of("age", Sort.Direction.ASC));
            Assert.assertEquals("all db size equals", 3, ageBeans.size());
            int prevAge = ageBeans.get(0).getAge();
            for (TestBean ageBean : ageBeans) {
                Assert.assertTrue("sort assert", prevAge <= ageBean.getAge());
                prevAge = ageBean.getAge();
            }
        }

        // age desc

        {
            final List<TestBean> ageBeansDesc = testBeanRepository.findAll(Sort.of("age", Sort.Direction.DESC));
            Assert.assertEquals("all db size equals", 3, ageBeansDesc.size());
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
            Assert.assertEquals("all db size equals", 3, beans.size());
            int prevAge = beans.get(0).getAge();
            long prevDt = beans.get(0).getDt();
            for (TestBean ageBean : beans) {
                Assert.assertTrue("sort assert", prevAge >= ageBean.getAge());
                if (prevAge == ageBean.getAge()) {
                    Assert.assertTrue("sort assert", prevDt <= ageBean.getDt());
                }
                prevAge = ageBean.getAge();
                prevDt = ageBean.getDt();
            }
        }

    }

    @Test
    public void findAllWithPage() {
        // 多页完全分割
        {
            final Page<TestBean> page = testBeanRepository.findAllWithPage(null, 1, 1);
            Assert.assertEquals("total page", 3, page.getTotalPage());
            Assert.assertEquals("total count", 3, page.getTotalCount());
            Assert.assertEquals("current page", 1, page.getCurrentPageNo());
            Assert.assertEquals("page size", 1, page.getPageSize());
            Assert.assertEquals("content size", 1, page.getData().size());
        }

        // 多页第下一页
        {
            final Page<TestBean> page = testBeanRepository.findAllWithPage(null, 2, 1);
            Assert.assertEquals("total page", 3, page.getTotalPage());
            Assert.assertEquals("total count", 3, page.getTotalCount());
            Assert.assertEquals("current page", 2, page.getCurrentPageNo());
            Assert.assertEquals("page size", 1, page.getPageSize());
            Assert.assertEquals("content size", 1, page.getData().size());
        }

        // 多页不完全分割
        {
            final Page<TestBean> page = testBeanRepository.findAllWithPage(null, 1, 2);
            Assert.assertEquals("total page", 2, page.getTotalPage());
            Assert.assertEquals("total count", 3, page.getTotalCount());
            Assert.assertEquals("current page", 1, page.getCurrentPageNo());
            Assert.assertEquals("page size", 2, page.getPageSize());
            Assert.assertEquals("content size", 2, page.getData().size());
        }

        // 单页完全分割
        {
            final Page<TestBean> page = testBeanRepository.findAllWithPage(null, 1, 3);
            Assert.assertEquals("total page", 1, page.getTotalPage());
            Assert.assertEquals("total count", 3, page.getTotalCount());
            Assert.assertEquals("current page", 1, page.getCurrentPageNo());
            Assert.assertEquals("page size", 3, page.getPageSize());
            Assert.assertEquals("content size", 3, page.getData().size());
        }

    }

    @Test
    public void findAllWithPageAndSort() {
        List<Sort> sorts = new ArrayList<>();
        sorts.add(Sort.of("age", Sort.Direction.ASC));
        // 多页完全分割
        {
            final Page<TestBean> page = testBeanRepository.findAllWithPage(sorts, 1, 1);
            Assert.assertEquals("total page", 3, page.getTotalPage());
            Assert.assertEquals("total count", 3, page.getTotalCount());
            Assert.assertEquals("current page", 1, page.getCurrentPageNo());
            Assert.assertEquals("content size", 1, page.getData().size());

            assertSorts(page.getData(), pair -> pair.getFirst().getAge() <= pair.getSecond().getAge());
        }

        // 多页第下一页
        {
            final Page<TestBean> page = testBeanRepository.findAllWithPage(sorts, 2, 1);
            Assert.assertEquals("total page", 3, page.getTotalPage());
            Assert.assertEquals("total count", 3, page.getTotalCount());
            Assert.assertEquals("current page", 2, page.getCurrentPageNo());
            Assert.assertEquals("content size", 1, page.getData().size());

            assertSorts(page.getData(), pair -> pair.getFirst().getAge() <= pair.getSecond().getAge());
        }

        // 多页不完全分割
        {
            final Page<TestBean> page = testBeanRepository.findAllWithPage(sorts, 1, 2);
            Assert.assertEquals("total page", 2, page.getTotalPage());
            Assert.assertEquals("total count", 3, page.getTotalCount());
            Assert.assertEquals("current page", 1, page.getCurrentPageNo());
            Assert.assertEquals("content size", 2, page.getData().size());
            assertSorts(page.getData(), pair -> pair.getFirst().getAge() <= pair.getSecond().getAge());
        }

        // 单页完全分割
        {
            final Page<TestBean> page = testBeanRepository.findAllWithPage(sorts, 1, 3);
            Assert.assertEquals("total page", 1, page.getTotalPage());
            Assert.assertEquals("total count", 3, page.getTotalCount());
            Assert.assertEquals("current page", 1, page.getCurrentPageNo());
            Assert.assertEquals("content size", 3, page.getData().size());
            assertSorts(page.getData(), pair -> pair.getFirst().getAge() <= pair.getSecond().getAge());
        }
    }

    private void assertSorts(List<TestBean> beans, Function<Pair<TestBean, TestBean>, Boolean> test) {
        TestBean prevBean = beans.get(0);
        for (TestBean bean : beans) {
            if (prevBean == bean) {
                continue;
            }
            Assert.assertTrue("sort assert", test.apply(Pair.of(prevBean, bean)));
            prevBean = bean;
        }
    }

    @Test
    public void updateNameById() {
        // 更新存在对象
        {
            final int id = 1;
            final String name = "name" + System.currentTimeMillis();
            final int effectRow = testBeanRepository.updateNameById(name, id);
            Assert.assertEquals("effect row", 1, effectRow);

            final Optional<TestBean> testBean = testBeanRepository.findById(id);
            Assert.assertTrue("test bean exists", testBean.isPresent());

            testBean.ifPresent(bean -> {
                Assert.assertEquals("name equals", name, bean.getName());
            });
        }

        // 更新不存在对象
        {
            final int id = 100;
            final String name = "name" + System.currentTimeMillis();
            final int effectRow = testBeanRepository.updateNameById(name, id);
            Assert.assertEquals("effect row", 0, effectRow);

            final Optional<TestBean> testBean = testBeanRepository.findById(id);
            Assert.assertFalse("test bean exists", testBean.isPresent());
        }
    }

    @Test
    public void findByNameOrAge() {
        final String name = "小明";
        final int age = 20;
        final List<TestBean> beans = testBeanRepository.findByNameOrAge(name, age);
        Assert.assertEquals("bean size", 2, beans.size());

        for (TestBean bean : beans) {
            Assert.assertTrue("equals ", name.equals(bean.getName()) || age == bean.getAge());
        }
    }

    @Test
    public void findByNameAndAge() {
        // 能查询到的情况
        {
            final String name = "小明";
            final int age = 28;
            final List<TestBean> beans = testBeanRepository.findByNameAndAge(name, age);
            Assert.assertEquals("bean size", 1, beans.size());

            for (TestBean bean : beans) {
                Assert.assertTrue("equals ", name.equals(bean.getName()) && age == bean.getAge());
            }
        }

        // 查询不到的情况
        {
            final String name = "小明";
            final int age = 29;
            final List<TestBean> beans = testBeanRepository.findByNameAndAge(name, age);
            Assert.assertEquals("bean size", 0, beans.size());
        }
    }

    @Test
    public void findByNameAndAge1() {
        // 能查询到的情况
        {
            final String name = "小明";
            final int age = 28;
            final List<TestBean> beans = testBeanRepository.findByNameAndAge1(name, age);
            Assert.assertEquals("bean size", 1, beans.size());

            for (TestBean bean : beans) {
                Assert.assertTrue("equals ", name.equals(bean.getName()) && age == bean.getAge());
            }
        }

        // 查询不到的情况
        {
            final String name = "小明";
            final int age = 29;
            final List<TestBean> beans = testBeanRepository.findByNameAndAge1(name, age);
            Assert.assertEquals("bean size", 0, beans.size());
        }
    }

    @Test
    public void findByAgeLessThen() {
        // 能查询到的情况
        {
            final List<TestBean> beans = testBeanRepository.findByAgeLessThen(28);
            Assert.assertEquals("record size", 2, beans.size());
            for (TestBean bean : beans) {
                Assert.assertTrue("age < 28", bean.getAge() < 28);
            }
        }
        // 查询不到的情况
        {
            final List<TestBean> beans = testBeanRepository.findByAgeLessThen(18);
            Assert.assertEquals("record size", 0, beans.size());
        }
    }

    @Test
    public void findByAgeLessThenEqual() {
        // 能查询到的情况
        {
            final List<TestBean> beans = testBeanRepository.findByAgeLessThenEqual(18);
            Assert.assertEquals("record size", 1, beans.size());
            for (TestBean bean : beans) {
                Assert.assertTrue("age <= 18", bean.getAge() <= 18);
            }
        }
        // 查询不到的情况
        {
            final List<TestBean> beans = testBeanRepository.findByAgeLessThenEqual(17);
            Assert.assertEquals("record size", 0, beans.size());
        }
    }

    @Test
    public void findByAgeGreaterThen() {
        // 能查询到的情况
        {
            final List<TestBean> beans = testBeanRepository.findByAgeGreaterThen(18);
            Assert.assertEquals("record size", 2, beans.size());
            for (TestBean bean : beans) {
                Assert.assertTrue("age >= 18", bean.getAge() >= 18);
            }
        }

        // 查询不到的情况
        {
            final List<TestBean> beans = testBeanRepository.findByAgeGreaterThen(28);
            Assert.assertEquals("record size", 0, beans.size());
        }
    }

    @Test
    public void findByAgeGreaterThenEqual() {
        // 能查询到的情况
        {
            final List<TestBean> beans = testBeanRepository.findByAgeGreaterThenEqual(18);
            Assert.assertEquals("record size", 3, beans.size());
            for (TestBean bean : beans) {
                Assert.assertTrue("age >= 18", bean.getAge() >= 18);
            }
        }
        // 查询不到的情况
        {
            final List<TestBean> beans = testBeanRepository.findByAgeGreaterThenEqual(29);
            Assert.assertEquals("record size", 0, beans.size());
        }
    }

    @Test
    public void findByNameLike() {
        // 能查询到的情况
        {
            final List<TestBean> beans = testBeanRepository.findByNameLike("小");
            System.out.println("beans " + beans);
            Assert.assertEquals("record size", 3, beans.size());
            for (TestBean bean : beans) {
                Assert.assertTrue("name like 小", bean.getName().contains("小"));
            }
        }
        // 查询不到的情况
        {
            final List<TestBean> beans = testBeanRepository.findByNameLike("大");
            Assert.assertEquals("record size", 0, beans.size());
        }
    }

    @Test
    public void findByNameNotLike() {
        // 能查询到的情况
        {
            final List<TestBean> beans = testBeanRepository.findByNameNotLike("大");
            System.out.println("beans " + beans);
            Assert.assertEquals("record size", 3, beans.size());
            for (TestBean bean : beans) {
                Assert.assertTrue("name not like 大", !bean.getName().contains("大"));
            }
        }
        // 查询不到的情况
        {
            final List<TestBean> beans = testBeanRepository.findByNameNotLike("小");
            Assert.assertEquals("record size", 0, beans.size());
        }
    }
}