using System;
namespace capabilities_1_3_0
{
 public class capabilities_1_3_0Doc : Altova.Xml.Document
 {
  override protected void DeclareNamespaces(Altova.Xml.Node node)
  {
   DeclareNamespace(node, "wms", "http://www.opengis.net/wms");
   DeclareNamespace(node, "xlink", "http://www.w3.org/1999/xlink");
  }
 }
}
