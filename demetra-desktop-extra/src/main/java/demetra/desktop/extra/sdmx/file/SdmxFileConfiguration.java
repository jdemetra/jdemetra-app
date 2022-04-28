package demetra.desktop.extra.sdmx.file;

import demetra.desktop.properties.NodePropertySetBuilder;
import demetra.desktop.util.Caches;
import demetra.desktop.util.Persistence;
import java.time.Clock;
import java.util.Locale;
import java.util.function.BiConsumer;
import nbbrd.design.MightBeGenerated;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.Sheet;
import sdmxdl.LanguagePriorityList;
import sdmxdl.ext.Cache;
import sdmxdl.file.SdmxFileManager;
import sdmxdl.file.SdmxFileSource;
import sdmxdl.provider.ext.MapCache;

@lombok.Data
public class SdmxFileConfiguration {

    private static final String LANGUAGES_PROPERTY = "languages";
    private static final String DEFAULT_LANGUAGES = null;
    private String languages = DEFAULT_LANGUAGES;

    private static final String NO_CACHE_PROPERTY = "noCache";
    private static final boolean DEFAULT_NO_CACHE = false;
    private boolean noCache = DEFAULT_NO_CACHE;

    @MightBeGenerated
    public static SdmxFileConfiguration copyOf(SdmxFileConfiguration bean) {
        SdmxFileConfiguration result = new SdmxFileConfiguration();
        result.languages = bean.languages;
        result.noCache = bean.noCache;
        return result;
    }

    public SdmxFileManager toSdmxFileManager() {
        return SdmxFileManager.ofServiceLoader()
                .toBuilder()
                .languages(toLanguages())
                .eventListener(toEventListener())
                .cache(toCache())
                .build();
    }

    private LanguagePriorityList toLanguages() throws IllegalArgumentException {
        return languages != null ? LanguagePriorityList.parse(languages) : LanguagePriorityList.ANY;
    }

    private BiConsumer<? super SdmxFileSource, ? super String> toEventListener() {
        return (source, message) -> StatusDisplayer.getDefault().setStatusText(message);
    }

    private Cache toCache() {
        if (noCache) {
            return Cache.noOp();
        }
        return MapCache.of(
                Caches.softValuesCacheAsMap(),
                Caches.softValuesCacheAsMap(),
                Clock.systemDefaultZone()
        );
    }

    Sheet toSheet() {
        Sheet result = new Sheet();
        NodePropertySetBuilder b = new NodePropertySetBuilder();

        b.withAutoCompletion()
                .select(this, LANGUAGES_PROPERTY)
                .servicePath(Locale.class.getName())
                .separator(",")
                .display("Languages")
                .description("Language priority list")
                .add();
        result.put(b.build());

        b.reset("Other");
        b.withBoolean()
                .select(this, NO_CACHE_PROPERTY)
                .display("No cache")
                .description("Disable caching")
                .add();
        result.put(b.build());

        return result;
    }

    @MightBeGenerated
    static final Persistence<SdmxFileConfiguration> PERSISTENCE = Persistence
            .builderOf(SdmxFileConfiguration.class)
            .name("INSTANCE")
            .version("VERSION")
            .onString(LANGUAGES_PROPERTY, DEFAULT_LANGUAGES, SdmxFileConfiguration::getLanguages, SdmxFileConfiguration::setLanguages)
            .onBoolean(NO_CACHE_PROPERTY, DEFAULT_NO_CACHE, SdmxFileConfiguration::isNoCache, SdmxFileConfiguration::setNoCache)
            .build();
}
