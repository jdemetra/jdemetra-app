/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa;

import demetra.bridge.TsConverter;
import ec.nbdemetra.ui.NbUtilities;
import ec.nbdemetra.ui.nodes.ControlNode;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.tsproviders.DataSourceProviderBuddySupport;
import ec.tss.sa.SaItem;
import ec.tss.tsproviders.utils.MultiLineNameUtil;
import ec.tstoolkit.MetaData;
import java.awt.Image;
import java.util.Optional;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Thomas Witthohn
 */
public class SaItemNode extends AbstractNode {

    public SaItemNode(SaItem item) {
        super(Children.create(new SaItemChildFactory(), false), Lookups.singleton(item));
        setName(item.getName());
        setDisplayName(MultiLineNameUtil.last(item.getName()));
        setShortDescription(MultiLineNameUtil.toHtml(item.getName()));
    }

    private Optional<Image> lookupIcon(int type, boolean opened) {
        SaItem item = getLookup().lookup(SaItem.class);
        return DataSourceProviderBuddySupport.getDefault().getIcon(item.getMoniker(), type, opened);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return lookupIcon(type, true).orElseGet(() -> super.getOpenedIcon(type));
    }

    @Override
    public Image getIcon(int type) {
        return lookupIcon(type, false).orElseGet(() -> super.getIcon(type));
    }

    @Override
    protected Sheet createSheet() {
        SaItem item = getLookup().lookup(SaItem.class);
        NodePropertySetBuilder b = new NodePropertySetBuilder();
        Sheet sheet = new Sheet();
        sheet.put(getDefinitionSheetSet(item, b));

        if (!MetaData.isNullOrEmpty(item.getMetaData())) {
            Sheet.Set info = NbUtilities.createMetadataPropertiesSet(item.getMetaData());
            sheet.put(info);
        }

        return sheet;
    }

    private static Sheet.Set getDefinitionSheetSet(SaItem item, NodePropertySetBuilder b) {
        return ControlNode.getDefinitionSheetSet(TsConverter.toTs(item.getTs()), b);
    }
}
