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
                NickInformation i = new NickInformation();
                i.Nicks = new string[] { "FireIRC2", "FireIRC2_", "FireIRC2__" };
                i.Identity = "Default Identity";
                i.UserName = "FireIRC2";
                i.RealName = "FireIRC2 User";
                settings.NickInfo.Add(i);
                mIRCServerListConverter.AddServerListToServers(mIRCServerListConverter.ConvertListToServers(mIRCServerListConverter.MakeFireIRCServerList("http://www.mirc.com/servers.ini")), settings);
            }
        }
    }
}
