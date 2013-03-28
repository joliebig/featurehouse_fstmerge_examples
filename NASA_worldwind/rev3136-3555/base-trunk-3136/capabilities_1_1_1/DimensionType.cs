using System;
using System.Collections;
using System.Xml;
using Altova.Types;
namespace capabilities_1_1_1
{
 public class DimensionType : Altova.Xml.Node
 {
  public DimensionType() : base() { SetCollectionParents(); }
  public DimensionType(XmlDocument doc) : base(doc) { SetCollectionParents(); }
  public DimensionType(XmlNode node) : base(node) { SetCollectionParents(); }
  public DimensionType(Altova.Xml.Node node) : base(node) { SetCollectionParents(); }
  public override void AdjustPrefix()
  {
   for (int i = 0; i < DomChildCount(NodeType.Attribute, "", "name"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Attribute, "", "name", i);
    InternalAdjustPrefix(DOMNode, false);
   }
   for (int i = 0; i < DomChildCount(NodeType.Attribute, "", "units"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Attribute, "", "units", i);
    InternalAdjustPrefix(DOMNode, false);
   }
   for (int i = 0; i < DomChildCount(NodeType.Attribute, "", "unitSymbol"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Attribute, "", "unitSymbol", i);
    InternalAdjustPrefix(DOMNode, false);
   }
  }
  public int GetnameMinCount()
  {
   return 1;
  }
  public int nameMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetnameMaxCount()
  {
   return 1;
  }
  public int nameMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetnameCount()
  {
   return DomChildCount(NodeType.Attribute, "", "name");
  }
  public int nameCount
  {
   get
   {
    return DomChildCount(NodeType.Attribute, "", "name");
   }
  }
  public bool Hasname()
  {
   return HasDomChild(NodeType.Attribute, "", "name");
  }
  public SchemaString GetnameAt(int index)
  {
   return new SchemaString(GetDomNodeValue(GetDomChildAt(NodeType.Attribute, "", "name", index)));
  }
  public SchemaString Getname()
  {
   return GetnameAt(0);
  }
  public SchemaString name
  {
   get
   {
    return GetnameAt(0);
   }
  }
  public void RemovenameAt(int index)
  {
   RemoveDomChildAt(NodeType.Attribute, "", "name", index);
  }
  public void Removename()
  {
   while (Hasname())
    RemovenameAt(0);
  }
  public void Addname(SchemaString newValue)
  {
   AppendDomChild(NodeType.Attribute, "", "name", newValue.ToString());
  }
  public void InsertnameAt(SchemaString newValue, int index)
  {
   InsertDomChildAt(NodeType.Attribute, "", "name", index, newValue.ToString());
  }
  public void ReplacenameAt(SchemaString newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Attribute, "", "name", index, newValue.ToString());
  }
        public nameCollection Mynames = new nameCollection( );
        public class nameCollection: IEnumerable
        {
            DimensionType parent;
            public DimensionType Parent
   {
    set
    {
     parent = value;
    }
   }
   public nameEnumerator GetEnumerator()
   {
    return new nameEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class nameEnumerator: IEnumerator
        {
   int nIndex;
   DimensionType parent;
   public nameEnumerator(DimensionType par)
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
    return(nIndex < parent.nameCount );
   }
   public SchemaString Current
   {
    get
    {
     return(parent.GetnameAt(nIndex));
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
  public int GetunitsMinCount()
  {
   return 1;
  }
  public int unitsMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetunitsMaxCount()
  {
   return 1;
  }
  public int unitsMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetunitsCount()
  {
   return DomChildCount(NodeType.Attribute, "", "units");
  }
  public int unitsCount
  {
   get
   {
    return DomChildCount(NodeType.Attribute, "", "units");
   }
  }
  public bool Hasunits()
  {
   return HasDomChild(NodeType.Attribute, "", "units");
  }
  public SchemaString GetunitsAt(int index)
  {
   return new SchemaString(GetDomNodeValue(GetDomChildAt(NodeType.Attribute, "", "units", index)));
  }
  public SchemaString Getunits()
  {
   return GetunitsAt(0);
  }
  public SchemaString units
  {
   get
   {
    return GetunitsAt(0);
   }
  }
  public void RemoveunitsAt(int index)
  {
   RemoveDomChildAt(NodeType.Attribute, "", "units", index);
  }
  public void Removeunits()
  {
   while (Hasunits())
    RemoveunitsAt(0);
  }
  public void Addunits(SchemaString newValue)
  {
   AppendDomChild(NodeType.Attribute, "", "units", newValue.ToString());
  }
  public void InsertunitsAt(SchemaString newValue, int index)
  {
   InsertDomChildAt(NodeType.Attribute, "", "units", index, newValue.ToString());
  }
  public void ReplaceunitsAt(SchemaString newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Attribute, "", "units", index, newValue.ToString());
  }
        public unitsCollection Myunitss = new unitsCollection( );
        public class unitsCollection: IEnumerable
        {
            DimensionType parent;
            public DimensionType Parent
   {
    set
    {
     parent = value;
    }
   }
   public unitsEnumerator GetEnumerator()
   {
    return new unitsEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class unitsEnumerator: IEnumerator
        {
   int nIndex;
   DimensionType parent;
   public unitsEnumerator(DimensionType par)
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
    return(nIndex < parent.unitsCount );
   }
   public SchemaString Current
   {
    get
    {
     return(parent.GetunitsAt(nIndex));
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
  public int GetunitSymbolMinCount()
  {
   return 0;
  }
  public int unitSymbolMinCount
  {
   get
   {
    return 0;
   }
  }
  public int GetunitSymbolMaxCount()
  {
   return 1;
  }
  public int unitSymbolMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetunitSymbolCount()
  {
   return DomChildCount(NodeType.Attribute, "", "unitSymbol");
  }
  public int unitSymbolCount
  {
   get
   {
    return DomChildCount(NodeType.Attribute, "", "unitSymbol");
   }
  }
  public bool HasunitSymbol()
  {
   return HasDomChild(NodeType.Attribute, "", "unitSymbol");
  }
  public SchemaString GetunitSymbolAt(int index)
  {
   return new SchemaString(GetDomNodeValue(GetDomChildAt(NodeType.Attribute, "", "unitSymbol", index)));
  }
  public SchemaString GetunitSymbol()
  {
   return GetunitSymbolAt(0);
  }
  public SchemaString unitSymbol
  {
   get
   {
    return GetunitSymbolAt(0);
   }
  }
  public void RemoveunitSymbolAt(int index)
  {
   RemoveDomChildAt(NodeType.Attribute, "", "unitSymbol", index);
  }
  public void RemoveunitSymbol()
  {
   while (HasunitSymbol())
    RemoveunitSymbolAt(0);
  }
  public void AddunitSymbol(SchemaString newValue)
  {
   AppendDomChild(NodeType.Attribute, "", "unitSymbol", newValue.ToString());
  }
  public void InsertunitSymbolAt(SchemaString newValue, int index)
  {
   InsertDomChildAt(NodeType.Attribute, "", "unitSymbol", index, newValue.ToString());
  }
  public void ReplaceunitSymbolAt(SchemaString newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Attribute, "", "unitSymbol", index, newValue.ToString());
  }
        public unitSymbolCollection MyunitSymbols = new unitSymbolCollection( );
        public class unitSymbolCollection: IEnumerable
        {
            DimensionType parent;
            public DimensionType Parent
   {
    set
    {
     parent = value;
    }
   }
   public unitSymbolEnumerator GetEnumerator()
   {
    return new unitSymbolEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class unitSymbolEnumerator: IEnumerator
        {
   int nIndex;
   DimensionType parent;
   public unitSymbolEnumerator(DimensionType par)
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
    return(nIndex < parent.unitSymbolCount );
   }
   public SchemaString Current
   {
    get
    {
     return(parent.GetunitSymbolAt(nIndex));
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
            Mynames.Parent = this;
            Myunitss.Parent = this;
            MyunitSymbols.Parent = this;
 }
}
}
