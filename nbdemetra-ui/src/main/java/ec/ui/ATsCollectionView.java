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
package ec.ui;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import ec.nbdemetra.ui.DemetraUI;
import ec.nbdemetra.ui.IConfigurable;
import ec.nbdemetra.ui.awt.ActionMaps;
import ec.nbdemetra.ui.awt.KeyStrokes;
import ec.nbdemetra.ui.tsaction.ITsAction;
import ec.nbdemetra.ui.tssave.ITsSave;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsEvent;
import ec.tss.TsFactory;
import ec.tss.TsInformationType;
import ec.tss.datatransfer.DataTransfers;
import ec.tss.datatransfer.TsDragRenderer;
import ec.tss.datatransfer.TssTransferHandler;
import ec.tss.datatransfer.TssTransferSupport;
import ec.tss.datatransfer.impl.LocalObjectTssTransferHandler;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.ui.commands.ColorSchemeCommand;
import ec.ui.commands.TsCollectionViewCommand;
import ec.ui.interfaces.IColorSchemeAble;
import static ec.ui.interfaces.ITsCollectionAble.TS_COLLECTION_PROPERTY;
import ec.ui.interfaces.ITsCollectionView;
import ec.util.chart.ColorScheme;
import ec.util.chart.swing.Charts;
import ec.util.chart.swing.ColorSchemeIcon;
import ec.util.various.swing.FontAwesome;
import ec.util.various.swing.JCommand;
import java.awt.Font;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.BeanInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.datatransfer.MultiTransferObject;

public abstract class ATsCollectionView extends ATsControl implements ITsCollectionView {

    // PROPERTIES DEFINITION
    public static final String DROP_CONTENT_PROPERTY = "dropContent";

    // ACTIONS KEYS
    public static final String FREEZE_ACTION = "freeze";
    public static final String COPY_ACTION = "copy";
    public static final String COPY_ALL_ACTION = "copyAll";
    public static final String DELETE_ACTION = "delete";
    public static final String CLEAR_ACTION = "clear";
    public static final String PASTE_ACTION = "paste";
    public static final String OPEN_ACTION = "open";
    public static final String SELECT_ALL_ACTION = "selectAll";
    public static final String RENAME_ACTION = "rename";
    public static final String DEFAULT_COLOR_SCHEME_ACTION = "defaultColorScheme";

    // DEFAULT PROPERTIES
    protected static final TsUpdateMode DEFAULT_UPDATEMODE = TsUpdateMode.Append;
    protected static final Ts[] DEFAULT_SELECTION = new Ts[0];
    protected static final Ts[] DEFAULT_DROP_CONTENT = new Ts[0];

    // PROPERTIES
    protected TsCollection collection;
    protected TsUpdateMode updateMode;
    protected ITsAction tsAction;
    protected Ts[] selection;
    protected Ts[] dropContent;

    // OTHER
    protected final TsFactoryObserver tsFactoryObserver;

    protected final DemetraUI demetraUI = DemetraUI.getDefault();

    public ATsCollectionView() {
        this.collection = TsFactory.instance.createTsCollection();
        this.updateMode = DEFAULT_UPDATEMODE;
        this.tsAction = null;
        this.selection = DEFAULT_SELECTION;
        this.dropContent = DEFAULT_DROP_CONTENT;
        this.tsFactoryObserver = new TsFactoryObserver();

        registerActions();
        registerInputs();
        enableProperties();

        TsFactory.instance.addObserver(tsFactoryObserver);
    }

    private void registerActions() {
        ActionMap am = getActionMap();
        am.put(FREEZE_ACTION, TsCollectionViewCommand.freeze().toAction(this));
        am.put(COPY_ACTION, TsCollectionViewCommand.copy().toAction(this));
        am.put(COPY_ALL_ACTION, TsCollectionViewCommand.copyAll().toAction(this));
        am.put(DELETE_ACTION, TsCollectionViewCommand.delete().toAction(this));
        am.put(CLEAR_ACTION, TsCollectionViewCommand.clear().toAction(this));
        am.put(PASTE_ACTION, TsCollectionViewCommand.paste().toAction(this));
        am.put(OPEN_ACTION, TsCollectionViewCommand.open().toAction(this));
        am.put(SELECT_ALL_ACTION, TsCollectionViewCommand.selectAll().toAction(this));
        am.put(RENAME_ACTION, TsCollectionViewCommand.rename().toAction(this));
        if (this instanceof IColorSchemeAble) {
            am.put(DEFAULT_COLOR_SCHEME_ACTION, ColorSchemeCommand.applyColorScheme(null).toAction((IColorSchemeAble) this));
        }
    }

