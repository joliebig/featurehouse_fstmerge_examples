using System;
using System.Drawing;
using System.Runtime.InteropServices;
using System.Security.Permissions;
using System.Windows.Forms;
namespace WorldWind.PluginEngine
{
 public class PluginListView : ListView
 {
  ImageList imageList;
  public PluginListView()
  {
   this.View = View.Details;
   this.ResizeRedraw = true;
  }
  protected override CreateParams CreateParams
  {
   get
   {
    const int LVS_OWNERDRAWFIXED = 0x0400;
    CreateParams cp=base.CreateParams;
    cp.Style|=(int)LVS_OWNERDRAWFIXED;
    return cp;
   }
  }
  [SecurityPermission(SecurityAction.LinkDemand, UnmanagedCode=true), SecurityPermission(SecurityAction.InheritanceDemand, UnmanagedCode=true)]
  protected override void WndProc(ref Message m)
  {
   const int WM_DRAWITEM = 0x002B;
   const int WM_REFLECT = 0x2000;
   const int ODS_SELECTED = 0x0001;
   switch(m.Msg)
   {
    case WM_REFLECT | WM_DRAWITEM:
    {
     NativeMethods.DRAWITEMSTRUCT dis = (NativeMethods.DRAWITEMSTRUCT)Marshal.PtrToStructure(
      m.LParam,typeof(NativeMethods.DRAWITEMSTRUCT));
     Rectangle r = new Rectangle(dis.rcItem.left, dis.rcItem.top,
      dis.rcItem.right - dis.rcItem.left, dis.rcItem.bottom - dis.rcItem.top);
     using( Graphics g = Graphics.FromHdc(dis.hdc) )
     {
      DrawItemState d = DrawItemState.Default;
      if((dis.itemState & ODS_SELECTED) > 0)
       d = DrawItemState.Selected;
      PluginListItem item = (PluginListItem)Items[dis.itemID];
      DrawItemEventArgs e = new DrawItemEventArgs(g,this.Font,r,dis.itemID,d);
      OnDrawItem(e, item);
      m.Result = (IntPtr)1;
     }
     break;
    }
    default:
     base.WndProc(ref m);
     break;
   }
  }
  protected override void OnMouseUp(MouseEventArgs e)
  {
   const int LVM_FIRST = 0x1000;
   const int LVM_SUBITEMHITTEST = LVM_FIRST + 57;
   NativeMethods.LVHITTESTINFO hitInfo = new NativeMethods.LVHITTESTINFO();
   hitInfo.pt = new Point(e.X, e.Y);
   IntPtr pointer = Marshal.AllocHGlobal(Marshal.SizeOf(typeof(NativeMethods.LVHITTESTINFO)));
   Marshal.StructureToPtr(hitInfo, pointer, true);
   Message message = Message.Create(Handle, LVM_SUBITEMHITTEST, IntPtr.Zero, pointer);
   DefWndProc(ref message);
   hitInfo = (NativeMethods.LVHITTESTINFO)Marshal.PtrToStructure(
    pointer, typeof(NativeMethods.LVHITTESTINFO));
   Marshal.FreeHGlobal(pointer);
   if(hitInfo.iItem >=0 && hitInfo.iSubItem == 1)
   {
    PluginListItem item = (PluginListItem)Items[hitInfo.iItem];
    item.PluginInfo.IsLoadedAtStartup = !item.PluginInfo.IsLoadedAtStartup;
    Invalidate();
    return;
   }
   base.OnMouseUp(e);
  }
  protected void OnDrawItem( DrawItemEventArgs e, PluginListItem item )
  {
   e.DrawBackground();
   const int imageWidth = 16+3;
   if(imageList==null)
    imageList = ((PluginDialog)Parent).ImageList;
   if(imageList!=null)
   {
    int imageIndex = item.PluginInfo.IsCurrentlyLoaded ? 0 : 1;
    imageList.Draw(e.Graphics, e.Bounds.Left+2, e.Bounds.Top+1, imageIndex);
   }
   Rectangle bounds = Rectangle.FromLTRB(e.Bounds.Left+imageWidth,
    e.Bounds.Top, e.Bounds.Left+Columns[0].Width, e.Bounds.Bottom);
   using(Brush brush = new SolidBrush(e.ForeColor))
    e.Graphics.DrawString(item.Name, e.Font, brush, bounds);
   bounds = Rectangle.FromLTRB(bounds.Right+1,
    bounds.Top, bounds.Right+Columns[1].Width+1, bounds.Bottom-1);
   ButtonState state = item.PluginInfo.IsLoadedAtStartup ? ButtonState.Checked : ButtonState.Normal;
   ControlPaint.DrawCheckBox(e.Graphics, bounds, state);
  }
 }
}
