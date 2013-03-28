using System;
using System.Collections;
using System.Data;
using System.IO;
using System.Reflection;
using System.Text;
using System.Xml;
using System.Xml.Schema;
namespace Microsoft.Office.OneNote
{
 [Serializable]
 public class Page : ImportNode, IEnumerable
 {
  public Page(string sectionPath)
  {
   this.sectionPath = sectionPath;
   this.id = new ObjectId();
  }
  public Page(string sectionPath, string title) : this(sectionPath)
  {
   this.title = title;
  }
  public Page(string sectionPath, string title, string html) : this(sectionPath, title)
  {
   OutlineObject outline = new OutlineObject();
   AddObject(outline);
   outline.AddContent(new HtmlContent(html));
  }
  public override object Clone()
  {
   Page clone = new Page(this.sectionPath);
   foreach (PageObject pageObject in this)
   {
    clone.AddObject(pageObject);
   }
   return clone;
  }
  public void AddObject(PageObject pageObject)
  {
   if (pageObject.Parent != this)
   {
    AddChild(pageObject);
   }
   pageObject.DeletePending = false;
  }
  public void DeleteObject(PageObject pageObject)
  {
   if (pageObject.Parent != this)
   {
    throw new ArgumentException("Page does not contain object: " + pageObject);
   }
   pageObject.DeletePending = true;
  }
  public IEnumerator GetEnumerator()
  {
   return new PageEnumerator(this);
  }
  public void Commit()
  {
   if (!CommitPending)
    return;
   SimpleImporter importer = new SimpleImporter();
   importer.Import(this.ToString());
   foreach (PageObject pageObject in this)
   {
    if (pageObject.DeletePending)
    {
     RemoveChild(pageObject);
     pageObject.DeletePending = false;
    }
   }
   committed = true;
   CommitPending = false;
  }
  public void NavigateTo()
  {
   SimpleImporter importer = new SimpleImporter();
   importer.NavigateToPage(sectionPath, id.ToString());
  }
  public override string ToString()
  {
   XmlDocument xmlDocument = new XmlDocument();
   SerializeToXml(xmlDocument);
   MemoryStream xmlStream = new MemoryStream();
   XmlWriter xmlWriter = new XmlTextWriter(xmlStream, Encoding.Unicode);
   xmlDocument.Save(xmlWriter);
   xmlWriter.Flush();
   ValidateXml(xmlStream);
   xmlStream.Seek(0, SeekOrigin.Begin);
   return new StreamReader(xmlStream).ReadToEnd();
  }
  protected internal override void SerializeToXml(XmlNode parentNode)
  {
   XmlDocument xmlDocument = parentNode as XmlDocument;
   if (xmlDocument == null)
    xmlDocument = parentNode.OwnerDocument;
   XmlElement import = xmlDocument.CreateElement("Import");
   parentNode.AppendChild(import);
   XmlElement ensurePageVerb = xmlDocument.CreateElement("EnsurePage");
   ensurePageVerb.SetAttribute("path", sectionPath);
   ensurePageVerb.SetAttribute("guid", id.ToString());
   ensurePageVerb.SetAttribute("date", XmlConvert.ToString(date));
   if (title != null)
    ensurePageVerb.SetAttribute("title", title);
   if (rtl)
    ensurePageVerb.SetAttribute("rtl", "true");
   if (previousPage != null)
    ensurePageVerb.SetAttribute("insertAfter", previousPage.Id.ToString());
   import.AppendChild(ensurePageVerb);
   XmlElement placeObjectsVerb = xmlDocument.CreateElement("PlaceObjects");
   placeObjectsVerb.SetAttribute("pagePath", sectionPath);
   placeObjectsVerb.SetAttribute("pageGuid", id.ToString());
   foreach (PageObject pageObject in this)
   {
    if (pageObject.CommitPending)
     pageObject.SerializeToXml(placeObjectsVerb);
   }
   if (placeObjectsVerb.ChildNodes.Count > 0)
    import.AppendChild(placeObjectsVerb);
   string xmlns = XmlNamespace;
   import.SetAttribute("xmlns", xmlns);
  }
  protected internal void ValidateXml(Stream xmlStream)
  {
   Assembly assembly = typeof (Page).Assembly;
   string schemaName = typeof (Page).Namespace + ".SimpleImport.xsd";
   Stream xsdStream = assembly.GetManifestResourceStream(schemaName);
   XmlSchema schema = XmlSchema.Read(xsdStream, null);
   xmlStream.Seek(0, SeekOrigin.Begin);
   XmlValidatingReader validator = new XmlValidatingReader(xmlStream, XmlNodeType.Document, null);
   validator.Schemas.Add(schema);
   try
   {
    while (validator.Read())
    {
     continue;
    }
   }
   catch (XmlException ex)
   {
    throw ex;
   }
   catch (XmlSchemaException ex)
   {
    throw ex;
   }
  }
  public override bool Equals(object obj)
  {
   Page page = obj as Page;
   if (page == null)
    return false;
   return page.Id.Equals(Id);
  }
  public override int GetHashCode()
  {
   return id.GetHashCode();
  }
  protected internal ObjectId Id
  {
   get
   {
    return id;
   }
   set
   {
    id = value;
   }
  }
  public String SectionPath
  {
   get
   {
    return sectionPath;
   }
   set
   {
    sectionPath = value;
    CommitPending = true;
    committed = false;
   }
  }
  public String Title
  {
   get
   {
    return title;
   }
   set
   {
    if (committed)
     throw new ReadOnlyException("Page.Title is read-only.");
    if (title == value || (title != null && title.Equals(value)))
     return;
    title = value;
    CommitPending = true;
   }
  }
  public DateTime Date
  {
   get
   {
    return date;
   }
   set
   {
    if (committed)
     throw new ReadOnlyException("Page.Date is read-only.");
    if (date == value || date.Equals(value))
     return;
    date = value;
    CommitPending = true;
   }
  }
  public bool RTL
  {
   get
   {
    return rtl;
   }
   set
   {
    if (committed)
     throw new ReadOnlyException("Page.RTL is read-only.");
    if (rtl == value)
     return;
    rtl = value;
    CommitPending = true;
   }
  }
  public Page PreviousPage
  {
   get
   {
    return previousPage;
   }
   set
   {
    if (committed)
     throw new ReadOnlyException("Page.PreviousPage is read-only.");
    if (previousPage == value || (previousPage != null && previousPage.Equals(value)))
     return;
    previousPage = value;
    CommitPending = true;
   }
  }
  private ObjectId id;
  private String sectionPath;
  private String title;
  private DateTime date = DateTime.Now;
  private bool rtl;
  [NonSerialized]
  private Page previousPage;
  private bool committed;
  private const string XmlNamespace = "http://schemas.microsoft.com/office/onenote/2004/import";
  class PageEnumerator : IEnumerator
  {
   protected internal PageEnumerator(Page page)
   {
    this.page = page;
    Reset();
   }
   public void Reset()
   {
    index = -1;
   }
   public object Current
   {
    get
    {
     if (index < page.GetChildCount())
     {
      return page.GetChild(index);
     }
     return null;
    }
   }
   public bool MoveNext()
   {
    while (++index < page.GetChildCount() &&
     !(page.GetChild(index) is PageObject))
    {
     continue;
    }
    return (index < page.GetChildCount());
   }
   private Page page;
   private int index;
  }
 }
}
