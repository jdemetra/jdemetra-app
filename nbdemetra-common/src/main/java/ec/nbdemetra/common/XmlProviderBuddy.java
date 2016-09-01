/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.common;

import ec.nbdemetra.ui.properties.FileLoaderFileFilter;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.tsproviders.AbstractDataSourceProviderBuddy;
import ec.nbdemetra.ui.tsproviders.IDataSourceProviderBuddy;
import ec.tss.tsproviders.IFileLoader;
import ec.tss.tsproviders.TsProviders;
import ec.tss.tsproviders.common.xml.XmlProvider;
import java.awt.Image;
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
    protected List<Set> createSheetSets(Object bean) {
        List<Set> result = new ArrayList<>();

        IFileLoader loader = TsProviders.lookup(XmlProvider.class, XmlProvider.SOURCE).get();
        
        NodePropertySetBuilder b = new NodePropertySetBuilder();

        b.reset("Source");
        b.withFile()
                .select(bean, "file")
                .display("Xml file")
                .description("The path to the xml file.")
                .filterForSwing(new FileLoaderFileFilter(loader))
                .paths(loader.getPaths())
                .directories(false)
                .add();
        b.with(Charset.class).select(bean, "charset").display("Charset").description("The charset used to read the file.").add();
        result.add(b.build());

        return result;
    }
}
