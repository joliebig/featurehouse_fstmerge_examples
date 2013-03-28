using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Text.RegularExpressions;
namespace OVT.FireIRC.Resources
{
    class mIRCServerListConverter
    {
        public static List<string> MakeFireIRCServerList(string pathofmIRCServerIni)
        {
            System.Net.WebClient c = new System.Net.WebClient();
            bool beginAdd = false;
            List<String> pd = new List<string>();
            Stream x = c.OpenRead(pathofmIRCServerIni);
            StreamReader r = new StreamReader(x);
            List<string> ren = new List<string>();
            string md = r.ReadToEnd();
            string[] m = md.Replace("\r", "").Split('\n');
            Regex rex = new Regex("n([0-9]{1,10})\\=(.*)SERVER\\:([^\\s]+):(.*)GROUP:(.*)");
            foreach (string i in m)
            {
                if (beginAdd == false)
                {
                    if (i == "[servers]")
                    {
                        beginAdd = true;
                    }
                }
                else
                {
                    if (i != "") { pd.Add(i); }
                    else { beginAdd = false; }
                }
            }
            foreach (string i in pd)
            {
                string[] p = rex.Split(i);
                p[1] = "";
                String o = String.Join(":", p);
                o = o.Remove(0, 2).Remove(o.Length - 3, 1);
                ren.Add(o);
            }
            return ren;
        }
        public static List<Server> ConvertListToServers(List<string> input)
        {
            List<Server> Servers = new List<Server>();
            foreach (string i in input)
            {
                string[] bd = i.Split(':');
                Server b = new Server();
                b.Name = bd[0];
                b.HostName = bd[1];
                b.Port = 6667;
                b.GroupName = bd[3];
                Servers.Add(b);
            }
            return Servers;
        }
        public static void AddServerListToServers(List<Server> bi, Settings s)
        {
            foreach (Server i in bi)
            {
                s.Servers.Add(i);
            }
        }
    }
}
