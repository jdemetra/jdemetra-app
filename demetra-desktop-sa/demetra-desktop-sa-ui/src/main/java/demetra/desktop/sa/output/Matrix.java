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
public class Matrix extends ListSelectionEditor<String> {

    public Matrix() {
        super(OutputSelection.matrixItems(SaManager.processors()));
    }
}
