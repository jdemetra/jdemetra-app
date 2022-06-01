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

import demetra.desktop.TsManager;
import demetra.desktop.properties.NodePropertySetBuilder;
import demetra.desktop.tsproviders.DataSourceProviderBuddy;
import demetra.desktop.tsproviders.DataSourceProviderBuddyUtil;
import demetra.desktop.tsproviders.TsProviderProperties;
import demetra.tsp.text.XmlBean;
import demetra.tsprovider.FileLoader;
import java.awt.Image;
import java.beans.IntrospectionException;
import java.nio.charset.Charset;
import nbbrd.design.DirectImpl;
import nbbrd.service.ServiceProvider;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Philippe Charles
 */
@DirectImpl
@ServiceProvider(DataSourceProviderBuddy.class)
public final class XmlProviderBuddy implements DataSourceProviderBuddy {

    private static final String SOURCE = "Xml";

    @Override
    public String getProviderName() {
        return SOURCE;
    }

    @Override
    public Image getIconOrNull(int type, boolean opened) {
        return ImageUtilities.loadImage("ec/nbdemetra/common/document-code.png", true);
    }

    @Override
    public Sheet getSheetOfBeanOrNull(Object bean) throws IntrospectionException {
        return bean instanceof XmlBean
                ? createSheetSets((XmlBean) bean)
                : DataSourceProviderBuddy.super.getSheetOfBeanOrNull(bean);
    }

    private Sheet createSheetSets(XmlBean bean) {
        NodePropertySetBuilder b = new NodePropertySetBuilder();
        return DataSourceProviderBuddyUtil.sheetOf(
                createSource(b, bean)
        );
    }

    private Sheet.Set createSource(NodePropertySetBuilder b, XmlBean bean) {
        b.reset("Source");
        TsManager.get()
                .getProvider(FileLoader.class, SOURCE)
                .ifPresent(o -> TsProviderProperties.addFile(b, o, bean));
        addReaderProperty(b, bean);
        return b.build();
    }

    private static void addReaderProperty(NodePropertySetBuilder b, XmlBean bean) {
        b.with(Charset.class)
                .select("charset", bean::getCharset, bean::setCharset)
                .display("Charset")
                .description("The charset used to read the file.")
                .add();
    }
}
