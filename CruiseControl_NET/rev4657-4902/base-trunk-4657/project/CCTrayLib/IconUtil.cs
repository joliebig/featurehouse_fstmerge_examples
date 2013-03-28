using System.Drawing;
using System.IO;
using System.Reflection;
namespace ThoughtWorks.CruiseControl.CCTrayLib
{
    public static class IconUtil
    {
        public static Icon LoadFromStream(Stream stream)
        {
            using (Bitmap bmp = (Bitmap)Image.FromStream(stream))
                return Icon.FromHandle(bmp.GetHicon());
        }
        public static Icon LoadFromResource(string resourceName)
        {
            return LoadFromStream(Assembly.GetExecutingAssembly().GetManifestResourceStream(resourceName));
        }
    }
}
