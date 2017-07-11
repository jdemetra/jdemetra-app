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
package ec.nbdemetra.ui.ns;

import ec.nbdemetra.ui.DemetraUiIcon;
import java.awt.Image;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Philippe Charles
 */
public abstract class AbstractNamedService implements INamedService {

    protected final NamedServiceSupport support;

    protected AbstractNamedService(Class<? extends INamedService> service, String name) {
        this.support = new NamedServiceSupport(service, name);
    }

    @Override
    public String getName() {
        return support.getName();
    }

    @Override
    public Image getIcon(int type, boolean opened) {
        return ImageUtilities.icon2Image(DemetraUiIcon.PUZZLE_16);
    }
}
