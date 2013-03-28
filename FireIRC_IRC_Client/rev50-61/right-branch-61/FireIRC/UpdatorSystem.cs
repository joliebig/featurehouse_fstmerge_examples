using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
using System.IO;
namespace FireIRC
{
    public class UpdatorSystem
    {
        static bool updatorChecked = false;
        static bool monoMode = false;
        public static bool MonoMode
        {
            get { return UpdatorSystem.monoMode; }
            set { UpdatorSystem.monoMode = value; }
        }
        static string installPath = typeof(UpdatorSystem).Assembly.Location;
        public static string InstallPath
        {
            get
            {
                string[] i = installPath.Split('\\');
                i[i.GetUpperBound(0)] = "";
                string m = String.Join("\\", i);
                m = m.Remove(m.Length - 1, 1);
                return m;
            }
            set { UpdatorSystem.installPath = value; }
        }
        public static bool UpdatorChecked
        {
            get { return UpdatorSystem.updatorChecked; }
            set { UpdatorSystem.updatorChecked = value; }
        }
        public static FireIRCUpdatePackage GetPackage()
        {
            XmlSerializer x = new XmlSerializer(typeof(FireIRCUpdatePackage));
            FileStream ps = File.Open("fireirc.pak", FileMode.Open);
            FireIRCUpdatePackage pdm = (FireIRCUpdatePackage)x.Deserialize(ps);
            ps.Close();
            return pdm;
        }
    }
}
