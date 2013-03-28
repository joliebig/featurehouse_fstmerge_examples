
package net.sourceforge.squirrel_sql.client.gui.session;

import net.sourceforge.squirrel_sql.client.session.ISession;

public interface IMainPanelFactory
{

	public abstract MainPanel createMainPanel(ISession session);

}