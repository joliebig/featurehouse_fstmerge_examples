using System;
using System.Collections;
using System.Xml;
using Altova.Types;
namespace capabilities_1_3_0.wms
{
 public class KeywordType : Altova.Xml.Node
 {
  public KeywordType() : base() { SetCollectionParents(); }
  public KeywordType(XmlDocument doc) : base(doc) { SetCollectionParents(); }
  public KeywordType(XmlNode node) : base(node) { SetCollectionParents(); }
  public KeywordType(Altova.Xml.Node node) : base(node) { SetCollectionParents(); }
  public SchemaString GetValue()
  {
   return new SchemaString(GetDomNodeValue(domNode));
  }
  public void SetValue(ISchemaType newValue)
  {
   SetDomNodeValue(domNode, newValue.ToString());
  }
  public SchemaString Value
  {
   get
   {
    return new SchemaString(GetDomNodeValue(domNode));
   }
   set
   {
    SetDomNodeValue(domNode, value.ToString());
   }
  }
  public override void AdjustPrefix()
  {
   for (int i = 0; i < DomChildCount(NodeType.Attribute, "", "vocabulary"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Attribute, "", "vocabulary", i);
    InternalAdjustPrefix(DOMNode, false);
   }
  }
  public int GetvocabularyMinCount()
  {
   return 0;
  }
  public int vocabularyMinCount
  {
   get
   {
    return 0;
   }
  }
  public int GetvocabularyMaxCount()
  {
   return 1;
  }
  public int vocabularyMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetvocabularyCount()
  {
   return DomChildCount(NodeType.Attribute, "", "vocabulary");
  }
  public int vocabularyCount
  {
   get
   {
    return DomChildCount(NodeType.Attribute, "", "vocabulary");
   }
  }
  public bool Hasvocabulary()
  {
   return HasDomChild(NodeType.Attribute, "", "vocabulary");
  }
  public SchemaString GetvocabularyAt(int index)
  {
   return new SchemaString(GetDomNodeValue(GetDomChildAt(NodeType.Attribute, "", "vocabulary", index)));
  }
  public SchemaString Getvocabulary()
  {
   return GetvocabularyAt(0);
  }
  public SchemaString vocabulary
  {
   get
   {
    return GetvocabularyAt(0);
   }
  }
  public void RemovevocabularyAt(int index)
  {
   RemoveDomChildAt(NodeType.Attribute, "", "vocabulary", index);
  }
  public void Removevocabulary()
  {
   while (Hasvocabulary())
    RemovevocabularyAt(0);
  }
  public void Addvocabulary(SchemaString newValue)
  {
   AppendDomChild(NodeType.Attribute, "", "vocabulary", newValue.ToString());
  }
  public void InsertvocabularyAt(SchemaString newValue, int index)
  {
   InsertDomChildAt(NodeType.Attribute, "", "vocabulary", index, newValue.ToString());
  }
  public void ReplacevocabularyAt(SchemaString newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Attribute, "", "vocabulary", index, newValue.ToString());
  }
        public vocabularyCollection Myvocabularys = new vocabularyCollection( );
        public class vocabularyCollection: IEnumerable
        {
            KeywordType parent;
            public KeywordType Parent
   {
    set
    {
     parent = value;
    }
   }
   public vocabularyEnumerator GetEnumerator()
   {
    return new vocabularyEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class vocabularyEnumerator: IEnumerator
        {
   int nIndex;
   KeywordType parent;
   public vocabularyEnumerator(KeywordType par)
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
    return(nIndex < parent.vocabularyCount );
   }
   public SchemaString Current
   {
    get
    {
     return(parent.GetvocabularyAt(nIndex));
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
            Myvocabularys.Parent = this;
 }
}
}
