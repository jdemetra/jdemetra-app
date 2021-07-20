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
package ec.nbdemetra.sa.revisionanalysis;

import ec.tss.sa.revisions.RevisionAnalysisSpec;
import ec.tstoolkit.timeseries.Month;
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
 * Property sheet component used to display parameters of the revision analysis
 *
 * @author Mats Maggi
 */
public class RevisionAnalysisControlNode {

    /**
     * Creates the property sheet nodes when the corresponding parent component
     * is opened
     *
     * @param mgr Explorer manager of the view
     * @param view Top component of the revision analysis
     * @return Node structure of the property sheet
     */
    public static Node onComponentOpened(final ExplorerManager mgr, final RevisionAnalysisTopComponent view) {
        InternalNode root = new InternalNode(view);
        mgr.setRootContext(root);
        return root;
    }

    static class InternalNode extends AbstractNode {

        InternalNode(RevisionAnalysisTopComponent view) {
            super(Children.LEAF, Lookups.singleton(view));
            setDisplayName("Revision Analysis");
        }

        @Override
        protected Sheet createSheet() {
            final RevisionAnalysisTopComponent ui = getLookup().lookup(RevisionAnalysisTopComponent.class);
            Sheet sheet = super.createSheet();

            Set revision = Sheet.createPropertiesSet();
            revision.setName("Revision Analysis");
            revision.setDisplayName("Revision Analysis");
            Set revisionDaySet = Sheet.createPropertiesSet();
            revisionDaySet.setName("Start of Revision");
            revisionDaySet.setDisplayName("Start of Revision");
            Set estimationTypes = Sheet.createPropertiesSet();
            estimationTypes.setName("Estimation Policy Type");
            estimationTypes.setDisplayName("Estimation Policy Type");
            Property<Boolean> outofsample = new Property(Boolean.class) {
                @Override
                public boolean canRead() {
                    return true;
                }

                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    return ui.getSpecification().isOutOfSample();
                }

                @Override
                public boolean canWrite() {
                    return true;
                }

                @Override
                public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    Boolean b = (Boolean) t;
                    ui.getSpecification().setOutOfSample(b);
                    ui.clear();
                }
            };
            outofsample.setName("Out of sample");
            outofsample.setShortDescription("Reference series is out-of-sample");
            revision.put(outofsample);
            Property<Boolean> ftarget = new Property(Boolean.class) {
                @Override
                public boolean canRead() {
                    return true;
                }

                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    return ui.getSpecification().isTargetFinal();
                }

                @Override
                public boolean canWrite() {
                    return true;
                }

                @Override
                public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    Boolean b = (Boolean) t;
                    ui.getSpecification().setTargetFinal(b);
                    ui.clear();
                }
            };
            ftarget.setName("Final target");
            ftarget.setShortDescription("Reference series is the final series");
            revision.put(ftarget);

            Property<Integer> analysisLength = new Property(Integer.class) {
                @Override
                public boolean canRead() {
                    return true;
                }

                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    return ui.getSpecification().getAnalysisLength();
                }

                @Override
                public boolean canWrite() {
                    return true;
                }

                @Override
                public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    int years = (Integer) t;
                    ui.getSpecification().setAnalysisLength(years);
                    ui.clear();
                }
            };
            analysisLength.setName("Length");
            analysisLength.setShortDescription("Length in years of the revision analysis");
            revision.put(analysisLength);

            Property<Integer> revisionDelay = new Property(Integer.class) {
                @Override
                public boolean canRead() {
                    return true;
                }

                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    return ui.getSpecification().getRevisionDelay();
                }

                @Override
                public boolean canWrite() {
                    return true;
                }

                @Override
                public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    int years = (Integer) t;
                    ui.getSpecification().setRevisionDelay(years);
                    ui.clear();
                }
            };
            revisionDelay.setName("Delay");
            revisionDelay.setShortDescription("Delay in years between 2 revisions");
            revision.put(revisionDelay);

            Property<RevisionAnalysisSpec.MainPolicyType> mainType = new Property(RevisionAnalysisSpec.MainPolicyType.class) {
                @Override
                public boolean canRead() {
                    return true;
                }

                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    return ui.getSpecification().getMainEstimation();
                }

                @Override
                public boolean canWrite() {
                    return true;
                }

                @Override
                public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    RevisionAnalysisSpec.MainPolicyType type = (RevisionAnalysisSpec.MainPolicyType) t;
                    ui.getSpecification().setMainEstimation(type);
                    ui.clear();
                }
            };
            mainType.setName("Main");
            estimationTypes.put(mainType);

            Property<RevisionAnalysisSpec.IntermediatePolicyType> intermediateType = new Property(RevisionAnalysisSpec.IntermediatePolicyType.class) {
                @Override
                public boolean canRead() {
                    return true;
                }

                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    return ui.getSpecification().getIntermediateEstimation();
                }

                @Override
                public boolean canWrite() {
                    return true;
                }

                @Override
                public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    RevisionAnalysisSpec.IntermediatePolicyType type = (RevisionAnalysisSpec.IntermediatePolicyType) t;
                    ui.getSpecification().setIntermediateEstimation(type);
                    ui.clear();
                }
            };
            intermediateType.setName("Intermediate");
            estimationTypes.put(intermediateType);

            Property<Integer> revisionDay = new Property(Integer.class) {
                @Override
                public boolean canRead() {
                    return true;
                }

                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    return ui.getSpecification().getRevisionDay().day + 1;
                }

                @Override
                public boolean canWrite() {
                    return true;
                }

                @Override
                public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    int d = (Integer) t;
                    Month m = ui.getSpecification().getRevisionDay().month;
                    ui.getSpecification().setRevisionDay(new RevisionAnalysisSpec.DayMonth(d - 1, m));
                    ui.clear();
                }
            };
            revisionDay.setName("Day");
            revisionDay.setShortDescription("Day of the month when starts the revision");
            revisionDaySet.put(revisionDay);

            Property<Month> revisionMonth = new Property(Month.class) {
                @Override
                public boolean canRead() {
                    return true;
                }

                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    return ui.getSpecification().getRevisionDay().month;
                }

                @Override
                public boolean canWrite() {
                    return true;
                }

                @Override
                public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    Month m = (Month) t;
                    int d = ui.getSpecification().getRevisionDay().day;
                    ui.getSpecification().setRevisionDay(new RevisionAnalysisSpec.DayMonth(d, m));
                    ui.clear();
                }
            };
            revisionMonth.setName("Month");
            revisionMonth.setShortDescription("Month representing the start of the revision");
            revisionDaySet.put(revisionMonth);

            sheet.put(revision);
            sheet.put(revisionDaySet);
            sheet.put(estimationTypes);
            return sheet;
        }
    }
}
