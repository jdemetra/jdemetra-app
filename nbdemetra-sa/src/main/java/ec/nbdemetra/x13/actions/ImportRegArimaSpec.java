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

import com.google.common.base.Optional;
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.interchange.ImportAction;
import ec.nbdemetra.ui.interchange.Importable;
import ec.nbdemetra.ui.nodes.SingleNodeAction;
import ec.nbdemetra.ws.WorkspaceFactory;
import ec.nbdemetra.ws.WorkspaceItem;
import ec.nbdemetra.x13.RegArimaSpecificationManager;
import ec.tss.tsproviders.utils.Parsers;
import ec.tss.xml.information.XmlInformationSet;
import ec.tstoolkit.algorithm.IProcSpecification;
import ec.tstoolkit.modelling.arima.x13.RegArimaSpecification;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.swing.JMenuItem;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

/**
 * Action on RegArima specification workspace node allowing the import
 *
 * @author Mats Maggi
 */
@ActionID(category = "Edit", id = "ec.nbdemetra.x13.actions.ImportRegArimaSpec")
@ActionRegistration(displayName = "#CTL_ImportRegArimaSpec", lazy = false)
@ActionReferences({
    @ActionReference(path = RegArimaSpecificationManager.PATH, position = 1000)
})
@Messages("CTL_ImportRegArimaSpec=Import from")
public class ImportRegArimaSpec extends SingleNodeAction<Node> implements Presenter.Popup {

    public ImportRegArimaSpec() {
        super(Node.class);
    }

    @Override
    protected void performAction(Node activatedNode) {

    }

    @Override
    public JMenuItem getPopupPresenter() {
        JMenuItem result = ImportAction.getPopupPresenter(getImportables());
        result.setText(Bundle.CTL_ImportRegArimaSpec());
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
                return RegArimaSpecification.class.getName();
            }

            @Override
            public void importConfig(Config config) throws IllegalArgumentException {
                RegArimaSpecification spec = fromConfig(config);
                if (spec != null) {
                    WorkspaceItem<IProcSpecification> ndoc = WorkspaceItem.newItem(RegArimaSpecificationManager.ID, config.getName(), spec);
                    WorkspaceFactory.getInstance().getActiveWorkspace().add(ndoc);
                }
            }
        });
    }

    private static RegArimaSpecification fromConfig(@Nonnull Config config) throws IllegalArgumentException {
        if (!RegArimaSpecification.class.getName().equals(config.getDomain())) {
            throw new IllegalArgumentException("Invalid config");
        }

        Parsers.Parser<XmlInformationSet> parser = Parsers.onJAXB(XmlInformationSet.class);

        for (Map.Entry<String, String> o : config.getParams().entrySet()) {
            if (o.getKey().equals("specification")) {
                Optional<XmlInformationSet> xmlSpec = parser.tryParse(o.getValue());
                if (xmlSpec.isPresent()) {
                    RegArimaSpecification spec = new RegArimaSpecification();
                    spec.read(xmlSpec.get().create());
                    return spec;
                }
            }
        }

        return null;
    }
}
