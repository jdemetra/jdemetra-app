package demetra.desktop.util;

import demetra.desktop.Config;
import java.io.File;

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

        public Builder<T> onBoolean(String key, boolean defaultValue, Function<T, Boolean> getter, BiConsumer<T, Boolean> setter) {
            nbbrd.io.text.BooleanProperty property = nbbrd.io.text.BooleanProperty.of(key, defaultValue);
            getter((target, config) -> property.set(config::parameter, getter.apply(target)));
            setter((target, config) -> setter.accept(target, property.get(config::getParameter)));
            return this;
        }

        public Builder<T> onInt(String key, int defaultValue, Function<T, Integer> getter, BiConsumer<T, Integer> setter) {
            nbbrd.io.text.IntProperty property = nbbrd.io.text.IntProperty.of(key, defaultValue);
            getter((target, config) -> property.set(config::parameter, getter.apply(target)));
            setter((target, config) -> setter.accept(target, property.get(config::getParameter)));
            return this;
        }

        private <X> Builder<T> onProperty(nbbrd.io.text.Property<X> p, Function<T, X> getter, BiConsumer<T, X> setter) {
            getter((target, config) -> p.set(config::parameter, getter.apply(target)));
            setter((target, config) -> setter.accept(target, p.get(config::getParameter)));
            return this;
        }

        public Builder<T> onString(String key, String defaultValue, Function<T, String> getter, BiConsumer<T, String> setter) {
            nbbrd.io.text.Property<String> property = nbbrd.io.text.Property.of(key, defaultValue, nbbrd.io.text.Parser.onString(), nbbrd.io.text.Formatter.onString());
            return onProperty(property, getter, setter);
        }

        public <X extends Enum<X>> Builder<T> onEnum(String key, X defaultValue, Function<T, X> getter, BiConsumer<T, X> setter) {
            nbbrd.io.text.Property<X> property = nbbrd.io.text.Property.of(key, defaultValue, nbbrd.io.text.Parser.onEnum((Class<X>) defaultValue.getClass()), nbbrd.io.text.Formatter.onEnum());
            return onProperty(property, getter, setter);
        }

        public Builder<T> onFile(String key, File defaultValue, Function<T, File> getter, BiConsumer<T, File> setter) {
            nbbrd.io.text.Property<File> property = nbbrd.io.text.Property.of(key, defaultValue, nbbrd.io.text.Parser.onFile(), nbbrd.io.text.Formatter.onFile());
            return onProperty(property, getter, setter);
        }

        public <X> Builder<T> onConverter(Config.Converter<X> p, Function<T, X> getter, BiConsumer<T, X> setter) {
            getter((target, config) -> p.set(config, getter.apply(target)));
            setter((target, config) -> setter.accept(target, p.get(config)));
            return this;
        }
    }
}
