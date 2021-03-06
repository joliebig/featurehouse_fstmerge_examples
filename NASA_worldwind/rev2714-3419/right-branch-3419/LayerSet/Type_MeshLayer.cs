using System;
using System.Collections;
using System.Xml;
using Altova.Types;
namespace LayerSet
{
 public class Type_MeshLayer : Altova.Xml.Node
 {
  public Type_MeshLayer() : base() { SetCollectionParents(); }
  public Type_MeshLayer(XmlDocument doc) : base(doc) { SetCollectionParents(); }
  public Type_MeshLayer(XmlNode node) : base(node) { SetCollectionParents(); }
  public Type_MeshLayer(Altova.Xml.Node node) : base(node) { SetCollectionParents(); }
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
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "Latitude"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "Latitude", i);
    InternalAdjustPrefix(DOMNode, true);
    new Type_LatitudeCoordinate2(DOMNode).AdjustPrefix();
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "Longitude"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "Longitude", i);
    InternalAdjustPrefix(DOMNode, true);
    new Type_LongitudeCoordinate2(DOMNode).AdjustPrefix();
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "Orientation"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "Orientation", i);
    InternalAdjustPrefix(DOMNode, true);
    new Type_Orientation2(DOMNode).AdjustPrefix();
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "ScaleFactor"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "ScaleFactor", i);
    InternalAdjustPrefix(DOMNode, true);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "MinViewRange"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "MinViewRange", i);
    InternalAdjustPrefix(DOMNode, true);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "MaxViewRange"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "MaxViewRange", i);
    InternalAdjustPrefix(DOMNode, true);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "MeshFilePath"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "MeshFilePath", i);
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
            Type_MeshLayer parent;
            public Type_MeshLayer Parent
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
   Type_MeshLayer parent;
   public ShowAtStartupEnumerator(Type_MeshLayer par)
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
            Type_MeshLayer parent;
            public Type_MeshLayer Parent
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
   Type_MeshLayer parent;
   public NameEnumerator(Type_MeshLayer par)
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
  public DistanceAboveSurfaceType GetDistanceAboveSurfaceAt(int index)
  {
   return new DistanceAboveSurfaceType(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "DistanceAboveSurface", index)));
  }
  public XmlNode GetStartingDistanceAboveSurfaceCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "DistanceAboveSurface" );
  }
  public XmlNode GetAdvancedDistanceAboveSurfaceCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "DistanceAboveSurface", curNode );
  }
  public DistanceAboveSurfaceType GetDistanceAboveSurfaceValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new DistanceAboveSurfaceType( curNode.InnerText );
  }
  public DistanceAboveSurfaceType GetDistanceAboveSurface()
  {
   return GetDistanceAboveSurfaceAt(0);
  }
  public DistanceAboveSurfaceType DistanceAboveSurface
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
  public void AddDistanceAboveSurface(DistanceAboveSurfaceType newValue)
  {
   AppendDomChild(NodeType.Element, "", "DistanceAboveSurface", newValue.ToString());
  }
  public void InsertDistanceAboveSurfaceAt(DistanceAboveSurfaceType newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "DistanceAboveSurface", index, newValue.ToString());
  }
  public void ReplaceDistanceAboveSurfaceAt(DistanceAboveSurfaceType newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "DistanceAboveSurface", index, newValue.ToString());
  }
        public DistanceAboveSurfaceCollection MyDistanceAboveSurfaces = new DistanceAboveSurfaceCollection( );
        public class DistanceAboveSurfaceCollection: IEnumerable
        {
            Type_MeshLayer parent;
            public Type_MeshLayer Parent
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
   Type_MeshLayer parent;
   public DistanceAboveSurfaceEnumerator(Type_MeshLayer par)
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
   public DistanceAboveSurfaceType Current
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
  public int GetLatitudeMinCount()
  {
   return 1;
  }
  public int LatitudeMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetLatitudeMaxCount()
  {
   return 1;
  }
  public int LatitudeMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetLatitudeCount()
  {
   return DomChildCount(NodeType.Element, "", "Latitude");
  }
  public int LatitudeCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "Latitude");
   }
  }
  public bool HasLatitude()
  {
   return HasDomChild(NodeType.Element, "", "Latitude");
  }
  public Type_LatitudeCoordinate2 GetLatitudeAt(int index)
  {
   return new Type_LatitudeCoordinate2(GetDomChildAt(NodeType.Element, "", "Latitude", index));
  }
  public XmlNode GetStartingLatitudeCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "Latitude" );
  }
  public XmlNode GetAdvancedLatitudeCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "Latitude", curNode );
  }
  public Type_LatitudeCoordinate2 GetLatitudeValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new Type_LatitudeCoordinate2( curNode );
  }
  public Type_LatitudeCoordinate2 GetLatitude()
  {
   return GetLatitudeAt(0);
  }
  public Type_LatitudeCoordinate2 Latitude
  {
   get
   {
    return GetLatitudeAt(0);
   }
  }
  public void RemoveLatitudeAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "Latitude", index);
  }
  public void RemoveLatitude()
  {
   while (HasLatitude())
    RemoveLatitudeAt(0);
  }
  public void AddLatitude(Type_LatitudeCoordinate2 newValue)
  {
   AppendDomElement("", "Latitude", newValue);
  }
  public void InsertLatitudeAt(Type_LatitudeCoordinate2 newValue, int index)
  {
   InsertDomElementAt("", "Latitude", index, newValue);
  }
  public void ReplaceLatitudeAt(Type_LatitudeCoordinate2 newValue, int index)
  {
   ReplaceDomElementAt("", "Latitude", index, newValue);
  }
        public LatitudeCollection MyLatitudes = new LatitudeCollection( );
        public class LatitudeCollection: IEnumerable
        {
            Type_MeshLayer parent;
            public Type_MeshLayer Parent
   {
    set
    {
     parent = value;
    }
   }
   public LatitudeEnumerator GetEnumerator()
   {
    return new LatitudeEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class LatitudeEnumerator: IEnumerator
        {
   int nIndex;
   Type_MeshLayer parent;
   public LatitudeEnumerator(Type_MeshLayer par)
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
    return(nIndex < parent.LatitudeCount );
   }
   public Type_LatitudeCoordinate2 Current
   {
    get
    {
     return(parent.GetLatitudeAt(nIndex));
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
  public int GetLongitudeMinCount()
  {
   return 1;
  }
  public int LongitudeMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetLongitudeMaxCount()
  {
   return 1;
  }
  public int LongitudeMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetLongitudeCount()
  {
   return DomChildCount(NodeType.Element, "", "Longitude");
  }
  public int LongitudeCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "Longitude");
   }
  }
  public bool HasLongitude()
  {
   return HasDomChild(NodeType.Element, "", "Longitude");
  }
  public Type_LongitudeCoordinate2 GetLongitudeAt(int index)
  {
   return new Type_LongitudeCoordinate2(GetDomChildAt(NodeType.Element, "", "Longitude", index));
  }
  public XmlNode GetStartingLongitudeCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "Longitude" );
  }
  public XmlNode GetAdvancedLongitudeCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "Longitude", curNode );
  }
  public Type_LongitudeCoordinate2 GetLongitudeValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new Type_LongitudeCoordinate2( curNode );
  }
  public Type_LongitudeCoordinate2 GetLongitude()
  {
   return GetLongitudeAt(0);
  }
  public Type_LongitudeCoordinate2 Longitude
  {
   get
   {
    return GetLongitudeAt(0);
   }
  }
  public void RemoveLongitudeAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "Longitude", index);
  }
  public void RemoveLongitude()
  {
   while (HasLongitude())
    RemoveLongitudeAt(0);
  }
  public void AddLongitude(Type_LongitudeCoordinate2 newValue)
  {
   AppendDomElement("", "Longitude", newValue);
  }
  public void InsertLongitudeAt(Type_LongitudeCoordinate2 newValue, int index)
  {
   InsertDomElementAt("", "Longitude", index, newValue);
  }
  public void ReplaceLongitudeAt(Type_LongitudeCoordinate2 newValue, int index)
  {
   ReplaceDomElementAt("", "Longitude", index, newValue);
  }
        public LongitudeCollection MyLongitudes = new LongitudeCollection( );
        public class LongitudeCollection: IEnumerable
        {
            Type_MeshLayer parent;
            public Type_MeshLayer Parent
   {
    set
    {
     parent = value;
    }
   }
   public LongitudeEnumerator GetEnumerator()
   {
    return new LongitudeEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class LongitudeEnumerator: IEnumerator
        {
   int nIndex;
   Type_MeshLayer parent;
   public LongitudeEnumerator(Type_MeshLayer par)
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
    return(nIndex < parent.LongitudeCount );
   }
   public Type_LongitudeCoordinate2 Current
   {
    get
    {
     return(parent.GetLongitudeAt(nIndex));
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
  public int GetOrientationMinCount()
  {
   return 1;
  }
  public int OrientationMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetOrientationMaxCount()
  {
   return 1;
  }
  public int OrientationMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetOrientationCount()
  {
   return DomChildCount(NodeType.Element, "", "Orientation");
  }
  public int OrientationCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "Orientation");
   }
  }
  public bool HasOrientation()
  {
   return HasDomChild(NodeType.Element, "", "Orientation");
  }
  public Type_Orientation2 GetOrientationAt(int index)
  {
   return new Type_Orientation2(GetDomChildAt(NodeType.Element, "", "Orientation", index));
  }
  public XmlNode GetStartingOrientationCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "Orientation" );
  }
  public XmlNode GetAdvancedOrientationCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "Orientation", curNode );
  }
  public Type_Orientation2 GetOrientationValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new Type_Orientation2( curNode );
  }
  public Type_Orientation2 GetOrientation()
  {
   return GetOrientationAt(0);
  }
  public Type_Orientation2 Orientation
  {
   get
   {
    return GetOrientationAt(0);
   }
  }
  public void RemoveOrientationAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "Orientation", index);
  }
  public void RemoveOrientation()
  {
   while (HasOrientation())
    RemoveOrientationAt(0);
  }
  public void AddOrientation(Type_Orientation2 newValue)
  {
   AppendDomElement("", "Orientation", newValue);
  }
  public void InsertOrientationAt(Type_Orientation2 newValue, int index)
  {
   InsertDomElementAt("", "Orientation", index, newValue);
  }
  public void ReplaceOrientationAt(Type_Orientation2 newValue, int index)
  {
   ReplaceDomElementAt("", "Orientation", index, newValue);
  }
        public OrientationCollection MyOrientations = new OrientationCollection( );
        public class OrientationCollection: IEnumerable
        {
            Type_MeshLayer parent;
            public Type_MeshLayer Parent
   {
    set
    {
     parent = value;
    }
   }
   public OrientationEnumerator GetEnumerator()
   {
    return new OrientationEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class OrientationEnumerator: IEnumerator
        {
   int nIndex;
   Type_MeshLayer parent;
   public OrientationEnumerator(Type_MeshLayer par)
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
    return(nIndex < parent.OrientationCount );
   }
   public Type_Orientation2 Current
   {
    get
    {
     return(parent.GetOrientationAt(nIndex));
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
  public int GetScaleFactorMinCount()
  {
   return 1;
  }
  public int ScaleFactorMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetScaleFactorMaxCount()
  {
   return 1;
  }
  public int ScaleFactorMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetScaleFactorCount()
  {
   return DomChildCount(NodeType.Element, "", "ScaleFactor");
  }
  public int ScaleFactorCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "ScaleFactor");
   }
  }
  public bool HasScaleFactor()
  {
   return HasDomChild(NodeType.Element, "", "ScaleFactor");
  }
  public ScaleFactorType GetScaleFactorAt(int index)
  {
   return new ScaleFactorType(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "ScaleFactor", index)));
  }
  public XmlNode GetStartingScaleFactorCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "ScaleFactor" );
  }
  public XmlNode GetAdvancedScaleFactorCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "ScaleFactor", curNode );
  }
  public ScaleFactorType GetScaleFactorValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new ScaleFactorType( curNode.InnerText );
  }
  public ScaleFactorType GetScaleFactor()
  {
   return GetScaleFactorAt(0);
  }
  public ScaleFactorType ScaleFactor
  {
   get
   {
    return GetScaleFactorAt(0);
   }
  }
  public void RemoveScaleFactorAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "ScaleFactor", index);
  }
  public void RemoveScaleFactor()
  {
   while (HasScaleFactor())
    RemoveScaleFactorAt(0);
  }
  public void AddScaleFactor(ScaleFactorType newValue)
  {
   AppendDomChild(NodeType.Element, "", "ScaleFactor", newValue.ToString());
  }
  public void InsertScaleFactorAt(ScaleFactorType newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "ScaleFactor", index, newValue.ToString());
  }
  public void ReplaceScaleFactorAt(ScaleFactorType newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "ScaleFactor", index, newValue.ToString());
  }
        public ScaleFactorCollection MyScaleFactors = new ScaleFactorCollection( );
        public class ScaleFactorCollection: IEnumerable
        {
            Type_MeshLayer parent;
            public Type_MeshLayer Parent
   {
    set
    {
     parent = value;
    }
   }
   public ScaleFactorEnumerator GetEnumerator()
   {
    return new ScaleFactorEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class ScaleFactorEnumerator: IEnumerator
        {
   int nIndex;
   Type_MeshLayer parent;
   public ScaleFactorEnumerator(Type_MeshLayer par)
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
    return(nIndex < parent.ScaleFactorCount );
   }
   public ScaleFactorType Current
   {
    get
    {
     return(parent.GetScaleFactorAt(nIndex));
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
  public int GetMinViewRangeMinCount()
  {
   return 1;
  }
  public int MinViewRangeMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetMinViewRangeMaxCount()
  {
   return 1;
  }
  public int MinViewRangeMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetMinViewRangeCount()
  {
   return DomChildCount(NodeType.Element, "", "MinViewRange");
  }
  public int MinViewRangeCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "MinViewRange");
   }
  }
  public bool HasMinViewRange()
  {
   return HasDomChild(NodeType.Element, "", "MinViewRange");
  }
  public MinViewRangeType GetMinViewRangeAt(int index)
  {
   return new MinViewRangeType(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "MinViewRange", index)));
  }
  public XmlNode GetStartingMinViewRangeCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "MinViewRange" );
  }
  public XmlNode GetAdvancedMinViewRangeCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "MinViewRange", curNode );
  }
  public MinViewRangeType GetMinViewRangeValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new MinViewRangeType( curNode.InnerText );
  }
  public MinViewRangeType GetMinViewRange()
  {
   return GetMinViewRangeAt(0);
  }
  public MinViewRangeType MinViewRange
  {
   get
   {
    return GetMinViewRangeAt(0);
   }
  }
  public void RemoveMinViewRangeAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "MinViewRange", index);
  }
  public void RemoveMinViewRange()
  {
   while (HasMinViewRange())
    RemoveMinViewRangeAt(0);
  }
  public void AddMinViewRange(MinViewRangeType newValue)
  {
   AppendDomChild(NodeType.Element, "", "MinViewRange", newValue.ToString());
  }
  public void InsertMinViewRangeAt(MinViewRangeType newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "MinViewRange", index, newValue.ToString());
  }
  public void ReplaceMinViewRangeAt(MinViewRangeType newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "MinViewRange", index, newValue.ToString());
  }
        public MinViewRangeCollection MyMinViewRanges = new MinViewRangeCollection( );
        public class MinViewRangeCollection: IEnumerable
        {
            Type_MeshLayer parent;
            public Type_MeshLayer Parent
   {
    set
    {
     parent = value;
    }
   }
   public MinViewRangeEnumerator GetEnumerator()
   {
    return new MinViewRangeEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class MinViewRangeEnumerator: IEnumerator
        {
   int nIndex;
   Type_MeshLayer parent;
   public MinViewRangeEnumerator(Type_MeshLayer par)
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
    return(nIndex < parent.MinViewRangeCount );
   }
   public MinViewRangeType Current
   {
    get
    {
     return(parent.GetMinViewRangeAt(nIndex));
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
  public int GetMaxViewRangeMinCount()
  {
   return 1;
  }
  public int MaxViewRangeMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetMaxViewRangeMaxCount()
  {
   return 1;
  }
  public int MaxViewRangeMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetMaxViewRangeCount()
  {
   return DomChildCount(NodeType.Element, "", "MaxViewRange");
  }
  public int MaxViewRangeCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "MaxViewRange");
   }
  }
  public bool HasMaxViewRange()
  {
   return HasDomChild(NodeType.Element, "", "MaxViewRange");
  }
  public MaxViewRangeType GetMaxViewRangeAt(int index)
  {
   return new MaxViewRangeType(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "MaxViewRange", index)));
  }
  public XmlNode GetStartingMaxViewRangeCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "MaxViewRange" );
  }
  public XmlNode GetAdvancedMaxViewRangeCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "MaxViewRange", curNode );
  }
  public MaxViewRangeType GetMaxViewRangeValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new MaxViewRangeType( curNode.InnerText );
  }
  public MaxViewRangeType GetMaxViewRange()
  {
   return GetMaxViewRangeAt(0);
  }
  public MaxViewRangeType MaxViewRange
  {
   get
   {
    return GetMaxViewRangeAt(0);
   }
  }
  public void RemoveMaxViewRangeAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "MaxViewRange", index);
  }
  public void RemoveMaxViewRange()
  {
   while (HasMaxViewRange())
    RemoveMaxViewRangeAt(0);
  }
  public void AddMaxViewRange(MaxViewRangeType newValue)
  {
   AppendDomChild(NodeType.Element, "", "MaxViewRange", newValue.ToString());
  }
  public void InsertMaxViewRangeAt(MaxViewRangeType newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "MaxViewRange", index, newValue.ToString());
  }
  public void ReplaceMaxViewRangeAt(MaxViewRangeType newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "MaxViewRange", index, newValue.ToString());
  }
        public MaxViewRangeCollection MyMaxViewRanges = new MaxViewRangeCollection( );
        public class MaxViewRangeCollection: IEnumerable
        {
            Type_MeshLayer parent;
            public Type_MeshLayer Parent
   {
    set
    {
     parent = value;
    }
   }
   public MaxViewRangeEnumerator GetEnumerator()
   {
    return new MaxViewRangeEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class MaxViewRangeEnumerator: IEnumerator
        {
   int nIndex;
   Type_MeshLayer parent;
   public MaxViewRangeEnumerator(Type_MeshLayer par)
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
    return(nIndex < parent.MaxViewRangeCount );
   }
   public MaxViewRangeType Current
   {
    get
    {
     return(parent.GetMaxViewRangeAt(nIndex));
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
  public int GetMeshFilePathMinCount()
  {
   return 1;
  }
  public int MeshFilePathMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetMeshFilePathMaxCount()
  {
   return 1;
  }
  public int MeshFilePathMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetMeshFilePathCount()
  {
   return DomChildCount(NodeType.Element, "", "MeshFilePath");
  }
  public int MeshFilePathCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "MeshFilePath");
   }
  }
  public bool HasMeshFilePath()
  {
   return HasDomChild(NodeType.Element, "", "MeshFilePath");
  }
  public MeshFilePathType GetMeshFilePathAt(int index)
  {
   return new MeshFilePathType(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "MeshFilePath", index)));
  }
  public XmlNode GetStartingMeshFilePathCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "MeshFilePath" );
  }
  public XmlNode GetAdvancedMeshFilePathCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "MeshFilePath", curNode );
  }
  public MeshFilePathType GetMeshFilePathValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new MeshFilePathType( curNode.InnerText );
  }
  public MeshFilePathType GetMeshFilePath()
  {
   return GetMeshFilePathAt(0);
  }
  public MeshFilePathType MeshFilePath
  {
   get
   {
    return GetMeshFilePathAt(0);
   }
  }
  public void RemoveMeshFilePathAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "MeshFilePath", index);
  }
  public void RemoveMeshFilePath()
  {
   while (HasMeshFilePath())
    RemoveMeshFilePathAt(0);
  }
  public void AddMeshFilePath(MeshFilePathType newValue)
  {
   AppendDomChild(NodeType.Element, "", "MeshFilePath", newValue.ToString());
  }
  public void InsertMeshFilePathAt(MeshFilePathType newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "MeshFilePath", index, newValue.ToString());
  }
  public void ReplaceMeshFilePathAt(MeshFilePathType newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "MeshFilePath", index, newValue.ToString());
  }
        public MeshFilePathCollection MyMeshFilePaths = new MeshFilePathCollection( );
        public class MeshFilePathCollection: IEnumerable
        {
            Type_MeshLayer parent;
            public Type_MeshLayer Parent
   {
    set
    {
     parent = value;
    }
   }
   public MeshFilePathEnumerator GetEnumerator()
   {
    return new MeshFilePathEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class MeshFilePathEnumerator: IEnumerator
        {
   int nIndex;
   Type_MeshLayer parent;
   public MeshFilePathEnumerator(Type_MeshLayer par)
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
    return(nIndex < parent.MeshFilePathCount );
   }
   public MeshFilePathType Current
   {
    get
    {
     return(parent.GetMeshFilePathAt(nIndex));
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
            Type_MeshLayer parent;
            public Type_MeshLayer Parent
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
   Type_MeshLayer parent;
   public ExtendedInformationEnumerator(Type_MeshLayer par)
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
            MyLatitudes.Parent = this;
            MyLongitudes.Parent = this;
            MyOrientations.Parent = this;
            MyScaleFactors.Parent = this;
            MyMinViewRanges.Parent = this;
            MyMaxViewRanges.Parent = this;
            MyMeshFilePaths.Parent = this;
            MyExtendedInformations.Parent = this;
 }
}
}
