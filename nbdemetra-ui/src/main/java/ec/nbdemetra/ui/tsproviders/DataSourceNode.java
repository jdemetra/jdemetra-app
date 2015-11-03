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
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.interchange.Exportable;
import ec.nbdemetra.ui.INameable;
import ec.nbdemetra.ui.IReloadable;
import ec.nbdemetra.ui.nodes.FailSafeChildFactory;
import ec.nbdemetra.ui.nodes.NodeAnnotator;
import ec.nbdemetra.ui.nodes.Nodes;
import static ec.nbdemetra.ui.tsproviders.DataSourceNode.ACTION_PATH;
import ec.nbdemetra.ui.tssave.ITsSavable;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsInformationType;
import ec.tss.datatransfer.DataTransfers;
import ec.tss.datatransfer.TssTransferSupport;
import ec.tss.tsproviders.DataSet;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.IDataSourceLoader;
import ec.tss.tsproviders.IDataSourceProvider;
import ec.tss.tsproviders.TsProviders;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.beans.IntrospectionException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nonnull;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import org.netbeans.api.actions.Closable;
import org.netbeans.api.actions.Editable;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.datatransfer.ExTransferable;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 * A node that represents a DataSource.
 *
 * @author Philippe Charles
 */
@ActionReferences({
    @ActionReference(path = ACTION_PATH, position = 1210, separatorBefore = 1200, id = @ActionID(category = "File", id = "ec.nbdemetra.ui.star.StarAction")),
    @ActionReference(path = ACTION_PATH, position = 1310, separatorBefore = 1300, id = @ActionID(category = "Edit", id = "ec.nbdemetra.ui.nodes.EditSourceAction")),
    @ActionReference(path = ACTION_PATH, position = 1320, separatorBefore = 1300, id = @ActionID(category = "Edit", id = "ec.nbdemetra.ui.nodes.actions.CloneSourceAction")),
    @ActionReference(path = ACTION_PATH, position = 1330, separatorBefore = 1300, id = @ActionID(category = "File", id = "ec.nbdemetra.ui.actions.ReloadAction")),
    @ActionReference(path = ACTION_PATH, position = 1340, separatorBefore = 1300, id = @ActionID(category = "File", id = "ec.nbdemetra.ui.actions.ReloadAction")),
    @ActionReference(path = ACTION_PATH, position = 1350, separatorBefore = 1300, id = @ActionID(category = "File", id = "ec.nbdemetra.ui.actions.RenameAction")),
    @ActionReference(path = ACTION_PATH, position = 1410, separatorBefore = 1400, id = @ActionID(category = "Edit", id = "org.openide.actions.CopyAction")),
    @ActionReference(path = ACTION_PATH, position = 1415, separatorBefore = 1400, id = @ActionID(category = "File", id = "ec.nbdemetra.ui.tssave.TsSaveAction")),
    @ActionReference(path = ACTION_PATH, position = 1430, separatorBefore = 1400, id = @ActionID(category = "File", id = "ec.nbdemetra.ui.interchange.ExportAction"))
})
public final class DataSourceNode extends AbstractNode {

    public static final String ACTION_PATH = "SourceNode";

    public DataSourceNode(@Nonnull DataSource dataSource) {
        this(dataSource, new InstanceContent());
    }

    private DataSourceNode(DataSource dataSource, InstanceContent abilities) {
        // 1. Children and lookup
        super(Children.create(new DataSourceChildFactory(dataSource), true),
                new ProxyLookup(Lookups.singleton(dataSource), new AbstractLookup(abilities)));
        // 2. Abilities
        {
            abilities.add(DataSourceProviderBuddySupport.getDefault().get(dataSource));
            abilities.add(new ReloadableImpl());
            abilities.add(NodeAnnotator.Support.getDefault());
            abilities.add(new NameableImpl());
            abilities.add(new TsSavableImpl());
            if (TsProviders.lookup(IDataSourceLoader.class, dataSource).isPresent()) {
                abilities.add(new EditableImpl());
                abilities.add(new ClosableImpl());
                abilities.add(new ExportableAsXmlImpl());
            }
        }
        // 3. Name and display name
        IDataSourceProvider provider = TsProviders.lookup(IDataSourceProvider.class, dataSource).get();
        setDisplayName(provider.getDisplayName(dataSource));
    }

    @Override
    public Action[] getActions(boolean context) {
        return Nodes.actionsForPath(ACTION_PATH);
    }

    @Deprecated
    public void refreshAnnotation() {
        fireIconChange();
        fireOpenedIconChange();
    }

    @Override
    public Image getIcon(int type) {
        Image image = getLookup().lookup(IDataSourceProviderBuddy.class).getIcon(getLookup().lookup(DataSource.class), type, false);
        return getLookup().lookup(NodeAnnotator.Support.class).annotateIcon(this, image);
    }

