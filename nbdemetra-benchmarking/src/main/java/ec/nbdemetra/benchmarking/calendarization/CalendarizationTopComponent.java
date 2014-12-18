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
package ec.nbdemetra.benchmarking.calendarization;

import com.google.common.collect.Lists;
import ec.benchmarking.simplets.Calendarization.PeriodObs;
import ec.nbdemetra.benchmarking.CalendarizationDocumentManager;
import ec.nbdemetra.ui.ActiveViewManager;
import ec.nbdemetra.ui.DemetraUiIcon;
import ec.nbdemetra.ui.NbComponents;
import ec.nbdemetra.ui.properties.OpenIdePropertySheetBeanEditor;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.ws.ui.WorkspaceTopComponent;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsFactory;
import ec.tss.datatransfer.TssTransferSupport;
import ec.tss.disaggregation.documents.CalendarizationDocument;
import ec.tss.disaggregation.documents.CalendarizationResults;
import ec.tss.disaggregation.documents.CalendarizationSpecification;
import ec.tss.documents.DocumentManager;
import ec.tstoolkit.data.Table;
import ec.tstoolkit.timeseries.Day;
import ec.tstoolkit.timeseries.DayOfWeek;
import ec.tstoolkit.timeseries.Month;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.ui.grid.JTsGrid;
import ec.util.grid.swing.XTable;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 * Top component used for the Calendarization. Input data can be manually
 * entered and processed with a given Frequency to display results in 2 graphs
 * (1 for the smoothed daily data and 1 for the aggregated data by a given
 * frequency). Aggregated data are also displayed in a table
 *
 * @author Mats Maggi
 */
