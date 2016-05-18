/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.properties;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import static ec.nbdemetra.ui.properties.Util.attr;
import ec.util.completion.ext.DesktopFileAutoCompletionSource;
import ec.util.completion.swing.FileListCellRenderer;
import ec.util.completion.swing.JAutoCompletion;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JComponent;
import javax.swing.JTextField;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;

/**
 *
 * @author Philippe Charles
 */
public class DesktopFilePropertyEditor extends AbstractExPropertyEditor {

    public static final String FILTER_ATTRIBUTE = "filter";
    public static final String PATHS_ATTRIBUTE = "paths";
    //
    private static final ExecutorService ICON_EXECUTOR = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setDaemon(true).build());
    // we cannot extend FileEditor (not a "friend" dependency), so we delegate to the default
    // => cannot register this editor
    final PropertyEditor fileEditor = PropertyEditorManager.findEditor(File.class);

    @Override
    public void setValue(Object value) {
        fileEditor.setValue(value);
    }

    @Override
    public Object getValue() {
        return fileEditor.getValue();
    }

    @Override
    public boolean isPaintable() {
        return fileEditor.isPaintable();
    }

    @Override
    public void paintValue(Graphics gfx, Rectangle box) {
        fileEditor.paintValue(gfx, box);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        fileEditor.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        fileEditor.removePropertyChangeListener(listener);
    }

    @Override
    public void attachEnv(PropertyEnv env) {
        super.attachEnv(env);
        if (fileEditor instanceof ExPropertyEditor) {
            ((ExPropertyEditor) fileEditor).attachEnv(env);
        }
    }

    @Override
    public String getAsText() {
        return fileEditor.getAsText();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        fileEditor.setAsText(text);
    }

    @Override
    public Component getCustomEditor() {
        return fileEditor.getCustomEditor();
    }

    @Override
    public boolean supportsCustomEditor() {
        return fileEditor.supportsCustomEditor();
    }

    @Override
    public String getJavaInitializationString() {
        return fileEditor.getJavaInitializationString();
    }

    @Override
    protected InplaceEditor createInplaceEditor() {
        return new AbstractInplaceEditor() {
            final JTextField component = new JTextField();
            final JAutoCompletion autoCompletion = new JAutoCompletion(component);

            @Override
            public void connect(PropertyEditor propertyEditor, PropertyEnv env) {
                Object tmp = attr(env, FILTER_ATTRIBUTE, Object.class).orElse(null);
                File[] paths = attr(env, PATHS_ATTRIBUTE, File[].class).orElse(new File[0]);
                java.io.FileFilter filter = null;
                if (tmp instanceof java.io.FileFilter) {
                    filter = (java.io.FileFilter) tmp;
                } else if (tmp instanceof javax.swing.filechooser.FileFilter) {
                    filter = toFileFilter((javax.swing.filechooser.FileFilter) tmp);
                } else if (tmp instanceof java.io.FilenameFilter) {
                    filter = toFileFilter((java.io.FilenameFilter) tmp);
                }
                autoCompletion.setSource(new DesktopFileAutoCompletionSource(filter, paths));
                autoCompletion.getList().setCellRenderer(new FileListCellRenderer(null, ICON_EXECUTOR, paths));
                super.connect(propertyEditor, env);
            }

            @Override
            public JComponent getComponent() {
                return component;
            }

            @Override
            public Object getValue() {
                return new File(component.getText());
            }

            @Override
            public void setValue(Object o) {
                component.setText(((File) o).getPath());
            }
        };
    }

    static java.io.FileFilter toFileFilter(javax.swing.filechooser.FileFilter filter) {
        return filter::accept;
    }

    static java.io.FileFilter toFileFilter(java.io.FilenameFilter filter) {
        return o -> filter.accept(o.getParentFile(), o.getName());
    }
}
