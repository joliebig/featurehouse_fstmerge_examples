using System;
using System.Reflection;
namespace WikiFunctions
{
    public static class Globals
    {
        public static bool RunningOnWindows
        { get { return Windows; } }
        private static readonly bool Windows = Environment.OSVersion.VersionString.Contains("Windows");
        private static readonly bool Mono = Type.GetType("Mono.Runtime") != null;
        public static bool UsingMono
        { get { return Mono; } }
        public static Version WikiFunctionsVersion
        {
            get { return Assembly.GetAssembly(typeof(Variables)).GetName().Version; }
        }
        public static bool UnitTestMode;
        public static int UnitTestIntValue;
        public static bool UnitTestBoolValue;
    }
}
