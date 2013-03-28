using System;
using System.Collections;
using System.IO;
using System.Diagnostics;
using System.Drawing;
using System.Xml.Serialization;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using System.Windows.Forms;
using WorldWind;
using WorldWind.Renderable;
using WorldWind.Net;
using System.Xml;
using MapTools;
namespace MeasureToolNG.Plugins
{
    public class MeasureToolNG : WorldWind.PluginEngine.Plugin
    {
        MenuItem menuItem;
        MeasureToolLayer layer;
        public override void Load()
        {
            layer = new MeasureToolLayer(
                ParentApplication.WorldWindow.CurrentWorld);
            ParentApplication.WorldWindow.CurrentWorld.RenderableObjects.Add(layer);
            menuItem = new MenuItem("Measure\tM");
            menuItem.Click += new EventHandler(menuItemClicked);
            ParentApplication.ToolsMenu.MenuItems.Add(menuItem);
            ParentApplication.WorldWindow.MouseMove += new MouseEventHandler(layer.MouseMove);
            ParentApplication.WorldWindow.MouseDown += new MouseEventHandler(layer.MouseDown);
            ParentApplication.WorldWindow.MouseUp += new MouseEventHandler(layer.MouseUp);
            ParentApplication.WorldWindow.KeyUp += new KeyEventHandler(layer.KeyUp);
        }
        public override void Unload()
        {
            if (menuItem != null)
            {
                ParentApplication.ToolsMenu.MenuItems.Remove(menuItem);
                menuItem.Dispose();
                menuItem = null;
            }
            ParentApplication.WorldWindow.MouseMove -= new MouseEventHandler(layer.MouseMove);
            ParentApplication.WorldWindow.MouseDown -= new MouseEventHandler(layer.MouseDown);
            ParentApplication.WorldWindow.MouseUp -= new MouseEventHandler(layer.MouseUp);
            ParentApplication.WorldWindow.KeyUp -= new KeyEventHandler(layer.KeyUp);
            ParentApplication.WorldWindow.CurrentWorld.RenderableObjects.Remove(layer);
        }
        void menuItemClicked(object sender, EventArgs e)
        {
            layer.IsOn = !layer.IsOn;
            menuItem.Checked = layer.IsOn;
        }
    }
    class MeasureToolLayer : LineFeature
    {
        public MeasureToolLayer(World world):base("Measure Tool",world,null,Color.Red)
        {
        }
        public void KeyUp(object sender, KeyEventArgs e)
        {
        }
        public void MouseMove(object sender, MouseEventArgs e)
        {
        }
        public void MouseDown(object sender, MouseEventArgs e)
        {
        }
        public void MouseUp(object sender, MouseEventArgs e)
        {
        }
    }
}
