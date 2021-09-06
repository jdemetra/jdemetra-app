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

import demetra.bridge.ToFileBean;
import demetra.bridge.TsConverter;
import demetra.desktop.TsManager;
import demetra.desktop.properties.NodePropertySetBuilder;
import demetra.desktop.tsproviders.AbstractDataSourceProviderBuddy;
import demetra.desktop.tsproviders.DataSourceProviderBuddy;
import ec.tss.tsproviders.IFileBean;
import ec.tss.tsproviders.IFileLoader;
import ec.tss.tsproviders.common.tsw.TswBean;
import ec.tss.tsproviders.common.tsw.TswProvider;
import java.awt.Image;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.List;
import nbbrd.service.ServiceProvider;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(DataSourceProviderBuddy.class)
public final class TswProviderBuddy extends AbstractDataSourceProviderBuddy {

    @Override
    public String getProviderName() {
        return TswProvider.SOURCE;
    }

    @Override
    public Image getIconOrNull(int type, boolean opened) {
        return ImageUtilities.loadImage("ec/nbdemetra/common/document-list.png", true);
    }

    @Override
    public boolean editBean(String title, Object bean) throws IntrospectionException {
        return super.editBean(title, bean instanceof ToFileBean ? ((ToFileBean) bean).getDelegate() : bean);
    }

    @Override
    protected List<Sheet.Set> createSheetSets(Object bean) throws IntrospectionException {
        return bean instanceof TswBean
                ? createSheetSets((TswBean) bean)
                : super.createSheetSets(bean);
    }

    private List<Sheet.Set> createSheetSets(TswBean bean) {
        List<Sheet.Set> result = new ArrayList<>();

        NodePropertySetBuilder b = new NodePropertySetBuilder();

        b.reset("Source");
        TsManager.getDefault()
                .getProvider(TswProvider.SOURCE)
                .map(TsConverter::fromTsProvider)
                .filter(TswProvider.class::isInstance)
                .map(TswProvider.class::cast)
                .ifPresent(o -> addFileProperty(b, bean, o));
        result.add(b.build());

        return result;
    }

    private static void addFileProperty(NodePropertySetBuilder b, IFileBean bean, IFileLoader loader) {
        b.withFile()
                .select(bean, "file")
                .display("Directory")
                .description("The path to the directory containing TSW files.")
                //                .filterForSwing(new FileLoaderFileFilter(loader))
                .paths(loader.getPaths())
                .directories(true)
                .files(false)
                .add();
    }
}
