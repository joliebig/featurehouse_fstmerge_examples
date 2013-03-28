using System;
using System.Reflection;
namespace NewsComponents.Utils
{
    public sealed class ExceptionHelper
    {
        private static readonly MethodInfo PreserveException = typeof(Exception).GetMethod("InternalPreserveStackTrace", BindingFlags.Instance | BindingFlags.NonPublic);
        public static void PreserveExceptionStackTrace(Exception e)
        {
            if (e == null)
                throw new ArgumentNullException("e");
            PreserveException.Invoke(e, null);
        }
    }
}
