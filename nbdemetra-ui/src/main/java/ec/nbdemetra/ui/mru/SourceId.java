/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.mru;

import ec.tss.tsproviders.DataSource;

/**
 *
 * @author Philippe Charles
 */
public final class SourceId {

    final DataSource dataSource;
    final String label;

    public SourceId(DataSource dataSource, String label) {
        this.dataSource = dataSource;
        this.label = label;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public int hashCode() {
        return dataSource.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof SourceId && equals((SourceId) obj));
    }

    private boolean equals(SourceId that) {
        return this.dataSource.equals(that.dataSource);
    }
}
