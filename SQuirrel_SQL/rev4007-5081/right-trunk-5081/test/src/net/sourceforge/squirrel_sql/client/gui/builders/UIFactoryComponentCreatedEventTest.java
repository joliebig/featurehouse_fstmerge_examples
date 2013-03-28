
package net.sourceforge.squirrel_sql.client.gui.builders;


import java.awt.Component;

import javax.swing.JPanel;

import net.sourceforge.squirrel_sql.AbstractSerializableTest;

import org.junit.Before;

public class UIFactoryComponentCreatedEventTest extends AbstractSerializableTest
{

	Component component = new JPanel();
	UIFactory mockFactory = mockHelper.createMock(UIFactory.class);
	
	@Before
	public void setUp() throws Exception
	{
		super.serializableToTest = new UIFactoryComponentCreatedEvent(mockFactory, component);
	}


}
