/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.common;

import demetra.bridge.ToFileBean;
import demetra.desktop.tsproviders.AbstractDataSourceProviderBuddy;
import demetra.desktop.tsproviders.DataSourceProviderBuddy;
import ec.tss.tsproviders.common.uscb.UscbProvider;
import nbbrd.service.ServiceProvider;

import java.beans.IntrospectionException;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(DataSourceProviderBuddy.class)
public final class UscbProviderBuddy extends AbstractDataSourceProviderBuddy {

    @Override
    public String getProviderName() {
        return UscbProvider.SOURCE;
    }

    @Override
    public boolean editBean(String title, Object bean) throws IntrospectionException {
        return super.editBean(title, bean instanceof ToFileBean ? ((ToFileBean) bean).getDelegate() : bean);
    }
}
