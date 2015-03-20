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
package ec.nbdemetra.ui.tsproviders;

import com.google.common.base.Optional;
import ec.nbdemetra.ui.DemetraUI;
import ec.nbdemetra.ui.IReloadable;
import ec.nbdemetra.ui.nodes.FailSafeChildFactory;
import ec.nbdemetra.ui.nodes.NodeAnnotator;
import ec.nbdemetra.ui.nodes.Nodes;
import ec.tss.Ts;
import ec.tss.TsInformationType;
import ec.tss.tsproviders.DataSet;
import ec.tss.tsproviders.IDataSourceProvider;
import ec.tss.tsproviders.TsProviders;
import java.awt.Image;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nonnull;
import javax.swing.Action;
import org.netbeans.api.actions.Openable;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 * A node that represents a DataSet.
 *
 * @author Philippe Charles
 */
abstract public class DataSetNode extends AbstractNode {

    /**
     * Creates a DataSet node from its kind.
     *
     * @param dataSet
     * @return
     */
    @Nonnull
    public static DataSetNode create(@Nonnull DataSet dataSet) {
        switch (dataSet.getKind()) {
            case COLLECTION:
                return new CollectionNode(dataSet);
            case SERIES:
                return new SeriesNode(dataSet);
        }
        throw new RuntimeException("Not implemented ?");
    }
    //
    private final String actionPath;

    public DataSetNode(@Nonnull DataSet dataSet, @Nonnull String actionPath) {
        this(dataSet, new InstanceContent(), actionPath);
    }

    private DataSetNode(DataSet dataSet, InstanceContent abilities, String actionPath) {
        // 1. Children and lookup
        super(dataSet.getKind() == DataSet.Kind.COLLECTION ? Children.create(new DataSetChildFactory(dataSet), true) : Children.LEAF,
                new ProxyLookup(Lookups.singleton(dataSet), new AbstractLookup(abilities)));
        this.actionPath = actionPath;
        // 2. Abilities
        {
            abilities.add(DataSourceProviderBuddySupport.getDefault().get(dataSet));
            switch (dataSet.getKind()) {
                case COLLECTION:
                    abilities.add(new ReloadableImpl());
                    break;
                case SERIES:
                    abilities.add(new OpenableImpl());
                    break;
            }
            abilities.add(NodeAnnotator.Support.getDefault());
        }
        // 3. Name and display name
        setDisplayName(TsProviders.lookup(IDataSourceProvider.class, dataSet).get().getDisplayNodeName(dataSet));
    }

    @Override
    public Action[] getActions(boolean context) {
        return Nodes.actionsForPath(actionPath);
    }

    @Override
    public Image getIcon(int type) {
        Image image = getLookup().lookup(IDataSourceProviderBuddy.class).getIcon(getLookup().lookup(DataSet.class), type, false);
        return getLookup().lookup(NodeAnnotator.Support.class).annotateIcon(this, image);
    }

    @Override
    public Image getOpenedIcon(int type) {
        Image image = getLookup().lookup(IDataSourceProviderBuddy.class).getIcon(getLookup().lookup(DataSet.class), type, true);
        return getLookup().lookup(NodeAnnotator.Support.class).annotateIcon(this, image);
    }

    @Override
    protected Sheet createSheet() {
        return getLookup().lookup(IDataSourceProviderBuddy.class).createSheet(getLookup().lookup(DataSet.class));
    }

    @Override
    public boolean canCopy() {
        return true;
    }

    private static final class DataSetChildFactory extends FailSafeChildFactory {

        private final DataSet dataSet;

        public DataSetChildFactory(DataSet dataSet) {
            this.dataSet = dataSet;
        }

        @Override
        protected boolean tryCreateKeys(List<Object> list) throws Exception {
            list.addAll(TsProviders.lookup(IDataSourceProvider.class, dataSet).get().children(dataSet));
            return true;
        }

        @Override
        protected Node tryCreateNodeForKey(Object key) throws Exception {
            return DataSetNode.create((DataSet) key);
        }

        @Override
        protected Node createExceptionNode(Exception ex) {
            if (ex instanceof IOException) {
                return new ProviderExceptionNode((IOException) ex, dataSet.getDataSource().getProviderName());
            }
            return super.createExceptionNode(ex);
        }
    }

    private final class ReloadableImpl implements IReloadable {

        @Override
        public void reload() {
            DataSet dataSet = getLookup().lookup(DataSet.class);
            IDataSourceProvider provider = TsProviders.lookup(IDataSourceProvider.class, dataSet).get();

            provider.reload(dataSet.getDataSource());
            setChildren(Children.create(new DataSetChildFactory(dataSet), true));
        }
    }

    private final class OpenableImpl implements Openable {

        @Override
        public void open() {
            Optional<Ts> data = TsProviders.getTs(getLookup().lookup(DataSet.class), TsInformationType.Data);
            if (data.isPresent()) {
                DemetraUI.getDefault().getTsAction().open(data.get());
            }
        }
    }
}
