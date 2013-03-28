using System.Collections;
using System.Configuration;
using System.Text;
using System.Xml.XPath;
using ThoughtWorks.CruiseControl.Core.Util;
namespace ThoughtWorks.CruiseControl.Core.Publishers
{
 public class BuildLogTransformer
 {
  public string TransformResultsWithAllStyleSheets(XPathDocument document)
  {
   IList list = (IList) ConfigurationManager.GetSection("xslFiles");
   return TransformResults(list, document);
  }
  public string TransformResults(IList xslFiles, XPathDocument document)
  {
   StringBuilder builder = new StringBuilder();
   if (xslFiles == null)
    return builder.ToString();
   XslTransformer transformer = new XslTransformer();
   foreach (string xslFile in xslFiles)
   {
                Log.Trace("Transforming using file : {0}",xslFile);
    builder.Append(transformer.TransformToXml(xslFile, document));
   }
   return builder.ToString();
  }
 }
}
