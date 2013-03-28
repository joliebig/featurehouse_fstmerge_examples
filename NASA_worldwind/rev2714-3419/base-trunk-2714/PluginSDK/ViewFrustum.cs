using System;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
namespace WorldWind
{
 public class Frustum
 {
  public Plane[] planes = new Plane[6];
  public void Update(Matrix m)
  {
   this.planes[0] = new Plane(
    m.M14 + m.M12,
    m.M24 + m.M22,
    m.M34 + m.M32,
    m.M44 + m.M42
    );
   this.planes[1] = new Plane(
    m.M14 - m.M13,
    m.M24 - m.M23,
    m.M34 - m.M33,
    m.M44 - m.M43
    );
   this.planes[2] = new Plane(
    m.M14 - m.M11,
    m.M24 - m.M21,
    m.M34 - m.M31,
    m.M44 - m.M41
    );
   this.planes[3] = new Plane(
    m.M14 + m.M11,
    m.M24 + m.M21,
    m.M34 + m.M31,
    m.M44 + m.M41
    );
   this.planes[4] = new Plane(
    m.M13,
    m.M23,
    m.M33,
    m.M43);
   this.planes[5] = new Plane(
    m.M14 - m.M12,
    m.M24 - m.M22,
    m.M34 - m.M32,
    m.M44 - m.M42
    );
   foreach(Plane p in this.planes)
    p.Normalize();
  }
  public bool Intersects(BoundingSphere c)
  {
   foreach(Plane p in this.planes)
   {
    float distancePlaneToPoint = p.A * c.Center.X + p.B * c.Center.Y + p.C * c.Center.Z + p.D;
    if(distancePlaneToPoint < -c.Radius)
     return false;
   }
   return true;
  }
  public bool ContainsPoint(Vector3 v)
  {
   foreach(Plane p in this.planes)
    if(Vector3.Dot(new Vector3(p.A, p.B, p.C), v) + p.D < 0)
     return false;
   return true;
  }
  public bool Contains(BoundingBox bb)
  {
   int iTotalIn = 0;
   foreach(Plane p in this.planes)
   {
    int iInCount = 8;
    int iPtIn = 1;
    for(int i = 0; i < 8; i++)
    {
     if(Vector3.Dot(new Vector3(p.A,p.B,p.C), bb.corners[i]) + p.D < 0)
     {
      iPtIn = 0;
      --iInCount;
     }
    }
    if(iInCount == 0)
     return false;
    iTotalIn += iPtIn;
   }
   if(iTotalIn == 6)
    return true;
   return false;
  }
  public bool Intersects(BoundingBox bb)
  {
   foreach(Plane p in this.planes)
   {
    Vector3 v = new Vector3(p.A,p.B,p.C);
    bool isInside = false;
    for(int i = 0; i < 8; i++)
    {
     if(Vector3.Dot(v, bb.corners[i]) + p.D >= 0)
     {
      isInside = true;
      break;
     }
    }
    if(!isInside)
     return false;
   }
   return true;
  }
  public override string ToString()
  {
   string res = string.Format("Near:\n{0}Far:\n{1}", planes[4], planes[1] );
   return res;
  }
 }
}
