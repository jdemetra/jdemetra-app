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
package ec.util.desktop;

import ec.util.completion.swing.FileListCellRenderer;
import ec.util.various.swing.BasicSwingLauncher;
import ec.util.various.swing.FontAwesome;
import ec.util.various.swing.StandardSwingColor;
import ec.util.various.swing.TextPrompt;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Philippe Charles
 */
public final class DesktopDemo extends javax.swing.JPanel {

    public static void main(String[] args) {
        new BasicSwingLauncher()
                .content(DesktopDemo.class)
                .title("Desktop Demo")
                .size(520, 400)
                .resizable(false)
                .icons(() -> FontAwesome.FA_DESKTOP.getImages(Color.BLACK, 16f, 32f, 64f))
                .launch();
    }

    private final Desktop desktop;
    private File sample;
    private final JFileChooser fileChooser = new JFileChooser();

    /**
     * Creates new form DesktopDemo
     */
    public DesktopDemo() {
        initComponents();

        this.desktop = DesktopManager.get();

        List<Entry<Desktop.KnownFolder, File>> data = new ArrayList<>();
        for (Desktop.KnownFolder o : Desktop.KnownFolder.values()) {
            data.add(new AbstractMap.SimpleEntry<>(o, desktop.getKnownFolder(o)));
        }
        jList1.setListData(data.toArray());
        jList1.setCellRenderer(new KnownFolderRenderer());

        try {
            sample = File.createTempFile("test", ".txt");
            sample.deleteOnExit();
        } catch (IOException ex) {
            reportException(ex);
        }
        if (desktop.isSupported(Desktop.Action.SHOW_IN_FOLDER)) {
            jList1.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() > 1) {
                        int index = jList1.getSelectionModel().getMinSelectionIndex();
                        if (index != -1) {
                            File file = ((Entry<Desktop.KnownFolder, File>) jList1.getModel().getElementAt(index)).getValue();
                            if (file != null) {
                                try {
                                    desktop.showInFolder(file);
                                } catch (IOException ex) {
                                    reportException(ex);
                                }
                            }
                        }
                    }
                }
            });
        }

        openButton.setEnabled(desktop.isSupported(Desktop.Action.OPEN));
        editButton.setEnabled(desktop.isSupported(Desktop.Action.EDIT));
        printButton.setEnabled(desktop.isSupported(Desktop.Action.PRINT));
        mailButton.setEnabled(desktop.isSupported(Desktop.Action.MAIL));
        browseButton.setEnabled(desktop.isSupported(Desktop.Action.BROWSE));
        showInFolderButton.setEnabled(desktop.isSupported(Desktop.Action.SHOW_IN_FOLDER));
        moveToTrashButton.setEnabled(desktop.isSupported(Desktop.Action.MOVE_TO_TRASH));

        searchField.setEnabled(desktop.isSupported(Desktop.Action.SEARCH));
        new TextPrompt(searchField.isEnabled() ? "type enter to launch search" : "Not supported", searchField)
                .setForeground(StandardSwingColor.TEXT_FIELD_INACTIVE_FOREGROUND.value());

        searchResult.setCellRenderer(new FileListCellRenderer(Executors.newSingleThreadExecutor()));
        searchResult.setEnabled(desktop.isSupported(Desktop.Action.SEARCH));

        jTabbedPane1.setIconAt(0, FontAwesome.FA_FOLDER_OPEN.getIcon(jTabbedPane1.getForeground(), jTabbedPane1.getFont().getSize()));
        jTabbedPane1.setIconAt(1, FontAwesome.FA_CHECK_CIRCLE_O.getIcon(jTabbedPane1.getForeground(), jTabbedPane1.getFont().getSize()));
        jTabbedPane1.setIconAt(2, FontAwesome.FA_SEARCH.getIcon(jTabbedPane1.getForeground(), jTabbedPane1.getFont().getSize()));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jPanel1 = new javax.swing.JPanel();
        openButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        printButton = new javax.swing.JButton();
        mailButton = new javax.swing.JButton();
        browseButton = new javax.swing.JButton();
        showInFolderButton = new javax.swing.JButton();
        moveToTrashButton = new javax.swing.JButton();
        reportButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        searchField = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        searchResult = new javax.swing.JList();

        setLayout(new java.awt.BorderLayout());

        jScrollPane3.setViewportView(jList1);

        jTabbedPane1.addTab("Known Folders", jScrollPane3);

        openButton.setText("Open");
        openButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openButtonActionPerformed(evt);
            }
        });

        editButton.setText("Edit");
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        printButton.setText("Print");
        printButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printButtonActionPerformed(evt);
            }
        });

        mailButton.setText("Mail");
        mailButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mailButtonActionPerformed(evt);
            }
        });

        browseButton.setText("Browse");
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        showInFolderButton.setText("Show in folder");
        showInFolderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showInFolderButtonActionPerformed(evt);
            }
        });

        moveToTrashButton.setText("Move to trash");
        moveToTrashButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveToTrashButtonActionPerformed(evt);
            }
        });

        reportButton.setForeground(new java.awt.Color(0, 204, 102));
        reportButton.setText("Report");
        reportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reportButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Launches the associated application to open the file");

        jLabel2.setText("Launches the associated editor application and opens a file for editing");

        jLabel3.setText("Prints a file with the native desktop printing facility");

        jLabel4.setText("Launches the mail composing window of the user default mail client");

        jLabel5.setText("Launches the default browser to display a URI");

        jLabel6.setText("Launches the default file manager and select the specified folder, file or app");

        jLabel7.setText("Move the given files to the system trash");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(editButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(openButton, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(mailButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(printButton, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(browseButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(reportButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(moveToTrashButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(showInFolderButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(openButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editButton)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(printButton)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mailButton)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(browseButton)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(showInFolderButton)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(moveToTrashButton)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reportButton)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Actions", jPanel1);

        searchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchFieldActionPerformed(evt);
            }
        });

        jScrollPane2.setViewportView(searchResult);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
                    .addComponent(searchField))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(searchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Search", jPanel2);

        add(jTabbedPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openButtonActionPerformed
        try {
            desktop.open(sample);
        } catch (IOException ex) {
            reportException(ex);
        }
    }//GEN-LAST:event_openButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        try {
            desktop.edit(sample);
        } catch (IOException ex) {
            reportException(ex);
        }
    }//GEN-LAST:event_editButtonActionPerformed

    private void printButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printButtonActionPerformed
        try {
            desktop.print(sample);
        } catch (IOException ex) {
            reportException(ex);
        }
    }//GEN-LAST:event_printButtonActionPerformed

    private void mailButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mailButtonActionPerformed
        try {
            desktop.mail(new MailtoBuilder().subject("hello").body("world").build());
        } catch (IOException ex) {
            reportException(ex);
        }
    }//GEN-LAST:event_mailButtonActionPerformed

    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        try {
            desktop.browse(URI.create("http://www.google.com"));
        } catch (IOException ex) {
            reportException(ex);
        }
    }//GEN-LAST:event_browseButtonActionPerformed

    private void showInFolderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showInFolderButtonActionPerformed
        try {
            desktop.showInFolder(sample);
        } catch (IOException ex) {
            reportException(ex);
        }
    }//GEN-LAST:event_showInFolderButtonActionPerformed

    private void moveToTrashButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveToTrashButtonActionPerformed
        try {
            desktop.moveToTrash(File.createTempFile("test", ".txt"));
        } catch (IOException ex) {
            reportException(ex);
        }
    }//GEN-LAST:event_moveToTrashButtonActionPerformed

    private void reportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reportButtonActionPerformed
        File report = new File("DesktopReport_" + new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss").format(new Date()) + ".xml");
        fileChooser.setSelectedFile(report);
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileOutputStream stream = new FileOutputStream(fileChooser.getSelectedFile())) {
                DesktopReport.create(desktop, stream);
            } catch (Exception ex) {
                reportException(ex);
            }
        }
    }//GEN-LAST:event_reportButtonActionPerformed

    private void searchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchFieldActionPerformed
        searchResult.setListData(new Object[0]);
        if (!searchField.getText().isEmpty()) {
            searchField.setEnabled(false);
            final String query = searchField.getText();
            new SwingWorker<File[], Void>() {
                @Override
                protected File[] doInBackground() throws Exception {
                    return desktop.search(query);
                }

                @Override
                protected void done() {
                    try {
                        searchResult.setListData(get());
                    } catch (InterruptedException | ExecutionException ex) {
                        reportException(ex);
                    }
                    searchField.setEnabled(true);
                }
            }.execute();
        }
    }//GEN-LAST:event_searchFieldActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    private javax.swing.JButton editButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton mailButton;
    private javax.swing.JButton moveToTrashButton;
    private javax.swing.JButton openButton;
    private javax.swing.JButton printButton;
    private javax.swing.JButton reportButton;
    private javax.swing.JTextField searchField;
    private javax.swing.JList searchResult;
    private javax.swing.JButton showInFolderButton;
    // End of variables declaration//GEN-END:variables

    private static final class KnownFolderRenderer extends DefaultListCellRenderer {

        private final Border border = BorderFactory.createEmptyBorder(5, 5, 5, 5);

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            Entry<Desktop.KnownFolder, File> entry = (Entry<Desktop.KnownFolder, File>) value;
            setIcon(get(entry.getKey()).getIcon(getForeground(), getFont().getSize() * 2));
            setText("<html><b>" + entry.getKey().name() + "</b><br>" + entry.getValue());
            if (!isSelected && isInValidFile(entry.getValue())) {
                setForeground(Color.RED);
            }
            setIconTextGap(5);
            setBorder(border);
            return this;
        }

        private boolean isInValidFile(File file) {
            return file != null && (!file.isDirectory() || !file.exists());
        }

        private FontAwesome get(Desktop.KnownFolder knownFolder) {
            switch (knownFolder) {
                case DESKTOP:
                    return FontAwesome.FA_DESKTOP;
                case DOCUMENTS:
                    return FontAwesome.FA_FOLDER_OPEN;
                case DOWNLOAD:
                    return FontAwesome.FA_DOWNLOAD;
                case MUSIC:
                    return FontAwesome.FA_MUSIC;
                case PICTURES:
                    return FontAwesome.FA_PICTURE_O;
                case PUBLICSHARE:
                    return FontAwesome.FA_SHARE;
                case TEMPLATES:
                    return FontAwesome.FA_QUESTION;
                case VIDEOS:
                    return FontAwesome.FA_VIDEO_CAMERA;
                default:
                    return FontAwesome.FA_QUESTION;
            }
        }
    }

    @XmlRootElement(name = "desktopReport")
    public static class DesktopReport {

        public static void create(Desktop desktop, OutputStream outputStream) throws JAXBException {
            DesktopReport report = new DesktopReport();
            report.version = "1.0.0";
            report.date = new Date();
            report.impl = desktop.getClass().getName();
            report.osArch = System.getProperty("os.arch");
            report.osName = System.getProperty("os.name");
            report.osVersion = System.getProperty("os.version");
            report.javaVersion = System.getProperty("java.version");

            List<Test> tests = new ArrayList<>();

            for (Desktop.Action o : Desktop.Action.values()) {
                Test r = new Test();
                r.type = o.getClass().getSimpleName();
                r.name = o.name();
                if (!desktop.isSupported(o)) {
                    r.supportType = SupportType.NONE;
                } else {
                    r.supportType = SupportType.OK;
                }
                tests.add(r);
            }

            for (Desktop.KnownFolder o : Desktop.KnownFolder.values()) {
                Test r = new Test();
                r.type = o.getClass().getSimpleName();
                r.name = o.name();
                File folder = desktop.getKnownFolder(o);
                if (folder == null) {
                    r.supportType = SupportType.NONE;
                } else if (!folder.isDirectory()) {
                    r.supportType = SupportType.BAD;
                    r.details = "not a folder";
                } else if (!folder.exists()) {
                    r.supportType = SupportType.BAD;
                    r.details = "doesn't exist";
                } else {
                    r.supportType = SupportType.OK;
                }
                tests.add(r);
            }

            report.tests = tests.toArray(new Test[tests.size()]);

            DesktopReport.create(report, outputStream);
        }

        public static void create(DesktopReport report, OutputStream outputStream) throws JAXBException {
            JAXBContext context = JAXBContext.newInstance(DesktopReport.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(report, outputStream);
        }

        public enum SupportType {

            NONE, BAD, OK
        }
        //
        @XmlAttribute(name = "version")
        public String version;
        @XmlAttribute(name = "date")
        public Date date;
        @XmlAttribute(name = "impl")
        public String impl;
        @XmlAttribute(name = "osArch")
        public String osArch;
        @XmlAttribute(name = "osName")
        public String osName;
        @XmlAttribute(name = "osVersion")
        public String osVersion;
        @XmlAttribute(name = "javaVersion")
        public String javaVersion;
        @XmlElement(name = "test")
        public Test[] tests;

        public static class Test {

            @XmlAttribute(name = "type")
            public String type;
            @XmlAttribute(name = "name")
            public String name;
            @XmlAttribute(name = "support")
            public SupportType supportType;
            @XmlAttribute(name = "details")
            public String details;
        }
    }

    private void reportException(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
    }
}
