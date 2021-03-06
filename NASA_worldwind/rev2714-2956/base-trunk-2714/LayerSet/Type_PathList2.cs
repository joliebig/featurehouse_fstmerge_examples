using System;
using System.Collections;
using System.Xml;
using Altova.Types;
namespace LayerSet
{
 public class Type_PathList2 : Altova.Xml.Node
 {
  public Type_PathList2() : base() { SetCollectionParents(); }
  public Type_PathList2(XmlDocument doc) : base(doc) { SetCollectionParents(); }
  public Type_PathList2(XmlNode node) : base(node) { SetCollectionParents(); }
  public Type_PathList2(Altova.Xml.Node node) : base(node) { SetCollectionParents(); }
  public override void AdjustPrefix()
  {
   for (int i = 0; i < DomChildCount(NodeType.Attribute, "", "ShowAtStartup"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Attribute, "", "ShowAtStartup", i);
    InternalAdjustPrefix(DOMNode, false);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "Name"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "Name", i);
    InternalAdjustPrefix(DOMNode, true);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "DistanceAboveSurface"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "DistanceAboveSurface", i);
    InternalAdjustPrefix(DOMNode, true);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "MinDisplayAltitude"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "MinDisplayAltitude", i);
    InternalAdjustPrefix(DOMNode, true);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "MaxDisplayAltitude"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "MaxDisplayAltitude", i);
    InternalAdjustPrefix(DOMNode, true);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "PathsDirectory"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "PathsDirectory", i);
    InternalAdjustPrefix(DOMNode, true);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "RGBColor"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "RGBColor", i);
    InternalAdjustPrefix(DOMNode, true);
    new Type_RGBColor2(DOMNode).AdjustPrefix();
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "WinColorName"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "WinColorName", i);
    InternalAdjustPrefix(DOMNode, true);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "ExtendedInformation"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "ExtendedInformation", i);
    InternalAdjustPrefix(DOMNode, true);
    new Type_ExtendedInformation(DOMNode).AdjustPrefix();
   }
  }
  public int GetShowAtStartupMinCount()
  {
   return 1;
  }
  public int ShowAtStartupMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetShowAtStartupMaxCount()
  {
   return 1;
  }
  public int ShowAtStartupMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetShowAtStartupCount()
  {
   return DomChildCount(NodeType.Attribute, "", "ShowAtStartup");
  }
  public int ShowAtStartupCount
  {
   get
   {
    return DomChildCount(NodeType.Attribute, "", "ShowAtStartup");
   }
  }
  public bool HasShowAtStartup()
  {
   return HasDomChild(NodeType.Attribute, "", "ShowAtStartup");
  }
  public SchemaBoolean GetShowAtStartupAt(int index)
  {
   return new SchemaBoolean(GetDomNodeValue(GetDomChildAt(NodeType.Attribute, "", "ShowAtStartup", index)));
  }
  public XmlNode GetStartingShowAtStartupCursor()
  {
   return GetDomFirstChild( NodeType.Attribute, "", "ShowAtStartup" );
  }
  public XmlNode GetAdvancedShowAtStartupCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Attribute, "", "ShowAtStartup", curNode );
  }
  public SchemaBoolean GetShowAtStartupValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new SchemaBoolean( curNode.Value );
  }
  public SchemaBoolean GetShowAtStartup()
  {
   return GetShowAtStartupAt(0);
  }
  public SchemaBoolean ShowAtStartup
  {
   get
   {
    return GetShowAtStartupAt(0);
   }
  }
  public void RemoveShowAtStartupAt(int index)
  {
   RemoveDomChildAt(NodeType.Attribute, "", "ShowAtStartup", index);
  }
  public void RemoveShowAtStartup()
  {
   while (HasShowAtStartup())
    RemoveShowAtStartupAt(0);
  }
  public void AddShowAtStartup(SchemaBoolean newValue)
  {
   AppendDomChild(NodeType.Attribute, "", "ShowAtStartup", newValue.ToString());
  }
  public void InsertShowAtStartupAt(SchemaBoolean newValue, int index)
  {
   InsertDomChildAt(NodeType.Attribute, "", "ShowAtStartup", index, newValue.ToString());
  }
  public void ReplaceShowAtStartupAt(SchemaBoolean newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Attribute, "", "ShowAtStartup", index, newValue.ToString());
  }
        public ShowAtStartupCollection MyShowAtStartups = new ShowAtStartupCollection( );
        public class ShowAtStartupCollection: IEnumerable
        {
            Type_PathList2 parent;
            public Type_PathList2 Parent
   {
    set
    {
     parent = value;
    }
   }
   public ShowAtStartupEnumerator GetEnumerator()
   {
    return new ShowAtStartupEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class ShowAtStartupEnumerator: IEnumerator
        {
   int nIndex;
   Type_PathList2 parent;
   public ShowAtStartupEnumerator(Type_PathList2 par)
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
    return(nIndex < parent.ShowAtStartupCount );
   }
   public SchemaBoolean Current
   {
    get
    {
     return(parent.GetShowAtStartupAt(nIndex));
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
   return DomChildCount(NodeType.Element, "", "Name");
  }
  public int NameCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "Name");
   }
  }
  public bool HasName()
  {
   return HasDomChild(NodeType.Element, "", "Name");
  }
  public SchemaString GetNameAt(int index)
  {
   return new SchemaString(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "Name", index)));
  }
  public XmlNode GetStartingNameCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "Name" );
  }
  public XmlNode GetAdvancedNameCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "Name", curNode );
  }
  public SchemaString GetNameValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new SchemaString( curNode.InnerText );
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
   RemoveDomChildAt(NodeType.Element, "", "Name", index);
  }
  public void RemoveName()
  {
   while (HasName())
    RemoveNameAt(0);
  }
  public void AddName(SchemaString newValue)
  {
   AppendDomChild(NodeType.Element, "", "Name", newValue.ToString());
  }
  public void InsertNameAt(SchemaString newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "Name", index, newValue.ToString());
  }
  public void ReplaceNameAt(SchemaString newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "Name", index, newValue.ToString());
  }
        public NameCollection MyNames = new NameCollection( );
        public class NameCollection: IEnumerable
        {
            Type_PathList2 parent;
            public Type_PathList2 Parent
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
   Type_PathList2 parent;
   public NameEnumerator(Type_PathList2 par)
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
  public int GetDistanceAboveSurfaceMinCount()
  {
   return 1;
  }
  public int DistanceAboveSurfaceMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetDistanceAboveSurfaceMaxCount()
  {
   return 1;
  }
  public int DistanceAboveSurfaceMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetDistanceAboveSurfaceCount()
  {
   return DomChildCount(NodeType.Element, "", "DistanceAboveSurface");
  }
  public int DistanceAboveSurfaceCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "DistanceAboveSurface");
   }
  }
  public bool HasDistanceAboveSurface()
  {
   return HasDomChild(NodeType.Element, "", "DistanceAboveSurface");
  }
  public SchemaDecimal GetDistanceAboveSurfaceAt(int index)
  {
   return new SchemaDecimal(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "DistanceAboveSurface", index)));
  }
  public XmlNode GetStartingDistanceAboveSurfaceCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "DistanceAboveSurface" );
  }
  public XmlNode GetAdvancedDistanceAboveSurfaceCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "DistanceAboveSurface", curNode );
  }
  public SchemaDecimal GetDistanceAboveSurfaceValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new SchemaDecimal( curNode.InnerText );
  }
  public SchemaDecimal GetDistanceAboveSurface()
  {
   return GetDistanceAboveSurfaceAt(0);
  }
  public SchemaDecimal DistanceAboveSurface
  {
   get
   {
    return GetDistanceAboveSurfaceAt(0);
   }
  }
  public void RemoveDistanceAboveSurfaceAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "DistanceAboveSurface", index);
  }
  public void RemoveDistanceAboveSurface()
  {
   while (HasDistanceAboveSurface())
    RemoveDistanceAboveSurfaceAt(0);
  }
  public void AddDistanceAboveSurface(SchemaDecimal newValue)
  {
   AppendDomChild(NodeType.Element, "", "DistanceAboveSurface", newValue.ToString());
  }
  public void InsertDistanceAboveSurfaceAt(SchemaDecimal newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "DistanceAboveSurface", index, newValue.ToString());
  }
  public void ReplaceDistanceAboveSurfaceAt(SchemaDecimal newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "DistanceAboveSurface", index, newValue.ToString());
  }
        public DistanceAboveSurfaceCollection MyDistanceAboveSurfaces = new DistanceAboveSurfaceCollection( );
        public class DistanceAboveSurfaceCollection: IEnumerable
        {
            Type_PathList2 parent;
            public Type_PathList2 Parent
   {
    set
    {
     parent = value;
    }
   }
   public DistanceAboveSurfaceEnumerator GetEnumerator()
   {
    return new DistanceAboveSurfaceEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class DistanceAboveSurfaceEnumerator: IEnumerator
        {
   int nIndex;
   Type_PathList2 parent;
   public DistanceAboveSurfaceEnumerator(Type_PathList2 par)
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
    return(nIndex < parent.DistanceAboveSurfaceCount );
   }
   public SchemaDecimal Current
   {
    get
    {
     return(parent.GetDistanceAboveSurfaceAt(nIndex));
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
  public int GetMinDisplayAltitudeMinCount()
  {
   return 1;
  }
  public int MinDisplayAltitudeMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetMinDisplayAltitudeMaxCount()
  {
   return 1;
  }
  public int MinDisplayAltitudeMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetMinDisplayAltitudeCount()
  {
   return DomChildCount(NodeType.Element, "", "MinDisplayAltitude");
  }
  public int MinDisplayAltitudeCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "MinDisplayAltitude");
   }
  }
  public bool HasMinDisplayAltitude()
  {
   return HasDomChild(NodeType.Element, "", "MinDisplayAltitude");
  }
  public MinDisplayAltitudeType2 GetMinDisplayAltitudeAt(int index)
  {
   return new MinDisplayAltitudeType2(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "MinDisplayAltitude", index)));
  }
  public XmlNode GetStartingMinDisplayAltitudeCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "MinDisplayAltitude" );
  }
  public XmlNode GetAdvancedMinDisplayAltitudeCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "MinDisplayAltitude", curNode );
  }
  public MinDisplayAltitudeType2 GetMinDisplayAltitudeValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new MinDisplayAltitudeType2( curNode.InnerText );
  }
  public MinDisplayAltitudeType2 GetMinDisplayAltitude()
  {
   return GetMinDisplayAltitudeAt(0);
  }
  public MinDisplayAltitudeType2 MinDisplayAltitude
  {
   get
   {
    return GetMinDisplayAltitudeAt(0);
   }
  }
  public void RemoveMinDisplayAltitudeAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "MinDisplayAltitude", index);
  }
  public void RemoveMinDisplayAltitude()
  {
   while (HasMinDisplayAltitude())
    RemoveMinDisplayAltitudeAt(0);
  }
  public void AddMinDisplayAltitude(MinDisplayAltitudeType2 newValue)
  {
   AppendDomChild(NodeType.Element, "", "MinDisplayAltitude", newValue.ToString());
  }
  public void InsertMinDisplayAltitudeAt(MinDisplayAltitudeType2 newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "MinDisplayAltitude", index, newValue.ToString());
  }
  public void ReplaceMinDisplayAltitudeAt(MinDisplayAltitudeType2 newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "MinDisplayAltitude", index, newValue.ToString());
  }
        public MinDisplayAltitudeCollection MyMinDisplayAltitudes = new MinDisplayAltitudeCollection( );
        public class MinDisplayAltitudeCollection: IEnumerable
        {
            Type_PathList2 parent;
            public Type_PathList2 Parent
   {
    set
    {
     parent = value;
    }
   }
   public MinDisplayAltitudeEnumerator GetEnumerator()
   {
    return new MinDisplayAltitudeEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class MinDisplayAltitudeEnumerator: IEnumerator
        {
   int nIndex;
   Type_PathList2 parent;
   public MinDisplayAltitudeEnumerator(Type_PathList2 par)
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
    return(nIndex < parent.MinDisplayAltitudeCount );
   }
   public MinDisplayAltitudeType2 Current
   {
    get
    {
     return(parent.GetMinDisplayAltitudeAt(nIndex));
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
  public int GetMaxDisplayAltitudeMinCount()
  {
   return 1;
  }
  public int MaxDisplayAltitudeMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetMaxDisplayAltitudeMaxCount()
  {
   return 1;
  }
  public int MaxDisplayAltitudeMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetMaxDisplayAltitudeCount()
  {
   return DomChildCount(NodeType.Element, "", "MaxDisplayAltitude");
  }
  public int MaxDisplayAltitudeCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "MaxDisplayAltitude");
   }
  }
  public bool HasMaxDisplayAltitude()
  {
   return HasDomChild(NodeType.Element, "", "MaxDisplayAltitude");
  }
  public MaxDisplayAltitudeType2 GetMaxDisplayAltitudeAt(int index)
  {
   return new MaxDisplayAltitudeType2(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "MaxDisplayAltitude", index)));
  }
  public XmlNode GetStartingMaxDisplayAltitudeCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "MaxDisplayAltitude" );
  }
  public XmlNode GetAdvancedMaxDisplayAltitudeCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "MaxDisplayAltitude", curNode );
  }
  public MaxDisplayAltitudeType2 GetMaxDisplayAltitudeValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new MaxDisplayAltitudeType2( curNode.InnerText );
  }
  public MaxDisplayAltitudeType2 GetMaxDisplayAltitude()
  {
   return GetMaxDisplayAltitudeAt(0);
  }
  public MaxDisplayAltitudeType2 MaxDisplayAltitude
  {
   get
   {
    return GetMaxDisplayAltitudeAt(0);
   }
  }
  public void RemoveMaxDisplayAltitudeAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "MaxDisplayAltitude", index);
  }
  public void RemoveMaxDisplayAltitude()
  {
   while (HasMaxDisplayAltitude())
    RemoveMaxDisplayAltitudeAt(0);
  }
  public void AddMaxDisplayAltitude(MaxDisplayAltitudeType2 newValue)
  {
   AppendDomChild(NodeType.Element, "", "MaxDisplayAltitude", newValue.ToString());
  }
  public void InsertMaxDisplayAltitudeAt(MaxDisplayAltitudeType2 newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "MaxDisplayAltitude", index, newValue.ToString());
  }
  public void ReplaceMaxDisplayAltitudeAt(MaxDisplayAltitudeType2 newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "MaxDisplayAltitude", index, newValue.ToString());
  }
        public MaxDisplayAltitudeCollection MyMaxDisplayAltitudes = new MaxDisplayAltitudeCollection( );
        public class MaxDisplayAltitudeCollection: IEnumerable
        {
            Type_PathList2 parent;
            public Type_PathList2 Parent
   {
    set
    {
     parent = value;
    }
   }
   public MaxDisplayAltitudeEnumerator GetEnumerator()
   {
    return new MaxDisplayAltitudeEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class MaxDisplayAltitudeEnumerator: IEnumerator
        {
   int nIndex;
   Type_PathList2 parent;
   public MaxDisplayAltitudeEnumerator(Type_PathList2 par)
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
    return(nIndex < parent.MaxDisplayAltitudeCount );
   }
   public MaxDisplayAltitudeType2 Current
   {
    get
    {
     return(parent.GetMaxDisplayAltitudeAt(nIndex));
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
  public int GetPathsDirectoryMinCount()
  {
   return 1;
  }
  public int PathsDirectoryMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetPathsDirectoryMaxCount()
  {
   return 1;
  }
  public int PathsDirectoryMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetPathsDirectoryCount()
  {
   return DomChildCount(NodeType.Element, "", "PathsDirectory");
  }
  public int PathsDirectoryCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "PathsDirectory");
   }
  }
  public bool HasPathsDirectory()
  {
   return HasDomChild(NodeType.Element, "", "PathsDirectory");
  }
  public SchemaString GetPathsDirectoryAt(int index)
  {
   return new SchemaString(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "PathsDirectory", index)));
  }
  public XmlNode GetStartingPathsDirectoryCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "PathsDirectory" );
  }
  public XmlNode GetAdvancedPathsDirectoryCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "PathsDirectory", curNode );
  }
  public SchemaString GetPathsDirectoryValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new SchemaString( curNode.InnerText );
  }
  public SchemaString GetPathsDirectory()
  {
   return GetPathsDirectoryAt(0);
  }
  public SchemaString PathsDirectory
  {
   get
   {
    return GetPathsDirectoryAt(0);
   }
  }
  public void RemovePathsDirectoryAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "PathsDirectory", index);
  }
  public void RemovePathsDirectory()
  {
   while (HasPathsDirectory())
    RemovePathsDirectoryAt(0);
  }
  public void AddPathsDirectory(SchemaString newValue)
  {
   AppendDomChild(NodeType.Element, "", "PathsDirectory", newValue.ToString());
  }
  public void InsertPathsDirectoryAt(SchemaString newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "PathsDirectory", index, newValue.ToString());
  }
  public void ReplacePathsDirectoryAt(SchemaString newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "PathsDirectory", index, newValue.ToString());
  }
        public PathsDirectoryCollection MyPathsDirectorys = new PathsDirectoryCollection( );
        public class PathsDirectoryCollection: IEnumerable
        {
            Type_PathList2 parent;
            public Type_PathList2 Parent
   {
    set
    {
     parent = value;
    }
   }
   public PathsDirectoryEnumerator GetEnumerator()
   {
    return new PathsDirectoryEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class PathsDirectoryEnumerator: IEnumerator
        {
   int nIndex;
   Type_PathList2 parent;
   public PathsDirectoryEnumerator(Type_PathList2 par)
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
    return(nIndex < parent.PathsDirectoryCount );
   }
   public SchemaString Current
   {
    get
    {
     return(parent.GetPathsDirectoryAt(nIndex));
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
  public int GetRGBColorMinCount()
  {
   return 0;
  }
  public int RGBColorMinCount
  {
   get
   {
    return 0;
   }
  }
  public int GetRGBColorMaxCount()
  {
   return 1;
  }
  public int RGBColorMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetRGBColorCount()
  {
   return DomChildCount(NodeType.Element, "", "RGBColor");
  }
  public int RGBColorCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "RGBColor");
   }
  }
  public bool HasRGBColor()
  {
   return HasDomChild(NodeType.Element, "", "RGBColor");
  }
  public Type_RGBColor2 GetRGBColorAt(int index)
  {
   return new Type_RGBColor2(GetDomChildAt(NodeType.Element, "", "RGBColor", index));
  }
  public XmlNode GetStartingRGBColorCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "RGBColor" );
  }
  public XmlNode GetAdvancedRGBColorCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "RGBColor", curNode );
  }
  public Type_RGBColor2 GetRGBColorValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new Type_RGBColor2( curNode );
  }
  public Type_RGBColor2 GetRGBColor()
  {
   return GetRGBColorAt(0);
  }
  public Type_RGBColor2 RGBColor
  {
   get
   {
    return GetRGBColorAt(0);
   }
  }
  public void RemoveRGBColorAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "RGBColor", index);
  }
  public void RemoveRGBColor()
  {
   while (HasRGBColor())
    RemoveRGBColorAt(0);
  }
  public void AddRGBColor(Type_RGBColor2 newValue)
  {
   AppendDomElement("", "RGBColor", newValue);
  }
  public void InsertRGBColorAt(Type_RGBColor2 newValue, int index)
  {
   InsertDomElementAt("", "RGBColor", index, newValue);
  }
  public void ReplaceRGBColorAt(Type_RGBColor2 newValue, int index)
  {
   ReplaceDomElementAt("", "RGBColor", index, newValue);
  }
        public RGBColorCollection MyRGBColors = new RGBColorCollection( );
        public class RGBColorCollection: IEnumerable
        {
            Type_PathList2 parent;
            public Type_PathList2 Parent
   {
    set
    {
     parent = value;
    }
   }
   public RGBColorEnumerator GetEnumerator()
   {
    return new RGBColorEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class RGBColorEnumerator: IEnumerator
        {
   int nIndex;
   Type_PathList2 parent;
   public RGBColorEnumerator(Type_PathList2 par)
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
    return(nIndex < parent.RGBColorCount );
   }
   public Type_RGBColor2 Current
   {
    get
    {
     return(parent.GetRGBColorAt(nIndex));
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
  public int GetWinColorNameMinCount()
  {
   return 0;
  }
  public int WinColorNameMinCount
  {
   get
   {
    return 0;
   }
  }
  public int GetWinColorNameMaxCount()
  {
   return 1;
  }
  public int WinColorNameMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetWinColorNameCount()
  {
   return DomChildCount(NodeType.Element, "", "WinColorName");
  }
  public int WinColorNameCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "WinColorName");
   }
  }
  public bool HasWinColorName()
  {
   return HasDomChild(NodeType.Element, "", "WinColorName");
  }
  public SchemaString GetWinColorNameAt(int index)
  {
   return new SchemaString(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "WinColorName", index)));
  }
  public XmlNode GetStartingWinColorNameCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "WinColorName" );
  }
  public XmlNode GetAdvancedWinColorNameCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "WinColorName", curNode );
  }
  public SchemaString GetWinColorNameValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new SchemaString( curNode.InnerText );
  }
  public SchemaString GetWinColorName()
  {
   return GetWinColorNameAt(0);
  }
  public SchemaString WinColorName
  {
   get
   {
    return GetWinColorNameAt(0);
   }
  }
  public void RemoveWinColorNameAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "WinColorName", index);
  }
  public void RemoveWinColorName()
  {
   while (HasWinColorName())
    RemoveWinColorNameAt(0);
  }
  public void AddWinColorName(SchemaString newValue)
  {
   AppendDomChild(NodeType.Element, "", "WinColorName", newValue.ToString());
  }
  public void InsertWinColorNameAt(SchemaString newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "WinColorName", index, newValue.ToString());
  }
  public void ReplaceWinColorNameAt(SchemaString newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "WinColorName", index, newValue.ToString());
  }
        public WinColorNameCollection MyWinColorNames = new WinColorNameCollection( );
        public class WinColorNameCollection: IEnumerable
        {
            Type_PathList2 parent;
            public Type_PathList2 Parent
   {
    set
    {
     parent = value;
    }
   }
   public WinColorNameEnumerator GetEnumerator()
   {
    return new WinColorNameEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class WinColorNameEnumerator: IEnumerator
        {
   int nIndex;
   Type_PathList2 parent;
   public WinColorNameEnumerator(Type_PathList2 par)
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
    return(nIndex < parent.WinColorNameCount );
   }
   public SchemaString Current
   {
    get
    {
     return(parent.GetWinColorNameAt(nIndex));
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
  public int GetExtendedInformationMinCount()
  {
   return 0;
  }
  public int ExtendedInformationMinCount
  {
   get
   {
    return 0;
   }
  }
  public int GetExtendedInformationMaxCount()
  {
   return 1;
  }
  public int ExtendedInformationMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetExtendedInformationCount()
  {
   return DomChildCount(NodeType.Element, "", "ExtendedInformation");
  }
  public int ExtendedInformationCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "ExtendedInformation");
   }
  }
  public bool HasExtendedInformation()
  {
   return HasDomChild(NodeType.Element, "", "ExtendedInformation");
  }
  public Type_ExtendedInformation GetExtendedInformationAt(int index)
  {
   return new Type_ExtendedInformation(GetDomChildAt(NodeType.Element, "", "ExtendedInformation", index));
  }
  public XmlNode GetStartingExtendedInformationCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "ExtendedInformation" );
  }
  public XmlNode GetAdvancedExtendedInformationCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "ExtendedInformation", curNode );
  }
  public Type_ExtendedInformation GetExtendedInformationValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new Type_ExtendedInformation( curNode );
  }
  public Type_ExtendedInformation GetExtendedInformation()
  {
   return GetExtendedInformationAt(0);
  }
  public Type_ExtendedInformation ExtendedInformation
  {
   get
   {
    return GetExtendedInformationAt(0);
   }
  }
  public void RemoveExtendedInformationAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "ExtendedInformation", index);
  }
  public void RemoveExtendedInformation()
  {
   while (HasExtendedInformation())
    RemoveExtendedInformationAt(0);
  }
  public void AddExtendedInformation(Type_ExtendedInformation newValue)
  {
   AppendDomElement("", "ExtendedInformation", newValue);
  }
  public void InsertExtendedInformationAt(Type_ExtendedInformation newValue, int index)
  {
   InsertDomElementAt("", "ExtendedInformation", index, newValue);
  }
  public void ReplaceExtendedInformationAt(Type_ExtendedInformation newValue, int index)
  {
   ReplaceDomElementAt("", "ExtendedInformation", index, newValue);
  }
        public ExtendedInformationCollection MyExtendedInformations = new ExtendedInformationCollection( );
        public class ExtendedInformationCollection: IEnumerable
        {
            Type_PathList2 parent;
            public Type_PathList2 Parent
   {
    set
    {
     parent = value;
    }
   }
   public ExtendedInformationEnumerator GetEnumerator()
   {
    return new ExtendedInformationEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class ExtendedInformationEnumerator: IEnumerator
        {
   int nIndex;
   Type_PathList2 parent;
   public ExtendedInformationEnumerator(Type_PathList2 par)
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
    return(nIndex < parent.ExtendedInformationCount );
   }
   public Type_ExtendedInformation Current
   {
    get
    {
     return(parent.GetExtendedInformationAt(nIndex));
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
            MyShowAtStartups.Parent = this;
            MyNames.Parent = this;
            MyDistanceAboveSurfaces.Parent = this;
            MyMinDisplayAltitudes.Parent = this;
            MyMaxDisplayAltitudes.Parent = this;
            MyPathsDirectorys.Parent = this;
            MyRGBColors.Parent = this;
            MyWinColorNames.Parent = this;
            MyExtendedInformations.Parent = this;
 }
}
}
