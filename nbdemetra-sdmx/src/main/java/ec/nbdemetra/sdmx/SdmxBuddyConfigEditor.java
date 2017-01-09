/*
 * Copyright 2013 National Bank of Belgium
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
package ec.nbdemetra.sdmx;

import ec.nbdemetra.ui.properties.PropertySheetDialogBuilder;
import ec.nbdemetra.ui.properties.IBeanEditor;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import java.beans.IntrospectionException;
import org.openide.nodes.Sheet;

/**
 *
 * @author Philippe Charles
 */
final class SdmxBuddyConfigEditor implements IBeanEditor {

    @Override
    public boolean editBean(Object bean) throws IntrospectionException {
        Sheet sheet = new Sheet();
        NodePropertySetBuilder b = new NodePropertySetBuilder();

        b.withBoolean().select(bean, "compactNaming").name("Compact naming").add();
        b.withBoolean().select(bean, "keysInMetaData").name("Keys in metadata").add();

        sheet.put(b.build());
        return new PropertySheetDialogBuilder().editSheet(sheet);
    }
}
