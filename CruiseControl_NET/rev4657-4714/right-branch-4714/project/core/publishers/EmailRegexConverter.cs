using System.Text.RegularExpressions;
using Exortech.NetReflector;
namespace ThoughtWorks.CruiseControl.Core.Publishers
{
    [ReflectorType("regexConverter")]
 public class EmailRegexConverter : IEmailConverter
 {
        private string find;
        private string replace;
        [ReflectorProperty("find")]
        public string Find
        {
            get { return find; }
            set { find = value; }
        }
        [ReflectorProperty("replace")]
        public string Replace
        {
            get { return replace; }
            set { replace = value; }
        }
  public EmailRegexConverter()
  {
  }
  public EmailRegexConverter(string find, string replace)
  {
   this.find = find;
   this.replace = replace;
  }
  public string Convert(string username)
  {
      return Regex.Replace(username, find, replace);
  }
 }
}
