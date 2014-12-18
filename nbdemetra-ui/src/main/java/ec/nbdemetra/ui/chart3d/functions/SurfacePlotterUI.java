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

import ec.nbdemetra.ui.chart3d.functions.SurfacePlotterUI.Functions;
import ec.tstoolkit.maths.realfunctions.IFunction;
import ec.tstoolkit.maths.realfunctions.IFunctionInstance;
import ec.ui.view.tsprocessing.IProcDocumentView;
import ec.ui.view.tsprocessing.PooledItemUI;

/**
 *
 * @author Mats Maggi
 */
public class SurfacePlotterUI<V extends IProcDocumentView<?>> extends PooledItemUI<V, Functions, SurfacePlotterView> {

    public SurfacePlotterUI() {
        super(SurfacePlotterView.class);
    }

    @Override
    protected void init(SurfacePlotterView c, V host, Functions information) {
        c.setFunctions(information.function, information.maxFunction, 100);
    }
    
    public static class Functions {
        public IFunction function;
        public IFunctionInstance maxFunction;
        
        public static Functions create(IFunction f, IFunctionInstance max) {
            return new Functions(f, max);
        }
        
        private Functions(IFunction f, IFunctionInstance maxF) {
            function = f;
            maxFunction = maxF;
        }
    }
}