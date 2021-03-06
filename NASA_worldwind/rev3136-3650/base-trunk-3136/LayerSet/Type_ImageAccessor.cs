using System;
using System.Collections;
using System.Xml;
using Altova.Types;
namespace LayerSet
{
 public class Type_ImageAccessor : Altova.Xml.Node
 {
  public Type_ImageAccessor() : base() { SetCollectionParents(); }
  public Type_ImageAccessor(XmlDocument doc) : base(doc) { SetCollectionParents(); }
  public Type_ImageAccessor(XmlNode node) : base(node) { SetCollectionParents(); }
  public Type_ImageAccessor(Altova.Xml.Node node) : base(node) { SetCollectionParents(); }
  public override void AdjustPrefix()
  {
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "LevelZeroTileSizeDegrees"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "LevelZeroTileSizeDegrees", i);
    InternalAdjustPrefix(DOMNode, true);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "NumberLevels"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "NumberLevels", i);
    InternalAdjustPrefix(DOMNode, true);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "TextureSizePixels"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "TextureSizePixels", i);
    InternalAdjustPrefix(DOMNode, true);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "ImageFileExtension"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "ImageFileExtension", i);
    InternalAdjustPrefix(DOMNode, true);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "PermanantDirectory"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "PermanantDirectory", i);
    InternalAdjustPrefix(DOMNode, true);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "DuplicateTilePath"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "DuplicateTilePath", i);
    InternalAdjustPrefix(DOMNode, true);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "WMSAccessor"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "WMSAccessor", i);
    InternalAdjustPrefix(DOMNode, true);
    new Type_WMSAccessor2(DOMNode).AdjustPrefix();
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "ImageTileService"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "ImageTileService", i);
    InternalAdjustPrefix(DOMNode, true);
    new Type_ImageTileService2(DOMNode).AdjustPrefix();
   }
  }
  public int GetLevelZeroTileSizeDegreesMinCount()
  {
   return 1;
  }
  public int LevelZeroTileSizeDegreesMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetLevelZeroTileSizeDegreesMaxCount()
  {
   return 1;
  }
  public int LevelZeroTileSizeDegreesMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetLevelZeroTileSizeDegreesCount()
  {
   return DomChildCount(NodeType.Element, "", "LevelZeroTileSizeDegrees");
  }
  public int LevelZeroTileSizeDegreesCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "LevelZeroTileSizeDegrees");
   }
  }
  public bool HasLevelZeroTileSizeDegrees()
  {
   return HasDomChild(NodeType.Element, "", "LevelZeroTileSizeDegrees");
  }
  public LevelZeroTileSizeDegreesType GetLevelZeroTileSizeDegreesAt(int index)
  {
   return new LevelZeroTileSizeDegreesType(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "LevelZeroTileSizeDegrees", index)));
  }
  public XmlNode GetStartingLevelZeroTileSizeDegreesCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "LevelZeroTileSizeDegrees" );
  }
  public XmlNode GetAdvancedLevelZeroTileSizeDegreesCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "LevelZeroTileSizeDegrees", curNode );
  }
  public LevelZeroTileSizeDegreesType GetLevelZeroTileSizeDegreesValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new LevelZeroTileSizeDegreesType( curNode.InnerText );
  }
  public LevelZeroTileSizeDegreesType GetLevelZeroTileSizeDegrees()
  {
   return GetLevelZeroTileSizeDegreesAt(0);
  }
  public LevelZeroTileSizeDegreesType LevelZeroTileSizeDegrees
  {
   get
   {
    return GetLevelZeroTileSizeDegreesAt(0);
   }
  }
  public void RemoveLevelZeroTileSizeDegreesAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "LevelZeroTileSizeDegrees", index);
  }
  public void RemoveLevelZeroTileSizeDegrees()
  {
   while (HasLevelZeroTileSizeDegrees())
    RemoveLevelZeroTileSizeDegreesAt(0);
  }
  public void AddLevelZeroTileSizeDegrees(LevelZeroTileSizeDegreesType newValue)
  {
   AppendDomChild(NodeType.Element, "", "LevelZeroTileSizeDegrees", newValue.ToString());
  }
  public void InsertLevelZeroTileSizeDegreesAt(LevelZeroTileSizeDegreesType newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "LevelZeroTileSizeDegrees", index, newValue.ToString());
  }
  public void ReplaceLevelZeroTileSizeDegreesAt(LevelZeroTileSizeDegreesType newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "LevelZeroTileSizeDegrees", index, newValue.ToString());
  }
        public LevelZeroTileSizeDegreesCollection MyLevelZeroTileSizeDegreess = new LevelZeroTileSizeDegreesCollection( );
        public class LevelZeroTileSizeDegreesCollection: IEnumerable
        {
            Type_ImageAccessor parent;
            public Type_ImageAccessor Parent
   {
    set
    {
     parent = value;
    }
   }
   public LevelZeroTileSizeDegreesEnumerator GetEnumerator()
   {
    return new LevelZeroTileSizeDegreesEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class LevelZeroTileSizeDegreesEnumerator: IEnumerator
        {
   int nIndex;
   Type_ImageAccessor parent;
   public LevelZeroTileSizeDegreesEnumerator(Type_ImageAccessor par)
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
    return(nIndex < parent.LevelZeroTileSizeDegreesCount );
   }
   public LevelZeroTileSizeDegreesType Current
   {
    get
    {
     return(parent.GetLevelZeroTileSizeDegreesAt(nIndex));
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
  public int GetNumberLevelsMinCount()
  {
   return 1;
  }
  public int NumberLevelsMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetNumberLevelsMaxCount()
  {
   return 1;
  }
  public int NumberLevelsMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetNumberLevelsCount()
  {
   return DomChildCount(NodeType.Element, "", "NumberLevels");
  }
  public int NumberLevelsCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "NumberLevels");
   }
  }
  public bool HasNumberLevels()
  {
   return HasDomChild(NodeType.Element, "", "NumberLevels");
  }
  public SchemaInt GetNumberLevelsAt(int index)
  {
   return new SchemaInt(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "NumberLevels", index)));
  }
  public XmlNode GetStartingNumberLevelsCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "NumberLevels" );
  }
  public XmlNode GetAdvancedNumberLevelsCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "NumberLevels", curNode );
  }
  public SchemaInt GetNumberLevelsValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new SchemaInt( curNode.InnerText );
  }
  public SchemaInt GetNumberLevels()
  {
   return GetNumberLevelsAt(0);
  }
  public SchemaInt NumberLevels
  {
   get
   {
    return GetNumberLevelsAt(0);
   }
  }
  public void RemoveNumberLevelsAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "NumberLevels", index);
  }
  public void RemoveNumberLevels()
  {
   while (HasNumberLevels())
    RemoveNumberLevelsAt(0);
  }
  public void AddNumberLevels(SchemaInt newValue)
  {
   AppendDomChild(NodeType.Element, "", "NumberLevels", newValue.ToString());
  }
  public void InsertNumberLevelsAt(SchemaInt newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "NumberLevels", index, newValue.ToString());
  }
  public void ReplaceNumberLevelsAt(SchemaInt newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "NumberLevels", index, newValue.ToString());
  }
        public NumberLevelsCollection MyNumberLevelss = new NumberLevelsCollection( );
        public class NumberLevelsCollection: IEnumerable
        {
            Type_ImageAccessor parent;
            public Type_ImageAccessor Parent
   {
    set
    {
     parent = value;
    }
   }
   public NumberLevelsEnumerator GetEnumerator()
   {
    return new NumberLevelsEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class NumberLevelsEnumerator: IEnumerator
        {
   int nIndex;
   Type_ImageAccessor parent;
   public NumberLevelsEnumerator(Type_ImageAccessor par)
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
    return(nIndex < parent.NumberLevelsCount );
   }
   public SchemaInt Current
   {
    get
    {
     return(parent.GetNumberLevelsAt(nIndex));
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
  public int GetTextureSizePixelsMinCount()
  {
   return 1;
  }
  public int TextureSizePixelsMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetTextureSizePixelsMaxCount()
  {
   return 1;
  }
  public int TextureSizePixelsMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetTextureSizePixelsCount()
  {
   return DomChildCount(NodeType.Element, "", "TextureSizePixels");
  }
  public int TextureSizePixelsCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "TextureSizePixels");
   }
  }
  public bool HasTextureSizePixels()
  {
   return HasDomChild(NodeType.Element, "", "TextureSizePixels");
  }
  public SchemaInt GetTextureSizePixelsAt(int index)
  {
   return new SchemaInt(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "TextureSizePixels", index)));
  }
  public XmlNode GetStartingTextureSizePixelsCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "TextureSizePixels" );
  }
  public XmlNode GetAdvancedTextureSizePixelsCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "TextureSizePixels", curNode );
  }
  public SchemaInt GetTextureSizePixelsValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new SchemaInt( curNode.InnerText );
  }
  public SchemaInt GetTextureSizePixels()
  {
   return GetTextureSizePixelsAt(0);
  }
  public SchemaInt TextureSizePixels
  {
   get
   {
    return GetTextureSizePixelsAt(0);
   }
  }
  public void RemoveTextureSizePixelsAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "TextureSizePixels", index);
  }
  public void RemoveTextureSizePixels()
  {
   while (HasTextureSizePixels())
    RemoveTextureSizePixelsAt(0);
  }
  public void AddTextureSizePixels(SchemaInt newValue)
  {
   AppendDomChild(NodeType.Element, "", "TextureSizePixels", newValue.ToString());
  }
  public void InsertTextureSizePixelsAt(SchemaInt newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "TextureSizePixels", index, newValue.ToString());
  }
  public void ReplaceTextureSizePixelsAt(SchemaInt newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "TextureSizePixels", index, newValue.ToString());
  }
        public TextureSizePixelsCollection MyTextureSizePixelss = new TextureSizePixelsCollection( );
        public class TextureSizePixelsCollection: IEnumerable
        {
            Type_ImageAccessor parent;
            public Type_ImageAccessor Parent
   {
    set
    {
     parent = value;
    }
   }
   public TextureSizePixelsEnumerator GetEnumerator()
   {
    return new TextureSizePixelsEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class TextureSizePixelsEnumerator: IEnumerator
        {
   int nIndex;
   Type_ImageAccessor parent;
   public TextureSizePixelsEnumerator(Type_ImageAccessor par)
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
    return(nIndex < parent.TextureSizePixelsCount );
   }
   public SchemaInt Current
   {
    get
    {
     return(parent.GetTextureSizePixelsAt(nIndex));
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
  public int GetImageFileExtensionMinCount()
  {
   return 1;
  }
  public int ImageFileExtensionMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetImageFileExtensionMaxCount()
  {
   return 1;
  }
  public int ImageFileExtensionMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetImageFileExtensionCount()
  {
   return DomChildCount(NodeType.Element, "", "ImageFileExtension");
  }
  public int ImageFileExtensionCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "ImageFileExtension");
   }
  }
  public bool HasImageFileExtension()
  {
   return HasDomChild(NodeType.Element, "", "ImageFileExtension");
  }
  public SchemaString GetImageFileExtensionAt(int index)
  {
   return new SchemaString(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "ImageFileExtension", index)));
  }
  public XmlNode GetStartingImageFileExtensionCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "ImageFileExtension" );
  }
  public XmlNode GetAdvancedImageFileExtensionCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "ImageFileExtension", curNode );
  }
  public SchemaString GetImageFileExtensionValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new SchemaString( curNode.InnerText );
  }
  public SchemaString GetImageFileExtension()
  {
   return GetImageFileExtensionAt(0);
  }
  public SchemaString ImageFileExtension
  {
   get
   {
    return GetImageFileExtensionAt(0);
   }
  }
  public void RemoveImageFileExtensionAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "ImageFileExtension", index);
  }
  public void RemoveImageFileExtension()
  {
   while (HasImageFileExtension())
    RemoveImageFileExtensionAt(0);
  }
  public void AddImageFileExtension(SchemaString newValue)
  {
   AppendDomChild(NodeType.Element, "", "ImageFileExtension", newValue.ToString());
  }
  public void InsertImageFileExtensionAt(SchemaString newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "ImageFileExtension", index, newValue.ToString());
  }
  public void ReplaceImageFileExtensionAt(SchemaString newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "ImageFileExtension", index, newValue.ToString());
  }
        public ImageFileExtensionCollection MyImageFileExtensions = new ImageFileExtensionCollection( );
        public class ImageFileExtensionCollection: IEnumerable
        {
            Type_ImageAccessor parent;
            public Type_ImageAccessor Parent
   {
    set
    {
     parent = value;
    }
   }
   public ImageFileExtensionEnumerator GetEnumerator()
   {
    return new ImageFileExtensionEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class ImageFileExtensionEnumerator: IEnumerator
        {
   int nIndex;
   Type_ImageAccessor parent;
   public ImageFileExtensionEnumerator(Type_ImageAccessor par)
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
    return(nIndex < parent.ImageFileExtensionCount );
   }
   public SchemaString Current
   {
    get
    {
     return(parent.GetImageFileExtensionAt(nIndex));
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
  public int GetPermanantDirectoryMinCount()
  {
   return 0;
  }
  public int PermanantDirectoryMinCount
  {
   get
   {
    return 0;
   }
  }
  public int GetPermanantDirectoryMaxCount()
  {
   return 1;
  }
  public int PermanantDirectoryMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetPermanantDirectoryCount()
  {
   return DomChildCount(NodeType.Element, "", "PermanantDirectory");
  }
  public int PermanantDirectoryCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "PermanantDirectory");
   }
  }
  public bool HasPermanantDirectory()
  {
   return HasDomChild(NodeType.Element, "", "PermanantDirectory");
  }
  public SchemaString GetPermanantDirectoryAt(int index)
  {
   return new SchemaString(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "PermanantDirectory", index)));
  }
  public XmlNode GetStartingPermanantDirectoryCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "PermanantDirectory" );
  }
  public XmlNode GetAdvancedPermanantDirectoryCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "PermanantDirectory", curNode );
  }
  public SchemaString GetPermanantDirectoryValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new SchemaString( curNode.InnerText );
  }
  public SchemaString GetPermanantDirectory()
  {
   return GetPermanantDirectoryAt(0);
  }
  public SchemaString PermanantDirectory
  {
   get
   {
    return GetPermanantDirectoryAt(0);
   }
  }
  public void RemovePermanantDirectoryAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "PermanantDirectory", index);
  }
  public void RemovePermanantDirectory()
  {
   while (HasPermanantDirectory())
    RemovePermanantDirectoryAt(0);
  }
  public void AddPermanantDirectory(SchemaString newValue)
  {
   AppendDomChild(NodeType.Element, "", "PermanantDirectory", newValue.ToString());
  }
  public void InsertPermanantDirectoryAt(SchemaString newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "PermanantDirectory", index, newValue.ToString());
  }
  public void ReplacePermanantDirectoryAt(SchemaString newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "PermanantDirectory", index, newValue.ToString());
  }
        public PermanantDirectoryCollection MyPermanantDirectorys = new PermanantDirectoryCollection( );
        public class PermanantDirectoryCollection: IEnumerable
        {
            Type_ImageAccessor parent;
            public Type_ImageAccessor Parent
   {
    set
    {
     parent = value;
    }
   }
   public PermanantDirectoryEnumerator GetEnumerator()
   {
    return new PermanantDirectoryEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class PermanantDirectoryEnumerator: IEnumerator
        {
   int nIndex;
   Type_ImageAccessor parent;
   public PermanantDirectoryEnumerator(Type_ImageAccessor par)
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
    return(nIndex < parent.PermanantDirectoryCount );
   }
   public SchemaString Current
   {
    get
    {
     return(parent.GetPermanantDirectoryAt(nIndex));
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
  public int GetDuplicateTilePathMinCount()
  {
   return 1;
  }
  public int DuplicateTilePathMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetDuplicateTilePathMaxCount()
  {
   return 1;
  }
  public int DuplicateTilePathMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetDuplicateTilePathCount()
  {
   return DomChildCount(NodeType.Element, "", "DuplicateTilePath");
  }
  public int DuplicateTilePathCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "DuplicateTilePath");
   }
  }
  public bool HasDuplicateTilePath()
  {
   return HasDomChild(NodeType.Element, "", "DuplicateTilePath");
  }
  public SchemaString GetDuplicateTilePathAt(int index)
  {
   return new SchemaString(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "DuplicateTilePath", index)));
  }
  public XmlNode GetStartingDuplicateTilePathCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "DuplicateTilePath" );
  }
  public XmlNode GetAdvancedDuplicateTilePathCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "DuplicateTilePath", curNode );
  }
  public SchemaString GetDuplicateTilePathValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new SchemaString( curNode.InnerText );
  }
  public SchemaString GetDuplicateTilePath()
  {
   return GetDuplicateTilePathAt(0);
  }
  public SchemaString DuplicateTilePath
  {
   get
   {
    return GetDuplicateTilePathAt(0);
   }
  }
  public void RemoveDuplicateTilePathAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "DuplicateTilePath", index);
  }
  public void RemoveDuplicateTilePath()
  {
   while (HasDuplicateTilePath())
    RemoveDuplicateTilePathAt(0);
  }
  public void AddDuplicateTilePath(SchemaString newValue)
  {
   AppendDomChild(NodeType.Element, "", "DuplicateTilePath", newValue.ToString());
  }
  public void InsertDuplicateTilePathAt(SchemaString newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "DuplicateTilePath", index, newValue.ToString());
  }
  public void ReplaceDuplicateTilePathAt(SchemaString newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "DuplicateTilePath", index, newValue.ToString());
  }
        public DuplicateTilePathCollection MyDuplicateTilePaths = new DuplicateTilePathCollection( );
        public class DuplicateTilePathCollection: IEnumerable
        {
            Type_ImageAccessor parent;
            public Type_ImageAccessor Parent
   {
    set
    {
     parent = value;
    }
   }
   public DuplicateTilePathEnumerator GetEnumerator()
   {
    return new DuplicateTilePathEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class DuplicateTilePathEnumerator: IEnumerator
        {
   int nIndex;
   Type_ImageAccessor parent;
   public DuplicateTilePathEnumerator(Type_ImageAccessor par)
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
    return(nIndex < parent.DuplicateTilePathCount );
   }
   public SchemaString Current
   {
    get
    {
     return(parent.GetDuplicateTilePathAt(nIndex));
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
  public int GetWMSAccessorMinCount()
  {
   return 1;
  }
  public int WMSAccessorMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetWMSAccessorMaxCount()
  {
   return 1;
  }
  public int WMSAccessorMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetWMSAccessorCount()
  {
   return DomChildCount(NodeType.Element, "", "WMSAccessor");
  }
  public int WMSAccessorCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "WMSAccessor");
   }
  }
  public bool HasWMSAccessor()
  {
   return HasDomChild(NodeType.Element, "", "WMSAccessor");
  }
  public Type_WMSAccessor2 GetWMSAccessorAt(int index)
  {
   return new Type_WMSAccessor2(GetDomChildAt(NodeType.Element, "", "WMSAccessor", index));
  }
  public XmlNode GetStartingWMSAccessorCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "WMSAccessor" );
  }
  public XmlNode GetAdvancedWMSAccessorCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "WMSAccessor", curNode );
  }
  public Type_WMSAccessor2 GetWMSAccessorValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new Type_WMSAccessor2( curNode );
  }
  public Type_WMSAccessor2 GetWMSAccessor()
  {
   return GetWMSAccessorAt(0);
  }
  public Type_WMSAccessor2 WMSAccessor
  {
   get
   {
    return GetWMSAccessorAt(0);
   }
  }
  public void RemoveWMSAccessorAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "WMSAccessor", index);
  }
  public void RemoveWMSAccessor()
  {
   while (HasWMSAccessor())
    RemoveWMSAccessorAt(0);
  }
  public void AddWMSAccessor(Type_WMSAccessor2 newValue)
  {
   AppendDomElement("", "WMSAccessor", newValue);
  }
  public void InsertWMSAccessorAt(Type_WMSAccessor2 newValue, int index)
  {
   InsertDomElementAt("", "WMSAccessor", index, newValue);
  }
  public void ReplaceWMSAccessorAt(Type_WMSAccessor2 newValue, int index)
  {
   ReplaceDomElementAt("", "WMSAccessor", index, newValue);
  }
        public WMSAccessorCollection MyWMSAccessors = new WMSAccessorCollection( );
        public class WMSAccessorCollection: IEnumerable
        {
            Type_ImageAccessor parent;
            public Type_ImageAccessor Parent
   {
    set
    {
     parent = value;
    }
   }
   public WMSAccessorEnumerator GetEnumerator()
   {
    return new WMSAccessorEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class WMSAccessorEnumerator: IEnumerator
        {
   int nIndex;
   Type_ImageAccessor parent;
   public WMSAccessorEnumerator(Type_ImageAccessor par)
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
    return(nIndex < parent.WMSAccessorCount );
   }
   public Type_WMSAccessor2 Current
   {
    get
    {
     return(parent.GetWMSAccessorAt(nIndex));
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
  public int GetImageTileServiceMinCount()
  {
   return 1;
  }
  public int ImageTileServiceMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetImageTileServiceMaxCount()
  {
   return 1;
  }
  public int ImageTileServiceMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetImageTileServiceCount()
  {
   return DomChildCount(NodeType.Element, "", "ImageTileService");
  }
  public int ImageTileServiceCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "ImageTileService");
   }
  }
  public bool HasImageTileService()
  {
   return HasDomChild(NodeType.Element, "", "ImageTileService");
  }
  public Type_ImageTileService2 GetImageTileServiceAt(int index)
  {
   return new Type_ImageTileService2(GetDomChildAt(NodeType.Element, "", "ImageTileService", index));
  }
  public XmlNode GetStartingImageTileServiceCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "ImageTileService" );
  }
  public XmlNode GetAdvancedImageTileServiceCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "ImageTileService", curNode );
  }
  public Type_ImageTileService2 GetImageTileServiceValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new Type_ImageTileService2( curNode );
  }
  public Type_ImageTileService2 GetImageTileService()
  {
   return GetImageTileServiceAt(0);
  }
  public Type_ImageTileService2 ImageTileService
  {
   get
   {
    return GetImageTileServiceAt(0);
   }
  }
  public void RemoveImageTileServiceAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "ImageTileService", index);
  }
  public void RemoveImageTileService()
  {
   while (HasImageTileService())
    RemoveImageTileServiceAt(0);
  }
  public void AddImageTileService(Type_ImageTileService2 newValue)
  {
   AppendDomElement("", "ImageTileService", newValue);
  }
  public void InsertImageTileServiceAt(Type_ImageTileService2 newValue, int index)
  {
   InsertDomElementAt("", "ImageTileService", index, newValue);
  }
  public void ReplaceImageTileServiceAt(Type_ImageTileService2 newValue, int index)
  {
   ReplaceDomElementAt("", "ImageTileService", index, newValue);
  }
        public ImageTileServiceCollection MyImageTileServices = new ImageTileServiceCollection( );
        public class ImageTileServiceCollection: IEnumerable
        {
            Type_ImageAccessor parent;
            public Type_ImageAccessor Parent
   {
    set
    {
     parent = value;
    }
   }
   public ImageTileServiceEnumerator GetEnumerator()
   {
    return new ImageTileServiceEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class ImageTileServiceEnumerator: IEnumerator
        {
   int nIndex;
   Type_ImageAccessor parent;
   public ImageTileServiceEnumerator(Type_ImageAccessor par)
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
    return(nIndex < parent.ImageTileServiceCount );
   }
   public Type_ImageTileService2 Current
   {
    get
    {
     return(parent.GetImageTileServiceAt(nIndex));
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
            MyLevelZeroTileSizeDegreess.Parent = this;
            MyNumberLevelss.Parent = this;
            MyTextureSizePixelss.Parent = this;
            MyImageFileExtensions.Parent = this;
            MyPermanantDirectorys.Parent = this;
            MyDuplicateTilePaths.Parent = this;
            MyWMSAccessors.Parent = this;
            MyImageTileServices.Parent = this;
 }
}
}
