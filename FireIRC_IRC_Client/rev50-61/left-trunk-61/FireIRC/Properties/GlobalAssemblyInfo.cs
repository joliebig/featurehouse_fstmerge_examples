using System.Reflection;
using System.Runtime.CompilerServices;
using System.Runtime.InteropServices;
[assembly: AssemblyCompany("Odyssey-Vector Technologies")]
[assembly: AssemblyProduct("FireIRC IRC Client")]
[assembly: AssemblyCopyright("Copyright Â© Odyssey-Vector Technologies 2008")]
[assembly: AssemblyTrademark("")]
[assembly: AssemblyCulture("")]
[assembly: AssemblyVersion("4.0.19.2386")]
[assembly: AssemblyFileVersion("4.0.19.2386")]
public struct VersionInformation
{
    public const int Major = 4;
    public const int Minor = 0;
    public const int Revision = 19;
    public const int Build = 2386;
    public const string LabBuild = "fireirc_lab1_johannah";
    public static string ToString()
    {
        return Major.ToString() + "." + Minor.ToString() + "." + Revision.ToString() + "." + Build.ToString() + " (" + LabBuild + ")" ;
    }
}
