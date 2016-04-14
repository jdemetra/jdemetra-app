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

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import ec.nbdemetra.ui.DemetraUI;
import ec.nbdemetra.ui.chart3d.AbstractSurfaceModel;
import ec.nbdemetra.ui.chart3d.SurfaceModel.PlotType;
import ec.nbdemetra.ui.chart3d.SurfaceVertex;
import ec.tstoolkit.data.DataBlock;
import ec.tstoolkit.data.IReadDataBlock;
import ec.tstoolkit.maths.realfunctions.IFunction;
import ec.tstoolkit.maths.realfunctions.IFunctionInstance;
import ec.tstoolkit.maths.realfunctions.IParametersDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.SwingWorker;

/**
 * Surface model used to generate likelihood data from a preprocessing model
 *
 * @author Mats Maggi
 */
public class FunctionsSurfaceModel extends AbstractSurfaceModel {

    private SurfaceVertex[] surfaceVertex;
    private final IFunction function;
    private IFunctionInstance maxFunction;
    private int p1_index;
    private int p2_index;
    private int steps;
    private SurfaceVertex optimum;
    private float eps;
    private SwingWorker<Void, Void> worker;
    public static final String PROGRESS_PROPERTY = "progress changed";

    public FunctionsSurfaceModel(IFunction f, IFunctionInstance maxF, int steps) {
        function = f;
        maxFunction = maxF;
        p1_index = 0;
        p2_index = 1;

        if (steps < ConfigurationToolBar.MIN_STEPS || steps > ConfigurationToolBar.MAX_STEPS) {
            throw new IllegalArgumentException("Number of steps must be between "
                    + ConfigurationToolBar.MIN_STEPS + " and " + ConfigurationToolBar.MAX_STEPS + " !");
        }

        this.steps = steps;
        eps = .2f;

        // Setting plot attributes
        setBoxed(true);
        setDisplayXY(true);
        setExpectDelay(false);
        setAutoScaleZ(true);
        setDisplayZ(true);
        setMesh(false);
        setBoxed(true);
        setDisplayGrids(true);
        setPlotType(PlotType.SURFACE);
        setFirstFunctionOnly(true);
    }

    /**
     * Returns the optimum point of the max likelihood function
     *
     * @return optimum point
     */
    public SurfaceVertex getOptimum() {
        return optimum;
    }

