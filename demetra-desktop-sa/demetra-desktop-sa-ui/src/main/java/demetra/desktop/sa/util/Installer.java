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
package demetra.desktop.sa.util;

import demetra.desktop.Config;
import demetra.desktop.sa.output.OutputFactoryBuddies;
import demetra.desktop.sa.properties.l2fprod.SaInterventionVariableDescriptor;
import demetra.desktop.sa.properties.l2fprod.SaInterventionVariablesEditor;
import demetra.desktop.sa.properties.l2fprod.SaTsVariableDescriptor;
import demetra.desktop.sa.properties.l2fprod.SaTsVariableDescriptorsEditor;
import demetra.desktop.sa.ui.DemetraSaUI;
import demetra.desktop.ui.properties.l2fprod.ArrayRenderer;
import demetra.desktop.ui.properties.l2fprod.CustomPropertyEditorRegistry;
import demetra.desktop.ui.properties.l2fprod.CustomPropertyRendererFactory;
import demetra.desktop.util.InstallerStep;
import static demetra.desktop.util.InstallerStep.put;
import static demetra.desktop.util.InstallerStep.tryGet;
import java.util.prefs.BackingStoreException;
import org.openide.modules.ModuleInstall;

import java.util.prefs.Preferences;
import org.openide.util.Exceptions;

public final class Installer extends ModuleInstall {

    public static final InstallerStep STEP = InstallerStep.all(
            new DemetraSaOptionsStep(), new SaOutputStep(), new PropertiesStep()
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

    private static final class DemetraSaOptionsStep extends InstallerStep {

        final Preferences prefs = prefs().node("options");

        @Override
        public void restore() {
            DemetraSaUI ui = DemetraSaUI.get();
            tryGet(prefs).ifPresent(ui::setConfig);
        }

        @Override
        public void close() {
            DemetraSaUI ui = DemetraSaUI.get();
            put(prefs, ui.getConfig());
            try {
                prefs.flush();
            } catch (BackingStoreException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
    
        private static final class SaOutputStep extends InstallerStep {

        final Preferences prefs = prefs().node("outputs");

        @Override
        public void restore() {
            OutputFactoryBuddies.getInstance().getFactories().forEach(buddy->{
                    Preferences nprefs = prefs.node(buddy.getDisplayName());
                    tryGet(nprefs).ifPresent(buddy::setConfig);
            });
        }

        @Override
        public void close() {
            OutputFactoryBuddies.getInstance().getFactories().forEach(buddy->{
                Config config = buddy.getConfig();
                if (config != null){
                    Preferences nprefs = prefs.node(buddy.getDisplayName());
                    put(nprefs, config);
                    try {
                        nprefs.flush();
                    } catch (BackingStoreException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            });
        }
    }
    


    private static final class PropertiesStep extends InstallerStep {

        @Override
        public void restore() {
            CustomPropertyEditorRegistry.INSTANCE.register(SaInterventionVariableDescriptor[].class, new SaInterventionVariablesEditor());
            CustomPropertyRendererFactory.INSTANCE.getRegistry().registerRenderer(SaInterventionVariableDescriptor[].class, new ArrayRenderer());
            CustomPropertyEditorRegistry.INSTANCE.register(SaTsVariableDescriptor[].class, new SaTsVariableDescriptorsEditor());
            CustomPropertyRendererFactory.INSTANCE.getRegistry().registerRenderer(SaTsVariableDescriptor[].class, new ArrayRenderer());
            CustomPropertyEditorRegistry.INSTANCE.register(SaInterventionVariableDescriptor[].class, new SaInterventionVariablesEditor());
        }

        @Override
        public void close() {
            CustomPropertyEditorRegistry.INSTANCE.unregister(SaInterventionVariableDescriptor[].class);
            CustomPropertyRendererFactory.INSTANCE.getRegistry().unregisterRenderer(SaInterventionVariableDescriptor[].class);
            CustomPropertyEditorRegistry.INSTANCE.unregister(SaTsVariableDescriptor[].class);
            CustomPropertyRendererFactory.INSTANCE.getRegistry().unregisterRenderer(SaTsVariableDescriptor[].class);
        }
    }
}
