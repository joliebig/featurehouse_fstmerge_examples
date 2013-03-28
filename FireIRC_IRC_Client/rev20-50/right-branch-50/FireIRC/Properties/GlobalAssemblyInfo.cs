using System.Reflection;
using System.Runtime.CompilerServices;
using System.Runtime.InteropServices;
[assembly: AssemblyCompany("Odyssey-Vector Technologies")]
[assembly: AssemblyProduct("FireIRC IRC Client")]
[assembly: AssemblyCopyright("Copyright Â© Odyssey-Vector Technologies 2008")]
[assembly: AssemblyTrademark("")]
[assembly: AssemblyCulture("")]
[assembly: AssemblyVersion("2.1.18.2151")]
[assembly: AssemblyFileVersion("2.1.18.2151")]
public struct VersionInformation
{
    public const int Major = 2;
    public const int Minor = 1;
    public const int Revision = 18;
    public const int Build = 2151;
    public const string LabBuild = "Retail";
    public static string ToString()
    {
        return Major.ToString() + "." + Minor.ToString() + "." + Revision.ToString() + "." + Build.ToString() + " (" + LabBuild + ")" ;
    }
}
