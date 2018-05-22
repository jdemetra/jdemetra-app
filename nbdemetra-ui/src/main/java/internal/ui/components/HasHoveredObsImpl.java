/*
 * Copyright 2018 National Bank of Belgium
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
package internal.ui.components;

import demetra.ui.beans.PropertyChangeSource;
import demetra.ui.components.HasHoveredObs;
import ec.util.chart.ObsIndex;

/**
 *
 * @author Philippe Charles
 */
@lombok.RequiredArgsConstructor
public final class HasHoveredObsImpl implements HasHoveredObs {

    @lombok.NonNull
    private final PropertyChangeSource.Broadcaster broadcaster;

    private ObsIndex hoveredObs = DEFAULT_HOVERED_OBS;

    @Override
    public ObsIndex getHoveredObs() {
        return hoveredObs;
    }

    @Override
    public void setHoveredObs(ObsIndex hoveredObs) {
        ObsIndex old = this.hoveredObs;
        this.hoveredObs = hoveredObs != null ? hoveredObs : DEFAULT_HOVERED_OBS;
        broadcaster.firePropertyChange(HOVERED_OBS_PROPERTY, old, this.hoveredObs);
    }

    private static final ObsIndex DEFAULT_HOVERED_OBS = ObsIndex.NULL;
}
