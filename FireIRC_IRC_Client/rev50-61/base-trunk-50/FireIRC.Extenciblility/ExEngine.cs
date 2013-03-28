using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
namespace FireIRC.Extenciblility
{
    public class ExEngine
    {
        ContextMenuStrip nickList = new ContextMenuStrip();
        public ContextMenuStrip NickList
        {
            get { return nickList; }
            set { nickList = value; }
        }
        ContextMenuStrip channelList = new ContextMenuStrip();
        public ContextMenuStrip ChannelList
        {
            get { return channelList; }
            set { channelList = value; }
        }
    }
}
