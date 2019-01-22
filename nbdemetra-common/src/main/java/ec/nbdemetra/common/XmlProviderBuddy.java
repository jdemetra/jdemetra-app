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
package ec.nbdemetra.common;

import demetra.ui.TsManager;
import ec.nbdemetra.ui.properties.FileLoaderFileFilter;
import demetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.tsproviders.AbstractDataSourceProviderBuddy;
import ec.nbdemetra.ui.tsproviders.IDataSourceProviderBuddy;
import ec.tss.tsproviders.IFileLoader;
import ec.tss.tsproviders.common.xml.XmlBean;
import ec.tss.tsproviders.common.xml.XmlProvider;
import java.awt.Image;
import java.beans.IntrospectionException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.openide.nodes.Sheet.Set;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = IDataSourceProviderBuddy.class)
public class XmlProviderBuddy extends AbstractDataSourceProviderBuddy {

    @Override
    public String getProviderName() {
        return XmlProvider.SOURCE;
    }

    @Override
    public Image getIcon(int type, boolean opened) {
        return ImageUtilities.loadImage("ec/nbdemetra/common/document-code.png", true);
    }

    @Override
    protected List<Set> createSheetSets(Object bean) throws IntrospectionException {
        return bean instanceof XmlBean
                ? createSheetSets((XmlBean) bean)
                : super.createSheetSets(bean);
    }

    private List<Set> createSheetSets(XmlBean bean) {
        List<Set> result = new ArrayList<>();

        NodePropertySetBuilder b = new NodePropertySetBuilder();

        b.reset("Source");
        TsManager.getDefault()
                .lookup(IFileLoader.class, XmlProvider.SOURCE)
                .ifPresent(o -> addFileProperty(b, bean, o));
        addReaderProperty(b, bean);
        result.add(b.build());

        return result;
    }

    private static void addFileProperty(NodePropertySetBuilder b, XmlBean bean, IFileLoader loader) {
        b.withFile()
                .select(bean, "file")
                .display("Xml file")
                .description("The path to the xml file.")
                .filterForSwing(new FileLoaderFileFilter(loader))
                .paths(loader.getPaths())
                .directories(false)
                .add();
    }

    private static void addReaderProperty(NodePropertySetBuilder b, XmlBean bean) {
        b.with(Charset.class)
                .select(bean, "charset")
                .display("Charset")
                .description("The charset used to read the file.")
                .add();
    }
}
