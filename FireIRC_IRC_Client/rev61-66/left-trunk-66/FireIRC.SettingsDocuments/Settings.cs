using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;
using System.Xml.Serialization;
namespace OVT.FireIRC.Resources
{
    public class Settings
    {
        Font mainFont;
        Color background;
        Color forground;
        List<Favorate> favorates = new List<Favorate>();
        List<Server> servers = new List<Server>();
        List<NickInformation> nickInfo = new List<NickInformation>();
        List<string> highlightRegEx = new List<string>();
        List<string> ignoreList = new List<string>();
        bool highlightsEnabled;
        bool useExperimentalInterface;
        bool useAcceptDenyPMSystem;
        public List<string> IgnoreList
        {
            get { return ignoreList; }
            set { ignoreList = value; }
        }
        public bool UseAcceptDenyPMSystem
        {
            get { return useAcceptDenyPMSystem; }
            set { useAcceptDenyPMSystem = value; }
        }
        public bool UseExperimentalInterface
        {
            get { return useExperimentalInterface; }
            set { useExperimentalInterface = value; }
        }
        public bool HighlightsEnabled
        {
            get { return highlightsEnabled; }
            set { highlightsEnabled = value; }
        }
        public List<string> HighlightRegEx
        {
            get { return highlightRegEx; }
            set { highlightRegEx = value; }
        }
        string primaryTheme = "FireIRC Default";
        public string PrimaryTheme
        {
            get { return primaryTheme; }
            set { primaryTheme = value; }
        }
        public List<NickInformation> NickInfo
        {
            get { return nickInfo; }
            set { nickInfo = value; }
        }
        public List<Server> Servers
        {
            get { return servers; }
            set { servers = value; }
        }
        public List<Favorate> Favorates
        {
            get { return favorates; }
            set { favorates = value; }
        }
        [XmlIgnore]
        public Font MainFont
        {
            get { return mainFont; }
            set { mainFont = value; }
        }
        [XmlIgnore]
        public Color Background
        {
            get { return background; }
            set { background = value; }
        }
        [XmlIgnore]
        public Color Forground
        {
            get { return forground; }
            set { forground = value; }
        }
        public NickInformation ReturnNickInformation(string name)
        {
            NickInformation ren = null;
            foreach (NickInformation i in NickInfo)
            {
                if (i.Identity == name)
                {
                    ren = i;
                }
            }
            return ren;
        }
    }
    public class Favorate
    {
        string channel;
        string password;
        string network;
        public string Network
        {
            get { return network; }
            set { network = value; }
        }
        public string Password
        {
            get { return password; }
            set { password = value; }
        }
        public string Channel
        {
            get { return channel; }
            set { channel = value; }
        }
    }
    public class Server
    {
        string hostName;
        int port;
        string name;
        string groupName;
        public string GroupName
        {
            get { return groupName; }
            set { groupName = value; }
        }
        public string Name
        {
            get { return name; }
            set { name = value; }
        }
        public int Port
        {
            get { return port; }
            set { port = value; }
        }
        public string HostName
        {
            get { return hostName; }
            set { hostName = value; }
        }
    }
    public class NickInformation
    {
        string[] nicks = new string[] { };
        string userName;
        string realName;
        string identity;
        string nickServPass;
        List<Perform> performs = new List<Perform>();
        public List<Perform> Performs
        {
            get { return performs; }
            set { performs = value; }
        }
        public string NickServPass
        {
            get { return nickServPass; }
            set { nickServPass = value; }
        }
        public string Identity
        {
            get { return identity; }
            set { identity = value; }
        }
        public string RealName
        {
            get { return realName; }
            set { realName = value; }
        }
        public string UserName
        {
            get { return userName; }
            set { userName = value; }
        }
        public string[] Nicks
        {
            get { return nicks; }
            set { nicks = value; }
        }
    }
    public class Perform
    {
        string server;
        string[] perf;
        public string[] Perf
        {
            get { return perf; }
            set { perf = value; }
        }
        public string Server
        {
            get { return server; }
            set { server = value; }
        }
    }
}
