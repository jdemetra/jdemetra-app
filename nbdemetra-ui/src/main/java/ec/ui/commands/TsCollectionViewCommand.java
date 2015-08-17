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
package ec.ui.commands;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import ec.nbdemetra.ui.DemetraUI;
import ec.nbdemetra.ui.tsaction.ITsAction;
import ec.nbdemetra.ui.tssave.ITsSave;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsFactory;
import ec.tss.TsInformationType;
import ec.tss.datatransfer.TssTransferSupport;
import ec.tss.tsproviders.DataSet;
import ec.tss.tsproviders.IDataSourceProvider;
import ec.tss.tsproviders.TsProviders;
import ec.tstoolkit.design.UtilityClass;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.utilities.Arrays2;
import ec.ui.interfaces.ITsCollectionView;
import static ec.ui.interfaces.ITsCollectionView.COLLECTION_PROPERTY;
import static ec.ui.interfaces.ITsCollectionView.SELECTION_PROPERTY;
import static ec.ui.interfaces.ITsCollectionView.UDPATE_MODE_PROPERTY;
import ec.util.various.swing.JCommand;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import javax.annotation.Nonnull;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.WeakListeners;

/**
 *
 * @author Philippe Charles
 */
@UtilityClass(ITsCollectionView.class)
public final class TsCollectionViewCommand {

    private TsCollectionViewCommand() {
        // static class
    }

    @Nonnull
    public static JCommand<ITsCollectionView> rename() {
        return RenameCommand.INSTANCE;
    }

    @Nonnull
    public static JCommand<ITsCollectionView> open() {
        return OpenCommand.INSTANCE;
    }

    @Nonnull
    public static JCommand<ITsCollectionView> openWith(@Nonnull ITsAction tsAction) {
        return new OpenWithCommand(tsAction);
    }

    @Nonnull
    public static JCommand<ITsCollectionView> save(@Nonnull ITsSave tsSave) {
        return new SaveCommand(tsSave);
    }

    @Nonnull
    public static JCommand<ITsCollectionView> copy() {
        return CopyCommand.INSTANCE;
    }

    @Nonnull
    public static JCommand<ITsCollectionView> copyAll() {
        return CopyAllCommand.INSTANCE;
    }

    @Nonnull
    public static JCommand<ITsCollectionView> paste() {
        return PasteCommand.INSTANCE;
    }

    @Nonnull
    public static JCommand<ITsCollectionView> delete() {
        return DeleteCommand.INSTANCE;
    }

    @Nonnull
    public static JCommand<ITsCollectionView> clear() {
        return ClearCommand.INSTANCE;
    }

    @Nonnull
    public static JCommand<ITsCollectionView> selectAll() {
        return SelectAllCommand.INSTANCE;
    }

    @Nonnull
    public static JCommand<ITsCollectionView> selectByFreq(@Nonnull TsFrequency freq) {
        return SelectByFreqCommand.VALUES.get(freq);
    }

    @Nonnull
    public static JCommand<ITsCollectionView> freeze() {
        return FreezeCommand.INSTANCE;
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation">
    private static abstract class SingleSelectionCommand extends ComponentCommand<ITsCollectionView> {

        public SingleSelectionCommand() {
            super(SELECTION_PROPERTY);
        }

        @Override
        public boolean isEnabled(ITsCollectionView component) {
            return component.getSelectionSize() == 1;
        }
    }

    private static abstract class AnySelectionCommand extends ComponentCommand<ITsCollectionView> {

        public AnySelectionCommand() {
            super(SELECTION_PROPERTY);
        }

        @Override
        public boolean isEnabled(ITsCollectionView component) {
            return component.getSelectionSize() > 0;
        }
    }

    private static abstract class AnyDataCommand extends ComponentCommand<ITsCollectionView> {

        public AnyDataCommand() {
            super(COLLECTION_PROPERTY);
        }

        @Override
        public boolean isEnabled(ITsCollectionView component) {
            return !component.getTsCollection().isEmpty();
        }
    }

    private static final class RenameCommand extends SingleSelectionCommand {

        public static final RenameCommand INSTANCE = new RenameCommand();

        @Override
        public void execute(final ITsCollectionView component) throws Exception {
            final Ts[] selection = component.getSelection();
            if (Arrays2.isNullOrEmpty(selection)) {
                return;
            }
            final Ts ts = selection[0];
            NotifyDescriptor.InputLine descriptor = new NotifyDescriptor.InputLine("New name:", "Rename time series");
            descriptor.setInputText(ts.getName());
            if (!ts.getMoniker().isAnonymous()) {
                descriptor.setAdditionalOptions(new Object[]{new JButton(new AbstractAction("Restore") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Optional<IDataSourceProvider> provider = TsProviders.lookup(IDataSourceProvider.class, ts.getMoniker());
                        if (provider.isPresent()) {
                            DataSet dataSet = provider.get().toDataSet(ts.getMoniker());
                            if (dataSet != null) {
                                component.getTsCollection().rename(ts, provider.get().getDisplayName(dataSet));
                                fireCollectionChange(component, ts);
                            }
                        }
                    }
                })});
            }
            if (DialogDisplayer.getDefault().notify(descriptor) == NotifyDescriptor.OK_OPTION) {
                component.getTsCollection().rename(ts, descriptor.getInputText());
                fireCollectionChange(component, ts);
            }
        }

