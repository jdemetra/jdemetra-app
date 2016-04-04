/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.sa;

import ec.satoolkit.ISaSpecification;
import ec.tss.sa.EstimationPolicyType;
import ec.tss.sa.SaProcessing;
import ec.tstoolkit.utilities.IModifiable;

/**
 *
 * @author Jean Palate
 */
public class MultiProcessingDocument implements IModifiable {

    private SaProcessing current_;
    private SaProcessing initial_;
    private ISaSpecification defSpec_;

    private MultiProcessingDocument() {
        defSpec_ = MultiProcessingManager.getDefaultSpecification();
    }

    public SaProcessing getCurrent() {
        return current_ == null ? initial_ : current_;
    }

    public SaProcessing getInitial() {
        return initial_;
    }

    public ISaSpecification getDefaultSpecification() {
        return defSpec_;
    }

    public void setDefaultSpecification(ISaSpecification spec) {
        defSpec_ = spec;
    }

    public static MultiProcessingDocument createNew() {
        MultiProcessingDocument doc = new MultiProcessingDocument();
        doc.current_ = new SaProcessing();
        return doc;
    }

    public static MultiProcessingDocument open(SaProcessing initial) {
        MultiProcessingDocument doc = new MultiProcessingDocument();
        doc.initial_ = initial;
        return doc;
    }

    public boolean isNew() {
        return initial_ == null;
    }

    public void refresh(EstimationPolicyType policy, boolean nospan) {
        if (initial_ == null) {
            return;
        }
        SaProcessing nprocessing = initial_.makeCopy();
        nprocessing.refresh(policy, nospan);
        current_ = nprocessing;
    }

    @Override
    public boolean isDirty() {
        return getCurrent().isDirty();
    }

    @Override
    public void resetDirty() {
        getCurrent().resetDirty();
    }
}
