using System.Xml.Serialization;
[System.Xml.Serialization.XmlTypeAttribute(Namespace="http://www.topografix.com/GPX/1/1")]
[System.Xml.Serialization.XmlRootAttribute("gpx", Namespace="http://www.topografix.com/GPX/1/1", IsNullable=false)]
public class gpxType {
    public metadataType metadata;
    [System.Xml.Serialization.XmlElementAttribute("wpt")]
    public wptType[] wpt;
    [System.Xml.Serialization.XmlElementAttribute("rte")]
    public rteType[] rte;
    [System.Xml.Serialization.XmlElementAttribute("trk")]
    public trkType[] trk;
    public extensionsType extensions;
    [System.Xml.Serialization.XmlAttributeAttribute()]
    public string version = "1.1";
    [System.Xml.Serialization.XmlAttributeAttribute()]
    public string creator;
}
[System.Xml.Serialization.XmlTypeAttribute(Namespace="http://www.topografix.com/GPX/1/1")]
public class metadataType {
    public string name;
    public string desc;
    public personType author;
    public copyrightType copyright;
    [System.Xml.Serialization.XmlElementAttribute("link")]
    public linkType[] link;
    public System.DateTime time;
    [System.Xml.Serialization.XmlIgnoreAttribute()]
    public bool timeSpecified;
    public string keywords;
    public boundsType bounds;
    public extensionsType extensions;
}
[System.Xml.Serialization.XmlTypeAttribute(Namespace="http://www.topografix.com/GPX/1/1")]
public class personType {
    public string name;
    public emailType email;
    public linkType link;
}
[System.Xml.Serialization.XmlTypeAttribute(Namespace="http://www.topografix.com/GPX/1/1")]
public class emailType {
    [System.Xml.Serialization.XmlAttributeAttribute()]
    public string id;
    [System.Xml.Serialization.XmlAttributeAttribute()]
    public string domain;
}
[System.Xml.Serialization.XmlTypeAttribute(Namespace="http://www.topografix.com/GPX/1/1")]
public class trksegType {
    [System.Xml.Serialization.XmlElementAttribute("trkpt")]
    public wptType[] trkpt;
    public extensionsType extensions;
}
[System.Xml.Serialization.XmlTypeAttribute(Namespace="http://www.topografix.com/GPX/1/1")]
public class wptType {
    public System.Decimal ele;
    [System.Xml.Serialization.XmlIgnoreAttribute()]
    public bool eleSpecified;
    public System.DateTime time;
    [System.Xml.Serialization.XmlIgnoreAttribute()]
    public bool timeSpecified;
    public System.Decimal magvar;
    [System.Xml.Serialization.XmlIgnoreAttribute()]
    public bool magvarSpecified;
    public System.Decimal geoidheight;
    [System.Xml.Serialization.XmlIgnoreAttribute()]
    public bool geoidheightSpecified;
    public string name;
    public string cmt;
    public string desc;
    public string src;
    [System.Xml.Serialization.XmlElementAttribute("link")]
    public linkType[] link;
    public string sym;
    public string type;
    public fixType fix;
    [System.Xml.Serialization.XmlIgnoreAttribute()]
    public bool fixSpecified;
    [System.Xml.Serialization.XmlElementAttribute(DataType="nonNegativeInteger")]
    public string sat;
    public System.Decimal hdop;
    [System.Xml.Serialization.XmlIgnoreAttribute()]
    public bool hdopSpecified;
    public System.Decimal vdop;
    [System.Xml.Serialization.XmlIgnoreAttribute()]
    public bool vdopSpecified;
    public System.Decimal pdop;
    [System.Xml.Serialization.XmlIgnoreAttribute()]
    public bool pdopSpecified;
    public System.Decimal ageofdgpsdata;
    [System.Xml.Serialization.XmlIgnoreAttribute()]
    public bool ageofdgpsdataSpecified;
    [System.Xml.Serialization.XmlElementAttribute(DataType="integer")]
    public string dgpsid;
    public extensionsType extensions;
    [System.Xml.Serialization.XmlAttributeAttribute()]
    public System.Decimal lat;
    [System.Xml.Serialization.XmlAttributeAttribute()]
    public System.Decimal lon;
}
[System.Xml.Serialization.XmlTypeAttribute(Namespace="http://www.topografix.com/GPX/1/1")]
public class linkType {
    public string text;
    public string type;
    [System.Xml.Serialization.XmlAttributeAttribute(DataType="anyURI")]
    public string href;
}
[System.Xml.Serialization.XmlTypeAttribute(Namespace="http://www.topografix.com/GPX/1/1")]
public enum fixType {
    none,
    [System.Xml.Serialization.XmlEnumAttribute("2d")]
    Item2d,
    [System.Xml.Serialization.XmlEnumAttribute("3d")]
    Item3d,
    dgps,
    pps,
}
[System.Xml.Serialization.XmlTypeAttribute(Namespace="http://www.topografix.com/GPX/1/1")]
public class extensionsType {
    [System.Xml.Serialization.XmlAnyElementAttribute()]
    public System.Xml.XmlElement[] Any;
}
[System.Xml.Serialization.XmlTypeAttribute(Namespace="http://www.topografix.com/GPX/1/1")]
public class trkType {
    public string name;
    public string cmt;
    public string desc;
    public string src;
    [System.Xml.Serialization.XmlElementAttribute("link")]
    public linkType[] link;
    [System.Xml.Serialization.XmlElementAttribute(DataType="nonNegativeInteger")]
    public string number;
    public string type;
    public extensionsType extensions;
    [System.Xml.Serialization.XmlElementAttribute("trkseg")]
    public trksegType[] trkseg;
}
[System.Xml.Serialization.XmlTypeAttribute(Namespace="http://www.topografix.com/GPX/1/1")]
public class rteType {
    public string name;
    public string cmt;
    public string desc;
    public string src;
    [System.Xml.Serialization.XmlElementAttribute("link")]
    public linkType[] link;
    [System.Xml.Serialization.XmlElementAttribute(DataType="nonNegativeInteger")]
    public string number;
    public string type;
    public extensionsType extensions;
    [System.Xml.Serialization.XmlElementAttribute("rtept")]
    public wptType[] rtept;
}
[System.Xml.Serialization.XmlTypeAttribute(Namespace="http://www.topografix.com/GPX/1/1")]
public class boundsType {
    [System.Xml.Serialization.XmlAttributeAttribute()]
    public System.Decimal minlat;
    [System.Xml.Serialization.XmlAttributeAttribute()]
    public System.Decimal minlon;
    [System.Xml.Serialization.XmlAttributeAttribute()]
    public System.Decimal maxlat;
    [System.Xml.Serialization.XmlAttributeAttribute()]
    public System.Decimal maxlon;
}
[System.Xml.Serialization.XmlTypeAttribute(Namespace="http://www.topografix.com/GPX/1/1")]
public class copyrightType {
    [System.Xml.Serialization.XmlElementAttribute(DataType="gYear")]
    public string year;
    [System.Xml.Serialization.XmlElementAttribute(DataType="anyURI")]
    public string license;
    [System.Xml.Serialization.XmlAttributeAttribute()]
    public string author;
}
