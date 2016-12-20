/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui;

import ec.nbdemetra.ui.nodes.StringProperty;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.tss.tsproviders.DataSource;
import ec.tstoolkit.MetaData;
import ec.tstoolkit.algorithm.IProcDocument;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Sheet;

/**
 *
 * @author Jean
 */
public class NbUtilities {

    public static Sheet.Set createMetadataPropertiesSet(final MetaData md) {
        NodePropertySetBuilder b = new NodePropertySetBuilder().name("Metadata");
        List<String> keys = new ArrayList<>(md.keySet());
        Collections.sort(keys);
        for (final String key : keys) {
            if (key.charAt(0) == '@') {
                String dname = key.substring(1);
                b.with(String.class).selectConst(key, md.get(key)).name(key).display(dname).add();
            } else {
                b.with(String.class).select(new StringProperty(key, md)).name(key).display(key).add();
            }
        }
        return b.build();
    }

    public static Sheet.Set creatDataSourcePropertiesSet(final DataSource dataSource) {
        NodePropertySetBuilder b = new NodePropertySetBuilder().name("Data source");
        b.with(String.class).select(dataSource, "getProviderName", null).display("Source").add();
        b.with(String.class).select(dataSource, "getVersion", null).display("Version").add();
        dataSource.forEach((k, v) -> b.with(String.class).selectConst(k, v).add());
        return b.build();
    }

    public static boolean editNote(IProcDocument doc) {
        if (doc == null) {
            return false;
        }
        JEditorPane editor = new JEditorPane();
        JScrollPane scroll = NbComponents.newJScrollPane(editor);
        scroll.setPreferredSize(new Dimension(300, 100));
        MetaData md = doc.getMetaData();
        String oldNote = md.get(MetaData.NOTE);
        editor.setText(oldNote);
        DialogDescriptor desc = new DialogDescriptor(scroll, "Note");
        if (DialogDisplayer.getDefault().notify(desc) != NotifyDescriptor.OK_OPTION) {
            return false;
        }
        String newNote = editor.getText();
        if (Objects.equals(oldNote, newNote)) {
            return false;
        }
        md.put(MetaData.NOTE, newNote);
        return true;
    }
}
