using System;
using System.Collections;
using System.Xml;
using Altova.Types;
namespace WorldXmlDescriptor
{
 public class LatLonBoundingBox : Altova.Xml.Node
 {
  public LatLonBoundingBox() : base() { SetCollectionParents(); }
  public LatLonBoundingBox(XmlDocument doc) : base(doc) { SetCollectionParents(); }
  public LatLonBoundingBox(XmlNode node) : base(node) { SetCollectionParents(); }
  public LatLonBoundingBox(Altova.Xml.Node node) : base(node) { SetCollectionParents(); }
  public override void AdjustPrefix()
  {
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "North"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "North", i);
    InternalAdjustPrefix(DOMNode, true);
    new LatitudeCoordinate(DOMNode).AdjustPrefix();
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "South"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "South", i);
    InternalAdjustPrefix(DOMNode, true);
    new LatitudeCoordinate(DOMNode).AdjustPrefix();
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "West"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "West", i);
    InternalAdjustPrefix(DOMNode, true);
    new LongitudeCoordinate(DOMNode).AdjustPrefix();
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "East"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "East", i);
    InternalAdjustPrefix(DOMNode, true);
    new LongitudeCoordinate(DOMNode).AdjustPrefix();
   }
  }
  public int GetNorthMinCount()
  {
   return 1;
  }
  public int NorthMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetNorthMaxCount()
  {
   return 1;
  }
  public int NorthMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetNorthCount()
  {
   return DomChildCount(NodeType.Element, "", "North");
  }
  public int NorthCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "North");
   }
  }
  public bool HasNorth()
  {
   return HasDomChild(NodeType.Element, "", "North");
  }
  public LatitudeCoordinate GetNorthAt(int index)
  {
   return new LatitudeCoordinate(GetDomChildAt(NodeType.Element, "", "North", index));
  }
  public LatitudeCoordinate GetNorth()
  {
   return GetNorthAt(0);
  }
  public LatitudeCoordinate North
  {
   get
   {
    return GetNorthAt(0);
   }
  }
  public void RemoveNorthAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "North", index);
  }
  public void RemoveNorth()
  {
   while (HasNorth())
    RemoveNorthAt(0);
  }
  public void AddNorth(LatitudeCoordinate newValue)
  {
   AppendDomElement("", "North", newValue);
  }
  public void InsertNorthAt(LatitudeCoordinate newValue, int index)
  {
   InsertDomElementAt("", "North", index, newValue);
  }
  public void ReplaceNorthAt(LatitudeCoordinate newValue, int index)
  {
   ReplaceDomElementAt("", "North", index, newValue);
  }
        public NorthCollection MyNorths = new NorthCollection( );
        public class NorthCollection: IEnumerable
        {
            LatLonBoundingBox parent;
            public LatLonBoundingBox Parent
   {
    set
    {
     parent = value;
    }
   }
   public NorthEnumerator GetEnumerator()
   {
    return new NorthEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class NorthEnumerator: IEnumerator
        {
   int nIndex;
   LatLonBoundingBox parent;
   public NorthEnumerator(LatLonBoundingBox par)
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
    return(nIndex < parent.NorthCount );
   }
   public LatitudeCoordinate Current
   {
    get
    {
     return(parent.GetNorthAt(nIndex));
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
  public int GetSouthMinCount()
  {
   return 1;
  }
  public int SouthMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetSouthMaxCount()
  {
   return 1;
  }
  public int SouthMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetSouthCount()
  {
   return DomChildCount(NodeType.Element, "", "South");
  }
  public int SouthCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "South");
   }
  }
  public bool HasSouth()
  {
   return HasDomChild(NodeType.Element, "", "South");
  }
  public LatitudeCoordinate GetSouthAt(int index)
  {
   return new LatitudeCoordinate(GetDomChildAt(NodeType.Element, "", "South", index));
  }
  public LatitudeCoordinate GetSouth()
  {
   return GetSouthAt(0);
  }
  public LatitudeCoordinate South
  {
   get
   {
    return GetSouthAt(0);
   }
  }
  public void RemoveSouthAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "South", index);
  }
  public void RemoveSouth()
  {
   while (HasSouth())
    RemoveSouthAt(0);
  }
  public void AddSouth(LatitudeCoordinate newValue)
  {
   AppendDomElement("", "South", newValue);
  }
  public void InsertSouthAt(LatitudeCoordinate newValue, int index)
  {
   InsertDomElementAt("", "South", index, newValue);
  }
  public void ReplaceSouthAt(LatitudeCoordinate newValue, int index)
  {
   ReplaceDomElementAt("", "South", index, newValue);
  }
        public SouthCollection MySouths = new SouthCollection( );
        public class SouthCollection: IEnumerable
        {
            LatLonBoundingBox parent;
            public LatLonBoundingBox Parent
   {
    set
    {
     parent = value;
    }
   }
   public SouthEnumerator GetEnumerator()
   {
    return new SouthEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class SouthEnumerator: IEnumerator
        {
   int nIndex;
   LatLonBoundingBox parent;
   public SouthEnumerator(LatLonBoundingBox par)
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
    return(nIndex < parent.SouthCount );
   }
   public LatitudeCoordinate Current
   {
    get
    {
     return(parent.GetSouthAt(nIndex));
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
  public int GetWestMinCount()
  {
   return 1;
  }
  public int WestMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetWestMaxCount()
  {
   return 1;
  }
  public int WestMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetWestCount()
  {
   return DomChildCount(NodeType.Element, "", "West");
  }
  public int WestCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "West");
   }
  }
  public bool HasWest()
  {
   return HasDomChild(NodeType.Element, "", "West");
  }
  public LongitudeCoordinate GetWestAt(int index)
  {
   return new LongitudeCoordinate(GetDomChildAt(NodeType.Element, "", "West", index));
  }
  public LongitudeCoordinate GetWest()
  {
   return GetWestAt(0);
  }
  public LongitudeCoordinate West
  {
   get
   {
    return GetWestAt(0);
   }
  }
  public void RemoveWestAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "West", index);
  }
  public void RemoveWest()
  {
   while (HasWest())
    RemoveWestAt(0);
  }
  public void AddWest(LongitudeCoordinate newValue)
  {
   AppendDomElement("", "West", newValue);
  }
  public void InsertWestAt(LongitudeCoordinate newValue, int index)
  {
   InsertDomElementAt("", "West", index, newValue);
  }
  public void ReplaceWestAt(LongitudeCoordinate newValue, int index)
  {
   ReplaceDomElementAt("", "West", index, newValue);
  }
        public WestCollection MyWests = new WestCollection( );
        public class WestCollection: IEnumerable
        {
            LatLonBoundingBox parent;
            public LatLonBoundingBox Parent
   {
    set
    {
     parent = value;
    }
   }
   public WestEnumerator GetEnumerator()
   {
    return new WestEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class WestEnumerator: IEnumerator
        {
   int nIndex;
   LatLonBoundingBox parent;
   public WestEnumerator(LatLonBoundingBox par)
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
    return(nIndex < parent.WestCount );
   }
   public LongitudeCoordinate Current
   {
    get
    {
     return(parent.GetWestAt(nIndex));
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
  public int GetEastMinCount()
  {
   return 1;
  }
  public int EastMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetEastMaxCount()
  {
   return 1;
  }
  public int EastMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetEastCount()
  {
   return DomChildCount(NodeType.Element, "", "East");
  }
  public int EastCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "East");
   }
  }
  public bool HasEast()
  {
   return HasDomChild(NodeType.Element, "", "East");
  }
  public LongitudeCoordinate GetEastAt(int index)
  {
   return new LongitudeCoordinate(GetDomChildAt(NodeType.Element, "", "East", index));
  }
  public LongitudeCoordinate GetEast()
  {
   return GetEastAt(0);
  }
  public LongitudeCoordinate East
  {
   get
   {
    return GetEastAt(0);
   }
  }
  public void RemoveEastAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "East", index);
  }
  public void RemoveEast()
  {
   while (HasEast())
    RemoveEastAt(0);
  }
  public void AddEast(LongitudeCoordinate newValue)
  {
   AppendDomElement("", "East", newValue);
  }
  public void InsertEastAt(LongitudeCoordinate newValue, int index)
  {
   InsertDomElementAt("", "East", index, newValue);
  }
  public void ReplaceEastAt(LongitudeCoordinate newValue, int index)
  {
   ReplaceDomElementAt("", "East", index, newValue);
  }
        public EastCollection MyEasts = new EastCollection( );
        public class EastCollection: IEnumerable
        {
            LatLonBoundingBox parent;
            public LatLonBoundingBox Parent
   {
    set
    {
     parent = value;
    }
   }
   public EastEnumerator GetEnumerator()
   {
    return new EastEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class EastEnumerator: IEnumerator
        {
   int nIndex;
   LatLonBoundingBox parent;
   public EastEnumerator(LatLonBoundingBox par)
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
    return(nIndex < parent.EastCount );
   }
   public LongitudeCoordinate Current
   {
    get
    {
     return(parent.GetEastAt(nIndex));
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
            MyNorths.Parent = this;
            MySouths.Parent = this;
            MyWests.Parent = this;
            MyEasts.Parent = this;
 }
}
}
