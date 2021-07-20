/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.nodes;

import demetra.ui.nodes.AbstractNodeBuilder;
import demetra.bridge.TsConverter;
import demetra.timeseries.TsPeriod;
import demetra.timeseries.TsUnit;
import demetra.timeseries.Ts;
import demetra.timeseries.TsInformationType;
import demetra.timeseries.TsMoniker;
import demetra.tsprovider.DataSourceProvider;
import demetra.tsprovider.util.MultiLineNameUtil;
import demetra.ui.TsManager;
import demetra.ui.components.parts.HasTsCollection;
import demetra.ui.components.parts.HasTsCollection.TsUpdateMode;
import demetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.tsproviders.DataSourceProviderBuddySupport;
import internal.FrozenTsHelper;
import demetra.ui.components.TsSelectionBridge;
import java.awt.Image;
import java.beans.PropertyVetoException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;
import javax.swing.JComponent;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Philippe Charles
 */
public class ControlNode {

    private static final Node VOID = new AbstractNodeBuilder().name("void").build();

    public static Node onComponentOpened(final ExplorerManager mgr, final HasTsCollection view) {
        if (view instanceof JComponent) {
            ((JComponent) view).addPropertyChangeListener(evt -> {
                switch (evt.getPropertyName()) {
                    case TsSelectionBridge.TS_SELECTION_PROPERTY:
                        onSelectionChange(mgr, view);
                        break;
                    case HasTsCollection.TS_COLLECTION_PROPERTY:
                        onCollectionChange(mgr, view);
                        break;
                }
            });
        }
        onCollectionChange(mgr, view);
        onSelectionChange(mgr, view);

        return VOID;
    }

    private static void onSelectionChange(ExplorerManager mgr, HasTsCollection view) {
        if (view.getTsSelectionModel().isSelectionEmpty()) {
            selectSingleIfReadonly(mgr, view);
        } else {
            Ts[] tss = view.getTsSelectionStream().toArray(Ts[]::new);
            selectNodes(mgr, tss);
        }
    }

    private static void onCollectionChange(ExplorerManager mgr, HasTsCollection view) {
        mgr.setRootContext(new TsCollectionNode(view.getTsCollection()));
        selectSingleIfReadonly(mgr, view);
    }

    private static boolean isReadonly(HasTsCollection view) {
        return view.getTsUpdateMode() == TsUpdateMode.None;
    }

    private static Ts getSingleOrNull(HasTsCollection view) {
        return view.getTsCollection().stream().findFirst().orElse(null);
    }

    private static void selectSingleIfReadonly(ExplorerManager mgr, HasTsCollection view) {
        if (isReadonly(view)) {
            Ts single = getSingleOrNull(view);
            if (single != null) {
                selectNodes(mgr, single);
                return;
            }
        }
        selectNodes(mgr);
    }

