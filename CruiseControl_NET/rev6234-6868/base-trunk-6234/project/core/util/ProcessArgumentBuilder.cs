using System.Text;
using System.Text.RegularExpressions;
namespace ThoughtWorks.CruiseControl.Core.Util
{
 public class ProcessArgumentBuilder
 {
        private static Regex hiddenTextRegex = new Regex("<hide>[^<]*</hide>");
        private readonly StringBuilder builder = new StringBuilder();
        public void AppendArgument(string format, string value)
  {
   if (string.IsNullOrEmpty(value)) return;
   AppendSpaceIfNotEmpty();
   builder.AppendFormat(format, value);
  }
        public void AppendHiddenArgument(string format, string value)
        {
            if (string.IsNullOrEmpty(value)) return;
            AppendArgument(format, HideArgument(value));
        }
        public void AppendArgument(string value)
  {
            if (string.IsNullOrEmpty(value)) return;
   AppendSpaceIfNotEmpty();
   builder.Append(value);
  }
        public void AppendHiddenArgument(string value)
        {
            if (string.IsNullOrEmpty(value)) return;
            AppendArgument(HideArgument(value));
        }
  private void AppendSpaceIfNotEmpty()
  {
   if (builder.Length > 0) builder.Append(" ");
  }
        public void AppendIf(bool condition, string value)
  {
   if (condition) AppendArgument(value);
  }
        public void AppendIf(bool condition, string format, string argument)
  {
   if (condition) AppendArgument(format, argument);
  }
  public void Append(string text)
  {
   builder.Append(text);
  }
        public void AddArgument(string arg, string value)
  {
   AddArgument(arg, " ", value);
  }
        public void AddArgument(string arg, string separator, string value)
  {
            if (string.IsNullOrEmpty(value)) return;
   AppendSpaceIfNotEmpty();
   builder.Append(string.Format("{0}{1}{2}", arg, separator, StringUtil.AutoDoubleQuoteString(value)));
  }
  public void AddArgument(string value)
  {
            if (string.IsNullOrEmpty(value)) return;
   AppendSpaceIfNotEmpty();
   builder.Append(StringUtil.AutoDoubleQuoteString(value));
  }
        public void AddHiddenArgument(string arg, string value)
        {
            if (string.IsNullOrEmpty(value)) return;
            AddArgument(arg, " ", HideArgument(value));
        }
        public void AddHiddenArgument(string arg, string separator, string value)
        {
            if (string.IsNullOrEmpty(value)) return;
            AddArgument(arg, separator, HideArgument(value));
        }
        public void AddHiddenArgument(string value)
        {
            if (string.IsNullOrEmpty(value)) return;
            AddArgument(HideArgument(value));
        }
  public override string ToString()
  {
   return builder.ToString();
  }
        public static string HideArgument(string value)
        {
            return string.Format("<hide>{0}</hide>", value);
        }
        public static string GenerateSanitisedArguments(string arguments)
        {
            var value = hiddenTextRegex.Replace(arguments, (m => new string('#', 5)));
            return value;
        }
        public static string GenerateApplicationArguments(string arguments)
        {
            var value = hiddenTextRegex.Replace(arguments,
                (m => m.Value.Substring(6, m.Value.Length - 13)));
            return value;
        }
    }
}
