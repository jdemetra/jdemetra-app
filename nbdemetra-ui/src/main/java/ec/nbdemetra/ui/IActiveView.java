/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui;

import javax.swing.JMenu;
import org.openide.nodes.Node;

/**
 *
 * @author pcuser
 */
public interface IActiveView {
    String getName();
    boolean fill(JMenu menu);
    boolean hasContextMenu();
    Node getNode();
 }
