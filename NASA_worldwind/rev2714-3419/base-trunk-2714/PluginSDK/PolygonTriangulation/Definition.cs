using System;
namespace GeometryUtility
{
 public struct ConstantValue
 {
  internal const double SmallValue=double.Epsilon;
  internal const double BigValue=double.MaxValue;
 }
 public enum VertexType
 {
  ErrorPoint,
  ConvexPoint,
  ConcavePoint
 }
 public enum PolygonType
 {
  Unknown,
  Convex,
  Concave
 }
 public enum PolygonDirection
 {
  Unknown,
  Clockwise,
  Count_Clockwise
 }
}
