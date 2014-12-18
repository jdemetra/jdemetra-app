package ec.util.completion.swing;

import java.util.Collections;
import java.util.List;
import javax.swing.AbstractListModel;

@SuppressWarnings("serial")
public class CustomListModel extends AbstractListModel {

    private String term = "";
    private List<?> data = Collections.emptyList();

    public void setData(String term, List<?> data) {
        this.term = term;
        this.data = data;
        fireContentsChanged(this, 0, data.size());
    }

    public String getTerm() {
        return term;
    }
    
    @Override
    public int getSize() {
        return data.size();
    }

    @Override
    public Object getElementAt(int index) {
        return data.get(index);
    }
}
