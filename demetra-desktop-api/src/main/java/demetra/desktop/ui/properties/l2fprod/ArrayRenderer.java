/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.ui.properties.l2fprod;

import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author PALATEJ
 */
public class ArrayRenderer extends DefaultTableCellRenderer {

    public ArrayRenderer() {
        super();
    }

    @Override
    protected void setValue(Object value) {
        //super.setValue(value);
        setText("");
    }

}
