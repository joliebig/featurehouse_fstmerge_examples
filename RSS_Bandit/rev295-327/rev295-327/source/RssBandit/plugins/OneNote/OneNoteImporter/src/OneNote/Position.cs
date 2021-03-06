using System; 
using System.Xml; namespace  Microsoft.Office.OneNote {
	
 [Serializable] 
 public class  Position  : ImportNode {
		
  public  Position() : this(LEFT_MARGIN, TOP_MARGIN)
  {
  }
 
  public  Position(double xInPoints, double yInPoints)
  {
   this.x = xInPoints;
   this.y = yInPoints;
  }
 
  public override  object Clone()
  {
   return MemberwiseClone();
  }
 
  public  void FromInches(double xInInches, double yInInches)
  {
   this.x = UnitConversions.InchesToPoints(xInInches);
   this.y = UnitConversions.InchesToPoints(yInInches);
  }
 
  protected internal override  void SerializeToXml(XmlNode parentNode)
  {
   XmlDocument xmlDocument = parentNode.OwnerDocument;
   XmlElement positionElement = xmlDocument.CreateElement("Position");
   positionElement.SetAttribute("x", X.ToString());
   positionElement.SetAttribute("y", Y.ToString());
   parentNode.AppendChild(positionElement);
  }
 
  public override  bool Equals(object obj)
  {
   Position other = obj as Position;
   if (other == null)
    return false;
   return other.X.Equals(X) && other.Y.Equals(Y);
  }
 
  public override  int GetHashCode()
  {
   return X.GetHashCode() ^ Y.GetHashCode();
  }
 
  public  double X
  {
   get
   {
    return x;
   }
   set
   {
    x = value;
   }
  }
 
  public  double Y
  {
   get
   {
    return y;
   }
   set
   {
    y = value;
   }
  }
 
  private  double x;
 
  private  double y;
 
  private  const int LEFT_MARGIN = 36; 
  private  const int TOP_MARGIN = 36;
	}

}
