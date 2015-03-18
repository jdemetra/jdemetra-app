/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ws;

import com.google.common.base.StandardSystemProperty;
import com.google.common.base.Throwables;
import ec.nbdemetra.ui.calendars.CalendarDocumentManager;
import ec.nbdemetra.ws.xml.XmlGenericWorkspace;
import ec.nbdemetra.ws.xml.compatibility.XmlWorkspace;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.DataSource.Builder;
import ec.tss.xml.calendar.XmlCalendars;
import ec.tstoolkit.algorithm.ProcessingContext;
import ec.tstoolkit.timeseries.calendars.GregorianCalendarManager;
import ec.tstoolkit.timeseries.calendars.IGregorianCalendarProvider;
import ec.tstoolkit.timeseries.regression.TsVariables;
import ec.tstoolkit.utilities.NameManager;
import ec.tstoolkit.utilities.Paths;
import ec.util.desktop.Desktop;
import ec.util.desktop.Desktop.KnownFolder;
import ec.util.desktop.DesktopManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author pcuser
 */
@ServiceProvider(service = IWorkspaceRepository.class,
        position = 10)
public class FileRepository extends AbstractWorkspaceRepository implements LookupListener {

    public static final String NAME = "File", FILENAME = "fileName", VERSION = "20120925";
    static final JAXBContext XML_GENERIC_WS_CONTEXT;
    static final JAXBContext XML_WS_CONTEXT;
    private static final FileChooserBuilder calendarChooserBuilder;

    static {
        try {
            calendarChooserBuilder = new FileChooserBuilder(CalendarDocumentManager.class);
            calendarChooserBuilder.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Xml files", "xml"));
            XML_GENERIC_WS_CONTEXT = JAXBContext.newInstance(XmlGenericWorkspace.class);
            XML_WS_CONTEXT = JAXBContext.newInstance(XmlWorkspace.class);
        } catch (JAXBException ex) {
            throw Throwables.propagate(ex);
        }
    }
    private Lookup.Result<IWorkspaceItemRepository> repositoryLookup;
    private final FileChooserBuilder fileChooserBuilder;

    public FileRepository() {
        this.repositoryLookup = Lookup.getDefault().lookupResult(IWorkspaceItemRepository.class);
        this.fileChooserBuilder = new FileChooserBuilder(FileRepository.class);
        fileChooserBuilder.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Xml files", "xml"));
    }

    public static DataSource encode(File file) {
        Builder builder = DataSource.builder(NAME, VERSION);
        if (file != null) {
            String sfile = file.getAbsolutePath();
            sfile = Paths.changeExtension(sfile, "xml");
            builder.put(FILENAME, sfile);
        }
        return builder.build();
    }

