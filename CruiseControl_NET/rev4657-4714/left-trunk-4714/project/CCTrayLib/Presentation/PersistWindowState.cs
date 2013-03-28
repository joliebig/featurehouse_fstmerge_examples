using System;
using System.ComponentModel;
using System.Drawing;
using System.Windows.Forms;
using Microsoft.Win32;
namespace ThoughtWorks.CruiseControl.CCTrayLib.Presentation
{
 public class PersistWindowState
 {
  public event EventHandler<WindowStateEventArgs> LoadState;
  public event EventHandler<WindowStateEventArgs> SaveState;
  private Form parent;
  private string regPath;
  private int normalLeft;
  private int normalTop;
  private int normalWidth;
  private int normalHeight;
  private bool visible;
  public PersistWindowState() {}
  public PersistWindowState(Form parent)
  {
   SetParentForm(parent);
  }
  public Form Parent
  {
   set
   {
    if (value != parent)
     SetParentForm(value);
   }
   get { return parent; }
  }
  private void SetParentForm(Form value)
  {
   parent = value;
   parent.Load += OnLoad;
   parent.Closing += OnClosing;
   parent.Move += OnMove;
   parent.Resize += OnResize;
   parent.VisibleChanged += OnVisibleChanged;
   normalWidth = parent.Width;
   normalHeight = parent.Height;
  }
  public string RegistryPath
  {
   set { regPath = value; }
   get { return regPath; }
  }
  private void OnLoad(object sender, EventArgs e)
  {
   RegistryKey key = Registry.CurrentUser.OpenSubKey(regPath);
   if (key == null)
    return;
   int left = (int) key.GetValue("Left", parent.Left);
   int top = (int) key.GetValue("Top", parent.Top);
   int width = (int) key.GetValue("Width", parent.Width);
   int height = (int) key.GetValue("Height", parent.Height);
   Point requestedLocation = new Point(left, top);
   foreach (Screen aScreen in Screen.AllScreens)
   {
    if (aScreen.Bounds.Contains(requestedLocation) )
    {
     Point adjusted = requestedLocation;
     if (aScreen.Primary && adjusted.Y < SystemInformation.WorkingArea.Top)
      adjusted = new Point(requestedLocation.X, SystemInformation.WorkingArea.Top);
     parent.Location = adjusted;
     break;
    }
   }
   parent.Size = new Size(width, height);
   visible = 1 == (int) key.GetValue("Visible", 1);
   if (!visible)
   {
    parent.BeginInvoke(new MethodInvoker(HideParent));
   }
   if (LoadState != null)
    LoadState(this, new WindowStateEventArgs(key));
  }
  private void HideParent()
  {
   parent.Hide();
  }
  private void OnClosing(object sender, CancelEventArgs e)
  {
   RegistryKey key = Registry.CurrentUser.CreateSubKey(regPath);
   if (key == null)
    return;
   key.SetValue("Left", normalLeft);
   key.SetValue("Top", normalTop);
   key.SetValue("Width", normalWidth);
   key.SetValue("Height", normalHeight);
   key.SetValue("Visible", visible ? 1 : 0);
   if (SaveState != null)
    SaveState(this, new WindowStateEventArgs(key));
  }
  private void OnMove(object sender, EventArgs e)
  {
   if (parent.WindowState == FormWindowState.Normal)
   {
    normalLeft = parent.Left;
    normalTop = parent.Top;
   }
  }
  private void OnResize(object sender, EventArgs e)
  {
   if (parent.WindowState == FormWindowState.Normal)
   {
    normalWidth = parent.Width;
    normalHeight = parent.Height;
   }
  }
  private void OnVisibleChanged(object sender, EventArgs e)
  {
   visible = parent.Visible;
  }
 }
}
