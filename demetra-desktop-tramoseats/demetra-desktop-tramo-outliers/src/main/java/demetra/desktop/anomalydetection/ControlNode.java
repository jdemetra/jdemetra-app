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
package demetra.desktop.anomalydetection;

import demetra.desktop.anomalydetection.ui.JTsAnomalyGrid;
import demetra.desktop.anomalydetection.ui.JTsCheckLastList;
import demetra.desktop.properties.NodePropertySetBuilder;
import demetra.modelling.TransformationType;
import demetra.tramo.TramoSpec;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.lookup.Lookups;
import org.openide.util.NbBundle.Messages;

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

        @Messages({
            "controlNode.OutliersNode.setDisplayName=Outliers Detection"
        })
        OutliersNode(JTsAnomalyGrid col) {
            super(Children.LEAF, Lookups.singleton(col));
            setDisplayName(Bundle.controlNode_OutliersNode_setDisplayName());
        }

        @Override
        @Messages({
            "controlNode.Outliers.Specification.displayName=Specification",
            "controlNode.Outliers.Specification.name=Default Specification",
            "controlNode.Outliers.Specification.desc=Specification used in the checking procedure",
            "controlNode.Outliers.defaultCriticalValue.name=Use default critical value",
            "controlNode.Outliers.defaultCriticalValue.desc=[va] The critical value is automatically determined. It depends on the number of observations considered in the outliers detection procedure",
            "controlNode.Outliers.criticalValue.name=Critical Value",
            "controlNode.Outliers.criticalValue.desc=Critical value defining the sensibility of the detection",
            "controlNode.Outliers.transformation.name=Transformation",
            "controlNode.Outliers.transformation.desc=Transformation type",
            "controlNode.Outliers.outliers.displayName=Outliers to display",
            "controlNode.Outliers.AO.name=AO",
            "controlNode.Outliers.AO.desc=Additive Outlier",
            "controlNode.Outliers.LS.name=LS",
            "controlNode.Outliers.LS.desc=Level Shift",
            "controlNode.Outliers.TC.name=TC",
            "controlNode.Outliers.TC.desc=Transitory Change",
            "controlNode.Outliers.SO.name=SO",
            "controlNode.Outliers.SO.desc=Seasonal Outlier",
        })
        protected Sheet createSheet() {
            JTsAnomalyGrid ui = getLookup().lookup(JTsAnomalyGrid.class);
            Sheet result = new Sheet();
            NodePropertySetBuilder b = new NodePropertySetBuilder();

            try {
                b.reset(Bundle.controlNode_Outliers_Specification_displayName());
                PropertySupport.Reflection<TramoSpec> specProp = new PropertySupport.Reflection<>(ui, TramoSpec.class, "defaultSpec");
                specProp.setPropertyEditorClass(TramoSpecPropertyEditor.class);
                specProp.setName(Bundle.controlNode_Outliers_Specification_name());
                specProp.setShortDescription(Bundle.controlNode_Outliers_Specification_desc());
                b.add(specProp);
                
                b.withBoolean().select(ui, "isDefaultCritical", "setDefaultCritical")
                        .display(Bundle.controlNode_Outliers_defaultCriticalValue_name())
                        .description(Bundle.controlNode_Outliers_defaultCriticalValue_desc())
                        .add();
                
                b.withDouble().select(ui, "getCriticalValue", "setCriticalValue").display(Bundle.controlNode_Outliers_criticalValue_name()).description(Bundle.controlNode_Outliers_criticalValue_desc()).add();
                b.withEnum(TransformationType.class).select(ui, "getTransformation", "setTransformation").display(Bundle.controlNode_Outliers_transformation_name()).description(Bundle.controlNode_Outliers_transformation_desc()).add();
                
                result.put(b.build());
            } catch (NoSuchMethodException ex) {
                Exceptions.printStackTrace(ex);
            }

            b.reset(Bundle.controlNode_Outliers_outliers_displayName());
            b.withBoolean().select(ui, "isShowAO", "setShowAO").display(Bundle.controlNode_Outliers_AO_name()).description(Bundle.controlNode_Outliers_AO_desc()).add();
            b.withBoolean().select(ui, "isShowLS", "setShowLS").display(Bundle.controlNode_Outliers_LS_name()).description(Bundle.controlNode_Outliers_LS_desc()).add();
            b.withBoolean().select(ui, "isShowTC", "setShowTC").display(Bundle.controlNode_Outliers_TC_name()).description(Bundle.controlNode_Outliers_TC_desc()).add();
            b.withBoolean().select(ui, "isShowSO", "setShowSO").display(Bundle.controlNode_Outliers_SO_name()).description(Bundle.controlNode_Outliers_SO_desc()).add();
            result.put(b.build());
            
            return result;
        }
    }

    /**
     * Node corresponding the the Check Last Properties
     */
    static class CheckLastNode extends AbstractNode {

        @Messages({
            "controlNode.CheckLastNode.setDisplayName=Check Last"
        })
        CheckLastNode(JTsCheckLastList col) {
            super(Children.LEAF, Lookups.singleton(col));
            setDisplayName(Bundle.controlNode_CheckLastNode_setDisplayName());
        }

        @Override
        @Messages({
            "controlNode.CheckLast.displayName=Check Last",
            "controlNode.CheckLast.name=Number Last Check",
            "controlNode.CheckLast.desc=Number of observations in the end of time series that will be forecasted and compared with the actual values.",
            "controlNode.CheckLast.Specification.displayName=Specification",
            "controlNode.CheckLast.Specification.name=Default Specification",
            "controlNode.CheckLast.Specification.desc=Specification used in the checking procedure",
            "controlNode.CheckLast.ColoringOption.displayName=Coloring Options",
            "controlNode.CheckLast.ColoringOption.red.name=Red Rows from",
            "controlNode.CheckLast.ColoringOption.red.desc=Value from which the rows are colored in red",
            "controlNode.CheckLast.ColoringOption.orange.name=Orange Rows from",
            "controlNode.CheckLast.ColoringOption.orange.desc=Value from which the rows are colored in orange"
        })
        protected Sheet createSheet() {
            JTsCheckLastList ui = getLookup().lookup(JTsCheckLastList.class);
            Sheet result = new Sheet();
            NodePropertySetBuilder b = new NodePropertySetBuilder();

            b.reset(Bundle.controlNode_CheckLast_displayName());
            b.withInt().select(ui, "getLastChecks", "setLastChecks").display(Bundle.controlNode_CheckLast_name()).description(Bundle.controlNode_CheckLast_desc()).add();
            result.put(b.build());

            try {
                b.reset(Bundle.controlNode_CheckLast_Specification_displayName());
                PropertySupport.Reflection<TramoSpec> specProp = new PropertySupport.Reflection<>(ui, TramoSpec.class, "spec");
                specProp.setPropertyEditorClass(TramoSpecPropertyEditor.class);
                specProp.setName(Bundle.controlNode_CheckLast_Specification_name());
                specProp.setShortDescription(Bundle.controlNode_CheckLast_Specification_desc());
                b.add(specProp);
                
                result.put(b.build());
            } catch (NoSuchMethodException ex) {
                Exceptions.printStackTrace(ex);
            }

            b.reset(Bundle.controlNode_CheckLast_ColoringOption_displayName());
            b.withDouble().select(ui, "getRedCells", "setRedCells").display(Bundle.controlNode_CheckLast_ColoringOption_red_name()).description(Bundle.controlNode_CheckLast_ColoringOption_red_desc()).add();
            b.withDouble().select(ui, "getOrangeCells", "setOrangeCells").display(Bundle.controlNode_CheckLast_ColoringOption_orange_name()).description(Bundle.controlNode_CheckLast_ColoringOption_orange_desc()).add();
            result.put(b.build());

            return result;
        }
    }
}
