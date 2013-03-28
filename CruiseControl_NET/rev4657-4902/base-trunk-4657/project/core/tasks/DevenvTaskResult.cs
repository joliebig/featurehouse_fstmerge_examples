using System.IO;
using System.Xml;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Tasks
{
 public class DevenvTaskResult : ProcessTaskResult
 {
  public DevenvTaskResult(ProcessResult result) :
   base(result){}
  public override string Data
  {
   get { return TransformDevenvOutput(result.StandardOutput, result.StandardError); }
  }
        private static string TransformDevenvOutput(string devenvOutput, string devenvError)
  {
   StringWriter output = new StringWriter();
   XmlWriter writer = new XmlTextWriter(output);
   writer.WriteStartElement("buildresults");
   WriteContent(writer, devenvOutput, false);
            WriteContent(writer, devenvError, true);
            writer.WriteEndElement();
   return output.ToString();
  }
  private static void WriteContent(XmlWriter writer, string messages, bool areErrors)
  {
            StringReader reader = new StringReader(messages);
   while (reader.Peek() >= 0)
   {
    string line = reader.ReadLine();
    if (StringUtil.IsBlank(line))
     continue;
    writer.WriteStartElement("message");
    if (areErrors)
     writer.WriteAttributeString("level", "error");
    writer.WriteValue(StringUtil.RemoveNulls(line));
    writer.WriteEndElement();
   }
  }
 }
}
