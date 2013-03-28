using System; 
using System.IO; 
using System.Text.RegularExpressions; 
using System.Xml; namespace  Microsoft.Office.OneNote {
	
 [Serializable] 
 public class  HtmlContent  : OutlineContent {
		
  public  HtmlContent(FileInfo file)
  {
   HtmlData = new FileData(file);
  }
 
  public  HtmlContent(String html)
  {
   HtmlData = new StringData(html);
  }
 
  public  HtmlContent(HtmlContent clone)
  {
   HtmlData = clone.HtmlData;
  }
 
  public override  object Clone()
  {
   return new HtmlContent(this);
  }
 
  protected internal override  void SerializeToXml(XmlNode parentNode)
  {
   XmlDocument xmlDocument = parentNode.OwnerDocument;
   XmlElement htmlElement = xmlDocument.CreateElement("Html");
   parentNode.AppendChild(htmlElement);
   if (HtmlData is StringData)
   {
    Data tidyHtmlData = (Data) HtmlData.Clone();
    tidyHtmlData.data = CleanHtml(HtmlData.data);
    tidyHtmlData.SerializeToXml(htmlElement);
   }
   else
   {
    HtmlData.SerializeToXml(htmlElement);
   }
  }
 
  protected internal static  string CleanHtml(string inputText)
  {
   if (inputText.IndexOf("<html") == -1)
   {
    inputText = "<html><body>" + inputText + "</body></html>";
   }
   inputText = inputText.Replace("<p xmlns=\"http://www.w3.org/1999/xhtml\">", "<p>");
   char space = '\xA0';
   inputText = inputText.Replace("&nbsp;", space.ToString());
   inputText = HtmlContent.replacePWhiteSpace.Replace(inputText, "${open}&nbsp;${close}");
   inputText = HtmlContent.replacePWhiteSpaceBetween.Replace(inputText, "${open}<p>&nbsp;</p>${close}");
   foreach (Match match in HtmlContent.replacePreWithBr.Matches(inputText))
   {
    string innerText = match.Groups["inner"].Value;
    string innerTextReplaced = innerText.Replace("\r\n", "<br>");
    innerTextReplaced = innerTextReplaced.Replace("\t", "&nbsp;");
    inputText = inputText.Replace(innerText, innerTextReplaced);
   }
   inputText = Regex.Replace(inputText, @"<pre[^>]*>", "<div style=\"font-family:Courier New>\"");
   inputText = inputText.Replace("</pre>", "</div>");
   inputText = HtmlContent.replaceBr.Replace(inputText, "<p>&nbsp;</p>");
   return inputText;
  }
 
  public  Data HtmlData
  {
   get
   {
    return (Data) GetChild("HtmlData");
   }
   set
   {
    if (value == null)
     throw new ArgumentNullException("HtmlData");
    if (!(value is FileData || value is StringData))
     throw new ArgumentException("Incorrect data type.");
    Data htmlData = HtmlData;
    if (htmlData != null)
     RemoveChild(htmlData);
    AddChild(value, "HtmlData");
   }
  }
 
  private static readonly  Regex replacePWhiteSpaceBetween = new Regex(@"(?<open></p[^>]*>)(?<inner>[^<\w]*)(?<close><p>)",
                                                                      RegexOptions.IgnoreCase | RegexOptions.Compiled);
 
  private static readonly  Regex replacePWhiteSpace = new Regex(@"(?<open>\<p[^>]*>)(?<inner>[^<\w]*)(?<close>\</p>)",
                                                               RegexOptions.IgnoreCase | RegexOptions.Compiled);
 
  private static readonly  Regex replacePreWithBr = new Regex(@"(?<open><pre[^>]*>)(?<inner>(?:\s*([^<]+)\s*)+)(?<close></pre>)",
                                                             RegexOptions.IgnoreCase | RegexOptions.Compiled);
 
  private static readonly  Regex replaceBr = new Regex(@"(?<open><br[^>]*>)(?<inner>[^<\w]*)(?<close><br[^>]*>)",
                                                      RegexOptions.IgnoreCase | RegexOptions.Compiled);

	}

}
