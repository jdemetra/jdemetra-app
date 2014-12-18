/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.grid.model;

/**
 *
 * @author Philippe Charles
 */
@Deprecated
interface IGridData {

    int getColumnCount();

    String getColumnName(int j);

    int getRowCount();

    String getRowName(int i);

    Number getValue(int i, int j);
}
