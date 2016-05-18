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
package ec.nbdemetra.ui.properties;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import ec.tstoolkit.design.IBuilder;
import ec.util.completion.AutoCompletionSource;
import ec.util.completion.AutoCompletionSources;
import java.beans.FeatureDescriptor;
import java.beans.PropertyEditor;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Callable;
import javax.swing.ListCellRenderer;
import javax.swing.text.NumberFormatter;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;

/**
 *
 * @author Philippe Charles
 */
public final class NodePropertySetBuilder implements IBuilder<Node.PropertySet> {

    final List<Node.Property<?>> nodeProperties;
    String name;
    String tabName;
    String displayName;
    String shortDescription;

    public NodePropertySetBuilder() {
        this.nodeProperties = new ArrayList<>();
        this.name = null;
        this.tabName = null;
        this.displayName = null;
        this.shortDescription = null;
    }

    public NodePropertySetBuilder reset(String name) {
        this.nodeProperties.clear();
        this.name = name;
        this.tabName = null;
        this.displayName = null;
        this.shortDescription = null;
        return this;
    }

    public NodePropertySetBuilder name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Specify a group name for one or more PropertySets.
     *
     * @param tabName
     * @return
     * @see
     * http://platform.netbeans.org/tutorials/nbm-nodesapi2.html#separate-property-groups
     */
    public NodePropertySetBuilder group(String tabName) {
        this.tabName = tabName;
        return this;
    }

