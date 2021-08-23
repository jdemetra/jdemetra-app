/*
 * Copyright 2013 National Bank of Belgium
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
package ec.nbdemetra.ui.nodes;

import demetra.ui.util.NbComponents;
import demetra.ui.components.JExceptionPanel;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.openide.DialogDisplayer;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;

/**
 *
 * @author Philippe Charles
 */
public class ExceptionNode extends AbstractNode {

    public static final String ACTION_PATH = "Demetra/Exception/Actions";

    private static int internalCounter = 0;

    public ExceptionNode(@NonNull Exception ex) {
        super(Children.LEAF, Lookups.fixed(ex, internalCounter++));
    }

    @Override
    public String getHtmlDisplayName() {
        Exception ex = getLookup().lookup(Exception.class);
        return "<b>" + ex.getClass().getSimpleName() + "</b>: " + ex.getMessage();
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("ec/nbdemetra/ui/nodes/exclamation-red.png", true);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return super.getIcon(type);
    }

    @Override
    public Action[] getActions(boolean context) {
        return Nodes.actionsForPath(ACTION_PATH);
    }

    @Override
    public Action getPreferredAction() {
        return ShowDetails.INSTANCE;
    }

    private static final class ShowDetails extends AbstractAction {

        private static final ShowDetails INSTANCE = new ShowDetails();

        @Override
        public void actionPerformed(ActionEvent e) {
            actionPerformed((Node) e.getSource());
        }

        private void actionPerformed(Node node) {
            int id = node.getLookup().lookup(Integer.class);
            Exception ex = node.getLookup().lookup(Exception.class);
            if (isInModalDialog()) {
                showDialog(ex);
            } else {
                showTopComponent(id, ex);
            }
        }

        private static void showTopComponent(int id, Exception exception) {
            String name = "exception" + id;
            TopComponent c = NbComponents.findTopComponentByNameAndClass(name, TopComponent.class);
            if (c == null) {
                c = new TopComponent() {
                    @Override
                    public int getPersistenceType() {
                        return TopComponent.PERSISTENCE_NEVER;
                    }
                };
                c.setName(name);
                c.setDisplayName(exception.getClass().getSimpleName());
                c.setLayout(new BorderLayout());
                c.add(JExceptionPanel.create(exception), BorderLayout.CENTER);
                c.open();
            }
            c.requestActive();
        }

        private static void showDialog(Exception exception) {
            JExceptionPanel p = JExceptionPanel.create(exception);
            DialogDisplayer.getDefault().notify(p.createDialogDescriptor(exception.getClass().getSimpleName()));
        }

        private static boolean isInModalDialog() {
            return KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusedWindow() instanceof Dialog;
        }
    }
}
