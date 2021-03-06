using System;
using System.Collections;
using System.Xml;
using Altova.Types;
namespace LayerSet
{
 public class Type_WMSAccessor : Altova.Xml.Node
 {
  public Type_WMSAccessor() : base() { SetCollectionParents(); }
  public Type_WMSAccessor(XmlDocument doc) : base(doc) { SetCollectionParents(); }
  public Type_WMSAccessor(XmlNode node) : base(node) { SetCollectionParents(); }
  public Type_WMSAccessor(Altova.Xml.Node node) : base(node) { SetCollectionParents(); }
  public override void AdjustPrefix()
  {
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "Username"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "Username", i);
    InternalAdjustPrefix(DOMNode, true);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "Password"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "Password", i);
    InternalAdjustPrefix(DOMNode, true);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "ServerGetMapUrl"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "ServerGetMapUrl", i);
    InternalAdjustPrefix(DOMNode, true);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "Version"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "Version", i);
    InternalAdjustPrefix(DOMNode, true);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "ImageFormat"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "ImageFormat", i);
    InternalAdjustPrefix(DOMNode, true);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "WMSLayerName"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "WMSLayerName", i);
    InternalAdjustPrefix(DOMNode, true);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "WMSLayerStyle"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "WMSLayerStyle", i);
    InternalAdjustPrefix(DOMNode, true);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "UseTransparency"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "UseTransparency", i);
    InternalAdjustPrefix(DOMNode, true);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "CacheExpirationTime"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "CacheExpirationTime", i);
    InternalAdjustPrefix(DOMNode, true);
    new Type_SimpleTimeSpan2(DOMNode).AdjustPrefix();
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "BoundingBoxOverlap"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "BoundingBoxOverlap", i);
    InternalAdjustPrefix(DOMNode, true);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "ServerLogoFilePath"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "ServerLogoFilePath", i);
    InternalAdjustPrefix(DOMNode, true);
   }
  }
  public int GetUsernameMinCount()
  {
   return 0;
  }
  public int UsernameMinCount
  {
   get
   {
    return 0;
   }
  }
  public int GetUsernameMaxCount()
  {
   return 1;
  }
  public int UsernameMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetUsernameCount()
  {
   return DomChildCount(NodeType.Element, "", "Username");
  }
  public int UsernameCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "Username");
   }
  }
  public bool HasUsername()
  {
   return HasDomChild(NodeType.Element, "", "Username");
  }
  public SchemaString GetUsernameAt(int index)
  {
   return new SchemaString(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "Username", index)));
  }
  public XmlNode GetStartingUsernameCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "Username" );
  }
  public XmlNode GetAdvancedUsernameCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "Username", curNode );
  }
  public SchemaString GetUsernameValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new SchemaString( curNode.InnerText );
  }
  public SchemaString GetUsername()
  {
   return GetUsernameAt(0);
  }
  public SchemaString Username
  {
   get
   {
    return GetUsernameAt(0);
   }
  }
  public void RemoveUsernameAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "Username", index);
  }
  public void RemoveUsername()
  {
   while (HasUsername())
    RemoveUsernameAt(0);
  }
  public void AddUsername(SchemaString newValue)
  {
   AppendDomChild(NodeType.Element, "", "Username", newValue.ToString());
  }
  public void InsertUsernameAt(SchemaString newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "Username", index, newValue.ToString());
  }
  public void ReplaceUsernameAt(SchemaString newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "Username", index, newValue.ToString());
  }
        public UsernameCollection MyUsernames = new UsernameCollection( );
        public class UsernameCollection: IEnumerable
        {
            Type_WMSAccessor parent;
            public Type_WMSAccessor Parent
   {
    set
    {
     parent = value;
    }
   }
   public UsernameEnumerator GetEnumerator()
   {
    return new UsernameEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class UsernameEnumerator: IEnumerator
        {
   int nIndex;
   Type_WMSAccessor parent;
   public UsernameEnumerator(Type_WMSAccessor par)
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
    return(nIndex < parent.UsernameCount );
   }
   public SchemaString Current
   {
    get
    {
     return(parent.GetUsernameAt(nIndex));
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
  public int GetPasswordMinCount()
  {
   return 0;
  }
  public int PasswordMinCount
  {
   get
   {
    return 0;
   }
  }
  public int GetPasswordMaxCount()
  {
   return 1;
  }
  public int PasswordMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetPasswordCount()
  {
   return DomChildCount(NodeType.Element, "", "Password");
  }
  public int PasswordCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "Password");
   }
  }
  public bool HasPassword()
  {
   return HasDomChild(NodeType.Element, "", "Password");
  }
  public SchemaString GetPasswordAt(int index)
  {
   return new SchemaString(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "Password", index)));
  }
  public XmlNode GetStartingPasswordCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "Password" );
  }
  public XmlNode GetAdvancedPasswordCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "Password", curNode );
  }
  public SchemaString GetPasswordValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new SchemaString( curNode.InnerText );
  }
  public SchemaString GetPassword()
  {
   return GetPasswordAt(0);
  }
  public SchemaString Password
  {
   get
   {
    return GetPasswordAt(0);
   }
  }
  public void RemovePasswordAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "Password", index);
  }
  public void RemovePassword()
  {
   while (HasPassword())
    RemovePasswordAt(0);
  }
  public void AddPassword(SchemaString newValue)
  {
   AppendDomChild(NodeType.Element, "", "Password", newValue.ToString());
  }
  public void InsertPasswordAt(SchemaString newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "Password", index, newValue.ToString());
  }
  public void ReplacePasswordAt(SchemaString newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "Password", index, newValue.ToString());
  }
        public PasswordCollection MyPasswords = new PasswordCollection( );
        public class PasswordCollection: IEnumerable
        {
            Type_WMSAccessor parent;
            public Type_WMSAccessor Parent
   {
    set
    {
     parent = value;
    }
   }
   public PasswordEnumerator GetEnumerator()
   {
    return new PasswordEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class PasswordEnumerator: IEnumerator
        {
   int nIndex;
   Type_WMSAccessor parent;
   public PasswordEnumerator(Type_WMSAccessor par)
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
    return(nIndex < parent.PasswordCount );
   }
   public SchemaString Current
   {
    get
    {
     return(parent.GetPasswordAt(nIndex));
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
  public int GetServerGetMapUrlMinCount()
  {
   return 1;
  }
  public int ServerGetMapUrlMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetServerGetMapUrlMaxCount()
  {
   return 1;
  }
  public int ServerGetMapUrlMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetServerGetMapUrlCount()
  {
   return DomChildCount(NodeType.Element, "", "ServerGetMapUrl");
  }
  public int ServerGetMapUrlCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "ServerGetMapUrl");
   }
  }
  public bool HasServerGetMapUrl()
  {
   return HasDomChild(NodeType.Element, "", "ServerGetMapUrl");
  }
  public SchemaString GetServerGetMapUrlAt(int index)
  {
   return new SchemaString(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "ServerGetMapUrl", index)));
  }
  public XmlNode GetStartingServerGetMapUrlCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "ServerGetMapUrl" );
  }
  public XmlNode GetAdvancedServerGetMapUrlCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "ServerGetMapUrl", curNode );
  }
  public SchemaString GetServerGetMapUrlValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new SchemaString( curNode.InnerText );
  }
  public SchemaString GetServerGetMapUrl()
  {
   return GetServerGetMapUrlAt(0);
  }
  public SchemaString ServerGetMapUrl
  {
   get
   {
    return GetServerGetMapUrlAt(0);
   }
  }
  public void RemoveServerGetMapUrlAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "ServerGetMapUrl", index);
  }
  public void RemoveServerGetMapUrl()
  {
   while (HasServerGetMapUrl())
    RemoveServerGetMapUrlAt(0);
  }
  public void AddServerGetMapUrl(SchemaString newValue)
  {
   AppendDomChild(NodeType.Element, "", "ServerGetMapUrl", newValue.ToString());
  }
  public void InsertServerGetMapUrlAt(SchemaString newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "ServerGetMapUrl", index, newValue.ToString());
  }
  public void ReplaceServerGetMapUrlAt(SchemaString newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "ServerGetMapUrl", index, newValue.ToString());
  }
        public ServerGetMapUrlCollection MyServerGetMapUrls = new ServerGetMapUrlCollection( );
        public class ServerGetMapUrlCollection: IEnumerable
        {
            Type_WMSAccessor parent;
            public Type_WMSAccessor Parent
   {
    set
    {
     parent = value;
    }
   }
   public ServerGetMapUrlEnumerator GetEnumerator()
   {
    return new ServerGetMapUrlEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class ServerGetMapUrlEnumerator: IEnumerator
        {
   int nIndex;
   Type_WMSAccessor parent;
   public ServerGetMapUrlEnumerator(Type_WMSAccessor par)
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
    return(nIndex < parent.ServerGetMapUrlCount );
   }
   public SchemaString Current
   {
    get
    {
     return(parent.GetServerGetMapUrlAt(nIndex));
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
  public int GetVersionMinCount()
  {
   return 1;
  }
  public int VersionMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetVersionMaxCount()
  {
   return 1;
  }
  public int VersionMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetVersionCount()
  {
   return DomChildCount(NodeType.Element, "", "Version");
  }
  public int VersionCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "Version");
   }
  }
  public bool HasVersion()
  {
   return HasDomChild(NodeType.Element, "", "Version");
  }
  public SchemaString GetVersionAt(int index)
  {
   return new SchemaString(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "Version", index)));
  }
  public XmlNode GetStartingVersionCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "Version" );
  }
  public XmlNode GetAdvancedVersionCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "Version", curNode );
  }
  public SchemaString GetVersionValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new SchemaString( curNode.InnerText );
  }
  public SchemaString GetVersion()
  {
   return GetVersionAt(0);
  }
  public SchemaString Version
  {
   get
   {
    return GetVersionAt(0);
   }
  }
  public void RemoveVersionAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "Version", index);
  }
  public void RemoveVersion()
  {
   while (HasVersion())
    RemoveVersionAt(0);
  }
  public void AddVersion(SchemaString newValue)
  {
   AppendDomChild(NodeType.Element, "", "Version", newValue.ToString());
  }
  public void InsertVersionAt(SchemaString newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "Version", index, newValue.ToString());
  }
  public void ReplaceVersionAt(SchemaString newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "Version", index, newValue.ToString());
  }
        public VersionCollection MyVersions = new VersionCollection( );
        public class VersionCollection: IEnumerable
        {
            Type_WMSAccessor parent;
            public Type_WMSAccessor Parent
   {
    set
    {
     parent = value;
    }
   }
   public VersionEnumerator GetEnumerator()
   {
    return new VersionEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class VersionEnumerator: IEnumerator
        {
   int nIndex;
   Type_WMSAccessor parent;
   public VersionEnumerator(Type_WMSAccessor par)
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
    return(nIndex < parent.VersionCount );
   }
   public SchemaString Current
   {
    get
    {
     return(parent.GetVersionAt(nIndex));
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
  public int GetImageFormatMinCount()
  {
   return 1;
  }
  public int ImageFormatMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetImageFormatMaxCount()
  {
   return 1;
  }
  public int ImageFormatMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetImageFormatCount()
  {
   return DomChildCount(NodeType.Element, "", "ImageFormat");
  }
  public int ImageFormatCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "ImageFormat");
   }
  }
  public bool HasImageFormat()
  {
   return HasDomChild(NodeType.Element, "", "ImageFormat");
  }
  public SchemaString GetImageFormatAt(int index)
  {
   return new SchemaString(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "ImageFormat", index)));
  }
  public XmlNode GetStartingImageFormatCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "ImageFormat" );
  }
  public XmlNode GetAdvancedImageFormatCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "ImageFormat", curNode );
  }
  public SchemaString GetImageFormatValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new SchemaString( curNode.InnerText );
  }
  public SchemaString GetImageFormat()
  {
   return GetImageFormatAt(0);
  }
  public SchemaString ImageFormat
  {
   get
   {
    return GetImageFormatAt(0);
   }
  }
  public void RemoveImageFormatAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "ImageFormat", index);
  }
  public void RemoveImageFormat()
  {
   while (HasImageFormat())
    RemoveImageFormatAt(0);
  }
  public void AddImageFormat(SchemaString newValue)
  {
   AppendDomChild(NodeType.Element, "", "ImageFormat", newValue.ToString());
  }
  public void InsertImageFormatAt(SchemaString newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "ImageFormat", index, newValue.ToString());
  }
  public void ReplaceImageFormatAt(SchemaString newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "ImageFormat", index, newValue.ToString());
  }
        public ImageFormatCollection MyImageFormats = new ImageFormatCollection( );
        public class ImageFormatCollection: IEnumerable
        {
            Type_WMSAccessor parent;
            public Type_WMSAccessor Parent
   {
    set
    {
     parent = value;
    }
   }
   public ImageFormatEnumerator GetEnumerator()
   {
    return new ImageFormatEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class ImageFormatEnumerator: IEnumerator
        {
   int nIndex;
   Type_WMSAccessor parent;
   public ImageFormatEnumerator(Type_WMSAccessor par)
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
    return(nIndex < parent.ImageFormatCount );
   }
   public SchemaString Current
   {
    get
    {
     return(parent.GetImageFormatAt(nIndex));
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
  public int GetWMSLayerNameMinCount()
  {
   return 1;
  }
  public int WMSLayerNameMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetWMSLayerNameMaxCount()
  {
   return 1;
  }
  public int WMSLayerNameMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetWMSLayerNameCount()
  {
   return DomChildCount(NodeType.Element, "", "WMSLayerName");
  }
  public int WMSLayerNameCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "WMSLayerName");
   }
  }
  public bool HasWMSLayerName()
  {
   return HasDomChild(NodeType.Element, "", "WMSLayerName");
  }
  public SchemaString GetWMSLayerNameAt(int index)
  {
   return new SchemaString(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "WMSLayerName", index)));
  }
  public XmlNode GetStartingWMSLayerNameCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "WMSLayerName" );
  }
  public XmlNode GetAdvancedWMSLayerNameCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "WMSLayerName", curNode );
  }
  public SchemaString GetWMSLayerNameValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new SchemaString( curNode.InnerText );
  }
  public SchemaString GetWMSLayerName()
  {
   return GetWMSLayerNameAt(0);
  }
  public SchemaString WMSLayerName
  {
   get
   {
    return GetWMSLayerNameAt(0);
   }
  }
  public void RemoveWMSLayerNameAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "WMSLayerName", index);
  }
  public void RemoveWMSLayerName()
  {
   while (HasWMSLayerName())
    RemoveWMSLayerNameAt(0);
  }
  public void AddWMSLayerName(SchemaString newValue)
  {
   AppendDomChild(NodeType.Element, "", "WMSLayerName", newValue.ToString());
  }
  public void InsertWMSLayerNameAt(SchemaString newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "WMSLayerName", index, newValue.ToString());
  }
  public void ReplaceWMSLayerNameAt(SchemaString newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "WMSLayerName", index, newValue.ToString());
  }
        public WMSLayerNameCollection MyWMSLayerNames = new WMSLayerNameCollection( );
        public class WMSLayerNameCollection: IEnumerable
        {
            Type_WMSAccessor parent;
            public Type_WMSAccessor Parent
   {
    set
    {
     parent = value;
    }
   }
   public WMSLayerNameEnumerator GetEnumerator()
   {
    return new WMSLayerNameEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class WMSLayerNameEnumerator: IEnumerator
        {
   int nIndex;
   Type_WMSAccessor parent;
   public WMSLayerNameEnumerator(Type_WMSAccessor par)
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
    return(nIndex < parent.WMSLayerNameCount );
   }
   public SchemaString Current
   {
    get
    {
     return(parent.GetWMSLayerNameAt(nIndex));
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
  public int GetWMSLayerStyleMinCount()
  {
   return 0;
  }
  public int WMSLayerStyleMinCount
  {
   get
   {
    return 0;
   }
  }
  public int GetWMSLayerStyleMaxCount()
  {
   return 1;
  }
  public int WMSLayerStyleMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetWMSLayerStyleCount()
  {
   return DomChildCount(NodeType.Element, "", "WMSLayerStyle");
  }
  public int WMSLayerStyleCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "WMSLayerStyle");
   }
  }
  public bool HasWMSLayerStyle()
  {
   return HasDomChild(NodeType.Element, "", "WMSLayerStyle");
  }
  public SchemaString GetWMSLayerStyleAt(int index)
  {
   return new SchemaString(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "WMSLayerStyle", index)));
  }
  public XmlNode GetStartingWMSLayerStyleCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "WMSLayerStyle" );
  }
  public XmlNode GetAdvancedWMSLayerStyleCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "WMSLayerStyle", curNode );
  }
  public SchemaString GetWMSLayerStyleValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new SchemaString( curNode.InnerText );
  }
  public SchemaString GetWMSLayerStyle()
  {
   return GetWMSLayerStyleAt(0);
  }
  public SchemaString WMSLayerStyle
  {
   get
   {
    return GetWMSLayerStyleAt(0);
   }
  }
  public void RemoveWMSLayerStyleAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "WMSLayerStyle", index);
  }
  public void RemoveWMSLayerStyle()
  {
   while (HasWMSLayerStyle())
    RemoveWMSLayerStyleAt(0);
  }
  public void AddWMSLayerStyle(SchemaString newValue)
  {
   AppendDomChild(NodeType.Element, "", "WMSLayerStyle", newValue.ToString());
  }
  public void InsertWMSLayerStyleAt(SchemaString newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "WMSLayerStyle", index, newValue.ToString());
  }
  public void ReplaceWMSLayerStyleAt(SchemaString newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "WMSLayerStyle", index, newValue.ToString());
  }
        public WMSLayerStyleCollection MyWMSLayerStyles = new WMSLayerStyleCollection( );
        public class WMSLayerStyleCollection: IEnumerable
        {
            Type_WMSAccessor parent;
            public Type_WMSAccessor Parent
   {
    set
    {
     parent = value;
    }
   }
   public WMSLayerStyleEnumerator GetEnumerator()
   {
    return new WMSLayerStyleEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class WMSLayerStyleEnumerator: IEnumerator
        {
   int nIndex;
   Type_WMSAccessor parent;
   public WMSLayerStyleEnumerator(Type_WMSAccessor par)
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
    return(nIndex < parent.WMSLayerStyleCount );
   }
   public SchemaString Current
   {
    get
    {
     return(parent.GetWMSLayerStyleAt(nIndex));
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
  public int GetUseTransparencyMinCount()
  {
   return 1;
  }
  public int UseTransparencyMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetUseTransparencyMaxCount()
  {
   return 1;
  }
  public int UseTransparencyMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetUseTransparencyCount()
  {
   return DomChildCount(NodeType.Element, "", "UseTransparency");
  }
  public int UseTransparencyCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "UseTransparency");
   }
  }
  public bool HasUseTransparency()
  {
   return HasDomChild(NodeType.Element, "", "UseTransparency");
  }
  public SchemaBoolean GetUseTransparencyAt(int index)
  {
   return new SchemaBoolean(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "UseTransparency", index)));
  }
  public XmlNode GetStartingUseTransparencyCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "UseTransparency" );
  }
  public XmlNode GetAdvancedUseTransparencyCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "UseTransparency", curNode );
  }
  public SchemaBoolean GetUseTransparencyValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new SchemaBoolean( curNode.InnerText );
  }
  public SchemaBoolean GetUseTransparency()
  {
   return GetUseTransparencyAt(0);
  }
  public SchemaBoolean UseTransparency
  {
   get
   {
    return GetUseTransparencyAt(0);
   }
  }
  public void RemoveUseTransparencyAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "UseTransparency", index);
  }
  public void RemoveUseTransparency()
  {
   while (HasUseTransparency())
    RemoveUseTransparencyAt(0);
  }
  public void AddUseTransparency(SchemaBoolean newValue)
  {
   AppendDomChild(NodeType.Element, "", "UseTransparency", newValue.ToString());
  }
  public void InsertUseTransparencyAt(SchemaBoolean newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "UseTransparency", index, newValue.ToString());
  }
  public void ReplaceUseTransparencyAt(SchemaBoolean newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "UseTransparency", index, newValue.ToString());
  }
        public UseTransparencyCollection MyUseTransparencys = new UseTransparencyCollection( );
        public class UseTransparencyCollection: IEnumerable
        {
            Type_WMSAccessor parent;
            public Type_WMSAccessor Parent
   {
    set
    {
     parent = value;
    }
   }
   public UseTransparencyEnumerator GetEnumerator()
   {
    return new UseTransparencyEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class UseTransparencyEnumerator: IEnumerator
        {
   int nIndex;
   Type_WMSAccessor parent;
   public UseTransparencyEnumerator(Type_WMSAccessor par)
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
    return(nIndex < parent.UseTransparencyCount );
   }
   public SchemaBoolean Current
   {
    get
    {
     return(parent.GetUseTransparencyAt(nIndex));
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
  public int GetCacheExpirationTimeMinCount()
  {
   return 0;
  }
  public int CacheExpirationTimeMinCount
  {
   get
   {
    return 0;
   }
  }
  public int GetCacheExpirationTimeMaxCount()
  {
   return 1;
  }
  public int CacheExpirationTimeMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetCacheExpirationTimeCount()
  {
   return DomChildCount(NodeType.Element, "", "CacheExpirationTime");
  }
  public int CacheExpirationTimeCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "CacheExpirationTime");
   }
  }
  public bool HasCacheExpirationTime()
  {
   return HasDomChild(NodeType.Element, "", "CacheExpirationTime");
  }
  public Type_SimpleTimeSpan2 GetCacheExpirationTimeAt(int index)
  {
   return new Type_SimpleTimeSpan2(GetDomChildAt(NodeType.Element, "", "CacheExpirationTime", index));
  }
  public XmlNode GetStartingCacheExpirationTimeCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "CacheExpirationTime" );
  }
  public XmlNode GetAdvancedCacheExpirationTimeCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "CacheExpirationTime", curNode );
  }
  public Type_SimpleTimeSpan2 GetCacheExpirationTimeValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new Type_SimpleTimeSpan2( curNode );
  }
  public Type_SimpleTimeSpan2 GetCacheExpirationTime()
  {
   return GetCacheExpirationTimeAt(0);
  }
  public Type_SimpleTimeSpan2 CacheExpirationTime
  {
   get
   {
    return GetCacheExpirationTimeAt(0);
   }
  }
  public void RemoveCacheExpirationTimeAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "CacheExpirationTime", index);
  }
  public void RemoveCacheExpirationTime()
  {
   while (HasCacheExpirationTime())
    RemoveCacheExpirationTimeAt(0);
  }
  public void AddCacheExpirationTime(Type_SimpleTimeSpan2 newValue)
  {
   AppendDomElement("", "CacheExpirationTime", newValue);
  }
  public void InsertCacheExpirationTimeAt(Type_SimpleTimeSpan2 newValue, int index)
  {
   InsertDomElementAt("", "CacheExpirationTime", index, newValue);
  }
  public void ReplaceCacheExpirationTimeAt(Type_SimpleTimeSpan2 newValue, int index)
  {
   ReplaceDomElementAt("", "CacheExpirationTime", index, newValue);
  }
        public CacheExpirationTimeCollection MyCacheExpirationTimes = new CacheExpirationTimeCollection( );
        public class CacheExpirationTimeCollection: IEnumerable
        {
            Type_WMSAccessor parent;
            public Type_WMSAccessor Parent
   {
    set
    {
     parent = value;
    }
   }
   public CacheExpirationTimeEnumerator GetEnumerator()
   {
    return new CacheExpirationTimeEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class CacheExpirationTimeEnumerator: IEnumerator
        {
   int nIndex;
   Type_WMSAccessor parent;
   public CacheExpirationTimeEnumerator(Type_WMSAccessor par)
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
    return(nIndex < parent.CacheExpirationTimeCount );
   }
   public Type_SimpleTimeSpan2 Current
   {
    get
    {
     return(parent.GetCacheExpirationTimeAt(nIndex));
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
  public int GetBoundingBoxOverlapMinCount()
  {
   return 0;
  }
  public int BoundingBoxOverlapMinCount
  {
   get
   {
    return 0;
   }
  }
  public int GetBoundingBoxOverlapMaxCount()
  {
   return 1;
  }
  public int BoundingBoxOverlapMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetBoundingBoxOverlapCount()
  {
   return DomChildCount(NodeType.Element, "", "BoundingBoxOverlap");
  }
  public int BoundingBoxOverlapCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "BoundingBoxOverlap");
   }
  }
  public bool HasBoundingBoxOverlap()
  {
   return HasDomChild(NodeType.Element, "", "BoundingBoxOverlap");
  }
  public BoundingBoxOverlapType GetBoundingBoxOverlapAt(int index)
  {
   return new BoundingBoxOverlapType(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "BoundingBoxOverlap", index)));
  }
  public XmlNode GetStartingBoundingBoxOverlapCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "BoundingBoxOverlap" );
  }
  public XmlNode GetAdvancedBoundingBoxOverlapCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "BoundingBoxOverlap", curNode );
  }
  public BoundingBoxOverlapType GetBoundingBoxOverlapValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new BoundingBoxOverlapType( curNode.InnerText );
  }
  public BoundingBoxOverlapType GetBoundingBoxOverlap()
  {
   return GetBoundingBoxOverlapAt(0);
  }
  public BoundingBoxOverlapType BoundingBoxOverlap
  {
   get
   {
    return GetBoundingBoxOverlapAt(0);
   }
  }
  public void RemoveBoundingBoxOverlapAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "BoundingBoxOverlap", index);
  }
  public void RemoveBoundingBoxOverlap()
  {
   while (HasBoundingBoxOverlap())
    RemoveBoundingBoxOverlapAt(0);
  }
  public void AddBoundingBoxOverlap(BoundingBoxOverlapType newValue)
  {
   AppendDomChild(NodeType.Element, "", "BoundingBoxOverlap", newValue.ToString());
  }
  public void InsertBoundingBoxOverlapAt(BoundingBoxOverlapType newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "BoundingBoxOverlap", index, newValue.ToString());
  }
  public void ReplaceBoundingBoxOverlapAt(BoundingBoxOverlapType newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "BoundingBoxOverlap", index, newValue.ToString());
  }
        public BoundingBoxOverlapCollection MyBoundingBoxOverlaps = new BoundingBoxOverlapCollection( );
        public class BoundingBoxOverlapCollection: IEnumerable
        {
            Type_WMSAccessor parent;
            public Type_WMSAccessor Parent
   {
    set
    {
     parent = value;
    }
   }
   public BoundingBoxOverlapEnumerator GetEnumerator()
   {
    return new BoundingBoxOverlapEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class BoundingBoxOverlapEnumerator: IEnumerator
        {
   int nIndex;
   Type_WMSAccessor parent;
   public BoundingBoxOverlapEnumerator(Type_WMSAccessor par)
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
    return(nIndex < parent.BoundingBoxOverlapCount );
   }
   public BoundingBoxOverlapType Current
   {
    get
    {
     return(parent.GetBoundingBoxOverlapAt(nIndex));
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
  public int GetServerLogoFilePathMinCount()
  {
   return 0;
  }
  public int ServerLogoFilePathMinCount
  {
   get
   {
    return 0;
   }
  }
  public int GetServerLogoFilePathMaxCount()
  {
   return 1;
  }
  public int ServerLogoFilePathMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetServerLogoFilePathCount()
  {
   return DomChildCount(NodeType.Element, "", "ServerLogoFilePath");
  }
  public int ServerLogoFilePathCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "ServerLogoFilePath");
   }
  }
  public bool HasServerLogoFilePath()
  {
   return HasDomChild(NodeType.Element, "", "ServerLogoFilePath");
  }
  public SchemaString GetServerLogoFilePathAt(int index)
  {
   return new SchemaString(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "ServerLogoFilePath", index)));
  }
  public XmlNode GetStartingServerLogoFilePathCursor()
  {
   return GetDomFirstChild( NodeType.Element, "", "ServerLogoFilePath" );
  }
  public XmlNode GetAdvancedServerLogoFilePathCursor( XmlNode curNode )
  {
   return GetDomNextChild( NodeType.Element, "", "ServerLogoFilePath", curNode );
  }
  public SchemaString GetServerLogoFilePathValueAtCursor( XmlNode curNode )
  {
   if( curNode == null )
      throw new Altova.Xml.XmlException("Out of range");
   else
    return new SchemaString( curNode.InnerText );
  }
  public SchemaString GetServerLogoFilePath()
  {
   return GetServerLogoFilePathAt(0);
  }
  public SchemaString ServerLogoFilePath
  {
   get
   {
    return GetServerLogoFilePathAt(0);
   }
  }
  public void RemoveServerLogoFilePathAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "ServerLogoFilePath", index);
  }
  public void RemoveServerLogoFilePath()
  {
   while (HasServerLogoFilePath())
    RemoveServerLogoFilePathAt(0);
  }
  public void AddServerLogoFilePath(SchemaString newValue)
  {
   AppendDomChild(NodeType.Element, "", "ServerLogoFilePath", newValue.ToString());
  }
  public void InsertServerLogoFilePathAt(SchemaString newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "ServerLogoFilePath", index, newValue.ToString());
  }
  public void ReplaceServerLogoFilePathAt(SchemaString newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "ServerLogoFilePath", index, newValue.ToString());
  }
        public ServerLogoFilePathCollection MyServerLogoFilePaths = new ServerLogoFilePathCollection( );
        public class ServerLogoFilePathCollection: IEnumerable
        {
            Type_WMSAccessor parent;
            public Type_WMSAccessor Parent
   {
    set
    {
     parent = value;
    }
   }
   public ServerLogoFilePathEnumerator GetEnumerator()
   {
    return new ServerLogoFilePathEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class ServerLogoFilePathEnumerator: IEnumerator
        {
   int nIndex;
   Type_WMSAccessor parent;
   public ServerLogoFilePathEnumerator(Type_WMSAccessor par)
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
    return(nIndex < parent.ServerLogoFilePathCount );
   }
   public SchemaString Current
   {
    get
    {
     return(parent.GetServerLogoFilePathAt(nIndex));
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
            MyUsernames.Parent = this;
            MyPasswords.Parent = this;
            MyServerGetMapUrls.Parent = this;
            MyVersions.Parent = this;
            MyImageFormats.Parent = this;
            MyWMSLayerNames.Parent = this;
            MyWMSLayerStyles.Parent = this;
            MyUseTransparencys.Parent = this;
            MyCacheExpirationTimes.Parent = this;
            MyBoundingBoxOverlaps.Parent = this;
            MyServerLogoFilePaths.Parent = this;
 }
}
}
