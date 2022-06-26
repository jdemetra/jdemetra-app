/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.sa.output;

import demetra.desktop.properties.ListSelectionEditor;
import demetra.sa.SaManager;

/**
 *
 * @author PALATEJ
 */
public class Arrays extends ListSelectionEditor<String> {

    public Arrays() {
        super(OutputSelection.arraysItems(SaManager.processors()));
    }
}
