/*
 * Copyright 2016 National Bank of Belgium
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
package demetra.ui.properties;

import demetra.ui.nodes.AbstractNodeBuilder;
import demetra.util.TreeTraverser;
import ec.util.table.swing.JTables;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Image;
import java.beans.IntrospectionException;
import java.util.Arrays;
import java.util.Collections;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.Icon;
import javax.swing.JTable;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Philippe Charles
 * @since 2.2.0
 */
public final class PropertySheetDialogBuilder {

    private String title;
    private Image image;

    public PropertySheetDialogBuilder() {
        this.title = null;
        this.image = null;
    }

    @Nonnull
    public PropertySheetDialogBuilder title(@Nullable String title) {
        this.title = title;
        return this;
    }

    @Nonnull
    public PropertySheetDialogBuilder icon(@Nullable Image image) {
        this.image = image;
        return this;
    }

    @Nonnull
    public PropertySheetDialogBuilder icon(@Nullable Icon icon) {
        return icon != null ? icon(ImageUtilities.icon2Image(icon)) : this;
    }

    public boolean editNode(@Nonnull Node node) {
        PropertySheet v = new PropertySheet();
        v.setNodes(new Node[]{node});

        TreeTraverser.of(v, PropertySheetDialogBuilder::children)
                .breadthFirstStream()
                .filter(o -> o instanceof JTable)
                .findFirst()
                .ifPresent(o -> JTables.setWidthAsPercentages(((JTable) o), .3, .7));

        DialogDescriptor d = new DialogDescriptor(v, title);
        Dialog dialog = DialogDisplayer.getDefault().createDialog(d);
        if (image != null) {
            dialog.setIconImage(image);
        }
        dialog.setVisible(true);

        return d.getValue() == DialogDescriptor.OK_OPTION;
    }

    public boolean editBean(@Nonnull Object bean) throws IntrospectionException {
        return editNode(new BeanNode(bean));
    }

    public boolean editSheet(@Nonnull Sheet sheet) {
        return editNode(new AbstractNodeBuilder().sheet(sheet).build());
    }

    private static Iterable<Component> children(Component c) {
        return c instanceof Container && ((Container) c).getComponentCount() > 0
                ? Arrays.asList(((Container) c).getComponents())
                : Collections.emptyList();
    }
}
