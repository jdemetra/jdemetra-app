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
package demetra.desktop.tsproviders;

import demetra.desktop.DemetraIcons;
import demetra.desktop.actions.Configurable;
import demetra.desktop.beans.BeanEditor;
import demetra.desktop.design.GlobalService;
import demetra.desktop.util.CollectionSupplier;
import demetra.desktop.util.FrozenTsHelper;
import demetra.desktop.util.LazyGlobalService;
import demetra.timeseries.TsMoniker;
import demetra.tsprovider.DataSet;
import demetra.tsprovider.DataSource;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Optional;

/**
 * @author Philippe Charles
 */
@GlobalService
public final class DataSourceProviderBuddySupport {

    @NonNull
    public static DataSourceProviderBuddySupport getDefault() {
        return LazyGlobalService.get(DataSourceProviderBuddySupport.class, DataSourceProviderBuddySupport::new);
    }

    private final CollectionSupplier<DataSourceProviderBuddy> providers;
    private final DataSourceProviderBuddy fallback;
    private final Image defaultImage;

    private DataSourceProviderBuddySupport() {
        this.providers = DataSourceProviderBuddyLoader::get;
        this.fallback = new DataSourceProviderBuddy() {
            @Override
            public @NonNull String getProviderName() {
                return "fallback";
            }
        };
        this.defaultImage = DemetraIcons.DOCUMENT_16.getImageIcon().getImage();
    }

    private Image getOrDefault(Image result) {
        return result != null ? result : defaultImage;
    }

    @NonNull
    private DataSourceProviderBuddy getByName(@Nullable String providerName) {
        String tmp = providerName == null ? "" : providerName;
        return providers.stream()
                .filter(o -> o.getProviderName().equals(tmp))
                .map(DataSourceProviderBuddy.class::cast)
                .findFirst()
                .orElse(fallback);
    }

    /**
     * Gets an icon for a provider.
     *
     * @param providerName
     * @param type
     * @param opened
     * @return an icon
     * @since 2.2.0
     */
    @NonNull
    public Image getImage(@NonNull String providerName, int type, boolean opened) {
        DataSourceProviderBuddy buddy = getByName(providerName);
        return getOrDefault(buddy.getIconOrNull(type, opened));
    }

    @NonNull
    public Icon getIcon(@NonNull String providerName, int type, boolean opened) {
        return ImageUtilities.image2Icon(getImage(providerName, type, opened));
    }

    @NonNull
    public Sheet createSheet(@NonNull String providerName) {
        DataSourceProviderBuddy buddy = getByName(providerName);
        return buddy.createSheet();
    }

    @NonNull
    public BeanEditor getBeanEditor(@NonNull String providerName, @NonNull String title) {
        DataSourceProviderBuddy buddy = getByName(providerName);
        return bean -> buddy.editBean(title, bean);
    }

    /**
     * Gets a configurable for a provider.
     *
     * @param providerName
     * @return an optional configurable
     * @since 2.2.0
     */
    @NonNull
    public Optional<Configurable> getConfigurable(@NonNull String providerName) {
        DataSourceProviderBuddy buddy = getByName(providerName);
        return buddy instanceof Configurable ? Optional.of((Configurable) buddy) : Optional.empty();
    }

    /**
     * Gets an icon for a data source.
     *
     * @param dataSource
     * @param type
     * @param opened
     * @return an icon
     * @since 2.2.0
     */
    @NonNull
    public Image getImage(@NonNull DataSource dataSource, int type, boolean opened) {
        DataSourceProviderBuddy buddy = getByName(dataSource.getProviderName());
        return getOrDefault(buddy.getIconOrNull(dataSource, type, opened));
    }

    @NonNull
    public Icon getIcon(@NonNull DataSource dataSource, int type, boolean opened) {
        return ImageUtilities.image2Icon(getImage(dataSource, type, opened));
    }

    @NonNull
    public Sheet createSheet(@NonNull DataSource dataSource) {
        DataSourceProviderBuddy buddy = getByName(dataSource.getProviderName());
        return buddy.createSheet(dataSource);
    }

    /**
     * Gets an icon for a data set.
     *
     * @param dataSet
     * @param type
     * @param opened
     * @return an icon
     * @since 2.2.0
     */
    @NonNull
    public Image getImage(@NonNull DataSet dataSet, int type, boolean opened) {
        DataSourceProviderBuddy buddy = getByName(dataSet.getDataSource().getProviderName());
        return getOrDefault(buddy.getIconOrNull(dataSet, type, opened));
    }

    @NonNull
    public Icon getIcon(@NonNull DataSet dataSet, int type, boolean opened) {
        return ImageUtilities.image2Icon(getImage(dataSet, type, opened));
    }

    @NonNull
    public Sheet createSheet(@NonNull DataSet dataSet) {
        DataSourceProviderBuddy buddy = getByName(dataSet.getDataSource().getProviderName());
        return buddy.createSheet(dataSet);
    }

    /**
     * Gets an icon for an exception thrown by a provider.
     *
     * @param providerName
     * @param ex
     * @param type
     * @param opened
     * @return an icon
     * @since 2.2.0
     */
    @NonNull
    public Image getImage(@NonNull String providerName, @NonNull IOException ex, int type, boolean opened) {
        DataSourceProviderBuddy buddy = getByName(providerName);
        return getOrDefault(buddy.getIconOrNull(ex, type, opened));
    }

    @NonNull
    public Icon getIcon(@NonNull String providerName, @NonNull IOException ex, int type, boolean opened) {
        return ImageUtilities.image2Icon(getImage(providerName, ex, type, opened));
    }

    @NonNull
    public Sheet createSheet(@NonNull String providerName, @NonNull IOException ex) {
        DataSourceProviderBuddy buddy = getByName(providerName);
        return buddy.createSheet(ex);
    }

    /**
     * Gets an icon for a moniker.
     *
     * @param moniker
     * @param type
     * @param opened
     * @return an icon
     * @since 2.2.0
     */
    @NonNull
    public Image getImage(@NonNull TsMoniker moniker, int type, boolean opened) {
        TsMoniker original = FrozenTsHelper.getOriginalMoniker(moniker);
        if (original != null) {
            DataSourceProviderBuddy buddy = getByName(original.getSource());
            return getOrDefault(buddy.getIconOrNull(moniker, type, opened));
        } else {
            return defaultImage;
        }
    }

    @NonNull
    public Icon getIcon(@NonNull TsMoniker moniker, int type, boolean opened) {
        return ImageUtilities.image2Icon(getImage(moniker, type, opened));
    }
}
