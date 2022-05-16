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

import demetra.desktop.Config;
import demetra.desktop.Persistable;
import demetra.desktop.TsManager;
import demetra.desktop.actions.Configurable;
import demetra.desktop.properties.PropertySheetDialogBuilder;
import demetra.desktop.tsproviders.DataSourceProviderBuddy;
import demetra.timeseries.TsMoniker;
import demetra.tsp.extra.sdmx.web.SdmxWebBean;
import demetra.tsp.extra.sdmx.web.SdmxWebProvider;
import demetra.tsprovider.DataSet;
import demetra.tsprovider.DataSource;
import static ec.util.chart.impl.TangoColorScheme.DARK_ORANGE;
import static ec.util.chart.swing.SwingColorSchemeSupport.rgbToColor;
import ec.util.various.swing.FontAwesome;
import internal.extra.sdmx.SdmxAutoCompletion;
import java.awt.Image;
import java.io.IOException;
import java.util.Optional;
import nbbrd.service.ServiceProvider;
import nbbrd.design.DirectImpl;
import nbbrd.io.function.IORunnable;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import sdmxdl.Connection;
import sdmxdl.Feature;
import sdmxdl.web.SdmxWebSource;

/**
 * @author Philippe Charles
 */
@DirectImpl
@ServiceProvider(DataSourceProviderBuddy.class)
public final class SdmxWebProviderBuddy implements DataSourceProviderBuddy, Configurable, Persistable {

    private static final String SOURCE = "DOTSTAT";

    private SdmxWebConfiguration configuration;

    public SdmxWebProviderBuddy() {
        this.configuration = new SdmxWebConfiguration();
        updateProvider();
    }

    private void updateProvider() {
        lookupProvider().ifPresent(provider -> provider.setSdmxManager(configuration.toSdmxWebManager()));
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
            SdmxWebProvider provider = lookupProvider.get();
            SdmxWebBean bean = provider.decodeBean(dataSource);
            SdmxWebSource source = provider.getSdmxManager().getSources().get(bean.getSource());
            if (source != null) {
                Image result = getSourceIcon(provider, source);
                return supportsDataQueryDetail(provider, source)
                        ? result
                        : ImageUtilities.mergeImages(result, getWarningBadge(), 13, 8);
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
                SdmxWebSource source = provider.getSdmxManager().getSources().get(bean.getSource());
                if (source != null) {
                    return getSourceIcon(provider, source);
                }
            }
        }
        return DataSourceProviderBuddy.super.getIconOrNull(moniker, type, opened);
    }

    @Override
    public Sheet getSheetOfBeanOrNull(Object bean) {
        return bean instanceof SdmxWebBean ? getSheetOrNull((SdmxWebBean) bean) : null;
    }

    @Override
    public void configure() {
        SdmxWebConfiguration editable = SdmxWebConfiguration.copyOf(configuration);
        PropertySheetDialogBuilder editor = new PropertySheetDialogBuilder()
                .title("Configure " + lookupProvider().map(SdmxWebProvider::getDisplayName).orElse(""))
                .icon(SdmxAutoCompletion.getDefaultIcon());
        if (editor.editSheet(editable.toSheet())) {
            configuration = editable;
            updateProvider();
        }
    }

    @Override
    public Config getConfig() {
        return SdmxWebConfiguration.PERSISTENCE.loadConfig(configuration);
    }

    @Override
    public void setConfig(Config config) throws IllegalArgumentException {
        SdmxWebConfiguration.PERSISTENCE.storeConfig(configuration, config);
        updateProvider();
    }

    private Sheet getSheetOrNull(SdmxWebBean bean) {
        return lookupProvider().map(provider -> SdmxWebBeanSupport.newSheet(bean, provider)).orElse(null);
    }

    private static Image getSourceIcon(SdmxWebProvider provider, SdmxWebSource source) {
        return ImageUtilities.icon2Image(SdmxAutoCompletion.FAVICONS.get(source.getWebsite(), IORunnable.noOp().asUnchecked()));
    }

    private static boolean supportsDataQueryDetail(SdmxWebProvider provider, SdmxWebSource source) {
        try ( Connection conn = provider.getSdmxManager().getConnection(source)) {
            return conn.getSupportedFeatures().contains(Feature.DATA_QUERY_DETAIL);
        } catch (IOException ex) {
            return false;
        }
    }

    private static Image getWarningBadge() {
        return FontAwesome.FA_EXCLAMATION_TRIANGLE.getImage(rgbToColor(DARK_ORANGE), 8f);
    }

    private static Optional<SdmxWebProvider> lookupProvider() {
        return TsManager.getDefault().getProvider(SdmxWebProvider.class);
    }
}
