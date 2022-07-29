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
package demetra.desktop.benchmarking.documents;

import demetra.desktop.workspace.AbstractWorkspaceItemManager;
import demetra.desktop.workspace.WorkspaceItemManager;
import demetra.util.Id;
import demetra.util.LinearId;
import jdplus.benchmarking.univariate.CholetteDocument;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(
        service = WorkspaceItemManager.class, position = 1500)
public class CholetteDocumentManager extends AbstractWorkspaceItemManager<CholetteDocument> {

    public static final LinearId ID = new LinearId("Benchmarking", "Univariate", "Cholette");
    public static final String PATH = "cholette";
    public static final String ITEMPATH = "cholette.item";
    public static final String CONTEXTPATH = "cholette.context";

    @Override
    protected String getItemPrefix() {
        return "Cholette";
    }

    @Override
    public Id getId() {
        return ID;
    }

    @Override
    public CholetteDocument createNewObject() {
        return new CholetteDocument();
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
    public Class getItemClass() {
        return CholetteDocument.class;
    }

}
