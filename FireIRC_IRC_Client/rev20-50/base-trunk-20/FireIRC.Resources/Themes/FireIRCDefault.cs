using System;
using System.Collections.Generic;
using System.Text;
using OVT.FireIRC.Resources.PlugIn;
namespace OVT.FireIRC.Resources.Themes
{
    public class FireIRCDefault : ImageTheme
    {
        public FireIRCDefault()
        {
            this.ToolbarImages.Images.Add("connect", MIRCTheme_Resources.connect);
            this.ToolbarImages.Images.Add("options", MIRCTheme_Resources.options);
            this.ToolbarImages.Images.Add("favorates", MIRCTheme_Resources.favorates);
            this.ToolbarImages.Images.Add("channel list", MIRCTheme_Resources.channel_list);
            this.ToolbarImages.Images.Add("plugins", MIRCTheme_Resources.plugin);
            this.ToolbarImages.Images.Add("address book", MIRCTheme_Resources.address_book);
            this.ToolbarImages.Images.Add("online timer", MIRCTheme_Resources.timer);
            this.ToolbarImages.Images.Add("colors", MIRCTheme_Resources.colors);
            this.ToolbarImages.Images.Add("url list", MIRCTheme_Resources.url_list);
            this.ToolbarImages.Images.Add("tile horz", MIRCTheme_Resources.tile_horz);
            this.ToolbarImages.Images.Add("cascade", MIRCTheme_Resources.cascade);
            this.ToolbarImages.Images.Add("about", MIRCTheme_Resources.about);
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
