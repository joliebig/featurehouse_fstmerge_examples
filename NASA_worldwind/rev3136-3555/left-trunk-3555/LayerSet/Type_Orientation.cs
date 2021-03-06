using System;
using System.Collections;
using System.Xml;
using Altova.Types;
namespace LayerSet
{
 public class Type_Orientation : Altova.Xml.Node
 {
  public Type_Orientation() : base() { SetCollectionParents(); }
  public Type_Orientation(XmlDocument doc) : base(doc) { SetCollectionParents(); }
  public Type_Orientation(XmlNode node) : base(node) { SetCollectionParents(); }
  public Type_Orientation(Altova.Xml.Node node) : base(node) { SetCollectionParents(); }
  public override void AdjustPrefix()
  {
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "RotationX"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "RotationX", i);
    InternalAdjustPrefix(DOMNode, true);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "RotationY"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "RotationY", i);
    InternalAdjustPrefix(DOMNode, true);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "RotationZ"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "RotationZ", i);
    InternalAdjustPrefix(DOMNode, true);
   }
  }
  public int GetRotationXMinCount()
  {
   return 1;
  }
  public int RotationXMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetRotationXMaxCount()
  {
   return 1;
  }
  public int RotationXMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetRotationXCount()
  {
   return DomChildCount(NodeType.Element, "", "RotationX");
  }
  public int RotationXCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "RotationX");
   }
  }
  public bool HasRotationX()
  {
   return HasDomChild(NodeType.Element, "", "RotationX");
  }
  public SchemaDecimal GetRotationXAt(int index)
  {
   return new SchemaDecimal(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "RotationX", index)));
  }
  public XmlNode GetStartingRotationXCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "RotationX" );
  }
  public XmlNode GetAdvancedRotationXCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "RotationX", curNode );
  }
  public SchemaDecimal GetRotationXValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new SchemaDecimal( curNode.InnerText );
  }
  public SchemaDecimal GetRotationX()
  {
   return GetRotationXAt(0);
  }
  public SchemaDecimal RotationX
  {
   get
   {
    return GetRotationXAt(0);
   }
  }
  public void RemoveRotationXAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "RotationX", index);
  }
  public void RemoveRotationX()
  {
   while (HasRotationX())
    RemoveRotationXAt(0);
  }
  public void AddRotationX(SchemaDecimal newValue)
  {
   AppendDomChild(NodeType.Element, "", "RotationX", newValue.ToString());
  }
  public void InsertRotationXAt(SchemaDecimal newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "RotationX", index, newValue.ToString());
  }
  public void ReplaceRotationXAt(SchemaDecimal newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "RotationX", index, newValue.ToString());
  }
        public RotationXCollection MyRotationXs = new RotationXCollection( );
        public class RotationXCollection: IEnumerable
        {
            Type_Orientation parent;
            public Type_Orientation Parent
   {
    set
    {
     parent = value;
    }
   }
   public RotationXEnumerator GetEnumerator()
   {
    return new RotationXEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class RotationXEnumerator: IEnumerator
        {
   int nIndex;
   Type_Orientation parent;
   public RotationXEnumerator(Type_Orientation par)
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
    return(nIndex < parent.RotationXCount );
   }
   public SchemaDecimal Current
   {
    get
    {
     return(parent.GetRotationXAt(nIndex));
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
  public int GetRotationYMinCount()
  {
   return 1;
  }
  public int RotationYMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetRotationYMaxCount()
  {
   return 1;
  }
  public int RotationYMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetRotationYCount()
  {
   return DomChildCount(NodeType.Element, "", "RotationY");
  }
  public int RotationYCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "RotationY");
   }
  }
  public bool HasRotationY()
  {
   return HasDomChild(NodeType.Element, "", "RotationY");
  }
  public SchemaDecimal GetRotationYAt(int index)
  {
   return new SchemaDecimal(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "RotationY", index)));
  }
  public XmlNode GetStartingRotationYCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "RotationY" );
  }
  public XmlNode GetAdvancedRotationYCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "RotationY", curNode );
  }
  public SchemaDecimal GetRotationYValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new SchemaDecimal( curNode.InnerText );
  }
  public SchemaDecimal GetRotationY()
  {
   return GetRotationYAt(0);
  }
  public SchemaDecimal RotationY
  {
   get
   {
    return GetRotationYAt(0);
   }
  }
  public void RemoveRotationYAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "RotationY", index);
  }
  public void RemoveRotationY()
  {
   while (HasRotationY())
    RemoveRotationYAt(0);
  }
  public void AddRotationY(SchemaDecimal newValue)
  {
   AppendDomChild(NodeType.Element, "", "RotationY", newValue.ToString());
  }
  public void InsertRotationYAt(SchemaDecimal newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "RotationY", index, newValue.ToString());
  }
  public void ReplaceRotationYAt(SchemaDecimal newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "RotationY", index, newValue.ToString());
  }
        public RotationYCollection MyRotationYs = new RotationYCollection( );
        public class RotationYCollection: IEnumerable
        {
            Type_Orientation parent;
            public Type_Orientation Parent
   {
    set
    {
     parent = value;
    }
   }
   public RotationYEnumerator GetEnumerator()
   {
    return new RotationYEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class RotationYEnumerator: IEnumerator
        {
   int nIndex;
   Type_Orientation parent;
   public RotationYEnumerator(Type_Orientation par)
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
    return(nIndex < parent.RotationYCount );
   }
   public SchemaDecimal Current
   {
    get
    {
     return(parent.GetRotationYAt(nIndex));
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
  public int GetRotationZMinCount()
  {
   return 1;
  }
  public int RotationZMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetRotationZMaxCount()
  {
   return 1;
  }
  public int RotationZMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetRotationZCount()
  {
   return DomChildCount(NodeType.Element, "", "RotationZ");
  }
  public int RotationZCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "RotationZ");
   }
  }
  public bool HasRotationZ()
  {
   return HasDomChild(NodeType.Element, "", "RotationZ");
  }
  public SchemaDecimal GetRotationZAt(int index)
  {
   return new SchemaDecimal(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "RotationZ", index)));
  }
  public XmlNode GetStartingRotationZCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "RotationZ" );
  }
  public XmlNode GetAdvancedRotationZCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "RotationZ", curNode );
  }
  public SchemaDecimal GetRotationZValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new SchemaDecimal( curNode.InnerText );
  }
  public SchemaDecimal GetRotationZ()
  {
   return GetRotationZAt(0);
  }
  public SchemaDecimal RotationZ
  {
   get
   {
    return GetRotationZAt(0);
   }
  }
  public void RemoveRotationZAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "RotationZ", index);
  }
  public void RemoveRotationZ()
  {
   while (HasRotationZ())
    RemoveRotationZAt(0);
  }
  public void AddRotationZ(SchemaDecimal newValue)
  {
   AppendDomChild(NodeType.Element, "", "RotationZ", newValue.ToString());
  }
  public void InsertRotationZAt(SchemaDecimal newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "RotationZ", index, newValue.ToString());
  }
  public void ReplaceRotationZAt(SchemaDecimal newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "RotationZ", index, newValue.ToString());
  }
        public RotationZCollection MyRotationZs = new RotationZCollection( );
        public class RotationZCollection: IEnumerable
        {
            Type_Orientation parent;
            public Type_Orientation Parent
   {
    set
    {
     parent = value;
    }
   }
   public RotationZEnumerator GetEnumerator()
   {
    return new RotationZEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class RotationZEnumerator: IEnumerator
        {
   int nIndex;
   Type_Orientation parent;
   public RotationZEnumerator(Type_Orientation par)
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
    return(nIndex < parent.RotationZCount );
   }
   public SchemaDecimal Current
   {
    get
    {
     return(parent.GetRotationZAt(nIndex));
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
            MyRotationXs.Parent = this;
            MyRotationYs.Parent = this;
            MyRotationZs.Parent = this;
 }
}
}
