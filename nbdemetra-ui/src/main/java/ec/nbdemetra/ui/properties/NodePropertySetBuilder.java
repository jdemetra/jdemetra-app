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
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.ListCellRenderer;
import javax.swing.text.NumberFormatter;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;

/**
 * Builder that simplifies the creation of property sheets.
 *
 * @author Philippe Charles
 */
public final class NodePropertySetBuilder implements IBuilder<Node.PropertySet> {

    private final List<Node.Property<?>> nodeProperties;
    private String name;
    private String tabName;
    private String displayName;
    private String shortDescription;

    public NodePropertySetBuilder() {
        this.nodeProperties = new ArrayList<>();
        this.name = null;
        this.tabName = null;
        this.displayName = null;
        this.shortDescription = null;
    }

    /**
     * Resets this builder.
     *
     * @param name a programmatic name
     * @return this builder
     */
    @Nonnull
    public NodePropertySetBuilder reset(@Nullable String name) {
        this.nodeProperties.clear();
        this.name = name;
        this.tabName = null;
        this.displayName = null;
        this.shortDescription = null;
        return this;
    }

    /**
     * Sets the programmatic name of the resulting set.
     *
     * @param name a programmatic name
     * @return this builder
     */
    @Nonnull
    public NodePropertySetBuilder name(@Nullable String name) {
        this.name = name;
        return this;
    }

    /**
     * Specify a group name for one or more PropertySets.
     *
     * @param tabName
     * @return this builder
     * @see
     * http://platform.netbeans.org/tutorials/nbm-nodesapi2.html#separate-property-groups
     */
    @Nonnull
    public NodePropertySetBuilder group(@Nullable String tabName) {
        this.tabName = tabName;
        return this;
    }

    /**
     * Sets the localized display name of the resulting set.
     *
     * @param displayName a display name
     * @return this builder
     */
    @Nonnull
    public NodePropertySetBuilder display(@Nullable String displayName) {
        this.displayName = displayName;
        return this;
    }

    /**
     * Sets the short description of this resulting set.
     *
     * @param shortDescription a short description
     * @return this builder
     */
    @Nonnull
    public NodePropertySetBuilder description(@Nullable String shortDescription) {
        this.shortDescription = shortDescription;
        return this;
    }

    /**
     * Adds a new property to the resulting set.
     *
     * @param nodeProperty a non-null property
     * @return this builder
     */
    @Nonnull
    public NodePropertySetBuilder add(@Nonnull Node.Property<?> nodeProperty) {
        nodeProperties.add(nodeProperty);
        return this;
    }

    /**
     * Creates a selection step for a specific type.
     *
     * @param <T>
     * @param valueType
     * @return a non-null selection step
     */
    @Nonnull
    public <T> SelectStep<T, DefaultStep> with(@Nonnull Class<T> valueType) {
        return new SelectStep<>(valueType, DefaultStep::new);
    }

    /**
     * Creates a selection step for a file.
     *
     * @return a non-null selection step
     */
    @Nonnull
    public SelectStep<File, FileStep> withFile() {
        return new SelectStep<>(File.class, FileStep::new);
    }

    /**
     * Creates a selection step for auto-completion.
     *
     * @return a non-null selection step
     */
    @Nonnull
    public SelectStep<String, AutoCompletedStep> withAutoCompletion() {
        return new SelectStep<>(String.class, AutoCompletedStep::new);
    }

    /**
     * Creates a selection step for a boolean.
     *
     * @return a non-null selection step
     */
    @Nonnull
    public SelectStep<Boolean, BooleanStep> withBoolean() {
        return new SelectStep<>(boolean.class, BooleanStep::new);
    }

    /**
     * Creates a selection step for an integer.
     *
     * @return a non-null selection step
     */
    @Nonnull
    public SelectStep<Integer, IntStep> withInt() {
        return new SelectStep<>(int.class, IntStep::new);
    }

    /**
     * Creates a selection step for a double.
     *
     * @return a non-null selection step
     */
    @Nonnull
    public SelectStep<Double, DoubleStep> withDouble() {
        return new SelectStep<>(double.class, DoubleStep::new);
    }

