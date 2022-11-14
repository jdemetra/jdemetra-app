/*
 * Copyright 2015 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
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
package demetra.desktop.sa.diagnostics;

import demetra.desktop.Config;
import demetra.desktop.Converter;
import demetra.desktop.DemetraIcons;
import demetra.desktop.actions.Configurable;
import demetra.desktop.properties.PropertySheetDialogBuilder;
import demetra.processing.DiagnosticsConfiguration;
import java.awt.Image;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;

/**
 *
 * Link between the GUI and the actual SaDiagnosticsFactory
 * @param <C> Actual converter
 * @param <B> Bean
 */
public abstract class AbstractSaDiagnosticsFactoryBuddy<C extends DiagnosticsConfiguration, B> implements SaDiagnosticsFactoryBuddy<C>, Configurable{
    
    private C core;
    private B bean;
    
    private final Converter<C, Config> coreConverter;
    private final Converter<B, C> beanConverter;
    
    protected AbstractSaDiagnosticsFactoryBuddy(final Converter<C, Config> coreConverter, final Converter<B, C> beanConverter){
        this.coreConverter=coreConverter;
        this.beanConverter=beanConverter;
    }
    
    protected B bean(){
        return bean;
    }
    
    protected C core(){
        return core;
    }
    
    protected void setCore(C core){
        this.core=core;
        bean=beanConverter.doBackward(core);
    }
    
    @Override
    public void configure(){
        editConfiguration();
    }
    
    // based on the current bean
    public abstract AbstractSaDiagnosticsNode createNode();
    
    
    /**
     * Updates the config and its bean
     * @param config 
     */
    @Override
    public void setConfig(@NonNull Config config){
        core=coreConverter.doBackward(config);
        bean=beanConverter.doBackward(core);
    }
    
    @Override
    public Config getConfig(){
        return coreConverter.doForward(core);
    }
    
    /**
     * Configuration after edition. Not necessary applied
     * @return 
     */
    @Override
    public C getCurrentDiagnosticsConfiguration(){
        return beanConverter.doForward(bean);
    }

    /**
     * Active (used in the algorithms) configuration
     * @return 
     */
    @Override
    public C getActiveDiagnosticsConfiguration(){
        return core;
    }
    
    @Override
    public void setActiveDiagnosticsConfiguration(C config){
        setCore(config);
    }
    
    @Override
    public boolean editConfiguration() {
            return new PropertySheetDialogBuilder()
                    .title(getDisplayName())
                    .editNode(createNode());
    }
    
    @Override
    public void commit(){
        core=beanConverter.doForward(bean);
    }
    
    @Override
    public void restore(){
        bean=beanConverter.doBackward(core);
    }
    
    @Override
    public boolean valid(){
        return beanConverter.doForward(bean) != null;
    }

    @Override
    public String getDisplayName() {
        return getName();
    }

    @Override
    public Image getIcon(int type, boolean opened) {
        return ImageUtilities.icon2Image(DemetraIcons.PUZZLE_16);
    }

    @Override
    public Sheet createSheet() {
        return new Sheet();
    }
}
