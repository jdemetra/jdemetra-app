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
package internal.ui.properties;

import demetra.desktop.design.SwingComponent;
import demetra.desktop.design.SwingProperty;
import demetra.desktop.util.ExtLayerUI;
import demetra.desktop.util.FontAwesomeUtils;
import ec.util.chart.swing.SwingColorSchemeSupport;
import ec.util.list.swing.JListOrdering;
import ec.util.list.swing.JLists;
import ec.util.various.swing.FontAwesome;
import ec.util.various.swing.JCommand;
import internal.util.Strings;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;

import javax.swing.*;
import java.awt.*;
import java.beans.BeanInfo;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @author Philippe Charles
 * @since 2.2.0
 */
@SwingComponent
public final class JAutoCompletedComponent extends JComponent {

    @SwingProperty
    public static final String VALUE_PROPERTY = "value";

    @SwingProperty
    public static final String AUTO_COMPLETION_PROPERTY = "autoCompletion";

    @SwingProperty
    public static final String DEFAULT_VALUE_SUPPLIER_PROPERTY = "defaultValueSupplier";

    @SwingProperty
    public static final String SEPARATOR_PROPERTY = "separator";

    @SwingProperty
    public static final String RUNNING_PROPERTY = "running";

    private final DefaultListModel<String> model;
    private final JListOrdering<String> list;

    private String value;
    private Consumer<JTextField> autoCompletion;
    private Callable<String> defaultValueSupplier;
    private String separator;
    private boolean running;
    private boolean updating;

    public JAutoCompletedComponent() {
        this.model = new DefaultListModel<>();
        this.list = new JListOrdering<>();

        this.value = "";
        this.autoCompletion = null;
        this.defaultValueSupplier = null;
        this.separator = ",";
        this.running = false;
        this.updating = false;

        initComponents();
        enableProperties();
    }

    private void initComponents() {
        list.setModel(model);
        list.setComponentPopupMenu(buildMenu().getPopupMenu());

        model.addListDataListener(JLists.dataListenerOf(evt -> {
            if (!updating) {
                setValue(String.join(separator, JLists.asList(model)));
            }
        }));

        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        setLayout(new BorderLayout());
        add(new JLayer<>(list, new ExtLayerUI<>(new LoadingRenderer())));
        add(buildToolBar(), BorderLayout.EAST);
    }

