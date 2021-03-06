using System;
using System.Collections;
using System.Xml;
using Altova.Types;
namespace capabilities_1_1_1
{
 public class KeywordListType : Altova.Xml.Node
 {
  public KeywordListType() : base() { SetCollectionParents(); }
  public KeywordListType(XmlDocument doc) : base(doc) { SetCollectionParents(); }
  public KeywordListType(XmlNode node) : base(node) { SetCollectionParents(); }
  public KeywordListType(Altova.Xml.Node node) : base(node) { SetCollectionParents(); }
  public override void AdjustPrefix()
  {
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "Keyword"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "Keyword", i);
    InternalAdjustPrefix(DOMNode, false);
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
   return DomChildCount(NodeType.Element, "", "Keyword");
  }
  public int KeywordCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "Keyword");
   }
  }
  public bool HasKeyword()
  {
   return HasDomChild(NodeType.Element, "", "Keyword");
  }
  public SchemaString GetKeywordAt(int index)
  {
   return new SchemaString(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "Keyword", index)));
  }
  public SchemaString GetKeyword()
  {
   return GetKeywordAt(0);
  }
  public SchemaString Keyword
  {
   get
   {
    return GetKeywordAt(0);
   }
  }
  public void RemoveKeywordAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "Keyword", index);
  }
  public void RemoveKeyword()
  {
   while (HasKeyword())
    RemoveKeywordAt(0);
  }
  public void AddKeyword(SchemaString newValue)
  {
   AppendDomChild(NodeType.Element, "", "Keyword", newValue.ToString());
  }
  public void InsertKeywordAt(SchemaString newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "Keyword", index, newValue.ToString());
  }
  public void ReplaceKeywordAt(SchemaString newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "Keyword", index, newValue.ToString());
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
   public SchemaString Current
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
