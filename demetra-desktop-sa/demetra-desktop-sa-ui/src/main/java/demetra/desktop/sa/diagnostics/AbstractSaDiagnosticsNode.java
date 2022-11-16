/*
 * Copyright 2016 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
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
package demetra.desktop.sa.diagnostics;

import demetra.desktop.DemetraIcons;
import java.awt.Image;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;

/**
 * @author Jean Palate
 * @param <B>
 * @param <T> Configuration
 */
public abstract class AbstractSaDiagnosticsNode<B> extends AbstractNode {

    protected B bean;

    public AbstractSaDiagnosticsNode(B bean) {
        super(Children.LEAF);
        this.bean=bean;
    }

    @Override
    public Image getIcon(int type) {
        return DemetraIcons.DOCUMENT_PRINT_16.getImageIcon().getImage();
    }

}
