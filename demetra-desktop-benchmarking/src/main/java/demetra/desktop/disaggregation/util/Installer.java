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
package demetra.desktop.disaggregation.util;

import demetra.desktop.disaggregation.descriptors.BiRatioDescriptor;
import demetra.desktop.disaggregation.descriptors.BiRatioEditor;
import demetra.desktop.disaggregation.descriptors.ShockDescriptor;
import demetra.desktop.disaggregation.descriptors.ShocksEditor;
import demetra.desktop.ui.properties.l2fprod.ArrayRenderer;
import demetra.desktop.ui.properties.l2fprod.CustomPropertyEditorRegistry;
import demetra.desktop.ui.properties.l2fprod.CustomPropertyRendererFactory;
import demetra.desktop.util.InstallerStep;
import org.openide.modules.ModuleInstall;


public final class Installer extends ModuleInstall {

    public static final InstallerStep STEP = InstallerStep.all(
            new PropertiesStep()
    );

    @Override
    public void restored() {
        super.restored();
        STEP.restore();
    }

    @Override
    public void close() {
        STEP.close();
        super.close();
    }

    private static final class PropertiesStep extends InstallerStep {

        @Override
        public void restore() {
            CustomPropertyEditorRegistry.INSTANCE.register(ShockDescriptor[].class, new ShocksEditor());
            CustomPropertyRendererFactory.INSTANCE.getRegistry().registerRenderer(ShockDescriptor[].class, new ArrayRenderer());
            CustomPropertyEditorRegistry.INSTANCE.register(BiRatioDescriptor[].class, new BiRatioEditor());
            CustomPropertyRendererFactory.INSTANCE.getRegistry().registerRenderer(BiRatioDescriptor[].class, new ArrayRenderer());
        }

        @Override
        public void close() {
            CustomPropertyEditorRegistry.INSTANCE.unregister(ShockDescriptor[].class);
            CustomPropertyEditorRegistry.INSTANCE.unregister(BiRatioDescriptor[].class);
        }
    }

}
