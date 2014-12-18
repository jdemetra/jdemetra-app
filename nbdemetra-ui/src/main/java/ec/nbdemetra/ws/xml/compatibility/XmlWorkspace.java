/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ws.xml.compatibility;

import ec.nbdemetra.ws.*;
import ec.satoolkit.GenericSaProcessingFactory;
import ec.satoolkit.algorithm.implementation.TramoSeatsProcessingFactory;
import ec.satoolkit.algorithm.implementation.X13ProcessingFactory;
import ec.tstoolkit.modelling.arima.Method;
import ec.tstoolkit.utilities.Id;
import ec.tstoolkit.utilities.LinearId;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Kristof Bayens
 */
@XmlRootElement(name = XmlWorkspace.RNAME)
@XmlType(name = XmlWorkspace.NAME)
public class XmlWorkspace {

    static final String NAME = "demetraWorkspaceType";
    static final String RNAME = "demetraWorkspace";
    public static final Id TRAMOSEATSSPEC = new LinearId(TramoSeatsProcessingFactory.DESCRIPTOR.family, WorkspaceFactory.SPECIFICATIONS, TramoSeatsProcessingFactory.DESCRIPTOR.name);
    public static final Id TRAMOSEATSDOC = new LinearId(TramoSeatsProcessingFactory.DESCRIPTOR.family, WorkspaceFactory.DOCUMENTS, TramoSeatsProcessingFactory.DESCRIPTOR.name);
    public static final Id X13SPEC = new LinearId(X13ProcessingFactory.DESCRIPTOR.family, WorkspaceFactory.SPECIFICATIONS, X13ProcessingFactory.DESCRIPTOR.name);
    public static final Id X13DOC = new LinearId(X13ProcessingFactory.DESCRIPTOR.family, WorkspaceFactory.DOCUMENTS, X13ProcessingFactory.DESCRIPTOR.name);
    public static final Id SAPROCESSING = new LinearId(GenericSaProcessingFactory.FAMILY, WorkspaceFactory.MULTIDOCUMENTS);
    @XmlElementWrapper()
    @XmlElement(name = "tramoseatsSpec")
    public XmlWksElement[] tramoseatsSpecs;
    @XmlElementWrapper()
    @XmlElement(name = "x12Spec")
    public XmlWksElement[] x12Specs;
    @XmlElementWrapper()
    @XmlElement(name = "tramoseatsDoc")
    public XmlWksElement[] tramoseatsDocs;
    @XmlElementWrapper()
    @XmlElement(name = "x12Doc")
    public XmlWksElement[] x12Docs;
    @XmlElementWrapper()
    @XmlElement(name = "processing")
    public XmlWksElement[] saProcessing;
    @XmlElement
    public XmlWksElement calendars;
    @XmlElement
    public XmlWksElement variables;
    @XmlAttribute
    public String defaultSpec;
    @XmlAttribute
    public Method defaultMethod;

    public XmlWorkspace() {
    }

