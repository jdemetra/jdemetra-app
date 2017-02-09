/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.nodes;

import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.tsproviders.DataSourceProviderBuddySupport;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsInformationType;
import ec.tss.TsStatus;
import ec.tss.tsproviders.IDataSourceProvider;
import ec.tss.tsproviders.TsProviders;
import ec.tss.tsproviders.utils.MultiLineNameUtil;
import ec.tstoolkit.MetaData;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import ec.ui.interfaces.ITsCollectionView;
import internal.FrozenTsHelper;
import ec.nbdemetra.ui.properties.LocalDateTimePropertyEditor;
import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.PropertyVetoException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import javax.annotation.Nonnull;
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

    public static Node onComponentOpened(final ExplorerManager mgr, final ITsCollectionView view) {
        view.addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case ITsCollectionView.SELECTION_PROPERTY:
                    onSelectionChange(mgr, view);
                    break;
                case ITsCollectionView.TS_COLLECTION_PROPERTY:
                    onCollectionChange(mgr, view);
                    break;
            }
        });
        onCollectionChange(mgr, view);
        onSelectionChange(mgr, view);

        return VOID;
    }

    private static void onSelectionChange(ExplorerManager mgr, ITsCollectionView view) {
        Ts[] selection = view.getSelection();
        if (selection.length == 0) {
            selectSingleIfReadonly(mgr, view);
        } else {
            selectNodes(mgr, selection);
        }
    }

    private static void onCollectionChange(ExplorerManager mgr, ITsCollectionView view) {
        mgr.setRootContext(new TsCollectionNode(view.getTsCollection()));
        selectSingleIfReadonly(mgr, view);
    }

    private static boolean isReadonly(ITsCollectionView view) {
        return view.getTsUpdateMode() == ITsCollectionView.TsUpdateMode.None;
    }

    private static Ts getSingleOrNull(ITsCollectionView view) {
        Ts[] result = view.getTsCollection().toArray();
        return result.length == 1 ? result[0] : null;
    }

    private static void selectSingleIfReadonly(ExplorerManager mgr, ITsCollectionView view) {
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

    static class TsCollectionNode extends AbstractNode {

        TsCollectionNode(TsCollection col) {
            super(new TsCollectionChildren(col), Lookups.singleton(col));
            setName(col.getName());
            setDisplayName(MultiLineNameUtil.last(col.getName()));
            setShortDescription(MultiLineNameUtil.toHtml(col.getName()));
        }

        @Override
        protected Sheet createSheet() {
            TsCollection col = getLookup().lookup(TsCollection.class);
            NodePropertySetBuilder b = new NodePropertySetBuilder();
            Sheet result = new Sheet();
            result.put(getDefinitionSheetSet(col, b));
            result.put(getMetaSheetSet(col, b));
            return result;
        }

        static class TsCollectionChildren extends Children.Keys<Ts> {

            final TsCollection col;

            TsCollectionChildren(TsCollection col) {
                this.col = col;
            }

            @Override
            protected void addNotify() {
                setKeys(col.toArray());
            }

            @Override
            protected void removeNotify() {
                setKeys(Collections.<Ts>emptyList());
            }

            @Override
            protected Node[] createNodes(Ts key) {
                return new Node[]{new TsNode(key)};
            }
        }
    }

    static class TsNode extends AbstractNode {

        TsNode(Ts ts) {
            super(Children.LEAF, Lookups.singleton(ts));
            setName(ts.getName());
            setDisplayName(MultiLineNameUtil.last(ts.getName()));
            setShortDescription(MultiLineNameUtil.toHtml(ts.getName()));
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }

        @Override
        public Image getIcon(int type) {
            Ts ts = getLookup().lookup(Ts.class);
            return DataSourceProviderBuddySupport.getDefault().get(ts.getMoniker()).getIcon(BeanInfo.ICON_COLOR_16x16, false);
        }

        @Override
        protected Sheet createSheet() {
            Ts ts = getLookup().lookup(Ts.class);
            NodePropertySetBuilder b = new NodePropertySetBuilder();
            Sheet result = new Sheet();
            result.put(getDefinitionSheetSet(ts, b));
            result.put(getDataSheetSet(ts, b));
            result.put(getMetaSheetSet(ts, b));
            return result;
        }
    }

    private static Sheet.Set getDefinitionSheetSet(TsCollection col, NodePropertySetBuilder b) {
        b.reset("Collection");

        b.with(String.class)
                .select(col, "getName", null)
                .display("Name")
                .add();

        if (!col.getMoniker().isAnonymous()) {
            String providerName = col.getMoniker().getSource();
            if (providerName != null) {
                b.with(String.class).selectConst("Provider", getProviderDisplayName(providerName)).add();
            }
        }

        b.withEnum(TsInformationType.class).select(col, "getInformationType", null).display("Information type").add();
        b.withBoolean().select(col, "isLocked", null).display("Locked").add();
        b.withInt().select(col, "getCount", null).display("Series count").add();

        if (col.hasMetaData() == TsStatus.Invalid) {
            b.withEnum(TsStatus.class).select(col, "hasMetaData", null).display("Meta data status").add();
        }

        return b.build();
    }

    private static Sheet.Set getMetaSheetSet(TsCollection col, NodePropertySetBuilder b) {
        b.reset("Meta data");
        MetaData md = col.getMetaData();
        if (md != null) {
            md.entrySet().stream()
                    .sorted(Comparator.comparing(Map.Entry::getKey))
                    .forEach(o -> b.with(String.class).selectConst(o.getKey(), o.getValue()).add());
        } else {
            b.withEnum(TsStatus.class).select(col, "hasMetaData", null).display("Meta data status").add();
        }
        return b.build();
    }

    @Nonnull
    public static Sheet.Set getDefinitionSheetSet(@Nonnull Ts ts, @Nonnull NodePropertySetBuilder b) {
        b.reset("Time series");

        b.with(String.class)
                .selectConst("name", MultiLineNameUtil.join(ts.getName()))
                .display("Name")
                .description(MultiLineNameUtil.toHtml(ts.getName()))
                .add();

        if (ts.isFrozen() || !ts.getMoniker().isAnonymous()) {
            String providerName = FrozenTsHelper.getSource(ts);
            if (providerName != null) {
                b.with(String.class).selectConst("Provider", getProviderDisplayName(providerName)).add();
            }
        }

        b.withEnum(TsInformationType.class).select(ts, "getInformationType", null).display("Information type").add();
        b.with(LocalDateTime.class)
                .selectConst("snapshot", FrozenTsHelper.getTimestamp(ts))
                .attribute(LocalDateTimePropertyEditor.NULL_STRING, "Latest")
                .display("Snapshot")
                .add();

        return b.build();
    }

    private static Sheet.Set getDataSheetSet(Ts ts, NodePropertySetBuilder b) {
        b.reset("Data");
        TsData data = ts.getTsData();
        if (data != null) {
            b.withEnum(TsFrequency.class).select(data, "getFrequency", null).display("Frequency").add();
            b.with(TsPeriod.class).select(data, "getStart", null).display("First period").add();
            b.with(TsPeriod.class).select(data, "getLastPeriod", null).display("Last period").add();
            b.withInt().select(data, "getObsCount", null).display("Obs count").add();
            b.with(TsData.class).selectConst("values", data).display("Values").add();
        } else {
            b.withEnum(TsStatus.class).select(ts, "hasData", null).display("Data status").add();
            b.with(String.class).selectConst("InvalidDataCause", ts.getInvalidDataCause()).display("Invalid data cause").add();
        }
        return b.build();
    }

    private static Sheet.Set getMetaSheetSet(Ts ts, NodePropertySetBuilder b) {
        b.reset("Meta data");
        MetaData md = ts.getMetaData();
        if (md != null) {
            md.entrySet().stream()
                    .filter(o -> !isFreezeKey(o.getKey()))
                    .sorted(Comparator.comparing(Map.Entry::getKey))
                    .forEach(o -> b.with(String.class).selectConst(o.getKey(), o.getValue()).add());
        } else {
            b.withEnum(TsStatus.class).select(ts, "hasMetaData", null).display("Meta data status").add();
        }
        return b.build();
    }

    private static boolean isFreezeKey(String key) {
        switch (key) {
            case Ts.SOURCE_OLD:
            case Ts.ID_OLD:
            case MetaData.SOURCE:
            case MetaData.ID:
            case MetaData.DATE:
                return true;
            default:
                return false;
        }
    }

    private static String getProviderDisplayName(String providerName) {
        return TsProviders.lookup(IDataSourceProvider.class, providerName)
                .transform(IDataSourceProvider::getDisplayName)
                .or(providerName);
    }
}
