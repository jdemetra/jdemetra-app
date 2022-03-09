/*
 * Copyright 2017 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop.extra.sdmx.file;

import nbbrd.io.function.IOFunction;
import sdmxdl.file.SdmxFileManager;
import sdmxdl.file.SdmxFileSource;
import demetra.desktop.properties.NodePropertySetBuilder;
import demetra.desktop.properties.PropertySheetDialogBuilder;
import demetra.desktop.tsproviders.DataSourceProviderBuddy;
import demetra.desktop.ui.properties.FileLoaderFileFilter;
import demetra.desktop.util.Caches;
import demetra.tsp.extra.sdmx.file.SdmxFileBean;
import demetra.tsp.extra.sdmx.file.SdmxFileProvider;
import demetra.tsprovider.DataSet;
import demetra.tsprovider.FileLoader;
import demetra.tsprovider.HasFilePaths;
import internal.extra.sdmx.SdmxAutoCompletion;
import static internal.tsp.extra.sdmx.SdmxCubeItems.resolveFileSet;
import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Clock;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import nbbrd.design.DirectImpl;
import nbbrd.service.ServiceProvider;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.Lookup;
import sdmxdl.util.ext.MapCache;
import sdmxdl.xml.XmlFileSource;

/**
 *
 * @author Philippe Charles
 */
@DirectImpl
@ServiceProvider(DataSourceProviderBuddy.class)
public final class SdmxFileProviderBuddy implements DataSourceProviderBuddy {

    private final ConcurrentMap autoCompletionCache;

    public SdmxFileProviderBuddy() {
        this.autoCompletionCache = Caches.ttlCacheAsMap(Duration.ofMinutes(1));
        lookupProvider().ifPresent(o -> o.setSdmxManager(createManager()));
    }

    @Override
    public String getProviderName() {
        return SdmxFileProvider.NAME;
    }

    @Override
    public Image getIconOrNull(int type, boolean opened) {
        return SdmxAutoCompletion.getDefaultIcon().getImage();
    }

    @Override
    public Image getIconOrNull(DataSet dataSet, int type, boolean opened) {
        switch (dataSet.getKind()) {
            case COLLECTION:
                return ImageUtilities.loadImage("ec/nbdemetra/ui/nodes/folder.png", true);
            case SERIES:
                return ImageUtilities.loadImage("ec/nbdemetra/ui/nodes/chart_line.png", true);
            case DUMMY:
                return null;
        }
        return DataSourceProviderBuddy.super.getIconOrNull(dataSet, type, opened);
    }

    @Override
    public Image getIconOrNull(IOException ex, int type, boolean opened) {
        return ImageUtilities.loadImage("ec/nbdemetra/ui/nodes/exclamation-red.png", true);
    }

    @Override
    public boolean editBean(String title, Object bean) throws IntrospectionException {
        if (bean instanceof SdmxFileBean) {
            Optional<SdmxFileProvider> provider = lookupProvider();
            if (provider.isPresent()) {
                return editBean(title, (SdmxFileBean) bean, provider.get());
            }
        }
        return DataSourceProviderBuddy.super.editBean(title, bean);
    }

    private static SdmxFileManager createManager() {
        return SdmxFileManager.ofServiceLoader()
                .toBuilder()
                .eventListener((src, msg) -> StatusDisplayer.getDefault().setStatusText(msg))
                .cache(getCache())
                .build();
    }

    private static MapCache getCache() {
        return MapCache.of(
                Caches.softValuesCacheAsMap(),
                Caches.softValuesCacheAsMap(),
                Clock.systemDefaultZone()
        );
    }

    private static Optional<SdmxFileProvider> lookupProvider() {
        return Optional.ofNullable(Lookup.getDefault().lookup(SdmxFileProvider.class));
    }

    private boolean editBean(String title, SdmxFileBean bean, SdmxFileProvider o) {
        return new PropertySheetDialogBuilder()
                .title(title)
                .icon(getIconOrNull(BeanInfo.ICON_COLOR_16x16, false))
                .editSheet(createSheet(bean, o, o.getSdmxManager()));
    }

