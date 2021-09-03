package demetra.desktop.actions;

import demetra.desktop.ConfigEditor;
import demetra.desktop.Persistable;
import demetra.desktop.design.SwingAction;
import ec.util.various.swing.JCommand;

import javax.swing.*;

public interface Configurable {

    @SwingAction
    String CONFIGURE_ACTION = "configure";

    void configure();

    static void configure(Persistable persistable, ConfigEditor editor) {
        persistable.setConfig(editor.editConfig(persistable.getConfig()));
    }

    static void registerActions(Configurable configurable, ActionMap am) {
        am.put(Configurable.CONFIGURE_ACTION, ConfigureCommand.INSTANCE.toAction(configurable));
    }

    final class ConfigureCommand extends JCommand<Configurable> {

        public static final ConfigureCommand INSTANCE = new ConfigureCommand();

        @Override
        public void execute(Configurable c) throws Exception {
            c.configure();
        }
    }
}
