using System; 
using System.Collections; 
using System.Diagnostics; 
using System.Drawing; 
using System.IO; 
using System.Reflection; 
using System.Text; 
using System.Windows.Forms; 
using Genghis; 
using Infragistics.Win.UltraWinExplorerBar; 
using Infragistics.Win.UltraWinToolbars; 
using NewsComponents.Utils; 
using RssBandit.WinGui.Interfaces; 
using RssBandit.WinGui.Utility; namespace  RssBandit.WinGui.Controls {
	
 public class  StateSerializationHelper {
		
  private  StateSerializationHelper() {}
 
  private static  string GetKeyOrderArray(Infragistics.Shared.KeyedSubObjectsCollectionBase c, string separator) {
   StringBuilder sb = new StringBuilder();
   Infragistics.Shared.IKeyedSubObject[] a = new Infragistics.Shared.IKeyedSubObject[c.Count];
   c.CopyTo(a, 0);
   for (int i = 0; i < a.Length; i++) {
    Infragistics.Shared.IKeyedSubObject o = a[i];
    if (o.Key == null || o.Key.Length == 0)
     throw new InvalidOperationException("KeyedSubObjectsCollectionBase must have a unique Key.");
    if (i > 0) sb.Append(separator);
    sb.Append(o.Key);
   }
   return sb.ToString();
  }
 
  private static  Version _infragisticsToolbarVersion = null;
 
  public static  Version InfragisticsToolbarVersion {
   get {
    if (_infragisticsToolbarVersion == null)
     _infragisticsToolbarVersion = Assembly.GetAssembly(typeof(UltraToolbarsManager)).GetName().Version;
    return _infragisticsToolbarVersion;
   }
  }
 
  public static  byte[] SaveControlStateToByte(UltraToolbarsManager toolbarManager, bool saveUserCustomizations) {
   using (MemoryStream stream = SaveToolbarManager(toolbarManager, saveUserCustomizations, true)) {
    return stream.ToArray();
   }
  }
 
  public static  string SaveControlStateToString(UltraToolbarsManager toolbarManager, bool saveUserCustomizations) {
   using (MemoryStream stream = SaveToolbarManager(toolbarManager, saveUserCustomizations, false)) {
    StreamReader r = new StreamReader(stream);
    return r.ReadToEnd();
   }
  }
 
  public static  MemoryStream SaveToolbarManager(UltraToolbarsManager toolbarManager, bool saveUserCustomizations, bool asBinary) {
   MemoryStream stream = new MemoryStream();
   if (asBinary)
    toolbarManager.SaveAsBinary(stream, saveUserCustomizations);
   else
    toolbarManager.SaveAsXml(stream, saveUserCustomizations);
   stream.Seek(0, SeekOrigin.Begin);
   return stream;
  }
 
  public static  void LoadControlStateFromByte(UltraToolbarsManager toolbarManager, byte[] theSettings, CommandMediator mediator) {
   if (theSettings == null)
    return;
   if (theSettings.Length == 0)
    return;
   using (Stream stream = new MemoryStream(theSettings)) {
    LoadToolbarManager(toolbarManager, stream, true, mediator);
   }
  }
 
  public static  void LoadControlStateFromString(UltraToolbarsManager toolbarManager, string theSettings, CommandMediator mediator) {
   if (string.IsNullOrEmpty(theSettings))
    return;
   using (Stream stream = new MemoryStream()) {
    StreamWriter writer = new StreamWriter(stream);
    writer.Write(theSettings);
    writer.Flush();
    stream.Seek(0, SeekOrigin.Begin);
    LoadToolbarManager(toolbarManager, stream, false, mediator);
   }
  }
 struct  LocalizedProperties {
			
   public  string Caption;
 
   public  string ToolTipText;
 
   public  string StatusText;
 
   public  string DescriptionOnMenu;
 
   public  string Category;
 
   public  string CustomizerCaption;
 
   public  string CustomizerDescription;
 
   public  LocalizedProperties(ToolBase tool) {
    Caption = tool.SharedProps.Caption;
    ToolTipText = tool.SharedProps.ToolTipText;
    StatusText = tool.SharedProps.StatusText;
    DescriptionOnMenu = tool.SharedProps.DescriptionOnMenu;
    Category = tool.SharedProps.Category;
    CustomizerCaption= tool.SharedProps.CustomizerCaption;
    CustomizerDescription = tool.SharedProps.CustomizerDescription;
   }
 
   public  void Apply(ToolBase tool) {
    tool.SharedProps.Caption = Caption;
    tool.SharedProps.ToolTipText = ToolTipText;
    tool.SharedProps.StatusText = StatusText;
    tool.SharedProps.DescriptionOnMenu = DescriptionOnMenu;
    tool.SharedProps.Category = Category;
    tool.SharedProps.CustomizerCaption = CustomizerCaption;
    tool.SharedProps.CustomizerDescription = CustomizerDescription;
   }

		}
		
  public static  void LoadToolbarManager(UltraToolbarsManager toolbarManager, Stream stream, bool asBinary, CommandMediator mediator)
  {
   Hashtable oCaptions = new Hashtable();
   for (int i = 0; i < toolbarManager.Tools.Count; i++) {
    ToolBase oTool = toolbarManager.Tools[i];
    LocalizedProperties props = new LocalizedProperties(oTool);
    oCaptions.Add(oTool.Key, props);
   }
   try {
    if (asBinary)
     toolbarManager.LoadFromBinary(stream);
    else
     toolbarManager.LoadFromXml(stream);
   }
   catch (Exception ex) {
    Trace.WriteLine("toolbarManager.LoadFrom...() failed: " + ex.Message);
    return;
   }
   for (int i = 0; i < toolbarManager.Tools.Count; i++) {
    ToolBase oTool = toolbarManager.Tools[i];
    if (oCaptions.Contains(oTool.Key)) {
     LocalizedProperties props = (LocalizedProperties) oCaptions[oTool.Key];
     props.Apply(oTool);
    }
    mediator.ReRegisterCommand(oTool as ICommand);
   }
  }
 
  private static  Version _infragisticsExplorerBarVersion = null;
 
  public static  Version InfragisticsExplorerBarVersion {
   get {
    if (_infragisticsExplorerBarVersion == null)
     _infragisticsExplorerBarVersion = Assembly.GetAssembly(typeof(UltraExplorerBar)).GetName().Version;
    return _infragisticsExplorerBarVersion;
   }
  }
 
  public static  void LoadExplorerBar(UltraExplorerBar explorerBar, Settings store, string preferenceID) {
   if (explorerBar == null)
    throw new ArgumentNullException("explorerBar");
   if (store == null)
    throw new ArgumentNullException("store");
   if (preferenceID == null || preferenceID.Length == 0)
    throw new ArgumentNullException("preferenceID");
   if (explorerBar.Groups.AllowDuplicateKeys)
    throw new InvalidOperationException("UltraExplorerBarGroupsCollection must provide unique Keys to support Load/Save settings operations.");
   Preferences prefReader = store.GetSubnode(preferenceID);
   int version = prefReader.GetInt32("version", 0);
   if (version < 1)
    return;
   Rectangle dimensions = new Rectangle();
   dimensions.X = prefReader.GetInt32("Location.X", explorerBar.Location.X);
   dimensions.Y = prefReader.GetInt32("Location.Y", explorerBar.Location.Y);
   dimensions.Width = prefReader.GetInt32("Size.Width", explorerBar.Size.Width);
   dimensions.Height = prefReader.GetInt32("Size.Height", explorerBar.Size.Height);
   if (explorerBar.Dock == DockStyle.None && explorerBar.Anchor == AnchorStyles.None)
    explorerBar.Bounds = dimensions;
   explorerBar.NavigationMaxGroupHeaders = prefReader.GetInt32("MaxGroupHeaders", explorerBar.NavigationMaxGroupHeaders);
   if (explorerBar.Groups.Count == 0)
    return;
   string defaultOrder = GetKeyOrderArray(explorerBar.Groups, ";");
   string orderArray = prefReader.GetString("groupOrder", defaultOrder);
   ArrayList groupOrder = new ArrayList(orderArray.Split(new char[]{';'}));
   for (int i = 0; i < groupOrder.Count; i++) {
    string key = (string)groupOrder[i];
    if (explorerBar.Groups.Exists(key) &&
     explorerBar.Groups.IndexOf(key) != i &&
     i < explorerBar.Groups.Count)
    {
     UltraExplorerBarGroup group = explorerBar.Groups[key];
     explorerBar.Groups.Remove(group);
     explorerBar.Groups.Insert(i, group);
    }
   }
   string selectedGroup = prefReader.GetString("selected", explorerBar.SelectedGroup.Key);
   for (int i = 0; i < explorerBar.Groups.Count; i++) {
    UltraExplorerBarGroup group = explorerBar.Groups[i];
    string key = String.Format("group.{0}", i);
    if (group.Key != null && group.Key.Length > 0)
     key = String.Format("group.{0}", group.Key);
    group.Visible = prefReader.GetBoolean(String.Format("{0}.Visible", key), group.Visible);
    if (selectedGroup == key)
     group.Selected = true;
   }
  }
 
  public static  void SaveExplorerBar(UltraExplorerBar explorerBar, Settings store, string preferenceID) {
   if (explorerBar == null)
    throw new ArgumentNullException("explorerBar");
   if (store == null)
    throw new ArgumentNullException("store");
   if (preferenceID == null || preferenceID.Length == 0)
    throw new ArgumentNullException("preferenceID");
   if (explorerBar.Groups.AllowDuplicateKeys)
    throw new InvalidOperationException("UltraExplorerBarGroupsCollection must provide unique Keys to support Load/Save settings operations.");
   Preferences prefWriter = store.GetSubnode(preferenceID);
   prefWriter.SetProperty("version", 1);
   prefWriter.SetProperty("Location.X", explorerBar.Location.X);
   prefWriter.SetProperty("Location.Y", explorerBar.Location.Y);
   prefWriter.SetProperty("Size.Width", explorerBar.Size.Width);
   prefWriter.SetProperty("Size.Height", explorerBar.Size.Height);
   prefWriter.SetProperty("MaxGroupHeaders", explorerBar.NavigationMaxGroupHeaders);
   if (explorerBar.Groups.Count == 0) {
    prefWriter.SetProperty("groupOrder", null);
    return;
   }
   prefWriter.SetProperty("groupOrder", GetKeyOrderArray(explorerBar.Groups, ";"));
   for (int i = 0; i < explorerBar.Groups.Count; i++) {
    UltraExplorerBarGroup group = explorerBar.Groups[i];
    string key = String.Format("group.{0}", i);
    if (group.Key != null && group.Key.Length > 0)
     key = String.Format("group.{0}", group.Key);
    if (group.Selected)
     prefWriter.SetProperty("selected", key);
    prefWriter.SetProperty(String.Format("{0}.Visible", key), group.Visible);
   }
  }

	}

}
