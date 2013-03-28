
package net.sourceforge.squirrel_sql.plugins.dbcopy.event;

import net.sourceforge.squirrel_sql.plugins.dbcopy.SessionInfoProvider;

public class AnalysisEvent extends AbstractCopyEvent {

    public AnalysisEvent(SessionInfoProvider prov) {
        super(prov);
    }
}
