/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.sa.multiprocessing.ui;

import demetra.desktop.nodes.ControlNode;
import demetra.desktop.properties.NodePropertySetBuilder;
import demetra.desktop.tsproviders.DataSourceProviderBuddySupport;
import demetra.desktop.util.NbUtilities;
import demetra.sa.SaItem;
import demetra.util.MultiLineNameUtil;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.lookup.Lookups;

import java.awt.*;

/**
 * @author Thomas Witthohn
 */
public class SaItemNode extends AbstractNode {

    public SaItemNode(SaItem item) {
        super(Children.create(new SaItemChildFactory(), false), Lookups.singleton(item));
        setName(item.getName());
        setDisplayName(MultiLineNameUtil.last(item.getName()));
        setShortDescription(MultiLineNameUtil.toHtml(item.getName()));
    }

    private Image lookupIcon(int type, boolean opened) {
        SaItem item = getLookup().lookup(SaItem.class);
        return DataSourceProviderBuddySupport.getDefault().getImage(item.getDefinition().getTs().getMoniker(), type, opened);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return lookupIcon(type, true);
    }

    @Override
    public Image getIcon(int type) {
        return lookupIcon(type, false);
    }

    @Override
    protected Sheet createSheet() {
        SaItem item = getLookup().lookup(SaItem.class);
        NodePropertySetBuilder b = new NodePropertySetBuilder();
        Sheet sheet = new Sheet();
        sheet.put(getDefinitionSheetSet(item, b));

        if ((item.getMeta().isEmpty())) {
            Sheet.Set info = NbUtilities.createMetadataPropertiesSet(item.getMeta());
            sheet.put(info);
        }

        return sheet;
    }

    private static Sheet.Set getDefinitionSheetSet(SaItem item, NodePropertySetBuilder b) {
        return ControlNode.getDefinitionSheetSet(item.getDefinition().getTs(), b);
    }
}
