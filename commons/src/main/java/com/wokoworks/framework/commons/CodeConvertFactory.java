package com.wokoworks.framework.commons;

import com.wokoworks.framework.commons.tuple.Tuple;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author 0x0001
 */
public class CodeConvertFactory {
    private final Map<Tuple.TwoTuple<Class<? extends Enum<?>>, Class<? extends Enum<?>>>, CodeConvert<? extends Enum<?>, ? extends Enum<?>>> map = new HashMap<>();

    public <T extends Enum<T>, E extends Enum<E>> Optional<CodeConvert<T, E>> getConvert(Class<T> from, Class<E> to) {
        return Optional.<CodeConvert<T, E>>ofNullable(((CodeConvert<T, E>) map.get(Tuple.of(from, to))));
    }

    public <T extends Enum<T>, E extends Enum<E>> CodeConvertFactory register(CodeConvert<T, E> convert) {
        map.put(Tuple.of(convert.fromClass(), convert.toClass()), convert);
        return this;
    }


    public interface CodeConvert<T extends Enum<T>, E extends Enum<E>> {
        Class<T> fromClass();

        Class<E> toClass();

        E convert(T t);
    }

    public static abstract class AbsCodeConvert<T extends Enum<T>, E extends Enum<E>> implements CodeConvert<T, E> {

        private final Map<T, E> convertMap = new HashMap<>();
        private final Class<T> fromClass;
        private final Class<E> toClass;

        public AbsCodeConvert(Class<T> fromClass, Class<E> toClass) {
            this.fromClass = fromClass;
            this.toClass = toClass;
        }

        @Override
        public Class<T> fromClass() {
            return fromClass;
        }

        @Override
        public Class<E> toClass() {
            return toClass;
        }

        @Override
        public E convert(T t) {
            E code = convertMap.get(t);
            if (code == null) {
                throw new RuntimeException("not support code convert" + t);
            }
            return code;
        }

        protected void register(T from, E to) {
            convertMap.put(from, to);
        }

        @PostConstruct
        public abstract void init();
    }
}