    private void enableProperties() {
        addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case VALUE_PROPERTY:
                    onValueChange();
                    break;
                case SEPARATOR_PROPERTY:
                    onSeparatorChange();
                    break;
                case RUNNING_PROPERTY:
                    onRunningChange();
                    break;
            }
        });
    }

    //<editor-fold defaultstate="collapsed" desc="Events handlers">
    private void onValueChange() {
        updating = true;
        model.clear();
        Strings.splitToStream(separator, value)
                .map(String::trim)
                .filter(o -> !o.isEmpty())
                .forEach(model::addElement);
        updating = false;
    }

    private void onSeparatorChange() {
        onValueChange();
    }

    private void onRunningChange() {
        list.setEnabled(!running);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getter/Setters">
    @NonNull
    public String getValue() {
        return value;
    }

    public void setValue(@Nullable String value) {
        String old = this.value;
        this.value = value != null ? value : "";
        firePropertyChange(VALUE_PROPERTY, old, this.value);
    }

    @Nullable
    public Consumer<JTextField> getAutoCompletion() {
        return autoCompletion;
    }

    public void setAutoCompletion(@Nullable Consumer<JTextField> autoCompletion) {
        Consumer<JTextField> old = this.autoCompletion;
        this.autoCompletion = autoCompletion;
        firePropertyChange(AUTO_COMPLETION_PROPERTY, old, this.autoCompletion);
    }

    @Nullable
    public Callable<String> getDefaultValueSupplier() {
        return defaultValueSupplier;
    }

    public void setDefaultValueSupplier(@Nullable Callable<String> defaultValueSupplier) {
        Callable<String> old = this.defaultValueSupplier;
        this.defaultValueSupplier = defaultValueSupplier;
        firePropertyChange(DEFAULT_VALUE_SUPPLIER_PROPERTY, old, this.defaultValueSupplier);
    }

    @NonNull
    public String getSeparator() {
        return separator;
    }

    public void setSeparator(@Nullable String separator) {
        String old = this.separator;
        this.separator = separator != null ? separator : ",";
        firePropertyChange(SEPARATOR_PROPERTY, old, this.separator);
    }

    public boolean isRunning() {
        return running;
    }

    private void setRunning(boolean running) {
        boolean old = this.running;
        this.running = running;
        firePropertyChange(RUNNING_PROPERTY, old, this.running);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    private JMenu buildMenu() {
        JMenu result = new JMenu();
        result.add(list.getActionMap().get(JListOrdering.MOVE_UP_ACTION)).setText("Move up");
        result.add(list.getActionMap().get(JListOrdering.MOVE_DOWN_ACTION)).setText("Move down");
        return result;
    }

    private JToolBar buildToolBar() {
        JToolBar result = new JToolBar();
        result.setOrientation(JToolBar.VERTICAL);
        result.setFloatable(false);

        JButton button;

        button = result.add(new Magic().toAction(this));
        button.setIcon(FontAwesomeUtils.getIcon(FontAwesome.FA_MAGIC, BeanInfo.ICON_MONO_16x16));
        button.setToolTipText("Retrieve the default values");
        button = result.add(new Add().toAction(this));
        button.setIcon(FontAwesomeUtils.getIcon(FontAwesome.FA_PLUS, BeanInfo.ICON_MONO_16x16));
        button.setToolTipText("Add a new value");
        button = result.add(new Remove().toAction(this));
        button.setIcon(FontAwesomeUtils.getIcon(FontAwesome.FA_MINUS, BeanInfo.ICON_MONO_16x16));
        button.setToolTipText("Remove the selected values");

        result.addSeparator();
        button = result.add(list.getActionMap().get(JListOrdering.MOVE_UP_ACTION));
        button.setIcon(FontAwesomeUtils.getIcon(FontAwesome.FA_CARET_UP, BeanInfo.ICON_MONO_16x16));
        button.setToolTipText("Move up the selected value");
        button = result.add(list.getActionMap().get(JListOrdering.MOVE_DOWN_ACTION));
        button.setIcon(FontAwesomeUtils.getIcon(FontAwesome.FA_CARET_DOWN, BeanInfo.ICON_MONO_16x16));
        button.setToolTipText("Move down the selected value");

        result.addSeparator();
        button = result.add(new Help().toAction(this));
        button.setIcon(FontAwesomeUtils.getIcon(FontAwesome.FA_QUESTION, BeanInfo.ICON_MONO_16x16));
        button.setToolTipText("Show help");

        return result;
    }

    private static final class Add extends JCommand<JAutoCompletedComponent> {

        @Override
        public void execute(JAutoCompletedComponent c) throws Exception {
            NotifyDescriptor.InputLine d = new NotifyDescriptor.InputLine("Value:", "Add") {
                {
                    if (c.autoCompletion != null) {
                        c.autoCompletion.accept(textField);
                    }
                }
            };
            if (DialogDisplayer.getDefault().notify(d) == NotifyDescriptor.OK_OPTION) {
                String value = d.getInputText();
                if (value != null && value.length() > 0) {
                    c.model.addElement(value);
                    int idx = c.model.getSize() - 1;
                    c.list.getSelectionModel().setSelectionInterval(idx, idx);
                }
            }
        }

        @Override
        public boolean isEnabled(JAutoCompletedComponent c) {
            return !c.isRunning();
        }

        @Override
        public ActionAdapter toAction(JAutoCompletedComponent c) {
            return super.toAction(c).withWeakPropertyChangeListener(c);
        }
    }

    private static final class Remove extends JCommand<JAutoCompletedComponent> {

        @Override
        public void execute(JAutoCompletedComponent c) throws Exception {
            int[] indices = JLists.getSelectionIndexStream(c.list.getSelectionModel()).toArray();
            for (int i = indices.length - 1; i >= 0; i--) {
                c.model.remove(indices[i]);
            }
            c.list.getSelectionModel().clearSelection();
        }

        @Override
        public boolean isEnabled(JAutoCompletedComponent c) {
            return !c.isRunning() && !c.list.getSelectionModel().isSelectionEmpty();
        }

        @Override
        public JCommand.ActionAdapter toAction(JAutoCompletedComponent c) {
            return super.toAction(c)
                    .withWeakPropertyChangeListener(c)
                    .withWeakListSelectionListener(c.list.getSelectionModel());
        }
    }

    private static final class Help extends JCommand<JAutoCompletedComponent> {

        @Override
        public void execute(JAutoCompletedComponent c) throws Exception {
        }

        @Override
        public boolean isEnabled(JAutoCompletedComponent component) {
            return false;
        }
    }

    private static final class Magic extends JCommand<JAutoCompletedComponent> {

        @Override
        public void execute(JAutoCompletedComponent c) throws Exception {
            if (c.defaultValueSupplier != null) {
                c.setRunning(true);
                new SwingWorker<String, Void>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        return c.defaultValueSupplier.call();
                    }

                    @Override
                    protected void done() {
                        try {
                            c.setValue(get());
                        } catch (InterruptedException | ExecutionException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                        c.setRunning(false);
                    }
                }.execute();
            }
        }

        @Override
        public boolean isEnabled(JAutoCompletedComponent c) {
            return c.defaultValueSupplier != null;
        }

        @Override
        public ActionAdapter toAction(JAutoCompletedComponent c) {
            return super.toAction(c).withWeakPropertyChangeListener(c, DEFAULT_VALUE_SUPPLIER_PROPERTY);
        }
    }

    private static final class LoadingRenderer implements Function<JListOrdering, Component>, Icon {

        private final JLabel label;
        private Icon iconStrongRef;

        private LoadingRenderer() {
            this.label = new JLabel();
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setHorizontalTextPosition(JLabel.CENTER);
            label.setVerticalTextPosition(JLabel.BOTTOM);
        }

        @Override
        public Component apply(JListOrdering view) {
            if (!view.isEnabled()) {
                if (iconStrongRef == null) {
                    iconStrongRef = FontAwesome.FA_SPINNER.getSpinningIcon(view, view.getForeground(), 24f);
                }
                label.setText("<html><center><b>Loading");
                label.setIcon(this);
                label.setBackground(SwingColorSchemeSupport.withAlpha(view.getBackground(), 200));
                label.setOpaque(true);
            } else {
                if (iconStrongRef != null) {
                    iconStrongRef = null;
                }
                if (view.getModel().getSize() == 0) {
                    label.setText("<html><center><b>No value defined</b><br>Use the toolbar on the right to add new values");
                    label.setIcon(null);
                    label.setBackground(view.getBackground());
                    label.setOpaque(true);
                } else {
                    label.setText(null);
                    label.setIcon(FontAwesome.FA_ARROW_DOWN.getIcon(SwingColorSchemeSupport.withAlpha(Color.LIGHT_GRAY, 30), label.getFont().getSize2D() * 10));
                    label.setBackground(view.getBackground());
                    label.setOpaque(false);
                }
            }
            return label;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (iconStrongRef != null) {
                iconStrongRef.paintIcon(c, g, x, y);
            }
        }

        @Override
        public int getIconWidth() {
            return iconStrongRef != null ? iconStrongRef.getIconWidth() : 0;
        }

        @Override
        public int getIconHeight() {
            return iconStrongRef != null ? iconStrongRef.getIconHeight() : 0;
        }
    }
    //</editor-fold>
}
