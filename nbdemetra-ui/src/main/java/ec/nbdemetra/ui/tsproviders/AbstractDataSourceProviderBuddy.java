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
package ec.nbdemetra.ui.tsproviders;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import ec.nbdemetra.ui.properties.ForwardingNodeProperty;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.properties.OpenIdePropertySheetBeanEditor;
import ec.tss.TsAsyncMode;
import ec.tss.TsMoniker;
import ec.tss.tsproviders.*;
import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.openide.ErrorManager;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.nodes.Sheet.Set;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Philippe Charles
 */
public abstract class AbstractDataSourceProviderBuddy implements IDataSourceProviderBuddy {

    @Override
    public Image getIcon(int type, boolean opened) {
        return ImageUtilities.loadImage("ec/nbdemetra/ui/nodes/document.png", true);
    }

    @Override
    public Image getIcon(DataSource dataSource, int type, boolean opened) {
        return getIcon(type, opened);
    }

    @Override
    public Image getIcon(DataSet dataSet, int type, boolean opened) {
        switch (dataSet.getKind()) {
            case COLLECTION:
                return ImageUtilities.loadImage("ec/nbdemetra/ui/nodes/folder.png", true);
            case SERIES:
                return ImageUtilities.loadImage("ec/nbdemetra/ui/nodes/chart_line.png", true);
            case DUMMY:
                return null;
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Image getIcon(IOException ex, int type, boolean opened) {
        return ImageUtilities.loadImage("ec/nbdemetra/ui/nodes/exclamation-red.png", true);
    }

    @Override
    public Image getIcon(TsMoniker moniker, int type, boolean opened) {
        return getIcon(type, opened);
    }

    static Sheet createSheet(List<Set> sets) {
        Sheet result = new Sheet();
        for (Set o : sets) {
            result.put(o);
        }
        return result;
    }

    @Override
    public Sheet createSheet() {
        return createSheet(createSheetSets());
    }

    protected List<Set> createSheetSets() {
        List<Set> result = new ArrayList<>();
        IDataSourceProvider provider = TsProviders.lookup(IDataSourceProvider.class, getProviderName()).get();
        NodePropertySetBuilder b = new NodePropertySetBuilder();
        b.with(String.class).select(provider, "getSource", null).display("Source").add();
        b.withEnum(TsAsyncMode.class).select(provider, "getAsyncMode", null).display("Async mode").add();
        b.with(Boolean.class).select(provider, "isAvailable", null).display("Available").add();
        b.withBoolean().selectConst("Loadable", provider instanceof IDataSourceLoader).add();
        b.withBoolean().selectConst("Files as source", provider instanceof IFileLoader).add();
        result.add(b.build());
        return result;
    }

    @Override
    public Sheet createSheet(DataSource dataSource) {
        return createSheet(createSheetSets(dataSource));
    }

    protected List<Set> createSheetSets(DataSource dataSource) {
        List<Set> result = new ArrayList<>();
        NodePropertySetBuilder b = new NodePropertySetBuilder().name("DataSource");
        b.with(String.class).select(dataSource, "getProviderName", null).display("Source").add();
        b.with(String.class).select(dataSource, "getVersion", null).display("Version").add();
        Optional<IDataSourceLoader> loader = TsProviders.lookup(IDataSourceLoader.class, dataSource);
        if (loader.isPresent()) {
            Object bean = loader.get().decodeBean(dataSource);
            try {
                for (Sheet.Set set : createSheetSets(bean)) {
                    for (Node.Property<?> o : set.getProperties()) {
                        b.add(ForwardingNodeProperty.readOnly(o));
                    }
                }
            } catch (IntrospectionException ex) {
                ErrorManager.getDefault().log(ex.getMessage());
            }
        }
        result.add(b.build());
        return result;
    }

    @Override
    public Sheet createSheet(DataSet dataSet) {
        return createSheet(createSheetSets(dataSet));
    }

    protected List<Set> createSheetSets(DataSet dataSet) {
        List<Set> result = Lists.newArrayList(createSheetSets(dataSet.getDataSource()));
        NodePropertySetBuilder b = new NodePropertySetBuilder().name("DataSet");
        b.withEnum(DataSet.Kind.class).select(dataSet, "getKind", null).display("Kind").add();
        fillParamProperties(b, dataSet);
        result.add(b.build());
        return result;
    }

    protected void fillParamProperties(NodePropertySetBuilder b, DataSet dataSet) {
        for (Map.Entry<String, String> o : dataSet.getParams().entrySet()) {
            b.with(String.class).selectConst(o.getKey(), o.getValue()).add();
        }
    }

    @Override
    public Sheet createSheet(IOException ex) {
        Sheet result = new Sheet();
        NodePropertySetBuilder b = new NodePropertySetBuilder().name("IOException");

        int i = 0;
        Throwable current = ex;
        while (current != null) {
            b.reset("throwable" + i++).display(current.getClass().getSimpleName());
            b.with(String.class).selectConst("Type", current.getClass().getName()).add();
            b.with(String.class).selectConst("Message", current.getMessage()).add();
            result.put(b.build());
            current = current.getCause();
        }

        return result;
    }

    @Override
    public boolean editBean(String title, Object bean) throws IntrospectionException {
        Sheet sheet = createSheet(createSheetSets(bean));
        Image image = getIcon(BeanInfo.ICON_COLOR_16x16, false);
        return OpenIdePropertySheetBeanEditor.editSheet(sheet, title, image);
    }

    protected List<Set> createSheetSets(Object bean) throws IntrospectionException {
        List<Set> result = new ArrayList<>();
        for (Node.PropertySet o : new BeanNode<>(bean).getPropertySets()) {
            Set set = Sheet.createPropertiesSet();
            set.put(o.getProperties());
            result.add(set);
        }
        return result;
    }
}
