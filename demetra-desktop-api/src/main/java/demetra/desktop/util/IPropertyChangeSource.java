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
package demetra.desktop.util;

import java.beans.PropertyChangeListener;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 *
 * @author Philippe Charles
 */
public interface IPropertyChangeSource {

    void addPropertyChangeListener(@NonNull String propertyName, @NonNull PropertyChangeListener listener);

    void addPropertyChangeListener(@NonNull PropertyChangeListener listener);

    void removePropertyChangeListener(@NonNull String propertyName, @NonNull PropertyChangeListener listener);

    void removePropertyChangeListener(@NonNull PropertyChangeListener listener);
   
}
