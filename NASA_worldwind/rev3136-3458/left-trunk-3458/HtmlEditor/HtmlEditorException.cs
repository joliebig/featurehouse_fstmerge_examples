using System;
namespace onlyconnect
{
 public class HtmlEditorException : ApplicationException
 {
  public HtmlEditorException ()
  {
  }
  public HtmlEditorException (string message) : base(message)
  {
  }
  public HtmlEditorException(string message,
   Exception inner) : base(message, inner)
  {
  }
 }
}
