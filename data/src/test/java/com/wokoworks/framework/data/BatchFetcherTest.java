package com.wokoworks.framework.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.*;
import java.util.stream.Collectors;

@RunWith(MockitoJUnitRunner.class)
public class BatchFetcherTest {

    private BatchFetcher<Person, Integer> personIntegerBatchFetcher;
    @Mock
    private BaseRepository<Person, Integer> repository;

    private Map<Integer, Person> personMap;


    @Before
    public void setup() {
        personIntegerBatchFetcher = new BatchFetcher<>(repository);
        personMap = new HashMap<>();
        personMap.put(1, new Person(1, "name1", 18));
        personMap.put(2, new Person(2, "name2", 20));

        Mockito.when(repository.findInIds(Mockito.any())).thenAnswer(mock -> {
            final Collection<Integer> ids = mock.getArgumentAt(0, Collection.class);
            if (ids == null || ids.isEmpty()) {
                return new ArrayList<>(0);
            }

            List<Person> personList = new ArrayList<>();
            for (Integer id : ids) {
                final Person person = personMap.get(id);
                if (person != null) {
                    personList.add(person);
                }
            }
            // 模拟数据库并不是通过传入id顺序返回的这个事实
            Collections.shuffle(personList);
            return personList;
        });
    }

    @Test
    public void testEmptyKey() {
        personIntegerBatchFetcher.fetch(Person::getId, null);
        Mockito.verify(repository, Mockito.never()).findInIds(Mockito.any());
    }

    @Test
    public void test() {
        List<Posts> list = new ArrayList<>();
        final int size = 10;
        for (int i = 0; i < size; i++) {
            final Posts post = new Posts(i, i + 1);
            list.add(post);
        }

        for (Posts posts : list) {
            personIntegerBatchFetcher.add(posts.getPersonId(), posts::setPerson);
        }

        personIntegerBatchFetcher.fetch(Person::getId, true);
        Mockito.verify(repository).findInIds(Mockito.any());

        final List<Posts> list1 = list.stream().filter(m -> Objects.nonNull(m.getPerson())).collect(Collectors.toList());
        Assert.assertEquals("size equal", 2, list1.size());

        for (Posts posts : list1) {
            Assert.assertEquals("name equal", personMap.get(posts.getPersonId()), posts.getPerson());
        }
    }

    @Test
    public void testDefaultValue() {
        List<Posts> list = new ArrayList<>();
        final int size = 10;
        for (int i = 0; i < size; i++) {
            final Posts post = new Posts(i, i + 1);
            list.add(post);
        }

        for (Posts posts : list) {
            personIntegerBatchFetcher.add(posts.getPersonId(), posts::setPerson);
        }
        final Person defaultPerson = new Person(0, "default name", 0);
        personIntegerBatchFetcher.fetch(Person::getId, defaultPerson);
        Mockito.verify(repository).findInIds(Mockito.any());

        final List<Posts> list1 = list.stream().filter(m -> Objects.nonNull(m.getPerson())).collect(Collectors.toList());
        Assert.assertEquals("size equal", size, list1.size());

        for (Posts posts : list1) {
            final Person person = personMap.get(posts.getPersonId());
            if (person != null) {
                Assert.assertEquals("name equal", person, posts.getPerson());
            } else {
                Assert.assertEquals("name equal", defaultPerson, posts.getPerson());
            }
        }
    }

    @Data
    private static class Posts {
        private final int id;
        private final int personId;
        private Person person;
    }

    @Data
    @AllArgsConstructor
    private static class Person {
        private int id;
        private String name;
        private int age;
    }

}