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
import ec.nbdemetra.ui.properties.AutoCompletedPropertyEditor3;
import ec.nbdemetra.ui.properties.CharsetPropertyEditor;
import ec.nbdemetra.ui.properties.DataFormatPropertyEditor;
import ec.nbdemetra.ui.properties.DayPropertyEditor;
import ec.nbdemetra.ui.properties.DhmsPropertyEditor;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.properties.TsPeriodSelectorPropertyEditor;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tstoolkit.timeseries.Day;
import ec.tstoolkit.timeseries.TsPeriodSelector;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.LinearId;
import ec.util.completion.AutoCompletionSources;
import ec.util.completion.FileAutoCompletionSource;
import ec.util.completion.swing.FileListCellRenderer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import javax.swing.JPanel;
import javax.swing.SwingWorker.StateValue;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = DemoComponentFactory.class)
public final class PropertyEditorFactory extends DemoComponentFactory {

    @Override
    public ImmutableMap<Id, Callable<Component>> getComponents() {
        return builder().put(new LinearId("(2) Other", "PropertyEditor"), propertySheet()).build();
    }

    private static Callable<Component> propertySheet() {
        return new Callable<Component>() {
            @Override
            public Component call() throws Exception {
                JPanel result = new JPanel();
                PropertySheet view = new PropertySheet();

                view.setNodes(new Node[]{new DemoNode()});

                result.setLayout(new BorderLayout());
                result.add(view, BorderLayout.CENTER);
                return result;
            }
        };
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

            b.with(Charset.class).select(bean, "charset").editor(CharsetPropertyEditor.class).display(Charset.class.getName()).add();
            b.with(DataFormat.class).select(bean, "dataFormat").editor(DataFormatPropertyEditor.class).display(DataFormat.class.getName()).add();
            b.withEnum(TsFrequency.class).select(bean, "frequency").display(TsFrequency.class.getName()).add();
            b.with(Day.class).select(bean, "day").editor(DayPropertyEditor.class).display(Day.class.getName()).add();
            b.withEnum(StateValue.class).select(bean, "stateValue").of(StateValue.DONE, StateValue.PENDING).display(StateValue.class.getName()).add();
            b.with(TsPeriodSelector.class).select(bean, "periodSelector").editor(TsPeriodSelectorPropertyEditor.class).display(TsPeriodSelector.class.getName()).add();
            result.put(b.build());

            b.reset("alpha");
            b.with(String.class)
                    .select(bean, "database")
                    .editor(AutoCompletedPropertyEditor3.class)
                    .attribute(AutoCompletedPropertyEditor3.SOURCE_ATTRIBUTE, AutoCompletionSources.of(false, "Access", "Sql Server", "Oracle"))
                    .attribute(AutoCompletedPropertyEditor3.SEPARATOR_ATTRIBUTE, ",")
                    .name("AutoCompletedPropertyEditor3")
                    .add();
            result.put(b.build());

            b.reset("other").group("Hello").display("Other").description("...");
            b.withAutoCompletion()
                    .select(bean, "database")
                    .source("Access", "Sql Server", "Oracle")
                    .separator(",")
                    .display("Databases")
                    .description("Names separated by a comma.")
                    .add();
            b.with(String.class)
                    .select(bean, "fileAsString")
                    .editor(AutoCompletedPropertyEditor3.class)
                    .attribute(AutoCompletedPropertyEditor3.SOURCE_ATTRIBUTE, new FileAutoCompletionSource())
                    .attribute(AutoCompletedPropertyEditor3.CELL_RENDERER_ATTRIBUTE, new FileListCellRenderer(Executors.newSingleThreadExecutor()))
                    .add();
            b.withInt()
                    .select(bean, "offset")
                    .min(-7).max(+7)
                    .display("Bounded int [-7, 7]")
                    .add();
            b.withFile()
                    .select(bean, "file")
                    .directories(true).files(false)
                    .display("Directory")
                    .description("Hello !")
                    .add();
            b.withInt()
                    .select(bean, "getReadOnly", null)
                    .add();
            b.with(String.class)
                    .select("html", "hello world")
                    .htmlDisplay("<b><font color=\"#FF0000\">Html</font></b>")
                    .add();
            b.withDouble()
                    .select(bean, "weight")
                    .min(0).max(1)
                    .display("Weight")
                    .add();
            b.with(long.class)
                    .select(bean, "duration")
                    .editor(DhmsPropertyEditor.class)
                    .add();
            result.put(b.build());

            return result;
        }
    }

    public static class DemoBean {

        Charset charset = Charset.defaultCharset();
        DataFormat dataFormat = DataFormat.DEFAULT;
        TsFrequency frequency = TsFrequency.Monthly;
        String database = "";
        String fileAsString = "";
        Day day = new Day(new Date());
        int offset = 0;
        File file = new File("");
        int readOnly = 123;
        StateValue stateValue = StateValue.DONE;
        double weight = 0.3;
        long duration = 60 * 1000;
        TsPeriodSelector periodSelector = new TsPeriodSelector();

        public Charset getCharset() {
            return charset;
        }

        public void setCharset(Charset charset) {
            this.charset = charset;
        }

        public DataFormat getDataFormat() {
            return dataFormat;
        }

        public void setDataFormat(DataFormat dataFormat) {
            this.dataFormat = dataFormat;
        }

        public TsFrequency getFrequency() {
            return frequency;
        }

        public void setFrequency(TsFrequency frequency) {
            this.frequency = frequency;
        }

        public String getDatabase() {
            return database;
        }

        public void setDatabase(String database) {
            this.database = database;
        }

        public String getFileAsString() {
            return fileAsString;
        }

        public void setFileAsString(String file) {
            this.fileAsString = file;
        }

        public Day getDay() {
            return day;
        }

        public void setDay(Day day) {
            this.day = day;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public int getReadOnly() {
            return readOnly;
        }

        public StateValue getStateValue() {
            return stateValue;
        }

        public void setStateValue(StateValue stateValue) {
            this.stateValue = stateValue;
        }

        public double getWeight() {
            return weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        public TsPeriodSelector getPeriodSelector() {
            return periodSelector;
        }

        public void setPeriodSelector(TsPeriodSelector periodSelector) {
            this.periodSelector = periodSelector;
        }
    }
}
