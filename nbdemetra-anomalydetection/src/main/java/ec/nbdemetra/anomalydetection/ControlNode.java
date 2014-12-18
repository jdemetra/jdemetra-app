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
package ec.nbdemetra.anomalydetection;

import ec.nbdemetra.anomalydetection.ui.JTsAnomalyGrid;
import ec.nbdemetra.anomalydetection.ui.JTsCheckLastList;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.tstoolkit.modelling.DefaultTransformationType;
import ec.tstoolkit.modelling.arima.tramo.TramoSpecification;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.lookup.Lookups;

/**
 * Nodes used for property sheets of anomaly detection's components
 * @author Mats Maggi
 */
public class ControlNode {

    /**
     * Creates the node of a Check Last View
     * @param mgr The explorer manager
     * @param view JTsCheckLastList view to bind with the properties
     * @return The created node
     */
    public static Node onComponentOpened(final ExplorerManager mgr, final JTsCheckLastList view) {
        CheckLastNode root = new CheckLastNode(view);
        mgr.setRootContext(root);
        return root;
    }
    
    /**
     * Creates the node of a Outliers Detection View
     * @param mgr The explorer manager
     * @param view JTsAnomalyGrid view to bind with the properties
     * @return The created node
     */
    public static Node onComponentOpened(final ExplorerManager mgr, final JTsAnomalyGrid view) {
        OutliersNode root = new OutliersNode(view);
        mgr.setRootContext(root);
        return root;
    }
    
    /**
     * Node corresponding to the Outliers Detection
     */
    static class OutliersNode extends AbstractNode {

        OutliersNode(JTsAnomalyGrid col) {
            super(Children.LEAF, Lookups.singleton(col));
            setDisplayName("Outliers Detection");
        }

        @Override
        protected Sheet createSheet() {
            JTsAnomalyGrid ui = getLookup().lookup(JTsAnomalyGrid.class);
            Sheet result = new Sheet();
            NodePropertySetBuilder b = new NodePropertySetBuilder();

            try {
                b.reset("Specification");
                PropertySupport.Reflection<TramoSpecification> specProp = new PropertySupport.Reflection<>(ui, TramoSpecification.class, "defaultSpec");
                specProp.setPropertyEditorClass(TramoSpecPropertyEditor.class);
                specProp.setName("Default Specification");
                specProp.setShortDescription("Default specification used");
                b.add(specProp);
                
                b.withBoolean().select(ui, "isDefaultCritical", "setDefaultCritical")
                        .display("Use default critical value")
                        .description("[va] The critical value is automatically determined. It depends on the number of observations considered in the outliers detection procedure")
                        .add();
                
                b.withDouble().select(ui, "getCriticalValue", "setCriticalValue").display("Critical Value").description("Critical value defining the sensibility of the detection").add();
                b.withEnum(DefaultTransformationType.class).select(ui, "getTransformation", "setTransformation").display("Transformation").description("Transformation type").add();
                
                result.put(b.build());
            } catch (NoSuchMethodException ex) {
                Exceptions.printStackTrace(ex);
            }

            b.reset("Outliers to display");
            b.withBoolean().select(ui, "isShowAO", "setShowAO").display("AO").description("Additive Outlier").add();
            b.withBoolean().select(ui, "isShowLS", "setShowLS").display("LS").description("Level Shift").add();
            b.withBoolean().select(ui, "isShowTC", "setShowTC").display("TC").description("Transitory Change").add();
            b.withBoolean().select(ui, "isShowSO", "setShowSO").display("SO").description("Seasonal Outlier").add();
            result.put(b.build());
            
            return result;
        }
    }

    /**
     * Node corresponding the the Check Last Properties
     */
    static class CheckLastNode extends AbstractNode {

        CheckLastNode(JTsCheckLastList col) {
            super(Children.LEAF, Lookups.singleton(col));
            setDisplayName("Check Last");
        }

        @Override
        protected Sheet createSheet() {
            JTsCheckLastList ui = getLookup().lookup(JTsCheckLastList.class);
            Sheet result = new Sheet();
            NodePropertySetBuilder b = new NodePropertySetBuilder();

            b.reset("Check Last");
            b.withInt().select(ui, "getLastChecks", "setLastChecks").display("Number Last Check").description("Number of last observations to check").add();
            result.put(b.build());

            try {
                b.reset("Specification");
                PropertySupport.Reflection<TramoSpecification> specProp = new PropertySupport.Reflection<>(ui, TramoSpecification.class, "spec");
                specProp.setPropertyEditorClass(TramoSpecPropertyEditor.class);
                specProp.setName("Default Specification");
                specProp.setShortDescription("Default specification used");
                b.add(specProp);
                
                result.put(b.build());
            } catch (NoSuchMethodException ex) {
                Exceptions.printStackTrace(ex);
            }

            b.reset("Coloring Options");
            b.withDouble().select(ui, "getRedCells", "setRedCells").display("Red Rows from").description("Value from which the rows are colored in red").add();
            b.withDouble().select(ui, "getOrangeCells", "setOrangeCells").display("Orange Rows from").description("Value from which the rows are colored in orange").add();
            result.put(b.build());

            return result;
        }
    }
}
