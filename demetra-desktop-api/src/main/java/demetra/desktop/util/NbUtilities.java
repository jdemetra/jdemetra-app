/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.util;

import demetra.tsprovider.TsMeta;
import demetra.desktop.properties.NodePropertySetBuilder;
import demetra.tsprovider.DataSource;
import demetra.util.Documented;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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

    public static Sheet.Set createMetadataPropertiesSet(final Map<String, String> md) {
        NodePropertySetBuilder b = new NodePropertySetBuilder().name("Metadata");
        List<String> keys = new ArrayList<>(md.keySet());
        Collections.sort(keys);
        for (final String key : keys) {
            if (key.charAt(0) == '@') {
                String dname = key.substring(1);
                b.with(String.class).selectConst(key, md.get(key)).name(key).display(dname).add();
            } else {
                b.with(String.class).selectConst(key, md.get(key)).name(key).display(key).add();
            }
        }
        return b.build();
    }

    public static Sheet.Set creatDataSourcePropertiesSet(final DataSource dataSource) {
        NodePropertySetBuilder b = new NodePropertySetBuilder().name("Data source");
        b.with(String.class).select(dataSource, "getProviderName", null).display("Source").add();
        b.with(String.class).select(dataSource, "getVersion", null).display("Version").add();
//        dataSource.forEach((k, v) -> b.with(String.class).selectConst(k, v).add());
        return b.build();
    }

    public static boolean editNote(Documented doc) {
        if (doc == null) {
            return false;
        }
        JEditorPane editor = new JEditorPane();
        JScrollPane scroll = NbComponents.newJScrollPane(editor);
        scroll.setPreferredSize(new Dimension(300, 100));
        Map<String, String> md = doc.getMetaData();
        String oldNote = TsMeta.NOTE.load(md);
        editor.setText(oldNote);
        DialogDescriptor desc = new DialogDescriptor(scroll, "Note");
        if (DialogDisplayer.getDefault().notify(desc) != NotifyDescriptor.OK_OPTION) {
            return false;
        }
        String newNote = editor.getText();
        if (Objects.equals(oldNote, newNote)) {
            return false;
        }
        TsMeta.NOTE.store(md, newNote);
        return true;
    }
}
