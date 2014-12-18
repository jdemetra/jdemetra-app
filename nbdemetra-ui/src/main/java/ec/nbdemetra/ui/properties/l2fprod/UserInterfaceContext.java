package ec.nbdemetra.ui.properties.l2fprod;

import ec.tstoolkit.timeseries.simplets.TsDomain;

/**
 *
 * @author Demortier Jeremy
 */
public enum UserInterfaceContext {
  INSTANCE;

  private TsDomain domain_;

  public TsDomain getDomain() {
    return domain_;
  }

  public void setDomain(TsDomain domain) {
    this.domain_ = domain;
  }
}
