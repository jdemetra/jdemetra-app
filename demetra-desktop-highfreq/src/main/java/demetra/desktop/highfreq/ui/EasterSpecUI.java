/*
 * Copyright 2023 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop.highfreq.ui;

import demetra.desktop.sa.descriptors.highfreq.AbstractEasterSpecUI;
import demetra.desktop.sa.descriptors.highfreq.HighFreqSpecUI;
import demetra.modelling.highfreq.EasterSpec;

/**
 *
 * @author PALATEJ
 */
public class EasterSpecUI extends AbstractEasterSpecUI {

   private final ExtendedAirlineSpecRoot root;
   
   public EasterSpecUI(ExtendedAirlineSpecRoot root){
       this.root=root;
   }

    @Override
    protected EasterSpec spec() {
        return root.getCore().getRegression().getEaster();
    }

    @Override
    protected HighFreqSpecUI root() {
        return root;
    }


}