    public boolean load(Workspace ws) {

        //
        if (tramoseatsSpecs != null) {
            IWorkspaceItemManager mgr = WorkspaceFactory.getInstance().getManager(TRAMOSEATSSPEC);
            if (mgr != null) {
                for (int i = 0; i < tramoseatsSpecs.length; ++i) {
                    WorkspaceItem<?> item = WorkspaceItem.item(mgr.getId(), tramoseatsSpecs[i].name, tramoseatsSpecs[i].file);
                    ws.quietAdd(item);
                }
            }
        }
        if (x12Specs != null) {
            IWorkspaceItemManager mgr = WorkspaceFactory.getInstance().getManager(X13SPEC);
            if (mgr != null) {
                for (int i = 0; i < x12Specs.length; ++i) {
                    WorkspaceItem<?> item = WorkspaceItem.item(mgr.getId(), x12Specs[i].name, x12Specs[i].file);
                    ws.quietAdd(item);
                }
            }
        }
        if (tramoseatsDocs != null) {
            IWorkspaceItemManager mgr = WorkspaceFactory.getInstance().getManager(TRAMOSEATSDOC);
            if (mgr != null) {
                for (int i = 0; i < tramoseatsDocs.length; ++i) {
                    WorkspaceItem<?> item = WorkspaceItem.item(mgr.getId(), tramoseatsDocs[i].name, tramoseatsDocs[i].file);
                    ws.quietAdd(item);
                }
            }
        }
        if (x12Docs != null) {
            IWorkspaceItemManager mgr = WorkspaceFactory.getInstance().getManager(X13DOC);
            if (mgr != null) {
                for (int i = 0; i < x12Docs.length; ++i) {
                    WorkspaceItem<?> item = WorkspaceItem.item(mgr.getId(), x12Docs[i].name, x12Docs[i].file);
                    ws.quietAdd(item);
                }
            }
        }
        if (saProcessing != null) {
            IWorkspaceItemManager mgr = WorkspaceFactory.getInstance().getManager(SAPROCESSING);
            if (mgr != null) {
                for (int i = 0; i < saProcessing.length; ++i) {
                    WorkspaceItem<?> item = WorkspaceItem.item(mgr.getId(), saProcessing[i].name, saProcessing[i].file);
                    ws.quietAdd(item);
                }
            }
        }
        return true;
    }
//    Workspace create(String file) {
//        String[] name=Paths.splitFile(file);
//        Workspace ws = new Workspace(name[1]);
//        ws.setFile(file);
//        ws.setPath(name[0]);
//        if (variables != null && variables.name != null) {
////            WksVariables vars = new WksVariables(ws);
////            vars.setName(variables.name);
////            vars.setFile(variables.file);
////            ws.setWksVariables(vars);
//        }
//        if (calendars != null && calendars.name != null) {
////            WksCalendar cal = new WksCalendar(ws);
////            cal.setName(calendars.name);
////            cal.setFile(calendars.file);
////            ws.setWksCalendar(cal);
//        }
//        ws.loadContext();
//        if (tramoseatsSpecs != null) {
//            for (int i = 0; i < tramoseatsSpecs.length; ++i) {
//                WorkspaceItem<TramoSeatsSpecification> item=new 
//                        WorkspaceItem<TramoSeatsSpecification>(ws, TramoSeatsSpecManager.ID);
//                item.setName(tramoseatsSpecs[i].name);
//                item.setFile(tramoseatsSpecs[i].file);
//                 ws.add(item);
//            }
//        }
//        if (x12Specs != null) {
//            for (int i = 0; i < x12Specs.length; ++i) {
//                WorkspaceItem<X13Specification> item=new
//                        WorkspaceItem<X13Specification>(ws, X13SpecManager.ID);
//                item.setName(x12Specs[i].name);
//                item.setFile(x12Specs[i].file);
//                 ws.add(item);
//            }
//        }
//        if (x13Specs != null) {
//            for (int i = 0; i < x13Specs.length; ++i) {
//                WorkspaceItem<X13Specification> item=new
//                        WorkspaceItem<X13Specification>(ws, X13SpecManager.ID);
//                item.setName(x13Specs[i].name);
//                item.setFile(x13Specs[i].file);
//                 ws.add(item);
//            }
//        }
//        if (tramoseatsDocs != null) {
//            for (int i = 0; i < tramoseatsDocs.length; ++i) {
//                WorkspaceItem<TsDocument> item = new WorkspaceItem<TsDocument>(ws, TramoSeatsDocManager.ID);
//                item.setName(tramoseatsDocs[i].name);
//                item.setFile(tramoseatsDocs[i].file);
//                ws.add(item);
//            }
//        }
//        if (x12Docs != null) {
//            for (int i = 0; i < x12Docs.length; ++i) {
//                  WorkspaceItem<TsDocument> item=new
//                        WorkspaceItem<TsDocument>(ws, X13DocManager.ID);
//                item.setName(x12Docs[i].name);
//                item.setFile(x12Docs[i].file);
//                 ws.add(item);
//            }
//        }
//        if (x13Docs != null) {
//            for (int i = 0; i < x13Docs.length; ++i) {
//                  WorkspaceItem<TsDocument> item=new
//                        WorkspaceItem<TsDocument>(ws, X13DocManager.ID);
//                item.setName(x13Docs[i].name);
//                item.setFile(x13Docs[i].file);
//                 ws.add(item);
//            }
//        }
//        if (saProcessing != null) {
//            for (int i = 0; i < saProcessing.length; ++i) {
//                   WorkspaceItem<SaProcessing> item=new
//                        WorkspaceItem<SaProcessing>(ws, SaProcessingManager.ID);
//                item.setName(saProcessing[i].name);
//                item.setFile(saProcessing[i].file);
//                 ws.add(item);
//            }
//        }
//
////        if (defaultSpec != null) {
////            if (defaultMethod == Method.Tramo) {
////                WksTramoSeatsSpec spec = ws.tramoSeatsSpecifications().search(defaultSpec);
////                if (spec != null)
////                    ws.setDefaultSpec(spec);
////            }
////            if (defaultMethod == Method.Regarima) {
////                WksX13Spec spec = ws.x13Specifications().search(defaultSpec);
////                if (spec != null)
////                    ws.setDefaultSpec(spec);
////            }
////        }
//        return ws;
//    }
}
