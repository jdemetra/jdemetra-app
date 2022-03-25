/*
 * Copyright 2013 National Bank of Belgium
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
package demetra.desktop.extra.sdmx.web;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import demetra.desktop.TsManager;
import demetra.desktop.properties.NodePropertySetBuilder;
import demetra.desktop.tsproviders.DataSourceProviderBuddy;
import demetra.desktop.tsproviders.TsProviderProperties;
import demetra.desktop.util.Caches;
import demetra.timeseries.TsMoniker;
import demetra.tsp.extra.sdmx.web.SdmxWebBean;
import demetra.tsp.extra.sdmx.web.SdmxWebProvider;
import demetra.tsprovider.DataSet;
import demetra.tsprovider.DataSource;
import internal.extra.sdmx.SdmxAutoCompletion;
import internal.extra.sdmx.SdmxManagerFactory;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import nbbrd.service.ServiceProvider;
import nbbrd.design.DirectImpl;
import nbbrd.io.function.IORunnable;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import sdmxdl.DataflowRef;
import sdmxdl.Dimension;
import sdmxdl.web.SdmxWebManager;
import sdmxdl.web.SdmxWebSource;
import sdmxdl.xml.XmlWebSource;

/**
 * @author Philippe Charles
 */
@DirectImpl
@ServiceProvider(DataSourceProviderBuddy.class)
public final class SdmxWebProviderBuddy implements DataSourceProviderBuddy {

    private static final String SOURCE = "DOTSTAT";

    private final ConcurrentMap autoCompletionCache;

    private File customSources;

    private SdmxWebManager webManager;

    public SdmxWebProviderBuddy() {
        this.autoCompletionCache = Caches.ttlCacheAsMap(Duration.ofMinutes(1));
        this.customSources = new File("");
        this.webManager = SdmxManagerFactory.newWebManager();
        lookupProvider().ifPresent(o -> o.setSdmxManager(webManager));
    }

    private static Optional<SdmxWebProvider> lookupProvider() {
        return TsManager.getDefault().getProvider(SdmxWebProvider.class);
    }

    @Override
    public String getProviderName() {
        return SOURCE;
    }

    @Override
    public Image getIconOrNull(int type, boolean opened) {
        return SdmxAutoCompletion.getDefaultIcon().getImage();
    }

    @Override
    public Image getIconOrNull(DataSource dataSource, int type, boolean opened) {
        Optional<SdmxWebProvider> lookupProvider = lookupProvider();
        if (lookupProvider.isPresent()) {
            SdmxWebBean bean = lookupProvider.get().decodeBean(dataSource);
            Image result = getIcon(bean);
            if (result != null) {
                return result;
            }
        }
        return DataSourceProviderBuddy.super.getIconOrNull(dataSource, type, opened);
    }

    @Override
    public Image getIconOrNull(TsMoniker moniker, int type, boolean opened) {
        Optional<SdmxWebProvider> lookupProvider = lookupProvider();
        if (lookupProvider.isPresent()) {
            SdmxWebProvider provider = lookupProvider.get();
            Optional<DataSet> dataSet = provider.toDataSet(moniker);
            if (dataSet.isPresent()) {
                SdmxWebBean bean = provider.decodeBean(dataSet.get().getDataSource());
                Image result = getIcon(bean);
                if (result != null) {
                    return result;
                }
            }
        }
        return DataSourceProviderBuddy.super.getIconOrNull(moniker, type, opened);
    }

    @Override
    public Sheet getSheetOfBeanOrNull(Object bean) {
        return bean instanceof SdmxWebBean ? getSheetOrNull((SdmxWebBean) bean) : null;
    }

    private Sheet getSheetOrNull(SdmxWebBean bean) {
        return lookupProvider().map(provider -> newSheet(bean, provider)).orElse(null);
    }

    private Image getIcon(SdmxWebBean bean) {
        SdmxWebSource source = webManager.getSources().get(bean.getSource());
        return source != null
                ? ImageUtilities.icon2Image(SdmxAutoCompletion.FAVICONS.get(source.getWebsite(), IORunnable.noOp().asUnchecked()))
                : null;
    }

    private static List<SdmxWebSource> loadSources(File file) {
        if (file.exists()) {
            try {
                return XmlWebSource.getParser().parseFile(file);
            } catch (IOException ex) {
                StatusDisplayer.getDefault().setStatusText(ex.getMessage());
            }
        }
        return Collections.emptyList();
    }

