using System;
using System.Collections;
using System.Xml;
using Altova.Types;
namespace capabilities_1_3_0.wms
{
 public class KeywordListType : Altova.Xml.Node
 {
  public KeywordListType() : base() { SetCollectionParents(); }
  public KeywordListType(XmlDocument doc) : base(doc) { SetCollectionParents(); }
  public KeywordListType(XmlNode node) : base(node) { SetCollectionParents(); }
  public KeywordListType(Altova.Xml.Node node) : base(node) { SetCollectionParents(); }
  public override void AdjustPrefix()
  {
   for (int i = 0; i < DomChildCount(NodeType.Element, "http://www.opengis.net/wms", "Keyword"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "http://www.opengis.net/wms", "Keyword", i);
    InternalAdjustPrefix(DOMNode, true);
    new KeywordType(DOMNode).AdjustPrefix();
   }
  }
  public int GetKeywordMinCount()
  {
   return 0;
  }
  public int KeywordMinCount
  {
   get
   {
    return 0;
   }
  }
  public int GetKeywordMaxCount()
  {
   return Int32.MaxValue;
  }
  public int KeywordMaxCount
  {
   get
   {
    return Int32.MaxValue;
   }
  }
  public int GetKeywordCount()
  {
   return DomChildCount(NodeType.Element, "http://www.opengis.net/wms", "Keyword");
  }
  public int KeywordCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "http://www.opengis.net/wms", "Keyword");
   }
  }
  public bool HasKeyword()
  {
   return HasDomChild(NodeType.Element, "http://www.opengis.net/wms", "Keyword");
  }
  public KeywordType GetKeywordAt(int index)
  {
   return new KeywordType(GetDomChildAt(NodeType.Element, "http://www.opengis.net/wms", "Keyword", index));
  }
  public KeywordType GetKeyword()
  {
   return GetKeywordAt(0);
  }
  public KeywordType Keyword
  {
   get
   {
    return GetKeywordAt(0);
   }
  }
  public void RemoveKeywordAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "http://www.opengis.net/wms", "Keyword", index);
  }
  public void RemoveKeyword()
  {
   while (HasKeyword())
    RemoveKeywordAt(0);
  }
  public void AddKeyword(KeywordType newValue)
  {
   AppendDomElement("http://www.opengis.net/wms", "Keyword", newValue);
  }
  public void InsertKeywordAt(KeywordType newValue, int index)
  {
   InsertDomElementAt("http://www.opengis.net/wms", "Keyword", index, newValue);
  }
  public void ReplaceKeywordAt(KeywordType newValue, int index)
  {
   ReplaceDomElementAt("http://www.opengis.net/wms", "Keyword", index, newValue);
  }
        public KeywordCollection MyKeywords = new KeywordCollection( );
        public class KeywordCollection: IEnumerable
        {
            KeywordListType parent;
            public KeywordListType Parent
   {
    set
    {
     parent = value;
    }
   }
   public KeywordEnumerator GetEnumerator()
   {
    return new KeywordEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class KeywordEnumerator: IEnumerator
        {
   int nIndex;
   KeywordListType parent;
   public KeywordEnumerator(KeywordListType par)
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
    return(nIndex < parent.KeywordCount );
   }
   public KeywordType Current
   {
    get
    {
     return(parent.GetKeywordAt(nIndex));
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
            MyKeywords.Parent = this;
 }
}
}
