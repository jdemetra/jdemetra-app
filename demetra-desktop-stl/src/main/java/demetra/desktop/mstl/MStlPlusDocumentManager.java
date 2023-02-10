/*
 * Copyright 2023 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop.mstl;

import demetra.desktop.workspace.AbstractWorkspaceTsItemManager;
import demetra.desktop.workspace.WorkspaceItemManager;
import demetra.stl.MStlPlusSpec;
import demetra.util.Id;
import demetra.util.LinearId;
import demetra.stl.MStlSpec;
import jdplus.mstlplus.MStlPlusDocument;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(service = WorkspaceItemManager.class,
        position = 3000)
public class MStlPlusDocumentManager extends AbstractWorkspaceTsItemManager<MStlPlusSpec, MStlPlusDocument> {


    public static final LinearId ID = new LinearId(MStlSpec.FAMILY, "documents", MStlSpec.METHOD);
    public static final String PATH = "mstlplus.doc";
    public static final String ITEMPATH = "mstlplus.doc.item";
    public static final String CONTEXTPATH = "mstlplus.doc.context";

    @Override
    protected String getItemPrefix() {
        return "MStlPlusDoc";
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    public MStlPlusDocument createNewObject() {
        return new MStlPlusDocument();
    }

    @Override
    public ItemType getItemType() {
        return ItemType.Doc;
    }

    @Override
    public String getActionsPath() {
        return PATH;
    }

    @Override
    public Status getStatus() {
        return Status.Certified;
    }

    @Override
    public Class<MStlPlusDocument> getItemClass() {
        return MStlPlusDocument.class;
    }

}
