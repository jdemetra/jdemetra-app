/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
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
package ec.nbdemetra.ui.properties;

import ec.nbdemetra.ui.nodes.AbstractNodeBuilder;
import java.awt.Dialog;
import java.awt.Image;
import java.beans.IntrospectionException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;

/**
 *
 * @author Philippe Charles
 */
public class OpenIdePropertySheetBeanEditor implements IBeanEditor {

    private final String title;
    private final Image image;

    public OpenIdePropertySheetBeanEditor(String title, Image image) {
        this.title = title;
        this.image = image;
    }

    @Override
    public boolean editBean(Object bean) throws IntrospectionException {
        return editNode(new BeanNode(bean), title, image);
    }

    public static boolean editNode(@Nonnull Node node, @Nullable String title, @Nullable Image image) {
        PropertySheet v = new PropertySheet();
        v.setNodes(new Node[]{node});

        DialogDescriptor d = new DialogDescriptor(v, title);
        Dialog dialog = DialogDisplayer.getDefault().createDialog(d);
        if (image != null) {
            dialog.setIconImage(image);
        }
        dialog.setVisible(true);

        return d.getValue() == DialogDescriptor.OK_OPTION;
    }

    public static boolean editSheet(@Nonnull Sheet sheet, @Nullable String title, @Nullable Image image) {
        Node node = new AbstractNodeBuilder().sheet(sheet).build();
        return editNode(node, title, image);
    }
}
