using System;
using System.Collections;
using System.Xml;
using Altova.Types;
namespace capabilities_1_1_1
{
 public class DCPTypeType : Altova.Xml.Node
 {
  public DCPTypeType() : base() { SetCollectionParents(); }
  public DCPTypeType(XmlDocument doc) : base(doc) { SetCollectionParents(); }
  public DCPTypeType(XmlNode node) : base(node) { SetCollectionParents(); }
  public DCPTypeType(Altova.Xml.Node node) : base(node) { SetCollectionParents(); }
  public override void AdjustPrefix()
  {
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "HTTP"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "HTTP", i);
    InternalAdjustPrefix(DOMNode, false);
    new HTTPType(DOMNode).AdjustPrefix();
   }
  }
  public int GetHTTPMinCount()
  {
   return 1;
  }
  public int HTTPMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetHTTPMaxCount()
  {
   return 1;
  }
  public int HTTPMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetHTTPCount()
  {
   return DomChildCount(NodeType.Element, "", "HTTP");
  }
  public int HTTPCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "HTTP");
   }
  }
  public bool HasHTTP()
  {
   return HasDomChild(NodeType.Element, "", "HTTP");
  }
  public HTTPType GetHTTPAt(int index)
  {
   return new HTTPType(GetDomChildAt(NodeType.Element, "", "HTTP", index));
  }
  public HTTPType GetHTTP()
  {
   return GetHTTPAt(0);
  }
  public HTTPType HTTP
  {
   get
   {
    return GetHTTPAt(0);
   }
  }
  public void RemoveHTTPAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "HTTP", index);
  }
  public void RemoveHTTP()
  {
   while (HasHTTP())
    RemoveHTTPAt(0);
  }
  public void AddHTTP(HTTPType newValue)
  {
   AppendDomElement("", "HTTP", newValue);
  }
  public void InsertHTTPAt(HTTPType newValue, int index)
  {
   InsertDomElementAt("", "HTTP", index, newValue);
  }
  public void ReplaceHTTPAt(HTTPType newValue, int index)
  {
   ReplaceDomElementAt("", "HTTP", index, newValue);
  }
        public HTTPCollection MyHTTPs = new HTTPCollection( );
        public class HTTPCollection: IEnumerable
        {
            DCPTypeType parent;
            public DCPTypeType Parent
   {
    set
    {
     parent = value;
    }
   }
   public HTTPEnumerator GetEnumerator()
   {
    return new HTTPEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class HTTPEnumerator: IEnumerator
        {
   int nIndex;
   DCPTypeType parent;
   public HTTPEnumerator(DCPTypeType par)
   {
    parent = par;
    nIndex = -1;
   }
   public void Reset()
   {
    nIndex = -1;
   }
   public bool MoveNext()
   {
    nIndex++;
    return(nIndex < parent.HTTPCount );
   }
   public HTTPType Current
   {
    get
    {
     return(parent.GetHTTPAt(nIndex));
    }
   }
   object IEnumerator.Current
   {
    get
    {
     return(Current);
    }
   }
     }
        private void SetCollectionParents()
        {
            MyHTTPs.Parent = this;
 }
}
}