    @NbBundle.Messages({
        "bean.cache.description=Mechanism used to improve performance."})
    private Sheet createSheet(SdmxFileBean bean, FileLoader loader, SdmxFileManager manager) {
        Sheet result = new Sheet();
        NodePropertySetBuilder b = new NodePropertySetBuilder();
        result.put(withSource(b.reset("Source"), bean, loader).build());
        result.put(withOptions(b.reset("Options"), bean, loader, manager).build());
        return result;
    }

    @NbBundle.Messages({
        "bean.file.display=Data file",
        "bean.file.description=The path to the sdmx data file.",})
    private NodePropertySetBuilder withSource(NodePropertySetBuilder b, SdmxFileBean bean, FileLoader loader) {
        b.withFile()
                .select(bean, "file")
                .display(Bundle.bean_file_display())
                .description(Bundle.bean_file_description())
                .filterForSwing(new FileLoaderFileFilter(loader))
                .paths(loader.getPaths())
                .directories(false)
                .add();
        return b;
    }

    @NbBundle.Messages({
        "bean.structureFile.display=Structure file",
        "bean.structureFile.description=The path to the sdmx structure file.",
        "bean.dialect.display=Dialect",
        "bean.dialect.description=The name of the dialect used to parse the sdmx data file.",
        "bean.dimensions.display=Dataflow dimensions",
        "bean.dimensions.description=An optional comma-separated list of dimensions that defines the order used to hierarchise time series.",
        "bean.labelAttribute.display=Series label attribute",
        "bean.labelAttribute.description=An optional attribute that carries the label of time series."
    })
    private NodePropertySetBuilder withOptions(NodePropertySetBuilder b, SdmxFileBean bean, FileLoader loader, SdmxFileManager manager) {
        b.withFile()
                .select(bean, "structureFile")
                .display(Bundle.bean_structureFile_display())
                .description(Bundle.bean_structureFile_description())
                .filterForSwing(new FileLoaderFileFilter(loader))
                .paths(loader.getPaths())
                .directories(false)
                .add();

        b.withAutoCompletion()
                .select(bean, "dialect")
                .source(SdmxAutoCompletion.onDialects())
                .cellRenderer(SdmxAutoCompletion.getDialectRenderer())
                .display(Bundle.bean_dialect_display())
                .description(Bundle.bean_dialect_description())
                .add();

        Supplier<String> toSource = () -> tryResolveFileSet(loader, bean).map(IOFunction.unchecked(XmlFileSource.getFormatter()::formatToString)).orElse("");
        Supplier<String> toFlow = () -> tryResolveFileSet(loader, bean).map(SdmxFileSource::asDataflowRef).map(Object::toString).orElse("");

//        b.withAutoCompletion()
//                .select(bean, "dimensions", List.class, Joiner.on(',')::join, Splitter.on(',').trimResults().omitEmptyStrings()::splitToList)
//                .source(SdmxAutoCompletion.onDimensions(manager, toSource, toFlow, autoCompletionCache))
//                .separator(",")
//                .defaultValueSupplier(() -> SdmxAutoCompletion.getDefaultDimensionsAsString(manager, toSource, toFlow, autoCompletionCache, ","))
//                .cellRenderer(SdmxAutoCompletion.getDimensionsRenderer())
//                .display(Bundle.bean_dimensions_display())
//                .description(Bundle.bean_dimensions_description())
//                .add();
        b.withAutoCompletion()
                .select(bean, "labelAttribute")
                .display(Bundle.bean_labelAttribute_display())
                .description(Bundle.bean_labelAttribute_description())
                .add();
        return b;
    }

    public static Optional<SdmxFileSource> tryResolveFileSet(HasFilePaths paths, SdmxFileBean bean) {
        try {
            return Optional.of(resolveFileSet(paths, bean));
        } catch (FileNotFoundException ex) {
            return Optional.empty();
        }
    }
}
