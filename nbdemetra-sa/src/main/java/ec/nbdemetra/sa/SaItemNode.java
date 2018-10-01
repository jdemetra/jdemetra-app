/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa;

import ec.nbdemetra.ui.NbUtilities;
import ec.nbdemetra.ui.nodes.ControlNode;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.tsproviders.DataSourceProviderBuddySupport;
import ec.tss.Ts;
import ec.tss.TsStatus;
import ec.tss.sa.SaItem;
import ec.tss.tsproviders.utils.MultiLineNameUtil;
import ec.tstoolkit.MetaData;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import ec.tstoolkit.timeseries.simplets.TsPeriod;
import java.awt.Image;
import java.util.Comparator;
import java.util.Map;
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
        sheet.put(getDataSheetSet(item, b));

        if (!MetaData.isNullOrEmpty(item.getMetaData())) {
            Sheet.Set info = NbUtilities.createMetadataPropertiesSet(item.getMetaData());
            sheet.put(info);
        }

        return sheet;
    }

    private static Sheet.Set getDefinitionSheetSet(SaItem item, NodePropertySetBuilder b) {
        return ControlNode.getDefinitionSheetSet(item.getTs(), b);
    }

    private static Sheet.Set getDataSheetSet(SaItem item, NodePropertySetBuilder b) {
        Ts ts = item.getTs();
        b.reset("Input Data");
        TsData data = ts.getTsData();
        if (data != null) {
            b.withEnum(TsFrequency.class).select(data, "getFrequency", null).display("Frequency").add();
            b.with(TsPeriod.class).select(data, "getStart", null).display("First period").add();
            b.with(TsPeriod.class).select(data, "getLastPeriod", null).display("Last period").add();
            b.withInt().select(data, "getObsCount", null).display("Obs count").add();
            b.with(TsData.class).selectConst("values", data).display("Values").add();
        } else {
            b.withEnum(TsStatus.class).select(ts, "hasData", null).display("Data status").add();
            b.with(String.class).selectConst("InvalidDataCause", ts.getInvalidDataCause()).display("Invalid data cause").add();
        }
        MetaData md = ts.getMetaData();
        if (md != null) {
            md.entrySet().stream()
                    .filter(o -> !isFreezeKey(o.getKey()))
                    .sorted(Comparator.comparing(Map.Entry::getKey))
                    .forEach(o -> b.with(String.class).selectConst(o.getKey(), o.getValue()).add());
        } else {
            b.withEnum(TsStatus.class).select(ts, "hasMetaData", null).display("Meta data status").add();
        }
        return b.build();
    }

    private static boolean isFreezeKey(String key) {
        switch (key) {
            case Ts.SOURCE_OLD:
            case Ts.ID_OLD:
            case MetaData.SOURCE:
            case MetaData.ID:
            case MetaData.DATE:
                return true;
            default:
                return false;
        }
    }
}
