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
        NickInformation nickInfo = new NickInformation();
        List<Perform> performs = new List<Perform>();
        List<string> highlightRegEx = new List<string>();
        bool highlightsEnabled;
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
        public List<Perform> Performs
        {
            get { return performs; }
            set { performs = value; }
        }
        public NickInformation NickInfo
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
        string nick1;
        string nick2;
        string nick3;
        string userName;
        string realName;
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
        public string Nick3
        {
            get { return nick3; }
            set { nick3 = value; }
        }
        public string Nick2
        {
            get { return nick2; }
            set { nick2 = value; }
        }
        public string Nick1
        {
            get { return nick1; }
            set { nick1 = value; }
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
