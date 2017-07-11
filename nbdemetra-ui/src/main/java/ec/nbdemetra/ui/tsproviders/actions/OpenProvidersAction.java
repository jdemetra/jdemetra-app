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
package ec.nbdemetra.ui.tsproviders.actions;

import com.google.common.base.Optional;
import ec.nbdemetra.ui.tsproviders.DataSourceProviderBuddySupport;
import ec.tss.tsproviders.IDataSourceLoader;
import ec.tss.tsproviders.IDataSourceProvider;
import ec.tss.tsproviders.IFileLoader;
import ec.tss.tsproviders.TsProviders;
import ec.util.list.swing.JLists;
import java.awt.event.ActionEvent;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.io.File;
import java.util.Comparator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

/**
 *
 * @author Philippe Charles
 */
@ActionID(category = "File", id = "ec.nbdemetra.ui.tsproviders.actions.OpenProvidersAction")
@ActionRegistration(displayName = "#CTL_OpenProvidersAction", lazy = false)
@Messages("CTL_OpenProvidersAction=Open")
public final class OpenProvidersAction extends AbstractAction implements Presenter.Popup {

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JMenuItem getPopupPresenter() {
        JMenu result = new JMenu(Bundle.CTL_OpenProvidersAction());
        TsProviders.all().filter(IFileLoader.class).stream()
                .sorted(ON_CLASS_SIMPLENAME)
                .forEach(o -> result.add(new AbstractActionImpl(o)));
        return result;
    }

    public static List<IFileLoader> getLoaders(final File file) {
        return TsProviders.all().filter(IFileLoader.class).filter(o -> o.accept(file)).toList();
    }

    public static <T extends IDataSourceLoader> Optional<T> chooseLoader(List<T> loaders) {
        if (loaders.size() == 1) {
            return Optional.of(loaders.get(0));
        }
        JComboBox cb = new JComboBox(loaders.toArray());
        cb.setRenderer(JLists.cellRendererOf(OpenProvidersAction::renderLoader));
        DialogDescriptor dd = new DialogDescriptor(cb, "Choose a loader");
        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION) {
            return Optional.of((T) cb.getSelectedItem());
        }
        return Optional.absent();
    }

    private static void renderLoader(JLabel label, Object value) {
        IDataSourceLoader loader = (IDataSourceLoader) value;
        label.setText(loader.getDisplayName());
        label.setIcon(DataSourceProviderBuddySupport.getDefault().getIcon(loader.getSource(), BeanInfo.ICON_COLOR_16x16, false).map(ImageUtilities::image2Icon).orElse(null));
    }

    private static final Comparator<IDataSourceProvider> ON_CLASS_SIMPLENAME = Comparator.comparing(o -> o.getClass().getSimpleName());

    private static final class AbstractActionImpl extends AbstractAction {

        private final IFileLoader loader;

        public AbstractActionImpl(IFileLoader loader) {
            super(loader.getDisplayName());
            DataSourceProviderBuddySupport.getDefault()
                    .getIcon(loader.getSource(), BeanInfo.ICON_COLOR_16x16, false)
                    .map(ImageUtilities::image2Icon)
                    .ifPresent(o -> super.putValue(Action.SMALL_ICON, o));
            this.loader = loader;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object bean = loader.newBean();
            try {
                if (DataSourceProviderBuddySupport.getDefault().get(loader).editBean("Open data source", bean)) {
                    loader.open(loader.encodeBean(bean));
                }
            } catch (IntrospectionException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
