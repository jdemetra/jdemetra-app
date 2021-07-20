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
package ec.nbdemetra.ui.chart3d.functions;

import demetra.ui.components.ExceptionPanel;
import ec.nbdemetra.ui.chart3d.JSurfacePanel;
import ec.nbdemetra.ui.chart3d.SurfaceModel;
import ec.nbdemetra.ui.chart3d.SurfaceModel.PlotType;
import ec.tstoolkit.data.DataBlock;
import ec.tstoolkit.data.IReadDataBlock;
import ec.tstoolkit.maths.realfunctions.IFunction;
import ec.tstoolkit.maths.realfunctions.IFunctionInstance;
import ec.tstoolkit.maths.realfunctions.IParametersDomain;
import ec.ui.view.tsprocessing.TsViewToolkit;
import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Main view displaying the results in the 3D plot
 *
 * @author Mats Maggi
 */
public class SurfacePlotterView extends JPanel {

    private JSurfacePanel panel;
    private ConfigurationToolBar config;
    private String[] elements;
    private FunctionsSurfaceModel m;
    private Functions2DChart chart;

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

    public void setFunctions(IFunction f, IFunctionInstance maxF, int steps) {
        if (f == null || maxF == null) {
            throw new IllegalArgumentException("The functions can't be null !");
        }
        int nbParams = 0;
        if (maxF.getParameters() != null) {
            nbParams = maxF.getParameters().getLength();
        }

        try {
            removeAll();
            switch (nbParams) {
                case 0:
                    TsViewToolkit toolkit = TsViewToolkit.getInstance();
                    JComponent msg = toolkit.getMessageViewer("The given function doesn't contain any parameters");
                    add(msg, BorderLayout.CENTER);
                    break;
                case 1:
                    chart = new Functions2DChart(f, maxF, 100);
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

                    IReadDataBlock parameters = maxF.getParameters();
                    DataBlock p = new DataBlock(parameters);
                    IParametersDomain d = f.getDomain();

                    elements = new String[p.getLength()];
                    for (int i = 0; i < p.getLength(); i++) {
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
            add(ExceptionPanel.create(e), BorderLayout.CENTER);
        }
    }

    public void setEpsilon(float eps) {
        chart.setEpsilon(eps);
    }

    public void setSteps(int steps) {
        chart.setSteps(steps);
    }
}
