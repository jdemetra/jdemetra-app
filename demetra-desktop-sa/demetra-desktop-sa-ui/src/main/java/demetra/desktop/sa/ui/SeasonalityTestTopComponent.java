/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sa.ui;

import demetra.desktop.components.JTsChart;
import demetra.desktop.components.parts.HasTsCollection;
import demetra.desktop.components.parts.HasTsCollection.TsUpdateMode;
import demetra.desktop.components.JHtmlView;
import demetra.desktop.components.parts.HasTs;
import demetra.desktop.components.parts.HasTsSupport;
import demetra.desktop.ui.processing.TsTopComponent;
import demetra.html.HtmlUtil;
import demetra.sa.html.HtmlSeasonalityDiagnostics;
import demetra.timeseries.Ts;
import demetra.timeseries.TsData;
import demetra.timeseries.TsInformationType;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.nodes.Sheet.Set;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import jdplus.sa.tests.SeasonalityTests;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//demetra.desktop.sa.ui//SeasonalityTest//EN",
        autostore = false)
@TopComponent.Description(preferredID = "SeasonalityTestTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "properties", openAtStartup = false)
@ActionID(category = "Tool", id = "demetra.desktop.sa.ui.SeasonalityTestTopComponent")
@ActionReference(path = "Menu/Statistical methods/Seasonal Adjustment/Tools", position = 333)
@TopComponent.OpenActionRegistration(displayName = "#CTL_SeasonalityTestAction",
        preferredID = "SeasonalityTestTopComponent")
@Messages({
    "CTL_SeasonalityTestAction=Seasonality Tests",
    "CTL_SeasonalityTestTopComponent=Seasonality Tests Window",
    "HINT_SeasonalityTestTopComponent=This is a Seasonality Tests window"
})
public final class SeasonalityTestTopComponent extends TsTopComponent {

    private final Node node;
    private boolean isLog = false;
    private int diffOrder = 1;
    private int lastYears = 0;

    @lombok.experimental.Delegate
    private final HasTs proxy;
    private final JTsChart jTsChart1;
    private final JHtmlView jEditorPane1;

    public SeasonalityTestTopComponent() {
        initComponents();
        setName(Bundle.CTL_SeasonalityTestTopComponent());
        setToolTipText(Bundle.HINT_SeasonalityTestTopComponent());
        jTsChart1 = new JTsChart();
        jTsChart1.setTsUpdateMode(TsUpdateMode.Single);
        proxy=HasTsSupport.of(jTsChart1);

        jEditorPane1 = new JHtmlView();

        node = new InternalNode();
        jTsChart1.addPropertyChangeListener(HasTsCollection.TS_COLLECTION_PROPERTY, evt -> showTests());

        jSplitPane1.setTopComponent(jTsChart1);
        jSplitPane1.setBottomComponent(jEditorPane1);

        jTsChart1.addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case HasTsCollection.TS_COLLECTION_PROPERTY:
                    onTsChange();
                    break;
            }
        });
   }

    @Override
    public void componentOpened() {
        super.componentOpened();
        SwingUtilities.invokeLater(() -> {
            jSplitPane1.setDividerLocation(.3);
            jSplitPane1.setResizeWeight(.3);
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();

        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setDividerLocation(100);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(0.5);
        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane jSplitPane1;
    // End of variables declaration//GEN-END:variables

    private void onTsChange(){
        showTests();
    }

    private TsData requestData() {
        Ts ts = getTs();
        if (ts == null) {
            return null;
        }
        if (ts.getType().encompass(TsInformationType.Data)) {
            return ts.getData();
        } else {
            loadAsync(TsInformationType.Data);
            return null;
        }
    }
    
    private void showTests() {
        demetra.timeseries.Ts cur = getTs();
        if (cur == null) {
            jEditorPane1.setHtml("");
        } else {
            test();
        }
    }

    private void test() {

        TsData s = requestData();
        if (s==null || s.isEmpty()) {
            return;
        }
        int freq = s.getAnnualFrequency();
        if (lastYears > 0) {
            int nmax = lastYears * freq;
            int nbeg = s.length() - nmax;
            if (diffOrder > 0) {
                nbeg -= diffOrder;
            }
            if (nbeg > 0) {
                s = s.drop(nbeg, 0);
            }
        }
         if (isLog) {
            s = s.log();
        }

        SeasonalityTests tests = SeasonalityTests.seasonalityTest(s.getValues(), freq, diffOrder, diffOrder <= 1, true);
        HtmlSeasonalityDiagnostics html = new HtmlSeasonalityDiagnostics(tests);
        jEditorPane1.setHtml(HtmlUtil.toString(html));
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "3.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    public Node getNode() {
        return node;
    }

    class InternalNode extends AbstractNode {

        @Messages({
            "seasonalityTestTopComponent.internalNode.displayName=Seasonality tests"
        })
        InternalNode() {
            super(Children.LEAF);
            setDisplayName(Bundle.seasonalityTestTopComponent_internalNode_displayName());
        }

        @Override
        @Messages({
            "seasonalityTestTopComponent.transform.name=Transform",
            "seasonalityTestTopComponent.transform.displayName=Transformation",
            "seasonalityTestTopComponent.log.name=Log",
            "seasonalityTestTopComponent.log.desc=When marked, logarithmic transformation is used.",
            "seasonalityTestTopComponent.differencing.name=Differencing",
            "seasonalityTestTopComponent.differencing.desc=An order of a regular differencing of the series.",
            "seasonalityTestTopComponent.lastYears.name=Last years",
            "seasonalityTestTopComponent.lastYears.desc=Number of years at the end of the series taken into account (0 = whole series)."
        })
        protected Sheet createSheet() {
            Sheet sheet = super.createSheet();
            Set transform = Sheet.createPropertiesSet();
            transform.setName(Bundle.seasonalityTestTopComponent_transform_name());
            transform.setDisplayName(Bundle.seasonalityTestTopComponent_transform_displayName());
            Property<Boolean> log = new Property(Boolean.class) {
                @Override
                public boolean canRead() {
                    return true;
                }

                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    return isLog;
                }

                @Override
                public boolean canWrite() {
                    return true;
                }

                @Override
                public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    isLog = (Boolean) t;
                    showTests();
                }
            };

            log.setName(Bundle.seasonalityTestTopComponent_log_name());
            log.setShortDescription(Bundle.seasonalityTestTopComponent_log_desc());
            transform.put(log);
            Property<Integer> diff = new Property(Integer.class) {
                @Override
                public boolean canRead() {
                    return true;
                }

                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    return diffOrder;
                }

                @Override
                public boolean canWrite() {
                    return true;
                }

                @Override
                public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    diffOrder = (Integer) t;
                    showTests();
                }
            };

            diff.setName(Bundle.seasonalityTestTopComponent_differencing_name());
            diff.setShortDescription(Bundle.seasonalityTestTopComponent_differencing_desc());
            transform.put(diff);
            Node.Property<Integer> length = new Node.Property(Integer.class) {
                @Override
                public boolean canRead() {
                    return true;
                }

                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    return lastYears;
                }

                @Override
                public boolean canWrite() {
                    return true;
                }

                @Override
                public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    lastYears = (Integer) t;
                    showTests();
                }
            };
            length.setName(Bundle.seasonalityTestTopComponent_lastYears_name());
            length.setShortDescription(Bundle.seasonalityTestTopComponent_lastYears_desc());
            transform.put(length);
            sheet.put(transform);
            return sheet;
        }
    }
}