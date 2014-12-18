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
package ec.nbdemetra.chainlinking.outlineview;

import java.lang.reflect.InvocationTargetException;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.nodes.Sheet;
import org.openide.nodes.Sheet.Set;
import org.openide.util.lookup.Lookups;

/**
 * Property sheet component used to display parameters of the chain linking.
 *
 * @author Mats Maggi
 */
public class ChainLinkingControlNode {

    /**
     * Creates the property sheet nodes when the corresponding parent component
     * is opened
     *
     * @param mgr Explorer manager of the view
     * @param view Top component of the chain linking
     * @return Node structure of the property sheet
     */
    public static Node onComponentOpened(final ExplorerManager mgr, final ChainLinkingTopComponent view) {
        InternalNode root = new InternalNode(view);
        mgr.setRootContext(root);
        return root;
    }

    static class InternalNode extends AbstractNode {

        InternalNode(ChainLinkingTopComponent view) {
            super(Children.LEAF, Lookups.singleton(view));
            setDisplayName("Chain Linking");
        }

        @Override
        protected Sheet createSheet() {
            final ChainLinkingTopComponent ui = getLookup().lookup(ChainLinkingTopComponent.class);
            Sheet sheet = super.createSheet();

            Set chain = Sheet.createPropertiesSet();
            chain.setName("Chain Linking");
            chain.setDisplayName("Chain Linking");

            Property<Integer> refYear = new Property(Integer.class) {
                @Override
                public boolean canRead() {
                    return true;
                }

                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    return ui.getRefYear();
                }

                @Override
                public boolean canWrite() {
                    return true;
                }

                @Override
                public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    Integer y = (Integer) t;
                    ui.setRefYear(y);
                }
            };
            refYear.setName("Reference Year");
            refYear.setShortDescription("Reference year of the chain linking");
            chain.put(refYear);

            sheet.put(chain);
            return sheet;
        }
    }
}