    @Override
    public Image getOpenedIcon(int type) {
        Image image = getLookup().lookup(IDataSourceProviderBuddy.class).getIcon(getLookup().lookup(DataSource.class), type, true);
        return getLookup().lookup(NodeAnnotator.Support.class).annotateIcon(this, image);
    }

    @Override
    protected Sheet createSheet() {
        return getLookup().lookup(IDataSourceProviderBuddy.class).createSheet(getLookup().lookup(DataSource.class));
    }

    @Override
    public boolean canCopy() {
        return true;
    }

    private Transferable getData(TsInformationType type) throws IOException {
        Optional<TsCollection> data = TsProviders.getTsCollection(getLookup().lookup(DataSource.class), type);
        if (data.isPresent()) {
            return TssTransferSupport.getDefault().fromTsCollection(data.get());
        }
        throw new IOException("Cannot create the TS collection '" + getDisplayName() + "'; check the logs for further details.");
    }

    @Override
    public Transferable clipboardCopy() throws IOException {
        // Transferable#getTransferData(DataFlavor) might be called by the system clipboard
        // Therefore, we load the data directly in the following call
        return getData(TsInformationType.All);
    }

    @Override
    public Transferable drag() throws IOException {
        ExTransferable data = ExTransferable.create(getData(TsInformationType.Definition));

        DataSource dataSource = getLookup().lookup(DataSource.class);
        Optional<File> file = TsProviders.tryGetFile(dataSource);
        if (file.isPresent()) {
            data.put(new LocalFileTransferable(file.get()));
        }
        return data;
    }

    private static final class DataSourceChildFactory extends FailSafeChildFactory {

        private final DataSource dataSource;

        public DataSourceChildFactory(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        protected boolean tryCreateKeys(List<Object> list) throws Exception {
            list.addAll(TsProviders.lookup(IDataSourceProvider.class, dataSource).get().children(dataSource));
            return true;
        }

        @Override
        protected Node tryCreateNodeForKey(Object key) throws Exception {
            return DataSetNode.create((DataSet) key);
        }

        @Override
        protected Node createExceptionNode(Exception ex) {
            if (ex instanceof IOException) {
                return new ProviderExceptionNode((IOException) ex, dataSource.getProviderName());
            }
            return super.createExceptionNode(ex);
        }
    }

    private final class ReloadableImpl implements IReloadable {

        @Override
        public void reload() {
            setChildren(Children.create(new DataSourceChildFactory(getLookup().lookup(DataSource.class)), true));
        }
    }

    private final class NameableImpl implements INameable {

        @Override
        public void rename() {
            final NotifyDescriptor.InputLine descriptor = new NotifyDescriptor.InputLine("New name:", "Rename DataSource");
            descriptor.setInputText(getDisplayName());
            descriptor.setAdditionalOptions(new Object[]{new JButton(new AbstractAction("Restore") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    DataSource dataSource = getLookup().lookup(DataSource.class);
                    setDisplayName(TsProviders.lookup(IDataSourceProvider.class, dataSource).get().getDisplayName(dataSource));
                }
            })});
            if (DialogDisplayer.getDefault().notify(descriptor) == NotifyDescriptor.OK_OPTION) {
                setDisplayName(descriptor.getInputText());
            }
        }
    }

    private final class ClosableImpl implements Closable {

        @Override
        public boolean close() {
            DataSource dataSource = getLookup().lookup(DataSource.class);
            return TsProviders.lookup(IDataSourceLoader.class, dataSource).get().close(dataSource);
        }
    }

    private final class EditableImpl implements Editable {

        @Override
        public void edit() {
            DataSource dataSource = getLookup().lookup(DataSource.class);
            IDataSourceLoader loader = TsProviders.lookup(IDataSourceLoader.class, dataSource).get();
            Object bean = loader.decodeBean(dataSource);
            try {
                if (DataSourceProviderBuddySupport.getDefault().get(loader).editBean("Edit data source", bean)) {
                    loader.close(dataSource);
                    loader.open(loader.encodeBean(bean));
                }
            } catch (IntrospectionException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private final class ExportableAsXmlImpl implements Exportable {

        @Override
        public Config exportConfig() {
            DataSource dataSource = getLookup().lookup(DataSource.class);
            return ProvidersUtil.getConfig(dataSource, getDisplayName());
        }
    }

    private final class TsSavableImpl implements ITsSavable {

        @Override
        public Ts[] getAllTs() {
            Optional<TsCollection> result = TsProviders.getTsCollection(getLookup().lookup(DataSource.class), TsInformationType.All);
            return result.isPresent() ? result.get().toArray() : new Ts[0];
        }
    }

    private static final class LocalFileTransferable extends ExTransferable.Single {

        private static final DataFlavor LOCAL_FILE_DATA_FLAVOR = DataTransfers.newLocalObjectDataFlavor(File.class);
        private final File file;

        public LocalFileTransferable(@Nonnull File file) {
            super(LOCAL_FILE_DATA_FLAVOR);
            this.file = file;
        }

        @Override
        protected Object getData() throws IOException, UnsupportedFlavorException {
            return file;
        }
    }
}
