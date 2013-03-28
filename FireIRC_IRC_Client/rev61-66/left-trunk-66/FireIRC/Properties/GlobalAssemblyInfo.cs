using System.Reflection;
using System.Runtime.CompilerServices;
using System.Runtime.InteropServices;
[assembly: AssemblyCompany("Odyssey-Vector Technologies")]
[assembly: AssemblyProduct("Shion IRC Client")]
[assembly: AssemblyCopyright("Copyright Â© Odyssey-Vector Technologies 2008")]
[assembly: AssemblyTrademark("")]
[assembly: AssemblyCulture("")]
[assembly: AssemblyVersion("5.0.1.0")]
[assembly: AssemblyFileVersion("5.0.1.0")]
public struct VersionInformation
{
    public const int Major = 5;
    public const int Minor = 0;
    public const int Revision = 1;
    public const int Build = 0;
    public const string LabBuild = "shion_lab2";
    public static string ToString()
    {
        return Major.ToString() + "." + Minor.ToString() + "." + Revision.ToString() + "." + Build.ToString() + " (" + LabBuild + ")" ;
    }
}
