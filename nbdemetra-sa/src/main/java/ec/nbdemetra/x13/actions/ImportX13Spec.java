/*
 * Copyright 2016 National Bank of Belgium
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
package ec.nbdemetra.x13.actions;

import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.interchange.ImportAction;
import ec.nbdemetra.ui.interchange.Importable;
import ec.nbdemetra.ui.nodes.SingleNodeAction;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.x13.X13SpecificationManager;
import ec.satoolkit.x13.X13Specification;
import ec.tss.tsproviders.utils.Parsers;
import ec.tss.xml.information.XmlInformationSet;
import ec.tss.xml.x13.XmlX13Specification;
import ec.tstoolkit.algorithm.IProcSpecification;
import java.util.Collections;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;
import javax.swing.JMenuItem;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

/**
 * Action on X13 specification workspace node allowing the import
 *
 * @author Mats Maggi
 */
@ActionID(category = "Edit", id = "ec.nbdemetra.x13.actions.ImportX13Spec")
@ActionRegistration(displayName = "#CTL_ImportX13Spec", lazy = false)
@ActionReferences({
    @ActionReference(path = X13SpecificationManager.PATH, position = 1000)
})
@Messages("CTL_ImportX13Spec=Import from")
public class ImportX13Spec extends SingleNodeAction<Node> implements Presenter.Popup {

    public ImportX13Spec() {
        super(Node.class);
    }

    @Override
    protected void performAction(Node activatedNode) {

    }

    @Override
    public JMenuItem getPopupPresenter() {
        JMenuItem result = ImportAction.getPopupPresenter(getImportables());
        result.setText(Bundle.CTL_ImportX13Spec());
        return result;
    }

    @Override
    protected boolean enable(Node activatedNode) {
        return true;
    }

    @Override
    public String getName() {
        return null;
    }

    private List<Importable> getImportables() {
        return Collections.<Importable>singletonList(new Importable() {

            @Override
            public String getDomain() {
                return X13Specification.class.getName();
            }

            @Override
            public void importConfig(Config config) throws IllegalArgumentException {
                X13Specification spec = fromConfig(config);
                if (spec != null) {
                    WorkspaceItem<IProcSpecification> ndoc = WorkspaceItem.newItem(X13SpecificationManager.ID, config.getName(), spec);
                    WorkspaceFactory.getInstance().getActiveWorkspace().add(ndoc);
                }
            }
        });
    }

    private static X13Specification fromConfig(@NonNull Config config) throws IllegalArgumentException {
        if (!X13Specification.class.getName().equals(config.getDomain())) {
            throw new IllegalArgumentException("Invalid config");
        }

        return config.getParam("specification")
                .map(Parsers.onJAXB(XmlInformationSet.class)::parse)
                .map(XmlInformationSet::create)
                .map(o -> {
                    X13Specification spec = new X13Specification();
                    spec.read(o);
                    return spec;
                })
                .orElse(null);
    }
}
