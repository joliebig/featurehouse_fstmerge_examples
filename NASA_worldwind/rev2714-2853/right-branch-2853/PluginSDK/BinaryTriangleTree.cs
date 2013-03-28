using System;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
namespace WorldWind
{
 public class BinaryTriangleTree
 {
  CustomVertex.PositionTextured[] _elevatedVertices;
  int _vertexDensity;
  int _verticeDensity;
  int _margin;
  double _layerRadius;
  int _maxVariance;
  BinaryTriangle[] _treeList;
  int _treeListLength;
  int _terrainFaceCount;
  short nw, sw, ne, se;
  public int TriangleCount;
  public short[] Indices;
  public BinaryTriangleTree(
   CustomVertex.PositionTextured[] ElevatedVertices,
   int VertexDensity,
   int Margin,
   double LayerRadius)
  {
   this._elevatedVertices = ElevatedVertices;
   this._vertexDensity = VertexDensity;
   this._margin = Margin;
   this._layerRadius = LayerRadius;
   this._verticeDensity = this._margin + this._vertexDensity + this._margin + 1;
   this.nw = (short)((this._margin * this._verticeDensity) + this._margin);
   this.sw = (short)(nw + (short)(this._vertexDensity * this._verticeDensity));
   this.ne = (short)(nw + (short)(this._vertexDensity));
   this.se = (short)(sw + (short)(this._vertexDensity));
  }
  public void BuildTree(int MaxVariance)
  {
   this._maxVariance = MaxVariance;
   this._treeList = new BinaryTriangle[this._vertexDensity * this._vertexDensity * 8];
   this._treeList[0] = new BinaryTriangle(this.ne, this.nw, this.sw);
   this._treeList[1] = new BinaryTriangle(this.sw, this.se, this.ne);
   this._treeList[0].bn = 1;
   this._treeList[1].bn = 0;
   this._treeListLength = 2;
   BuildFace(0);
   BuildFace(1);
   this._terrainFaceCount = FaceCount();
  }
  private void BuildFace(short f)
  {
   if(this._treeList[f].lc != -1)
   {
    BuildFace(this._treeList[f].lc);
    BuildFace(this._treeList[f].rc);
   }
   else
   {
    if(FaceVariance(this._treeList[f].i1, this._treeList[f].i2, this._treeList[f].i3) > this._maxVariance)
    {
     SplitFace(f);
     BuildFace(this._treeList[f].lc);
     BuildFace(this._treeList[f].rc);
    }
   }
  }
  private void SplitFace(short f)
  {
   if(this._treeList[f].bn != -1)
   {
    if(this._treeList[this._treeList[f].bn].bn != f)
    {
     SplitFace(this._treeList[f].bn);
    }
    SplitFaceForReal(f);
    SplitFaceForReal(this._treeList[f].bn);
    this._treeList[this._treeList[f].lc].rn = this._treeList[this._treeList[f].bn].rc;
    this._treeList[this._treeList[f].rc].ln = this._treeList[this._treeList[f].bn].lc;
    this._treeList[this._treeList[this._treeList[f].bn].lc].rn = this._treeList[f].rc;
    this._treeList[this._treeList[this._treeList[f].bn].rc].ln = this._treeList[f].lc;
   }
   else
   {
    SplitFaceForReal(f);
   }
  }
  private void SplitFaceForReal(short f)
  {
   short mid_hyp_indice = MidHypVerticeIndice(this._treeList[f].i1, this._treeList[f].i3);
   this._treeList[this._treeListLength] = new BinaryTriangle(this._treeList[f].i2, mid_hyp_indice, this._treeList[f].i1);
   this._treeList[this._treeListLength + 1] = new BinaryTriangle(this._treeList[f].i3, mid_hyp_indice, this._treeList[f].i2);
   this._treeList[f].rc = (short)this._treeListLength;
   this._treeList[f].lc = (short)(this._treeListLength + 1);
   this._treeListLength += 2;
   this._treeList[this._treeList[f].lc].ln = this._treeList[f].rc;
   this._treeList[this._treeList[f].rc].rn = this._treeList[f].lc;
   this._treeList[this._treeList[f].lc].bn = this._treeList[f].ln;
   if(this._treeList[f].ln != -1)
   {
    if(this._treeList[this._treeList[f].ln].bn == f)
    {
     this._treeList[this._treeList[f].ln].bn = this._treeList[f].lc;
    }
    else
    {
     if(this._treeList[this._treeList[f].ln].ln == f)
     {
      this._treeList[this._treeList[f].ln].ln = this._treeList[f].lc;
     }
     else
     {
      this._treeList[this._treeList[f].ln].rn = this._treeList[f].lc;
     }
    }
   }
   this._treeList[this._treeList[f].rc].bn = this._treeList[f].rn;
   if(this._treeList[f].rn != -1)
   {
    if(this._treeList[this._treeList[f].rn].bn == f)
    {
     this._treeList[this._treeList[f].rn].bn = this._treeList[f].rc;
    }
    else
    {
     if(this._treeList[this._treeList[f].rn].rn == f)
     {
      this._treeList[this._treeList[f].rn].rn = this._treeList[f].rc;
     }
     else
     {
      this._treeList[this._treeList[f].rn].ln = this._treeList[f].rc;
     }
    }
   }
  }
  private short MidHypVerticeIndice(short v1, short v3)
  {
   short i1 = (short)Math.Floor((float)(v1 / this._verticeDensity));
   short j1 = (short)(v1 % (short)this._verticeDensity);
   short i3 = (short)Math.Floor((float)(v3 / this._verticeDensity));
   short j3 = (short)(v3 % (short)this._verticeDensity);
   short ih = (short)(i1 + ((i3 - i1) / 2));
   short jh = (short)(j1 + ((j3 - j1) / 2));
   short mid_hyp_indice = (short)((ih * (short)this._verticeDensity) + jh);
   return mid_hyp_indice;
  }
  private int FaceCount()
  {
   int tot = 0;
   for(short i = 0; i < this._treeListLength; i++)
   {
    if(this._treeList[i].rc == -1) tot++;
   }
   return tot;
  }
  private double FaceVariance(short v1, short v2, short v3)
  {
   double MaxVar = 0;
   if(Math.Abs(v1 - v2) == 1 || Math.Abs(v3 - v2) == 1)
   {
    MaxVar = 0;
   }
   else
   {
    short mid_hyp_indice = MidHypVerticeIndice(v1, v3);
    CustomVertex.PositionTextured vh = this._elevatedVertices[mid_hyp_indice];
    Vector3 v = MathEngine.CartesianToSpherical(vh.X, vh.Y, vh.Z);
    double real = v.X - this._layerRadius;
    float xe = (this._elevatedVertices[v1].X + this._elevatedVertices[v3].X) / 2;
    float ye = (this._elevatedVertices[v1].Y + this._elevatedVertices[v3].Y) / 2;
    float ze = (this._elevatedVertices[v1].Z + this._elevatedVertices[v3].Z) / 2;
    v = MathEngine.CartesianToSpherical(xe, ye, ze);
    double extrapolated = v.X - this._layerRadius;
    MaxVar = real - extrapolated;
    MaxVar = Math.Max(MaxVar, FaceVariance(v2, mid_hyp_indice, v1));
    MaxVar = Math.Max(MaxVar, FaceVariance(v3, mid_hyp_indice, v2));
   }
   return MaxVar;
  }
  public void BuildIndices()
  {
   int MarginFaces = (this._margin + this._vertexDensity) * this._margin * 2 * 4;
   int TotFaces = MarginFaces + this._terrainFaceCount;
   this.Indices = new short[TotFaces * 3];
   int idx = 0;
   for(short i = 0; i < this._treeListLength; i++)
   {
    if(this._treeList[i].rc == -1)
    {
     this.Indices[idx] = this._treeList[i].i1;
     this.Indices[idx+1] = this._treeList[i].i2;
     this.Indices[idx+2] = this._treeList[i].i3;
     idx += 3;
    }
   }
   if(this._margin != 0)
   {
    short i1, i2;
    i1 = this.nw;
    for(i2 = (short)(this.nw + 1); i2 <= this.ne; i2++)
    {
     if(VerticeIsUsedInTerrain(i2))
     {
      this.Indices[idx] = i1;
      this.Indices[idx+1] = i2;
      this.Indices[idx+2] = (short)(i2 - (short)this._verticeDensity);
      idx += 3;
      this.Indices[idx] = (short)(i2 - (short)this._verticeDensity);
      this.Indices[idx+1] = (short)(i1 - (short)this._verticeDensity);
      this.Indices[idx+2] = i1;
      idx += 3;
      i1 = i2;
     }
    }
    i1 = this.sw;
    for(i2 = (short)(this.sw + 1); i2 <= this.se; i2++)
    {
     if(VerticeIsUsedInTerrain(i2))
     {
      this.Indices[idx] = i2;
      this.Indices[idx+1] = i1;
      this.Indices[idx+2] = (short)(i1 + (short)this._verticeDensity);
      idx += 3;
      this.Indices[idx] = (short)(i1 + (short)this._verticeDensity);
      this.Indices[idx+1] = (short)(i2 + (short)this._verticeDensity);
      this.Indices[idx+2] = i2;
      idx += 3;
      i1 = i2;
     }
    }
    i1 = this.nw;
    for(i2 = (short)(this.nw + (short)this._verticeDensity); i2 <= this.sw; i2 += (short)this._verticeDensity)
    {
     if(VerticeIsUsedInTerrain(i2))
     {
      this.Indices[idx] = i2;
      this.Indices[idx+1] = i1;
      this.Indices[idx+2] = (short)(i1 - 1);
      idx += 3;
      this.Indices[idx] = (short)(i1 - 1);
      this.Indices[idx+1] = (short)(i2 - 1);
      this.Indices[idx+2] = i2;
      idx += 3;
      i1 = i2;
     }
    }
    i1 = this.ne;
    for(i2 = (short)(this.ne + (short)this._verticeDensity); i2 <= this.se; i2 += (short)this._verticeDensity)
    {
     if(VerticeIsUsedInTerrain(i2))
     {
      this.Indices[idx] = i1;
      this.Indices[idx+1] = i2;
      this.Indices[idx+2] = (short)(i2 + 1);
      idx += 3;
      this.Indices[idx] = (short)(i2 + 1);
      this.Indices[idx+1] = (short)(i1 + 1);
      this.Indices[idx+2] = i1;
      idx += 3;
      i1 = i2;
     }
    }
   }
  }
  private bool VerticeIsUsedInTerrain(short v)
  {
   for(int i = 0; i < this.Indices.Length; i++)
   {
    if (this.Indices[i] == v) return true;
   }
   return false;
  }
 }
 public class BinaryTriangle
 {
  public short i1, i2, i3;
  public short lc, rc;
  public short ln, rn, bn;
  public BinaryTriangle(short vertice1, short vertice2, short vertice3)
  {
   this.i1 = vertice1;
   this.i2 = vertice2;
   this.i3 = vertice3;
   this.lc = -1;
   this.rc = -1;
   this.ln = -1;
   this.rn = -1;
   this.bn = -1;
  }
 }
}
