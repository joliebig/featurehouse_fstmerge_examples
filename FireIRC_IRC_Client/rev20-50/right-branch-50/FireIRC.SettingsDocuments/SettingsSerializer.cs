using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
using System.IO;
using OVT.Melissa.PluginSupport;
namespace OVT.FireIRC.Resources
{
    public class SettingsContainer
    {
        Settings settings = new Settings();
        public Settings Settings
        {
            get { return settings; }
            set { settings = value; }
        }
        static public string FireIRCSettingsDirectory = PropertyService.ConfigDirectory;
        public object[] SaveSettings()
        {
            XmlSerializer x = new XmlSerializer(typeof(global::OVT.FireIRC.Resources.Settings));
            FileStream s = File.Open(Path.Combine(FireIRCSettingsDirectory, "fireirc.settings.xml"), FileMode.Create);
            x.Serialize(s, settings);
            s.Close();
            return new object[] { x };
        }
        public void ReadSettings()
        {
            try
            {
                XmlSerializer x = new XmlSerializer(typeof(Settings));
                FileStream s = File.Open(Path.Combine(FireIRCSettingsDirectory, "fireirc.settings.xml"), FileMode.Open);
                Settings = (Settings)x.Deserialize(s);
                s.Close();
            }
            catch
            {
                if (Directory.Exists(FireIRCSettingsDirectory) == false) { Directory.CreateDirectory(FireIRCSettingsDirectory); }
                Settings.NickInfo.Nick1 = "FireIRC2";
                Settings.NickInfo.Nick2 = "FireIRC2_";
                Settings.NickInfo.Nick3 = "FireIRC2__";
                Settings.NickInfo.UserName = "FireIRC2";
                Settings.NickInfo.RealName = "FireIRC2 User";
                mIRCServerListConverter.AddServerListToServers(mIRCServerListConverter.ConvertListToServers(mIRCServerListConverter.MakeFireIRCServerList("http://www.mirc.com/servers.ini")), settings);
            }
        }
    }
}
