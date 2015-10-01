/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.nodes;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsInformationType;
import ec.tss.TsStatus;
import ec.tss.tsproviders.IDataSourceProvider;
import ec.tss.tsproviders.TsProviders;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import ec.ui.interfaces.ITsCollectionView;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
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

    public static Node onComponentOpened(final ExplorerManager mgr, final ITsCollectionView view) {

//        AbstractNode root = new AbstractNode(Children.create(new Custom(view), false));
        TsCollectionNode root = new TsCollectionNode(view.getTsCollection());
        mgr.setRootContext(root);

        view.addPropertyChangeListener(ITsCollectionView.SELECTION_PROPERTY, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                try {
                    List<Ts> selection = Arrays.asList(view.getSelection());
                    if (selection.isEmpty()) {
                        mgr.setSelectedNodes(new Node[]{mgr.getRootContext()});
                    } else {
                        List<Node> nodes = new ArrayList<>();
                        for (Node o : mgr.getRootContext().getChildren().getNodes()) {
                            if (selection.contains(o.getLookup().lookup(Ts.class))) {
                                nodes.add(o);
                            }
                        }
                        mgr.setSelectedNodes(Iterables.toArray(nodes, Node.class));
                    }
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });
        view.addPropertyChangeListener(ITsCollectionView.TS_COLLECTION_PROPERTY, new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                mgr.setRootContext(new TsCollectionNode(view.getTsCollection()));
            }
        });

        return root;
    }

    static class TsCollectionNode extends AbstractNode {

        TsCollectionNode(TsCollection col) {
            super(new TsCollectionChildren(col), Lookups.singleton(col));
            setName(col.getName());
        }

        @Override
        protected Sheet createSheet() {
            TsCollection col = getLookup().lookup(TsCollection.class);
            Sheet result = new Sheet();
            NodePropertySetBuilder b = new NodePropertySetBuilder();

            b.reset("Collection");
            b.with(String.class).select(col, "getName", null).display("Name").add();
            if (!col.getMoniker().isAnonymous()) {
                Optional<IDataSourceProvider> provider = TsProviders.lookup(IDataSourceProvider.class, col.getMoniker());
                b.with(String.class).select("Provider", provider.isPresent() ? provider.get().getDisplayName() : col.getMoniker().getSource()).add();
//                b.with(String.class).select("Unique ID", ts.getMoniker().getId()).add();
            }
            b.withEnum(TsInformationType.class).select(col, "getInformationType", null).display("Information type").add();
            b.withBoolean().select(col, "isLocked", null).display("Locked").add();
            b.withInt().select(col, "getCount", null).display("Series count").add();
            if (col.hasMetaData() == TsStatus.Invalid) {
                b.withEnum(TsStatus.class).select(col, "hasMetaData", null).display("Meta data status").add();
            }
            result.put(b.build());

            if (col.hasMetaData() == TsStatus.Valid) {
                b.reset("Meta data");
                // using treemap to order entries
                for (Entry<String, String> o : new TreeMap<>(col.getMetaData()).entrySet()) {
                    b.with(String.class).select(o.getKey(), o.getValue()).add();
                }
                result.put(b.build());
            }

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
        }

        @Override
        protected Sheet createSheet() {
            Ts ts = getLookup().lookup(Ts.class);
            Sheet result = new Sheet();
            NodePropertySetBuilder b = new NodePropertySetBuilder();

            b.reset("Time series");
            b.with(String.class).select(ts, "getName", null).display("Name").add();
            if (!ts.getMoniker().isAnonymous()) {
                Optional<IDataSourceProvider> provider = TsProviders.lookup(IDataSourceProvider.class, ts.getMoniker());
                b.with(String.class).select("Provider", provider.isPresent() ? provider.get().getDisplayName() : ts.getMoniker().getSource()).add();
//                b.with(String.class).select("Unique ID", ts.getMoniker().getId()).add();
            }
            b.withEnum(TsInformationType.class).select(ts, "getInformationType", null).display("Information type").add();
            b.withBoolean().select(ts, "isFrozen", null).display("Frozen").add();
            if (ts.hasData() == TsStatus.Invalid) {
                b.withEnum(TsStatus.class).select(ts, "hasData", null).display("Data status").add();
                b.with(String.class).select("InvalidDataCause", ts.getInvalidDataCause()).display("Invalid data cause").add();
            }
            if (ts.hasMetaData() == TsStatus.Invalid) {
                b.withEnum(TsStatus.class).select(ts, "hasMetaData", null).display("Meta data status").add();
            }
            result.put(b.build());

            if (ts.hasData() == TsStatus.Valid) {
                b.reset("Data");
                TsData data = ts.getTsData();
                b.withEnum(TsFrequency.class).select(data, "getFrequency", null).display("Frequency").add();
                b.with(TsPeriod.class).select(data, "getStart", null).display("First period").add();
                b.with(TsPeriod.class).select(data, "getLastPeriod", null).display("Last period").add();
                b.withInt().select(data, "getObsCount", null).display("Obs count").add();
                b.with(TsData.class).select(ts, "getTsData", null).display("Values").add();
                result.put(b.build());
            }

            if (ts.hasMetaData() == TsStatus.Valid) {
                b.reset("Meta data");
                // using treemap to order entries
                for (Entry<String, String> o : new TreeMap<>(ts.getMetaData()).entrySet()) {
                    b.with(String.class).select(o.getKey(), o.getValue()).add();
                }
                result.put(b.build());
            }

            return result;
        }
    }
}
