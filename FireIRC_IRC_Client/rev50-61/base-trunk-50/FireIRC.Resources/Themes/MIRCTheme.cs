using System;
using System.Collections.Generic;
using System.Text;
using FireIRC.Extenciblility;
namespace OVT.FireIRC.Resources.Themes
{
    public class MIRCTheme : ImageTheme
    {
        public MIRCTheme()
        {
            this.TreeViewImages.Images.Add("irc_query", MIRCTheme_Resources.query_icon);
            this.TreeViewImages.Images.Add("irc_channel", MIRCTheme_Resources.channelwindow_icon);
            this.TreeViewImages.Images.Add("irc_server", MIRCTheme_Resources.serverwindow_icon);
            this.TreeViewImages.Images.Add("folder_icon", MIRCTheme_Resources.CLSDFOLD);
            this.ModeIcons.Images.Add("Operator", MIRCTheme_Resources.irc_op);
            this.ModeIcons.Images.Add("HalfOp", MIRCTheme_Resources.irc_halfop);
            this.ModeIcons.Images.Add("Voice", MIRCTheme_Resources.irc_voice);
            this.ModeIcons.Images.Add("Normal", MIRCTheme_Resources.irc_normal);
        }
    }
}
