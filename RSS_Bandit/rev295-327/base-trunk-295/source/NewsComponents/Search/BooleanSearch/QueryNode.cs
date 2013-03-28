using System;
using System.Collections;
namespace NewsComponents.Search.BooleanSearch
{
 public class QueryNode
 {
  private ArrayList m_children = new ArrayList();
  private QueryNode m_parent;
  private string m_data = "";
  private bool m_inverted = false;
  public string Value
  {
   get
   {
    return m_data;
   }
   set
   {
    m_data = value;
   }
  }
  public QueryNode Parent
  {
   get
   {
    return m_parent;
   }
  }
  public QueryNode[] Children
  {
   get
   {
    return (QueryNode[]) m_children.ToArray(this.GetType());
   }
  }
  public bool Inverted
  {
   get
   {
    return m_inverted;
   }
   set
   {
    m_inverted = value;
   }
  }
  public QueryNode AddChild()
  {
   QueryNode child = new QueryNode();
   m_children.Add(child);
   child.m_parent = this;
   return child;
  }
  public QueryNode InsertAbove()
  {
   for(int i = 0; i < m_parent.m_children.Count; i ++)
   {
    if (m_parent.m_children[i] == this)
    {
     QueryNode new_node = new QueryNode();
     new_node.m_parent = m_parent;
     m_parent.m_children[i] = new_node;
     new_node.m_children.Add(this);
     m_parent = new_node;
     return new_node;
    }
   }
   return null;
  }
 }
}
