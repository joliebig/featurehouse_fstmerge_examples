using System;
using System.Collections.Generic;
using System.Text;
using System.Management;
namespace OVT.FireIRC.Resources
{
    public class SystemInfo
    {
        public static string GetSystemInformation()
        {
            string systemInformation = "[Operating System] " + Environment.OSVersion.ToString() + " ";
            systemInformation += GetCPUInformation();
            systemInformation += GetRamInformation();
            systemInformation += GetVideoInformation();
            systemInformation += GetHDInformation();
            systemInformation += "[Uptime] " + GetUptime().ToString();
            return systemInformation;
        }
        static string GetRamInformation()
        {
            string systemInformation = "";
            ObjectQuery winQuery = new ObjectQuery("SELECT * FROM Win32_OperatingSystem");
            ManagementObjectSearcher searcher = new ManagementObjectSearcher(winQuery);
            foreach (ManagementObject item in searcher.Get())
            {
                systemInformation += "[Physical Memory] " + (ulong)item["TotalVisibleMemorySize"] / 1024 + " MB ";
            }
            return systemInformation;
        }
        static string GetCPUInformation()
        {
            string systemInformation = "";
            ObjectQuery winQuery = new ObjectQuery("SELECT * FROM Win32_Processor");
            ManagementObjectSearcher searcher = new ManagementObjectSearcher(winQuery);
            foreach (ManagementObject item in searcher.Get())
            {
                systemInformation += "[Processor] " + item["Name"] + " ";
            }
            return systemInformation;
        }
        static string GetVideoInformation()
        {
            string systemInformation = "";
            ObjectQuery winQuery = new ObjectQuery("SELECT * FROM Win32_VideoController");
            ManagementObjectSearcher searcher = new ManagementObjectSearcher(winQuery);
            foreach (ManagementObject item in searcher.Get())
            {
                systemInformation += "[Video Controller] " + item["Name"] + " @ " + (uint)item["AdapterRAM"] / 1048576 + " MB ";
            }
            return systemInformation;
        }
        static string GetHDInformation()
        {
            string systemInformation = "";
            ObjectQuery winQuery = new ObjectQuery("SELECT * FROM Win32_DiskDrive");
            ManagementObjectSearcher searcher = new ManagementObjectSearcher(winQuery);
            int i = 1;
            foreach (ManagementObject item in searcher.Get())
            {
                if (item["Size"] != null)
                {
                    systemInformation += "[Disk " + i + "] " + (ulong)item["Size"] / 1048576 + " MB ";
                    i++;
                }
            }
            return systemInformation;
        }
        static string GetUptime()
        {
            TimeSpan uptimeTs = new TimeSpan();
            ManagementClass management = new ManagementClass("Win32_OperatingSystem");
            ManagementObjectCollection mngInstance = management.GetInstances();
            foreach (ManagementObject mngObject in mngInstance)
            {
                DateTime lastBootUp = ParseCIMDateTime(mngObject["LastBootUpTime"].ToString());
                if (lastBootUp != DateTime.MinValue)
                {
                    uptimeTs = DateTime.Now - lastBootUp;
                }
            }
            return String.Format("{0}d,{1}h,{2}m,{3}s,{4}ms", uptimeTs.Days, uptimeTs.Hours, uptimeTs.Minutes, uptimeTs.Seconds, uptimeTs.Milliseconds);
        }
        private static DateTime ParseCIMDateTime(string wmiDate)
        {
            DateTime date = DateTime.MinValue;
            if (wmiDate != null && wmiDate.IndexOf('.') != -1)
            {
                string tempDate = wmiDate.Substring(0, wmiDate.IndexOf('.') + 4);
                if (tempDate.Length == 18)
                {
                    int year = Convert.ToInt32(tempDate.Substring(0, 4));
                    int month = Convert.ToInt32(tempDate.Substring(4, 2));
                    int day = Convert.ToInt32(tempDate.Substring(6, 2));
                    int hour = Convert.ToInt32(tempDate.Substring(8, 2));
                    int minute = Convert.ToInt32(tempDate.Substring(10, 2));
                    int second = Convert.ToInt32(tempDate.Substring(12, 2));
                    int milisecond = Convert.ToInt32(tempDate.Substring(15, 3));
                    date = new DateTime(year, month, day, hour, minute, second, milisecond);
                }
            }
            return date;
        }
    }
}
