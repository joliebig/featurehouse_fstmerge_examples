using System;
namespace onlyconnect
{
 internal class CSnap: IHTMLEditHost
 {
  public int SnapRect(IHTMLElement pIElement,
   RECT rect,
   ELEMENT_CORNER ehandle
   )
  {
   Console.WriteLine ("SnapRect called");
   int hr = HRESULT.S_OK;
   return hr;
  }
 }
}
