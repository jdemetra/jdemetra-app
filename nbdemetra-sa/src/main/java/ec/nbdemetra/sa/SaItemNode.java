/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa;

import ec.nbdemetra.ui.NbUtilities;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.tss.sa.SaItem;
import ec.tstoolkit.MetaData;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;

/**
 *
 * @author Thomas Witthohn
 */
public class SaItemNode extends AbstractNode {

    private final SaItem item;

    public SaItemNode(SaItem item) {
        super(Children.create(new SaItemChildFactory(), false));
        this.item = item;
    }

    @Override
    public String getDisplayName() {
        return item.getName();

    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        NodePropertySetBuilder b = new NodePropertySetBuilder().name("Time series");
        b.with(String.class).select("Time series name", item.getTs().getRawName()).add();
        b.withBoolean().select("Is frozen", item.getTs().isFrozen()).add();
        sheet.put(b.build());

        MetaData metaData = item.getMetaData();

        if (MetaData.isNullOrEmpty(metaData)) {
            return sheet;
        }

        Sheet.Set info = NbUtilities.createMetadataPropertiesSet(metaData);
        sheet.put(info);

        return sheet;
    }

}
