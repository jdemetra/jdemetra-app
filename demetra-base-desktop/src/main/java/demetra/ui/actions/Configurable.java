package demetra.ui.actions;

import demetra.ui.ConfigEditor;
import demetra.ui.Persistable;
import ec.util.various.swing.JCommand;
import javax.swing.ActionMap;

public interface Configurable {

    void configure();

    static void configure(Persistable persistable, ConfigEditor editor) {
        persistable.setConfig(editor.editConfig(persistable.getConfig()));
    }
    
    static final String CONFIGURE_ACTION = "configure";
    
    static void registerActions(Configurable configurable, ActionMap am) {
        am.put(Configurable.CONFIGURE_ACTION, ConfigureCommand.INSTANCE.toAction(configurable));
    }

    static final class ConfigureCommand extends JCommand<Configurable> {

        public static final ConfigureCommand INSTANCE = new ConfigureCommand();

        @Override
        public void execute(Configurable c) throws Exception {
            c.configure();
        }
    }
}