    public NodePropertySetBuilder display(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public NodePropertySetBuilder description(String shortDescription) {
        this.shortDescription = shortDescription;
        return this;
    }

    public NodePropertySetBuilder add(Node.Property<?> nodeProperty) {
        nodeProperties.add(nodeProperty);
        return this;
    }

    public <T> SelectStep<T, DefaultStep> with(Class<T> valueType) {
        return new SelectStep<T, DefaultStep>(valueType) {
            @Override
            protected DefaultStep<T> next(Node.Property<T> property) {
                return new DefaultStep<>(property);
            }
        };
    }

    public SelectStep<File, FileStep> withFile() {
        return new SelectStep<File, FileStep>(File.class) {
            @Override
            protected FileStep next(Node.Property<File> property) {
                return new FileStep(property);
            }
        };
    }

    public SelectStep<String, AutoCompletedStep> withAutoCompletion() {
        return new SelectStep<String, AutoCompletedStep>(String.class) {
            @Override
            protected AutoCompletedStep next(Node.Property<String> property) {
                return new AutoCompletedStep(property);
            }
        };
    }

    public SelectStep<Boolean, BooleanStep> withBoolean() {
        return new SelectStep<Boolean, BooleanStep>(boolean.class) {
            @Override
            protected BooleanStep next(Node.Property<Boolean> property) {
                return new BooleanStep(property);
            }
        };
    }

    public SelectStep<Integer, IntStep> withInt() {
        return new SelectStep<Integer, IntStep>(int.class) {
            @Override
            protected IntStep next(Node.Property<Integer> property) {
                return new IntStep(property);
            }
        };
    }

    public SelectStep<Double, DoubleStep> withDouble() {
        return new SelectStep<Double, DoubleStep>(double.class) {
            @Override
            protected DoubleStep next(Node.Property<Double> property) {
                return new DoubleStep(property);
            }
        };
    }

    public <T extends Enum<T>> SelectStep<T, EnumStep<T>> withEnum(Class<T> valueType) {
        return new SelectStep<T, EnumStep<T>>(valueType) {
            @Override
            protected EnumStep<T> next(Node.Property<T> property) {
                return new EnumStep<>(property);
            }
        };
    }

    @Override
    public Sheet.Set build() {
        Sheet.Set result = name != null ? new Sheet.Set() : Sheet.createPropertiesSet();
        if (!Strings.isNullOrEmpty(name)) {
            result.setName(name);
        }
        if (!Strings.isNullOrEmpty(tabName)) {
            result.setValue("tabName", tabName);
        }
        if (!Strings.isNullOrEmpty(displayName)) {
            result.setDisplayName(displayName);
        }
        if (!Strings.isNullOrEmpty(shortDescription)) {
            result.setShortDescription(shortDescription);
        }
        result.put(Iterables.toArray(nodeProperties, Node.Property.class));
        return result;
    }

    public abstract class SelectStep<T, NEXT_STEP> {

        final Class<T> valueType;

        SelectStep(Class<T> valueType) {
            this.valueType = valueType;
        }

        public NEXT_STEP select(Node.Property<T> property) {
            return next(property);
        }

        public NEXT_STEP select(Object bean, String property) {
            try {
                return next(new PropertySupport.Reflection<>(bean, valueType, property));
            } catch (NoSuchMethodException ex) {
                throw Throwables.propagate(ex);
            }
        }

        public NEXT_STEP select(Object bean, String getter, String setter) {
            try {
                return next(new PropertySupport.Reflection<>(bean, valueType, getter, setter));
            } catch (NoSuchMethodException ex) {
                throw Throwables.propagate(ex);
            }
        }

        public NEXT_STEP select(String name, T value) {
            return next(new ConstProperty<>(value, name, valueType, null, null));
        }

        public NEXT_STEP selectField(Object bean, String fieldName) {
            return next(FieldNodeProperty.create(bean, valueType, fieldName));
        }

        abstract protected NEXT_STEP next(Node.Property<T> property);
    }

    public abstract class PropertyStep<T, THIS extends PropertyStep> {

        final InternalProperty<T> nodeProperty;

        PropertyStep(Node.Property<T> nodeProperty) {
            this.nodeProperty = new InternalProperty(nodeProperty);
        }

        /**
         * Sets the programmatic name of this feature. This name must be
         * non-null and unique.
         *
         * @see FeatureDescriptor#setName(java.lang.String)
         * @param name The programmatic name of the property/method/event
         * @return a reference to this object.
         */
        public THIS name(String name) {
            nodeProperty.setName(name);
            return (THIS) this;
        }

        /**
         * Sets the localized display name of this feature.
         *
         * @see FeatureDescriptor#setDisplayName(java.lang.String)
         * @param displayName The localized display name for the
         * property/method/event.
         * @return a reference to this object.
         */
        public THIS display(String displayName) {
            nodeProperty.setDisplayName(displayName);
            return (THIS) this;
        }

        /**
         * Sets a variant of the display name containing HTML markup conforming
         * to the limited subset of font-markup HTML supported by the
         * lightweight HTML renderer {@link org.openide.awt.HtmlRenderer} (font
         * color, bold, italic and strike-through supported; font colors can be
         * UIManager color keys if they are prefixed with a ! character, i.e.
         * &lt;font color=&amp;'controlShadow'&gt;). Enclosing HTML tags are not
         * needed.
         * <p>
         * <strong>This method should set either an HTML display name or null;
         * it should not set the non-HTML display name.</strong>
         *
         * @see Property#getHtmlDisplayName()
         * @see org.openide.awt.HtmlRenderer
         * @param htmlDisplayName a String containing conformant, legal HTML
         * markup which represents the display name, or null. The default
         * implementation is null.
         * @return a reference to this object.
         */
        public THIS htmlDisplay(String htmlDisplayName) {
            nodeProperty.setHtmlDisplayName(htmlDisplayName);
            return (THIS) this;
        }

        /**
         * Sets the short description of this feature.
         *
         * @see FeatureDescriptor#setShortDescription(java.lang.String)
         * @param description A localized short description associated with this
         * property/method/event. This defaults to be the display name.
         * @return a reference to this object.
         */
        public THIS description(String description) {
            nodeProperty.setShortDescription(description);
            return (THIS) this;
        }

        /**
         * Associates a named attribute with this feature.
         *
         * @see FeatureDescriptor#setValue(java.lang.String, java.lang.Object)
         * @param attributeName The locale-independent name of the attribute
         * @param value The value.
         * @return a reference to this object.
         */
        public THIS attribute(String attributeName, Object value) {
            nodeProperty.setValue(attributeName, value);
            return (THIS) this;
        }

        /**
         * Sets the property editor explicitly.
         *
         * @param editorType class type of the property editor
         * @return a reference to this object.
         */
        protected THIS editor(Class<? extends PropertyEditor> editorType) {
            nodeProperty.setPropertyEditorClass(editorType);
            return (THIS) this;
        }

        /**
         * Adds this {@link Property} to the builder.
         *
         * @return a reference to the builder
         */
        public NodePropertySetBuilder add() {
            return NodePropertySetBuilder.this.add(nodeProperty);
        }
    }

    public final class DefaultStep<T> extends PropertyStep<T, DefaultStep> {

        DefaultStep(Node.Property<T> nodeProperty) {
            super(nodeProperty);
        }

        @Override
        public DefaultStep editor(Class<? extends PropertyEditor> editor) {
            return super.editor(editor);
        }
    }

    public final class AutoCompletedStep extends PropertyStep<String, AutoCompletedStep> {

        AutoCompletedStep(Node.Property<String> nodeProperty) {
            super(nodeProperty);
            editor(AutoCompletedPropertyEditor3.class);
        }

        public AutoCompletedStep servicePath(String path) {
            return attribute(AutoCompletedPropertyEditor3.SERVICE_PATH_ATTRIBUTE, path);
        }

        public AutoCompletedStep promptText(String text) {
            return attribute(AutoCompletedPropertyEditor3.PROMPT_TEXT_ATTRIBUTE, text);
        }

        public AutoCompletedStep autoFocus(boolean autoFocus) {
            return attribute(AutoCompletedPropertyEditor3.AUTO_FOCUS_ATTRIBUTE, autoFocus);
        }

        public AutoCompletedStep delay(int delay) {
            return attribute(AutoCompletedPropertyEditor3.DELAY_ATTRIBUTE, delay);
        }

        public AutoCompletedStep minLength(int minLength) {
            return attribute(AutoCompletedPropertyEditor3.MIN_LENGTH_ATTRIBUTE, minLength);
        }

        public AutoCompletedStep separator(String separator) {
            return attribute(AutoCompletedPropertyEditor3.SEPARATOR_ATTRIBUTE, separator);
        }

        public AutoCompletedStep source(AutoCompletionSource source) {
            return attribute(AutoCompletedPropertyEditor3.SOURCE_ATTRIBUTE, source);
        }

        public AutoCompletedStep source(Object... list) {
            return source(Arrays.asList(list));
        }

        public AutoCompletedStep source(Iterable<?> list) {
            return source(AutoCompletionSources.of(false, list));
        }

        public AutoCompletedStep cellRenderer(ListCellRenderer cellRenderer) {
            return attribute(AutoCompletedPropertyEditor3.CELL_RENDERER_ATTRIBUTE, cellRenderer);
        }

        public AutoCompletedStep defautlValueSupplier(Callable<String> defautlValueSupplier) {
            return attribute(AutoCompletedPropertyEditor3.DEFAULT_VALUE_SUPPLIER_ATTRIBUTE, defautlValueSupplier);
        }

        public AutoCompletedStep defautlValueSupplier(String defautlValue) {
            return defautlValueSupplier(() -> defautlValue);
        }
    }

    /**
     * http://bits.netbeans.org/dev/javadoc/org-openide-explorer/org/openide/explorer/doc-files/propertyViewCustomization.html
     */
    public final class FileStep extends PropertyStep<File, FileStep> {

        FileStep(Node.Property<File> nodeProperty) {
            super(nodeProperty);
            editor(DesktopFilePropertyEditor.class);
            //editor(FileEditor.class);
        }

        public FileStep paths(File[] paths) {
            return attribute(DesktopFilePropertyEditor.PATHS_ATTRIBUTE, paths);
        }

        /**
         * the value represents filter for the file dialog
         *
         * @param fileFilter
         * @return
         */
        public FileStep filterForSwing(javax.swing.filechooser.FileFilter fileFilter) {
            return attribute("filter", fileFilter);
        }

        /**
         * the value represents filter for the file dialog
         *
         * @param fileFilter
         * @return
         */
        public FileStep filter(java.io.FileFilter fileFilter) {
            return attribute("filter", fileFilter);
        }

        /**
         * the value represents filter for the file dialog
         *
         * @param fileNameFilter
         * @return
         */
        public FileStep filterByName(java.io.FilenameFilter fileNameFilter) {
            return attribute("filter", fileNameFilter);
        }

        /**
         * should directories be selectable as values for the property
         *
         * @param selectables
         * @return
         */
        public FileStep directories(boolean selectables) {
            return attribute("directories", selectables);
        }

        /**
         * should files be selectable as values for the property
         *
         * @param selectables
         * @return
         */
        public FileStep files(boolean selectables) {
            return attribute("files", selectables);
        }

        /**
         * the dir that should be preselected when displaying the dialog
         *
         * @param file
         * @return
         */
        public FileStep currentDir(File file) {
            return attribute("currentDir", file);
        }

        /**
         * an absolute directory which can be used as a base against which
         * relative filenames should be interpreted. Incoming relative paths may
         * be resolved against this base directory when e.g. opening a file
         * chooser, as with the two-argument File constructors. Outgoing paths
         * which can be expressed relative to this base directory may be
         * relativized, according to the discretion of the implementation;
         * currently files selected in the file chooser which are under the base
         * directory (including the base directory itself) will be relativized,
         * while others will be left absolute. The empty abstract pathname (new
         * File("")) is used to represent the base directory itself.
         *
         * @param file
         * @return
         */
        public FileStep baseDir(File file) {
            return attribute("baseDir", file);
        }
    }

    public final class BooleanStep extends PropertyStep<Boolean, BooleanStep> {

        BooleanStep(Node.Property<Boolean> nodeProperty) {
            super(nodeProperty);
        }
    }

    public final class IntStep extends PropertyStep<Integer, IntStep> {

        IntStep(Node.Property<Integer> nodeProperty) {
            super(nodeProperty);
            editor(JSpinFieldPropertyEditor.class);
        }

        public IntStep max(int max) {
            return attribute(JSpinFieldPropertyEditor.MAX_ATTRIBUTE, max);
        }

        public IntStep min(int min) {
            return attribute(JSpinFieldPropertyEditor.MIN_ATTRIBUTE, min);
        }
    }

    public final class DoubleStep extends PropertyStep<Double, DoubleStep> {

        final NumberFormatter formatter;

        DoubleStep(Node.Property<Double> nodeProperty) {
            super(nodeProperty);
            formatter = new NumberFormatter();
            editor(FormattedPropertyEditor.class);
            attribute(FormattedPropertyEditor.FORMATTER_ATTRIBUTE, formatter);
        }

        public DoubleStep max(double max) {
            formatter.setMaximum(max);
            return this;
        }

        public DoubleStep min(double min) {
            formatter.setMinimum(min);
            return this;
        }
    }

    public final class EnumStep<T extends Enum<T>> extends PropertyStep<T, EnumStep<T>> {

        public EnumStep(Property<T> nodeProperty) {
            super(nodeProperty);
            editor(ComboBoxPropertyEditor.class);
            of(EnumSet.allOf(nodeProperty.getValueType()));
        }

        public EnumStep<T> of(T... values) {
            return attribute(ComboBoxPropertyEditor.VALUES_ATTRIBUTE, values);
        }

        public EnumStep<T> of(Iterable<T> values) {
            return of(Iterables.toArray(values, nodeProperty.getValueType()));
        }

        public EnumStep<T> noneOf(T... values) {
            EnumSet tmp = EnumSet.allOf(nodeProperty.getValueType());
            tmp.removeAll(Arrays.asList(values));
            return of(tmp);
        }

        public EnumStep<T> noneOf(Iterable<T> values) {
            EnumSet tmp = EnumSet.allOf(nodeProperty.getValueType());
            tmp.removeAll(Lists.newArrayList(values));
            return of(tmp);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Custom node properties">
    private final static class InternalProperty<T> extends ForwardingNodeProperty<T> {

        private Class<? extends PropertyEditor> editorType;
        private String htmlDisplayName;

        public InternalProperty(Node.Property<T> p) {
            super(p);
            this.editorType = null;
            this.htmlDisplayName = null;
        }

        @Override
        public PropertyEditor getPropertyEditor() {
            if (editorType != null) {
                try {
                    return editorType.newInstance();
                } catch (InstantiationException | IllegalAccessException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }

            return super.getPropertyEditor();
        }

        /**
         * Set the property editor explicitly.
         *
         * @param editorType class type of the property editor
         */
        public void setPropertyEditorClass(Class<? extends PropertyEditor> editorType) {
            this.editorType = editorType;
        }

        @Override
        public String getHtmlDisplayName() {
            return htmlDisplayName != null ? htmlDisplayName : super.getHtmlDisplayName();
        }

        public void setHtmlDisplayName(String htmlDisplayName) {
            this.htmlDisplayName = htmlDisplayName;
        }
    }

    private static final class ConstProperty<T> extends PropertySupport.ReadOnly<T> {

        private final T value;

        public ConstProperty(T value, String name, Class<T> type, String displayName, String shortDescription) {
            super(name, type, displayName, shortDescription);
            this.value = value;
        }

        @Override
        public T getValue() throws IllegalAccessException, InvocationTargetException {
            return value;
        }
    }
    //</editor-fold>
}
