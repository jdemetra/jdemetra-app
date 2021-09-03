package demetra.desktop;

import demetra.desktop.design.GlobalService;
import demetra.desktop.util.CollectionSupplier;
import demetra.desktop.util.Collections2;
import demetra.desktop.util.LazyGlobalService;
import ec.util.chart.ColorScheme;
import ec.util.chart.impl.SmartColorScheme;
import ec.util.chart.swing.SwingColorSchemeSupport;
import ec.util.various.swing.OnEDT;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.WeakHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@GlobalService
public final class ColorSchemeManager {

    @NonNull
    public static ColorSchemeManager getDefault() {
        return LazyGlobalService.get(ColorSchemeManager.class, ColorSchemeManager::new);
    }

    private final CollectionSupplier<ColorScheme> providers = CollectionSupplier.ofLookup(ColorScheme.class);
    private final WeakHashMap<String, SwingColorSchemeSupport> cache = new WeakHashMap<>();

    private ColorSchemeManager() {
    }

    @NonNull
    public ColorScheme getMainColorScheme() {
        String mainColorSchemeName = DemetraOptions.getDefault().getColorSchemeName();
        return providers
                .stream()
                .filter(Collections2.compose(Predicate.isEqual(mainColorSchemeName), ColorScheme::getName))
                .map(ColorScheme.class::cast)
                .findFirst()
                .orElseGet(SmartColorScheme::new);
    }

    @NonNull
    public List<? extends ColorScheme> getColorSchemes() {
        return providers
                .stream()
                .sorted(Comparator.comparing(ColorScheme::getDisplayName))
                .collect(Collectors.toList());
    }

    @OnEDT
    @NonNull
    public SwingColorSchemeSupport getSupport(@Nullable ColorScheme colorScheme) {
        ColorScheme result = colorScheme != null ? colorScheme : getMainColorScheme();
        return cache.computeIfAbsent(result.getName(), name -> SwingColorSchemeSupport.from(result));
    }
}
