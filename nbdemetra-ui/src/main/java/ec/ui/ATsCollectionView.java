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

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import demetra.ui.TsManager;
import ec.nbdemetra.ui.DemetraUI;
import ec.nbdemetra.ui.IConfigurable;
import ec.nbdemetra.ui.awt.ActionMaps;
import ec.nbdemetra.ui.awt.KeyStrokes;
import ec.nbdemetra.ui.tsaction.ITsAction;
import ec.nbdemetra.ui.tssave.ITsSave;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsInformationType;
import ec.tss.TsMoniker;
import ec.tss.TsStatus;
import ec.tss.datatransfer.DataTransfers;
import ec.tss.datatransfer.TsDragRenderer;
import ec.tss.datatransfer.TssTransferSupport;
import ec.tss.datatransfer.impl.LocalObjectTssTransferHandler;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.ui.commands.ColorSchemeCommand;
import ec.ui.commands.TsCollectionViewCommand;
import ec.ui.interfaces.IColorSchemeAble;
import static ec.ui.interfaces.ITsCollectionAble.TS_COLLECTION_PROPERTY;
import ec.ui.interfaces.ITsCollectionView;
import static ec.ui.interfaces.ITsCollectionView.SELECTION_PROPERTY;
import ec.util.chart.ColorScheme;
import ec.util.chart.swing.Charts;
import ec.util.chart.swing.ColorSchemeIcon;
import ec.util.various.swing.FontAwesome;
import ec.util.various.swing.JCommand;
import java.awt.Font;
import java.awt.Image;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.BeanInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Supplier;
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
import javax.swing.TransferHandler;
import static javax.swing.TransferHandler.COPY;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;

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
    private static final boolean DEFAULT_FREEZE_ON_IMPORT = false;

    // PROPERTIES
    protected TsCollection collection;
    protected TsUpdateMode updateMode;
    protected ITsAction tsAction;
    protected Ts[] selection;
    protected Ts[] dropContent;
    private boolean freezeOnImport;

    // OTHER
    protected final DemetraUI demetraUI = DemetraUI.getDefault();

    public ATsCollectionView() {
        this.collection = TsManager.getDefault().newTsCollection();
        this.updateMode = DEFAULT_UPDATEMODE;
        this.tsAction = null;
        this.selection = DEFAULT_SELECTION;
        this.dropContent = DEFAULT_DROP_CONTENT;
        this.freezeOnImport = DEFAULT_FREEZE_ON_IMPORT;

        registerActions();
        registerInputs();
        enableProperties();

        TsManager.getDefault().addUpdateListener(this::checkTsUpdate);
    }

    private void checkTsUpdate(TsMoniker moniker) {
        if (moniker.equals(collection.getMoniker())
                || collection.search(moniker) != null) {
            fireTsCollectionChange(null, collection);
        }
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

    private void fireTsCollectionChange(TsCollection oldCol, TsCollection newCol) {
        // update selection to reflect changes in collection
        Ts[] oldSelection = selection;
        selection = retainTsCollection(selection, newCol);
        firePropertyChange(TS_COLLECTION_PROPERTY, oldCol, newCol);
        firePropertyChange(SELECTION_PROPERTY, oldSelection, selection);
    }

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    @Override
    public TsCollection getTsCollection() {
        return collection;
    }

    @Override
    public void setTsCollection(TsCollection collection) {
        TsCollection old = this.collection;
        this.collection = collection != null ? collection : TsManager.getDefault().newTsCollection();
        fireTsCollectionChange(old, this.collection);
    }

    @Override
    public Ts[] getSelection() {
        return selection.clone();
    }

    @Override
    public void setSelection(Ts[] tss) {
        Ts[] old = this.selection;
        this.selection = tss != null ? retainTsCollection(tss, collection) : DEFAULT_SELECTION;
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

    @Override
    public boolean isFreezeOnImport() {
        return freezeOnImport;
    }

    @Override
    public void setFreezeOnImport(boolean freezeOnImport) {
        boolean old = this.freezeOnImport;
        this.freezeOnImport = freezeOnImport;
        firePropertyChange(FREEZE_ON_IMPORT_PROPERTY, old, this.freezeOnImport);
    }

    // TODO: set this method public?
    protected void setDropContent(Ts[] dropContent) {
        Ts[] old = this.dropContent;
        this.dropContent = dropContent != null ? removeTsCollection(dropContent, collection) : DEFAULT_DROP_CONTENT;
        firePropertyChange(DROP_CONTENT_PROPERTY, old, this.dropContent);
    }
    //</editor-fold>

    @Override
    public void dispose() {
        TsManager.getDefault().removeUpdateListener(this::checkTsUpdate);
        super.dispose();
    }

    public void connect() {
        TsManager.getDefault().addUpdateListener(this::checkTsUpdate);
    }

    protected Transferable transferableOnSelection() {
        TsCollection col = TsManager.getDefault().newTsCollection();
        col.quietAppend(Arrays.asList(selection));
        return TssTransferSupport.getDefault().fromTsCollection(col);
    }

    private static Ts[] retainTsCollection(Ts[] tss, TsCollection collection) {
        List<Ts> tmp = Lists.newArrayList(tss);
        tmp.retainAll(Arrays.asList(collection.toArray()));
        return Iterables.toArray(tmp, Ts.class);
    }

    private static Ts[] removeTsCollection(Ts[] tss, TsCollection collection) {
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
            if (ATsCollectionView.canImport(ATsCollectionView.this, support::getTransferable)) {
                if (support.isDrop()) {
                    support.setDropAction(COPY);
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean importData(TransferSupport support) {
            return ATsCollectionView.importData(ATsCollectionView.this, support::getTransferable);
        }
    }

    public static boolean canImport(@Nonnull ITsCollectionView view, @Nonnull Supplier<Transferable> toData) {
        if (!view.getTsUpdateMode().isReadOnly()) {
            Transferable t = toData.get();
            return TssTransferSupport.getDefault().canImport(t)
                    && TransferChange.of(t, view.getTsCollection()).mayChangeContent();
        }
        return false;
    }

    public static boolean importData(@Nonnull ITsCollectionView view, @Nonnull Supplier<Transferable> toData) {
        if (!view.getTsUpdateMode().isReadOnly()) {
            return TssTransferSupport.getDefault()
                    .toTsCollectionStream(toData.get())
                    .peek(o -> importData(view, o))
                    .count() > 0;
        }
        return false;
    }

    private static void importData(ITsCollectionView view, TsCollection data) {
        if (view.isFreezeOnImport()) {
            data.load(TsInformationType.All);
            view.getTsUpdateMode().update(view.getTsCollection(), freezedCopyOf(data));
        } else {
            if (TransferChange.isNotYetLoaded(data)) {
                // TODO: put load in a separate thread
                data.load(TsInformationType.Definition);
            }
            if (!data.isEmpty()) {
                data.query(TsInformationType.All);
                view.getTsUpdateMode().update(view.getTsCollection(), data);
            }
        }
    }

    private static TsCollection freezedCopyOf(TsCollection input) {
        return input.stream().map(Ts::freeze).collect(TsManager.getDefault().getTsCollector());
    }

    private enum TransferChange {
        YES, NO, MAYBE;

        public boolean mayChangeContent() {
            return this != NO;
        }

        public static TransferChange of(Transferable source, TsCollection target) {
            LocalObjectTssTransferHandler handler = Lookup.getDefault().lookup(LocalObjectTssTransferHandler.class);
            return handler != null ? of(handler, source, target) : MAYBE;
        }

        private static TransferChange of(LocalObjectTssTransferHandler handler, Transferable source, TsCollection target) {
            return DataTransfers.getMultiTransferables(source)
                    .map(handler::peekTsCollection)
                    .map(o -> of(o, target))
                    .filter(TransferChange::mayChangeContent)
                    .findFirst()
                    .orElse(NO);
        }

        private static TransferChange of(@Nullable TsCollection source, @Nonnull TsCollection target) {
            if (source == null) {
                return MAYBE;
            }
            if (isNotYetLoaded(source)) {
                return MAYBE;
            }
            if (!source.stream().allMatch(target::contains)) {
                return YES;
            }
            return NO;
        }

        public static boolean isNotYetLoaded(TsCollection o) {
            return !o.getMoniker().isAnonymous() && o.isEmpty();
        }
    }

    protected static String getNoDataMessage(TsCollection input, TsUpdateMode updateMode) {
        Ts[] col = input.toArray();
        switch (col.length) {
            case 0:
                return updateMode.isReadOnly() ? "No data" : "Drop data here";
            case 1:
                Ts single = col[0];
                switch (single.hasData()) {
                    case Invalid:
                        String cause = single.getInvalidDataCause();
                        return !Strings.isNullOrEmpty(cause) ? cause : "Invalid data without cause";
                    case Undefined:
                        return "Loading" + System.lineSeparator() + single.getName();
                    case Valid:
                        return "No obs";
                }
            default:
                int[] counter = new int[TsStatus.values().length];
                Arrays.fill(counter, 0);
                Stream.of(col).forEach(o -> counter[o.hasData().ordinal()]++);
                if (counter[TsStatus.Invalid.ordinal()] == col.length) {
                    return "Invalid data";
                }
                if (counter[TsStatus.Undefined.ordinal()] > 1) {
                    return "Loading " + counter[TsStatus.Undefined.ordinal()] + " series";
                }
                return "Nothing to display";
        }
    }
}
