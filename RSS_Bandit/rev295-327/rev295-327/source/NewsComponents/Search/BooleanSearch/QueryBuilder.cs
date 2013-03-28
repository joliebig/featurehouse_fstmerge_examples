using System; 
using System.Text; namespace  NewsComponents.Search.BooleanSearch {
	
 public class  QueryBuilder {
		enum  Tokens 
  {
   OpenBracket,
   CloseBracket,
   OperatorAnd,
   OperatorOr,
   OperatorNot,
   Text,
   Whitespace
  } 
  private  string m_query;
 
  public  string Query
  {
   get
   {
    return m_query;
   }
   set
   {
    m_query = value;
   }
  }
 
  public  QueryBuilder(string query)
  {
   m_query = query;
  }
 
  public  bool Validate()
  {
   if (m_query == null)
    return false;
   int unclosed_brackets = 0;
   bool in_inverted_commas = false;
   Tokens last_token = Tokens.Whitespace;
   for (int i = 0; i < m_query.Length; i ++)
   {
    char cur_char = m_query[i];
    if (cur_char == '"')
     in_inverted_commas ^= true;
    if (in_inverted_commas)
     continue;
    switch(cur_char)
    {
     case '(':
      if (last_token == Tokens.CloseBracket
       || last_token == Tokens.Text
       || last_token == Tokens.OperatorNot)
        return false;
      last_token = Tokens.OpenBracket;
      unclosed_brackets ++;
      break;
     case ')':
      if (last_token == Tokens.OperatorAnd ||
       last_token == Tokens.OperatorOr ||
       last_token == Tokens.OperatorNot)
        return false;
      last_token = Tokens.CloseBracket;
      unclosed_brackets --;
      break;
     case '&':
      if (last_token == Tokens.OperatorAnd ||
       last_token == Tokens.OperatorOr ||
       last_token == Tokens.OperatorNot)
       return false;
      last_token = Tokens.OperatorAnd;
      break;
     case '|':
      if (last_token == Tokens.OperatorAnd ||
       last_token == Tokens.OperatorOr ||
       last_token == Tokens.OperatorNot)
       return false;
      last_token = Tokens.OperatorOr;
      break;
     case '!':
      last_token = Tokens.OperatorNot;
      break;
     case ' ':
      last_token = Tokens.Whitespace;
      break;
     default:
      last_token = Tokens.Text;
      break;
    }
    if (unclosed_brackets < 0)
     return false;
   }
   if (in_inverted_commas)
    return false;
   if (unclosed_brackets != 0)
    return false;
   return true;
  }
 
  public  QueryTree BuildTree()
  {
   if (Validate() == false)
    return null;
   System.Text.StringBuilder word_token = new StringBuilder();
   QueryNode tree_root = new QueryNode();
   QueryNode node = tree_root.AddChild();
   bool in_inverted_commas = false;
   for (int i = 0; i < m_query.Length; i ++)
   {
    char cur_char = m_query[i];
    if (cur_char == '"')
     in_inverted_commas ^= true;
    if ("()&|!".IndexOf(cur_char) != -1 && in_inverted_commas == false)
    {
     string token = word_token.ToString().Trim();
     if (token.Length > 0)
      node.Value = token;
     word_token.Length = 0;
     if (cur_char == '(' || cur_char == '&')
     {
      if (node.Children.Length > 0)
       node = node.InsertAbove();
      else
       node = node.AddChild();
     }
     else if (cur_char == ')')
     {
      while (node.Value.Length > 0)
       node = node.Parent;
     }
     else if (cur_char == '|')
     {
      node = node.Parent.AddChild();
     }
     else if (cur_char =='!')
     {
      node.Inverted ^= true;
     }
    }
    else
    {
     if (cur_char == '"')
      cur_char = ' ';
     word_token.Append(cur_char, 1);
    }
   }
   string final_token = word_token.ToString().Trim();
   if (final_token.Length > 0)
    node.Value = final_token;
   return new QueryTree(tree_root);
  }

	}

}
