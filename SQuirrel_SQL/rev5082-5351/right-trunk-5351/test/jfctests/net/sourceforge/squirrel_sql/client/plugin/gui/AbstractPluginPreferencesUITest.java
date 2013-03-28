
package net.sourceforge.squirrel_sql.client.plugin.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.sourceforge.squirrel_sql.BaseSQuirreLJUnit4TestCase;
import net.sourceforge.squirrel_sql.client.plugin.PluginException;
import net.sourceforge.squirrel_sql.client.plugin.PluginQueryTokenizerPreferencesManager;
import net.sourceforge.squirrel_sql.fw.preferences.IQueryTokenizerPreferenceBean;

import org.fest.swing.annotation.GUITest;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.fixture.JCheckBoxFixture;
import org.fest.swing.fixture.JTextComponentFixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@GUITest
public abstract class AbstractPluginPreferencesUITest extends BaseSQuirreLJUnit4TestCase
{

	protected JFrame frame = null;
	protected FrameFixture fixture = null;
	protected PluginQueryTokenizerPreferencesPanel classUnderTest = null;
	protected PluginQueryTokenizerPreferencesManager prefsManager =
		new PluginQueryTokenizerPreferencesManager();
	protected JCheckBoxFixture useCustomQTCheckBox = null;

	@Before
   public void setUp() throws Exception
   {
   	if (frame == null) {
   		frame = constructTestFrame();
   	}
   	fixture = new FrameFixture(frame);
   	fixture.show();
   	useCustomQTCheckBox = fixture.checkBox("useCustomQTCheckBox");
   }

	@After
   public void tearDown()
   {
		if (fixture != null) {
			fixture.cleanUp();
		}
   }

	@Test
	public void testCustomQTCheckbox() {
		JTextComponentFixture lineCommentTextField = fixture.textBox("lineCommentTextField");
		JTextComponentFixture statementSeparatorTextField = fixture.textBox("statementSeparatorTextField");
		
		useCustomQTCheckBox.uncheck();
		useCustomQTCheckBox.check();
		useCustomQTCheckBox.uncheck();
		
		lineCommentTextField.requireDisabled();
		statementSeparatorTextField.requireDisabled();
		
		useCustomQTCheckBox.check();
		lineCommentTextField.requireEnabled();
		statementSeparatorTextField.requireEnabled();		
	}	

	
   protected JFrame constructTestFrame() throws Exception
   {
   	JFrame f = new JFrame();
   	f.getContentPane().setLayout(new BorderLayout());
   	File prefsFile = new File("prefs.xml");
   	prefsFile.delete();
   	prefsManager.initialize(new DummyPlugin(), getPreferenceBean());
   	classUnderTest = getPrefsPanelToTest();
   	JScrollPane sp = new JScrollPane(classUnderTest);
   	f.getContentPane().add(sp, BorderLayout.CENTER);
   	JButton button = new JButton("Save");
   	button.setName("saveButton");
   	button.addActionListener(new ActionListener()
   	{
   		public void actionPerformed(ActionEvent e)
   		{
   			classUnderTest.applyChanges();
   			prefsManager.unload();
   		}
   	});
   	JButton exitButton = new JButton("Exit");
   	exitButton.addActionListener(new ActionListener()
   	{
   		public void actionPerformed(ActionEvent e)
   		{
   			System.exit(0);
   		}
   	});
   	JPanel buttonPanel = new JPanel();
   	buttonPanel.add(button);
   	buttonPanel.add(exitButton);
   	f.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
   	f.setBounds(200, 50, 700, 700);
   	return f;
   }
   
   protected abstract PluginQueryTokenizerPreferencesPanel getPrefsPanelToTest() throws PluginException;
   
   protected abstract IQueryTokenizerPreferenceBean getPreferenceBean();
}