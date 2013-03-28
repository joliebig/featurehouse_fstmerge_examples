package net.sourceforge.squirrel_sql.plugins.editextras;


import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;


class RemoveNewLinesCommand implements ICommand {
    private final ISQLPanelAPI _api;

    RemoveNewLinesCommand(ISQLPanelAPI api) {
        super();
        _api = api;
    }

    public void execute() throws BaseException {
        int[] bounds = _api.getSQLEntryPanel().getBoundsOfSQLToBeExecuted();

        if (bounds[0] == bounds[1]) {
            return;
        }

        String textToChange = _api.getSQLEntryPanel().getSQLToBeExecuted();

        if (null == textToChange) {
            return;
        }

        String[] parts = textToChange.split("\n");
        String newText = StringUtilities.join(parts, null);

        _api.getSQLEntryPanel().setSelectionStart(bounds[0]);
        _api.getSQLEntryPanel().setSelectionEnd(bounds[1]);
        _api.getSQLEntryPanel().replaceSelection(newText);
    }
}
