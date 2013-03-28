using System;
using System.Diagnostics;
using System.IO;
namespace ThoughtWorks.CruiseControl.Core.Util
{
    class KillUtil
    {
        public const string WIN2K_SUPPORT_TOOLS_DIR = @"C:\\Program Files\\Support Tools";
        public static void KillPid(int pid)
        {
            Process process = new Process();
            string platform = string.Empty;
            switch (Environment.OSVersion.Platform)
            {
                case PlatformID.Win32NT:
                    if ((Environment.OSVersion.Version.Major == 5) && (Environment.OSVersion.Version.Minor == 0))
                    {
                        process.StartInfo.FileName = string.Format("{0}\\kill.exe", WIN2K_SUPPORT_TOOLS_DIR);
                        process.StartInfo.Arguments = string.Format("-f {0}", pid);
                        platform = "Windows";
                        break;
                    }
                    else
                    {
                        process.StartInfo.FileName = string.Format("{0}\\taskkill.exe",
                                Environment.GetFolderPath(Environment.SpecialFolder.System));
                        process.StartInfo.Arguments = string.Format("/pid {0} /t /f", pid);
                        platform = "Windows";
                        break;
                    }
               case PlatformID.Unix:
                    Process unameprocess = new Process();
                    process.StartInfo.FileName = "uname";
                    process.StartInfo.Arguments = "-s";
                    process.StartInfo.CreateNoWindow = true;
                    process.StartInfo.RedirectStandardOutput = true;
                    process.Start();
                    process.WaitForExit();
                    StreamReader soReader = process.StandardOutput;
                    string output = soReader.ReadToEnd();
                    int nRet = process.ExitCode;
                    process.Close();
                    if ((nRet == 0) && (output.Contains("Darwin")))
                    {
                        process.StartInfo.FileName = "/bin/kill";
                        process.StartInfo.Arguments = string.Format("-9 {0}", pid);
                        platform = "Mac";
                    }
                    else
                    {
                        process.StartInfo.FileName = "/usr/bin/pkill";
                        process.StartInfo.Arguments = string.Format("-9 -g {0}", pid);
                        platform = "Unix";
                    }
                    break;
                default:
                    throw new Exception("Unknown Operating System.");
            }
            if (!File.Exists(process.StartInfo.FileName))
            {
                throw new Exception(string.Format("Kill command {0} not found on {1} OS. PID:{2}",
                                                  process.StartInfo.FileName, platform, Convert.ToString(pid)));
            }
            process.Start();
            process.WaitForExit();
            process.Close();
        }
    }
}