@ConvertAsProperties(
        dtd = "-//ec.nbdemetra.benchmarking//Calendarization//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "CalendarizationTopComponent",
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@NbBundle.Messages({
    "CTL_CalendarizationAction=Calendarization",
    "CTL_CalendarizationTopComponent=Calendarization Window",
    "HINT_CalendarizationTopComponent=This is a Calendarization window"
})
public class CalendarizationTopComponent extends WorkspaceTopComponent<CalendarizationDocument> implements ExplorerManager.Provider, ClipboardOwner {

    private CalendarizationTableModel model;
    private List<PeriodObs> observations = new ArrayList<>();
    private double[] weights = new double[]{1, 1, 1, 1, 1, 1, 1};
    private CalendarizationSpecification spec;

    // Charts displaying results
    private CalendarizationChartView chart;
    private CalendarizationChartView chartAggregates;

    // Grid displaying aggregated data
    private JTsGrid grid;
    private Node n;
    private CalendarizationSpreadSheetParser parser;

    public CalendarizationTopComponent() {
        this(null);
    }

    public CalendarizationTopComponent(WorkspaceItem<CalendarizationDocument> item) {
        super(item);
        if (item == null) {
            return;
        }
        setName(getDocument().getDisplayName());
        setToolTipText(Bundle.CTL_CalendarizationTopComponent());

        chart = new CalendarizationChartView("Smoothed Data");
        chartAggregates = new CalendarizationChartView("Aggregated Data");
        grid = new JTsGrid();
        parser = new CalendarizationSpreadSheetParser();

        initComponents();

        // Frequencies available to aggregate input data
        TsFrequency[] frequencies = new TsFrequency[]{TsFrequency.Monthly, TsFrequency.Quarterly, TsFrequency.Yearly};
        for (TsFrequency f : frequencies) {
            frequencyCombo.addItem(f);
        }

        spec = getDocument().getElement().getSpecification().clone();
        weights = spec.getWeights().clone();
        frequencyCombo.setSelectedItem(spec.getAggFrequency());

        if (getDocument().getElement() != null && getDocument().getElement().getInput() != null) {
            observations = Lists.newArrayList(getDocument().getElement().getInput());

            if (!observations.isEmpty()) {
                // Sets minimal selectable date based on loaded data
                Calendar min = observations.get(observations.size() - 1).end.toCalendar();
                min.add(Calendar.DATE, 1);
                from.setMinSelectableDate(min.getTime());
                from.setDate(min.getTime());
                min.add(Calendar.DATE, 1);
                to.setDate(min.getTime());
                clearButton.setEnabled(true);
            }

        } else {
            getDocument().getElement().setInput(cloneObs());
        }

        initTable();

        rightPanel.setTopComponent(chart);
        tabbedPane.add("Chart", chartAggregates);
        tabbedPane.add("Grid", grid);

        // Init events on text fields
        initEvents();

        refreshNode();
    }

    private void initTable() {
        // Init table and its model
        model = new CalendarizationTableModel(observations);
        model.addTableModelListener(new CustomTableListener());
        xTable.setModel(model);
        xTable.getTableHeader().setReorderingAllowed(false);
        xTable.setTransferHandler(new PeriodObsTransferHandler());
        xTable.setDragEnabled(true);
        xTable.setNoDataRenderer(new XTable.DefaultNoDataRenderer("Drop data here", "Drop data here"));
        addDragOnTable();
        xTable.setComponentPopupMenu(buildMenu().getPopupMenu());

        XTable.setWidthAsPercentages(xTable, new double[]{.3, .3, .2, .2});

        // Editor on "value" column to change input values
        TableColumn col = xTable.getColumnModel().getColumn(2);
        col.setCellEditor(new ValueCellEditor());

        model.fireTableDataChanged();
    }

    private void refreshNode() {
        n = CalendarizationControlNode.onComponentOpened(getExplorerManager(), this);

        try {
            getExplorerManager().setSelectedNodes(new Node[]{n});
        } catch (PropertyVetoException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void initEvents() {
        from.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("date") || evt.getPropertyName().equals("value")) {
                    Date f = from.getDate();
                    if (f != null) {
                        Calendar c = from.getCalendar();
                        c.add(Calendar.DATE, 1);
                        if (to.getDate() == null) {
                            to.setDate(c.getTime());
                        }
                        to.setMinSelectableDate(c.getTime());
                    }
                    enableAddButton();
                }
            }
        });

        to.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("date") || evt.getPropertyName().equals("value")) {
                    enableAddButton();
                }
            }
        });

        value.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                enableAddButton();
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Calendar fromCal = from.getCalendar();
                Calendar toCal = to.getCalendar();
                Day start = new Day(fromCal.get(Calendar.YEAR), Month.valueOf(fromCal.get(Calendar.MONTH)), fromCal.get(Calendar.DATE) - 1);
                Day end = new Day(toCal.get(Calendar.YEAR), Month.valueOf(toCal.get(Calendar.MONTH)), toCal.get(Calendar.DATE) - 1);
                Double v = Double.parseDouble(value.getValue().toString());

                observations.add(new PeriodObs(start, end, v));
                getDocument().getElement().setInput(cloneObs());
                model.fireTableDataChanged();
                reset();
                clearButton.setEnabled(true);
            }
        });

        frequencyCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED
                        && e.getItem() != null) {
                    spec.setAggFrequency((TsFrequency) frequencyCombo.getSelectedItem());
                    getDocument().getElement().setSpecification(spec.clone());
                    processData();
                }

            }
        });
    }

    private JMenu buildMenu() {
        JMenu menu = new JMenu();
        JMenuItem item;
        item = new JMenuItem(new CopyAction());
        item.setText("Copy to clipboard");
        menu.add(item);

        JMenuItem item2;
        item2 = new JMenuItem(new PasteAction());
        item2.setText("Paste from clipboard");
        menu.add(item2);

        return menu;
    }

    // Resets the form values when a new PeriodObs is successfully added
    private void reset() {
        Calendar toCal = to.getCalendar();
        toCal.add(Calendar.DATE, 1);
        from.setMinSelectableDate(toCal.getTime());
        from.setDate(toCal.getTime());
        toCal.add(Calendar.DATE, 1);
        to.setDate(toCal.getTime());
        value.setValue(null);

        enableAddButton();
    }

    // Enables/disables the Add button depending on validity of given data
    private void enableAddButton() {
        if (from.getMinSelectableDate() != null && from.getDate() != null) {
            if (from.getDate().before(from.getMinSelectableDate())) {
                addButton.setEnabled(false);
                return;
            }
        }

        if (to.getMinSelectableDate() != null && to.getDate() != null) {
            if (to.getDate().before(to.getMinSelectableDate())) {
                addButton.setEnabled(false);
                return;
            }
        }

        if (from.getCalendar() != null
                && to.getCalendar() != null
                && value.isEditValid()) {
            try {
                value.commitEdit();
                if (value.getValue() != null) {
                    addButton.setEnabled(true);
                }
            } catch (ParseException ex) {
                addButton.setEnabled(false);
            }
        } else {
            addButton.setEnabled(false);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splitPane = new javax.swing.JSplitPane();
        leftPanel = new javax.swing.JPanel();
        newEntryPanel = new javax.swing.JPanel();
        fromPanel = new javax.swing.JPanel();
        fromLabel = new javax.swing.JLabel();
        from = new com.toedter.calendar.JDateChooser();
        toPanel = new javax.swing.JPanel();
        toLabel = new javax.swing.JLabel();
        to = new com.toedter.calendar.JDateChooser();
        valuePanel = new javax.swing.JPanel();
        valueLabel = new javax.swing.JLabel();
        value = new javax.swing.JFormattedTextField();
        buttonsPanel = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        processPanel = new javax.swing.JPanel();
        dailyWeightButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        frequencyLabel = new javax.swing.JLabel();
        frequencyCombo = new javax.swing.JComboBox();
        scrollPane = NbComponents.newJScrollPane();
        xTable = new ec.util.grid.swing.XTable();
        rightPanel = new javax.swing.JSplitPane();
        tabbedPane = new javax.swing.JTabbedPane();

        setLayout(new java.awt.BorderLayout());

        splitPane.setDividerLocation(250);
        splitPane.setResizeWeight(0.3);

        leftPanel.setLayout(new java.awt.BorderLayout());

        newEntryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(CalendarizationTopComponent.class, "CalendarizationTopComponent.newEntryPanel.border.title"))); // NOI18N
        newEntryPanel.setLayout(new javax.swing.BoxLayout(newEntryPanel, javax.swing.BoxLayout.PAGE_AXIS));

        fromPanel.setLayout(new javax.swing.BoxLayout(fromPanel, javax.swing.BoxLayout.LINE_AXIS));

        org.openide.awt.Mnemonics.setLocalizedText(fromLabel, org.openide.util.NbBundle.getMessage(CalendarizationTopComponent.class, "CalendarizationTopComponent.fromLabel.text")); // NOI18N
        fromLabel.setMaximumSize(new java.awt.Dimension(45, 14));
        fromLabel.setMinimumSize(new java.awt.Dimension(45, 14));
        fromLabel.setPreferredSize(new java.awt.Dimension(45, 14));
        fromPanel.add(fromLabel);

        from.setDateFormatString(org.openide.util.NbBundle.getMessage(CalendarizationTopComponent.class, "CalendarizationTopComponent.from.dateFormatString")); // NOI18N
        from.setMaximumSize(new java.awt.Dimension(109, 20));
        from.setPreferredSize(new java.awt.Dimension(109, 20));
        fromPanel.add(from);

        newEntryPanel.add(fromPanel);

        toPanel.setLayout(new javax.swing.BoxLayout(toPanel, javax.swing.BoxLayout.LINE_AXIS));

        org.openide.awt.Mnemonics.setLocalizedText(toLabel, org.openide.util.NbBundle.getMessage(CalendarizationTopComponent.class, "CalendarizationTopComponent.toLabel.text")); // NOI18N
        toLabel.setMaximumSize(new java.awt.Dimension(45, 14));
        toLabel.setMinimumSize(new java.awt.Dimension(45, 14));
        toLabel.setPreferredSize(new java.awt.Dimension(45, 14));
        toPanel.add(toLabel);

        to.setDateFormatString(org.openide.util.NbBundle.getMessage(CalendarizationTopComponent.class, "CalendarizationTopComponent.to.dateFormatString")); // NOI18N
        to.setMaximumSize(new java.awt.Dimension(109, 20));
        to.setPreferredSize(new java.awt.Dimension(109, 20));
        toPanel.add(to);

        newEntryPanel.add(toPanel);

        valuePanel.setLayout(new javax.swing.BoxLayout(valuePanel, javax.swing.BoxLayout.LINE_AXIS));

        org.openide.awt.Mnemonics.setLocalizedText(valueLabel, org.openide.util.NbBundle.getMessage(CalendarizationTopComponent.class, "CalendarizationTopComponent.valueLabel.text")); // NOI18N
        valueLabel.setMaximumSize(new java.awt.Dimension(45, 14));
        valueLabel.setMinimumSize(new java.awt.Dimension(45, 14));
        valueLabel.setPreferredSize(new java.awt.Dimension(45, 14));
        valuePanel.add(valueLabel);

        value.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("###0.#######"))));
        value.setText(org.openide.util.NbBundle.getMessage(CalendarizationTopComponent.class, "CalendarizationTopComponent.value.text")); // NOI18N
        value.setMaximumSize(new java.awt.Dimension(109, 20));
        value.setMinimumSize(new java.awt.Dimension(27, 20));
        valuePanel.add(value);

        newEntryPanel.add(valuePanel);

        org.openide.awt.Mnemonics.setLocalizedText(addButton, org.openide.util.NbBundle.getMessage(CalendarizationTopComponent.class, "CalendarizationTopComponent.addButton.text")); // NOI18N
        addButton.setAlignmentX(0.5F);
        addButton.setEnabled(false);
        buttonsPanel.add(addButton);

        org.openide.awt.Mnemonics.setLocalizedText(clearButton, org.openide.util.NbBundle.getMessage(CalendarizationTopComponent.class, "CalendarizationTopComponent.clearButton.text")); // NOI18N
        clearButton.setAlignmentX(0.5F);
        clearButton.setEnabled(false);
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });
        buttonsPanel.add(clearButton);

        newEntryPanel.add(buttonsPanel);

        leftPanel.add(newEntryPanel, java.awt.BorderLayout.NORTH);

        processPanel.setLayout(new javax.swing.BoxLayout(processPanel, javax.swing.BoxLayout.PAGE_AXIS));

        dailyWeightButton.setIcon(DemetraUiIcon.CALENDAR_16);
        org.openide.awt.Mnemonics.setLocalizedText(dailyWeightButton, org.openide.util.NbBundle.getMessage(CalendarizationTopComponent.class, "CalendarizationTopComponent.dailyWeightButton.text")); // NOI18N
        dailyWeightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dailyWeightButtonActionPerformed(evt);
            }
        });
        processPanel.add(dailyWeightButton);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        org.openide.awt.Mnemonics.setLocalizedText(frequencyLabel, org.openide.util.NbBundle.getMessage(CalendarizationTopComponent.class, "CalendarizationTopComponent.frequencyLabel.text")); // NOI18N
        jPanel1.add(frequencyLabel);

        jPanel1.add(frequencyCombo);

        processPanel.add(jPanel1);

        leftPanel.add(processPanel, java.awt.BorderLayout.SOUTH);

        xTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "From", "To", "Value"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrollPane.setViewportView(xTable);

        leftPanel.add(scrollPane, java.awt.BorderLayout.CENTER);

        splitPane.setLeftComponent(leftPanel);

        rightPanel.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        rightPanel.setResizeWeight(0.5);
        rightPanel.setBottomComponent(tabbedPane);

        splitPane.setRightComponent(rightPanel);

        add(splitPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    public void setDailyWeight(DayOfWeek day, double value) {
        weights[day.intValue()] = value;
        spec.setWeights(weights);
        getDocument().getElement().setSpecification(spec.clone());
        processData();
    }

    public double getDailyWeight(DayOfWeek day) {
        return weights[day.intValue()];
    }

    /**
     * Process the input data and displays the results
     */
    public void processData() {
        if (!observations.isEmpty()) {
            CalendarizationResults results = getDocument().getElement().getResults();

            // Generate data for the first graph (smoothed data + input periods)
            createSmoothData(results);

            // Generate data for the aggregates graph and grid
            createAggregates(results);
        }
    }

    private TimeSeriesCollection createDailyValues() {
        /* Creates the daily data for each day between the start and
         * end day of all the given input data
         */
        TimeSeriesCollection days = new TimeSeriesCollection();
        for (PeriodObs p : observations) {
            TimeSeries pTs = new TimeSeries("[" + p.start.toString() + ", " + p.end.toString() + ")");
            int nbDays = p.end.difference(p.start) + 1;
            double val = p.value / nbDays;  // Average value by day
            Day current = p.start;
            for (int i = 0; i < nbDays; i++) {
                org.jfree.data.time.Day day = new org.jfree.data.time.Day(current.getTime());
                pTs.add(day, val);
                current = current.plus(1);
            }
            days.addSeries(pTs);
        }

        return days;
    }

    private void createSmoothData(CalendarizationResults results) {
        TimeSeries smoothTs = new TimeSeries("smoothed");
        TimeSeries smoothDevMinus = new TimeSeries("smoothed_minus");
        TimeSeries smoothDevPlus = new TimeSeries("smoothed_plus");
        double[] smooth = results.getData(CalendarizationResults.SMOOTH, double[].class);
        double[] smoothDev = results.getData(CalendarizationResults.SMOOTH_DEV, double[].class);
        Day current = observations.get(0).start;
        for (int i = 0; i < smooth.length; i++) {
            org.jfree.data.time.Day day = new org.jfree.data.time.Day(current.getTime());
            smoothTs.add(day, smooth[i]);
            smoothDevMinus.add(day, smooth[i] - smoothDev[i]);
            smoothDevPlus.add(day, smooth[i] + smoothDev[i]);
            current = current.plus(1);
        }

        TimeSeriesCollection smoothDevs = new TimeSeriesCollection();
        smoothDevs.addSeries(smoothDevMinus);
        smoothDevs.addSeries(smoothDevPlus);

        chart.setData(createDailyValues(), smoothTs, smoothDevs);
    }

    private void createAggregates(CalendarizationResults results) {
        // Create Aggregates chart
        TsData agg = results.getData(CalendarizationResults.AGGREGATED, TsData.class);
        TsData aggStdev = results.getData(CalendarizationResults.AGGREGATED_DEV, TsData.class);
        TsData aggMinus = agg.minus(aggStdev);
        TsData aggPlus = agg.plus(aggStdev);
        chartAggregates.setData(agg, aggMinus, aggPlus);

        // Create the TsCollection to send to the grid
        TsCollection collection = TsFactory.instance.createTsCollection();
        Ts tsAgg = DocumentManager.instance.getTs(getDocument().getElement(), CalendarizationResults.AGGREGATED);
        Ts tsAggStDev = DocumentManager.instance.getTs(getDocument().getElement(), CalendarizationResults.AGGREGATED_DEV);
        tsAgg.set(agg);
        collection.add(tsAgg);
        tsAggStDev.set(aggStdev);
        collection.add(tsAggStDev);
        grid.setTsCollection(collection);
    }

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        observations.clear();
        getDocument().getElement().setInput(cloneObs());
        model.fireTableDataChanged();
        clearButton.setEnabled(false);
        chart.clear();
        chartAggregates.clear();
        grid.setTsCollection(null);

        // Resets the minimum selectable dates
        from.setMinSelectableDate(null);
        to.setMinSelectableDate(null);
    }//GEN-LAST:event_clearButtonActionPerformed

    private void dailyWeightButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dailyWeightButtonActionPerformed
        OpenIdePropertySheetBeanEditor.editNode(n, "Properties", null);
    }//GEN-LAST:event_dailyWeightButtonActionPerformed

    // <editor-fold defaultstate="collapsed" desc="Properties I/O">
    void writeProperties(java.util.Properties p) {
        // TODO : save some information ?
    }

    void readProperties(java.util.Properties p) {
        // TODO : read saved data
    }
    // </editor-fold>

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JButton clearButton;
    private javax.swing.JButton dailyWeightButton;
    private javax.swing.JComboBox frequencyCombo;
    private javax.swing.JLabel frequencyLabel;
    private com.toedter.calendar.JDateChooser from;
    private javax.swing.JLabel fromLabel;
    private javax.swing.JPanel fromPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JPanel newEntryPanel;
    private javax.swing.JPanel processPanel;
    private javax.swing.JSplitPane rightPanel;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JTabbedPane tabbedPane;
    private com.toedter.calendar.JDateChooser to;
    private javax.swing.JLabel toLabel;
    private javax.swing.JPanel toPanel;
    private javax.swing.JFormattedTextField value;
    private javax.swing.JLabel valueLabel;
    private javax.swing.JPanel valuePanel;
    private ec.util.grid.swing.XTable xTable;
    // End of variables declaration//GEN-END:variables

    @Override
    protected String getContextPath() {
        return CalendarizationDocumentManager.CONTEXTPATH;
    }

    @Override
    public void componentOpened() {
        super.componentOpened();
    }

    @Override
    public void componentClosed() {
        getExplorerManager().setRootContext(Node.EMPTY);
        super.componentClosed();
    }

    @Override
    public void componentActivated() {
        ActiveViewManager.getInstance().set(this);
    }

    @Override
    public void componentDeactivated() {
        ActiveViewManager.getInstance().set(null);
    }

    private List<PeriodObs> cloneObs() {
        List<PeriodObs> obs2 = new ArrayList<>();
        for (PeriodObs o : observations) {
            obs2.add(new PeriodObs(o.start, o.end, o.value));
        }
        return obs2;
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {

    }

    public Transferable transferableOnSelection() {
        int selection = xTable.getSelectedRows().length;
        Table t = new Table(selection, 3);
        for (int i = 0; i < selection; i++) {
            t.set(i, 0, observations.get(xTable.getSelectedRows()[i]).start.getTime());
            t.set(i, 1, observations.get(xTable.getSelectedRows()[i]).end.getTime());
            t.set(i, 2, observations.get(xTable.getSelectedRows()[i]).value);
        }

        return TssTransferSupport.getDefault().fromTable(t);
    }

    private void importFromTable(Table<?> t) {
        try {
            List<PeriodObs> obs = parser.parse(t);
            observations.clear();
            observations.addAll(obs);
            getDocument().getElement().setInput(cloneObs());
            model.fireTableDataChanged();
            clearButton.setEnabled(true);
        } catch (IOException | IllegalArgumentException ex) {
            NotifyDescriptor d = new NotifyDescriptor(ex.getMessage(),
                    "Error",
                    NotifyDescriptor.DEFAULT_OPTION,
                    NotifyDescriptor.ERROR_MESSAGE,
                    null,
                    null);
            DialogDisplayer.getDefault().notify(d);
        }
    }

    // Editor used to change given value in the "value" column
    private class ValueCellEditor extends DefaultCellEditor {

        public ValueCellEditor() {
            super(new JFormattedTextField());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            JFormattedTextField editor = (JFormattedTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);

            if (value != null) {
                editor.setHorizontalAlignment(SwingConstants.LEFT);
                editor.setText((String) value);
            }
            return editor;
        }
    }

    // Listener tracking changes on the table's data
    private class CustomTableListener implements TableModelListener {

        @Override
        public void tableChanged(TableModelEvent e) {
            getDocument().getElement().setInput(cloneObs());
            processData();
        }
    }

    private class PasteAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            Table<?> t = TssTransferSupport.getDefault().toTable(Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null));
            if (t != null) {
                importFromTable(t);
            }
        }
    }

    private class CopyAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!observations.isEmpty()) {
                Table t = new Table(observations.size(), 3);
                for (int i = 0; i < observations.size(); i++) {
                    t.set(i, 0, observations.get(i).start.getTime());
                    t.set(i, 1, observations.get(i).end.getTime());
                    t.set(i, 2, observations.get(i).value);
                }

                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(TssTransferSupport.getDefault().fromTable(t), null);
            }
        }

    }

    public class PeriodObsTransferHandler extends TransferHandler {

        @Override
        protected Transferable createTransferable(JComponent c) {
            return CalendarizationTopComponent.this.transferableOnSelection();
        }

        @Override
        public boolean canImport(TransferSupport support) {
            boolean result = TssTransferSupport.getDefault().canImport(support.getDataFlavors());
            if (result && support.isDrop()) {
                support.setDropAction(COPY);
            }
            return result;
        }

        @Override
        public boolean importData(TransferSupport support) {
            Table<?> t = TssTransferSupport.getDefault().toTable(support.getTransferable());
            if (t != null) {
                importFromTable(t);
                return true;
            }
            return false;
        }
    }

    private void addDragOnTable() {
        DragSource dragSource = DragSource.getDefaultDragSource();
        dragSource.createDefaultDragGestureRecognizer(xTable, DnDConstants.ACTION_COPY_OR_MOVE, new DragGestureListener() {
            @Override
            public void dragGestureRecognized(DragGestureEvent dge) {
                TransferHandler transferHandler = xTable.getTransferHandler();
                if (transferHandler != null) {
                    transferHandler.exportAsDrag(xTable, dge.getTriggerEvent(), TransferHandler.COPY);
                }
            }
        });
    }

}
