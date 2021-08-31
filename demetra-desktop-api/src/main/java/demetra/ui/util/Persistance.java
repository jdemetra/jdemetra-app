package demetra.ui.util;

import demetra.ui.Config;
import nbbrd.io.text.*;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

@lombok.Value
@lombok.Builder
public class Persistance<T> {

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
            BooleanProperty property = BooleanProperty.of(key, defaultValue);
            getter((target, config) -> property.set(config::parameter, getter.apply(target)));
            setter((target, config) -> setter.accept(target, property.get(config::getParameter)));
            return this;
        }

        public Builder<T> onInt(String key, int defaultValue, Function<T, Integer> getter, BiConsumer<T, Integer> setter) {
            IntProperty property = IntProperty.of(key, defaultValue);
            getter((target, config) -> property.set(config::parameter, getter.apply(target)));
            setter((target, config) -> setter.accept(target, property.get(config::getParameter)));
            return this;
        }

        private <X> Builder<T> onProperty(Property<X> p, Function<T, X> getter, BiConsumer<T, X> setter) {
            getter((target, config) -> p.set(config::parameter, getter.apply(target)));
            setter((target, config) -> setter.accept(target, p.get(config::getParameter)));
            return this;
        }

        public Builder<T> onString(String key, String defaultValue, Function<T, String> getter, BiConsumer<T, String> setter) {
            Property<String> property = Property.of(key, defaultValue, Parser.onString(), Formatter.onString());
            return onProperty(property, getter, setter);
        }

        public <X extends Enum<X>> Builder<T> onEnum(String key, X defaultValue, Function<T, X> getter, BiConsumer<T, X> setter) {
            Property<X> property = Property.of(key, defaultValue, Parser.onEnum((Class<X>) defaultValue.getClass()), Formatter.onEnum());
            return onProperty(property, getter, setter);
        }

        public <X> Builder<T> onConverter(Config.Converter<X> p, Function<T, X> getter, BiConsumer<T, X> setter) {
            getter((target, config) -> p.set(config, getter.apply(target)));
            setter((target, config) -> setter.accept(target, p.get(config)));
            return this;
        }
    }
}
