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
package ec.nbdemetra.db;

import ec.nbdemetra.ui.properties.DhmsPropertyEditor;
import ec.nbdemetra.ui.properties.FileLoaderFileFilter;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.tsproviders.AbstractDataSourceProviderBuddy;
import ec.tss.tsproviders.IFileLoader;
import ec.tss.tsproviders.TsProviders;
import ec.tss.tsproviders.db.DbBean;
import ec.tss.tsproviders.db.DbBean.BulkBean;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tstoolkit.timeseries.TsAggregationType;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.util.completion.AutoCompletionSource;
import ec.util.completion.AutoCompletionSources;
import ec.util.completion.swing.JAutoCompletion;
import java.awt.Image;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;

/**
 * An abstract provider buddy that targets database providers.
 *
 * @author Philippe Charles
 * @param <BEAN>
 */
public abstract class DbProviderBuddy<BEAN extends DbBean> extends AbstractDataSourceProviderBuddy {

    /**
     * Checks if the provider can use files as data input.
     * <p>
     * Some databases are stored exclusively in files while others can live in
     * memory or remotely. This property allows the user to search the file
     * system for a database file.
     *
     * @return true if the bean represents a file; false otherwise
     */
    abstract protected boolean isFile();

    @Override
    public Image getIcon(int type, boolean opened) {
        return DbIcon.DATABASE.getImageIcon().getImage();
    }

    @Override
    protected List<Sheet.Set> createSheetSets(Object bean) throws IntrospectionException {
        return bean instanceof DbBean
                ? createSheetSets((DbBean) bean)
                : super.createSheetSets(bean);
    }

    private List<Sheet.Set> createSheetSets(DbBean bean) {
        List<Sheet.Set> result = new ArrayList<>();
        NodePropertySetBuilder b = new NodePropertySetBuilder();
        result.add(withSource(b.reset("Source"), (BEAN) bean).build());
        result.add(withOptions(b.reset("Options"), (BEAN) bean).build());
        if (bean instanceof BulkBean) {
            result.add(withCache(b.reset("Cache").description("Mechanism used to improve performance."), (BEAN) bean).build());
        }
        return result;
    }

    /**
     * Adds the {@link DbBean#dbName} property to the specified builder.
     * <br>Override this to customize this property.
     *
     * @param b
     * @param bean
     * @return
     */
    @NbBundle.Messages({
        "bean.dbName.display=Data source name",
        "bean.dbName.description=Data structure describing the connection to the database."})
    @Nonnull
    protected NodePropertySetBuilder withDbName(@Nonnull NodePropertySetBuilder b, @Nonnull BEAN bean) {
        return b.withAutoCompletion()
                .select(bean, "dbName")
                .source(getDbSource(bean))
                .cellRenderer(getDbRenderer(bean))
                .display(Bundle.bean_dbName_display())
                .description(Bundle.bean_dbName_description())
                .add();
    }

    @NbBundle.Messages({
        "bean.file.display=Database file",
        "bean.file.description=The path to the database file."})
    @Nonnull
    protected NodePropertySetBuilder withFileName(@Nonnull NodePropertySetBuilder b, @Nonnull BEAN bean) {
        return TsProviders.lookup(IFileLoader.class, getProviderName())
                .toJavaUtil()
                .map(o -> b.withFile()
                        .select(bean, "file")
                        .filterForSwing(new FileLoaderFileFilter(o))
                        .paths(o.getPaths())
                        .directories(false)
                        .display(Bundle.bean_file_display())
                        .description(Bundle.bean_file_description())
                        .add())
                .orElse(b);
    }

    @NbBundle.Messages({
        "bean.tableName.display=Table name",
        "bean.tableName.description=The name of the table (or view) that contains observations.",
        "bean.dimColumns.display=Dimension columns",
        "bean.dimColumns.description=A comma-separated list of column names that defines the dimensions of the table.",
        "bean.periodColumn.display=Period column",
        "bean.periodColumn.description=A column name that defines the period of an observation.",
        "bean.valueColumn.display=Value column",
        "bean.valueColumn.description=A column name that defines the value of an observation.",
        "bean.versionColumn.display=Version column",
        "bean.versionColumn.description=An optional column name that defines the version of an observation.",})
    @Nonnull
    protected NodePropertySetBuilder withSource(@Nonnull NodePropertySetBuilder b, @Nonnull BEAN bean) {
        AutoCompletionSource columns = getColumnSource(bean);
        ListCellRenderer columnCellRenderer = getColumnRenderer(bean);

        if (isFile()) {
            withFileName(b, bean);
        } else {
            withDbName(b, bean);
        }
        b.withAutoCompletion()
                .select(bean, "tableName")
                .source(getTableSource(bean))
                .cellRenderer(getTableRenderer(bean))
                .display(Bundle.bean_tableName_display())
                .description(Bundle.bean_tableName_description())
                .add();
        b.withAutoCompletion()
                .select(bean, "dimColumns")
                .source(columns)
                .separator(",")
                .defaultValueSupplier(() -> columns.getValues("").stream().map(columns::toString).collect(Collectors.joining(",")))
                .cellRenderer(columnCellRenderer)
                .display(Bundle.bean_dimColumns_display())
                .description(Bundle.bean_dimColumns_description())
                .add();
        b.withAutoCompletion()
                .select(bean, "periodColumn")
                .source(columns)
                .cellRenderer(columnCellRenderer)
                .display(Bundle.bean_periodColumn_display())
                .description(Bundle.bean_periodColumn_description())
                .add();
        b.withAutoCompletion()
                .select(bean, "valueColumn")
                .source(columns)
                .cellRenderer(columnCellRenderer)
                .display(Bundle.bean_valueColumn_display())
                .description(Bundle.bean_valueColumn_description())
                .add();
        b.withAutoCompletion()
                .select(bean, "versionColumn")
                .source(columns)
                .cellRenderer(columnCellRenderer)
                .display(Bundle.bean_versionColumn_display())
                .description(Bundle.bean_versionColumn_description())
                .add();
        return b;
    }