    public static File decode(DataSource source) {
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

    private static File getDocumentsFolder() {
        Desktop desktop = DesktopManager.get();
        if (desktop.isSupported(Desktop.Action.KNOWN_FOLDER_LOOKUP)) {
            try {
                return desktop.getKnownFolderPath(KnownFolder.DOCUMENTS);
            } catch (IOException ex) {
                // log this?
            }
        }
        return null;
    }

    private static String path() {
        File documents = getDocumentsFolder();
        if (documents == null) {
            // fallback
            documents = new File(StandardSystemProperty.USER_HOME.value());
        }
        File def = new File(documents, "Demetra+");
        if (!def.exists()) {
            def.mkdirs();
        }
        return def.getAbsolutePath();
    }
    private static String defPath = path();

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public DataSource getDefaultDataSource() {
        return encode(null);
    }

    @Override
    public Object getProperties() {
        // TODO
        return null;
    }

    @Override
    public void setProperties() {
        // TODO
    }

    @Override
    public boolean saveAs(Workspace ws) {
        java.io.File file = fileChooserBuilder.showSaveDialog();
        if (file != null) {
            try {
                ws.loadAll();
                ws.setName(Paths.changeExtension(file.getName(), null));
                ws.setDataSource(encode(file));
                return save(ws, true);
            } catch (Exception ex) {
                return false;
            }
        }
        return false;
    }

    @Override
    protected boolean saveWorkspace(Workspace ws) {
        File file = decode(ws.getDataSource());
        if (file == null) {
            return saveAs(ws);
        }
        try (FileOutputStream stream = new FileOutputStream(file)) {
            //XMLOutputFactory factory=XMLOutputFactory.newInstance();
            try (OutputStreamWriter writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)) {

                Marshaller marshaller = XML_GENERIC_WS_CONTEXT.createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                XmlGenericWorkspace xws = new XmlGenericWorkspace();
                if (!xws.from(ws)) {
                    return false;
                }
                marshaller.marshal(xws, writer);
                ws.resetDirty();
                writer.flush();
                saveContext(ws);
                return true;
            }
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    protected boolean deleteWorkspace(Workspace ws) {
        // TODO
        return false;
    }

    @Override
    public Workspace open() {
        java.io.File file = fileChooserBuilder.showOpenDialog();
        if (file != null) {
            Workspace ws = new Workspace(encode(file), Paths.changeExtension(file.getName(), null));
            if (!load(ws)) {
                NotifyDescriptor nd = new NotifyDescriptor.Message(file.getName() + " is not a valid workspace!");
                DialogDisplayer.getDefault().notify(nd);
                return null;
            } else {
                return ws;
            }
        }
        return null;
    }

    @Override
    public boolean load(Workspace ws) {
        if (!(ws.getRepository() instanceof FileRepository)) {
            return false;
        }
        if (loadWorkspace(ws)) {
            loadContext(ws);
            return true;
        }
        if (loadLegacyWorkspace(ws)) {
            loadContext(ws);
            return true;
        }
        return false;
    }

    private boolean loadLegacyWorkspace(Workspace ws) {
        File file = decode(ws.getDataSource());
        try {
            Unmarshaller unmarshaller = XML_WS_CONTEXT.createUnmarshaller();
            XmlWorkspace xws = (XmlWorkspace) unmarshaller.unmarshal(file);
            if (xws == null) {
                return false;
            }
            if (!xws.load(ws)) {
                return false;
            }
            ws.resetDirty();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean loadWorkspace(Workspace ws) {
        File file = decode(ws.getDataSource());
        try {
            Unmarshaller unmarshaller = XML_GENERIC_WS_CONTEXT.createUnmarshaller();
            XmlGenericWorkspace xws = (XmlGenericWorkspace) unmarshaller.unmarshal(file);
            if (xws == null) {
                return false;
            }
            if (!xws.to(ws)) {
                return false;
            }
            ws.resetDirty();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static String getRepositoryRootFolder(Workspace ws) {
        File id = decode(ws.getDataSource());
        if (id == null) {
            return Paths.concatenate(defPath, ws.getName());
        } else {
            return Paths.changeExtension(id.getAbsolutePath(), null);
        }
    }

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
        for (IWorkspaceItemRepository fac : repositoryLookup.allInstances()) {
            register(fac.getSupportedType(), fac);
        }
    }

    @Override
    public void resultChanged(LookupEvent le) {
        initialize();
    }

    private void loadContext(Workspace ws) {
        loadCalendars(ws, getCalendarsFile(ws, false));
        loadVariables(ws, getVariablesFile(ws, false));
        ws.getContext().resetDirty();
    }

    private void saveContext(Workspace ws) {
        saveCalendars(ws, getCalendarsFile(ws, true));
        ws.getContext().resetDirty();
    }

    public static void importCalendars(Workspace ws) {
        java.io.File file = calendarChooserBuilder.showOpenDialog();
        if (file != null) {
            loadCalendars(ws, file.getAbsolutePath());
        }
    }

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

    public static boolean saveCalendars(Workspace ws, String file) {
        GregorianCalendarManager wsMgr = ws.getContext().getGregorianCalendars();
        return AbstractFileItemRepository.saveLegacy(file, wsMgr, XmlCalendars.class);
    }

    private static String getCalendarsFile(Workspace ws, boolean create) {
        String folder = getRepositoryFolder(ws, CALENDARS_REPOSITORY, create);
        return Paths.concatenate(folder, CALENDARS);
    }
    private static final String CALENDARS_REPOSITORY = "Calendars";
    private static final String CALENDARS = "Calendars.xml";

    private static String getVariablesFile(Workspace ws, boolean create) {
        String folder = getRepositoryFolder(ws, VARIABLES_REPOSITORY, create);
        return Paths.concatenate(folder, VARIABLES);
    }
    private static final String VARIABLES_REPOSITORY = "Variables";
    private static final String VARIABLES = "Variables.xml";
}
