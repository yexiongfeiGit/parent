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
     * 保存对象
     *
     * @param t         需要保存的对象
     * @param keyHolder 主键保存
     * @return 数据库影响行数
     */
    int save(T t, KeyHolder keyHolder);

    /**
     * 保存对象, 不需要返回id
     *
     * @param t 需要保存的对象
     * @return 数据库影响行数
     */
    default int save(T t) {
        return save(t, null);
    }

    /**
     * 通过id查找对象
     *
     * @param id 主键
     * @return 查询到的对象信息
     */
    Optional<T> findById(K id);

    /**
     * 通过id集合查询列表
     *
     * @param ids id集合
     * @return 查询到的对象集合
     */
    List<T> findInIds(Collection<? extends K> ids);

    /**
     * 通过id删除对象
     *
     * @param id 主键
     * @return 数据库影响行数
     */
    int deleteById(K id);

    /**
     * 统计表记录行数
     *
     * @return 表记录行数
     */
    int totalCount();


    /**
     * 查询所有数据
     *
     * @param sorts 排序方式
     * @return 表里所有的记录
     */
    List<T> findAll(Sort... sorts);


    /**
     * 查询所有数据,通过分页返回
     *
     * @param sorts  排序方式
     * @param pageNo 第几页
     * @param limit  分页条数
     * @return
     */
    Page<T> findAllWithPage(List<Sort> sorts, int pageNo, int limit);


    /**
     * 批量保存数据,并提取自增id
     *
     * @param list      需要保存的对象列表
     * @param keyHolder 需要保留自增主键的holder
     * @return 影响行数
     */
    int[] batchSave(List<T> list, KeyHolder keyHolder);

    /**
     * 批量保存数据,不需要提取自增id
     *
     * @param list 需要保存的对象列表
     * @return 影响行数
     */
    default int[] batchSave(List<T> list) {
        return batchSave(list, null);
    }


    /**
     * 设置id工具方法
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
     * id设置接口
     *
     * @param <T>
     * @param <K>
     */
    @FunctionalInterface
    interface IDSetter<T, K> {
        /**
         * 设置id
         *
         * @param data
         * @param id
         */
        void setId(T data, K id);
    }
}
