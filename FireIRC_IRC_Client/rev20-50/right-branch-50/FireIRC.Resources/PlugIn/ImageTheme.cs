using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using System.Drawing;
namespace OVT.FireIRC.Resources.PlugIn
{
    public class ImageTheme
    {
        ImageList toolbarImages = new ImageList();
        ImageList treeViewImages = new ImageList();
        ImageList modeIcons = new ImageList();
        public ImageList ModeIcons
        {
            get
            {
                return modeIcons;
            }
        }
        public ImageList ToolbarImages
        {
            get
            {
                return toolbarImages;
            }
        }
        public ImageList TreeViewImages
        {
            get
            {
                return treeViewImages;
            }
        }
    }
}