        private final TsCollection fake = TsFactory.instance.createTsCollection();

        private void fireCollectionChange(ITsCollectionView component, Ts ts) {
            TsCollection real = component.getTsCollection();
            component.setTsCollection(fake);
            component.setTsCollection(real);
            component.setSelection(new Ts[]{ts});

//            selection = retainTsCollection(selection);
//            ATsCollectionView.this.firePropertyChange(COLLECTION_PROPERTY, null, collection);
        }
    }

    private static final class OpenCommand extends SingleSelectionCommand {

        public static final OpenCommand INSTANCE = new OpenCommand();

        @Override
        public void execute(ITsCollectionView component) throws Exception {
            Ts[] selection = component.getSelection();
            if (Arrays2.isNullOrEmpty(selection)) {
                return;
            }
            ITsAction tsAction = component.getTsAction();
            (tsAction != null ? tsAction : DemetraUI.getDefault().getTsAction()).open(selection[0]);
        }
    }

    private static final class OpenWithCommand extends SingleSelectionCommand {

        private final ITsAction tsAction;

        public OpenWithCommand(@Nonnull ITsAction tsAction) {
            this.tsAction = tsAction;
        }

        @Override
        public void execute(ITsCollectionView component) throws Exception {
            Ts[] selection = component.getSelection();
            if (Arrays2.isNullOrEmpty(selection)) {
                return;
            }
            tsAction.open(selection[0]);
        }
    }

    private static final class SaveCommand extends AnySelectionCommand {

        private final ITsSave tsSave;

        SaveCommand(@Nonnull ITsSave tsSave) {
            this.tsSave = tsSave;
        }

        @Override
        public void execute(ITsCollectionView component) throws Exception {
            Ts[] selection = component.getSelection();
            if (Arrays2.isNullOrEmpty(selection)) {
                return;
            }
            tsSave.save(selection);
        }
    }

    private static final class CopyCommand extends AnySelectionCommand {

        public static final CopyCommand INSTANCE = new CopyCommand();

        @Override
        public void execute(ITsCollectionView component) throws Exception {
            TsCollection col = TsFactory.instance.createTsCollection();
            col.quietAppend(Arrays.asList(component.getSelection()));
            Transferable transferable = TssTransferSupport.getDefault().fromTsCollection(col);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, null);
        }
    }

    private static final class CopyAllCommand extends AnyDataCommand {

        public static final CopyAllCommand INSTANCE = new CopyAllCommand();

        @Override
        public void execute(ITsCollectionView component) throws Exception {
            Transferable transferable = TssTransferSupport.getDefault().fromTsCollection(component.getTsCollection());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, null);
        }
    }

    private static final class PasteCommand extends JCommand<ITsCollectionView> {

        public static final PasteCommand INSTANCE = new PasteCommand();

        @Override
        public boolean isEnabled(ITsCollectionView component) {
            return !component.getTsUpdateMode().isReadOnly()
                    && TssTransferSupport.getDefault().isValidClipboard();
        }

        @Override
        public void execute(ITsCollectionView component) throws Exception {
            Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(component);
            TsCollection col = TssTransferSupport.getDefault().toTsCollection(transferable);
            if (col != null) {
                col.query(TsInformationType.All);
                if (!col.isEmpty()) {
                    component.getTsUpdateMode().update(component.getTsCollection(), col);
                }
            }
        }

        @Override
        public ActionAdapter toAction(ITsCollectionView c) {
            final ActionAdapter result = super.toAction(c);
            if (c instanceof Component) {
                result.withWeakPropertyChangeListener((Component) c, UDPATE_MODE_PROPERTY);
            }
            TssTransferSupport source = TssTransferSupport.getDefault();
            PropertyChangeListener realListener = new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    result.refreshActionState();
                }
            };
            result.putValue("TssTransferSupport", realListener);
            source.addPropertyChangeListener(TssTransferSupport.VALID_CLIPBOARD_PROPERTY, WeakListeners.propertyChange(realListener, source));
            return result;
        }
    }

    private static final class DeleteCommand extends ComponentCommand<ITsCollectionView> {

        public static final DeleteCommand INSTANCE = new DeleteCommand();

        public DeleteCommand() {
            super(SELECTION_PROPERTY, UDPATE_MODE_PROPERTY);
        }

        @Override
        public boolean isEnabled(ITsCollectionView component) {
            return !component.getTsUpdateMode().isReadOnly() && component.getSelectionSize() > 0;
        }

        @Override
        public void execute(ITsCollectionView component) throws Exception {
            component.getTsCollection().remove(Arrays.asList(component.getSelection()));
        }
    }

    private static final class ClearCommand extends ComponentCommand<ITsCollectionView> {

        public static final ClearCommand INSTANCE = new ClearCommand();

        public ClearCommand() {
            super(COLLECTION_PROPERTY, UDPATE_MODE_PROPERTY);
        }

        @Override
        public boolean isEnabled(ITsCollectionView component) {
            return !component.getTsUpdateMode().isReadOnly() && !component.getTsCollection().isEmpty();
        }

        @Override
        public void execute(ITsCollectionView component) throws Exception {
            component.getTsCollection().clear();
        }
    }

    private static final class SelectAllCommand extends ComponentCommand<ITsCollectionView> {

        public static final SelectAllCommand INSTANCE = new SelectAllCommand();

        public SelectAllCommand() {
            super(SELECTION_PROPERTY, COLLECTION_PROPERTY);
        }

        @Override
        public boolean isEnabled(ITsCollectionView component) {
            return component.getSelectionSize() != component.getTsCollection().getCount();
        }

        @Override
        public void execute(ITsCollectionView component) throws Exception {
            component.setSelection(component.getTsCollection().toArray());
        }
    }

    private static final class SelectByFreqCommand extends JCommand<ITsCollectionView> {

        public static final EnumMap<TsFrequency, SelectByFreqCommand> VALUES;

        static {
            VALUES = new EnumMap<>(TsFrequency.class);
            for (TsFrequency o : TsFrequency.values()) {
                VALUES.put(o, new SelectByFreqCommand(o));
            }
        }

        private final TsFrequency freq;

        private SelectByFreqCommand(TsFrequency freq) {
            this.freq = freq;
        }

        @Override
        public void execute(ITsCollectionView component) throws Exception {
            TsCollection col = component.getTsCollection();
            List<Ts> tmp = Lists.newArrayListWithCapacity(col.getCount());
            for (Ts o : col) {
                if (o.getTsData() != null && o.getTsData().getFrequency() == freq) {
                    tmp.add(o);
                }
            }
            component.setSelection(Iterables.toArray(tmp, Ts.class));
        }
    }

    private static final class FreezeCommand extends ComponentCommand<ITsCollectionView> {

        private static final FreezeCommand INSTANCE = new FreezeCommand();

        public FreezeCommand() {
            super(SELECTION_PROPERTY, UDPATE_MODE_PROPERTY);
        }

        @Override
        public boolean isEnabled(ITsCollectionView component) {
            return component.getSelectionSize() == 1 && !component.getTsUpdateMode().isReadOnly();
        }

        @Override
        public void execute(ITsCollectionView component) throws Exception {
            Ts[] selection = component.getSelection();
            if (Arrays2.isNullOrEmpty(selection)) {
                return;
            }
            component.getTsCollection().add(selection[0].freeze());
        }
    }
    //</editor-fold>
}
