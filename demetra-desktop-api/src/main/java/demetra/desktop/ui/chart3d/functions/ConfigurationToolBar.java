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
package demetra.desktop.ui.chart3d.functions;

import demetra.desktop.ui.chart3d.SurfaceModel;
import java.awt.Dimension;
import javax.swing.Box;
import javax.swing.Box.Filler;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

/**
 * Menu Bar containing all needed configuration options for the 3D and 2D
 * likelihood graph.
 *
 * @author Mats Maggi
 */
public class ConfigurationToolBar extends JMenuBar {

    // Properties
    public static final String XY_TICKS = "XY Ticks";
    public static final String Z_TICKS = "Z Ticks";
    public static final String BOX_GRID = "Box Grid";
    public static final String XY_MESH = "XY Mesh";
    public static final String DRAW_BOX = "Draw Box";
    public static final String HIDE_ON_DRAG = "Hide On Drag";
    public static final String PLOT_TYPE = "Plot Type";
    public static final String PAINTING_MODE = "Painting Mode";
    public static final String STEPS = "Steps";
    public static final String EPSILON = "Epsilon";
    private final String[] params = {XY_TICKS, Z_TICKS, BOX_GRID, XY_MESH, DRAW_BOX, HIDE_ON_DRAG};
    // Constants
    public static final int MAX_STEPS = 1000;
    public static final int MIN_STEPS = 20;
    public static final float MAX_EPS = 1f;
    public static final float MIN_EPS = 0.005f;
    // Menus
    private JMenu plotTypeMenu;
    private JMenu plotColorMenu;
    private JMenu parameters;
    private JMenu viewParams;
    private String[] fnParams;
    private ParameterComboBox comboboxes;

    public ConfigurationToolBar(String[] elements, boolean full) {
        super();
        this.fnParams = elements;
        initComponents(full);
    }

    public ParameterComboBox getParametersComboBoxes() {
        return comboboxes;
    }

    private void initComponents(boolean full) {
        plotTypeMenu = new JMenu(PLOT_TYPE);
        plotColorMenu = new JMenu(PAINTING_MODE);
        parameters = new JMenu("Parameters");
        viewParams = new JMenu("View");

        if (full) {
            createPlotType();
            createPlotColor();
            createViewParams();
        }
        createParameters();

        if (full) {
            createParamSelection();
        }

    }

    private void createPlotType() {
        ButtonGroup group = new ButtonGroup();
        SurfaceModel.PlotType[] types = SurfaceModel.PlotType.values();
        for (final SurfaceModel.PlotType t : types) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(t.getPropertyName());
            if (t.getPropertyName().equals(SurfaceModel.PlotType.SURFACE.getPropertyName())) {
                item.setSelected(true);
            }

            item.addActionListener(event -> firePropertyChange(PLOT_TYPE, null, t));

            group.add(item);
            plotTypeMenu.add(item);
        }
        add(plotTypeMenu);
    }

    private void createPlotColor() {
        ButtonGroup group = new ButtonGroup();
        SurfaceModel.PlotColor[] colors = SurfaceModel.PlotColor.values();
        for (final SurfaceModel.PlotColor c : colors) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(c.getPropertyName());
            if (c.getPropertyName().equals(SurfaceModel.PlotColor.SPECTRUM.getPropertyName())) {
                item.setSelected(true);
            }

            item.addActionListener(event -> firePropertyChange(PAINTING_MODE, null, c));

            group.add(item);
            plotColorMenu.add(item);
        }

        add(plotColorMenu);
    }

    private void createViewParams() {
        for (final String s : params) {
            JCheckBoxMenuItem item = new JCheckBoxMenuItem(s);
            item.addActionListener(event -> {
                if (event.getSource() != null && event.getSource() instanceof JCheckBoxMenuItem) {
                    JCheckBoxMenuItem i = (JCheckBoxMenuItem) event.getSource();
                    firePropertyChange(s, !i.isSelected(), i.isSelected());
                }
            });
            item.setSelected(!s.equals(XY_MESH) && !s.equals(HIDE_ON_DRAG));

            viewParams.add(item);
        }

        add(viewParams);
    }

    private void createParameters() {
        createSteps();
        createEpsilon();

        add(parameters);
    }

    private void createParamSelection() {
        add(Box.createHorizontalGlue());
        comboboxes = new ParameterComboBox(fnParams);
        comboboxes.addPropertyChangeListener(ParameterComboBox.PARAMETERS_CHANGED, evt -> firePropertyChange(ParameterComboBox.PARAMETERS_CHANGED, null, null));
        add(comboboxes);
    }

    /**
     * Sets function Likelihood function parameters into the 2 Combo Boxes.
     *
     * @param elements Array of parameter's names
     */
    public void setElements(String[] elements) {
        fnParams = elements;
        comboboxes.setElements(elements);
    }

    private void createSteps() {
        Filler filler = new Filler(new Dimension(50, 0), new Dimension(50, 0), new Dimension(32767, 0));
        JPanel comp = new JPanel();
        comp.setLayout(new BoxLayout(comp, BoxLayout.LINE_AXIS));

        // Label
        JLabel label = new JLabel(STEPS);
        label.setMaximumSize(new Dimension(50, 18));

        // Spinner
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(100, MIN_STEPS, MAX_STEPS, 5);
        final JSpinner spinner = new JSpinner(spinnerModel);
        spinner.setMaximumSize(new Dimension(50, 18));
        spinner.setPreferredSize(new Dimension(50, 18));

        // Save Button
        JButton save = new JButton("OK");
        save.setMaximumSize(new Dimension(50, 18));

        save.addActionListener(event -> {
            parameters.getPopupMenu().setVisible(false);
            parameters.setSelected(false);
            firePropertyChange(STEPS, null, spinner.getValue());
        });

        comp.add(label);
        comp.add(filler);
        comp.add(spinner);
        comp.add(save);

        comp.setBorder(new EmptyBorder(1, 5, 1, 5));

        parameters.add(comp);
    }

    private void createEpsilon() {
        Filler filler = new Filler(new Dimension(50, 0), new Dimension(50, 0), new Dimension(32767, 0));
        JPanel comp = new JPanel();
        comp.setLayout(new BoxLayout(comp, BoxLayout.LINE_AXIS));

        // Label
        JLabel label = new JLabel(EPSILON);
        label.setMaximumSize(new Dimension(50, 18));

        // Spinner
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0.2, MIN_EPS, MAX_EPS, 0.05);
        final JSpinner spinner = new JSpinner(spinnerModel);
        spinner.setMaximumSize(new Dimension(50, 18));
        spinner.setPreferredSize(new Dimension(50, 18));

        // Save Button
        JButton save = new JButton("OK");
        save.setMaximumSize(new Dimension(50, 18));
        save.addActionListener(event -> {
            parameters.getPopupMenu().setVisible(false);
            parameters.setSelected(false);
            firePropertyChange(EPSILON, null, spinner.getValue());
        });

        comp.add(label);
        comp.add(filler);
        comp.add(spinner);
        comp.add(save);

        comp.setBorder(new EmptyBorder(1, 5, 1, 5));

        parameters.add(comp);
    }
}
