using System;
using System.Collections;
using System.Xml;
using Altova.Types;
namespace capabilities_1_1_1
{
 public class HTTPType : Altova.Xml.Node
 {
  public HTTPType() : base() { SetCollectionParents(); }
  public HTTPType(XmlDocument doc) : base(doc) { SetCollectionParents(); }
  public HTTPType(XmlNode node) : base(node) { SetCollectionParents(); }
  public HTTPType(Altova.Xml.Node node) : base(node) { SetCollectionParents(); }
  public override void AdjustPrefix()
  {
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "Get"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "Get", i);
    InternalAdjustPrefix(DOMNode, false);
    new GetType(DOMNode).AdjustPrefix();
   }
   for (int i = 0; i < DomChildCount(NodeType.Element, "", "Post"); i++)
   {
    XmlNode DOMNode = GetDomChildAt(NodeType.Element, "", "Post", i);
    InternalAdjustPrefix(DOMNode, false);
    new PostType(DOMNode).AdjustPrefix();
   }
  }
  public int GetGetMinCount()
  {
   return 1;
  }
  public int GetMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetGetMaxCount()
  {
   return 1;
  }
  public int GetMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetGetCount()
  {
   return DomChildCount(NodeType.Element, "", "Get");
  }
  public int GetCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "Get");
   }
  }
  public bool HasGet()
  {
   return HasDomChild(NodeType.Element, "", "Get");
  }
  public GetType GetGetAt(int index)
  {
   return new GetType(GetDomChildAt(NodeType.Element, "", "Get", index));
  }
  public GetType GetGet()
  {
   return GetGetAt(0);
  }
  public GetType Get
  {
   get
   {
    return GetGetAt(0);
   }
  }
  public void RemoveGetAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "Get", index);
  }
  public void RemoveGet()
  {
   while (HasGet())
    RemoveGetAt(0);
  }
  public void AddGet(GetType newValue)
  {
   AppendDomElement("", "Get", newValue);
  }
  public void InsertGetAt(GetType newValue, int index)
  {
   InsertDomElementAt("", "Get", index, newValue);
  }
  public void ReplaceGetAt(GetType newValue, int index)
  {
   ReplaceDomElementAt("", "Get", index, newValue);
  }
        public GetCollection MyGets = new GetCollection( );
        public class GetCollection: IEnumerable
        {
            HTTPType parent;
            public HTTPType Parent
   {
    set
    {
     parent = value;
    }
   }
   public GetEnumerator GetEnumerator()
   {
    return new GetEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class GetEnumerator: IEnumerator
        {
   int nIndex;
   HTTPType parent;
   public GetEnumerator(HTTPType par)
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
    return(nIndex < parent.GetCount );
   }
   public GetType Current
   {
    get
    {
     return(parent.GetGetAt(nIndex));
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
  public int GetPostMinCount()
  {
   return 1;
  }
  public int PostMinCount
  {
   get
   {
    return 1;
   }
  }
  public int GetPostMaxCount()
  {
   return 1;
  }
  public int PostMaxCount
  {
   get
   {
    return 1;
   }
  }
  public int GetPostCount()
  {
   return DomChildCount(NodeType.Element, "", "Post");
  }
  public int PostCount
  {
   get
   {
    return DomChildCount(NodeType.Element, "", "Post");
   }
  }
  public bool HasPost()
  {
   return HasDomChild(NodeType.Element, "", "Post");
  }
  public PostType GetPostAt(int index)
  {
   return new PostType(GetDomChildAt(NodeType.Element, "", "Post", index));
  }
  public PostType GetPost()
  {
   return GetPostAt(0);
  }
  public PostType Post
  {
   get
   {
    return GetPostAt(0);
   }
  }
  public void RemovePostAt(int index)
  {
   RemoveDomChildAt(NodeType.Element, "", "Post", index);
  }
  public void RemovePost()
  {
   while (HasPost())
    RemovePostAt(0);
  }
  public void AddPost(PostType newValue)
  {
   AppendDomElement("", "Post", newValue);
  }
  public void InsertPostAt(PostType newValue, int index)
  {
   InsertDomElementAt("", "Post", index, newValue);
  }
  public void ReplacePostAt(PostType newValue, int index)
  {
   ReplaceDomElementAt("", "Post", index, newValue);
  }
        public PostCollection MyPosts = new PostCollection( );
        public class PostCollection: IEnumerable
        {
            HTTPType parent;
            public HTTPType Parent
   {
    set
    {
     parent = value;
    }
   }
   public PostEnumerator GetEnumerator()
   {
    return new PostEnumerator(parent);
   }
   IEnumerator IEnumerable.GetEnumerator()
   {
    return GetEnumerator();
   }
        }
        public class PostEnumerator: IEnumerator
        {
   int nIndex;
   HTTPType parent;
   public PostEnumerator(HTTPType par)
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
    return(nIndex < parent.PostCount );
   }
   public PostType Current
   {
    get
    {
     return(parent.GetPostAt(nIndex));
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
            MyGets.Parent = this;
            MyPosts.Parent = this;
 }
}
}
