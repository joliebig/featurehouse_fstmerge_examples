using System;
using System.Collections;
using System.Text.RegularExpressions;
namespace ICSharpCode.SharpZipLib.Core
{
 public class NameFilter : IScanFilter
 {
  public NameFilter(string filter)
  {
   filter_ = filter;
   inclusions_ = new ArrayList();
   exclusions_ = new ArrayList();
   Compile();
  }
  public static bool IsValidExpression(string expression)
  {
   bool result = true;
   try
   {
    Regex exp = new Regex(expression, RegexOptions.IgnoreCase | RegexOptions.Singleline);
   }
   catch
   {
    result = false;
   }
   return result;
  }
  public static bool IsValidFilterExpression(string toTest)
  {
   if ( toTest == null )
   {
    throw new ArgumentNullException("toTest");
   }
   bool result = true;
   try
   {
    string[] items = toTest.Split(';');
    for ( int i = 0; i < items.Length; ++i )
    {
     if ( items[i] != null && items[i].Length > 0 )
     {
      string toCompile;
      if ( items[i][0] == '+' )
      {
       toCompile = items[i].Substring(1, items[i].Length - 1);
      }
      else if ( items[i][0] == '-' )
      {
       toCompile = items[i].Substring(1, items[i].Length - 1);
      }
      else
      {
       toCompile = items[i];
      }
      Regex testRegex = new Regex(toCompile, RegexOptions.IgnoreCase | RegexOptions.Singleline);
     }
    }
   }
   catch ( Exception )
   {
    result = false;
   }
   return result;
  }
  public override string ToString()
  {
   return filter_;
  }
  public bool IsIncluded(string name)
  {
   bool result = false;
   if ( inclusions_.Count == 0 )
   {
    result = true;
   }
   else
   {
    foreach ( Regex r in inclusions_ )
    {
     if ( r.IsMatch(name) )
     {
      result = true;
      break;
     }
    }
   }
   return result;
  }
  public bool IsExcluded(string name)
  {
   bool result = false;
   foreach ( Regex r in exclusions_ )
   {
    if ( r.IsMatch(name) )
    {
     result = true;
     break;
    }
   }
   return result;
  }
  public bool IsMatch(string name)
  {
   return (IsIncluded(name) == true) && (IsExcluded(name) == false);
  }
  void Compile()
  {
   if ( filter_ == null )
   {
    return;
   }
   string[] items = filter_.Split(';');
   for ( int i = 0; i < items.Length; ++i )
   {
    if ( (items[i] != null) && (items[i].Length > 0) )
    {
     bool include = (items[i][0] != '-');
     string toCompile;
     if ( items[i][0] == '+' )
     {
      toCompile = items[i].Substring(1, items[i].Length - 1);
     }
     else if ( items[i][0] == '-' )
     {
      toCompile = items[i].Substring(1, items[i].Length - 1);
     }
     else
     {
      toCompile = items[i];
     }
     if ( include )
     {
      inclusions_.Add(new Regex(toCompile, RegexOptions.IgnoreCase | RegexOptions.Compiled | RegexOptions.Singleline));
     }
     else
     {
      exclusions_.Add(new Regex(toCompile, RegexOptions.IgnoreCase | RegexOptions.Compiled | RegexOptions.Singleline));
     }
    }
   }
  }
  string filter_;
  ArrayList inclusions_;
  ArrayList exclusions_;
 }
}
