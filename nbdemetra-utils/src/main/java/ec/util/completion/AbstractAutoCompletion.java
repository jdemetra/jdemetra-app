package ec.util.completion;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

public abstract class AbstractAutoCompletion<C> {

    // PROPERTIES DEFINITIONS
    public static final String AUTO_FOCUS_PROPERTY = "autoFocus";
    public static final String DELAY_PROPERTY = "delay";
    public static final String ENABLED_PROPERTY = "enabled";
    public static final String MIN_LENGTH_PROPERTY = "minLength";
    public static final String SOURCE_PROPERTY = "source";
    public static final String SEPARATOR_PROPERTY = "separator";
    // PROPERTIES DEFAULT VALUES
    protected final boolean DEFAULT_AUTO_FOCUS = false;
    protected final int DEFAULT_DELAY = 300;
    protected final boolean DEFAULT_ENABLED = true;
    protected final int DEFAULT_MIN_LENGTH = 1;
    protected final AutoCompletionSource DEFAULT_SOURCE = AutoCompletionSources.empty();
    protected final String DEFAULT_SEPARATOR = "";
    // PROPERTIES
    protected boolean autoFocus = DEFAULT_AUTO_FOCUS;
    protected int delay = DEFAULT_DELAY;
    protected boolean enabled = DEFAULT_ENABLED;
    protected int minLength = DEFAULT_MIN_LENGTH;
    protected AutoCompletionSource source = DEFAULT_SOURCE;
    protected String separator = DEFAULT_SEPARATOR;
    // OTHER
    protected final PropertyChangeSupport support = new PropertyChangeSupport(this);

    // GETTERS/SETTERS >
    public boolean isAutoFocus() {
        return autoFocus;
    }

    public void setAutoFocus(boolean autoFocus) {
        boolean old = this.autoFocus;
        this.autoFocus = autoFocus;
        firePropertyChange(AUTO_FOCUS_PROPERTY, old, this.autoFocus);
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        int old = this.delay;
        this.delay = delay >= 0 ? delay : DEFAULT_DELAY;
        firePropertyChange(DELAY_PROPERTY, old, this.delay);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        boolean old = this.enabled;
        this.enabled = enabled;
        firePropertyChange(ENABLED_PROPERTY, old, this.enabled);
    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        int old = this.minLength;
        this.minLength = minLength >= 0 ? minLength : DEFAULT_MIN_LENGTH;
        firePropertyChange(MIN_LENGTH_PROPERTY, old, this.minLength);
    }

    public AutoCompletionSource getSource() {
        return source;
    }

    public void setSource(AutoCompletionSource source) {
        AutoCompletionSource old = this.source;
        this.source = source != null ? source : DEFAULT_SOURCE;
        firePropertyChange(SOURCE_PROPERTY, old, this.source);
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        String old = this.separator;
        this.separator = separator != null ? separator : DEFAULT_SEPARATOR;
        firePropertyChange(SEPARATOR_PROPERTY, old, this.separator);
    }
    // < GETTERS/SETTERS

    abstract public void close();

    abstract public void search(String term);

    abstract protected InputView<? extends C> getInputView();

    abstract protected SearchView<? extends C> getSearchView();

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }

    protected <P> void firePropertyChange(String name, P oldValue, P newValue) {
        support.firePropertyChange(name, oldValue, newValue);
    }

    protected void hide() {
        getInputView().requestEditing();
        close();
    }

    protected void insertCurrent() {
        Object selectedValue = getSearchView().getSelectedValue();
        if (selectedValue != null) {
            String str = source.toString(selectedValue);
            getInputView().setTerm(str);
            getInputView().requestEditing();
            close();
        }
    }

    protected void selectPrev() {
        getSearchView().moveSelection(-1, false);
    }

    protected void selectNext() {
        getSearchView().moveSelection(1, false);
    }

    protected void search() {
        search(getInputView().getTerm());
    }

    public interface InputView<C> {
        
        boolean isEditing();

        void requestEditing();

        String getTerm();

        void setTerm(String term);

        C getComponent();
    }

    public interface SearchView<C> {

        void onClose();

        void onSearchStarted(String term);

        void onSearchFailed(String term, Exception ex);

        void onSearchDone(String term, List<?> values);

        void moveSelection(int steps, boolean page);

        Object getSelectedValue();

        C getComponent();
    }
}
