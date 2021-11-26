/*
 * Copyright 2016 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
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
package demetra.desktop.ui.properties.l2fprod;

import demetra.desktop.ui.properties.l2fprod.OutlierDefinition.OutlierType;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JSeparator;
import javax.swing.ListCellRenderer;

/**
 * ComboBox containing selectable elements via check boxes
 *
 * @author Mats Maggi
 */
final class OutlierCheckComboBox extends JComboBox {

    private List<CheckListItem> cbs;
    private Map<OutlierType, Boolean> mapObjSelected;
    private final List<CheckComboBoxSelectionChangedListener> changedListeners = new ArrayList<>();

    public OutlierCheckComboBox(final OutlierType[] objs) {
        this(objs, false);
    }

    public OutlierCheckComboBox(final OutlierType[] objs, boolean selected) {
        resetItems(objs, selected);
    }

    public OutlierCheckComboBox(final OutlierType[] objs, final Set<OutlierType> selected) {
        mapObjSelected = new LinkedHashMap();
        for (OutlierType item : objs) {
            mapObjSelected.put(item, selected.contains(item));
        }

        reset();
    }

    public OutlierCheckComboBox(Map<OutlierType, Boolean> mapObjSelected) {
        this.mapObjSelected = mapObjSelected;
        reset();
    }

    private boolean layingOut = false;

    @Override
    public void doLayout() {
        try {
            layingOut = true;
            super.doLayout();
        } finally {
            layingOut = false;
        }
    }

    @Override
    public Dimension getSize() {
        Dimension dim = super.getSize();
        if (!layingOut) {
            dim.width = Math.max(dim.width * 2, getPreferredSize().width);
        }
        return dim;
    }

    public void addSelectionChangedListener(CheckComboBoxSelectionChangedListener l) {
        if (l == null) {
            return;
        }
        changedListeners.add(l);
    }

    public void removeSelectionChangedListener(CheckComboBoxSelectionChangedListener l) {
        changedListeners.remove(l);
    }

    public void resetItems(final OutlierType[] objs, boolean selected) {
        mapObjSelected = new LinkedHashMap();
        for (OutlierType item : objs) {
            mapObjSelected.put(item, selected);
        }

        reset();
    }

    public OutlierType[] getSelectedItems() {
        List<OutlierType> ret = new ArrayList<>();
        mapObjSelected.entrySet().stream().forEach((entry) -> {
            OutlierType obj = entry.getKey();
            Boolean selected = entry.getValue();
            if (obj != null && selected) {
                ret.add(obj);
            }
        });

        return ret.toArray(new OutlierType[0]);
    }

    public void addSelectedItems(OutlierType[] objs) {
        if (objs == null) {
            return;
        }

        for (OutlierType obj : objs) {
            if (mapObjSelected.containsKey(obj)) {
                mapObjSelected.put(obj, true);
            }
        }

        reset();
        repaint();
    }

    public void uncheckAllItems() {
        mapObjSelected.keySet().stream().forEach((t) -> mapObjSelected.put(t, false));

        reset();
        repaint();
    }

    private void reset() {
        this.removeAllItems();

        initCBs();

        cbs.stream().forEach(this::addItem);

        setRenderer(new CheckBoxRenderer(cbs));
        addActionListener(this);
    }

    private void initCBs() {
        cbs = new ArrayList<>();

        CheckListItem cb;
        for (Map.Entry<OutlierType, Boolean> entry : mapObjSelected.entrySet()) {
            OutlierType obj = entry.getKey();
            Boolean selected = entry.getValue();

            cb = new CheckListItem(obj);
            cb.setSelected(selected);
            cbs.add(cb);
        }
    }

    private void checkBoxSelectionChanged(int index) {
        int n = cbs.size();
        if (index < 0 || index >= n) {
            return;
        }

        CheckListItem cb = cbs.get(index);
        if (cb.getType() == null) {
            return;
        }

        if (cb.isSelected()) {
            cb.setSelected(false);
            mapObjSelected.put(cb.getType(), false);
        } else {
            cb.setSelected(true);
            mapObjSelected.put(cb.getType(), true);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int sel = getSelectedIndex();

        if (sel == 0) {
            getUI().setPopupVisible(this, false);
        } else if (sel > 0) {
            checkBoxSelectionChanged(sel);
            changedListeners.stream().forEach((l) -> l.selectionChanged(sel));
        }

        this.setSelectedIndex(-1); // clear selection
    }

    @Override
    public void setPopupVisible(boolean flag) {
    }

    class CheckBoxRenderer implements ListCellRenderer {

        private final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
        private final List<CheckListItem> items;

        public CheckBoxRenderer(final List<CheckListItem> items) {
            this.items = items;
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean hasFocus) {
            setBackground(Color.WHITE);
            setForeground(Color.BLACK);
            if (index > 0 && index <= items.size()) {
                CheckListItem item = (CheckListItem) value;
                if (item.getType() == null) {
                    return new JSeparator(JSeparator.HORIZONTAL);
                }
                item.setBackground(OutlierColorChooser.getColor(item.getType().toString()));
                item.setForeground(OutlierColorChooser.getForeColor(item.getType().toString()));
                return item;
            }

            return defaultRenderer.getListCellRendererComponent(list, "Select outliers to add", index, false, hasFocus);
        }
    }

    static class CheckListItem extends JCheckBox {

        private OutlierType type;

        public CheckListItem(OutlierType type) {
            if (type != null) {
                setText(type.toString());
                this.type = type;
            }
        }

        public OutlierType getType() {
            return type;
        }

        @Override
        public String toString() {
            return type == null ? " " : type.toString();
        }
    }
}

interface CheckComboBoxSelectionChangedListener extends java.util.EventListener {

    void selectionChanged(int idx);
}