    private void registerInputs() {
        InputMap im = getInputMap();
        KeyStrokes.putAll(im, KeyStrokes.COPY, COPY_ACTION);
        KeyStrokes.putAll(im, KeyStrokes.PASTE, PASTE_ACTION);
        KeyStrokes.putAll(im, KeyStrokes.DELETE, DELETE_ACTION);
        KeyStrokes.putAll(im, KeyStrokes.SELECT_ALL, SELECT_ALL_ACTION);
        KeyStrokes.putAll(im, KeyStrokes.OPEN, OPEN_ACTION);
        KeyStrokes.putAll(im, KeyStrokes.CLEAR, CLEAR_ACTION);
    }

    private void enableProperties() {
        addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case TS_COLLECTION_PROPERTY:
                    onCollectionChange();
                    break;
                case SELECTION_PROPERTY:
                    onSelectionChange();
                    break;
                case UDPATE_MODE_PROPERTY:
                    onUpdateModeChange();
                    break;
                case TS_ACTION_PROPERTY:
                    onTsActionChange();
                    break;
                case DROP_CONTENT_PROPERTY:
                    onDropContentChange();
                    break;
            }
        });
    }

    //<editor-fold defaultstate="collapsed" desc="Event handlers">
    abstract protected void onCollectionChange();

    abstract protected void onSelectionChange();

    abstract protected void onUpdateModeChange();

    abstract protected void onTsActionChange();

    abstract protected void onDropContentChange();
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    @Override
    public TsCollection getTsCollection() {
        return collection;
    }

    @Override
    public void setTsCollection(TsCollection collection) {
        TsCollection old = this.collection;
        this.collection = collection != null ? collection : TsFactory.instance.createTsCollection();
        // update selection to reflect changes in collection
        Ts[] oldSelection = this.selection;
        this.selection = retainTsCollection(selection);
        firePropertyChange(TS_COLLECTION_PROPERTY, old, this.collection);
        firePropertyChange(SELECTION_PROPERTY, oldSelection, selection);
    }

    @Override
    public Ts[] getSelection() {
        return selection.clone();
    }

    @Override
    public void setSelection(Ts[] tss) {
        Ts[] old = this.selection;
        this.selection = tss != null ? retainTsCollection(tss) : DEFAULT_SELECTION;
        firePropertyChange(SELECTION_PROPERTY, old, this.selection);
    }

    @Override
    public int getSelectionSize() {
        return selection.length;
    }

    @Override
    public TsUpdateMode getTsUpdateMode() {
        return updateMode;
    }

    @Override
    public void setTsUpdateMode(TsUpdateMode updateMode) {
        TsUpdateMode old = this.updateMode;
        this.updateMode = updateMode != null ? updateMode : DEFAULT_UPDATEMODE;
        firePropertyChange(UDPATE_MODE_PROPERTY, old, this.updateMode);
    }

    @Override
    public ITsAction getTsAction() {
        return tsAction;
    }

    @Override
    public void setTsAction(ITsAction tsAction) {
        ITsAction old = this.tsAction;
        this.tsAction = tsAction;
        firePropertyChange(TS_ACTION_PROPERTY, old, this.tsAction);
    }

    // TODO: set this method public?
    protected void setDropContent(Ts[] dropContent) {
        Ts[] old = this.dropContent;
        this.dropContent = dropContent != null ? removeTsCollection(dropContent) : DEFAULT_DROP_CONTENT;
        firePropertyChange(DROP_CONTENT_PROPERTY, old, this.dropContent);
    }
    //</editor-fold>

    @Override
    public void dispose() {
        TsFactory.instance.deleteObserver(tsFactoryObserver);
        super.dispose();
    }

    public void connect() {
        TsFactory.instance.addObserver(tsFactoryObserver);
    }

    protected Transferable transferableOnSelection() {
        TsCollection col = TsFactory.instance.createTsCollection();
        col.quietAppend(Arrays.asList(selection));
        return TssTransferSupport.getDefault().fromTsCollection(col);
    }

    protected Ts[] retainTsCollection(Ts[] tss) {
        List<Ts> tmp = Lists.newArrayList(tss);
        tmp.retainAll(Arrays.asList(collection.toArray()));
        return Iterables.toArray(tmp, Ts.class);
    }

    protected Ts[] removeTsCollection(Ts[] tss) {
        List<Ts> tmp = Lists.newArrayList(tss);
        tmp.removeAll(Arrays.asList(collection.toArray()));
        return Iterables.toArray(tmp, Ts.class);
    }

    //<editor-fold defaultstate="collapsed" desc="Menus">
    public JMenu buildColorSchemeMenu() {
        ActionMap am = getActionMap();
        JMenu result = new JMenu("Color scheme");
        if (this instanceof IColorSchemeAble) {
            JMenuItem item;

            item = new JCheckBoxMenuItem(am.get(DEFAULT_COLOR_SCHEME_ACTION));
            item.setText("Default");
            result.add(item);

            result.addSeparator();
            for (ColorScheme o : DemetraUI.getDefault().getColorSchemes()) {
                item = new JCheckBoxMenuItem(ColorSchemeCommand.applyColorScheme(o).toAction((IColorSchemeAble) this));
                item.setText(o.getDisplayName());
                item.setIcon(new ColorSchemeIcon(o));
                result.add(item);
            }
        }
        return result;
    }

    protected JMenu buildMenu() {
        InputMap im = getInputMap();
        ActionMap am = getActionMap();
        JMenu result = new JMenu();

        JMenuItem item;

        item = new JMenuItem(am.get(OPEN_ACTION));
        item.setText("Open");
        item.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_FOLDER_OPEN_O));
        ExtAction.hideWhenDisabled(item);
        item.setAccelerator(KeyStrokes.OPEN.get(0));
        item.setFont(item.getFont().deriveFont(Font.BOLD));
        result.add(item);

        result.add(buildOpenWithMenu());

        JMenu menu = buildSaveMenu();
        if (menu.getSubElements().length > 0) {
            result.add(buildSaveMenu());
        }

        item = new JMenuItem(am.get(RENAME_ACTION));
        item.setText("Rename");
        item.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_PENCIL_SQUARE_O));
        ExtAction.hideWhenDisabled(item);
        result.add(item);

        item = new JMenuItem(am.get(FREEZE_ACTION));
        item.setText("Freeze");
        item.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_LOCK));
        ExtAction.hideWhenDisabled(item);
        result.add(item);

        item = new JMenuItem(am.get(COPY_ACTION));
        item.setText("Copy");
        item.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_FILES_O));
        item.setAccelerator(KeyStrokes.COPY.get(0));
        ExtAction.hideWhenDisabled(item);
        result.add(item);

        item = new JMenuItem(am.get(PASTE_ACTION));
        item.setText("Paste");
        item.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_CLIPBOARD));
        item.setAccelerator(KeyStrokes.PASTE.get(0));
