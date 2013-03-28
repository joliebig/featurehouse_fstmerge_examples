using System;
using System.Collections;
using System.Text;
using System.Xml.Serialization;
using System.Reflection;
namespace NewsComponents.Xml.Serialization
{
 public sealed class XmlAttributeOverridesThumbprinter
 {
  private XmlAttributeOverridesThumbprinter()
  {
  }
  public static string GetThumbprint( XmlAttributeOverrides overrides )
  {
   return GetClassThumbprint(overrides);
  }
  private static string GetClassThumbprint(XmlAttributeOverrides overrides)
  {
   Hashtable types = GetTypesHashtable(overrides);
   StringSorter sorter = new StringSorter();
   foreach (Type t in types.Keys)
   {
    sorter.AddString(t.AssemblyQualifiedName);
   }
   string[] sortedTypeNames = sorter.GetOrderedArray();
   StringBuilder printBuilder = new StringBuilder();
   foreach (string typeName in sortedTypeNames)
   {
    System.Diagnostics.Debug.WriteLine(string.Format("+++ Starting thumbprint for type {0}", typeName));
    printBuilder.AppendFormat(">>{0}>>", typeName);
    GetTypePrint(typeName, types, printBuilder);
    printBuilder.Append("<<");
    System.Diagnostics.Debug.WriteLine(string.Format("--- Finished thumbprint for type {0}", typeName));
   }
   return printBuilder.ToString();
  }
  private static void GetTypePrint(string typeName, Hashtable attributes, StringBuilder printBuilder)
  {
   Type t = Type.GetType(typeName);
   Hashtable memberAttributes = attributes[t] as Hashtable;
   System.Diagnostics.Debug.Assert(null != memberAttributes);
   StringSorter sorter = new StringSorter();
   foreach (string memberName in memberAttributes.Keys)
   {
    sorter.AddString(memberName);
   }
   string[] sortedMemberNames = sorter.GetOrderedArray();
   foreach (string memberName in sortedMemberNames)
   {
    printBuilder.AppendFormat("**{0}**", memberName);
    System.Diagnostics.Debug.WriteLine(string.Format("++ Started member thumbprint for type {0}, member:{1}.", typeName, memberName));
    GetXmlAttributesThumbprint(memberAttributes[memberName] as XmlAttributes, printBuilder);
    printBuilder.Append("***");
    System.Diagnostics.Debug.WriteLine(string.Format("-- Finished member thumbprint for type {0}, member:{1}.", typeName, memberName));
   }
  }
  private static void GetXmlAttributesThumbprint(XmlAttributes atts, StringBuilder printBuilder )
  {
   if (null == atts) return;
   printBuilder.Append("any");
   printBuilder.Append(":");
   AddXmlAnyElementsPrint(atts.XmlAnyElements, printBuilder);
   printBuilder.Append(":");
   AddXmlArrayPrint(atts.XmlArray, printBuilder);
   printBuilder.Append(":");
   AddXmlArrayItemsPrint(atts.XmlArrayItems, printBuilder);
   printBuilder.Append(":");
   AddXmlAttributePrint( atts.XmlAttribute, printBuilder );
   printBuilder.Append(":");
   AddXmlChoiceIdentifierPrint( atts.XmlChoiceIdentifier, printBuilder );
   printBuilder.Append(":");
   AddXmlDefaultValuePrint( atts.XmlDefaultValue, printBuilder );
   printBuilder.Append(":");
   AddXmlElementsPrint( atts.XmlElements, printBuilder );
   printBuilder.Append(":");
   AddXmlEnumPrint( atts.XmlEnum, printBuilder );
   printBuilder.Append(":");
   AddXmlIgnorePrint( atts.XmlIgnore, printBuilder );
   printBuilder.Append(":");
   AddXmlNamespacePrint( atts.Xmlns, printBuilder );
   printBuilder.Append(":");
   AddXmlRootPrint( atts.XmlRoot, printBuilder );
   printBuilder.Append(":");
   AddXmlTextPrint( atts.XmlText, printBuilder );
   printBuilder.Append(":");
   AddXmlTypePrint( atts.XmlType, printBuilder );
  }
  private static void AddXmlAnyElementsPrint(XmlAnyElementAttributes atts, StringBuilder printBuilder)
  {
   if (null != atts)
   {
    foreach (XmlAnyElementAttribute att in atts)
    {
     printBuilder.Append("anyatt");
     printBuilder.Append("/");
     printBuilder.Append(att.Name);
     printBuilder.Append("/");
     printBuilder.Append(att.Namespace);
     printBuilder.Append("::");
    }
   }
   printBuilder.Append("%%");
  }
  private static void AddXmlArrayPrint(XmlArrayAttribute att, StringBuilder printBuilder)
  {
   if (null != att)
   {
    printBuilder.Append(att.ElementName);
    printBuilder.Append("/");
    printBuilder.Append(att.Form);
    printBuilder.Append("/");
    printBuilder.Append(att.IsNullable);
    printBuilder.Append("/");
    printBuilder.Append(att.Namespace);
   }
   printBuilder.Append("%%");
  }
  private static void AddXmlArrayItemsPrint( XmlArrayItemAttributes atts, StringBuilder printBuilder )
  {
   if (null != atts)
   {
    foreach (XmlArrayItemAttribute att in atts)
    {
     printBuilder.Append(att.DataType);
     printBuilder.Append("/");
     printBuilder.Append(att.ElementName);
     printBuilder.Append("/");
     printBuilder.Append(att.Form);
     printBuilder.Append("/");
     printBuilder.Append(att.IsNullable);
     printBuilder.Append("/");
     printBuilder.Append(att.Namespace);
     printBuilder.Append("/");
     printBuilder.Append(att.NestingLevel);
     if (null != att.Type)
     {
      printBuilder.Append("/");
      printBuilder.Append(att.Type.AssemblyQualifiedName);
     }
     printBuilder.Append("::");
    }
   }
   printBuilder.Append("%%");
  }
  private static void AddXmlAttributePrint(XmlAttributeAttribute att, StringBuilder printBuilder)
  {
   if (null != att)
   {
    printBuilder.Append(att.AttributeName);
    printBuilder.Append("/");
    printBuilder.Append(att.DataType);
    printBuilder.Append("/");
    printBuilder.Append(att.Form);
    printBuilder.Append("/");
    printBuilder.Append(att.Namespace);
    printBuilder.Append("/");
    if (null != att.Type)
    {
     printBuilder.Append(att.Type.AssemblyQualifiedName);
    }
   }
   printBuilder.Append("%%");
  }
  private static void AddXmlChoiceIdentifierPrint(XmlChoiceIdentifierAttribute att, StringBuilder printBuilder)
  {
   if (null != att)
   {
    printBuilder.Append(att.MemberName);
   }
   printBuilder.Append("%%");
  }
  private static void AddXmlDefaultValuePrint(object defaultValue, StringBuilder printBuilder)
  {
   if (null != defaultValue)
   {
    printBuilder.Append(defaultValue);
   }
   printBuilder.Append("%%");
  }
  private static void AddXmlElementsPrint(XmlElementAttributes atts, StringBuilder printBuilder)
  {
   if (null != atts)
   {
    foreach (XmlElementAttribute att in atts)
    {
     printBuilder.Append(att.DataType);
     printBuilder.Append("/");
     printBuilder.Append(att.ElementName);
     printBuilder.Append("/");
     printBuilder.Append(att.Form);
     printBuilder.Append("/");
     printBuilder.Append(att.IsNullable);
     printBuilder.Append("/");
     printBuilder.Append(att.Namespace);
     printBuilder.Append("/");
     if (null != att.Type)
     {
      printBuilder.Append(att.Type.AssemblyQualifiedName);
     }
     printBuilder.Append("::");
    }
   }
   printBuilder.Append("%%");
  }
  private static void AddXmlEnumPrint(XmlEnumAttribute att, StringBuilder printBuilder)
  {
   if (null != att)
   {
    printBuilder.Append(att.Name);
   }
   printBuilder.Append("%%");
  }
  private static void AddXmlIgnorePrint(bool ignore, StringBuilder printBuilder)
  {
   printBuilder.Append(ignore);
   printBuilder.Append("%%");
  }
  private static void AddXmlNamespacePrint(bool Xmlns, StringBuilder printBuilder)
  {
   printBuilder.Append(Xmlns);
   printBuilder.Append("%%");
  }
  internal static void AddXmlRootPrint(XmlRootAttribute root, StringBuilder printBuilder)
  {
   if (null != root)
   {
    printBuilder.Append(root.DataType);
    printBuilder.Append("/");
    printBuilder.Append(root.ElementName);
    printBuilder.Append("/");
    printBuilder.Append(root.IsNullable);
    printBuilder.Append("/");
    printBuilder.Append(root.Namespace);
   }
   printBuilder.Append("%%");
  }
  private static void AddXmlTextPrint(XmlTextAttribute att, StringBuilder printBuilder)
  {
   if (null != att)
   {
    printBuilder.Append(att.DataType);
    printBuilder.Append("/");
    if (null != att.Type)
    {
     printBuilder.Append(att.Type.AssemblyQualifiedName);
    }
   }
   printBuilder.Append("%%");
  }
  private static void AddXmlTypePrint(XmlTypeAttribute att, StringBuilder printBuilder)
  {
   if (null != att)
   {
    printBuilder.Append(att.IncludeInSchema);
    printBuilder.Append("/");
    printBuilder.Append(att.Namespace);
    printBuilder.Append("/");
    printBuilder.Append(att.TypeName);
   }
   printBuilder.Append("%%");
  }
  private static Hashtable GetTypesHashtable(XmlAttributeOverrides overrides)
  {
   return GetHashtable(overrides, "types");
  }
  private static Hashtable GetHashtable(XmlAttributeOverrides overrides, string fieldName)
  {
   Hashtable types;
   FieldInfo typesInfo = typeof(XmlAttributeOverrides).GetField(fieldName, BindingFlags.NonPublic | BindingFlags.Instance | BindingFlags.GetField) as FieldInfo;
   if (null == typesInfo)
   {
    throw new System.ArgumentException("XmlAttributeOverrides does conform to expected structure");
   }
   types = typesInfo.GetValue(overrides) as Hashtable;
   System.Diagnostics.Debug.Assert(null != types);
   return types;
  }
 }
}