    /**
     * Creates a selection step for a specific enum.
     *
     * @param <T>
     * @param valueType
     * @return a non-null selection step
     */
    @Nonnull
    public <T extends Enum<T>> SelectStep<T, EnumStep<T>> withEnum(@Nonnull Class<T> valueType) {
        return new SelectStep<>(valueType, EnumStep::new);
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

    public final class SelectStep<T, NEXT_STEP> {

        private final Class<T> valueType;
        private final Function<Node.Property<T>, NEXT_STEP> next;

        private SelectStep(Class<T> valueType, Function<Node.Property<T>, NEXT_STEP> next) {
            this.valueType = valueType;
            this.next = next;
        }

        @Nonnull
        public NEXT_STEP select(@Nonnull Node.Property<T> property) {
            return next.apply(property);
        }

        @Nonnull
        public NEXT_STEP select(@Nonnull Object bean, @Nonnull String property) {
            try {
                return next.apply(new PropertySupport.Reflection<>(bean, valueType, property));
            } catch (NoSuchMethodException ex) {
                throw Throwables.propagate(ex);
            }
        }

        @Nonnull
        public NEXT_STEP select(@Nonnull Object bean, @Nonnull String getter, @Nullable String setter) {
            try {
                return next.apply(new PropertySupport.Reflection<>(bean, valueType, getter, setter));
            } catch (NoSuchMethodException ex) {
                throw Throwables.propagate(ex);
            }
        }

        /**
         *
         * @param name
         * @param value
         * @return
         * @deprecated use
         * {@link #selectConst(java.lang.String, java.lang.Object)} instead
         */
        @Deprecated
        @Nonnull
        public NEXT_STEP select(@Nonnull String name, @Nonnull T value) {
            return selectConst(name, value);
        }

        /**
         *
         * @param name a non-null name
         * @param value a non-null value
         * @return
         * @since 2.2.0
         */
        @Nonnull
        public NEXT_STEP selectConst(@Nonnull String name, @Nonnull T value) {
            return next.apply(new ConstProperty<>(value, name, valueType, null, null));
        }

        @Nonnull
        public NEXT_STEP selectField(@Nonnull Object bean, @Nonnull String fieldName) {
            return next.apply(FieldNodeProperty.create(bean, valueType, fieldName));
        }
    }

    /**
     * Abstract property step.
     *
     * @param <T> the property type
     * @param <THIS> this builder type
     */
    public abstract class PropertyStep<T, THIS extends PropertyStep> {

        final InternalProperty<T> nodeProperty;

        private PropertyStep(Node.Property<T> nodeProperty) {
            this.nodeProperty = new InternalProperty(nodeProperty);
        }

        /**
         * Sets the programmatic name of this property. This name must be
         * non-null and unique.
         *
         * @see FeatureDescriptor#setName(java.lang.String)
         * @param name The programmatic name of the property/method/event
         * @return a reference to this object.
         */
        @Nonnull
        public THIS name(@Nonnull String name) {
            nodeProperty.setName(Objects.requireNonNull(name));
            return (THIS) this;
        }

        /**
         * Sets the localized display name of this property.
         *
         * @see FeatureDescriptor#setDisplayName(java.lang.String)
         * @param displayName The localized display name for the
         * property/method/event.
         * @return a reference to this object.
         */
        @Nonnull
        public THIS display(@Nullable String displayName) {
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
        @Nonnull
        public THIS htmlDisplay(@Nullable String htmlDisplayName) {
            nodeProperty.setHtmlDisplayName(htmlDisplayName);
            return (THIS) this;
        }

        /**
         * Sets the short description of this property.
         *
         * @see FeatureDescriptor#setShortDescription(java.lang.String)
         * @param description A localized short description associated with this
         * property/method/event. This defaults to be the display name.
         * @return a reference to this object.
         */
        @Nonnull
        public THIS description(@Nullable String description) {
            nodeProperty.setShortDescription(description);
            return (THIS) this;
        }

        /**
         * Associates a named attribute with this property.
         *
         * @see FeatureDescriptor#setValue(java.lang.String, java.lang.Object)
         * @param attributeName The locale-independent name of the attribute
         * @param value The value.
         * @return a reference to this object.
         */
        @Nonnull
        public THIS attribute(@Nonnull String attributeName, @Nonnull Object value) {
            nodeProperty.setValue(attributeName, value);
            return (THIS) this;
        }

        /**
         * Sets the property editor explicitly.
         *
         * @param editorType class type of the property editor
         * @return a reference to this object.
         */
        @Nonnull
        protected THIS editor(@Nullable Class<? extends PropertyEditor> editorType) {
            nodeProperty.setPropertyEditorClass(editorType);
            return (THIS) this;
        }

        /**
         * Adds this {@link Property} to the builder.
         *
         * @return a reference to the builder
         */
        @Nonnull
        public NodePropertySetBuilder add() {
            return NodePropertySetBuilder.this.add(nodeProperty);
        }
    }

    /**
     * Generic property step.
     *
     * @param <T> the property type
     */
    public final class DefaultStep<T> extends PropertyStep<T, DefaultStep> {

        private DefaultStep(Node.Property<T> nodeProperty) {
            super(nodeProperty);
        }

        @Override
        public DefaultStep editor(@Nullable Class<? extends PropertyEditor> editor) {
            return super.editor(editor);
        }
    }

    /**
     * Specific property step that deals with auto-completion.
     */
    public final class AutoCompletedStep extends PropertyStep<String, AutoCompletedStep> {

        private AutoCompletedStep(Node.Property<String> nodeProperty) {
            super(nodeProperty);
            editor(AutoCompletedPropertyEditor3.class);
        }

        /**
         * Sets the service path of the auto-completion.
         *
         * @param path a non-null path
         * @return this step
         * @see AutoCompletedPropertyEditor3.SERVICE_PATH_ATTRIBUTE
         */
        @Nonnull
        public AutoCompletedStep servicePath(@Nonnull String path) {
            return attribute(AutoCompletedPropertyEditor3.SERVICE_PATH_ATTRIBUTE, path);
        }

        /**
         * Sets the text prompt of the auto-completion.
         *
         * @param text a non-null text
         * @return this step
         * @see AutoCompletedPropertyEditor3.PROMPT_TEXT_ATTRIBUTE
         */
        @Nonnull
        public AutoCompletedStep promptText(@Nonnull String text) {
            return attribute(AutoCompletedPropertyEditor3.PROMPT_TEXT_ATTRIBUTE, text);
        }

        /**
         * Enables/disables the auto focus of the auto-completion.
         *
         * @param autoFocus a non-null autoFocus
         * @return this step
         * @see AutoCompletedPropertyEditor3.AUTO_FOCUS_ATTRIBUTE
         */
        @Nonnull
        public AutoCompletedStep autoFocus(boolean autoFocus) {
            return attribute(AutoCompletedPropertyEditor3.AUTO_FOCUS_ATTRIBUTE, autoFocus);
        }

        /**
         * Sets the delay of the auto-completion.
         *
         * @param delay a non-null delay in milliseconds
         * @return this step
         * @see AutoCompletedPropertyEditor3.DELAY_ATTRIBUTE
         */
        @Nonnull
        public AutoCompletedStep delay(int delay) {
            return attribute(AutoCompletedPropertyEditor3.DELAY_ATTRIBUTE, delay);
        }

        /**
         * Sets the min length of the auto-completion.
         *
         * @param minLength a non-null min length
         * @return this step
         * @see AutoCompletedPropertyEditor3.MIN_LENGTH_ATTRIBUTE
         */
        @Nonnull
        public AutoCompletedStep minLength(int minLength) {
            return attribute(AutoCompletedPropertyEditor3.MIN_LENGTH_ATTRIBUTE, minLength);
        }

        /**
         * Sets the separator of the auto-completion.
         *
         * @param separator a non-null separator
         * @return this step
         * @see AutoCompletedPropertyEditor3.SEPARATOR_ATTRIBUTE
         */
        @Nonnull
        public AutoCompletedStep separator(@Nonnull String separator) {
            return attribute(AutoCompletedPropertyEditor3.SEPARATOR_ATTRIBUTE, separator);
        }

        /**
         * Sets the data source of the auto-completion.
         *
         * @param source a non-null source
         * @return this step
         * @see AutoCompletedPropertyEditor3.SOURCE_ATTRIBUTE
         */
        @Nonnull
        public AutoCompletedStep source(@Nonnull AutoCompletionSource source) {
            return attribute(AutoCompletedPropertyEditor3.SOURCE_ATTRIBUTE, source);
        }

        /**
         * Sets the data source of the auto-completion.
         *
         * @param list a non-null array of values
         * @return this step
         * @see AutoCompletedPropertyEditor3.SOURCE_ATTRIBUTE
         */
        @Nonnull
        public AutoCompletedStep source(@Nonnull Object... list) {
            return source(Arrays.asList(list));
        }

        /**
         * Sets the data source of the auto-completion.
         *
         * @param list a non-null list of values
         * @return this step
         * @see AutoCompletedPropertyEditor3.SOURCE_ATTRIBUTE
         */
        @Nonnull
        public AutoCompletedStep source(@Nonnull Iterable<?> list) {
            return source(AutoCompletionSources.of(false, list));
        }

        /**
         * Sets the cell renderer of the the auto-completion.
         *
         * @param cellRenderer a non-null cell renderer
         * @return this step
         * @see AutoCompletedPropertyEditor3#CELL_RENDERER_ATTRIBUTE
         */
        @Nonnull
        public AutoCompletedStep cellRenderer(@Nonnull ListCellRenderer cellRenderer) {
            return attribute(AutoCompletedPropertyEditor3.CELL_RENDERER_ATTRIBUTE, cellRenderer);
        }

        /**
         * Sets the default value supplier of the auto-completion.
         *
         * @param defaultValueSupplier a non-null default value supplier
         * @return this step
         * @since 2.2.0
         * @see AutoCompletedPropertyEditor3.DEFAULT_VALUE_SUPPLIER_ATTRIBUTE
         */
        @Nonnull
        public AutoCompletedStep defaultValueSupplier(@Nonnull Callable<String> defaultValueSupplier) {
            return attribute(AutoCompletedPropertyEditor3.DEFAULT_VALUE_SUPPLIER_ATTRIBUTE, defaultValueSupplier);
        }

        /**
         * Sets the default value of the auto-completion.
         *
         * @param defaultValue a non-null default value
         * @return this step
         * @since 2.2.0
         * @see AutoCompletedPropertyEditor3.DEFAULT_VALUE_SUPPLIER_ATTRIBUTE
         */
        @Nonnull
        public AutoCompletedStep defaultValueSupplier(@Nonnull String defaultValue) {
            return AutoCompletedStep.this.defaultValueSupplier(() -> defaultValue);
        }
    }

    /**
     * Specific property step that deals with files.
     *
     * http://bits.netbeans.org/dev/javadoc/org-openide-explorer/org/openide/explorer/doc-files/propertyViewCustomization.html
     */
    public final class FileStep extends PropertyStep<File, FileStep> {

        private FileStep(Node.Property<File> nodeProperty) {
            super(nodeProperty);
            editor(DesktopFilePropertyEditor.class);
            //editor(FileEditor.class);
        }

        @Nonnull
        public FileStep paths(@Nonnull File[] paths) {
            return attribute(DesktopFilePropertyEditor.PATHS_ATTRIBUTE, paths);
        }

        /**
         * the value represents filter for the file dialog
         *
         * @param fileFilter
         * @return
         */
        @Nonnull
        public FileStep filterForSwing(@Nonnull javax.swing.filechooser.FileFilter fileFilter) {
            return attribute("filter", fileFilter);
        }

        /**
         * the value represents filter for the file dialog
         *
         * @param fileFilter
         * @return
         */
        @Nonnull
        public FileStep filter(@Nonnull java.io.FileFilter fileFilter) {
            return attribute("filter", fileFilter);
        }

        /**
         * the value represents filter for the file dialog
         *
         * @param fileNameFilter
         * @return
         */
        @Nonnull
        public FileStep filterByName(@Nonnull java.io.FilenameFilter fileNameFilter) {
            return attribute("filter", fileNameFilter);
        }

        /**
         * should directories be selectable as values for the property
         *
         * @param selectables
         * @return
         */
        @Nonnull
        public FileStep directories(boolean selectables) {
            return attribute("directories", selectables);
        }

        /**
         * should files be selectable as values for the property
         *
         * @param selectables
         * @return
         */
        @Nonnull
        public FileStep files(boolean selectables) {
            return attribute("files", selectables);
        }

        /**
         * the dir that should be preselected when displaying the dialog
         *
         * @param file
         * @return
         */
        @Nonnull
        public FileStep currentDir(@Nonnull File file) {
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
        @Nonnull
        public FileStep baseDir(@Nonnull File file) {
            return attribute("baseDir", file);
        }
    }

    /**
     * Specific property step that deals with booleans.
     */
    public final class BooleanStep extends PropertyStep<Boolean, BooleanStep> {

        private BooleanStep(Node.Property<Boolean> nodeProperty) {
            super(nodeProperty);
        }
    }

    /**
     * Specific property step that deals with integers.
     */
    public final class IntStep extends PropertyStep<Integer, IntStep> {

        private IntStep(Node.Property<Integer> nodeProperty) {
            super(nodeProperty);
            editor(JSpinFieldPropertyEditor.class);
        }

        /**
         * Sets the maximum permissible value.
         *
         * @param max maximum legal value that can be input
         * @return this step
         */
        @Nonnull
        public IntStep max(int max) {
            return attribute(JSpinFieldPropertyEditor.MAX_ATTRIBUTE, max);
        }

        /**
         * Sets the minimum permissible value.
         *
         * @param min minimum legal value that can be input
         * @return this step
         */
        @Nonnull
        public IntStep min(int min) {
            return attribute(JSpinFieldPropertyEditor.MIN_ATTRIBUTE, min);
        }
    }

    /**
     * Specific property step that deals with doubles.
     */
    public final class DoubleStep extends PropertyStep<Double, DoubleStep> {

        private final NumberFormatter formatter;

        private DoubleStep(Node.Property<Double> nodeProperty) {
            super(nodeProperty);
            formatter = new NumberFormatter();
            editor(FormattedPropertyEditor.class);
            attribute(FormattedPropertyEditor.FORMATTER_ATTRIBUTE, formatter);
        }

        /**
         * Sets the maximum permissible value.
         *
         * @param max maximum legal value that can be input
         * @return this step
         */
        @Nonnull
        public DoubleStep max(double max) {
            formatter.setMaximum(max);
            return this;
        }

        /**
         * Sets the minimum permissible value.
         *
         * @param min minimum legal value that can be input
         * @return this step
         */
        @Nonnull
        public DoubleStep min(double min) {
            formatter.setMinimum(min);
            return this;
        }
    }

    /**
     * Specific property step that deals with enums.
     *
     * @param <T>
     */
    public final class EnumStep<T extends Enum<T>> extends PropertyStep<T, EnumStep<T>> {

        private EnumStep(Property<T> nodeProperty) {
            super(nodeProperty);
            editor(ComboBoxPropertyEditor.class);
            of(EnumSet.allOf(nodeProperty.getValueType()));
        }

        @Nonnull
        public EnumStep<T> of(@Nonnull T... values) {
            return attribute(ComboBoxPropertyEditor.VALUES_ATTRIBUTE, values);
        }

        @Nonnull
        public EnumStep<T> of(@Nonnull Iterable<T> values) {
            return of(Iterables.toArray(values, nodeProperty.getValueType()));
        }

        @Nonnull
        public EnumStep<T> noneOf(@Nonnull T... values) {
            EnumSet<T> tmp = EnumSet.allOf(nodeProperty.getValueType());
            tmp.removeAll(Arrays.asList(values));
            return of(tmp);
        }

        @Nonnull
        public EnumStep<T> noneOf(@Nonnull Iterable<T> values) {
            EnumSet<T> tmp = EnumSet.allOf(nodeProperty.getValueType());
            tmp.removeAll(Lists.newArrayList(values));
            return of(tmp);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Custom node properties">
    private final static class InternalProperty<T> extends ForwardingNodeProperty<T> {

        private Class<? extends PropertyEditor> editorType;
        private String htmlDisplayName;

        private InternalProperty(Node.Property<T> p) {
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
        public void setPropertyEditorClass(@Nullable Class<? extends PropertyEditor> editorType) {
            this.editorType = editorType;
        }

        @Override
        public String getHtmlDisplayName() {
            return htmlDisplayName != null ? htmlDisplayName : super.getHtmlDisplayName();
        }

        public void setHtmlDisplayName(@Nullable String htmlDisplayName) {
            this.htmlDisplayName = htmlDisplayName;
        }
    }

    private static final class ConstProperty<T> extends PropertySupport.ReadOnly<T> {

        private final T value;

        private ConstProperty(T value, String name, Class<T> type, String displayName, String shortDescription) {
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
