using System;
using System.ComponentModel;
using System.Drawing;
using System.Windows.Forms;
using Microsoft.Win32;
namespace Validator
{
    public class PersistWindowState : Component
    {
        public event WindowStateEventHandler LoadState;
        public event WindowStateEventHandler SaveState;
        private Form parent;
        private string regPath;
        private int normalLeft;
        private int normalTop;
        private int normalWidth;
        private int normalHeight;
        private bool visible;
        public Form Parent
        {
            set
            {
                parent = value;
                parent.Load += new EventHandler(OnLoad);
                parent.Closing += new CancelEventHandler(OnClosing);
                parent.Move += new EventHandler(OnMove);
                parent.Resize += new EventHandler(OnResize);
                parent.VisibleChanged += new EventHandler(OnVisibleChanged);
                normalWidth = parent.Width;
                normalHeight = parent.Height;
            }
            get { return parent; }
        }
        public string RegistryPath
        {
            set { regPath = value; }
            get { return regPath; }
        }
        private void OnLoad(object sender, EventArgs e)
        {
            RegistryKey key = Registry.CurrentUser.OpenSubKey(regPath);
            if (key != null)
            {
                int left = (int)key.GetValue("Left", parent.Left);
                int top = (int)key.GetValue("Top", parent.Top);
                int width = (int)key.GetValue("Width", parent.Width);
                int height = (int)key.GetValue("Height", parent.Height);
                Point requestedLocation = new Point(left, top);
                foreach (Screen aScreen in Screen.AllScreens)
                {
                    if (aScreen.Bounds.Contains(requestedLocation))
                    {
                        parent.Location = requestedLocation;
                        break;
                    }
                }
                parent.Size = new Size(width, height);
                visible = 1 == (int)key.GetValue("Visible", 1);
                if (!visible)
                {
                    parent.BeginInvoke(new MethodInvoker(HideParent));
                }
                if (LoadState != null)
                    LoadState(this, new WindowStateEventArgs(key));
            }
        }
        private void HideParent()
        {
            parent.Hide();
        }
        private void OnClosing(object sender, CancelEventArgs e)
        {
            RegistryKey key = Registry.CurrentUser.CreateSubKey(regPath);
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
