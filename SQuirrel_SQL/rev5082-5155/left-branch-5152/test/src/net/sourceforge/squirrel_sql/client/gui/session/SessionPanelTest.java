
package net.sourceforge.squirrel_sql.client.gui.session;


import static org.easymock.EasyMock.expect;

import java.awt.Font;

import javax.swing.ImageIcon;

import net.sourceforge.squirrel_sql.AbstractSerializableTest;
import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.builders.UIFactory;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.properties.SessionProperties;
import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;

public class SessionPanelTest extends AbstractSerializableTest
{
	
	ISession mockSession = mockHelper.createMock(ISession.class);
	IApplication mockApplication = mockHelper.createMock(IApplication.class);
	IIdentifier mockIdentifier = mockHelper.createMock(IIdentifier.class);
	SquirrelPreferences mockPreferences = mockHelper.createMock(SquirrelPreferences.class);
	SessionProperties mockSessionProperties = mockHelper.createMock(SessionProperties.class);
	FontInfo mockFontInfo = mockHelper.createMock(FontInfo.class);
	Font mockFont = mockHelper.createMock(Font.class);
	SquirrelResources mockSquirrelResources = mockHelper.createMock(SquirrelResources.class);
	ImageIcon mockImageIcon = mockHelper.createMock(ImageIcon.class);
	IMainPanelFactory mockMainPanelFactory = mockHelper.createMock(IMainPanelFactory.class);
	
	@Before
	public void setUp() throws Exception
	{
		
		expect(mockSession.getApplication()).andStubReturn(mockApplication);
		expect(mockSession.getIdentifier()).andStubReturn(mockIdentifier);
		expect(mockSession.getProperties()).andStubReturn(mockSessionProperties);
		
		
		expect(mockPreferences.getUseScrollableTabbedPanes()).andStubReturn(false);
		
		
		expect(mockSessionProperties.getFontInfo()).andStubReturn(mockFontInfo);
		
		
		expect(mockFontInfo.createFont()).andStubReturn(mockFont);
		
		
		expect(mockApplication.getResources()).andStubReturn(mockSquirrelResources);
		
		
		expect(mockSquirrelResources.getIcon(EasyMock.isA(String.class))).andStubReturn(mockImageIcon);
		
		mockHelper.replayAll();
		UIFactory.initialize(mockPreferences, mockApplication);
		
		serializableToTest = new SessionPanel(mockSession);
		((SessionPanel)serializableToTest).setMainPanelFactory(mockMainPanelFactory);
	}

	@After
	public void tearDown() throws Exception
	{
		super.tearDown();
		mockHelper.verifyAll();
	}
		
}
