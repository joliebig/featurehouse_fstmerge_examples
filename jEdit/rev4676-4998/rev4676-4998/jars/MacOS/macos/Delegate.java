

package macos; 


import com.apple.eawt.*; 
import com.apple.cocoa.application.*; 
import com.apple.cocoa.foundation.*; 
import java.util.Vector; 
import java.io.File; 
import javax.swing.*; 
import org.gjt.sp.jedit.*; 
import org.gjt.sp.jedit.gui.*; 
import org.gjt.sp.jedit.msg.*; 
import org.gjt.sp.jedit.options.GlobalOptions; 
import org.gjt.sp.util.Log; 
import com.apple.eio.*; 
import java.util.*; 
import org.gjt.sp.jedit.browser.*; 


  class  Delegate  extends ApplicationAdapter {
	
	
	

	
	
	private Buffer lastOpenFile;

	
	
	
	
	<<<<<<< /work/joliebig/semistructured_merge/fse2011_artifact/fstmerge_tmp1307448649191/fstmerge_var1_7996853296129719619
public Delegate()
	{
		if (jEdit.getBooleanProperty("MacOSPlugin.useScreenMenuBar",
			jEdit.getBooleanProperty("MacOSPlugin.default.useScreenMenuBar"))
		)
			System.setProperty("apple.laf.useScreenMenuBar","true");
		else
			System.setProperty("apple.laf.useScreenMenuBar","false");
	}
=======
>>>>>>> /work/joliebig/semistructured_merge/fse2011_artifact/fstmerge_tmp1307448649191/fstmerge_var2_6394676385388872556


	 
	
	
	
	
	<<<<<<< /work/joliebig/semistructured_merge/fse2011_artifact/fstmerge_tmp1307448649265/fstmerge_var1_8925321157202561214
public void handleAbout(ApplicationEvent event)
	{
		event.setHandled(true);
		new AboutDialog(jEdit.getActiveView());
	}
=======
>>>>>>> /work/joliebig/semistructured_merge/fse2011_artifact/fstmerge_tmp1307448649265/fstmerge_var2_4543228565538506008


	 

	
	<<<<<<< /work/joliebig/semistructured_merge/fse2011_artifact/fstmerge_tmp1307448649339/fstmerge_var1_363083855839649203
public void handleFileCodes(BufferUpdate msg)
	{
		Buffer buffer = msg.getBuffer();
		
		
		if (!buffer.isDirty() && msg.getWhat() == BufferUpdate.DIRTY_CHANGED)
		{
			try {
				FileManager.setFileTypeAndCreator(buffer.getPath(),
					buffer.getIntegerProperty("MacOSPlugin.type",
						jEdit.getIntegerProperty("MacOSPlugin.default.type",0)),
					buffer.getIntegerProperty("MacOSPlugin.creator",
						jEdit.getIntegerProperty("MacOSPlugin.default.creator",0)));
			} catch (Exception e) {
				
			}
		}
		
		else if (msg.getWhat() == BufferUpdate.CREATED)
		{			
			if ("true".equals(
				jEdit.getProperty("MacOSPlugin.preserveCodes")))
			{
				try {
					int type = FileManager.getFileType(buffer.getPath());
					int creator = FileManager.getFileCreator(buffer.getPath());
					
					if (type != 0)
						buffer.setIntegerProperty("MacOSPlugin.type",type);
					if (creator != 0)
						buffer.setIntegerProperty("MacOSPlugin.creator",creator);
				} catch (Exception e) {
					
				}
			}
		}
	}
=======
>>>>>>> /work/joliebig/semistructured_merge/fse2011_artifact/fstmerge_tmp1307448649339/fstmerge_var2_87200268616815561


	 
	
	
	<<<<<<< /work/joliebig/semistructured_merge/fse2011_artifact/fstmerge_tmp1307448649408/fstmerge_var1_3962406808565927767
public void handleOpenFile(ApplicationEvent event)
	{
		filenames.add(event.getFilename());
		event.setHandled(true);
	}
=======
>>>>>>> /work/joliebig/semistructured_merge/fse2011_artifact/fstmerge_tmp1307448649408/fstmerge_var2_2572169613678160566


	 

	
	<<<<<<< /work/joliebig/semistructured_merge/fse2011_artifact/fstmerge_tmp1307448649482/fstmerge_var1_1849377821849148559
public void handleOpenFile(ViewUpdate msg)
	{
		if(msg.getWhat() == ViewUpdate.CREATED)
		{
			Iterator i = filenames.iterator();
			while (i.hasNext())
				jEdit.openFile(msg.getView(),(String)i.next());
			MacOSPlugin.started = true;
			NSApplication app = NSApplication.sharedApplication();
			app.setServicesProvider(new Delegate());
		}
	}
=======
>>>>>>> /work/joliebig/semistructured_merge/fse2011_artifact/fstmerge_tmp1307448649482/fstmerge_var2_4962954464468671442


	 
	
	
	<<<<<<< /work/joliebig/semistructured_merge/fse2011_artifact/fstmerge_tmp1307448649554/fstmerge_var1_1068260424635019335
public void handlePreferences(ApplicationEvent event)
	{
		event.setHandled(true);
		new GlobalOptions(jEdit.getActiveView());
	}
=======
>>>>>>> /work/joliebig/semistructured_merge/fse2011_artifact/fstmerge_tmp1307448649554/fstmerge_var2_8131453557226931088


	 
	
	
	
	public void handleQuit(ApplicationEvent event)
	{
		event.setHandled(false);
		jEdit.exit(jEdit.getActiveView(),true);
	}

	 
	
	
	
	
	
	
	
	

	
	
	
	
	
	<<<<<<< /work/joliebig/semistructured_merge/fse2011_artifact/fstmerge_tmp1307448649626/fstmerge_var1_3139406866288186988
public NSMenu applicationDockMenu(NSApplication sender)
	{
		NSMenu dockMenu;
		BufferMenu bufMenu;
		MacrosMenu macMenu;
		RecentMenu recMenu;
		RecentDirMenu dirMenu;
		NSMenuItem showCurrItem;
		NSMenuItem showCurrDirItem;
		NSMenuItem newViewItem;
		
		
		NSMenuItem miBuff = new NSMenuItem(jEdit.getProperty("MacOSPlugin.menu.buffers.label"),null,"");
		miBuff.setSubmenu(bufMenu = new BufferMenu());
		
		
		NSMenuItem miRec = new NSMenuItem(jEdit.getProperty("MacOSPlugin.menu.recent.label"),null,"");
		miRec.setSubmenu(recMenu = new RecentMenu());
		
		
		NSMenuItem miDir = new NSMenuItem(jEdit.getProperty("MacOSPlugin.menu.recentDir.label"),null,"");
		miDir.setSubmenu(dirMenu = new RecentDirMenu());
		
		
		NSMenuItem miMac = new NSMenuItem(jEdit.getProperty("MacOSPlugin.menu.macros.label"),null,"");
		miMac.setSubmenu(macMenu = new MacrosMenu());
		
		dockMenu = new NSMenu();
		newViewItem = new NSMenuItem(jEdit.getProperty("MacOSPlugin.menu.newView"),actionSel,"");
		newViewItem.setTarget(new NewViewAction());
		dockMenu.addItem(newViewItem);
		dockMenu.addItem(new NSMenuItem().separatorItem());
		showCurrItem = new NSMenuItem(jEdit.getProperty("MacOSPlugin.menu.showCurrent"),actionSel,"");
		dockMenu.addItem(showCurrItem);
		showCurrDirItem = new NSMenuItem(jEdit.getProperty("MacOSPlugin.menu.showCurrentDir"),actionSel,"");
		dockMenu.addItem(showCurrDirItem);
		dockMenu.addItem(new NSMenuItem().separatorItem());
		dockMenu.addItem(miBuff);
		dockMenu.addItem(miRec);
		dockMenu.addItem(miDir);
		
		
		if (jEdit.getViewCount() == 0)
			miMac.setEnabled(false);
		
		bufMenu.updateMenu();
		recMenu.updateMenu();
		dirMenu.updateMenu();
		macMenu.updateMenu();
		
		View view = jEdit.getActiveView();
		if (view != null)
		{
			File buff = new File(view.getBuffer().getPath());
			if (buff.exists())
			{
				showCurrItem.setTarget(new ShowFileAction(buff.getPath()));
				showCurrDirItem.setTarget(new ShowFileAction(buff.getParent()));
			}
		}
		else
		{
			showCurrItem.setEnabled(false);
			showCurrDirItem.setEnabled(false);
		}
		
		return dockMenu;
	}
=======
>>>>>>> /work/joliebig/semistructured_merge/fse2011_artifact/fstmerge_tmp1307448649626/fstmerge_var2_8778431099990250831


	 
	
	
	<<<<<<< /work/joliebig/semistructured_merge/fse2011_artifact/fstmerge_tmp1307448649701/fstmerge_var1_792291369105180386
public boolean applicationShouldHandleReopen(NSApplication theApplication, boolean flag)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if (jEdit.getViewCount() == 0)
					new NewViewAction().doAction();
			}
		});
		
		return false;
	}