//        ExtAction.hideWhenDisabled(item);
        result.add(item);

        item = new JMenuItem(am.get(DELETE_ACTION));
        item.setText("Remove");
        item.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_TRASH_O));
        item.setAccelerator(KeyStrokes.DELETE.get(0));
        ExtAction.hideWhenDisabled(item);
        result.add(item);

        result.addSeparator();

        item = new JMenuItem(am.get(SELECT_ALL_ACTION));
        item.setText("Select all");
        item.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_ASTERISK));
        item.setAccelerator(KeyStrokes.SELECT_ALL.get(0));
        result.add(item);

        item = new JMenuItem(am.get(CLEAR_ACTION));
        item.setText("Clear");
        item.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_ERASER));
        item.setAccelerator(KeyStrokes.CLEAR.get(0));
        result.add(item);

        if (this instanceof IConfigurable) {
            result.addSeparator();
            item = new JMenuItem(am.get(CONFIGURE_ACTION));
            item.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_COGS));
            item.setText("Configure...");
            result.add(item);
        }

        return result;
    }

    protected JMenu buildSelectByFreqMenu() {
        JMenu result = new JMenu("Select by frequency");
        result.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_CALENDAR_O));
        for (TsFrequency freq : EnumSet.of(TsFrequency.Monthly, TsFrequency.Quarterly, TsFrequency.HalfYearly, TsFrequency.Yearly)) {
            JMenuItem item = new JMenuItem(TsCollectionViewCommand.selectByFreq(freq).toAction(this));
            item.setText(freq.name());
            result.add(item);
        }
        return result;
    }

    protected JMenu buildOpenWithMenu() {
        JMenu result = new JMenu(new OpenWithCommand().toAction(this));
        result.setText("Open with");
        result.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_BAR_CHART_O));
        ExtAction.hideWhenDisabled(result);

        for (ITsAction o : DemetraUI.getDefault().getTsActions()) {
            JMenuItem item = new JMenuItem(TsCollectionViewCommand.openWith(o).toAction(this));
            item.setName(o.getName());
            item.setText(o.getDisplayName());
            item.setIcon(demetraUI.getPopupMenuIcon(ImageUtilities.image2Icon(o.getIcon(BeanInfo.ICON_COLOR_16x16, false))));
            result.add(item);
        }

        return result;
    }

    protected JMenu buildSaveMenu() {
        JMenu result = new JMenu(new SaveCommand().toAction(this));
        result.setText("Save");
        ExtAction.hideWhenDisabled(result);
        for (ITsSave o : DemetraUI.getDefault().getTsSave()) {
            JMenuItem item = new JMenuItem(TsCollectionViewCommand.save(o).toAction(this));
            item.setName(o.getName());
            item.setText(o.getDisplayName());
            item.setIcon(demetraUI.getPopupMenuIcon(ImageUtilities.image2Icon(o.getIcon(BeanInfo.ICON_COLOR_16x16, false))));
            result.add(item);
        }
        return result;
    }
    //</editor-fold>

    private static final class OpenWithCommand extends JCommand<ATsCollectionView> {

        @Override
        public void execute(ATsCollectionView component) throws Exception {
            // do nothing
        }

        @Override
        public boolean isEnabled(ATsCollectionView component) {
            return component.getSelectionSize() == 1;
        }

        @Override
        public JCommand.ActionAdapter toAction(ATsCollectionView component) {
            return super.toAction(component).withWeakPropertyChangeListener(component, SELECTION_PROPERTY);
        }
    }

    private static final class SaveCommand extends JCommand<ATsCollectionView> {

        @Override
        public void execute(ATsCollectionView component) throws Exception {
            // do nothing
        }

        @Override
        public boolean isEnabled(ATsCollectionView component) {
            return component.getSelectionSize() >= 1;
        }

        @Override
        public JCommand.ActionAdapter toAction(ATsCollectionView component) {
            return super.toAction(component).withWeakPropertyChangeListener(component, SELECTION_PROPERTY);
        }
    }

    public class TsActionMouseAdapter extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (!Charts.isPopup(e) && Charts.isDoubleClick(e)) {
                ActionMaps.performAction(getActionMap(), OPEN_ACTION, e);
            }
        }
    }

    protected class TsFactoryObserver implements Observer {

        final AtomicBoolean dirty = new AtomicBoolean(false);

        @Override
        public void update(Observable o, Object arg) {
            if (arg instanceof TsEvent) {
                TsEvent event = (TsEvent) arg;
                if ((event.isCollection() && collection.equals(event.tscollection))
                        || (event.isSeries() && collection.contains(event.ts))) {
                    if (!dirty.getAndSet(true)) {
                        SwingUtilities.invokeLater(() -> {
                            //                            if (dirty.getAndSet(false)) {
                            // update selection to reflect changes in collection
                            dirty.set(false);
                            Ts[] oldSelection = selection;
                            selection = retainTsCollection(selection);
                            firePropertyChange(TS_COLLECTION_PROPERTY, null, collection);
                            firePropertyChange(SELECTION_PROPERTY, oldSelection, selection);
//                            }
                        });
                    }
                }
            }
        }
    }

    public class TsCollectionSelectionListener implements ListSelectionListener {

        protected boolean enabled = true;

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        protected int indexToModel(int index) {
            return index;
        }

        protected int indexToView(int index) {
            return index;
        }

        protected void selectionChanged(ListSelectionModel model) {
            int iMin = model.getMinSelectionIndex();
            int iMax = model.getMaxSelectionIndex();

            if ((iMin == -1) || (iMax == -1)) {
                setSelection(null);
            }

            java.util.List<Ts> selected = new ArrayList(1 + (iMax - iMin));
            for (int i = iMin; i <= iMax; i++) {
                if (model.isSelectedIndex(i)) {
                    selected.add(collection.get(indexToModel(i)));
                }
            }
            setSelection(Iterables.toArray(selected, Ts.class));
        }

        public void changeSelection(ListSelectionModel model) {
            model.clearSelection();
            for (Ts o : selection) {
                int index = indexToView(collection.indexOf(o));
                model.addSelectionInterval(index, index);
            }
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (enabled && !e.getValueIsAdjusting()) {
                enabled = false;
                selectionChanged((ListSelectionModel) e.getSource());
                enabled = true;
            }
        }
    }

    public class TsCollectionTransferHandler extends TransferHandler {

        @Nullable
        private Stream<Ts> peekCollection(@Nonnull Transferable transferable) {
            Optional<MultiTransferObject> multi = DataTransfers.getMultiTransferObject(transferable);
            if (multi.isPresent()) {
                return DataTransfers.asTransferableStream(multi.get())
                        .map(o -> peek(o))
                        .filter(o -> o != null)
                        .flatMap(Function.identity());
            }
            return peek(transferable);
        }

        private Stream<Ts> peek(Transferable t) {
            TssTransferHandler handler = Lookup.getDefault().lookup(LocalObjectTssTransferHandler.class);
            if (handler != null) {
                DataFlavor dataFlavor = handler.getDataFlavor();
                if (t.isDataFlavorSupported(dataFlavor)) {
                    try {
                        Object data = t.getTransferData(dataFlavor);
                        if (handler.canImportTsCollection(data)) {
                            return handler.importTsCollection(data).stream();
                        }
                    } catch (UnsupportedFlavorException | IOException ex) {
                    }
                }
            }
            return null;
        }

        private boolean mayChangeContent(TransferSupport support) {
            Stream<Ts> newContent = peekCollection(support.getTransferable());
            if (newContent != null) {
                return !newContent.allMatch(collection::contains); // YES/NO
            }
            return true; // MAYBE
        }

        @Override
        public int getSourceActions(JComponent c) {
            TsDragRenderer r = selection.length < 10 ? TsDragRenderer.asChart() : TsDragRenderer.asCount();
            Image image = r.getTsDragRendererImage(Arrays.asList(selection));
            setDragImage(image);
            return COPY;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            return ATsCollectionView.this.transferableOnSelection();
        }

        @Override
        public boolean canImport(TransferSupport support) {
            boolean result = !ATsCollectionView.this.getTsUpdateMode().isReadOnly()
                    && TssTransferSupport.getDefault().canImport(support.getDataFlavors())
                    && mayChangeContent(support);
            if (result && support.isDrop()) {
                support.setDropAction(COPY);
            }
            return result;
        }

        @Override
        public boolean importData(TransferSupport support) {
            TsCollection col = TssTransferSupport.getDefault().toTsCollection(support.getTransferable());
            if (col != null) {
                col.query(TsInformationType.All);
                if (!col.isEmpty()) {
                    getTsUpdateMode().update(getTsCollection(), col);
                }
                return true;
            }
            return false;
        }
    }
}
