/*
 * Copyright 2015 National Bank of Belgium
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
package ec.util.completion.swing;

import ec.util.completion.AbstractAutoCompletion;
import ec.util.completion.ExtAutoCompletionSource;
import ec.util.completion.ExtAutoCompletionSource.Request;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nonnull;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

@SuppressWarnings("serial")
public class JAutoCompletion extends AbstractAutoCompletion<JComponent> {

    protected static final String HIDE_ACTION = "HIDE";
    protected static final String INSERT_CURRENT_ACTION = "INSERT_CURRENT";
    protected static final String SELECT_PREV_ACTION = "SELECT_PREV";
    protected static final String SELECT_NEXT_ACTION = "SELECT_NEXT";

    protected final InputView<JTextComponent> inputView;
    protected final SearchView<JList> searchView;
    protected final Timer timer;
    protected long latestId;

    public JAutoCompletion(@Nonnull JTextComponent input) {
        this(input, new JList());
    }

    public JAutoCompletion(@Nonnull JTextComponent input, @Nonnull JList list) {
        this.inputView = new JTextComponentInputView(input);
        this.searchView = new JListSearchView(list);
        this.timer = new Timer(delay, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                search();
            }
        });
        this.timer.setRepeats(false);
        this.latestId = 0;
        addPropertyChangeListener(evt -> {
            String p = evt.getPropertyName();
            if (p.equals(DELAY_PROPERTY)) {
                timer.setDelay(delay);
            }
        });
    }

    void fillMaps(JComponent c) {
        ActionMap am = c.getActionMap();
        am.put(HIDE_ACTION, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hide();
            }
        });
        am.put(INSERT_CURRENT_ACTION, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertCurrent();
            }
        });
        am.put(SELECT_PREV_ACTION, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectPrev();
            }
        });
        am.put(SELECT_NEXT_ACTION, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectNext();
            }
        });

        InputMap im = c.getInputMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), HIDE_ACTION);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), INSERT_CURRENT_ACTION);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), SELECT_PREV_ACTION);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), SELECT_NEXT_ACTION);
    }

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    @Nonnull
    public JList getList() {
        return searchView.getComponent();
    }
    //</editor-fold>

    @Override
    public void close() {
        searchView.onClose();
        timer.stop();
    }

    @Override
    public void search(String term) {
        if (!enabled || term.length() < minLength) {
            close();
            return;
        }
        search(getRequest(term));
    }

    private Request getRequest(String term) {
        return source instanceof ExtAutoCompletionSource
                ? ((ExtAutoCompletionSource) source).getRequest(term)
                : ExtAutoCompletionSource.wrap(source, term);
    }

    private void search(final ExtAutoCompletionSource.Request request) {
        switch (request.getBehavior()) {
            case ASYNC:
                new SwingWorker<List<?>, String>() {
                    final long id = ++latestId;

                    @Override
                    protected List<?> doInBackground() throws Exception {
                        publish("STARTED");
                        return request.call();
                    }

                    @Override
                    protected void process(List<String> chunks) {
                        // should be called once
                        searchView.onSearchStarted(request.getTerm());
                    }

                    @Override
                    protected void done() {
                        if (!inputView.isEditing() || id < latestId) {
                            return;
                        }
                        try {
                            searchView.onSearchDone(request.getTerm(), get());
                        } catch (InterruptedException | ExecutionException ex) {
                            searchView.onSearchFailed(request.getTerm(), ex);
                        }
                    }
                }.execute();
                break;
            case SYNC:
                if (!inputView.isEditing()) {
                    return;
                }
                try {
                    searchView.onSearchDone(request.getTerm(), request.call());
                } catch (Exception ex) {
                    searchView.onSearchFailed(request.getTerm(), ex);
                }
                break;
            case NONE:
                break;
        }
    }

    @Override
    protected InputView<JTextComponent> getInputView() {
        return inputView;
    }

    @Override
    protected SearchView<JList> getSearchView() {
        return searchView;
    }

    private final class JTextComponentInputView implements InputView<JTextComponent> {

        private final JTextComponent input;

        public JTextComponentInputView(JTextComponent input) {
            this.input = input;
            this.input.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void removeUpdate(DocumentEvent e) {
                    fireDocumentEvent(e);
                }

                @Override
                public void insertUpdate(DocumentEvent e) {
                    fireDocumentEvent(e);
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                }
            });
            this.input.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    close();
                }
            });
            this.input.addAncestorListener(new AncestorListener() {
                @Override
                public void ancestorAdded(AncestorEvent event) {
                    close();
                }

                @Override
                public void ancestorRemoved(AncestorEvent event) {
                    close();
                }

                @Override
                public void ancestorMoved(AncestorEvent event) {
                    close();
                }
            });
            fillMaps(this.input);
        }

        @Override
        public boolean isEditing() {
            return input.hasFocus();
        }

        @Override
        public void requestEditing() {
            input.requestFocusInWindow();
        }

        @Override
        public JTextComponent getComponent() {
            return input;
        }
        //
        String previous = "";
        int beginIdx = 0;
        int endIdx = 0;
        boolean listening = true;

        void fireDocumentEvent(DocumentEvent e) {
            if (listening) {
                SwingUtilities.invokeLater(() -> {
                    String current = getTerm();
                    if (!current.equals(previous)) {
                        previous = current;
                        timer.restart();
                    }
                });
            }
        }

        @Override
        public String getTerm() {
            String all = input.getText();
            if (separator.isEmpty()) {
                beginIdx = 0;
                endIdx = all.length();
                return all;
            }
            int pos = input.getSelectionStart();
            beginIdx = pos > 0 ? all.lastIndexOf(separator, pos - 1) : -1;
            beginIdx = beginIdx >= 0 ? beginIdx + separator.length() : 0;
            endIdx = all.indexOf(separator, pos);
            endIdx = endIdx >= 0 ? endIdx : all.length();
            return all.substring(beginIdx, endIdx);
        }

        @Override
        public void setTerm(String value) {
            String oldValue = input.getText();
            String newValue = oldValue.substring(0, beginIdx) + value + oldValue.substring(endIdx);
            listening = false;
            input.setText(newValue);
            input.setSelectionStart(beginIdx + value.length());
            input.setSelectionEnd(beginIdx + value.length());
            listening = true;
        }
    }

    private final class JListSearchView implements SearchView<JList> {

        private final JList list;
        private final CustomListModel model;
        private final JScrollPane scrollPane;
        private final JLabel message;
        private final XPopup popup;

        JListSearchView(JList view) {
            this.list = view;
            this.model = new CustomListModel();
            this.scrollPane = new JScrollPane(this.list);
            this.message = new JLabel();
            this.popup = new XPopup();

            this.message.setOpaque(true);
            this.message.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Object ex = message.getClientProperty("Exception");
                    if (ex instanceof Exception) {
                        onExceptionDetails((Exception) ex);
                    }
                }
            });

            this.list.setFocusable(false);
            this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            this.list.setCellRenderer(new CustomListCellRenderer(true));
            this.list.setModel(model);
            this.list.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() > 1) {
                        insertCurrent();
                    }
                }
            });
            fillMaps(this.list);
        }

        private void onExceptionDetails(Exception ex) {
            StringWriter errors = new StringWriter();
            ((Exception) ex).printStackTrace(new PrintWriter(errors));
            JComponent component = new JScrollPane(new JTextArea(errors.toString()));
            component.setPreferredSize(new Dimension(500, 300));
            JOptionPane.showMessageDialog(message, component, "Exception details", JOptionPane.ERROR_MESSAGE);
        }

        @Override
        public JList getComponent() {
            return list;
        }

        @Override
        public void onClose() {
            hidePopup();
        }

        @Override
        public void onSearchStarted(String term) {
            hidePopup();
            message.setText("<html><i>Searching ...");
            message.setToolTipText("");
            message.putClientProperty("Exception", null);
            showPopup(message);
        }

        @Override
        public void onSearchFailed(String term, Exception ex) {
            hidePopup();
            message.setText("<html><i>Error (click for details)");
            message.setToolTipText(ex.getMessage());
            message.putClientProperty("Exception", ex);
            showPopup(message);
        }

        @Override
        public void onSearchDone(String term, List<?> values) {
            // force resize
            hidePopup();
            // set data
            model.setData(term, values);
            // check if there is work to do
            if (values.isEmpty()) {
                return;
            }
            // compute size
            scrollPane.setPreferredSize(computeSize());
            // set selection
            if (autoFocus) {
                list.setSelectedIndex(0);
            } else {
                list.clearSelection();
            }
            list.ensureIndexIsVisible(0);
            showPopup(scrollPane);
        }

        Dimension computeSize() {
            Dimension size = list.getPreferredSize();
            JTextComponent textComponent = inputView.getComponent();
            Insets popupInsets = scrollPane.getInsets();

            int width = size.width + popupInsets.left + popupInsets.right;
            int minWidth = textComponent.getWidth();
            boolean horizontalScrollBarVisible = false;

            if (width <= minWidth) {
                width = minWidth;
            } else {
                Window window = SwingUtilities.getWindowAncestor(textComponent);
                int x = textComponent.getLocationOnScreen().x - window.getLocationOnScreen().x;
                int maxWidth = window.getWidth() - x - window.getInsets().right;
                if (width > maxWidth) {
                    width = maxWidth;
                    horizontalScrollBarVisible = true;
                }
            }

            int height = size.height + popupInsets.top + popupInsets.bottom
                    + (horizontalScrollBarVisible ? getHorizontalScrollBarHeight() : 0);
            int maxHeight = textComponent.getHeight() * 7;

            if (height > maxHeight) {
                height = maxHeight;
            }

            return new Dimension(width, height);
        }

        int getHorizontalScrollBarHeight() {
            int result = scrollPane.getHorizontalScrollBar().getHeight();
            return result != 0 ? result : UIManager.getInt("ScrollBar.width");
        }

        void showPopup(Component c) {
            popup.show(inputView.getComponent(), c, XPopup.Anchor.BOTTOM_LEADING, new Dimension());
        }

        void hidePopup() {
            list.clearSelection();
            popup.hide();
        }

        @Override
        public void moveSelection(int step, boolean page) {
            int idx = list.getSelectedIndex() + step;
            if (0 <= idx && idx < model.getSize()) {
                list.setSelectedIndex(idx);
                list.ensureIndexIsVisible(idx);
            }
        }

        @Override
        public Object getSelectedValue() {
            return list.getSelectedValue();
        }
    }
}
