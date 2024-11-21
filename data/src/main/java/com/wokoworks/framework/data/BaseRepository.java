package com.wokoworks.framework.data;

import org.springframework.jdbc.support.KeyHolder;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author 0x0001
 */
public interface BaseRepository<T, K> {
    /**
     * Saving object
     *
     * @param t         Object to be preserved
     * @param keyHolder Primary keys
     * @return Database influence rows
     */
    int save(T t, KeyHolder keyHolder);

    /**
     * Saving object, No need to return id
     *
     * @param t Object to be preserved
     * @return Database influence rows
     */
    default int save(T t) {
        return save(t, null);
    }

    /**
     * pass id Search object
     *
     * @param id Primary key
     * @return Object information
     */
    Optional<T> findById(K id);

    /**
     * pass id Collection query list
     *
     * @param ids id gather
     * @return Object collection that is found out
     */
    List<T> findInIds(Collection<? extends K> ids);

    /**
     * pass id Delete
     *
     * @param id Primary key
     * @return Database influence rows
     */
    int deleteById(K id);

    /**
     * Statistical table record row
     *
     * @return Table record row number
     */
    int totalCount();


    /**
     * Query all data
     *
     * @param sorts sort by
     * @return All records in the table
     */
    List<T> findAll(Sort... sorts);


    /**
     * Query all data,Return through pages
     *
     * @param sorts  sort by
     * @param pageNo Page
     * @param limit  Paging number
     * @return
     */
    Page<T> findAllWithPage(List<Sort> sorts, int pageNo, int limit);


    /**
     * Batch save data,And extract self -increase id
     *
     * @param list      List of objects that need to be preserved
     * @param keyHolder Need to keep the self -increase main key holder
     * @return Influence row
     */
    int[] batchSave(List<T> list, KeyHolder keyHolder);

    /**
     * Batch save data,No need to extract self -increase id
     *
     * @param list List of objects that need to be preserved
     * @return Influence row
     */
    default int[] batchSave(List<T> list) {
        return batchSave(list, null);
    }


    /**
     * set up id Tool method
     *
     * @param data
     * @param keyHolder
     * @param setter
     */
    default void setIds(List<T> data, KeyHolder keyHolder, IDSetter<T, K> setter) {
        final List<K> ids = keyHolder.getKeyList().stream().map(Map::values)
            .map(c -> c.iterator().next())
            .map(id -> ((K) id))
            .collect(Collectors.toList());
        int i = 0;
        for (T d : data) {
            setter.setId(d, ids.get(i++));
        }
    }

    /**
     * id Set interface
     *
     * @param <T>
     * @param <K>
     */
    @FunctionalInterface
    interface IDSetter<T, K> {
        /**
         * set up id
         *
         * @param data
         * @param id
         */
        void setId(T data, K id);
    }
}