=======
>>>>>>> /work/joliebig/semistructured_merge/fse2011_artifact/fstmerge_tmp1307448649701/fstmerge_var2_4874858613973861403


	 
	
	
	

	
	
	
	
	
	
	
	
	<<<<<<< /work/joliebig/semistructured_merge/fse2011_artifact/fstmerge_tmp1307448649838/fstmerge_var1_7686592121831708162
public String openFile(NSPasteboard pboard, String userData)
	{
		if (jEdit.getViewCount() == 0)
			return null;
		
		NSData data = pboard.dataForType("NSFilenamesPboardType");
		String[] error = new String[1];
		int[] format = new int[1];
		NSArray filenames = (NSArray)NSPropertyListSerialization.propertyListFromData(data,
			NSPropertyListSerialization.PropertyListImmutable,
			format,
			error);
		int count = filenames.count();
		for (int i=0; i<count; i++)
		{
			File file = new File((String)filenames.objectAtIndex(i));
			if (file.isDirectory())
				VFSBrowser.browseDirectory(jEdit.getActiveView(),file.getPath());
			else
				jEdit.openFile(jEdit.getActiveView(),file.getPath());
		}
		
		return null;
	}
=======
>>>>>>> /work/joliebig/semistructured_merge/fse2011_artifact/fstmerge_tmp1307448649838/fstmerge_var2_4881614454937561450


	 
	
	
	<<<<<<< /work/joliebig/semistructured_merge/fse2011_artifact/fstmerge_tmp1307448649910/fstmerge_var1_8369538125021526552