    /**
     * Calculates and initializes the array of data used to display the plot.
     * This method is multithreaded to increase performance. The number of
     * threads used is defined by NbDemetra options
     */
    public void generateData() {
        if (function == null || maxFunction == null) {
            throw new IllegalArgumentException("The given functions can't be null !");
        }

        final IReadDataBlock parameters = maxFunction.getParameters();
        final DataBlock p = new DataBlock(parameters);
        final IParametersDomain d = function.getDomain();

        if (p1_index >= parameters.getLength() || p2_index >= parameters.getLength()) {
            throw new IllegalArgumentException("One or more parameters' indexes are out of limits");
        }

        setDataAvailable(false);

        setXMin((float) p.get(p1_index) - eps);
        setXMax((float) p.get(p1_index) + eps);
        setYMin((float) p.get(p2_index) - eps);
        setYMax((float) p.get(p2_index) + eps);
        setCalcDivisions(steps - 1);

        final float stepx = (xMax - xMin) / steps;
        final float stepy = (yMax - yMin) / steps;
        final float xfactor = 20 / (xMax - xMin);
        final float yfactor = 20 / (yMax - yMin);

        final int total = steps * steps;    // Total of values to calculate
        surfaceVertex = new SurfaceVertex[total];
        final float[] fnPts = new float[total];
        for (int i=0; i<fnPts.length; ++i)
            fnPts[i]=Float.NaN;

        // Getting coordinates of optimum point
        float p1 = (float) parameters.get(p1_index);
        float p2 = (float) parameters.get(p2_index);
        optimum = new SurfaceVertex((p1 - xMin) * xfactor - 10, (p2 - yMin) * yfactor - 10, (float) maxFunction.getValue());

        worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Create all tasks calculating the points (x,y,z)
                List<Callable<Void>> tasks = createTasks();
                if (tasks == null) {
                    return null;
                }

                DemetraUI config = DemetraUI.getDefault();
                int nThread = config.getBatchPoolSize().intValue(); // Gets the number of threads
                int priority = config.getBatchPriority().intValue();    // Gets the priority of the processing

                ExecutorService executorService = Executors.newFixedThreadPool(nThread, new ThreadFactoryBuilder().setDaemon(true).setPriority(priority).build());
                try {
                    executorService.invokeAll(tasks);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                executorService.shutdown();
                return null;
            }

            Callable<Void> createFn(final int i, final int j) {
                return () -> {
                    DataBlock p3 = p.deepClone();
                    float xv = xMin + i * stepx;
                    p3.set(p1_index, xv); // Change the value of 1st param (X)
                    float yv = yMin + j * stepy;
                    p3.set(p2_index, yv); // Change the value of 2nd param (Y)
                    float z = Float.NaN;
                    try {
                        if (d.checkBoundaries(p3)) {
                            // Evaluates the value of the z point
                            z = (float) function.evaluate(p3).getValue();
                        }
                    }catch (Exception err) {
                    }
                    if (Float.isInfinite(z)) {
                        z = Float.NaN;
                    }
                    /* Array used is a one dimension array so, the value k represents the position
                    * of the [i][j] value converted into a one dimension index.
                    * If i = 4, j = 5, and steps = 100, then k = 4*100 + 5 = 405
                    */
                    int k = i * steps + j;
                    fnPts[k] = z;
                    return null;
                };
            }

            List<Callable<Void>> createTasks() {
                List<Callable<Void>> list = new ArrayList<>();
                for (int i = 0; i < steps; i++) {
                    for (int j = 0; j < steps; j++) {
                        list.add(createFn(i, j));
                    }
                }
                return list;
            }

            @Override
            protected void done() {
                int q = 0;
                while (q < fnPts.length && Float.isNaN(fnPts[q])) {
                    ++q;
                }
                if (q == fnPts.length) {
                    return;
                }
                z1Min = fnPts[q];
                z1Max = fnPts[q];
                for (int i = q + 1; i < fnPts.length; ++i) {
                    float z = fnPts[i];

                    // Calculating ranges (min/max) of the z value
                    if (!Float.isNaN(z)) {
                        if (z > z1Max) {
                            z1Max = z;
                        } else if (z < z1Min) {
                            z1Min = z;
                        }
                    }
                }

                /* Array used is a one dimension array so, the value k represents the position
                 * of the [i][j] value converted into a one dimension index.
                 * If i = 4, j = 5, and steps = 100, then k = 4*100 + 5 = 405
                 */
                for (int i = 0; i < steps; i++) {
                    for (int j = 0; j < steps; j++) {
                        int k = i * steps + j;
                        float z = fnPts[k];
                        final float xv = xMin + i * stepx;
                        final float yv = yMin + j * stepy;
                        surfaceVertex[k] = new SurfaceVertex((xv - xMin) * xfactor - 10, (yv - yMin) * yfactor - 10, z);
                    }
                }
                
                autoScale();
                setDataAvailable(true);
                fireStateChanged();
            }
        };
        worker.execute();
    }

    /**
     * Defines the value of epsilon (range of the function). Default value is
     * 0.2
     *
     * @param eps New value for epsilon
     */
    public void setEps(float eps) {
        this.eps = eps;
        generateData();
    }

    /**
     * Defines the number of steps (points per parameter) to calculate. A steps'
     * value of 100 will generate 100 x 100 values.
     *
     * @param steps Number of steps
     */
    public void setSteps(int steps) {
        if (steps < ConfigurationToolBar.MIN_STEPS || steps > ConfigurationToolBar.MAX_STEPS) {
            throw new IllegalArgumentException("Number of steps must be between "
                    + ConfigurationToolBar.MIN_STEPS + " and " + ConfigurationToolBar.MAX_STEPS + " !");
        }
        this.steps = steps;
        generateData();
    }

    /**
     * Returns the surface vertex of the model. This array contains all the
     * calculated points generated by calling the <code>generateData()</code>
     * method.
     *
     * @return Array of surface vertices
     */
    @Override
    public SurfaceVertex[] getSurfaceVertex() {
        return surfaceVertex;
    }

    /**
     * Sets the index of the X axis parameter.
     *
     * @param p1_index Index of the parameter to use as X
     */
    public void setP1Index(int p1_index) {
        if (p1_index < 0) {
            throw new IllegalArgumentException("The index can't be < 0 !");
        }
        this.p1_index = p1_index;
    }

    /**
     * Sets the index of the Y axis parameter.
     *
     * @param p2_index Index of the parameter to use as Y
     */
    public void setP2Index(int p2_index) {
        if (p2_index < 0) {
            throw new IllegalArgumentException("The index can't be < 0 !");
        }
        this.p2_index = p2_index;
    }
}
