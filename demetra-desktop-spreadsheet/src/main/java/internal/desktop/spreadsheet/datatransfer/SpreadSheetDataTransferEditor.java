package internal.desktop.spreadsheet.datatransfer;

import demetra.desktop.beans.BeanEditor;
import demetra.desktop.properties.NodePropertySetBuilder;
import demetra.desktop.properties.PropertySheetDialogBuilder;
import java.awt.Image;
import java.beans.IntrospectionException;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;

@lombok.RequiredArgsConstructor
final class SpreadSheetDataTransferEditor implements BeanEditor {

    @lombok.NonNull
    private final String title;

    @lombok.NonNull
    private final Image icon;

    @NbBundle.Messages({
        "bean.importTs.display=Allow import",
        "bean.importTs.description=Enable/disable import of time series.",
        "bean.exportTs.display=Allow export",
        "bean.exportTs.description=Enable/disable export of time series."
    })
    private Sheet getSheet(SpreadSheetDataTransferBean bean) {
        Sheet result = new Sheet();
        NodePropertySetBuilder b = new NodePropertySetBuilder();

        b.reset("tsimport").display("Time series import");
        b.withBoolean()
                .select("importTs", bean::isImportTs, bean::setImportTs)
                .display(Bundle.bean_importTs_display())
                .description(Bundle.bean_importTs_description())
                .add();
        result.put(b.build());

        b.reset("tsexport").display("Time series export");
        b.withBoolean()
                .selectField(bean, "exportTs")
                .display(Bundle.bean_exportTs_display())
                .description(Bundle.bean_exportTs_description())
                .add();
        result.put(b.build());

//        b.reset("other").display("Other");
        //            b.withBoolean().selectField(this, "importMatrix").display("Allow import").add();
//        b.withBoolean()
//                .selectField(bean, "exportMatrix")
//                .display("Allow matrix export")
//                .add();
//        b.withBoolean()
//                .selectField(bean, "importTable")
//                .display("Allow table import")
//                .add();
//        b.withBoolean()
//                .selectField(bean, "exportTable")
//                .display("Allow table export")
//                .add();
//        result.put(b.build());

        return result;
    }

    @Override
    public final boolean editBean(Object bean) throws IntrospectionException {
        SpreadSheetDataTransferBean config = (SpreadSheetDataTransferBean) bean;
        return new PropertySheetDialogBuilder()
                .title(title)
                .icon(icon)
                .editSheet(getSheet(config));
    }

}
