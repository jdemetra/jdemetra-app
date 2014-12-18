/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.interfaces;

/**
 *
 * @author Kristof Bayens
 */
public interface ITsList extends ITsCollectionView {

    public static final String MULTI_SELECTION_PROPERTY = "multiSelection";
    public static final String SHOW_HEADER_PROPERTY = "showHeader";
    public static final String SORTABLE_PROPERTY = "sortable";
    public static final String INFORMATION_PROPERTY = "information";
    public static final String SORT_INFO_PROPERTY = "sortInfo";

    boolean isMultiSelection();

    void setMultiSelection(boolean multiSelection);

    boolean isShowHeader();

    void setShowHeader(boolean showHeader);

    boolean isSortable();

    void setSortable(boolean sortable);

    InfoType[] getInformation();

    void setInformation(InfoType[] information);

    InfoType getSortInfo();

    void setSortInfo(InfoType sortinfo);

    public enum InfoType {

        Name,
        Source,
        Id,
        Frequency,
        Start,
        End,
        Length,
        Data,
        TsIdentifier
    }
}
