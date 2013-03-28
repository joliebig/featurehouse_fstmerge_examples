using System;
using System.Collections;
namespace NewsComponents.Search.BooleanSearch
{
 public class QueryTree
 {
  private QueryNode m_root;
  public QueryTree(QueryNode root)
  {
   m_root = root;
  }
  private void RecurseTree(QueryNode node, ref string text, int level)
  {
   text += new string(' ', level * 3);
   if (node.Inverted)
    text += "[NOT]";
   text += node.Value;
   text += "\n";
   for (int i = 0; i < node.Children.Length; i ++)
   {
    level ++;
    RecurseTree(node.Children[i], ref text, level);
    level --;
   }
  }
  public override string ToString()
  {
   string text = "Tree:\n\n";
   RecurseTree(m_root, ref text, 1);
   return text;
  }
  public bool IsMatch(IDocument doc)
  {
   Stack stack = new Stack();
   stack.Push(m_root);
   while (stack.Count > 0)
   {
    QueryNode node = (QueryNode) stack.Pop();
    if (node.Value.Length == 0)
    {
     foreach(QueryNode child in node.Children)
      stack.Push(child);
     continue;
    }
    if (doc.Find(node.Value) ^ node.Inverted == false)
     continue;
    if (node.Children.Length == 0)
    {
     return true;
    }
    foreach(QueryNode child in node.Children)
     stack.Push(child);
   }
   return false;
  }
  public IDocument[] GetMatches(IDocument[] candidates)
  {
   ArrayList documents = new ArrayList();
   foreach(IDocument candidate in candidates)
   {
    if (IsMatch(candidate))
     documents.Add(candidate);
   }
   return (IDocument[]) documents.ToArray(typeof(IDocument));
  }
 }
}
