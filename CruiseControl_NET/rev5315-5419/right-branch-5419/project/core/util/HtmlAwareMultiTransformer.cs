using System.Collections;
using System.Text;
namespace ThoughtWorks.CruiseControl.Core.Util
{
 public class HtmlAwareMultiTransformer : IMultiTransformer
 {
  private readonly ITransformer delegateTransformer;
  public HtmlAwareMultiTransformer(ITransformer delegateTransformer)
  {
   this.delegateTransformer = delegateTransformer;
  }
  public string Transform(string input, string[] transformerFileNames, Hashtable xsltArgs)
  {
   StringBuilder builder = new StringBuilder();
   foreach (string transformerFileName in transformerFileNames)
   {
    builder.Append(delegateTransformer.Transform(input, transformerFileName, xsltArgs));
    builder.Append("<br/>");
   }
   return builder.ToString();
  }
 }
}
