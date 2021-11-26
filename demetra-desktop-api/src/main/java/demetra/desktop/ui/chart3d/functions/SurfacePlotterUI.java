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

import demetra.desktop.ui.processing.IProcDocumentView;
import demetra.desktop.ui.chart3d.functions.SurfacePlotterUI.Functions;
import demetra.desktop.ui.processing.PooledItemUI;
import jdplus.math.functions.IFunction;
import jdplus.math.functions.IFunctionPoint;


/**
 *
 * @author Mats Maggi
 * @param <V>
 */
public class SurfacePlotterUI<V extends IProcDocumentView<?>> extends PooledItemUI<V, Functions, SurfacePlotterView> {

    public SurfacePlotterUI() {
        super(SurfacePlotterView.class);
    }

    @Override
    protected void init(SurfacePlotterView c, V host, Functions information) {
        c.setFunctions(information.function, information.maxFunction, 100);
        if (information.eps > 0) {
            c.setEpsilon((float) information.eps);
        }
    }

    public static class Functions {

        public final IFunction function;
        public final IFunctionPoint maxFunction;
        public final double eps;

        public static Functions create(IFunction f, IFunctionPoint max) {
            return new Functions(f, max);
        }

        public static Functions create(IFunction f, IFunctionPoint max, double eps) {
            return new Functions(f, max, eps);
        }

        private Functions(IFunction f, IFunctionPoint maxF) {
            function = f;
            maxFunction = maxF;
            eps = 0;
        }

        private Functions(IFunction f, IFunctionPoint maxF, double e) {
            function = f;
            maxFunction = maxF;
            eps = e;
        }
    }
}
