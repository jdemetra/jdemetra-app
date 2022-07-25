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
package demetra.desktop.benchmarking;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//ec.nbdemetra.benchmarking//MultiCholette//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "MultiCholetteTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
//@ActionID(category = "Window", id = "ec.nbdemetra.benchmarking.MultiCholetteTopComponent")
//@ActionReference(path = "Menu/Statistical methods/Benchmarking", position = 3000, separatorBefore = 2999)
//@TopComponent.OpenActionRegistration(
//        displayName = "#CTL_MultiCholetteAction")
@Messages({
    "CTL_MultiCholetteAction=Multi-variate Cholette",
    "CTL_MultiCholetteTopComponent=Multi-variate Cholette Window",
    "HINT_MultiCholetteTopComponent=This is a Multi-variate Cholette window"
})
public final class MultiCholetteTopComponent extends WorkspaceTopComponent<MultiCholetteDocument> {

    private Node node;
    private JToolBar toolBarRepresentation;
    private JSplitPane visualRepresentation;
    // toolBar stuff
    private JButton runButton;
    private JTsVariableList inputList;
    private TsVariables vars;
    private MultiCholetteDocumentView mcView;

    public MultiCholetteTopComponent() {
        super(null);
        //initDocument();
    }

    public MultiCholetteTopComponent(WorkspaceItem<MultiCholetteDocument> doc) {
        super(doc);
        initDocument();
    }

    private void start() {

        TsVariables dvars = new TsVariables("s", new DefaultNameValidator("+-*=.;"));
        for (String s : vars.getNames()) {
            dvars.set(s, vars.get(s));
        }
        getDocument().getElement().setInput(dvars);
        mcView.refresh();
    }

    public void initDocument() {
        setName(getDocument().getDisplayName());
        setToolTipText(Bundle.CTL_MultiCholetteTopComponent());
        initComponents();
        node = new InternalNode();
        toolBarRepresentation = NbComponents.newInnerToolbar();
        runButton = toolBarRepresentation.add(new AbstractAction("", DemetraUiIcon.COMPILE_16) {
            @Override
            public void actionPerformed(ActionEvent e) {
                start();
            }
        });
        toolBarRepresentation.setFloatable(false);
        toolBarRepresentation.addSeparator();
        toolBarRepresentation.add(Box.createRigidArea(new Dimension(5, 0)));
        runButton.setDisabledIcon(ImageUtilities.createDisabledIcon(runButton.getIcon()));
        toolBarRepresentation.add(Box.createHorizontalGlue());
        toolBarRepresentation.addSeparator();
        vars = new TsVariables("s", new DefaultNameValidator("+-*=.;"));

        if (getDocument() != null) {
            TsVariables dvars = getDocument().getElement().getInput();
            if (dvars != null) {
                for (String s : dvars.getNames()) {
                    vars.set(s, dvars.get(s));
                }
            }
        }
        inputList = new JTsVariableList(vars);
        initList();
        mcView = new MultiCholetteDocumentView();
        mcView.setDocument(getDocument().getElement());
        visualRepresentation = NbComponents.newJSplitPane(JSplitPane.VERTICAL_SPLIT, inputList, mcView);
        visualRepresentation.setOneTouchExpandable(true);

        setLayout(new BorderLayout());
        add(toolBarRepresentation, BorderLayout.NORTH);
        add(visualRepresentation, BorderLayout.CENTER);
    }

    private void initList() {
        inputList.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(JTsVariableList.CLEAR_ACTION)
                        || evt.getPropertyName().equals(JTsVariableList.DELETE_ACTION)
                        || evt.getPropertyName().equals(JTsVariableList.RENAME_ACTION)) {
                    clear();
                }
            }
        });
    }

    private MultiCholetteSpecification getSpecification() {
        return getDocument().getElement().getSpecification();
    }

    private void setSpecification(MultiCholetteSpecification spec) {
        getDocument().getElement().setSpecification(spec);
        clear();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.LINE_AXIS));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        super.componentOpened();
        if (visualRepresentation != null) {
            visualRepresentation.setDividerLocation(200);
        }
    }

    @Override
    public void componentClosed() {
        super.componentClosed();
        if (getDocument() != null && getDocument().getElement().getInput() == null) {
            getDocument().getElement().setInput(vars);
        }
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    protected String getContextPath() {
        return MultiCholetteDocumentManager.CONTEXTPATH; //To change body of generated methods, choose Tools | Templates.
    }

    private void clear() {
        getDocument().getElement().setInput(null);
        mcView.refresh();
    }

    @Override
    public Node getNode() {
        return node;
    }

    class InternalNode extends AbstractNode {

        InternalNode() {
            super(Children.LEAF);
            setDisplayName("Multi-variate Cholette method");
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = super.createSheet();
            Sheet.Set benchmarking = Sheet.createPropertiesSet();
            benchmarking.setName("Benchmarking");
            benchmarking.setDisplayName("Benchmarking");
            Node.Property<Double> rho = new Node.Property(Double.class) {
                @Override
                public boolean canRead() {
                    return true;
                }

                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    MultiCholetteSpecification spec = getSpecification();
                    return spec.getParameters().getRho();
                }

                @Override
                public boolean canWrite() {
                    return true;
                }

                @Override
                public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    MultiCholetteDocument document = getDocument().getElement();
                    MultiCholetteSpecification nspec = document.getSpecification().clone();
                    nspec.getParameters().setRho((Double) t);
                    document.setSpecification(nspec);
                    clear();
                }
            };
            rho.setName("Rho");
            benchmarking.put(rho);
            Node.Property<Double> lambda = new Node.Property(Double.class) {
                @Override
                public boolean canRead() {
                    return true;
                }

                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    MultiCholetteSpecification spec = getSpecification();
                    return spec.getParameters().getLambda();
                }

                @Override
                public boolean canWrite() {
                    return true;
                }

                @Override
                public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    MultiCholetteDocument document = getDocument().getElement();
                    MultiCholetteSpecification nspec = document.getSpecification().clone();
                    nspec.getParameters().setLambda((Double) t);
                    setSpecification(nspec);
                    clear();
                }
            };
            lambda.setName("Lambda");
            benchmarking.put(lambda);
            sheet.put(benchmarking);

            Sheet.Set model = Sheet.createPropertiesSet();
            model.setName("Model");
            model.setDisplayName("Model");
            Node.Property<String[]> cnts = new Node.Property(String[].class) {
                @Override
                public boolean canRead() {
                    return true;
                }

                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    MultiCholetteSpecification spec = getSpecification();
                    List<String> cnt = spec.getConstraints();
                    return cnt.toArray(new String[cnt.size()]);
                }

                @Override
                public boolean canWrite() {
                    return true;
                }

                @Override
                public void setValue(Object t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                    MultiCholetteDocument document = getDocument().getElement();
                    MultiCholetteSpecification nspec = document.getSpecification().clone();
                    nspec.clearConstraints();
                    String[] cnt = (String[]) t;
                    if (cnt != null) {
                        for (int i = 0; i < cnt.length; ++i) {
                            nspec.addConstraint(cnt[i]);
                        }
                    }
                    document.setSpecification(nspec);
                    clear();
                }
            };
            cnts.setName("Constraints");
            model.put(cnts);

            sheet.put(model);
            return sheet;
        }
    }
}

class MultiCholetteDocumentView extends AbstractDocumentViewer<MultiCholetteDocument> {

    @Override
    protected IProcDocumentView<MultiCholetteDocument> getView(MultiCholetteDocument doc) {
        return MultiCholetteViewFactory.getDefault().create(doc);
    }
}
