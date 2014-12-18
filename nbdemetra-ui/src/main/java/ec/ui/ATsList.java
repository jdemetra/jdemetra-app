/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui;

import com.google.common.collect.Iterables;
import ec.tstoolkit.utilities.Arrays2;
import ec.ui.interfaces.ITsList;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.List;
import javax.swing.JMenu;

/**
 *
 * @author Philippe Charles
 */
public abstract class ATsList extends ATsCollectionView implements ITsList {

    // DEFAULT PROPERTIES
    protected static final boolean DEFAULT_MULTI_SELECTION = true;
    protected static final boolean DEFAULT_SHOW_HEADER = true;
    protected static final boolean DEFAULT_SORTABLE = false;
    protected static final List<InfoType> DEFAULT_INFORMATION = Arrays2.unmodifiableList(InfoType.TsIdentifier, InfoType.Start, InfoType.End, InfoType.Length, InfoType.Data);
    protected static final InfoType DEFAULT_SORT_INFO = InfoType.TsIdentifier;
    // PROPERTIES
    protected boolean multiSelection;
    protected boolean showHeader;
    protected boolean sortable;
    protected List<InfoType> information;
    protected InfoType sortInfo;

    public ATsList() {
        this.multiSelection = DEFAULT_MULTI_SELECTION;
        this.showHeader = DEFAULT_SHOW_HEADER;
        this.sortable = DEFAULT_SORTABLE;
        this.information = DEFAULT_INFORMATION;
        this.sortInfo = DEFAULT_SORT_INFO;

        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case MULTI_SELECTION_PROPERTY:
                        onMultiSelectionChange();
                        break;
                    case SHOW_HEADER_PROPERTY:
                        onShowHeaderChange();
                        break;
                    case SORTABLE_PROPERTY:
                        onSortableChange();
                        break;
                    case INFORMATION_PROPERTY:
                        onInformationChange();
                        break;
                    case SORT_INFO_PROPERTY:
                        onSortInfoChange();
                        break;
                }
            }
        });
    }

    // EVENT HANDLERS > 
    abstract protected void onMultiSelectionChange();

    abstract protected void onShowHeaderChange();

    abstract protected void onSortableChange();

    abstract protected void onInformationChange();

    abstract protected void onSortInfoChange();
    // < EVENT HANDLERS 

    // GETTERS/SETTERS >
    @Override
    public InfoType[] getInformation() {
        return Iterables.toArray(information, InfoType.class);
    }

    @Override
    public void setInformation(InfoType[] information) {
        List<InfoType> old = this.information;
        this.information = information != null ? Arrays.asList(information) : DEFAULT_INFORMATION;
        firePropertyChange(INFORMATION_PROPERTY, old, this.information);
    }

    @Override
    public boolean isMultiSelection() {
        return multiSelection;
    }

    @Override
    public void setMultiSelection(boolean multiSelection) {
        boolean old = this.multiSelection;
        this.multiSelection = multiSelection;
        firePropertyChange(MULTI_SELECTION_PROPERTY, old, this.multiSelection);
    }

    @Override
    public boolean isShowHeader() {
        return showHeader;
    }

    @Override
    public void setShowHeader(boolean showHeader) {
        boolean old = this.showHeader;
        this.showHeader = showHeader;
        firePropertyChange(SHOW_HEADER_PROPERTY, old, this.showHeader);
    }

    @Override
    public InfoType getSortInfo() {
        return sortInfo;
    }

    @Override
    public void setSortInfo(InfoType sortInfo) {
        InfoType old = this.sortInfo;
        this.sortInfo = sortInfo != null ? sortInfo : DEFAULT_SORT_INFO;
        firePropertyChange(SORT_INFO_PROPERTY, old, this.sortInfo);
    }

    @Override
    public boolean isSortable() {
        return sortable;
    }

    @Override
    public void setSortable(boolean sortable) {
        boolean old = this.sortable;
        this.sortable = sortable;
        firePropertyChange(SORTABLE_PROPERTY, old, this.sortable);
    }
    // < GETTERS/SETTERS

    protected JMenu buildListMenu() {
        JMenu result = buildMenu();
        
        int index = 8;
        result.add(buildSelectByFreqMenu(), index++);

        return result;
    }
    // REUSABLE ACTIONS >
    // < REUSABLE ACTIONS    
}
