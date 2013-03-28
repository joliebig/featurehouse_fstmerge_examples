using System;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using WorldWind.Camera;
namespace WorldWind
{
 public class BoundingBox
 {
  public Vector3[] corners;
  public BoundingBox(Vector3 v0, Vector3 v1, Vector3 v2, Vector3 v3, Vector3 v4, Vector3 v5, Vector3 v6, Vector3 v7)
  {
   this.corners = new Vector3[8];
   this.corners[0] = v0;
   this.corners[1] = v1;
   this.corners[2] = v2;
   this.corners[3] = v3;
   this.corners[4] = v4;
   this.corners[5] = v5;
   this.corners[6] = v6;
   this.corners[7] = v7;
  }
  public BoundingBox( float south, float north, float west, float east, float radius1, float radius2)
  {
   float scale = radius2 / radius1;
   this.corners = new Vector3[8];
   this.corners[0] = MathEngine.SphericalToCartesian(south, west, radius1);
   this.corners[1] = Vector3.Scale(this.corners[0], scale);
   this.corners[2] = MathEngine.SphericalToCartesian(south, east, radius1);
   this.corners[3] = Vector3.Scale(this.corners[2], scale);
   this.corners[4] = MathEngine.SphericalToCartesian(north, west, radius1);
   this.corners[5] = Vector3.Scale(this.corners[4], scale);
   this.corners[6] = MathEngine.SphericalToCartesian(north, east, radius1);
   this.corners[7] = Vector3.Scale(this.corners[6], scale);
  }
  public Vector3 CalculateCenter()
  {
   Vector3 res = new Vector3();
   foreach(Vector3 corner in corners)
   {
    res += corner;
   }
   res.Scale(1.0f / corners.Length);
   return res;
  }
  public float CalcRelativeScreenArea(CameraBase camera)
  {
   Vector3 a = camera.Project(corners[0]);
   Vector3 b = camera.Project(corners[2]);
   Vector3 c = camera.Project(corners[6]);
   Vector3 d = camera.Project(corners[4]);
   Vector3 ab = Vector3.Subtract(b,a);
   Vector3 ac = Vector3.Subtract(c,a);
   Vector3 ad = Vector3.Subtract(d,a);
   float tri1SqArea = Vector3.Cross(ab,ac).LengthSq();
   float tri2SqArea = Vector3.Cross(ad,ac).LengthSq();
   return tri1SqArea + tri2SqArea;
  }
 }
}
