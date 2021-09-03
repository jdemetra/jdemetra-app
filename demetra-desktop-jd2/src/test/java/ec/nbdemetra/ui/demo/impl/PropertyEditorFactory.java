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
package ec.nbdemetra.ui.demo.impl;

import com.google.common.collect.ImmutableMap;
import ec.nbdemetra.ui.demo.DemoComponentFactory;
import demetra.desktop.core.properties.CharsetPropertyEditor;
import demetra.desktop.core.properties.ObsFormatPropertyEditor;
import ec.nbdemetra.ui.properties.DayPropertyEditor;
import demetra.desktop.properties.DhmsPropertyEditor;
import demetra.desktop.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.properties.TsPeriodSelectorPropertyEditor;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tstoolkit.timeseries.Day;
import ec.tstoolkit.timeseries.TsPeriodSelector;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.utilities.Id;
import ec.util.completion.FileAutoCompletionSource;
import ec.util.completion.swing.FileListCellRenderer;
import ec.util.various.swing.BasicSwingLauncher;
import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import javax.swing.JPanel;
import javax.swing.SwingWorker.StateValue;
import nbbrd.design.DirectImpl;
import nbbrd.service.ServiceProvider;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Philippe Charles
 */
@DirectImpl
@ServiceProvider
public final class PropertyEditorFactory implements DemoComponentFactory {

    @Override
    public ImmutableMap<Id, Callable<Component>> getComponents() {
        return DemoComponentFactory
                .builder()
                .put(OtherFactory.ID.extend("PropertyEditor"), PropertyEditorFactory::propertySheet)
                .build();
    }

    private static Component propertySheet() {
        JPanel result = new JPanel();
        PropertySheet view = new PropertySheet();

        view.setNodes(new Node[]{new DemoNode()});

        result.setLayout(new BorderLayout());
        result.add(view, BorderLayout.CENTER);
        return result;
    }

    public static final class DemoNode extends AbstractNode {

        public DemoNode() {
            super(Children.LEAF, Lookups.singleton(new DemoBean()));
        }

        @Override
        protected Sheet createSheet() {
            DemoBean bean = getLookup().lookup(DemoBean.class);
            Sheet result = super.createSheet();
            NodePropertySetBuilder b = new NodePropertySetBuilder();
            result.put(withPrimitives(b, bean).build());
            result.put(withCustomEditors(b, bean).build());
            result.put(withEnums(b, bean).build());
            result.put(withAutoCompletion(b, bean).build());
            result.put(withOther(b, bean).build());
            return result;
        }
    }

    private static NodePropertySetBuilder withPrimitives(NodePropertySetBuilder b, Object bean) {
        b.reset("Primitives");
        b.withDouble()
                .selectField(bean, "doubleValue")
                .add();
        b.withDouble()
                .selectField(bean, "smallDouble")
                .add();
        return b;
    }

    private static NodePropertySetBuilder withCustomEditors(NodePropertySetBuilder b, Object bean) {
        b.reset("Specific");
        b.with(Charset.class)
                .selectField(bean, "charset")
                .editor(CharsetPropertyEditor.class)
                .display("Charset")
                .add();
        b.with(DataFormat.class)
                .selectField(bean, "dataFormat")
                .editor(ObsFormatPropertyEditor.class)
                .display("DataFormat")
                .add();
        b.with(Day.class)
                .selectField(bean, "day")
                .editor(DayPropertyEditor.class)
                .display("Day")
                .add();
        b.with(TsPeriodSelector.class)
                .selectField(bean, "periodSelector")
                .editor(TsPeriodSelectorPropertyEditor.class)
                .display("TsPeriodSelector")
                .add();
        b.with(long.class)
                .selectField(bean, "duration")
                .editor(DhmsPropertyEditor.class)
                .display("Duration")
                .add();
        return b;
    }

    private static NodePropertySetBuilder withEnums(NodePropertySetBuilder b, Object bean) {
        b.reset("Enums");
        b.withEnum(TsFrequency.class)
                .selectField(bean, "frequency")
                .display("Enum all")
                .add();
        b.withEnum(TsFrequency.class)
                .selectField(bean, "limitedFrequency")
                .of(TsFrequency.BiMonthly, TsFrequency.Monthly)
                .display("Enum subset")
                .add();
        return b;
    }

    private static NodePropertySetBuilder withAutoCompletion(NodePropertySetBuilder b, Object bean) {
        b.reset("AutoCompletion");
        b.withAutoCompletion()
                .selectField(bean, "database")
                .source("Access", "Sql Server", "Oracle")
                .display("Single")
                .add();
        b.withAutoCompletion()
                .selectField(bean, "tables")
                .source("User", "Address", "Order")
                .separator(",")
                .display("Multiple")
                .description("Names separated by a comma.")
                .add();
        b.withAutoCompletion()
                .selectField(bean, "columns")
                .source("FirstName", "LastName", "Birthdate")
                .separator(",")
                .defaultValueSupplier("FirstName,LastName")
                .display("Multiple with magic")
                .description("Names separated by a comma.")
                .add();
        b.withAutoCompletion()
                .selectField(bean, "fileAsString")
                .source(new FileAutoCompletionSource())
                .cellRenderer(new FileListCellRenderer(Executors.newSingleThreadExecutor()))
                .display("File")
                .add();
        return b;
    }

    private static NodePropertySetBuilder withOther(NodePropertySetBuilder b, Object bean) {
        b.reset("other").group("Hello").display("Other").description("...");
        b.withInt()
                .selectField(bean, "offset")
                .min(-7).max(+7)
                .display("Bounded int [-7, 7]")
                .add();
        b.withFile()
                .selectField(bean, "file")
                .directories(true).files(false)
                .display("Directory")
                .description("Hello !")
                .add();
        b.withInt()
                .selectField(bean, "readOnly")
                .add();
        b.with(String.class)
                .selectConst("html", "hello world")
                .htmlDisplay("<b><font color=\"#FF0000\">Html</font></b>")
                .add();
        b.withDouble()
                .selectField(bean, "weight")
                .min(0).max(1)
                .display("Weight")
                .add();
        return b;
    }

    public static class DemoBean {

        public double doubleValue = 3.14;
        public double smallDouble = 0.0000001;
        public Charset charset = Charset.defaultCharset();
        public DataFormat dataFormat = DataFormat.DEFAULT;
        public TsFrequency frequency = TsFrequency.Monthly;
        public TsFrequency limitedFrequency = TsFrequency.Monthly;
        public String database = "";
        public String tables = "";
        public String columns = "";
        public String fileAsString = "";
        public Day day = new Day(new Date());
        public int offset = 0;
        public File file = new File("");
        public final int readOnly = 123;
        public StateValue stateValue = StateValue.DONE;
        public double weight = 0.3;
        public long duration = 60 * 1000;
        public TsPeriodSelector periodSelector = new TsPeriodSelector();
    }

    public static void main(String[] args) {
        new BasicSwingLauncher().content(PropertyEditorFactory::propertySheet).size(350, 500).launch();
    }
}
