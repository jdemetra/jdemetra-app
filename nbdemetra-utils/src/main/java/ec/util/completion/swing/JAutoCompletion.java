package ec.util.completion.swing;

import ec.util.completion.AbstractAutoCompletion;
import ec.util.completion.AutoCompletionSource;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
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
    //
    protected final InputView<JTextComponent> inputView;
    protected final SearchView<JList> searchView;
    protected final Timer timer;
    protected long latestId;

    public JAutoCompletion(JTextComponent input) {
        this(input, new JList());
    }

    public JAutoCompletion(JTextComponent input, JList list) {
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
        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String p = evt.getPropertyName();
                if (p.equals(DELAY_PROPERTY)) {
                    timer.setDelay(delay);
                }
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

    // GETTERS/SETTERS >
    public JList getList() {
        return searchView.getComponent();
    }
    // < GETTERS/SETTERS

    @Override
    public void close() {
        searchView.onClose();
        timer.stop();
    }

    @Override
    public void search(final String term) {
        if (!enabled || term.length() < minLength) {
            close();
            return;
        }
        switch (source.getBehavior(term)) {
            case ASYNC:
                new SwingWorker<List<?>, String>() {
                    final long id = ++latestId;
                    final AutoCompletionSource x = source;

                    @Override
                    protected List<?> doInBackground() throws Exception {
                        publish("STARTED");
                        return x.getValues(term);
                    }

                    @Override
                    protected void process(List<String> chunks) {
                        // should be called once
                        searchView.onSearchStarted(term);
                    }

                    @Override
                    protected void done() {
                        if (!inputView.isEditing() || id < latestId) {
                            return;
                        }
                        try {
                            searchView.onSearchDone(term, get());
                        } catch (InterruptedException | ExecutionException ex) {
                            searchView.onSearchFailed(term, ex);
                        }
                    }
                }.execute();
                break;
            case SYNC:
                if (!inputView.isEditing()) {
                    return;
                }
                try {
                    searchView.onSearchDone(term, source.getValues(term));
                } catch (Exception ex) {
                    searchView.onSearchFailed(term, ex);
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

    private class JTextComponentInputView implements InputView<JTextComponent> {

        final JTextComponent input;

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
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        String current = getTerm();
                        if (!current.equals(previous)) {
                            previous = current;
                            timer.restart();
                        }
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

    private class JListSearchView implements SearchView<JList> {

        final JList list;
        final CustomListModel model;
        final JScrollPane scrollPane;
        final JLabel message;
        final XPopup popup;

        JListSearchView(JList view) {
            this.list = view;
            this.model = new CustomListModel();
            this.scrollPane = new JScrollPane(this.list);
            this.message = new JLabel();
            this.popup = new XPopup();

            this.message.setOpaque(true);

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
            showPopup(message);
        }

        @Override
        public void onSearchFailed(String term, Exception ex) {
            hidePopup();
            message.setText("<html><i>Error ...");
            message.setToolTipText(ex.getMessage());
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
            // FIXME: compute component size 
            list.revalidate();
            int width = Math.max(list.getPreferredSize().width + 10, inputView.getComponent().getWidth());
            int height = Math.min(list.getPreferredSize().height, inputView.getComponent().getHeight() * 7) + 4;
            scrollPane.setPreferredSize(new Dimension(width, height));
            scrollPane.revalidate();
            // set selection
            if (autoFocus) {
                list.setSelectedIndex(0);
            } else {
                list.clearSelection();
            }
            list.ensureIndexIsVisible(0);
            showPopup(scrollPane);
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
