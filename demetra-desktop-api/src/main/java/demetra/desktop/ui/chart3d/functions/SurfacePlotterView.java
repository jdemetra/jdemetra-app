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

import demetra.data.DoubleSeq;
import demetra.desktop.components.JExceptionPanel;
import demetra.desktop.ui.processing.TsViewToolkit;
import demetra.desktop.ui.chart3d.JSurfacePanel;
import demetra.desktop.ui.chart3d.SurfaceModel;
import demetra.desktop.ui.chart3d.SurfaceModel.PlotType;

import javax.swing.*;
import java.awt.*;
import jdplus.data.DataBlock;
import jdplus.math.functions.IFunction;
import jdplus.math.functions.IFunctionPoint;
import jdplus.math.functions.IParametersDomain;

/**
 * Main view displaying the results in the 3D plot
 *
 * @author Mats Maggi
 */
public class SurfacePlotterView extends JPanel {

    private final JSurfacePanel panel;
    private ConfigurationToolBar config;
    private String[] elements;
    private FunctionsSurfaceModel m;
    private JFunctions2DChart chart;

    public SurfacePlotterView() {
        setLayout(new BorderLayout());
        elements = new String[]{"", ""};
        panel = new JSurfacePanel();
    }

    private void setParameters(int index1, int index2) {
        m.setP1Index(index1);
        m.setP2Index(index2);
        m.generateData();
        panel.setModel(m);
        panel.getSurface().setOptimum(m.getOptimum());
        panel.getSurface().setXLabel(elements[index1]);
        panel.getSurface().setYLabel(elements[index2]);
        panel.getSurface().repaint();
    }

    public void setFunctions(IFunction f, IFunctionPoint maxF, int steps) {
        if (f == null || maxF == null) {
            throw new IllegalArgumentException("The functions can't be null !");
        }
        int nbParams = 0;
        if (maxF.getParameters() != null) {
            nbParams = maxF.getParameters().length();
        }

        try {
            removeAll();
            switch (nbParams) {
                case 0:
                    JComponent msg = TsViewToolkit.getMessageViewer("The given function doesn't contain any parameters");
                    add(msg, BorderLayout.CENTER);
                    break;
                case 1:
                    chart = new JFunctions2DChart(f, maxF, 100);
                    chart.generateData();
                    config = new ConfigurationToolBar(elements, false);
                    config.addPropertyChangeListener(evt -> {
                        switch (evt.getPropertyName()) {
                            case ConfigurationToolBar.STEPS:
                                chart.setSteps((Integer) evt.getNewValue());
                                break;
                            case ConfigurationToolBar.EPSILON:
                                chart.setEpsilon(((Double) evt.getNewValue()).floatValue());
                                break;
                        }
                    });

                    add(chart, BorderLayout.CENTER);
                    add(config, BorderLayout.NORTH);
                    break;
                default:
                    config = new ConfigurationToolBar(elements, true);

                    config.addPropertyChangeListener(evt -> {
                        if (panel.getSurface().getModel() != null
                                && panel.getSurface().getModel() instanceof FunctionsSurfaceModel) {
                            FunctionsSurfaceModel m1 = (FunctionsSurfaceModel) panel.getSurface().getModel();
                            switch (evt.getPropertyName()) {
                                case ConfigurationToolBar.XY_TICKS:
                                    m1.setDisplayXY((Boolean) evt.getNewValue());
                                    break;
                                case ConfigurationToolBar.Z_TICKS:
                                    m1.setDisplayZ((Boolean) evt.getNewValue());
                                    break;
                                case ConfigurationToolBar.BOX_GRID:
                                    m1.setDisplayGrids((Boolean) evt.getNewValue());
                                    break;
                                case ConfigurationToolBar.XY_MESH:
                                    m1.setMesh((Boolean) evt.getNewValue());
                                    break;
                                case ConfigurationToolBar.DRAW_BOX:
                                    m1.setBoxed((Boolean) evt.getNewValue());
                                    break;
                                case ConfigurationToolBar.HIDE_ON_DRAG:
                                    m1.setExpectDelay((Boolean) evt.getNewValue());
                                    break;
                                case ConfigurationToolBar.PLOT_TYPE:
                                    m1.setPlotType((PlotType) evt.getNewValue());
                                    break;
                                case ConfigurationToolBar.PAINTING_MODE:
                                    m1.setPlotColor((SurfaceModel.PlotColor) evt.getNewValue());
                                    break;
                                case ConfigurationToolBar.STEPS:
                                    m1.setSteps((Integer) evt.getNewValue());
                                    break;
                                case ConfigurationToolBar.EPSILON:
                                    m1.setEps(((Double) evt.getNewValue()).floatValue());
                                    break;
                                case ParameterComboBox.PARAMETERS_CHANGED:
                                    setParameters(config.getParametersComboBoxes().getSelectedIndex1(), config.getParametersComboBoxes().getSelectedIndex2());
                            }
                        }
                    });

                    m = new FunctionsSurfaceModel(f, maxF, steps);

                    DoubleSeq parameters = maxF.getParameters();
                    DataBlock p = DataBlock.of(parameters);
                    IParametersDomain d = f.getDomain();

                    elements = new String[p.length()];
                    for (int i = 0; i < p.length(); i++) {
                        elements[i] = d.getDescription(i);
                    }

                    config.setElements(elements);
                    m.generateData();
                    panel.setModel(m);
                    panel.getSurface().setOptimum(m.getOptimum());
                    panel.getSurface().setXLabel(elements[0]);
                    panel.getSurface().setYLabel(elements[1]);

                    add(panel, BorderLayout.CENTER);
                    add(config, BorderLayout.NORTH);
            }
        } catch (IllegalArgumentException e) {
            add(JExceptionPanel.create(e), BorderLayout.CENTER);
        }
    }

    public void setEpsilon(float eps) {
        chart.setEpsilon(eps);
    }

    public void setSteps(int steps) {
        chart.setSteps(steps);
    }
}