public String openSelection(NSPasteboard pboard, String userData)
	{
		String string = pboard.stringForType("NSStringPboardType");
		if (jEdit.getViewCount() == 0)
			new NewViewAction().doAction();
		jEdit.newFile(jEdit.getActiveView()).insert(0,pboard.stringForType("NSStringPboardType"));
		return null;
	}
=======
>>>>>>> /work/joliebig/semistructured_merge/fse2011_artifact/fstmerge_tmp1307448649910/fstmerge_var2_7254457372411865506


	 
	
	
	
	
	
	
	  
	
	
	
	
	
	
	class  BufferMenu  extends NSMenu {
		
		

		
		
		


	}

	 
	
	
	  
	
	
	class  MacrosMenu  extends NSMenu {
		
		

		
		
		

		
		
		


	}

	 
	
	
	  
	
	
	class  RecentMenu  extends NSMenu {
		
		

		
		
		<<<<<<< /work/joliebig/semistructured_merge/fse2011_artifact/fstmerge_tmp1307448650404/fstmerge_var1_1690585028597824904
public void updateMenu()
		{
			List recent = BufferHistory.getHistory();
			NSMenuItem item;
			File file;
			int max = recent.size();
			int min = max - 20;
			
			int length = numberOfItems();
			for (int i=0; i<length; i++)
				removeItemAtIndex(0);
			
			if (max == 0)
			{
				item = new NSMenuItem(jEdit.getProperty("MacOSPlugin.menu.recent.none"),null,"");
				item.setEnabled(false);
				addItem(item);
				return;
			}
			
			if (min < 0)
				min = 0;
			
			for (int i=max-1; i >= min ; i--)
			{
				file = new File(((BufferHistory.Entry)recent.get(i)).path);
				item = new NSMenuItem(file.getName(),actionSel,"");
				item.setTarget(new ShowFileAction(file.getPath()));
				if (!file.exists())
					item.setEnabled(false);
				addItem(item);
			}
		}
=======
>>>>>>> /work/joliebig/semistructured_merge/fse2011_artifact/fstmerge_tmp1307448650404/fstmerge_var2_5472469994688109147



	}

	 
	
	
	  
	
	
	class  RecentDirMenu  extends NSMenu {
		
		

		
		
		<<<<<<< /work/joliebig/semistructured_merge/fse2011_artifact/fstmerge_tmp1307448650545/fstmerge_var1_1101176010191009094
public void updateMenu()
		{
			HistoryModel model = HistoryModel.getModel("vfs.browser.path");
			NSMenuItem item;
			File file;
			int max = model.getSize();
			
			int length = numberOfItems();
			for (int i=0; i<length; i++)
				removeItemAtIndex(0);
			
			if (max == 0)
			{
				item = new NSMenuItem(jEdit.getProperty("MacOSPlugin.menu.recentDir.none"),null,"");
				item.setEnabled(false);
				addItem(item);
				return;
			}
			
			for (int i=0; i < max ; i++)
			{
				file = new File(model.getItem(i));
				item = new NSMenuItem(file.getName(),actionSel,"");
				item.setTarget(new ShowFileAction(file.getPath()));
				if (!file.exists())
					item.setEnabled(false);
				addItem(item);
			}
		}
=======
>>>>>>> /work/joliebig/semistructured_merge/fse2011_artifact/fstmerge_tmp1307448650545/fstmerge_var2_7819031473486720529



	}

	 
	
	
	  
	
	
	class  MacroAction {
		
		

		
		
		

		
		
		


	}

	 
	
	
	  
	
	
	class  NewViewAction {
		
		<<<<<<< /work/joliebig/semistructured_merge/fse2011_artifact/fstmerge_tmp1307448650764/fstmerge_var1_8159410819323225447
public void doAction()
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					if (jEdit.getViewCount() == 0)
						PerspectiveManager.loadPerspective(true);
					else
						jEdit.newView(jEdit.getActiveView());
				}
			});
		}
