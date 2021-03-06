using System;
using System.Collections;
using System.Xml;
using Altova.Types;
namespace WorldXmlDescriptor
{
 public class TerrainAccessor : Altova.Xml.Node
 {
  public TerrainAccessor() : base() { SetCollectionParents(); }
  public TerrainAccessor(XmlDocument doc) : base(doc) { SetCollectionParents(); }
  public TerrainAccessor(XmlNode node) : base(node) { SetCollectionParents(); }
  public TerrainAccessor(Altova.Xml.Node node) : base(node) { SetCollectionParents(); }
  public override void AdjustPrefix()
  {
   for (int i = 0; i < DomChildCount(NodeType.Attribute, "", "Name"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Attribute, "", "Name", i);
    InternalAdjustPrefix(DOMNode, false);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "TerrainTileService"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "TerrainTileService", i);
    InternalAdjustPrefix(DOMNode, true);
    new TerrainTileService(DOMNode).AdjustPrefix();
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "DownloadableWMSSet"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "DownloadableWMSSet", i);
    InternalAdjustPrefix(DOMNode, true);
    new DownloadableWMS(DOMNode).AdjustPrefix();
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "LatLonBoundingBox"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "LatLonBoundingBox", i);
    InternalAdjustPrefix(DOMNode, true);
    new LatLonBoundingBox(DOMNode).AdjustPrefix();
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "HigherResolutionSubsets"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "HigherResolutionSubsets", i);
    InternalAdjustPrefix(DOMNode, true);
    new TerrainAccessor(DOMNode).AdjustPrefix();
   }
  }
  public int GetNameMinCount()
  {
   return 1;
  }
  public int NameMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetNameMaxCount()
  {
   return 1;
  }
  public int NameMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetNameCount()
  {
   return DomChildCount(NodeType.Attribute, "", "Name");
  }
  public int NameCount
  {
   get
   {
    return DomChildCount(NodeType.Attribute, "", "Name");
   }
  }
  public bool HasName()
  {
   return HasDomChild(NodeType.Attribute, "", "Name");
  }
  public SchemaString GetNameAt(int index)
  {
   return new SchemaString(GetDomNodeValue(GetDomChildAt(NodeType.Attribute, "", "Name", index)));
  }
  public SchemaString GetName()
  {
   return GetNameAt(0);
  }
  public SchemaString Name
  {
   get
   {
    return GetNameAt(0);
   }
  }
  public void RemoveNameAt(int index)
  {
   RemoveDomChildAt(NodeType.Attribute, "", "Name", index);
  }
  public void RemoveName()
  {
   while (HasName())
    RemoveNameAt(0);
  }
  public void AddName(SchemaString newValue)
  {
   AppendDomChild(NodeType.Attribute, "", "Name", newValue.ToString());
  }
  public void InsertNameAt(SchemaString newValue, int index)
  {
   InsertDomChildAt(NodeType.Attribute, "", "Name", index, newValue.ToString());
  }
  public void ReplaceNameAt(SchemaString newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Attribute, "", "Name", index, newValue.ToString());
  }
        public NameCollection MyNames = new NameCollection( );
        public class NameCollection: IEnumerable
        {
            TerrainAccessor parent;
            public TerrainAccessor Parent
   {
    set
    {
     parent = value;
    }
   }
   public NameEnumerator GetEnumerator()
   {
    return new NameEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class NameEnumerator: IEnumerator
        {
   int nIndex;
   TerrainAccessor parent;
   public NameEnumerator(TerrainAccessor par)
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
    return(nIndex < parent.NameCount );
   }
   public SchemaString Current
   {
    get
    {
     return(parent.GetNameAt(nIndex));
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
  public int GetTerrainTileServiceMinCount()
  {
   return 1;
  }
  public int TerrainTileServiceMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetTerrainTileServiceMaxCount()
  {
   return 1;
  }
  public int TerrainTileServiceMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetTerrainTileServiceCount()
  {
   return DomChildCount(NodeType.Element, "", "TerrainTileService");
  }
  public int TerrainTileServiceCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "TerrainTileService");
   }
  }
  public bool HasTerrainTileService()
  {
   return HasDomChild(NodeType.Element, "", "TerrainTileService");
  }
  public TerrainTileService GetTerrainTileServiceAt(int index)
  {
   return new TerrainTileService(GetDomChildAt(NodeType.Element, "", "TerrainTileService", index));
  }
  public TerrainTileService GetTerrainTileService()
  {
   return GetTerrainTileServiceAt(0);
  }
  public TerrainTileService TerrainTileService
  {
   get
   {
    return GetTerrainTileServiceAt(0);
   }
  }
  public void RemoveTerrainTileServiceAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "TerrainTileService", index);
  }
  public void RemoveTerrainTileService()
  {
   while (HasTerrainTileService())
    RemoveTerrainTileServiceAt(0);
  }
  public void AddTerrainTileService(TerrainTileService newValue)
  {
   AppendDomElement("", "TerrainTileService", newValue);
  }
  public void InsertTerrainTileServiceAt(TerrainTileService newValue, int index)
  {
   InsertDomElementAt("", "TerrainTileService", index, newValue);
  }
  public void ReplaceTerrainTileServiceAt(TerrainTileService newValue, int index)
  {
   ReplaceDomElementAt("", "TerrainTileService", index, newValue);
  }
        public TerrainTileServiceCollection MyTerrainTileServices = new TerrainTileServiceCollection( );
        public class TerrainTileServiceCollection: IEnumerable
        {
            TerrainAccessor parent;
            public TerrainAccessor Parent
   {
    set
    {
     parent = value;
    }
   }
   public TerrainTileServiceEnumerator GetEnumerator()
   {
    return new TerrainTileServiceEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class TerrainTileServiceEnumerator: IEnumerator
        {
   int nIndex;
   TerrainAccessor parent;
   public TerrainTileServiceEnumerator(TerrainAccessor par)
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
    return(nIndex < parent.TerrainTileServiceCount );
   }
   public TerrainTileService Current
   {
    get
    {
     return(parent.GetTerrainTileServiceAt(nIndex));
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
  public int GetDownloadableWMSSetMinCount()
  {
   return 1;
  }
  public int DownloadableWMSSetMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetDownloadableWMSSetMaxCount()
  {
   return 1;
  }
  public int DownloadableWMSSetMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetDownloadableWMSSetCount()
  {
   return DomChildCount(NodeType.Element, "", "DownloadableWMSSet");
  }
  public int DownloadableWMSSetCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "DownloadableWMSSet");
   }
  }
  public bool HasDownloadableWMSSet()
  {
   return HasDomChild(NodeType.Element, "", "DownloadableWMSSet");
  }
  public DownloadableWMS GetDownloadableWMSSetAt(int index)
  {
   return new DownloadableWMS(GetDomChildAt(NodeType.Element, "", "DownloadableWMSSet", index));
  }
  public DownloadableWMS GetDownloadableWMSSet()
  {
   return GetDownloadableWMSSetAt(0);
  }
  public DownloadableWMS DownloadableWMSSet
  {
   get
   {
    return GetDownloadableWMSSetAt(0);
   }
  }
  public void RemoveDownloadableWMSSetAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "DownloadableWMSSet", index);
  }
  public void RemoveDownloadableWMSSet()
  {
   while (HasDownloadableWMSSet())
    RemoveDownloadableWMSSetAt(0);
  }
  public void AddDownloadableWMSSet(DownloadableWMS newValue)
  {
   AppendDomElement("", "DownloadableWMSSet", newValue);
  }
  public void InsertDownloadableWMSSetAt(DownloadableWMS newValue, int index)
  {
   InsertDomElementAt("", "DownloadableWMSSet", index, newValue);
  }
  public void ReplaceDownloadableWMSSetAt(DownloadableWMS newValue, int index)
  {
   ReplaceDomElementAt("", "DownloadableWMSSet", index, newValue);
  }
        public DownloadableWMSSetCollection MyDownloadableWMSSets = new DownloadableWMSSetCollection( );
        public class DownloadableWMSSetCollection: IEnumerable
        {
            TerrainAccessor parent;
            public TerrainAccessor Parent
   {
    set
    {
     parent = value;
    }
   }
   public DownloadableWMSSetEnumerator GetEnumerator()
   {
    return new DownloadableWMSSetEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class DownloadableWMSSetEnumerator: IEnumerator
        {
   int nIndex;
   TerrainAccessor parent;
   public DownloadableWMSSetEnumerator(TerrainAccessor par)
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
    return(nIndex < parent.DownloadableWMSSetCount );
   }
   public DownloadableWMS Current
   {
    get
    {
     return(parent.GetDownloadableWMSSetAt(nIndex));
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
  public int GetLatLonBoundingBoxMinCount()
  {
   return 1;
  }
  public int LatLonBoundingBoxMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetLatLonBoundingBoxMaxCount()
  {
   return 1;
  }
  public int LatLonBoundingBoxMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetLatLonBoundingBoxCount()
  {
   return DomChildCount(NodeType.Element, "", "LatLonBoundingBox");
  }
  public int LatLonBoundingBoxCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "LatLonBoundingBox");
   }
  }
  public bool HasLatLonBoundingBox()
  {
   return HasDomChild(NodeType.Element, "", "LatLonBoundingBox");
  }
  public LatLonBoundingBox GetLatLonBoundingBoxAt(int index)
  {
   return new LatLonBoundingBox(GetDomChildAt(NodeType.Element, "", "LatLonBoundingBox", index));
  }
  public LatLonBoundingBox GetLatLonBoundingBox()
  {
   return GetLatLonBoundingBoxAt(0);
  }
  public LatLonBoundingBox LatLonBoundingBox
  {
   get
   {
    return GetLatLonBoundingBoxAt(0);
   }
  }
  public void RemoveLatLonBoundingBoxAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "LatLonBoundingBox", index);
  }
  public void RemoveLatLonBoundingBox()
  {
   while (HasLatLonBoundingBox())
    RemoveLatLonBoundingBoxAt(0);
  }
  public void AddLatLonBoundingBox(LatLonBoundingBox newValue)
  {
   AppendDomElement("", "LatLonBoundingBox", newValue);
  }
  public void InsertLatLonBoundingBoxAt(LatLonBoundingBox newValue, int index)
  {
   InsertDomElementAt("", "LatLonBoundingBox", index, newValue);
  }
  public void ReplaceLatLonBoundingBoxAt(LatLonBoundingBox newValue, int index)
  {
   ReplaceDomElementAt("", "LatLonBoundingBox", index, newValue);
  }
        public LatLonBoundingBoxCollection MyLatLonBoundingBoxs = new LatLonBoundingBoxCollection( );
        public class LatLonBoundingBoxCollection: IEnumerable
        {
            TerrainAccessor parent;
            public TerrainAccessor Parent
   {
    set
    {
     parent = value;
    }
   }
   public LatLonBoundingBoxEnumerator GetEnumerator()
   {
    return new LatLonBoundingBoxEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class LatLonBoundingBoxEnumerator: IEnumerator
        {
   int nIndex;
   TerrainAccessor parent;
   public LatLonBoundingBoxEnumerator(TerrainAccessor par)
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
    return(nIndex < parent.LatLonBoundingBoxCount );
   }
   public LatLonBoundingBox Current
   {
    get
    {
     return(parent.GetLatLonBoundingBoxAt(nIndex));
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
  public int GetHigherResolutionSubsetsMinCount()
  {
   return 0;
  }
  public int HigherResolutionSubsetsMinCount
  {
   get
   {
    return 0;
   }
  }
  public int GetHigherResolutionSubsetsMaxCount()
  {
   return Int32.MaxValue;
  }
  public int HigherResolutionSubsetsMaxCount
  {
   get
   {
    return Int32.MaxValue;
   }
  }
  public int GetHigherResolutionSubsetsCount()
  {
   return DomChildCount(NodeType.Element, "", "HigherResolutionSubsets");
  }
  public int HigherResolutionSubsetsCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "HigherResolutionSubsets");
   }
  }
  public bool HasHigherResolutionSubsets()
  {
   return HasDomChild(NodeType.Element, "", "HigherResolutionSubsets");
  }
  public TerrainAccessor GetHigherResolutionSubsetsAt(int index)
  {
   return new TerrainAccessor(GetDomChildAt(NodeType.Element, "", "HigherResolutionSubsets", index));
  }
  public TerrainAccessor GetHigherResolutionSubsets()
  {
   return GetHigherResolutionSubsetsAt(0);
  }
  public TerrainAccessor HigherResolutionSubsets
  {
   get
   {
    return GetHigherResolutionSubsetsAt(0);
   }
  }
  public void RemoveHigherResolutionSubsetsAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "HigherResolutionSubsets", index);
  }
  public void RemoveHigherResolutionSubsets()
  {
   while (HasHigherResolutionSubsets())
    RemoveHigherResolutionSubsetsAt(0);
  }
  public void AddHigherResolutionSubsets(TerrainAccessor newValue)
  {
   AppendDomElement("", "HigherResolutionSubsets", newValue);
  }
  public void InsertHigherResolutionSubsetsAt(TerrainAccessor newValue, int index)
  {
   InsertDomElementAt("", "HigherResolutionSubsets", index, newValue);
  }
  public void ReplaceHigherResolutionSubsetsAt(TerrainAccessor newValue, int index)
  {
   ReplaceDomElementAt("", "HigherResolutionSubsets", index, newValue);
  }
        public HigherResolutionSubsetsCollection MyHigherResolutionSubsetss = new HigherResolutionSubsetsCollection( );
        public class HigherResolutionSubsetsCollection: IEnumerable
        {
            TerrainAccessor parent;
            public TerrainAccessor Parent
   {
    set
    {
     parent = value;
    }
   }
   public HigherResolutionSubsetsEnumerator GetEnumerator()
   {
    return new HigherResolutionSubsetsEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class HigherResolutionSubsetsEnumerator: IEnumerator
        {
   int nIndex;
   TerrainAccessor parent;
   public HigherResolutionSubsetsEnumerator(TerrainAccessor par)
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
    return(nIndex < parent.HigherResolutionSubsetsCount );
   }
   public TerrainAccessor Current
   {
    get
    {
     return(parent.GetHigherResolutionSubsetsAt(nIndex));
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
            MyNames.Parent = this;
            MyTerrainTileServices.Parent = this;
            MyDownloadableWMSSets.Parent = this;
            MyLatLonBoundingBoxs.Parent = this;
            MyHigherResolutionSubsetss.Parent = this;
 }
}
}
