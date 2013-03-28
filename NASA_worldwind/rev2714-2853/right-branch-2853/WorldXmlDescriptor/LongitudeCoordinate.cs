using System;
using System.Collections;
using System.Xml;
using Altova.Types;
namespace WorldXmlDescriptor
{
 public class LongitudeCoordinate : Altova.Xml.Node
 {
  public LongitudeCoordinate() : base() { SetCollectionParents(); }
  public LongitudeCoordinate(XmlDocument doc) : base(doc) { SetCollectionParents(); }
  public LongitudeCoordinate(XmlNode node) : base(node) { SetCollectionParents(); }
  public LongitudeCoordinate(Altova.Xml.Node node) : base(node) { SetCollectionParents(); }
  public override void AdjustPrefix()
  {
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "Value"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "Value", i);
    InternalAdjustPrefix(DOMNode, true);
   }
  }
  public int GetValueMinCount()
  {
   return 1;
  }
  public int ValueMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetValueMaxCount()
  {
   return 1;
  }
  public int ValueMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetValueCount()
  {
   return DomChildCount(NodeType.Element, "", "Value");
  }
  public int ValueCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "Value");
   }
  }
  public bool HasValue()
  {
   return HasDomChild(NodeType.Element, "", "Value");
  }
  public ValueType2 GetValueAt(int index)
  {
   return new ValueType2(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "Value", index)));
  }
  public ValueType2 GetValue()
  {
   return GetValueAt(0);
  }
  public ValueType2 Value
  {
   get
   {
    return GetValueAt(0);
   }
  }
  public void RemoveValueAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "Value", index);
  }
  public void RemoveValue()
  {
   while (HasValue())
    RemoveValueAt(0);
  }
  public void AddValue(ValueType2 newValue)
  {
   AppendDomChild(NodeType.Element, "", "Value", newValue.ToString());
  }
  public void InsertValueAt(ValueType2 newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "Value", index, newValue.ToString());
  }
  public void ReplaceValueAt(ValueType2 newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "Value", index, newValue.ToString());
  }
        public ValueCollection MyValues = new ValueCollection( );
        public class ValueCollection: IEnumerable
        {
            LongitudeCoordinate parent;
            public LongitudeCoordinate Parent
   {
    set
    {
     parent = value;
    }
   }
   public ValueEnumerator GetEnumerator()
   {
    return new ValueEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class ValueEnumerator: IEnumerator
        {
   int nIndex;
   LongitudeCoordinate parent;
   public ValueEnumerator(LongitudeCoordinate par)
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
    return(nIndex < parent.ValueCount );
   }
   public ValueType2 Current
   {
    get
    {
     return(parent.GetValueAt(nIndex));
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
            MyValues.Parent = this;
 }
}
}
