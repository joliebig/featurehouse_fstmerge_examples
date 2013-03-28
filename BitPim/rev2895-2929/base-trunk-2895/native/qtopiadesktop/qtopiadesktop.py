"""Be at one with Qtopia desktop (eg as used by the Zaurus)"""
import xml.parsers.expat
import os
import encodings.ascii
class XMLParser:
    def __init__(self, filename):
        parser=xml.parsers.expat.ParserCreate()
        parser.CharacterDataHandler=self._handleCharData
        parser.StartElementHandler=self._handleStartElement
        parser.EndElementHandler=self._handleEndElement
        parser.ParseFile(open(filename, "rt"))
    def _handleCharData(self, data): pass
    def _handleEndElement(self, name): pass
    def _handleStartElement(self, name, attrs):
        raise NotImplementedException()
class CategoriesParser(XMLParser):
    def __init__(self, filename="Categories.xml"):
        filename=getqdpath(filename)
        self._data={}
        XMLParser.__init__(self, filename)
    def _handleStartElement(self, name, attrs):
        if name!="Category":
            return
        self._data[int(ununicodify(attrs["id"]))]=ununicodify(attrs["name"])
    def categories(self):
        return self._data
class ABParser(XMLParser):
    def __init__(self, filename="addressbook/addressbook.xml", categories={}):
        filename=getqdpath(filename)
        self.categories=categories
        self._data=[]
        XMLParser.__init__(self, filename)
    def _handleStartElement(self, name, attrs):
        if name!="Contact":
            return
        d=cleandict(attrs)
        c=d.get("Categories", "")
        cats=[]
        if len(c):
            c=[int(x) for x in c.split(";")]
            for cat in c:
                if self.categories.has_key(cat):
                    cats.append(self.categories[cat])
                else:
                    if __debug__:
                        print "couldn't find category",cat,"in",`self.categories`
        if len(cats):
            d["Categories"]=cats
        else:
            if d.has_key("Categories"):
                del d["Categories"]
        if d.has_key("Emails"):
            d["Emails"]=d["Emails"].split()
        self._data.append(d)
    def contacts(self):
        return self._data
def getqdpath(filename):
    filename=os.path.expanduser(os.path.join("~/.palmtopcenter", filename))
    if filename.startswith("~"):
        filename="c:\\"+filename[1:]
    return os.path.abspath(filename)
def getfilename():
    "Returns the filename we need"
    return getqdpath("addressbook/addressbook.xml")
def cleandict(d):
    newd={}
    for k,v in d.items():
        newd[ununicodify(k)]=ununicodify(v)
    return newd
def ununicodify(value):
    try:
        return str(value)
    except UnicodeEncodeError:
        return value.encode("ascii", 'xmlcharrefreplace')
def getcontacts():
    cats=CategoriesParser()
    ab=ABParser(categories=cats.categories())
    return ab.contacts()
if __name__=="__main__":
    print getcontacts()