    @NbBundle.Messages({
        "bean.dataFormat.display=Data format",
        "bean.dataFormat.description=The format used to parse dates and numbers from character strings.",
        "bean.frequency.display=Frequency",
        "bean.frequency.description=The frequency of the observations in the table. An undefined frequency allows the provider to guess it.",
        "bean.aggregationType.display=Aggregation type",
        "bean.aggregationType.description=The aggregation method to use when a frequency is defined."})
    @Nonnull
    protected NodePropertySetBuilder withOptions(@Nonnull NodePropertySetBuilder b, @Nonnull BEAN bean) {
        b.with(DataFormat.class)
                .select(bean, "dataFormat")
                .display(Bundle.bean_dataFormat_display())
                .description(Bundle.bean_dataFormat_description())
                .add();
        b.withEnum(TsFrequency.class)
                .select(bean, "frequency")
                .display(Bundle.bean_frequency_display())
                .description(Bundle.bean_frequency_description())
                .add();
        b.withEnum(TsAggregationType.class)
                .select(bean, "aggregationType")
                .display(Bundle.bean_aggregationType_display())
                .description(Bundle.bean_aggregationType_description())
                .add();
        return b;
    }

    @NbBundle.Messages({
        "bean.cacheDepth.display=Depth",
        "bean.cacheDepth.description=The data retrieval depth. It is always more performant to get one big chunk of data instead of several smaller parts. The downside of it is the increase of memory usage. Setting this value to zero disables the cache.",
        "bean.cacheTtl.display=Time to live",
        "bean.cacheTtl.description=The lifetime of the data stored in the cache. Setting this value to zero disables the cache."})
    @Nonnull
    protected NodePropertySetBuilder withCache(@Nonnull NodePropertySetBuilder b, @Nonnull BEAN bean) {
        b.withInt()
                .select(bean, "cacheDepth")
                .display(Bundle.bean_cacheDepth_display())
                .description(Bundle.bean_cacheDepth_description())
                .min(0)
                .add();
        b.with(long.class)
                .select(bean, "cacheTtl")
                .editor(DhmsPropertyEditor.class)
                .display(Bundle.bean_cacheTtl_display())
                .description(Bundle.bean_cacheTtl_description())
                .add();
        return b;
    }

    /**
     * Gets an auto completion source for a bean field such as
     * {@link DbBean#dbName}.
     * <p>
     * The default source is an empty one. Override this method to provide your
     * own source.
     *
     * @param bean
     * @return a non-null auto completion source
     * @see DbBean
     * @see JAutoCompletion#setSource(ec.util.completion.AutoCompletionSource)
     */
    @Nonnull
    protected AutoCompletionSource getDbSource(@Nonnull BEAN bean) {
        return AutoCompletionSources.empty();
    }

    /**
     * Gets an auto completion source for a bean field such as
     * {@link DbBean#tableName}.
     * <p>
     * The default source is an empty one. Override this method to provide your
     * own source.
     *
     * @param bean
     * @return a non-null auto completion source
     * @see DbBean
     * @see JAutoCompletion#setSource(ec.util.completion.AutoCompletionSource)
     */
    @Nonnull
    protected AutoCompletionSource getTableSource(@Nonnull BEAN bean) {
        return AutoCompletionSources.empty();
    }

    /**
     * Gets an auto completion source for a bean field such as
     * {@link DbBean#periodColumn}.
     * <p>
     * The default source is an empty one. Override this method to provide your
     * own source.
     *
     * @param bean
     * @return a non-null auto completion source
     * @see DbBean
     * @see JAutoCompletion#setSource(ec.util.completion.AutoCompletionSource)
     */
    @Nonnull
    protected AutoCompletionSource getColumnSource(@Nonnull BEAN bean) {
        return AutoCompletionSources.empty();
    }

    /**
     * Gets an auto completion renderer for a bean field such as
     * {@link DbBean#dbName}.
     * <p>
     * The default renderer just uses {@link Object#toString()}. Override this
     * method to provide your own renderer.
     *
     * @param bean
     * @return a non-null auto completion renderer
     * @see DbBean
     * @see JList#setCellRenderer(javax.swing.ListCellRenderer)
     */
    @Nonnull
    protected ListCellRenderer getDbRenderer(@Nonnull BEAN bean) {
        return new DefaultListCellRenderer();
    }

    /**
     * Gets an auto completion renderer for a bean field such as
     * {@link DbBean#tableName}.
     * <p>
     * The default renderer just uses {@link Object#toString()}. Override this
     * method to provide your own renderer.
     *
     * @param bean
     * @return a non-null auto completion renderer
     * @see DbBean
     * @see JList#setCellRenderer(javax.swing.ListCellRenderer)
     */
    @Nonnull
    protected ListCellRenderer getTableRenderer(@Nonnull BEAN bean) {
        return new DefaultListCellRenderer();
    }

    /**
     * Gets an auto completion renderer for a bean field such as
     * {@link DbBean#periodColumn}.
     * <p>
     * The default renderer just uses {@link Object#toString()}. Override this
     * method to provide your own renderer.
     *
     * @param bean
     * @return a non-null auto completion renderer
     * @see DbBean
     * @see JList#setCellRenderer(javax.swing.ListCellRenderer)
     */
    @Nonnull
    protected ListCellRenderer getColumnRenderer(@Nonnull BEAN bean) {
        return new DefaultListCellRenderer();
    }
}
