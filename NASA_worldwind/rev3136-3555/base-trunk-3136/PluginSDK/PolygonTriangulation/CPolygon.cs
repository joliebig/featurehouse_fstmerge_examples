using System;
namespace GeometryUtility
{
 public class CPolygon
 {
  private CPoint2D[] m_aVertices;
  public CPoint2D this[int index]
  {
   set
   {
    m_aVertices[index]=value;
   }
   get
   {
    return m_aVertices[index];
   }
  }
  public CPolygon()
  {
  }
  public CPolygon(CPoint2D[] points)
  {
   int nNumOfPoitns=points.Length;
   try
   {
    if (nNumOfPoitns<3 )
    {
     InvalidInputGeometryDataException ex=
      new InvalidInputGeometryDataException();
     throw ex;
    }
    else
    {
     m_aVertices=new CPoint2D[nNumOfPoitns];
     for (int i=0; i<nNumOfPoitns; i++)
     {
      m_aVertices[i]=points[i];
     }
    }
   }
   catch (Exception e)
   {
    System.Diagnostics.Trace.WriteLine(
     e.Message+e.StackTrace);
   }
  }
  public int VertexIndex(CPoint2D vertex)
  {
   int nIndex=-1;
   int nNumPts=m_aVertices.Length;
   for (int i=0; i<nNumPts; i++)
   {
    if (CPoint2D.SamePoints(m_aVertices[i], vertex))
     nIndex=i;
   }
   return nIndex;
  }
  public CPoint2D PreviousPoint(CPoint2D vertex)
  {
   int nIndex;
   nIndex=VertexIndex(vertex);
   if (nIndex==-1)
    return null;
   else
   {
    if (nIndex==0)
    {
     int nPoints=m_aVertices.Length;
     return m_aVertices[nPoints-1];
    }
    else
     return m_aVertices[nIndex-1];
   }
  }
  public CPoint2D NextPoint(CPoint2D vertex)
  {
   CPoint2D nextPt=new CPoint2D();
   int nIndex;
   nIndex=VertexIndex(vertex);
   if (nIndex==-1)
    return null;
   else
   {
    int nNumOfPt=m_aVertices.Length;
    if (nIndex==nNumOfPt-1)
    {
     return m_aVertices[0];
    }
    else
     return m_aVertices[nIndex+1];
   }
  }
  public double PolygonArea()
  {
   double dblArea=0;
   int nNumOfVertices=m_aVertices.Length;
   int j;
   for (int i=0; i<nNumOfVertices; i++)
   {
    j=(i+1) % nNumOfVertices;
    dblArea += m_aVertices[i].X*m_aVertices[j].Y;
    dblArea -= (m_aVertices[i].Y*m_aVertices[j].X);
   }
   dblArea=dblArea/2;
   return Math.Abs(dblArea);
  }
  public static double PolygonArea(CPoint2D[] points)
  {
   double dblArea=0;
   int nNumOfPts=points.Length;
   int j;
   for (int i=0; i<nNumOfPts; i++)
   {
    j=(i+1) % nNumOfPts;
    dblArea += points[i].X*points[j].Y;
    dblArea -= (points[i].Y*points[j].X);
   }
   dblArea=dblArea/2;
   return dblArea;
  }
  public VertexType PolygonVertexType(CPoint2D vertex)
  {
   VertexType vertexType=VertexType.ErrorPoint;
   if (PolygonVertex(vertex))
   {
    CPoint2D pti=vertex;
    CPoint2D ptj=PreviousPoint(vertex);
    CPoint2D ptk=NextPoint(vertex);
    double dArea=PolygonArea(new CPoint2D[] {ptj,pti, ptk});
    if (dArea<0)
     vertexType= VertexType.ConvexPoint;
    else if (dArea> 0)
     vertexType= VertexType.ConcavePoint;
   }
   return vertexType;
  }
  public bool Diagonal(CPoint2D vertex1, CPoint2D vertex2)
  {
   bool bDiagonal=false;
   int nNumOfVertices=m_aVertices.Length;
   int j=0;
   for (int i= 0; i<nNumOfVertices; i++)
   {
    bDiagonal=true;
    j= (i+1) % nNumOfVertices;
    double x1=vertex1.X;
    double y1=vertex1.Y;
    double x2=vertex1.X;
    double y2=vertex1.Y;
    double x3=m_aVertices[i].X;
    double y3=m_aVertices[i].Y;
    double x4=m_aVertices[j].X;
    double y4=m_aVertices[j].Y;
    double de=(y4-y3)*(x2-x1)-(x4-x3)*(y2-y1);
    double ub=-1;
    if (Math.Abs(de-0)>ConstantValue.SmallValue)
     ub=((x2-x1)*(y1-y3)-(y2-y1)*(x1-x3))/de;
    if ((ub> 0) && (ub<1))
    {
     bDiagonal=false;
    }
   }
   return bDiagonal;
  }
  public PolygonType GetPolygonType()
  {
   int nNumOfVertices=m_aVertices.Length;
   bool bSignChanged=false;
   int nCount=0;
   int j=0, k=0;
   for (int i=0; i<nNumOfVertices; i++)
   {
    j=(i+1) % nNumOfVertices;
    k=(i+2) % nNumOfVertices;
    double crossProduct=(m_aVertices[j].X- m_aVertices[i].X)
     *(m_aVertices[k].Y- m_aVertices[j].Y);
    crossProduct=crossProduct-(
     (m_aVertices[j].Y- m_aVertices[i].Y)
     *(m_aVertices[k].X- m_aVertices[j].X)
     );
    if ((crossProduct>0) && (nCount==0) )
     nCount=1;
    else if ((crossProduct<0) && (nCount==0))
     nCount=-1;
    if (((nCount==1) && (crossProduct<0))
     ||( (nCount==-1) && (crossProduct>0)) )
     bSignChanged=true;
   }
   if (bSignChanged)
    return PolygonType.Concave;
   else
    return PolygonType.Convex;
  }
  public bool PrincipalVertex(CPoint2D vertex)
  {
   bool bPrincipal=false;
   if (PolygonVertex(vertex))
   {
    CPoint2D pt1=PreviousPoint(vertex);
    CPoint2D pt2=NextPoint(vertex);
    if (Diagonal(pt1, pt2))
     bPrincipal=true;
   }
   return bPrincipal;
  }
  public bool PolygonVertex(CPoint2D point)
  {
   bool bVertex=false;
   int nIndex=VertexIndex(point);
   if ((nIndex>=0) && (nIndex<=m_aVertices.Length-1))
          bVertex=true;
   return bVertex;
  }
  public void ReverseVerticesDirection()
  {
   int nVertices=m_aVertices.Length;
   CPoint2D[] aTempPts=new CPoint2D[nVertices];
   for (int i=0; i<nVertices; i++)
    aTempPts[i]=m_aVertices[i];
   for (int i=0; i<nVertices; i++)
   m_aVertices[i]=aTempPts[nVertices-1-i];
  }
  public PolygonDirection VerticesDirection()
  {
   int nCount=0, j=0, k=0;
   int nVertices=m_aVertices.Length;
   for (int i=0; i<nVertices; i++)
   {
    j=(i+1) % nVertices;
    k=(i+2) % nVertices;
    double crossProduct=(m_aVertices[j].X - m_aVertices[i].X)
     *(m_aVertices[k].Y- m_aVertices[j].Y);
    crossProduct=crossProduct-(
     (m_aVertices[j].Y- m_aVertices[i].Y)
     *(m_aVertices[k].X- m_aVertices[j].X)
     );
    if (crossProduct>0)
     nCount++;
    else
     nCount--;
   }
   if( nCount<0)
    return PolygonDirection.Count_Clockwise;
   else if (nCount> 0)
    return PolygonDirection.Clockwise;
   else
    return PolygonDirection.Unknown;
    }
  public static PolygonDirection PointsDirection(
   CPoint2D[] points)
  {
   int nCount=0, j=0, k=0;
   int nPoints=points.Length;
   if (nPoints<3)
    return PolygonDirection.Unknown;
   for (int i=0; i<nPoints; i++)
   {
    j=(i+1) % nPoints;
    k=(i+2) % nPoints;
    double crossProduct=(points[j].X - points[i].X)
     *(points[k].Y- points[j].Y);
    crossProduct=crossProduct-(
     (points[j].Y- points[i].Y)
     *(points[k].X- points[j].X)
     );
    if (crossProduct>0)
     nCount++;
    else
     nCount--;
   }
   if( nCount<0)
    return PolygonDirection.Count_Clockwise;
   else if (nCount> 0)
    return PolygonDirection.Clockwise;
   else
    return PolygonDirection.Unknown;
  }
  public static void ReversePointsDirection(
   CPoint2D[] points)
  {
   int nVertices=points.Length;
   CPoint2D[] aTempPts=new CPoint2D[nVertices];
   for (int i=0; i<nVertices; i++)
    aTempPts[i]=points[i];
   for (int i=0; i<nVertices; i++)
    points[i]=aTempPts[nVertices-1-i];
  }
 }
}
