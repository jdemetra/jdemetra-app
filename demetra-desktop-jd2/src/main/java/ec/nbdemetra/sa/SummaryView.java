/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa;

import demetra.desktop.util.NbComponents;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.tss.html.HtmlUtil;
import ec.tss.html.implementation.HtmlRegArimaReport;
import ec.tss.sa.RegArimaReport;
import ec.tstoolkit.algorithm.AlgorithmDescriptor;
import demetra.desktop.components.JHtmlView;
import ec.util.list.swing.JLists;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Philippe Charles
 */
public class SummaryView extends AbstractSaProcessingTopComponent implements MultiViewElement {

    // main components
    private final JComponent visualRepresentation;
    private final JToolBar toolBarRepresentation;
    // data
    private Map<Integer, Map<AlgorithmDescriptor, RegArimaReport>> reports;
    // subcomponents
    private final JComboBox<Map.Entry<Integer, AlgorithmDescriptor>> comboBox;
    private final JHtmlView reportTB_;

    public SummaryView(WorkspaceItem<MultiProcessingDocument> doc, MultiProcessingController controller) {
        super(doc, controller);
        this.reports = new HashMap<>();
        this.reportTB_ = new JHtmlView();

        this.comboBox = new JComboBox<>();
        comboBox.setRenderer(JLists.cellRendererOf((label, value) -> {
            if (value != null) {
                label.setText(TaggedTreeNode.freqName(value.getKey()) + " > " + value.getValue().name);
            }
        }));
        comboBox.addItemListener(event -> {
            if (event.getStateChange() == ItemEvent.SELECTED && event.getItem() != null) {
                Map.Entry<Integer, AlgorithmDescriptor> item = (Map.Entry<Integer, AlgorithmDescriptor>) event.getItem();
                HtmlRegArimaReport report = new HtmlRegArimaReport(item.getValue().name, reports.get(item.getKey()).get(item.getValue()));
                reportTB_.setHtml(HtmlUtil.toString(report));
            } else {
                reportTB_.setHtml("");
            }
        });

        toolBarRepresentation = NbComponents.newInnerToolbar();
        toolBarRepresentation.addSeparator();
        toolBarRepresentation.add(comboBox);

        visualRepresentation = reportTB_;

        setData(Collections.emptyMap());

        setLayout(new BorderLayout());
        add(toolBarRepresentation, BorderLayout.NORTH);
        add(visualRepresentation, BorderLayout.CENTER);
    }

    @Override
    protected void onSaProcessingStateChange() {
        super.onSaProcessingStateChange();
        if (controller.getSaProcessingState().isFinished()) {
            setData(getCurrentProcessing().createRegArimaReports());
        } else {
            setData(Collections.emptyMap());
        }
    }

    // MultiViewElement >
    @Override
    public JComponent getVisualRepresentation() {
        return visualRepresentation;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        return toolBarRepresentation;
    }

    @Override
    public void componentOpened() {
    }

    @Override
    public void componentClosed() {
    }

    @Override
    public void componentShowing() {
    }

    @Override
    public void componentHidden() {
    }

    @Override
    public void componentActivated() {
    }

    @Override
    public void componentDeactivated() {
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback callback) {
    }

    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }
    // < MultiViewElement

    private static ComboBoxModel<Map.Entry<Integer, AlgorithmDescriptor>> asComboBoxModel(Map<Integer, Map<AlgorithmDescriptor, RegArimaReport>> reports) {
        DefaultComboBoxModel<Map.Entry<Integer, AlgorithmDescriptor>> result = new DefaultComboBoxModel<>();
        for (Map.Entry<Integer, Map<AlgorithmDescriptor, RegArimaReport>> item : reports.entrySet()) {
            for (AlgorithmDescriptor ritem : item.getValue().keySet()) {
                result.addElement(new HashMap.SimpleImmutableEntry<>(item.getKey(), ritem));
            }
        }
        return result;
    }

    public void setData(Map<Integer, Map<AlgorithmDescriptor, RegArimaReport>> reports) {
        reportTB_.setHtml("");
        this.reports = reports;
        long count = reports.values().stream().mapToLong(r -> r.values().size()).sum();
        comboBox.setVisible(count > 1);
        comboBox.setModel(asComboBoxModel(reports));
        comboBox.setSelectedIndex(-1);
        if (!reports.isEmpty()) {
            comboBox.setSelectedIndex(0);
        }
    }
}
