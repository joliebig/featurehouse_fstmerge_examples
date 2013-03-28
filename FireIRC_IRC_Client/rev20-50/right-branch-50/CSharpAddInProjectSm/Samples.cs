using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using OVT.FireIRC.Resources.PlugIn;
namespace CSharpAddInProjectSm
{
    public class SampleAlias : IAlias
    {
        public void ExecuteAlias(string[] parms, OVT.FireIRC.Resources.IRC.IrcClient client)
        {
            throw new NotImplementedException();
        }
    }
    public class SampleOnCommand : IOnCommand
    {
        public void ExecuteOnCommand(OVT.FireIRC.Resources.IRC.IrcClient irc, OVT.FireIRC.Resources.Client.ExClass exClass, Dictionary<string, string> PVars)
        {
            throw new NotImplementedException();
        }
    }
    public class SampleTheme : ImageTheme
    {
        public SampleTheme()
        {
            this.ToolbarImages.Images.Add("connect", SampResources.Blank);
            this.ToolbarImages.Images.Add("options", SampResources.Blank);
            this.ToolbarImages.Images.Add("favorates", SampResources.Blank);
            this.ToolbarImages.Images.Add("channel list", SampResources.Blank);
            this.ToolbarImages.Images.Add("plugins", SampResources.Blank);
            this.ToolbarImages.Images.Add("address book", SampResources.Blank);
            this.ToolbarImages.Images.Add("online timer", SampResources.Blank);
            this.ToolbarImages.Images.Add("colors", SampResources.Blank);
            this.ToolbarImages.Images.Add("url list", SampResources.Blank);
            this.ToolbarImages.Images.Add("tile horz", SampResources.Blank);
            this.ToolbarImages.Images.Add("cascade", SampResources.Blank);
            this.ToolbarImages.Images.Add("about", SampResources.Blank);
            this.TreeViewImages.Images.Add("irc_query", SampResources.Blank);
            this.TreeViewImages.Images.Add("irc_channel", SampResources.Blank);
            this.TreeViewImages.Images.Add("irc_server", SampResources.Blank);
            this.TreeViewImages.Images.Add("folder_icon", SampResources.Blank);
            this.ModeIcons.Images.Add("Operator", SampResources.Blank);
            this.ModeIcons.Images.Add("HalfOp", SampResources.Blank);
            this.ModeIcons.Images.Add("Voice", SampResources.Blank);
            this.ModeIcons.Images.Add("Normal", SampResources.Blank);
        }
    }
}
