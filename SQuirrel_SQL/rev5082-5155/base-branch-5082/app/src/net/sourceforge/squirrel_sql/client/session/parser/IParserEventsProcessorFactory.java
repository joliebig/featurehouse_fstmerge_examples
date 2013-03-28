package net.sourceforge.squirrel_sql.client.session.parser;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

public interface IParserEventsProcessorFactory
{
   
   IParserEventsProcessor getParserEventsProcessor(IIdentifier sqlEntryPanelIdentifier, ISession sess);
}