=======
>>>>>>> /work/joliebig/semistructured_merge/fse2011_artifact/fstmerge_tmp1307448650764/fstmerge_var2_3538043147741307104



	}

	 
	
	
	  
	
	
	class  ShowFileAction {
		
		

		
		
		

		
		
		


	}

	
	
	private List filenames = new LinkedList();

	 
	
	
	public void applicationOpenFiles(NSApplication sender, NSArray filenames)
	{
		int count = filenames.count();
		for (int i=0; i<count; i++)
		{
			File file = new File((String)filenames.objectAtIndex(i));
			Buffer buffer;
			
			View view = jEdit.getActiveView();
			if(view == null)
				view = PerspectiveManager.loadPerspective(true);
			
			if (file.isDirectory())
			{
				VFSBrowser.browseDirectory(jEdit.getActiveView(),file.getPath());
				return;
			}
			
			if (jEdit.openFile(view,file.getPath()) == null)
				Log.log(Log.ERROR,this,"Error opening file.");
		}
	}

	 
	
	
	public String insertSelection(NSPasteboard pboard, String userData)
	{
		String string = pboard.stringForType("NSStringPboardType");
		if (jEdit.getViewCount() > 0)
		{
			View view = jEdit.getActiveView();
			view.getBuffer().insert(view.getTextArea().getCaretPosition(),string);
		}
		return null;
	}


}
