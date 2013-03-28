using System; 
using System.Runtime.Serialization; 
using System.Security.Permissions; 
using Infragistics.Win; 
using Infragistics.Win.UltraWinToolbars; 
using RssBandit.Utility.Keyboard; 
using RssBandit.WinGui.Interfaces; 
using RssBandit.WinGui.Utility; namespace  RssBandit.WinGui.Tools {
	
 [Serializable] 
 public class  AppPopupMenuCommand  : Infragistics.Win.UltraWinToolbars.PopupMenuTool, ICommand, ICommandComponent {
		
  private  System.ComponentModel.Container components = null;
 
  protected  CommandMediator med;
 
  protected  event ExecuteCommandHandler OnExecute; 
  public  AppPopupMenuCommand(string key):base(key) {
   InitializeComponent();
  }
 
  protected  AppPopupMenuCommand(SerializationInfo info, StreamingContext context) :
   base (info, context) {
  }
 
  public  AppPopupMenuCommand(string cmdId, CommandMediator mediator, ExecuteCommandHandler executor, string caption, string description):
   this(cmdId) {
   base.SharedProps.Caption = caption;
   base.SharedProps.StatusText = description;
   OnExecute += executor;
   med = mediator;
   med.RegisterCommand (cmdId, this);
  }
 
  public  AppPopupMenuCommand(string cmdId, CommandMediator mediator, ExecuteCommandHandler executor, string caption, string desciption, int imageIndex):
   this(cmdId, mediator, executor, caption, desciption) {
   Infragistics.Win.Appearance a = new Infragistics.Win.Appearance();
   a.Image = imageIndex;
   base.SharedProps.AppearancesSmall.Appearance = a;
  }
 
  public  void ReJoinMediatorFrom(AppPopupMenuCommand cmd) {
   this.Mediator = cmd.Mediator;
   this.OnExecute = cmd.OnExecute;
  }
 
  protected override  Infragistics.Win.UltraWinToolbars.ToolBase Clone(bool cloneNewInstance) {
   AppPopupMenuCommand tool = new AppPopupMenuCommand(this.Key);
   tool.InitializeFrom(this, cloneNewInstance);
   return tool;
  }
 
  [SecurityPermission(SecurityAction.Demand, SerializationFormatter=true)] 
  protected override  void GetObjectData(SerializationInfo info, StreamingContext context) {
   base.GetObjectData(info, context);
  }
 
  protected override  void Initialize(ToolsCollectionBase parentCollection) {
   base.Initialize(parentCollection);
  }
 
  public new  bool Checked {
   get { return base.Checked; }
   set { base.Checked = value; }
  }
 
  public  bool Enabled {
   get { return base.SharedProps.Enabled; }
   set { base.SharedProps.Enabled = value; }
  }
 
  public  bool Visible {
   get { return base.SharedProps.Visible; }
   set { base.SharedProps.Visible = value; }
  }
 
  public  void ClickHandler(object obj, EventArgs e) {
   this.Execute();
  }
 
  protected  void Dispose( bool disposing ) {
   if( disposing ) {
    if(components != null) {
     components.Dispose();
    }
    this.Mediator = null;
    this.OnExecute = null;
   }
   base.Dispose();
  }
 
  private  void InitializeComponent() {
   components = new System.ComponentModel.Container();
  }
 
  public virtual  void Execute() {
   if (OnExecute != null)
    OnExecute(this);
  }
 
  public virtual  void Initialize() {
  }
 
  public  string CommandID { get { return base.Key; } }
 
  public  CommandMediator Mediator{
   get { return med; }
   set { med = value; }
  }

	}
	
 [Serializable] 
 public class  AppButtonToolCommand  : Infragistics.Win.UltraWinToolbars.ButtonTool, ICommand, ICommandComponent {
		
  private  System.ComponentModel.Container components = null;
 
  protected  CommandMediator med;
 
  internal protected  event ExecuteCommandHandler OnExecute; 
  public  AppButtonToolCommand(string key):base(key) {
   InitializeComponent();
  }
 
  protected  AppButtonToolCommand(SerializationInfo info, StreamingContext context) :
   base (info, context) {
   }
 
  public  AppButtonToolCommand(string cmdId, CommandMediator mediator, ExecuteCommandHandler executor, string caption, string description):
   this(cmdId) {
   base.SharedProps.Caption = caption;
   base.SharedProps.StatusText = description;
   OnExecute += executor;
   med = mediator;
   med.RegisterCommand (cmdId, this);
   }
 
  public  AppButtonToolCommand(string cmdId, CommandMediator mediator, ExecuteCommandHandler executor, string caption, string desciption, int imageIndex):
   this(cmdId, mediator, executor, caption, desciption) {
   Appearance a = new Appearance();
   a.Image = imageIndex;
   base.SharedProps.AppearancesSmall.Appearance = a;
   }
 
  public  AppButtonToolCommand(string cmdId, CommandMediator mediator, ExecuteCommandHandler executor, string caption, string description, ShortcutHandler shortcuts):
   this(cmdId, mediator, executor ,caption, description) {
   SetShortcuts(cmdId, shortcuts);
   }
 
  public  AppButtonToolCommand(string cmdId, CommandMediator mediator, ExecuteCommandHandler executor, string caption, string description, int imageIndex, ShortcutHandler shortcuts):
   this(cmdId, mediator, executor ,caption, description, imageIndex) {
   SetShortcuts(cmdId, shortcuts);
   }
 
  public  void ReJoinMediatorFrom(AppButtonToolCommand cmd) {
   this.Mediator = cmd.Mediator;
   this.OnExecute = cmd.OnExecute;
  }
 
  protected override  Infragistics.Win.UltraWinToolbars.ToolBase Clone(bool cloneNewInstance) {
   AppButtonToolCommand tool = new AppButtonToolCommand(this.Key);
   tool.InitializeFrom(this, cloneNewInstance);
   return tool;
  }
 
  [SecurityPermission(SecurityAction.Demand, SerializationFormatter=true)] 
  protected override  void GetObjectData(SerializationInfo info, StreamingContext context) {
   base.GetObjectData(info, context);
  }
 
  protected override  void Initialize(ToolsCollectionBase parentCollection) {
   base.Initialize(parentCollection);
  }
 
  public  bool Checked {
   get { throw new NotImplementedException(); }
   set { throw new NotImplementedException(); }
  }
 
  public  bool Enabled {
   get { return base.SharedProps.Enabled; }
   set { base.SharedProps.Enabled = value; }
  }
 
  public  bool Visible {
   get { return base.SharedProps.Visible; }
   set { base.SharedProps.Visible = value; }
  }
 
  public  void ClickHandler(object obj, EventArgs e) {
   this.Execute();
  }
 
  private  void SetShortcuts(string cmdId, ShortcutHandler shortcuts) {
   if(shortcuts != null) {
    this.SharedProps.Shortcut = shortcuts.GetShortcut(cmdId);
   }
  }
 
  protected  void Dispose( bool disposing ) {
   if( disposing ) {
    if(components != null) {
     components.Dispose();
    }
    this.Mediator = null;
    this.OnExecute = null;
   }
   base.Dispose();
  }
 
  private  void InitializeComponent() {
   components = new System.ComponentModel.Container();
  }
 
  public virtual  void Execute() {
   if (OnExecute != null)
    OnExecute(this);
  }
 
  public virtual  void Initialize() {
  }
 
  public  string CommandID { get { return base.Key; } }
 
  public  CommandMediator Mediator{
   get { return med; }
   set { med = value; }
  }

	}
	
 [Serializable] 
 public class  AppStateButtonToolCommand  : Infragistics.Win.UltraWinToolbars.StateButtonTool, ICommand, ICommandComponent {
		
  private  System.ComponentModel.Container components = null;
 
  protected  CommandMediator med;
 
  protected  event ExecuteCommandHandler OnExecute; 
  public  AppStateButtonToolCommand(string key):base(key) {
   InitializeComponent();
   base.MenuDisplayStyle = StateButtonMenuDisplayStyle.DisplayCheckmark;
  }
 
  protected  AppStateButtonToolCommand(SerializationInfo info, StreamingContext context) : base (info, context) {
  }
 
  public  AppStateButtonToolCommand(string cmdId, CommandMediator mediator, ExecuteCommandHandler executor, string caption, string description):
   this(cmdId) {
   base.SharedProps.Caption = caption;
   base.SharedProps.StatusText = description;
   OnExecute += executor;
   med = mediator;
   med.RegisterCommand (cmdId, this);
   }
 
  public  AppStateButtonToolCommand(string cmdId, CommandMediator mediator, ExecuteCommandHandler executor, string caption, string description, int imageIndex, ShortcutHandler shortcuts):
   this(cmdId, mediator, executor ,caption, description)
  {
   Appearance a = new Appearance();
   a.Image = imageIndex;
   base.SharedProps.AppearancesSmall.Appearance = a;
   base.MenuDisplayStyle = StateButtonMenuDisplayStyle.DisplayToolImage;
   SetShortcuts(cmdId, shortcuts);
  }
 
  public  AppStateButtonToolCommand(string cmdId, CommandMediator mediator, ExecuteCommandHandler executor, string caption, string description, ShortcutHandler shortcuts):
   this(cmdId, mediator, executor ,caption, description)
  {
   SetShortcuts(cmdId, shortcuts);
  }
 
  public  void ReJoinMediatorFrom(AppStateButtonToolCommand cmd) {
   this.Mediator = cmd.Mediator;
   this.OnExecute = cmd.OnExecute;
  }
 
  protected override  Infragistics.Win.UltraWinToolbars.ToolBase Clone(bool cloneNewInstance) {
   AppStateButtonToolCommand tool = new AppStateButtonToolCommand(this.Key);
   tool.InitializeFrom(this, cloneNewInstance);
   return tool;
  }
 
  [SecurityPermission(SecurityAction.Demand, SerializationFormatter=true)] 
  protected override  void GetObjectData(SerializationInfo info, StreamingContext context) {
   base.GetObjectData(info, context);
  }
 
  protected override  void Initialize(ToolsCollectionBase parentCollection) {
   base.Initialize(parentCollection);
  }
 
  public  bool Enabled {
   get { return base.SharedProps.Enabled; }
   set { base.SharedProps.Enabled = value; }
  }
 
  public  bool Visible {
   get { return base.SharedProps.Visible; }
   set { base.SharedProps.Visible = value; }
  }
 
  public  void ClickHandler(object obj, EventArgs e) {
   this.Execute();
  }
 
  private  void SetShortcuts(string cmdId, ShortcutHandler shortcuts) {
   if(shortcuts != null) {
    this.SharedProps.Shortcut = shortcuts.GetShortcut(cmdId);
   }
  }
 
  protected  void Dispose( bool disposing ) {
   if( disposing ) {
    if(components != null) {
     components.Dispose();
    }
    this.Mediator = null;
    this.OnExecute = null;
   }
   base.Dispose();
  }
 
  private  void InitializeComponent() {
   components = new System.ComponentModel.Container();
  }
 
  public virtual  void Execute() {
   if (OnExecute != null)
    OnExecute(this);
  }
 
  public virtual  void Initialize() {
  }
 
  public  string CommandID { get { return base.Key; } }
 
  public  CommandMediator Mediator{
   get { return med; }
   set { med = value; }
  }

	}

}
