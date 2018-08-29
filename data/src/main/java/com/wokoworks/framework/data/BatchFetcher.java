package com.wokoworks.framework.data;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 0x0001
 */
@Slf4j
public class BatchFetcher<T, K extends Serializable> {

    private final BaseRepository<T, K> repository;
    private final Multimap<K, Consumer<T>> multimap;

    public BatchFetcher(BaseRepository<T, K> repository) {
        this.repository = repository;
        multimap = Multimaps.newMultimap(new HashMap<>(), ArrayList::new);
    }

    public BatchFetcher<T, K> add(K id, Consumer<T> consumer) {
        multimap.put(id, consumer);
        return this;
    }

    public void fetch(Function<T, K> idExtract, boolean ignoreNull) {
        fetch(idExtract, ignoreNull, null);
    }

    public void fetch(Function<T, K> idExtract, T defaultValue) {
        fetch(idExtract, false, defaultValue);
    }

    private void fetch(Function<T, K> idExtract, boolean ignoreNull, T defaultValue) {
        final Set<K> ids = multimap.keySet();
        if (ids.isEmpty()) {
            return;
        }
        final List<T> lists = repository.findInIds(ids);
        final Map<K, T> map = lists.stream().collect(Collectors.toMap(idExtract, Function.identity()));

        multimap.forEach((k, supplier) -> {
            final T data = map.get(k);
            if (data == null) {
                log.info("not found id: {}, in repository: {}", k, repository);
            }
            if (ignoreNull && data == null) {
                return;
            }
            supplier.accept(Optional.ofNullable(data).orElse(defaultValue));
        });
    }
}