using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
namespace UpdateGlobalAssembly
{
    class Program
    {
        static string Format = @"using System.Reflection;
using System.Runtime.CompilerServices;
using System.Runtime.InteropServices;
[assembly: AssemblyCompany(""Odyssey-Vector Technologies"")]
[assembly: AssemblyProduct(""{2}"")]
[assembly: AssemblyCopyright(""Copyright Â© Odyssey-Vector Technologies 2008"")]
[assembly: AssemblyTrademark("""")]
[assembly: AssemblyCulture("""")]
[assembly: AssemblyVersion(""{0}"")]
[assembly: AssemblyFileVersion(""{0}"")]
{1}";
        static string gaip;
        static string vtp;
        static string productName;
        static string labData;
        public static string ProductName
        {
            get { return Program.productName; }
            set { Program.productName = value; }
        }
        static void Main(string[] args)
        {
            try
            {
                GlobalAssemblyInfoFilePath = args[0];
                VersionTxTPath = args[1];
                ProductName = args[2];
                LabData = args[3];
                string version = File.ReadAllText(VersionTxTPath);
                string[] version_Split = version.Split('.');
                int build = Convert.ToInt32(version_Split[3]);
                build += 1;
                version_Split[3] = build.ToString();
                Version ver = new Version(String.Join(".", version_Split));
                File.WriteAllText(VersionTxTPath, ver.ToString());
                VersionDataAssembly v = new VersionDataAssembly(ver, LabData);
                File.WriteAllText(GlobalAssemblyInfoFilePath, String.Format(Format, v.ToString(), v.ToClassDefinition(), ProductName));
            }
            catch (IndexOutOfRangeException)
            {
                Console.WriteLine("Global Assembly Version Updator");
                Console.WriteLine("(C) 2008 Odyssey-Vector Technologies");
                Console.WriteLine("GlobalAssembly.exe [CS Global Version Document] [Version File] [Product Name] [Lab Version]");
            }
        }
        public static string GlobalAssemblyInfoFilePath
        {
            get { return gaip; }
            set { gaip = value; }
        }
        public static string VersionTxTPath
        {
            get { return vtp; }
            set { vtp = value; }
        }
        public static string LabData
        {
            get { return labData; }
            set { labData = value; }
        }
    }
    public struct VersionDataAssembly
    {
        public VersionDataAssembly(Version v, string labVersion)
        {
            Major = v.Major;
            Minor = v.Minor;
            Revision = v.Revision;
            Build = v.Build;
            LabVersion = labVersion;
        }
        public int Major;
        public int Minor;
        public int Revision;
        public int Build;
        public string LabVersion;
        public override string ToString()
        {
            return Major.ToString() + "." + Minor.ToString() + "." + Build.ToString() + "." + Revision.ToString();
        }
        public string ToClassDefinition()
        {
            string Format = @"public struct VersionInformation
[
    public const int Major = {0};
    public const int Minor = {1};
    public const int Revision = {2};
    public const int Build = {3};
    public const string LabBuild = ""{4}"";
    public static string ToString()
    [
        return Major.ToString() + ""."" + Minor.ToString() + ""."" + Revision.ToString() + ""."" + Build.ToString() + "" ("" + LabBuild + "")"" ;
    ]
]";
            return String.Format(Format, new object[] { Major, Minor, Build, Revision, LabVersion }).Replace("[", "{").Replace("]", "}");
        }
    }
}
