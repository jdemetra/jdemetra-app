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
package ec.nbdemetra.ws;

import com.google.common.base.StandardSystemProperty;
import ec.demetra.workspace.WorkspaceFamily;
import ec.demetra.workspace.file.FileFormat;
import ec.demetra.workspace.file.FileWorkspace;
import ec.nbdemetra.ui.calendars.CalendarDocumentManager;
import ec.tss.tsproviders.DataSource;
import ec.tss.xml.calendar.XmlCalendars;
import ec.tstoolkit.algorithm.ProcessingContext;
import ec.tstoolkit.timeseries.calendars.GregorianCalendarManager;
import ec.tstoolkit.timeseries.calendars.IGregorianCalendarProvider;
import ec.tstoolkit.timeseries.regression.TsVariables;
import ec.tstoolkit.utilities.LinearId;
import ec.tstoolkit.utilities.NameManager;
import ec.tstoolkit.utilities.Paths;
import ec.util.desktop.Desktop;
import ec.util.desktop.Desktop.KnownFolder;
import ec.util.desktop.DesktopManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.function.UnaryOperator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Jean Palate
 * @since 1.0.0
 */
@ServiceProvider(service = IWorkspaceRepository.class, position = 10)
public class FileRepository extends AbstractWorkspaceRepository implements LookupListener {

    public static final String NAME = "File", FILENAME = "fileName", VERSION = "20120925";

    @Deprecated
    private static final FileChooserBuilder CALENDAR_FILE_CHOOSER;

    static {
        CALENDAR_FILE_CHOOSER = new FileChooserBuilder(CalendarDocumentManager.class)
                .setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Xml files", "xml"));
    }

    private final Lookup.Result<IWorkspaceItemRepository> repositoryLookup;
    private final FileChooserBuilder wsFileChooser;

    public FileRepository() {
        this.repositoryLookup = Lookup.getDefault().lookupResult(IWorkspaceItemRepository.class);
        this.wsFileChooser = new FileChooserBuilder(FileRepository.class)
                .setDefaultWorkingDirectory(getDefaultWorkingDirectory(DesktopManager.get(), System::getProperty))
                .setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Xml files", "xml"));
    }

    @Nonnull
    public static DataSource encode(@Nullable File file) {
        if (file != null) {
            String sfile = file.getAbsolutePath();
            sfile = Paths.changeExtension(sfile, "xml");
            return DataSource.of(NAME, VERSION, FILENAME, sfile);
        }
        return DataSource.of(NAME, VERSION);
    }