    private static void selectNodes(ExplorerManager mgr, Ts... tss) {
        try {
            if (tss.length == 0) {
                mgr.setSelectedNodes(new Node[]{mgr.getRootContext()});
            } else {
                HashSet<Ts> selection = new HashSet<>(Arrays.asList(tss));
                mgr.setSelectedNodes(Arrays.stream(mgr.getRootContext().getChildren().getNodes())
                        .filter(o -> selection.contains(o.getLookup().lookup(Ts.class)))
                        .toArray(Node[]::new));
            }
        } catch (PropertyVetoException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private static final class TsCollectionNode extends AbstractNode {

        TsCollectionNode(demetra.timeseries.TsCollection col) {
            super(new TsCollectionChildren(col), Lookups.singleton(col));
            setName(col.getName());
            setDisplayName(MultiLineNameUtil.last(col.getName()));
            setShortDescription(MultiLineNameUtil.toHtml(col.getName()));
        }

        @Override
        protected Sheet createSheet() {
            demetra.timeseries.TsCollection col = getLookup().lookup(demetra.timeseries.TsCollection.class);
            NodePropertySetBuilder b = new NodePropertySetBuilder();
            Sheet result = new Sheet();
            result.put(getDefinitionSheetSet(col, b));
            result.put(getMetaSheetSet(col, b));
            return result;
        }

        @lombok.AllArgsConstructor
        static class TsCollectionChildren extends Children.Keys<demetra.timeseries.Ts> {

            final demetra.timeseries.TsCollection col;

            @Override
            protected void addNotify() {
                setKeys(col.getItems());
            }

            @Override
            protected void removeNotify() {
                setKeys(Collections.emptyList());
            }

            @Override
            protected Node[] createNodes(demetra.timeseries.Ts key) {
                return new Node[]{new TsNode(key)};
            }
        }
    }

    private static final class TsNode extends AbstractNode {

        TsNode(demetra.timeseries.Ts ts) {
            super(Children.LEAF, Lookups.singleton(ts));
            setName(ts.getName());
            setDisplayName(MultiLineNameUtil.last(ts.getName()));
            setShortDescription(MultiLineNameUtil.toHtml(ts.getName()));
        }

        private Optional<Image> lookupIcon(int type, boolean opened) {
            demetra.timeseries.Ts ts = getLookup().lookup(demetra.timeseries.Ts.class);
            return DataSourceProviderBuddySupport.getDefault().getIcon(ts.getMoniker(), type, opened);
        }

        @Override
        public Image getOpenedIcon(int type) {
            return lookupIcon(type, true).orElseGet(() -> super.getOpenedIcon(type));
        }

        @Override
        public Image getIcon(int type) {
            return lookupIcon(type, false).orElseGet(() -> super.getIcon(type));
        }

        @Override
        protected Sheet createSheet() {
            demetra.timeseries.Ts ts = getLookup().lookup(demetra.timeseries.Ts.class);
            NodePropertySetBuilder b = new NodePropertySetBuilder();
            Sheet result = new Sheet();
            result.put(getDefinitionSheetSet(ts, b));
            result.put(getDataSheetSet(ts, b));
            result.put(getMetaSheetSet(ts, b));
            return result;
        }
    }

    private static Sheet.Set getDefinitionSheetSet(demetra.timeseries.TsCollection col, NodePropertySetBuilder b) {
        b.reset("Collection");

        b.with(String.class)
                .select(col, "getName", null)
                .display("Name")
                .add();

        if (col.getMoniker().isProvided()) {
            addDataSourceProperties(col.getMoniker(), b);
        }

        b.withEnum(TsInformationType.class).select(col, "getType", null).display("Information type").add();
        b.withInt().select(col, "getSize", null).display("Series count").add();

        return b.build();
    }

    private static Sheet.Set getMetaSheetSet(demetra.timeseries.TsCollection col, NodePropertySetBuilder b) {
        b.reset("Meta data");
        col.getMeta().entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .forEach(o -> b.with(String.class).selectConst(o.getKey(), o.getValue()).add());
        return b.build();
    }

    public static Sheet.@NonNull Set getDefinitionSheetSet(demetra.timeseries.@NonNull Ts ts, @NonNull NodePropertySetBuilder b) {
        b.reset("Time series");

        b.with(String.class)
                .selectConst("name", MultiLineNameUtil.join(ts.getName()))
                .display("Name")
                .description(MultiLineNameUtil.toHtml(ts.getName()))
                .add();

        if (FrozenTsHelper.isFrozen(ts) || ts.getMoniker().isProvided()) {
            addDataSourceProperties(ts.getMoniker(), b);
        }

        b.withEnum(TsInformationType.class).select(ts, "getType", null).display("Information type").add();
        b.with(LocalDateTime.class)
                .selectConst("snapshot", FrozenTsHelper.getTimestamp(ts))
                //                .attribute(LocalDateTimePropertyEditor.NULL_STRING, "Latest")
                .attribute("nullString", "Latest")
                .display("Snapshot")
                .add();

        return b.build();
    }

    private static Sheet.Set getDataSheetSet(demetra.timeseries.Ts ts, NodePropertySetBuilder b) {
        b.reset("Data");
        demetra.timeseries.TsData data = ts.getData();
        if (!data.isEmpty()) {
            b.with(TsUnit.class).select(data, "getTsUnit", null).display("TsUnit").add();
            b.with(TsPeriod.class).select(data, "getStart", null).display("First period").add();
            b.with(TsPeriod.class).select(data.getDomain(), "getLastPeriod", null).display("Last period").add();
            b.withInt().select(data, "length", null).display("Obs count").add();
            b.with(demetra.timeseries.TsData.class).selectConst("values", data).display("Values").add();
        } else {
            b.with(String.class).selectConst("InvalidDataCause", data.getEmptyCause()).display("Invalid data cause").add();
        }
        return b.build();
    }

    private static Sheet.Set getMetaSheetSet(demetra.timeseries.Ts ts, NodePropertySetBuilder b) {
        b.reset("Meta data");
        ts.getMeta().entrySet().stream()
                .filter(o -> !FrozenTsHelper.isFreezeKey(o.getKey()))
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .forEach(o -> b.with(String.class).selectConst(o.getKey(), o.getValue()).add());
        return b.build();
    }

    private static void addDataSourceProperties(demetra.timeseries.TsMoniker moniker, NodePropertySetBuilder b) {
        TsMoniker original = FrozenTsHelper.getOriginalMoniker(moniker);
        if (original != null) {
            String providerName = original.getSource();
            DataSourceProvider p = TsManager.getDefault().getProvider(DataSourceProvider.class, providerName).orElse(null);
            b.with(String.class).selectConst("Provider", getProviderDisplayName(p, providerName)).add();
            b.with(String.class).selectConst("Data source", getDataSourceDisplayName(p, original, "unavailable")).add();
        }
    }

    private static String getProviderDisplayName(DataSourceProvider provider, String fallback) {
        return provider != null ? provider.getDisplayName() : fallback;
    }

    private static String getDataSourceDisplayName(DataSourceProvider provider, TsMoniker moniker, String fallback) {
        if (provider != null) {
            Optional<demetra.tsprovider.DataSet> dataSet = provider.toDataSet(moniker);
            if (dataSet.isPresent()) {
                return provider.getDisplayName(dataSet.get().getDataSource());
            }
        }
        return fallback;
    }
}
