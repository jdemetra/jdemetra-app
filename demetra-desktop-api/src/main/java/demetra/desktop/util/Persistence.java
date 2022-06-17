package demetra.desktop.util;

import demetra.desktop.Config;
import demetra.desktop.beans.BeanHandler;
import demetra.tsprovider.util.PropertyHandler;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

@lombok.Value
@lombok.Builder
public class Persistence<T> {

    @lombok.NonNull
    String domain;

    @lombok.NonNull
    String name;

    @lombok.NonNull
    String version;

    @lombok.Singular
    List<BiConsumer<T, Config.Builder>> getters;

    @lombok.Singular
    List<BiConsumer<T, Config>> setters;

    public Config loadConfig(T target) {
        Config.Builder config = Config.builder(domain, name, version);
        getters.forEach(item -> item.accept(target, config));
        return config.build();
    }

    public void storeConfig(T target, Config config) {
        Config.checkDomain(config, domain);
        setters.forEach(item -> item.accept(target, config));
    }

    public static <X> Builder<X> builderOf(Class<X> type) {
        return new Builder<X>().domain(type.getName());
    }

    public static final class Builder<T> {

        public <X> Builder<T> with(PropertyHandler<X> p, Function<T, X> getter, BiConsumer<T, X> setter) {
            return with(p, BeanHandler.of(getter, setter));
        }

        public <X> Builder<T> with(PropertyHandler<X> p, BeanHandler<X, T> handler) {
            Config.Converter<X> converter = Config.Converter.of(p);
            getter((target, config) -> converter.set(config, handler.load(target)));
            setter((target, config) -> handler.store(target, converter.get(config)));
            return this;
        }
    }
}
