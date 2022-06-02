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
package demetra.desktop.core.tsproviders;

import demetra.desktop.TsManager;
import demetra.desktop.tsproviders.DataSourceManager;
import demetra.tsprovider.FileLoader;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.BeanInfo;
import java.util.Comparator;

/**
 * @author Philippe Charles
 */
@ActionID(category = "File", id = OpenProvidersNodeAction.ID)
@ActionRegistration(displayName = "#CTL_OpenProvidersAction", lazy = false)
@Messages("CTL_OpenProvidersAction=Open")
public final class OpenProvidersNodeAction extends AbstractAction implements Presenter.Popup {

    public static final String ID = "demetra.desktop.core.tsproviders.OpenProvidersAction";

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JMenuItem getPopupPresenter() {
        JMenu result = new JMenu(Bundle.CTL_OpenProvidersAction());
        TsManager.get().getProviders()
                .filter(FileLoader.class::isInstance)
                .map(FileLoader.class::cast)
                .sorted(ON_CLASS_SIMPLENAME)
                .forEach(o -> result.add(new AbstractActionImpl(o)));
        return result;
    }

    private static final Comparator<FileLoader> ON_CLASS_SIMPLENAME = Comparator.comparing(o -> o.getClass().getSimpleName());

    private static final class AbstractActionImpl extends AbstractAction {

        private final FileLoader loader;

        public AbstractActionImpl(FileLoader loader) {
            super(loader.getDisplayName());
            super.putValue(Action.SMALL_ICON, DataSourceManager.get().getIcon(loader.getSource(), BeanInfo.ICON_COLOR_16x16, false));
            this.loader = loader;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object bean = loader.newBean();
            if (DataSourceManager.get().getBeanEditor(loader.getSource(), "Open data source").editBean(bean, Exceptions::printStackTrace)) {
                loader.open(loader.encodeBean(bean));
            }
        }
    }
}
