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
package demetra.desktop.properties;

import demetra.desktop.util.Collections2;
import ec.util.completion.AutoCompletionSource;
import ec.util.completion.AutoCompletionSources;
import nbbrd.design.BuilderPattern;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.beans.FeatureDescriptor;
import java.beans.PropertyEditor;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static demetra.desktop.util.JTextComponents.fixMaxDecimals;

/**
 * Builder that simplifies the creation of property sheets.
 *
 * @author Philippe Charles
 */
@BuilderPattern(Sheet.Set.class)
public final class NodePropertySetBuilder {

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
    @NonNull
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
    @NonNull
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
    @NonNull
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
    @NonNull
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
    @NonNull
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
    @NonNull
    public NodePropertySetBuilder add(Node.@NonNull Property<?> nodeProperty) {
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
    @NonNull
    public <T> SelectStep<T, DefaultStep> with(@NonNull Class<T> valueType) {
        return new SelectStep<>(valueType, DefaultStep::new);
    }

    /**
     * Creates a selection step for a file.
     *
     * @return a non-null selection step
     */
    @NonNull
    public SelectStep<File, FileStep> withFile() {
        return new SelectStep<>(File.class, FileStep::new);
    }

    /**
     * Creates a selection step for auto-completion.
     *
     * @return a non-null selection step
     */
    @NonNull
    public SelectStep<String, AutoCompletedStep> withAutoCompletion() {
        return new SelectStep<>(String.class, AutoCompletedStep::new);
    }

    /**
     * Creates a selection step for a boolean.
     *
     * @return a non-null selection step
     */
    @NonNull
    public SelectStep<Boolean, BooleanStep> withBoolean() {
        return new SelectStep<>(boolean.class, BooleanStep::new);
    }

    /**
     * Creates a selection step for an integer.
     *
     * @return a non-null selection step
     */
    @NonNull
    public SelectStep<Integer, IntStep> withInt() {
        return new SelectStep<>(int.class, IntStep::new);
    }

    /**
     * Creates a selection step for a double.
     *
     * @return a non-null selection step
     */
    @NonNull
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
    @NonNull
    public <T extends Enum<T>> SelectStep<T, EnumStep<T>> withEnum(@NonNull Class<T> valueType) {
        return new SelectStep<>(valueType, EnumStep::new);
    }

    public Sheet.Set build() {
        Sheet.Set result = name != null ? new Sheet.Set() : Sheet.createPropertiesSet();
        if (name != null && !name.isEmpty()) {
            result.setName(name);
        }
        if (tabName != null && !tabName.isEmpty()) {
            result.setValue("tabName", tabName);
        }
        if (displayName != null && !displayName.isEmpty()) {
            result.setDisplayName(displayName);
        }
        if (shortDescription != null && !shortDescription.isEmpty()) {
            result.setShortDescription(shortDescription);
        }
        result.put(Collections2.toArray(nodeProperties, Node.Property.class));
        return result;
    }

    public static final class SelectStep<T, NEXT_STEP extends PropertyStep> {

        private final Class<T> valueType;
        private final Function<Node.Property<T>, NEXT_STEP> next;

        private SelectStep(Class<T> valueType, Function<Node.Property<T>, NEXT_STEP> next) {
            this.valueType = valueType;
            this.next = next;
        }

        @NonNull
        public NEXT_STEP select(Node.@NonNull Property<T> property) {
            return next.apply(property);
        }

        @NonNull
        public NEXT_STEP select(@NonNull Object bean, @NonNull String property) {
            try {
                return next.apply(new PropertySupport.Reflection<>(bean, valueType, property));
            } catch (NoSuchMethodException ex) {
                throw new RuntimeException(ex);
            }
        }

        @NonNull
        public NEXT_STEP select(@NonNull Object bean, @NonNull String getter, @Nullable String setter) {
            try {
                return next.apply(new PropertySupport.Reflection<>(bean, valueType, getter, setter));
            } catch (NoSuchMethodException ex) {
                throw new RuntimeException(ex);
            }
        }

        @NonNull
        public <X> NEXT_STEP select(@NonNull Object bean, @NonNull String property, @NonNull Class<X> source, @NonNull Function<X, T> forward, @NonNull Function<T, X> backward) {
            try {
                return next.apply(new PropertyAdapter<>(new PropertySupport.Reflection<>(bean, source, property), valueType, forward, backward));
            } catch (NoSuchMethodException ex) {
                throw new RuntimeException(ex);
            }
        }

        /**
         *
         * @param name a non-null name
         * @param value a nullable value
         * @return
         * @since 2.2.0
         */
        @NonNull
        public NEXT_STEP selectConst(@NonNull String name, @Nullable T value) {
            return next.apply(new ConstProperty<>(value, name, valueType, null, null));
        }

        @NonNull
        public NEXT_STEP selectField(@NonNull Object bean, @NonNull String fieldName) {
            return next.apply(FieldNodeProperty.create(bean, valueType, fieldName));
        }

        @NonNull
        public <BEAN> NEXT_STEP select(@NonNull String name, @Nullable Supplier<T> getter, @Nullable Consumer<T> setter) {
            return next.apply(new FunctionalProperty<>(valueType, name, getter, setter));
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
        @NonNull
        public THIS name(@NonNull String name) {
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
        @NonNull
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
        @NonNull
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
        @NonNull
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
        @NonNull
        public THIS attribute(@NonNull String attributeName, @NonNull Object value) {
            nodeProperty.setValue(attributeName, value);
            return (THIS) this;
        }

        /**
         * Sets the property editor explicitly.
         *
         * @param editorType class type of the property editor
         * @return a reference to this object.
         */
        @NonNull
        protected THIS editor(@Nullable Class<? extends PropertyEditor> editorType) {
            nodeProperty.setPropertyEditorClass(editorType);
            return (THIS) this;
        }

        /**
         * Adds this {@link Property} to the builder.
         *
         * @return a reference to the builder
         */
        @NonNull
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
            editor(AutoCompletedPropertyEditor.class);
        }

        /**
         * Sets the service path of the auto-completion.
         *
         * @param path a non-null path
         * @return this step
         * @see AutoCompletedPropertyEditor3.SERVICE_PATH_ATTRIBUTE
         */
        @NonNull
        public AutoCompletedStep servicePath(@NonNull String path) {
            return attribute(AutoCompletedPropertyEditor.SERVICE_PATH_ATTRIBUTE, path);
        }

        /**
         * Sets the text prompt of the auto-completion.
         *
         * @param text a non-null text
         * @return this step
         * @see AutoCompletedPropertyEditor3.PROMPT_TEXT_ATTRIBUTE
         */
        @NonNull
        public AutoCompletedStep promptText(@NonNull String text) {
            return attribute(AutoCompletedPropertyEditor.PROMPT_TEXT_ATTRIBUTE, text);
        }

        /**
         * Enables/disables the auto focus of the auto-completion.
         *
         * @param autoFocus a non-null autoFocus
         * @return this step
         * @see AutoCompletedPropertyEditor3.AUTO_FOCUS_ATTRIBUTE
         */
        @NonNull
        public AutoCompletedStep autoFocus(boolean autoFocus) {
            return attribute(AutoCompletedPropertyEditor.AUTO_FOCUS_ATTRIBUTE, autoFocus);
        }

        /**
         * Sets the delay of the auto-completion.
         *
         * @param delay a non-null delay in milliseconds
         * @return this step
         * @see AutoCompletedPropertyEditor3.DELAY_ATTRIBUTE
         */
        @NonNull
        public AutoCompletedStep delay(int delay) {
            return attribute(AutoCompletedPropertyEditor.DELAY_ATTRIBUTE, delay);
        }

        /**
         * Sets the min length of the auto-completion.
         *
         * @param minLength a non-null min length
         * @return this step
         * @see AutoCompletedPropertyEditor3.MIN_LENGTH_ATTRIBUTE
         */
        @NonNull
        public AutoCompletedStep minLength(int minLength) {
            return attribute(AutoCompletedPropertyEditor.MIN_LENGTH_ATTRIBUTE, minLength);
        }

        /**
         * Sets the separator of the auto-completion.
         *
         * @param separator a non-null separator
         * @return this step
         * @see AutoCompletedPropertyEditor3.SEPARATOR_ATTRIBUTE
         */
        @NonNull
        public AutoCompletedStep separator(@NonNull String separator) {
            return attribute(AutoCompletedPropertyEditor.SEPARATOR_ATTRIBUTE, separator);
        }

        /**
         * Sets the data source of the auto-completion.
         *
         * @param source a non-null source
         * @return this step
         * @see AutoCompletedPropertyEditor3.SOURCE_ATTRIBUTE
         */
        @NonNull
        public AutoCompletedStep source(@NonNull AutoCompletionSource source) {
            return attribute(AutoCompletedPropertyEditor.SOURCE_ATTRIBUTE, source);
        }

        /**
         * Sets the data source of the auto-completion.
         *
         * @param list a non-null array of values
         * @return this step
         * @see AutoCompletedPropertyEditor3.SOURCE_ATTRIBUTE
         */
        @NonNull
        public AutoCompletedStep source(@NonNull Object... list) {
            return source(Arrays.asList(list));
        }

        /**
         * Sets the data source of the auto-completion.
         *
         * @param list a non-null list of values
         * @return this step
         * @see AutoCompletedPropertyEditor3.SOURCE_ATTRIBUTE
         */
        @NonNull
        public AutoCompletedStep source(@NonNull Iterable<?> list) {
            return source(AutoCompletionSources.of(false, list));
        }

        /**
         * Sets the cell renderer of the the auto-completion.
         *
         * @param cellRenderer a non-null cell renderer
         * @return this step
         * @see AutoCompletedPropertyEditor#CELL_RENDERER_ATTRIBUTE
         */
        @NonNull
        public AutoCompletedStep cellRenderer(@NonNull ListCellRenderer cellRenderer) {
            return attribute(AutoCompletedPropertyEditor.CELL_RENDERER_ATTRIBUTE, cellRenderer);
        }

        /**
         * Sets the default value supplier of the auto-completion.
         *
         * @param defaultValueSupplier a non-null default value supplier
         * @return this step
         * @since 2.2.0
         * @see AutoCompletedPropertyEditor3.DEFAULT_VALUE_SUPPLIER_ATTRIBUTE
         */
        @NonNull
        public AutoCompletedStep defaultValueSupplier(@NonNull Callable<String> defaultValueSupplier) {
            return attribute(AutoCompletedPropertyEditor.DEFAULT_VALUE_SUPPLIER_ATTRIBUTE, defaultValueSupplier);
        }

        /**
         * Sets the default value of the auto-completion.
         *
         * @param defaultValue a non-null default value
         * @return this step
         * @since 2.2.0
         * @see AutoCompletedPropertyEditor3.DEFAULT_VALUE_SUPPLIER_ATTRIBUTE
         */
        @NonNull
        public AutoCompletedStep defaultValueSupplier(@NonNull String defaultValue) {
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

        @NonNull
        public FileStep paths(@NonNull File[] paths) {
            return attribute(DesktopFilePropertyEditor.PATHS_ATTRIBUTE, paths);
        }

        /**
         * the value represents filter for the file dialog
         *
         * @param fileFilter
         * @return
         */
        @NonNull
        public FileStep filterForSwing(javax.swing.filechooser.@NonNull FileFilter fileFilter) {
            return attribute("filter", fileFilter);
        }

        /**
         * the value represents filter for the file dialog
         *
         * @param fileFilter
         * @return
         */
        @NonNull
        public FileStep filter(java.io.@NonNull FileFilter fileFilter) {
            return attribute("filter", fileFilter);
        }

        /**
         * the value represents filter for the file dialog
         *
         * @param fileNameFilter
         * @return
         */
        @NonNull
        public FileStep filterByName(java.io.@NonNull FilenameFilter fileNameFilter) {
            return attribute("filter", fileNameFilter);
        }

        /**
         * should directories be selectable as values for the property
         *
         * @param selectables
         * @return
         */
        @NonNull
        public FileStep directories(boolean selectables) {
            return attribute("directories", selectables);
        }

        /**
         * should files be selectable as values for the property
         *
         * @param selectables
         * @return
         */
        @NonNull
        public FileStep files(boolean selectables) {
            return attribute("files", selectables);
        }

        /**
         * the dir that should be preselected when displaying the dialog
         *
         * @param file
         * @return
         */
        @NonNull
        public FileStep currentDir(@NonNull File file) {
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
        @NonNull
        public FileStep baseDir(@NonNull File file) {
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
        @NonNull
        public IntStep max(int max) {
            return attribute(JSpinFieldPropertyEditor.MAX_ATTRIBUTE, max);
        }

        /**
         * Sets the minimum permissible value.
         *
         * @param min minimum legal value that can be input
         * @return this step
         */
        @NonNull
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
            DecimalFormat format = new DecimalFormat();
            fixMaxDecimals(format);
            formatter = new NumberFormatter(format);
            editor(FormattedPropertyEditor.class);
            attribute(FormattedPropertyEditor.FORMATTER_ATTRIBUTE, formatter);
        }

        /**
         * Sets the maximum permissible value.
         *
         * @param max maximum legal value that can be input
         * @return this step
         */
        @NonNull
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
        @NonNull
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

        private EnumStep<T> withValues(T... values) {
            return attribute(ComboBoxPropertyEditor.VALUES_ATTRIBUTE, values);
        }

        @NonNull
        public EnumStep<T> of(@NonNull T... values) {
            return withValues(values);
        }

        @NonNull
        public EnumStep<T> of(@NonNull Iterable<T> values) {
            return withValues(Collections2.toArray(values, nodeProperty.getValueType()));
        }

        @NonNull
        public EnumStep<T> noneOf(@NonNull T... values) {
            EnumSet<T> tmp = EnumSet.allOf(nodeProperty.getValueType());
            for (T o : values) {
                tmp.remove(o);
            }
            return withValues(Collections2.toArray(tmp, nodeProperty.getValueType()));
        }

        @NonNull
        public EnumStep<T> noneOf(@NonNull Iterable<T> values) {
            EnumSet<T> tmp = EnumSet.allOf(nodeProperty.getValueType());
            for (T o : values) {
                tmp.remove(o);
            }
            return withValues(Collections2.toArray(tmp, nodeProperty.getValueType()));
        }
    }

    public static <X, Y> Node.@NonNull Property<Y> adapt(
            @NonNull Object bean, @NonNull String property,
            @NonNull Class<X> source, @NonNull Class<Y> target,
            @NonNull Function<X, Y> forward, @NonNull Function<Y, X> backward) {
        try {
            return new PropertyAdapter<>(new PropertySupport.Reflection<>(bean, source, property), target, forward, backward);
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException(ex);
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

    private static final class PropertyAdapter<X, Y> extends Node.Property<Y> {

        private final Node.Property<X> source;
        private final Function<X, Y> forward;
        private final Function<Y, X> backward;

        private PropertyAdapter(Node.Property<X> source, Class<Y> target, Function<X, Y> forward, Function<Y, X> backward) {
            super(target);
            this.source = source;
            this.forward = forward;
            this.backward = backward;
        }

        @Override
        public boolean canRead() {
            return source.canRead();
        }

        @Override
        public Y getValue() throws IllegalAccessException, InvocationTargetException {
            return forward.apply(source.getValue());
        }

        @Override
        public boolean canWrite() {
            return source.canWrite();
        }

        @Override
        public void setValue(Y t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            source.setValue(backward.apply(t));
        }
    }

    private static final class FunctionalProperty<T> extends Node.Property<T> {

        @Nullable
        private final Supplier<T> getter;

        @Nullable
        private final Consumer<T> setter;

        private FunctionalProperty(
                @NonNull Class<T> valueType,
                @NonNull String name,
                @Nullable Supplier<T> getter,
                @Nullable Consumer<T> setter
        ) {
            super(Objects.requireNonNull(valueType));
            setName(Objects.requireNonNull(name));
            this.getter = getter;
            this.setter = setter;
        }

        @Override
        public boolean canRead() {
            return getter != null;
        }

        @Override
        public T getValue() throws IllegalAccessException, InvocationTargetException {
            return getter.get();
        }

        @Override
        public boolean canWrite() {
            return setter != null;
        }

        @Override
        public void setValue(T t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            setter.accept(t);
        }
    }
    //</editor-fold>
}
