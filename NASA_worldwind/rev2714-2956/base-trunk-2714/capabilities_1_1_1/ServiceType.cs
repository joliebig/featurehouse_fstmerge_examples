using System;
using System.Collections;
using System.Xml;
using Altova.Types;
namespace capabilities_1_1_1
{
 public class ServiceType : Altova.Xml.Node
 {
  public ServiceType() : base() { SetCollectionParents(); }
  public ServiceType(XmlDocument doc) : base(doc) { SetCollectionParents(); }
  public ServiceType(XmlNode node) : base(node) { SetCollectionParents(); }
  public ServiceType(Altova.Xml.Node node) : base(node) { SetCollectionParents(); }
  public override void AdjustPrefix()
  {
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "Name"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "Name", i);
    InternalAdjustPrefix(DOMNode, false);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "Title"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "Title", i);
    InternalAdjustPrefix(DOMNode, false);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "Abstract"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "Abstract", i);
    InternalAdjustPrefix(DOMNode, false);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "KeywordList"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "KeywordList", i);
    InternalAdjustPrefix(DOMNode, false);
    new KeywordListType(DOMNode).AdjustPrefix();
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "OnlineResource"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "OnlineResource", i);
    InternalAdjustPrefix(DOMNode, false);
    new OnlineResourceType(DOMNode).AdjustPrefix();
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "ContactInformation"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "ContactInformation", i);
    InternalAdjustPrefix(DOMNode, false);
    new ContactInformationType(DOMNode).AdjustPrefix();
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "Fees"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "Fees", i);
    InternalAdjustPrefix(DOMNode, false);
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "AccessConstraints"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "AccessConstraints", i);
    InternalAdjustPrefix(DOMNode, false);
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
            ServiceType parent;
            public ServiceType Parent
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
   ServiceType parent;
   public NameEnumerator(ServiceType par)
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
  public int GetTitleMinCount()
  {
   return 1;
  }
  public int TitleMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetTitleMaxCount()
  {
   return 1;
  }
  public int TitleMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetTitleCount()
  {
   return DomChildCount(NodeType.Element, "", "Title");
  }
  public int TitleCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "Title");
   }
  }
  public bool HasTitle()
  {
   return HasDomChild(NodeType.Element, "", "Title");
  }
  public SchemaString GetTitleAt(int index)
  {
   return new SchemaString(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "Title", index)));
  }
  public SchemaString GetTitle()
  {
   return GetTitleAt(0);
  }
  public SchemaString Title
  {
   get
   {
    return GetTitleAt(0);
   }
  }
  public void RemoveTitleAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "Title", index);
  }
  public void RemoveTitle()
  {
   while (HasTitle())
    RemoveTitleAt(0);
  }
  public void AddTitle(SchemaString newValue)
  {
   AppendDomChild(NodeType.Element, "", "Title", newValue.ToString());
  }
  public void InsertTitleAt(SchemaString newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "Title", index, newValue.ToString());
  }
  public void ReplaceTitleAt(SchemaString newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "Title", index, newValue.ToString());
  }
        public TitleCollection MyTitles = new TitleCollection( );
        public class TitleCollection: IEnumerable
        {
            ServiceType parent;
            public ServiceType Parent
   {
    set
    {
     parent = value;
    }
   }
   public TitleEnumerator GetEnumerator()
   {
    return new TitleEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class TitleEnumerator: IEnumerator
        {
   int nIndex;
   ServiceType parent;
   public TitleEnumerator(ServiceType par)
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
    return(nIndex < parent.TitleCount );
   }
   public SchemaString Current
   {
    get
    {
     return(parent.GetTitleAt(nIndex));
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
  public int GetAbstract2MinCount()
  {
   return 0;
  }
  public int Abstract2MinCount
  {
   get
   {
    return 0;
   }
  }
  public int GetAbstract2MaxCount()
  {
   return 1;
  }
  public int Abstract2MaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetAbstract2Count()
  {
   return DomChildCount(NodeType.Element, "", "Abstract");
  }
  public int Abstract2Count
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "Abstract");
   }
  }
  public bool HasAbstract2()
  {
   return HasDomChild(NodeType.Element, "", "Abstract");
  }
  public SchemaString GetAbstract2At(int index)
  {
   return new SchemaString(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "Abstract", index)));
  }
  public SchemaString GetAbstract2()
  {
   return GetAbstract2At(0);
  }
  public SchemaString Abstract2
  {
   get
   {
    return GetAbstract2At(0);
   }
  }
  public void RemoveAbstract2At(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "Abstract", index);
  }
  public void RemoveAbstract2()
  {
   while (HasAbstract2())
    RemoveAbstract2At(0);
  }
  public void AddAbstract2(SchemaString newValue)
  {
   AppendDomChild(NodeType.Element, "", "Abstract", newValue.ToString());
  }
  public void InsertAbstract2At(SchemaString newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "Abstract", index, newValue.ToString());
  }
  public void ReplaceAbstract2At(SchemaString newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "Abstract", index, newValue.ToString());
  }
        public Abstract2Collection MyAbstract2s = new Abstract2Collection( );
        public class Abstract2Collection: IEnumerable
        {
            ServiceType parent;
            public ServiceType Parent
   {
    set
    {
     parent = value;
    }
   }
   public Abstract2Enumerator GetEnumerator()
   {
    return new Abstract2Enumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class Abstract2Enumerator: IEnumerator
        {
   int nIndex;
   ServiceType parent;
   public Abstract2Enumerator(ServiceType par)
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
    return(nIndex < parent.Abstract2Count );
   }
   public SchemaString Current
   {
    get
    {
     return(parent.GetAbstract2At(nIndex));
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
  public int GetKeywordListMinCount()
  {
   return 0;
  }
  public int KeywordListMinCount
  {
   get
   {
    return 0;
   }
  }
  public int GetKeywordListMaxCount()
  {
   return 1;
  }
  public int KeywordListMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetKeywordListCount()
  {
   return DomChildCount(NodeType.Element, "", "KeywordList");
  }
  public int KeywordListCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "KeywordList");
   }
  }
  public bool HasKeywordList()
  {
   return HasDomChild(NodeType.Element, "", "KeywordList");
  }
  public KeywordListType GetKeywordListAt(int index)
  {
   return new KeywordListType(GetDomChildAt(NodeType.Element, "", "KeywordList", index));
  }
  public KeywordListType GetKeywordList()
  {
   return GetKeywordListAt(0);
  }
  public KeywordListType KeywordList
  {
   get
   {
    return GetKeywordListAt(0);
   }
  }
  public void RemoveKeywordListAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "KeywordList", index);
  }
  public void RemoveKeywordList()
  {
   while (HasKeywordList())
    RemoveKeywordListAt(0);
  }
  public void AddKeywordList(KeywordListType newValue)
  {
   AppendDomElement("", "KeywordList", newValue);
  }
  public void InsertKeywordListAt(KeywordListType newValue, int index)
  {
   InsertDomElementAt("", "KeywordList", index, newValue);
  }
  public void ReplaceKeywordListAt(KeywordListType newValue, int index)
  {
   ReplaceDomElementAt("", "KeywordList", index, newValue);
  }
        public KeywordListCollection MyKeywordLists = new KeywordListCollection( );
        public class KeywordListCollection: IEnumerable
        {
            ServiceType parent;
            public ServiceType Parent
   {
    set
    {
     parent = value;
    }
   }
   public KeywordListEnumerator GetEnumerator()
   {
    return new KeywordListEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class KeywordListEnumerator: IEnumerator
        {
   int nIndex;
   ServiceType parent;
   public KeywordListEnumerator(ServiceType par)
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
    return(nIndex < parent.KeywordListCount );
   }
   public KeywordListType Current
   {
    get
    {
     return(parent.GetKeywordListAt(nIndex));
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
  public int GetOnlineResourceMinCount()
  {
   return 1;
  }
  public int OnlineResourceMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetOnlineResourceMaxCount()
  {
   return 1;
  }
  public int OnlineResourceMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetOnlineResourceCount()
  {
   return DomChildCount(NodeType.Element, "", "OnlineResource");
  }
  public int OnlineResourceCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "OnlineResource");
   }
  }
  public bool HasOnlineResource()
  {
   return HasDomChild(NodeType.Element, "", "OnlineResource");
  }
  public OnlineResourceType GetOnlineResourceAt(int index)
  {
   return new OnlineResourceType(GetDomChildAt(NodeType.Element, "", "OnlineResource", index));
  }
  public OnlineResourceType GetOnlineResource()
  {
   return GetOnlineResourceAt(0);
  }
  public OnlineResourceType OnlineResource
  {
   get
   {
    return GetOnlineResourceAt(0);
   }
  }
  public void RemoveOnlineResourceAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "OnlineResource", index);
  }
  public void RemoveOnlineResource()
  {
   while (HasOnlineResource())
    RemoveOnlineResourceAt(0);
  }
  public void AddOnlineResource(OnlineResourceType newValue)
  {
   AppendDomElement("", "OnlineResource", newValue);
  }
  public void InsertOnlineResourceAt(OnlineResourceType newValue, int index)
  {
   InsertDomElementAt("", "OnlineResource", index, newValue);
  }
  public void ReplaceOnlineResourceAt(OnlineResourceType newValue, int index)
  {
   ReplaceDomElementAt("", "OnlineResource", index, newValue);
  }
        public OnlineResourceCollection MyOnlineResources = new OnlineResourceCollection( );
        public class OnlineResourceCollection: IEnumerable
        {
            ServiceType parent;
            public ServiceType Parent
   {
    set
    {
     parent = value;
    }
   }
   public OnlineResourceEnumerator GetEnumerator()
   {
    return new OnlineResourceEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class OnlineResourceEnumerator: IEnumerator
        {
   int nIndex;
   ServiceType parent;
   public OnlineResourceEnumerator(ServiceType par)
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
    return(nIndex < parent.OnlineResourceCount );
   }
   public OnlineResourceType Current
   {
    get
    {
     return(parent.GetOnlineResourceAt(nIndex));
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
  public int GetContactInformationMinCount()
  {
   return 0;
  }
  public int ContactInformationMinCount
  {
   get
   {
    return 0;
   }
  }
  public int GetContactInformationMaxCount()
  {
   return 1;
  }
  public int ContactInformationMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetContactInformationCount()
  {
   return DomChildCount(NodeType.Element, "", "ContactInformation");
  }
  public int ContactInformationCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "ContactInformation");
   }
  }
  public bool HasContactInformation()
  {
   return HasDomChild(NodeType.Element, "", "ContactInformation");
  }
  public ContactInformationType GetContactInformationAt(int index)
  {
   return new ContactInformationType(GetDomChildAt(NodeType.Element, "", "ContactInformation", index));
  }
  public ContactInformationType GetContactInformation()
  {
   return GetContactInformationAt(0);
  }
  public ContactInformationType ContactInformation
  {
   get
   {
    return GetContactInformationAt(0);
   }
  }
  public void RemoveContactInformationAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "ContactInformation", index);
  }
  public void RemoveContactInformation()
  {
   while (HasContactInformation())
    RemoveContactInformationAt(0);
  }
  public void AddContactInformation(ContactInformationType newValue)
  {
   AppendDomElement("", "ContactInformation", newValue);
  }
  public void InsertContactInformationAt(ContactInformationType newValue, int index)
  {
   InsertDomElementAt("", "ContactInformation", index, newValue);
  }
  public void ReplaceContactInformationAt(ContactInformationType newValue, int index)
  {
   ReplaceDomElementAt("", "ContactInformation", index, newValue);
  }
        public ContactInformationCollection MyContactInformations = new ContactInformationCollection( );
        public class ContactInformationCollection: IEnumerable
        {
            ServiceType parent;
            public ServiceType Parent
   {
    set
    {
     parent = value;
    }
   }
   public ContactInformationEnumerator GetEnumerator()
   {
    return new ContactInformationEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class ContactInformationEnumerator: IEnumerator
        {
   int nIndex;
   ServiceType parent;
   public ContactInformationEnumerator(ServiceType par)
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
    return(nIndex < parent.ContactInformationCount );
   }
   public ContactInformationType Current
   {
    get
    {
     return(parent.GetContactInformationAt(nIndex));
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
  public int GetFeesMinCount()
  {
   return 0;
  }
  public int FeesMinCount
  {
   get
   {
    return 0;
   }
  }
  public int GetFeesMaxCount()
  {
   return 1;
  }
  public int FeesMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetFeesCount()
  {
   return DomChildCount(NodeType.Element, "", "Fees");
  }
  public int FeesCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "Fees");
   }
  }
  public bool HasFees()
  {
   return HasDomChild(NodeType.Element, "", "Fees");
  }
  public SchemaString GetFeesAt(int index)
  {
   return new SchemaString(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "Fees", index)));
  }
  public SchemaString GetFees()
  {
   return GetFeesAt(0);
  }
  public SchemaString Fees
  {
   get
   {
    return GetFeesAt(0);
   }
  }
  public void RemoveFeesAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "Fees", index);
  }
  public void RemoveFees()
  {
   while (HasFees())
    RemoveFeesAt(0);
  }
  public void AddFees(SchemaString newValue)
  {
   AppendDomChild(NodeType.Element, "", "Fees", newValue.ToString());
  }
  public void InsertFeesAt(SchemaString newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "Fees", index, newValue.ToString());
  }
  public void ReplaceFeesAt(SchemaString newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "Fees", index, newValue.ToString());
  }
        public FeesCollection MyFeess = new FeesCollection( );
        public class FeesCollection: IEnumerable
        {
            ServiceType parent;
            public ServiceType Parent
   {
    set
    {
     parent = value;
    }
   }
   public FeesEnumerator GetEnumerator()
   {
    return new FeesEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class FeesEnumerator: IEnumerator
        {
   int nIndex;
   ServiceType parent;
   public FeesEnumerator(ServiceType par)
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
    return(nIndex < parent.FeesCount );
   }
   public SchemaString Current
   {
    get
    {
     return(parent.GetFeesAt(nIndex));
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
  public int GetAccessConstraintsMinCount()
  {
   return 0;
  }
  public int AccessConstraintsMinCount
  {
   get
   {
    return 0;
   }
  }
  public int GetAccessConstraintsMaxCount()
  {
   return 1;
  }
  public int AccessConstraintsMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetAccessConstraintsCount()
  {
   return DomChildCount(NodeType.Element, "", "AccessConstraints");
  }
  public int AccessConstraintsCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "AccessConstraints");
   }
  }
  public bool HasAccessConstraints()
  {
   return HasDomChild(NodeType.Element, "", "AccessConstraints");
  }
  public SchemaString GetAccessConstraintsAt(int index)
  {
   return new SchemaString(GetDomNodeValue(GetDomChildAt(NodeType.Element, "", "AccessConstraints", index)));
  }
  public SchemaString GetAccessConstraints()
  {
   return GetAccessConstraintsAt(0);
  }
  public SchemaString AccessConstraints
  {
   get
   {
    return GetAccessConstraintsAt(0);
   }
  }
  public void RemoveAccessConstraintsAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "AccessConstraints", index);
  }
  public void RemoveAccessConstraints()
  {
   while (HasAccessConstraints())
    RemoveAccessConstraintsAt(0);
  }
  public void AddAccessConstraints(SchemaString newValue)
  {
   AppendDomChild(NodeType.Element, "", "AccessConstraints", newValue.ToString());
  }
  public void InsertAccessConstraintsAt(SchemaString newValue, int index)
  {
   InsertDomChildAt(NodeType.Element, "", "AccessConstraints", index, newValue.ToString());
  }
  public void ReplaceAccessConstraintsAt(SchemaString newValue, int index)
  {
   ReplaceDomChildAt(NodeType.Element, "", "AccessConstraints", index, newValue.ToString());
  }
        public AccessConstraintsCollection MyAccessConstraintss = new AccessConstraintsCollection( );
        public class AccessConstraintsCollection: IEnumerable
        {
            ServiceType parent;
            public ServiceType Parent
   {
    set
    {
     parent = value;
    }
   }
   public AccessConstraintsEnumerator GetEnumerator()
   {
    return new AccessConstraintsEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class AccessConstraintsEnumerator: IEnumerator
        {
   int nIndex;
   ServiceType parent;
   public AccessConstraintsEnumerator(ServiceType par)
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
    return(nIndex < parent.AccessConstraintsCount );
   }
   public SchemaString Current
   {
    get
    {
     return(parent.GetAccessConstraintsAt(nIndex));
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
            MyTitles.Parent = this;
            MyAbstract2s.Parent = this;
            MyKeywordLists.Parent = this;
            MyOnlineResources.Parent = this;
            MyContactInformations.Parent = this;
            MyFeess.Parent = this;
            MyAccessConstraintss.Parent = this;
 }
}
}
