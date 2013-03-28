using System.Diagnostics;
namespace WikiFunctions
{
    public class Profiler
    {
            [Conditional("DEBUG")]
            public void Profile(string message) { }
            [Conditional("DEBUG")]
            public void Flush() { }
    }
}
