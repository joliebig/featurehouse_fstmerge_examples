using System;
using System.Threading;
using System.Timers;
using System.Collections;
using System.Windows;
using System.Windows.Forms;
using WorldWind;
using WorldWind.Menu;
using WorldWind.Renderable;
using jhuapl.util;
namespace jhuapl.collab.CollabSpace
{
 public class Navigator : WorldWind.PluginEngine.Plugin
 {
  protected NavigatorMenuButton m_menuButton;
  protected System.Windows.Forms.MenuItem m_navMenuItem;
  protected System.Windows.Forms.MenuItem m_infoMenuItem;
  public System.Windows.Forms.MenuItem NavMenu
  {
   get { return m_navMenuItem; }
  }
  public System.Windows.Forms.MenuItem InfoMenu
  {
   get { return m_infoMenuItem; }
  }
  public Navigator()
  {
  }
  public override void Load()
  {
   JHU_Globals.Initialize(ParentApplication.WorldWindow);
   JHU_Globals.getInstance().BasePath = this.PluginDirectory + @"\Plugins\Navigator\";
            m_menuButton = new NavigatorMenuButton(this.PluginDirectory + @"\Plugins\Navigator\Data\Icons\Interface\Navigator.png", this);
   ParentApplication.WorldWindow.MenuBar.AddToolsMenuButton(m_menuButton);
   m_navMenuItem = new System.Windows.Forms.MenuItem();
   m_navMenuItem.Text = "Hide Navigator\tN";
   m_navMenuItem.Click += new System.EventHandler(navMenuItem_Click);
   ParentApplication.ToolsMenu.MenuItems.Add(m_navMenuItem);
   m_infoMenuItem = new System.Windows.Forms.MenuItem();
   m_infoMenuItem.Text = "Hide Info\tI";
   m_infoMenuItem.Click += new System.EventHandler(infoMenuItem_Click);
   ParentApplication.ToolsMenu.MenuItems.Add(m_infoMenuItem);
   ParentApplication.WorldWindow.KeyUp += new KeyEventHandler(keyUp);
   JHU_Globals.getInstance().NavigatorForm.Enabled = true;
   JHU_Globals.getInstance().NavigatorForm.Visible = true;
   JHU_Globals.getInstance().InfoForm.Enabled = true;
   JHU_Globals.getInstance().InfoForm.Visible = true;
   base.Load ();
  }
  public override void Unload()
  {
   m_menuButton.SetPushed(false);
   base.Unload ();
  }
  protected void navMenuItem_Click(object sender, EventArgs s)
  {
   if (JHU_Globals.getInstance().NavigatorForm.Enabled)
   {
    JHU_Globals.getInstance().NavigatorForm.Enabled = false;
    m_navMenuItem.Text = "Show Navigator\tN";
   }
   else
   {
    JHU_Globals.getInstance().NavigatorForm.Enabled = true;
    JHU_Globals.getInstance().NavigatorForm.Visible = true;
    m_navMenuItem.Text = "Hide Navigator\tN";
   }
  }
  protected void infoMenuItem_Click(object sender, EventArgs s)
  {
   if (JHU_Globals.getInstance().InfoForm.Enabled)
   {
    JHU_Globals.getInstance().InfoForm.Enabled = false;
    m_infoMenuItem.Text = "Show Info\tI";
   }
   else
   {
    JHU_Globals.getInstance().InfoForm.Enabled = true;
    JHU_Globals.getInstance().InfoForm.Visible = true;
    m_infoMenuItem.Text = "Hide Info\tI";
   }
  }
  protected void keyUp(object sender, KeyEventArgs e)
  {
   if (e.KeyData==Keys.N)
   {
    navMenuItem_Click(sender, e);
   }
   else if (e.KeyData==Keys.I)
   {
    infoMenuItem_Click(sender, e);
   }
  }
 }
 public class NavigatorMenuButton : MenuButton
 {
  internal static Navigator m_plugin;
  protected JHU_RootWidget m_rootWidget;
  protected bool m_setFlag = true;
  public NavigatorMenuButton(string buttonIconPath, Navigator plugin) : base(buttonIconPath)
  {
   m_plugin = plugin;
   m_rootWidget = JHU_Globals.getInstance().RootWidget;
   this.Description = "Navigator";
   this.SetPushed(true);
  }
  public override void Dispose()
  {
   base.Dispose ();
  }
  public override void Update(DrawArgs drawArgs)
  {
  }
  public override bool IsPushed()
  {
   return m_setFlag;
  }
  public override void SetPushed(bool isPushed)
  {
   m_setFlag = isPushed;
  }
  public override void OnKeyDown(KeyEventArgs keyEvent)
  {
   m_rootWidget.OnKeyDown(keyEvent);
  }
  public override void OnKeyUp(KeyEventArgs keyEvent)
  {
   m_rootWidget.OnKeyUp(keyEvent);
  }
  public override bool OnMouseDown(MouseEventArgs e)
  {
   if(this.IsPushed())
    return m_rootWidget.OnMouseDown(e);
   else
    return false;
  }
  public override bool OnMouseMove(MouseEventArgs e)
  {
   if(this.IsPushed())
    return m_rootWidget.OnMouseMove(e);
   else
    return false;
  }
  public override bool OnMouseUp(MouseEventArgs e)
  {
   if(this.IsPushed())
    return m_rootWidget.OnMouseUp(e);
   else
    return false;
  }
  public override bool OnMouseWheel(MouseEventArgs e)
  {
   if(this.IsPushed())
    return m_rootWidget.OnMouseWheel(e);
   else
    return false;
  }
  public override void Render(DrawArgs drawArgs)
  {
   if (JHU_Globals.getInstance().NavigatorForm.Visible)
    m_plugin.NavMenu.Text = "Hide Navigator\tN";
   else
    m_plugin.NavMenu.Text = "Show Navigator\tN";
   if (JHU_Globals.getInstance().InfoForm.Visible)
    m_plugin.InfoMenu.Text = "Hide Info\tI";
   else
    m_plugin.InfoMenu.Text = "Show Info\tI";
   m_rootWidget.Render(drawArgs);
  }
 }
}
