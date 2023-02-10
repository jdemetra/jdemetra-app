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
package demetra.desktop.stl;

import demetra.desktop.workspace.AbstractWorkspaceTsItemManager;
import demetra.desktop.workspace.WorkspaceItemManager;
import demetra.stl.MStlSpec;
import demetra.stl.StlPlusSpec;
import demetra.util.Id;
import demetra.util.LinearId;
import jdplus.stlplus.StlPlusDocument;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author palatej
 */
@ServiceProvider(service = WorkspaceItemManager.class,
        position = 3000)
public class StlPlusDocumentManager extends AbstractWorkspaceTsItemManager<StlPlusSpec, StlPlusDocument> {


    public static final LinearId ID = new LinearId(MStlSpec.FAMILY, "documents", StlPlusSpec.METHOD);
    public static final String PATH = "stlplus.doc";
    public static final String ITEMPATH = "stlplus.doc.item";
    public static final String CONTEXTPATH = "stlplus.doc.context";

    @Override
    protected String getItemPrefix() {
        return "StlPlusDoc";
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    public StlPlusDocument createNewObject() {
        return new StlPlusDocument();
    }

    @Override
    public WorkspaceItemManager.ItemType getItemType() {
        return WorkspaceItemManager.ItemType.Doc;
    }

    @Override
    public String getActionsPath() {
        return PATH;
    }

    @Override
    public WorkspaceItemManager.Status getStatus() {
        return WorkspaceItemManager.Status.Certified;
    }

    @Override
    public Class<StlPlusDocument> getItemClass() {
        return StlPlusDocument.class;
    }

}
