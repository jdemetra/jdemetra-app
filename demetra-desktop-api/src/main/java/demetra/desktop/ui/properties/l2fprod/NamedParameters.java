/*
 * Copyright 2017 National Bank of Belgium
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
package demetra.desktop.ui.properties.l2fprod;

import demetra.data.Parameter;
import demetra.util.NamedObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class NamedParameters {
    
    @Override
    public String toString(){
        return coefficients.isEmpty()? "" : "...";
    }

    private List<NamedObject<Parameter>> coefficients = new ArrayList<>();

    public NamedParameters addAll(Collection<NamedObject<Parameter>> coeff) {
        coefficients.addAll(coeff);
        return this;
    }

    public NamedParameters addAll(String[] names, Parameter[] p) {
        if (p != null && p.length != names.length) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < names.length; ++i) {
            Parameter c;
            if (p != null && p[i] != null) {
                c = p[i];
            } else {
                c = Parameter.undefined();
            }
            coefficients.add(new NamedObject<>(names[i], c));
        }
        return this;
    }

    public NamedParameters add(String name, Parameter p) {
        if (p == null) {
            p = Parameter.undefined();
        }
        coefficients.add(new NamedObject<>(name, p));
        return this;
    }

    public int size() {
        return coefficients.size();
    }

    public NamedObject<Parameter> get(int idx) {
        return coefficients.get(idx);
    }

    public void set(int idx, Parameter obj) {
        String name = coefficients.get(idx).getName();
        coefficients.set(idx, new NamedObject(name, obj));
    }

    public List<NamedObject<Parameter>> all() {
        return Collections.unmodifiableList(coefficients);
    }

    public Parameter[] parameters() {
        return coefficients.stream().map(np -> np.getObject()).toArray(Parameter[]::new);
    }

    public void set(Parameter[] p) {
        String[] names = coefficients.stream().map(v -> v.getName()).toArray(String[]::new);
        if (p == null) {
            for (int i = 0; i < names.length; ++i) {
                coefficients.set(i, new NamedObject<>(names[i], Parameter.undefined()));
            }
        }else{
             for (int i = 0; i < names.length; ++i) {
                coefficients.set(i, new NamedObject<>(names[i], p[i]));
            }
        }
    }

}