    @Nullable
    public static File decode(@Nonnull DataSource source) {
        if (!source.getProviderName().equals(NAME)) {
            return null;
        }
        if (source.getVersion().equals(VERSION)) {
            String file = source.get(FILENAME);
            if (file != null) {
                return new File(file);
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public DataSource getDefaultDataSource() {
        return encode(null);
    }

    @Override
    public boolean saveAs(Workspace ws) {
        File file = wsFileChooser.showSaveDialog();
        if (file == null) {
            return false;
        }

        try {
            ws.loadAll();
            ws.setName(Paths.changeExtension(file.getName(), null));
            ws.setDataSource(encode(file));
            return save(ws, true);
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    protected boolean saveWorkspace(Workspace ws) {
        File file = decode(ws.getDataSource());
        if (file == null) {
            return saveAs(ws);
        }

        Path target = file.toPath();
        try (FileWorkspace storage = Files.exists(target)
                ? FileWorkspace.open(target)
                : FileWorkspace.create(target, FileFormat.GENERIC)) {
            storage.setName(ws.getName());
            storeCalendar(storage, ws.getContext().getGregorianCalendars());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return false;
        }

        ws.resetDirty();
        ws.getContext().resetDirty();
        return true;
    }

    @Override
    protected boolean deleteWorkspace(Workspace ws) {
        // TODO
        return false;
    }

    @Override
    public Workspace open() {
        java.io.File file = wsFileChooser.showOpenDialog();
        if (file == null) {
            return null;
        }

        Workspace ws = new Workspace(encode(file), Paths.changeExtension(file.getName(), null));
        if (!load(ws)) {
            NotifyDescriptor nd = new NotifyDescriptor.Message(file.getName() + " is not a valid workspace!");
            DialogDisplayer.getDefault().notify(nd);
            return null;
        } else {
            return ws;
        }
    }

    @Override
    public boolean load(Workspace ws) {
        if (!(ws.getRepository() instanceof FileRepository)) {
            return false;
        }

        File file = decode(ws.getDataSource());
        if (file == null || !file.exists()) {
            return false;
        }

        try (FileWorkspace storage = FileWorkspace.open(file.toPath())) {
            ws.setName(storage.getName());
            loadCalendars(storage, ws);
            loadItems(storage.getItems(), ws);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            return false;
        }

        ws.resetDirty();
        ws.getContext().resetDirty();
        return true;
    }

    private static void loadItems(Collection<ec.demetra.workspace.WorkspaceItem> items, Workspace ws) {
        items.forEach(o -> {
            if (!o.getFamily().equals(WorkspaceFamily.UTIL_CAL)) {
                WorkspaceItem<?> witem = WorkspaceItem.item(LinearId.of(o.getFamily()), o.getLabel(), o.getId(), o.getComments());
                ws.quietAdd(witem);
                IWorkspaceItemManager<?> manager = WorkspaceFactory.getInstance().getManager(witem.getFamily());
                if (manager != null && manager.isAutoLoad()) {
                    witem.load();
                }
            }
        });
    }

    private static final ec.demetra.workspace.WorkspaceItem CAL_ID = ec.demetra.workspace.WorkspaceItem.builder().family(WorkspaceFamily.UTIL_CAL).id("Calendars").build();

    private static void storeCalendar(FileWorkspace storage, GregorianCalendarManager value) {
        try {
            storage.store(CAL_ID, value);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private static void loadCalendars(FileWorkspace storage, Workspace ws) {
        try {
            GregorianCalendarManager source = (GregorianCalendarManager) storage.load(CAL_ID);
            GregorianCalendarManager target = ws.getContext().getGregorianCalendars();
            for (String name : source.getNames()) {
                IGregorianCalendarProvider cal = source.get(name);
                target.set(name, cal);
                if (ws.searchDocument(cal) == null) {
                    WorkspaceItem<IGregorianCalendarProvider> item = WorkspaceItem.system(CalendarDocumentManager.ID, name, cal);
                    ws.add(item);
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Deprecated
    public static String getRepositoryRootFolder(Workspace ws) {
        File id = decode(ws.getDataSource());
        if (id == null) {
            File defaultWsFolder = getDefaultWorkingDirectory(DesktopManager.get(), System::getProperty);
            if (!defaultWsFolder.exists()) {
                defaultWsFolder.mkdirs();
            }
            return Paths.concatenate(defaultWsFolder.getAbsolutePath(), ws.getName());
        } else {
            return Paths.changeExtension(id.getAbsolutePath(), null);
        }
    }

    @Deprecated
    public static String getRepositoryFolder(Workspace ws, String repository, boolean create) {
        String root = getRepositoryRootFolder(ws);
        File frepo = new File(root, repository);
        if (frepo.exists() && !frepo.isDirectory()) {
            return null;
        }
        if (!frepo.exists() && create) {
            frepo.mkdirs();
        }
        return frepo.getAbsolutePath();
    }

    @Override
    public Collection<Class> getSupportedTypes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void initialize() {
        repositoryLookup.allInstances().forEach(o -> register(o.getSupportedType(), o));
    }

    @Override
    public void resultChanged(LookupEvent le) {
        initialize();
    }

    @Deprecated
    public static void importCalendars(Workspace ws) {
        java.io.File file = CALENDAR_FILE_CHOOSER.showOpenDialog();
        if (file != null) {
            loadCalendars(ws, file.getAbsolutePath());
        }
    }

    @Deprecated
    public static boolean loadCalendars(Workspace ws, String file) {
        GregorianCalendarManager wsMgr = ws.getContext().getGregorianCalendars();
        XmlCalendars xml = AbstractFileItemRepository.loadXmlLegacy(file, XmlCalendars.class);
        if (xml != null) {
            xml.copyTo(wsMgr);
        } else // try Demetra+ format
        {
            ec.tss.xml.legacy.XmlCalendars oxml = AbstractFileItemRepository.loadXmlLegacy(file, ec.tss.xml.legacy.XmlCalendars.class);
            if (oxml != null) {
                oxml.copyTo(wsMgr);
            } else {
                return false;
            }
        }
        for (String s : wsMgr.getNames()) {
            IGregorianCalendarProvider cal = wsMgr.get(s);
            if (ws.searchDocument(cal) == null) {
                WorkspaceItem<IGregorianCalendarProvider> item = WorkspaceItem.system(CalendarDocumentManager.ID, s, cal);
                ws.add(item);
            }
        }
        return true;
    }

    @Deprecated
    public static boolean loadVariables(Workspace ws, String file) {
        NameManager<TsVariables> wsMgr = ws.getContext().getTsVariableManagers();
        TsVariables mgr = AbstractFileItemRepository.loadLegacy(file, ec.tss.xml.legacy.XmlTsVariables.class);
        if (mgr != null) {
            wsMgr.set(ProcessingContext.LEGACY, mgr);
            return true;
        } else {
            return false;
        }
    }

    @Deprecated
    public static boolean saveCalendars(Workspace ws, String file) {
        GregorianCalendarManager wsMgr = ws.getContext().getGregorianCalendars();
        return AbstractFileItemRepository.saveLegacy(file, wsMgr, XmlCalendars.class);
    }

    private static File getDefaultWorkingDirectory(Desktop desktop, UnaryOperator<String> properties) {
        File documents = getDocumentsDirectory(desktop).orElseGet(() -> getUserHome(properties));
        return new File(documents, "Demetra+");
    }

    private static Optional<File> getDocumentsDirectory(Desktop desktop) {
        if (desktop.isSupported(Desktop.Action.KNOWN_FOLDER_LOOKUP)) {
            try {
                return Optional.ofNullable(desktop.getKnownFolderPath(KnownFolder.DOCUMENTS));
            } catch (IOException ex) {
                // log this?
            }
        }
        return Optional.empty();
    }

    private static File getUserHome(UnaryOperator<String> properties) {
        return new File(properties.apply(StandardSystemProperty.USER_HOME.key()));
    }
}
