using System;
using System.Collections;
using System.Xml;
using Altova.Types;
namespace LayerSet
{
 public class Type_LongitudeCoordinate2 : Altova.Xml.Node
 {
  public Type_LongitudeCoordinate2() : base() { SetCollectionParents(); }
  public Type_LongitudeCoordinate2(XmlDocument doc) : base(doc) { SetCollectionParents(); }
  public Type_LongitudeCoordinate2(XmlNode node) : base(node) { SetCollectionParents(); }
  public Type_LongitudeCoordinate2(Altova.Xml.Node node) : base(node) { SetCollectionParents(); }
  public override void AdjustPrefix()
  {
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "Value"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "Value", i);
    InternalAdjustPrefix(DOMNode, true);
   }
  }
  public int GetValue2MinCount()
  {
   return 1;
  }
  public int Value2MinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetValue2MaxCount()
  {
   return 1;
  }
  public int Value2MaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetValue2Count()
  {
   return DomChildCount(NodeType.Element, "", "Value");
  }
  public int Value2Count
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "Value");
   }
  }
  public bool HasValue2()
  {
   return HasDomChild(NodeType.Element, "", "Value");
  }
  public ValueType4 GetValue2At(int index)
  {
   return new ValueType4(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "Value", index)));
  }
  public XmlNode GetStartingValue2Cursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "Value" );
  }
  public XmlNode GetAdvancedValue2Cursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "Value", curNode );
  }
  public ValueType4 GetValue2ValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new ValueType4( curNode.InnerText );
  }
  public ValueType4 GetValue2()
  {
   return GetValue2At(0);
  }
  public ValueType4 Value2
  {
   get
   {
    return GetValue2At(0);
   }
  }
  public void RemoveValue2At(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "Value", index);
  }
  public void RemoveValue2()
  {
   while (HasValue2())
    RemoveValue2At(0);
  }
  public void AddValue2(ValueType4 newValue)
  {
   AppendDomChild(NodeType.Element, "", "Value", newValue.ToString());
  }
  public void InsertValue2At(ValueType4 newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "Value", index, newValue.ToString());
  }
  public void ReplaceValue2At(ValueType4 newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "Value", index, newValue.ToString());
  }
        public Value2Collection MyValue2s = new Value2Collection( );
        public class Value2Collection: IEnumerable
        {
            Type_LongitudeCoordinate2 parent;
            public Type_LongitudeCoordinate2 Parent
   {
    set
    {
     parent = value;
    }
   }
   public Value2Enumerator GetEnumerator()
   {
    return new Value2Enumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class Value2Enumerator: IEnumerator
        {
   int nIndex;
   Type_LongitudeCoordinate2 parent;
   public Value2Enumerator(Type_LongitudeCoordinate2 par)
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
    return(nIndex < parent.Value2Count );
   }
   public ValueType4 Current
   {
    get
    {
     return(parent.GetValue2At(nIndex));
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
            MyValue2s.Parent = this;
 }
}
}
