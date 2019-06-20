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

import java.awt.Image;
import java.beans.IntrospectionException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;

/**
 *
 * @author Philippe Charles
 */
@Deprecated
public class OpenIdePropertySheetBeanEditor implements IBeanEditor {

    private final String title;
    private final Image image;

    public OpenIdePropertySheetBeanEditor(String title, Image image) {
        this.title = title;
        this.image = image;
    }

    @Override
    public boolean editBean(Object bean) throws IntrospectionException {
        return new PropertySheetDialogBuilder().title(title).icon(image).editBean(bean);
    }

    @Deprecated
    public static boolean editNode(@NonNull Node node, @Nullable String title, @Nullable Image image) {
        return new PropertySheetDialogBuilder().title(title).icon(image).editNode(node);
    }

    @Deprecated
    public static boolean editSheet(@NonNull Sheet sheet, @Nullable String title, @Nullable Image image) {
        return new PropertySheetDialogBuilder().title(title).icon(image).editSheet(sheet);
    }
}
