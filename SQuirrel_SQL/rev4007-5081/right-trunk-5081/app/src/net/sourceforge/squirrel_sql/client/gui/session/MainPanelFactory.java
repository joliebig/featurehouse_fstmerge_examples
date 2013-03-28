
package net.sourceforge.squirrel_sql.client.gui.session;

import net.sourceforge.squirrel_sql.client.session.ISession;

public class MainPanelFactory implements IMainPanelFactory
{

	public MainPanel createMainPanel(ISession session) {
		return new MainPanel(session);
	}
}
