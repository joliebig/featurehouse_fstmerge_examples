using System;
using System.Text;
using System.Runtime.InteropServices;
namespace MapTools
{
 public class ShapeLib
 {
  public enum ShapeType
  {
   NullShape = 0,
   Point = 1,
   PolyLine = 3,
   Polygon = 5,
   MultiPoint = 8,
   PointZ = 11,
   PolyLineZ = 13,
   PolygonZ = 15,
   MultiPointZ = 18,
   PointM = 21,
   PolyLineM = 23,
   PolygonM = 25,
   MultiPointM = 28,
   MultiPatch = 31
  }
  public enum PartType
  {
   TriangleStrip = 0,
   TriangleFan = 1,
   OuterRing = 2,
   InnerRing = 3,
   FirstRing = 4,
   Ring = 5
  }
  [StructLayout(LayoutKind.Sequential)]
  public class SHPObject
  {
   public ShapeType shpType;
   public int nShapeId;
   public int nParts;
   public IntPtr paPartStart;
   public IntPtr paPartType;
   public int nVertices;
   public IntPtr padfX;
   public IntPtr padfY;
   public IntPtr padfZ;
   public IntPtr padfM;
   public double dfXMin;
   public double dfYMin;
   public double dfZMin;
   public double dfMMin;
   public double dfXMax;
   public double dfYMax;
   public double dfZMax;
   public double dfMMax;
  }
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern IntPtr SHPOpen(string szShapeFile, string szAccess);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern IntPtr SHPCreate(string szShapeFile, ShapeType shpType);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern void SHPGetInfo(IntPtr hSHP, ref int pnEntities,
   ref ShapeType pshpType, double[] adfMinBound, double[] adfMaxBound);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern SHPObject SHPReadObject(IntPtr hSHP, int iShape);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern int SHPWriteObject(IntPtr hSHP, int iShape, SHPObject psObject);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern void SHPDestroyObject(SHPObject psObject);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern void SHPComputeExtents(SHPObject psObject);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern SHPObject SHPCreateObject(ShapeType shpType, int nShapeId,
   int nParts, int[] panPartStart, PartType[] paPartType,
   int nVertices, double[] adfX, double[] adfY,
   double[] adfZ, double[] adfM );
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern SHPObject SHPCreateSimpleObject(ShapeType shpType, int nVertices,
   double[] adfX, double[] adfY, double[] adfZ);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern void SHPClose(IntPtr hSHP);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern string SHPTypeName(ShapeType shpType);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern string SHPPartTypeName (PartType partType);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern IntPtr SHPCreateTree(IntPtr hSHP, int nDimension, int nMaxDepth,
   double[] adfBoundsMin, double[] adfBoundsMax);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern void SHPDestroyTree(IntPtr hTree);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern int SHPTreeAddShapeId(IntPtr hTree, SHPObject psObject);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern void SHPTreeTrimExtraNodes(IntPtr hTree);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern IntPtr SHPTreeFindLikelyShapes(IntPtr hTree,
   double[] adfBoundsMin, double[] adfBoundsMax, ref int pnShapeCount);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern int SHPCheckBoundsOverlap(double[] adfBox1Min, double[] adfBox1Max,
   double[] adfBox2Min, double[] adfBox2Max, int nDimension);
  public enum DBFFieldType
  {
   FTString,
   FTInteger,
   FTDouble,
   FTLogical,
   FTInvalid
  };
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern IntPtr DBFOpen (string szDBFFile, string szAccess);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern IntPtr DBFCreate (string szDBFFile);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern int DBFGetFieldCount (IntPtr hDBF);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern int DBFGetRecordCount (IntPtr hDBF);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern int DBFAddField (IntPtr hDBF, string szFieldName,
   DBFFieldType eType, int nWidth, int nDecimals);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern DBFFieldType DBFGetFieldInfo (IntPtr hDBF, int iField,
   StringBuilder szFieldName, ref int pnWidth, ref int pnDecimals);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern int DBFGetFieldIndex (IntPtr hDBF, string szFieldName);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern int DBFReadIntegerAttribute (IntPtr hDBF, int iShape, int iField);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern double DBFReadDoubleAttribute (IntPtr hDBF, int iShape, int iField);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern string DBFReadStringAttribute (IntPtr hDBF, int iShape, int iField);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi, EntryPoint="DBFReadLogicalAttribute")]
  private static extern string _DBFReadLogicalAttribute (IntPtr hDBF, int iShape, int iField);
  public static bool DBFReadLogicalAttribute (IntPtr hDBF, int iShape, int iField)
  {
   return (_DBFReadLogicalAttribute(hDBF, iShape, iField)=="T");
  }
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern int DBFIsAttributeNULL (IntPtr hDBF, int iShape, int iField);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern int DBFWriteIntegerAttribute (IntPtr hDBF, int iShape,
   int iField, int nFieldValue);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern int DBFWriteDoubleAttribute (IntPtr hDBF, int iShape,
   int iField, double dFieldValue);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern int DBFWriteStringAttribute (IntPtr hDBF, int iShape,
   int iField, string szFieldValue);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern int DBFWriteNULLAttribute (IntPtr hDBF, int iShape, int iField);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi, EntryPoint="DBFWriteLogicalAttribute")]
  private static extern int _DBFWriteLogicalAttribute (IntPtr hDBF, int iShape,
   int iField, char lFieldValue);
  public static int DBFWriteLogicalAttribute (IntPtr hDBF, int iShape, int iField, bool bFieldValue)
  {
   if (bFieldValue)
    return _DBFWriteLogicalAttribute(hDBF, iShape, iField, 'T');
   else
    return _DBFWriteLogicalAttribute(hDBF, iShape, iField, 'F');
  }
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern IntPtr DBFReadTuple (IntPtr hDBF, int hEntity);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern int DBFWriteTuple (IntPtr hDBF, int hEntity, IntPtr pRawTuple);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern IntPtr DBFCloneEmpty (IntPtr hDBF, string szFilename);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern void DBFClose (IntPtr hDBF);
  [DllImport("shapelib.dll", CharSet=CharSet.Ansi)]
  public static extern sbyte DBFGetNativeFieldType (IntPtr hDBF, int iField);
  private ShapeLib(){}
 }
}
