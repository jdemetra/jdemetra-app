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

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import ec.nbdemetra.ui.tsproviders.DataSourceProviderBuddySupport;
import ec.nbdemetra.ui.tsproviders.ProvidersNode;
import ec.tss.tsproviders.IDataSourceLoader;
import ec.tss.tsproviders.IDataSourceProvider;
import ec.tss.tsproviders.IFileLoader;
import ec.tss.tsproviders.TsProviders;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.io.File;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
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
@ActionReferences({
    @ActionReference(path = ProvidersNode.ACTION_PATH, position = 1310, separatorBefore = 1300)
})
@Messages("CTL_OpenProvidersAction=Open")
public final class OpenProvidersAction extends AbstractAction implements Presenter.Popup {

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JMenuItem getPopupPresenter() {
        JMenu result = new JMenu(Bundle.CTL_OpenProvidersAction());
        for (IFileLoader o : TsProviders.all().filter(IFileLoader.class).toSortedList(ON_CLASS_SIMPLENAME)) {
            result.add(new AbstractActionImpl(o));
        }
        return result;
    }

    public static List<IFileLoader> getLoaders(final File file) {
        return TsProviders.all().filter(IFileLoader.class).filter(new Predicate<IFileLoader>() {
            @Override
            public boolean apply(IFileLoader input) {
                return input.accept(file);
            }
        }).toList();
    }

    public static <T extends IDataSourceLoader> Optional<T> chooseLoader(List<T> loaders) {
        if (loaders.size() == 1) {
            return Optional.of(loaders.get(0));
        }
        JComboBox cb = new JComboBox(Iterables.toArray(loaders, IDataSourceLoader.class));
        cb.setRenderer(new LoaderRenderer());
        DialogDescriptor dd = new DialogDescriptor(cb, "Choose a loader");
        if (DialogDisplayer.getDefault().notify(dd) == NotifyDescriptor.OK_OPTION) {
            return Optional.of((T) cb.getSelectedItem());
        }
        return Optional.absent();
    }

    private static final class LoaderRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel result = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            result.setText(((IFileLoader) value).getDisplayName());
            result.setIcon(ImageUtilities.image2Icon(DataSourceProviderBuddySupport.getDefault().get((IFileLoader) value).getIcon(BeanInfo.ICON_COLOR_16x16, false)));
            return result;
        }
    }
    //
    private static final Ordering<IDataSourceProvider> ON_CLASS_SIMPLENAME = Ordering.natural().onResultOf(new Function<IDataSourceProvider, String>() {
        @Override
        public String apply(IDataSourceProvider input) {
            return input.getClass().getSimpleName();
        }
    });

    private static final class AbstractActionImpl extends AbstractAction {

        private final IFileLoader loader;

        public AbstractActionImpl(IFileLoader loader) {
            super(loader.getDisplayName());
            Image image = DataSourceProviderBuddySupport.getDefault().get(loader).getIcon(BeanInfo.ICON_COLOR_16x16, false);
            if (image != null) {
                super.putValue(Action.SMALL_ICON, ImageUtilities.image2Icon(image));
            }
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
