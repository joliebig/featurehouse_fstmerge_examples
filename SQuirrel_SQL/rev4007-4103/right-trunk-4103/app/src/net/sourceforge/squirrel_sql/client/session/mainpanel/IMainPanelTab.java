package net.sourceforge.squirrel_sql.client.session.mainpanel;

import java.awt.Component;

import net.sourceforge.squirrel_sql.client.session.ISession;


public interface IMainPanelTab {
	
	String getTitle();

	
	String getHint();

	
	Component getComponent();

	
	void setSession(ISession session);

	
	void sessionClosing(ISession session);

	
	void select();
}

