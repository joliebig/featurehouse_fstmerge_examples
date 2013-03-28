using System; 
using System.Collections; 
using System.Collections.Specialized; 
using Infragistics.Win.UltraWinToolbars; 
using RssBandit.AppServices; namespace  RssBandit.WinGui {
	
 internal interface  ICommandBarImplementationSupport {
		
  CommandBar GetToolBarInstance(string id); 
  CommandBar GetMenuBarInstance(string id); 
  CommandBar AddToolBar(string id); 
  CommandBar AddMenuBar(string id); 
  CommandBar GetContexMenuInstance(string id); 
  CommandBar AddContextMenu(string id);
	}
	
 internal class  CommandBarManager : ICommandBarManager {
		
  internal  ICommandBarImplementationSupport instanceContainer;
 
  internal  CommandBarCollection collection;
 
  public  CommandBarManager(ICommandBarImplementationSupport instanceContainer) {
   this.instanceContainer = instanceContainer;
   this.collection = new CommandBarCollection(this);
  }
 
  public  ICommandBarCollection CommandBars {
   get {
    return this.collection;
   }
  }

	}
	
 internal class  CommandBarCollection : ICommandBarCollection {
		
  private readonly  CommandBarManager owner;
 
  public  CommandBarCollection(CommandBarManager owner) {
   this.owner = owner;
  }
 
  public  ICommandBar AddContextMenu(string identifier) {
   CommandBar c = owner.instanceContainer.AddContextMenu(identifier);
   return c;
  }
 
  public  ICommandBar AddMenuBar(string identifier) {
   return null;
  }
 
  public  ICommandBar AddToolBar(string identifier) {
   return owner.instanceContainer.AddToolBar(identifier);
  }
 
  ICommandBar ICommandBarCollection.this[string identifier] {
   get {
    return null;
   }
  }
 
  public  void Remove(ICommandBar commandBar) {
  }
 
  public  void Remove(string identifier) {
   throw new NotImplementedException();
  }
 
  public  bool Contains(ICommandBar commandBar) {
   return false;
  }
 
  public  bool Contains(string identifier) {
   throw new NotImplementedException();
  }
 
  public  void Clear() {
  }
 
  public  bool IsSynchronized {
   get {
    return false;
   }
  }
 
  public  int Count {
   get {
    return 0;
   }
  }
 
  public  void CopyTo(Array array, int index) {
  }
 
  public  object SyncRoot {
   get {
    return null;
   }
  }
 
  public  IEnumerator GetEnumerator() {
   return null;
  }
 
  ICommandBar ICommandBarCollection.AddContextMenu(string identifier)
  {
   throw new Exception("The method or operation is not implemented.");
  }
 
  ICommandBar ICommandBarCollection.AddMenuBar(string identifier)
  {
   throw new Exception("The method or operation is not implemented.");
  }
 
  ICommandBar ICommandBarCollection.AddToolBar(string identifier)
  {
   throw new Exception("The method or operation is not implemented.");
  }
 
  void ICommandBarCollection.Clear()
  {
   throw new Exception("The method or operation is not implemented.");
  }
 
  bool ICommandBarCollection.Contains(ICommandBar commandBar)
  {
   throw new Exception("The method or operation is not implemented.");
  }
 
  bool ICommandBarCollection.Contains(string identifier)
  {
   throw new Exception("The method or operation is not implemented.");
  }
 
  void ICommandBarCollection.Remove(ICommandBar commandBar)
  {
   throw new Exception("The method or operation is not implemented.");
  }
 
  void ICommandBarCollection.Remove(string identifier)
  {
   throw new Exception("The method or operation is not implemented.");
  }
 
  void ICollection.CopyTo(Array array, int index)
  {
   throw new Exception("The method or operation is not implemented.");
  }
 
  int ICollection.Count
  {
   get { throw new Exception("The method or operation is not implemented."); }
  }
 
  bool ICollection.IsSynchronized
  {
   get { throw new Exception("The method or operation is not implemented."); }
  }
 
  object ICollection.SyncRoot
  {
   get { throw new Exception("The method or operation is not implemented."); }
  }
 
  IEnumerator IEnumerable.GetEnumerator()
  {
   throw new Exception("The method or operation is not implemented.");
  }

	}
	
 internal class  CommandBar : ICommandBar {
		
  internal  UltraToolbarsManager decoratedItem;
 
  private  TagIdentifier TagID;
 
  public  CommandBar(UltraToolbarsManager decoratedItem) {
   this.decoratedItem = decoratedItem;
   this.TagID = new TagIdentifier(Guid.NewGuid().ToString("N"), null);
  }
 
  public  string Identifier {
   get { return TagID.Identifier; }
   set { TagID.Identifier = value; }
  }
 
  public  ICommandBarItemCollection Items {
   get { return new CommandBarItemCollection(decoratedItem.Tools); }
  }

	}
	
 internal class  CommandBarItemCollection : ICommandBarItemCollection {
		
  ToolsCollectionBase decoratedItem;
 
  public  CommandBarItemCollection(ToolsCollectionBase decoratedItem) {
   this.decoratedItem = decoratedItem;
  }
 
  public  ICommandBarItem this[int index] {
   get {
    return new CommandBarItem(decoratedItem[index]);
   }
  }
 
  public  void RemoveAt(int index) {
  }
 
  public  void Insert(int index, ICommandBarItem value) {
  }
 
  public  void AddRange(ICollection values) {
  }
 
  public  ICommandBarSeparator InsertSeparator(int index) {
   return null;
  }
 
  public  ICommandBarComboBox AddComboBox(string identifier, string caption) {
   return null;
  }
 
  public  void Remove(ICommandBarItem item) {
  }
 
  public  bool Contains(ICommandBarItem item) {
   return false;
  }
 
  public  void Clear() {
  }
 
  public  int IndexOf(ICommandBarItem item) {
   return 0;
  }
 
  public  ICommandBarButton AddButton(string identifier, string caption, System.Drawing.Image image, ExecuteCommandHandler clickHandler, System.Windows.Forms.Keys keyBinding) {
   return null;
  }
 
  ICommandBarButton ICommandBarItemCollection.AddButton(string identifier, string caption, ExecuteCommandHandler clickHandler, System.Windows.Forms.Keys keyBinding) {
   return null;
  }
 
  ICommandBarButton ICommandBarItemCollection.AddButton(string identifier, string caption, System.Drawing.Image image, ExecuteCommandHandler clickHandler) {
   return null;
  }
 
  ICommandBarButton ICommandBarItemCollection.AddButton(string identifier, string caption, ExecuteCommandHandler clickHandler) {
   return null;
  }
 
  public  ICommandBarMenu InsertMenu(int index, string identifier, string caption) {
   return null;
  }
 
  public  ICommandBarCheckBox InsertCheckButton(int index, string caption) {
   return null;
  }
 
  public  ICommandBarButton InsertButton(int index, string caption, ExecuteCommandHandler clickHandler) {
   return null;
  }
 
  public  void Add(ICommandBarItem value) {
  }
 
  public  ICommandBarSeparator AddSeparator() {
   return null;
  }
 
  public  ICommandBarMenu AddMenu(string identifier, string caption, System.Drawing.Image image) {
   return null;
  }
 
  ICommandBarMenu ICommandBarItemCollection.AddMenu(string identifier, string caption) {
   return null;
  }
 
  public  ICommandBarCheckBox AddCheckButton(string identifier, string caption, System.Drawing.Image image, System.Windows.Forms.Keys keyBinding) {
   return null;
  }
 
  ICommandBarCheckBox ICommandBarItemCollection.AddCheckButton(string identifier, string caption, System.Windows.Forms.Keys keyBinding) {
   return null;
  }
 
  ICommandBarCheckBox ICommandBarItemCollection.AddCheckButton(string identifier, string caption, System.Drawing.Image image) {
   return null;
  }
 
  ICommandBarCheckBox ICommandBarItemCollection.AddCheckButton(string identifier, string caption) {
   return null;
  }
 
  public  bool IsSynchronized {
   get {
    return false;
   }
  }
 
  public  int Count {
   get {
    return 0;
   }
  }
 
  public  void CopyTo(Array array, int index) {
  }
 
  public  object SyncRoot {
   get {
    return null;
   }
  }
 
  public  IEnumerator GetEnumerator() {
   return null;
  }

	}
	
 internal class  CommandBarItem : ICommandBarItem {
		
  private  ToolBase decoratedItem;
 
  public  CommandBarItem(ToolBase decoratedItem) {
   this.decoratedItem = decoratedItem;
  }
 
  public  System.Drawing.Image Image {
   get {
    return null;
   }
   set {
   }
  }
 
  public  string Identifier {
   get {
    TagIdentifier ti = this.decoratedItem.Tag as TagIdentifier;
    if (ti != null)
     return ti.Identifier;
    return null;
   }
  }
 
  public  bool Visible {
   get { return decoratedItem.SharedProps.Visible; }
   set { decoratedItem.SharedProps.Visible = value; }
  }
 
  public  object Tag {
   get {
    TagIdentifier ti = this.decoratedItem.Tag as TagIdentifier;
    if (ti != null)
     return ti.Tag;
    return null;
   }
   set {
    TagIdentifier ti = this.decoratedItem.Tag as TagIdentifier;
    if (ti != null)
     ti.Tag = value;
   }
  }
 
  public  bool Enabled {
   get { return decoratedItem.SharedProps.Enabled; }
   set { decoratedItem.SharedProps.Enabled = value; }
  }
 
  public  string Text {
   get { return decoratedItem.SharedProps.Caption; }
   set { decoratedItem.SharedProps.Caption = value; }
  }

	}
	
 internal class  TagIdentifier {
		
  public  TagIdentifier(string id, object tag) {
   Identifier = id;
   Tag = tag;
  }
 
  public  string Identifier;
 
  public  object Tag;

	}

}
