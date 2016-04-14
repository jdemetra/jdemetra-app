/*
 * Copyright 2013 National Bank of Belgium
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
package ec.ui;

import ec.nbdemetra.ui.IConfigurable;
import ec.nbdemetra.ui.ThemeSupport;
import ec.nbdemetra.ui.awt.ActionMaps;
import ec.nbdemetra.ui.awt.InputMaps;
import ec.nbdemetra.ui.awt.JComponent2;
import ec.tss.tsproviders.utils.DataFormat;
import ec.ui.commands.TsControlCommand;
import ec.ui.interfaces.IColorSchemeAble;
import ec.ui.interfaces.ITsControl;
import ec.ui.interfaces.ITsHelper;
import ec.ui.interfaces.ITsPrinter;
import ec.util.various.swing.JCommand;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import javax.swing.ActionMap;
import javax.swing.InputMap;

public abstract class ATsControl extends JComponent2 implements ITsControl, ClipboardOwner {

    private static final long serialVersionUID = 3804565526142589316L;

    // KEY ACTIONS
    public static final String PRINT_ACTION = "print";
    public static final String CONFIGURE_ACTION = "configure";
    public static final String FORMAT_ACTION = "format";

    // TODO: implements default behavior
    protected TooltipType m_tooltip = TooltipType.None;
    protected boolean m_toolwindow = false;
    protected final ThemeSupport themeSupport;

    public ATsControl() {
        this.themeSupport = new ThemeSupport() {
            @Override
            protected void dataFormatChanged() {
                ATsControl.this.firePropertyChange(DATA_FORMAT_PROPERTY, null, getDataFormat());
            }

            @Override
            protected void colorSchemeChanged() {
                ATsControl.this.firePropertyChange(IColorSchemeAble.COLOR_SCHEME_PROPERTY, null, getColorScheme());
            }
        };

        enableProperties();
        // DEBUG
        //this.addPropertyChangeListener(new OuputPropertyChangeListener());

        themeSupport.register();
        registerActions();
    }

    private void registerActions() {
        ActionMap am = getActionMap();
        am.put(PRINT_ACTION, TsControlCommand.printPreview().toAction(this));
        am.put(CONFIGURE_ACTION, ConfigureCommand.INSTANCE.toAction(this));
        am.put(FORMAT_ACTION, TsControlCommand.editDataFormat().toAction(this));
    }

    private void enableProperties() {
        this.addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case DATA_FORMAT_PROPERTY:
                    onDataFormatChange();
                    break;
                case IColorSchemeAble.COLOR_SCHEME_PROPERTY:
                    onColorSchemeChange();
                    break;
            }
        });
    }

    //<editor-fold defaultstate="collapsed" desc="Event handlers">
    abstract protected void onDataFormatChange();

    abstract protected void onColorSchemeChange();
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    @Override
    public DataFormat getDataFormat() {
        return themeSupport.getLocalDataFormat();
    }

    @Override
    public void setDataFormat(DataFormat dataFormat) {
        themeSupport.setLocalDataFormat(dataFormat);
    }
    //</editor-fold>

    @Override
    public void dispose() {
    }

    @Override
    public TooltipType getTsTooltip() {
        return m_tooltip;
    }

    @Override
    public void setTsTooltip(TooltipType tooltipType) {
        m_tooltip = tooltipType;
    }

    @Override
    public boolean isToolWindowLayout() {
        return m_toolwindow;
    }

    @Override
    public void setToolWindowLayout(boolean behavior) {
        m_toolwindow = behavior;
    }

    @Override
    public ITsHelper getHelper() {
        return null;
    }

    @Override
    public ITsPrinter getPrinter() {
        return null;
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        // Do nothing
    }

    @Deprecated
    protected Transferable getClipboardContents() {
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        return cb.getContents(this);
    }

    @Deprecated
    protected void setClipboardContents(Transferable transferable) {
        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
        cb.setContents(transferable, this);
    }

    @Deprecated
    protected void fillActionMap(ActionMap am) {
        ActionMaps.copyEntries(getActionMap(), false, am);
    }

    @Deprecated
    protected void fillInputMap(InputMap im) {
        InputMaps.copyEntries(getInputMap(), false, im);
    }

    //<editor-fold defaultstate="collapsed" desc="Commands">
    private static final class ConfigureCommand extends JCommand<ATsControl> {

        public static final ConfigureCommand INSTANCE = new ConfigureCommand();

        @Override
        public void execute(ATsControl c) throws Exception {
            if (c instanceof IConfigurable) {
                IConfigurable configurable = (IConfigurable) c;
                configurable.setConfig(configurable.editConfig(configurable.getConfig()));
            }
        }
    }
    //</editor-fold>
}