    @NbBundle.Messages({
        "bean.cache.description=Mechanism used to improve performance."})
    private Sheet newSheet(SdmxWebBean bean, SdmxWebProvider provider) {
        Sheet result = new Sheet();
        NodePropertySetBuilder b = new NodePropertySetBuilder();
        result.put(withSource(b.reset("Source"), bean, provider).build());
        result.put(withOptions(b.reset("Options"), bean, provider).build());
        result.put(withCache(b.reset("Cache").description(Bundle.bean_cache_description()), bean).build());
        return result;
    }

    @NbBundle.Messages({
        "bean.source.display=Provider",
        "bean.source.description=The identifier of the service that provides data.",
        "bean.flow.display=Dataflow",
        "bean.flow.description=The identifier of a specific dataflow.",})
    private NodePropertySetBuilder withSource(NodePropertySetBuilder b, SdmxWebBean bean, SdmxWebProvider provider) {
        b.withAutoCompletion()
                .select("source", bean::getSource, bean::setSource)
                .servicePath(SdmxWebSource.class.getName())
                .display(Bundle.bean_source_display())
                .description(Bundle.bean_source_description())
                .add();

        Supplier<SdmxWebSource> toSource = () -> getWebSourceOrNull(bean, provider);

        SdmxAutoCompletion dataflow = SdmxAutoCompletion.onDataflow(provider.getSdmxManager(), toSource, autoCompletionCache);

        b.withAutoCompletion()
                .select("flow", bean::getFlow, bean::setFlow)
                .source(dataflow.getSource())
                .cellRenderer(dataflow.getRenderer())
                .display(Bundle.bean_flow_display())
                .description(Bundle.bean_flow_description())
                .add();

        return b;
    }

    @NbBundle.Messages({
        "bean.dimensions.display=Dataflow dimensions",
        "bean.dimensions.description=An optional comma-separated list of dimensions that defines the order used to hierarchise time series.",
        "bean.labelAttribute.display=Series label attribute",
        "bean.labelAttribute.description=An optional attribute that carries the label of time series."
    })
    private NodePropertySetBuilder withOptions(NodePropertySetBuilder b, SdmxWebBean bean, SdmxWebProvider provider) {
        Supplier<SdmxWebSource> toSource = () -> getWebSourceOrNull(bean, provider);
        Supplier<DataflowRef> toFlow = () -> getDataflowRefOrNull(bean);

        SdmxAutoCompletion dimension = SdmxAutoCompletion.onDimension(provider.getSdmxManager(), toSource, toFlow, autoCompletionCache);

        b.withAutoCompletion()
                .select(bean, "dimensions", List.class,
                        Joiner.on(',')::join, Splitter.on(',').trimResults().omitEmptyStrings()::splitToList)
                .source(dimension.getSource())
                .cellRenderer(dimension.getRenderer())
                .separator(",")
                .defaultValueSupplier(() -> dimension.getSource().getValues("").stream().map(Dimension.class::cast).sorted(Comparator.comparingInt(Dimension::getPosition)).map(Dimension::getId).collect(Collectors.joining(",")))
                .display(Bundle.bean_dimensions_display())
                .description(Bundle.bean_dimensions_description())
                .add();

        SdmxAutoCompletion attribute = SdmxAutoCompletion.onAttribute(provider.getSdmxManager(), toSource, toFlow, autoCompletionCache);
        
        b.withAutoCompletion()
                .select("labelAttribute", bean::getLabelAttribute, bean::setLabelAttribute)
                .source(attribute.getSource())
                .cellRenderer(attribute.getRenderer())
                .display(Bundle.bean_labelAttribute_display())
                .description(Bundle.bean_labelAttribute_description())
                .add();

        return b;
    }

    private NodePropertySetBuilder withCache(NodePropertySetBuilder b, SdmxWebBean bean) {
        TsProviderProperties.addBulkCube(b, bean::getCache, bean::setCache);
        return b;
    }

    private static SdmxWebSource getWebSourceOrNull(SdmxWebBean bean, SdmxWebProvider provider) {
        return provider.getSdmxManager().getSources().get(bean.getSource());
    }

    private static DataflowRef getDataflowRefOrNull(SdmxWebBean bean) {
        try {
            return DataflowRef.parse(bean.getFlow());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
