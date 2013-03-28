using System;
using System.ComponentModel;
using System.Windows.Forms;
using RssBandit.Utility.Keyboard;
using RssBandit.WinGui.Interfaces;
using RssBandit.WinGui.Utility;
namespace RssBandit.WinGui.Menus {
 public class AppContextMenuCommand : MenuItem, ICommand, ICommandComponent {
  private Container components = null;
  protected CommandMediator med;
  protected event ExecuteCommandHandler OnExecute;
  protected string description = String.Empty;
  protected int imageIndex;
  private object tag = null;
  public AppContextMenuCommand()
  {
   InitializeComponent();
   EventHandler evh = ClickHandler;
   this.Click += evh;
  }
  public AppContextMenuCommand(string cmdId, CommandMediator mediator, ExecuteCommandHandler executor, string caption, string description, ShortcutHandler shortcuts) :
   this(cmdId, mediator, executor, caption, description)
  {
   SetShortcuts(cmdId, shortcuts);
  }
  public AppContextMenuCommand(string cmdId, CommandMediator mediator, ExecuteCommandHandler executor, string caption, string description):
   this() {
   Text = caption;
   this.description = description;
   Tag = cmdId;
   med = mediator;
   if (executor != null)
    OnExecute += executor;
   med.RegisterCommand (cmdId, this);
  }
  public AppContextMenuCommand(string cmdId, CommandMediator mediator, ExecuteCommandHandler executor, string caption, string description, int imageIndex, ShortcutHandler shortcuts):
   this(cmdId, mediator, executor, caption, description, imageIndex)
  {
   SetShortcuts(cmdId, shortcuts);
  }
  public AppContextMenuCommand(string cmdId, CommandMediator mediator, ExecuteCommandHandler executor, string caption, string description, int imageIndex):
   this(cmdId, mediator, executor, caption, description) {
   this.imageIndex = imageIndex;
  }
  public AppContextMenuCommand(string cmdId, CommandMediator mediator, string caption, string description, ShortcutHandler shortcuts):
   this(cmdId, mediator, null, caption, description)
  {
   SetShortcuts(cmdId, shortcuts);
  }
  public AppContextMenuCommand(string cmdId, CommandMediator mediator, string caption, string description):
   this(cmdId, mediator, null, caption, description) {
  }
  public AppContextMenuCommand(string cmdId, CommandMediator mediator, string caption, string description, int imageIndex, ShortcutHandler shortcuts) :
   this(cmdId, mediator, caption, description, shortcuts)
  {
   this.imageIndex = imageIndex;
  }
  private void SetShortcuts(string cmdId, ShortcutHandler shortcuts)
  {
   if(shortcuts != null)
   {
    this.Shortcut = shortcuts.GetShortcut(cmdId);
    this.ShowShortcut = shortcuts.IsShortcutDisplayed(cmdId);
   }
  }
  public object Tag {
   get { return tag; }
   set { tag = value; }
  }
  public new bool Checked {
   get { return base.Checked; }
   set { base.Checked = value; }
  }
  public new bool Enabled {
   get { return base.Enabled; }
   set { base.Enabled = value; }
  }
  public new bool Visible {
   get { return base.Visible; }
   set { base.Visible = value; }
  }
  public void ClickHandler(object obj, EventArgs e) {
   this.Execute();
  }
  protected override void Dispose( bool disposing ) {
   if( disposing ) {
    if(components != null) {
     components.Dispose();
    }
   }
   base.Dispose( disposing );
  }
  private void InitializeComponent() {
   components = new System.ComponentModel.Container();
  }
  public virtual void Execute() {
   if (OnExecute != null)
    OnExecute(this);
  }
  public virtual void Initialize() {
  }
  public string CommandID { get { return (string)Tag; } }
  public CommandMediator Mediator {
   get { return med; }
   set { med = value; }
  }
 }
}
