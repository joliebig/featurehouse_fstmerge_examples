

package org.gjt.sp.jedit.gui; 


import javax.swing.*; 
import java.awt.event.*; 
import java.awt.*; 
import org.gjt.sp.jedit.*; 



  class  EnhancedMenuItem  extends JMenuItem {
	
	
	
	

	 

	
	

	 

	
	

	 

	
	

	
	

	
	

	
	

	

	
	

	
	

	
	

	
	

	
	

	 

	
	static
	{
		String shortcutFont;
		if (OperatingSystem.isMacOSLF())
			shortcutFont = "Lucida Grande";
		else
			shortcutFont = "Monospaced";
		
		acceleratorFont = UIManager.getFont("MenuItem.acceleratorFont");
		if(acceleratorFont == null)
			acceleratorFont = new Font(shortcutFont,Font.PLAIN,12);
		else
		{
			acceleratorFont = new Font(shortcutFont,
				acceleratorFont.getStyle(),
				acceleratorFont.getSize());
		}
		acceleratorForeground = UIManager
			.getColor("MenuItem.acceleratorForeground");
		if(acceleratorForeground == null)
			acceleratorForeground = Color.black;

		acceleratorSelectionForeground = UIManager
			.getColor("MenuItem.acceleratorSelectionForeground");
		if(acceleratorSelectionForeground == null)
			acceleratorSelectionForeground = Color.black;
	}

	 

	

	
	  

	

	
	class  MouseHandler  extends MouseAdapter {
		
		

		

		

		

		

		

		


	}

	 

	
	static
	{
		String shortcutFont;
		if (OperatingSystem.isMacOSLF())
			shortcutFont = "Lucida Grande";
		else
			shortcutFont = "Monospaced";
		
		acceleratorFont = UIManager.getFont("MenuItem.acceleratorFont");
		if(acceleratorFont == null)
			acceleratorFont = new Font(shortcutFont,Font.PLAIN,12);
		else
		{
			acceleratorFont = new Font(shortcutFont,
				acceleratorFont.getStyle(),
				acceleratorFont.getSize());
		}
		acceleratorForeground = UIManager
			.getColor("MenuItem.acceleratorForeground");
		if(acceleratorForeground == null)
			acceleratorForeground = Color.black;

		acceleratorSelectionForeground = UIManager
			.getColor("MenuItem.acceleratorSelectionForeground");
		if(acceleratorSelectionForeground == null)
			acceleratorSelectionForeground = Color.black;
	}


}
