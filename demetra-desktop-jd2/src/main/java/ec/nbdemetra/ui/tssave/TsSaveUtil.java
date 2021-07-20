/*
 * Copyright 2017 National Bank of Belgium
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
package ec.nbdemetra.ui.tssave;

import ec.nbdemetra.ui.SingleFileExporter;
import java.io.File;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.openide.filesystems.FileChooserBuilder;

/**
 *
 * @author Philippe Charles
 */
@lombok.experimental.UtilityClass
public class TsSaveUtil {

    public void saveToFile(@NonNull FileChooserBuilder fileChooser, @NonNull Predicate<File> predicate, @NonNull Consumer<File> action) {
        File target = fileChooser.showSaveDialog();
        if (target != null && predicate.test(target)) {
            action.accept(target);
        }
    }

    @NonNull
    public FileChooserBuilder fileChooser(@NonNull Class type) {
        return new FileChooserBuilder(type).setSelectionApprover(SingleFileExporter.overwriteApprover());
    }
}
