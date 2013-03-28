using System;
using System.Collections.Generic;
using System.Text;
using FireIRC.Extenciblility;
namespace OVT.FireIRC.Resources.Themes
{
    public class ColloquyTheme : ImageTheme
    {
        public ColloquyTheme()
        {
            this.TreeViewImages.Images.Add("irc_query", ColloquyTheme_Resources.privateChatTab);
            this.TreeViewImages.Images.Add("irc_channel", ColloquyTheme_Resources.roomTab);
            this.TreeViewImages.Images.Add("irc_server", ColloquyTheme_Resources.serverWindow);
            this.TreeViewImages.Images.Add("folder_icon", MIRCTheme_Resources.CLSDFOLD);
            this.ModeIcons.Images.Add("Operator", ColloquyTheme_Resources.op);
            this.ModeIcons.Images.Add("HalfOp", ColloquyTheme_Resources.half_op);
            this.ModeIcons.Images.Add("Voice", ColloquyTheme_Resources.voice);
            this.ModeIcons.Images.Add("Normal", ColloquyTheme_Resources.person);
        }
    }
}
