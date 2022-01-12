package demetra.desktop.ui.properties.l2fprod;

import demetra.timeseries.TsDomain;


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
