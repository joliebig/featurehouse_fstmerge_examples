using System;
using System.Collections;
using System.IO;
using System.Diagnostics;
using System.Windows.Forms;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using WorldWind;
using WorldWind.Terrain;
using WorldWind.Renderable;
namespace jhuapl.util
{
 public class JHU_TerrainPath : RenderableObject
 {
  float north;
  float south;
  float east;
  float west;
  BoundingBox boundingBox;
  World _parentWorld;
  BinaryReader _dataArchiveReader;
  long _fileOffset;
  long _fileSize;
  TerrainAccessor _terrainAccessor;
  float heightAboveSurface;
  string terrainFileName;
  bool terrainMapped;
  bool isLoaded;
  Vector3 lastUpdatedPosition;
  float verticalExaggeration = 1.0f;
  double _minDisplayAltitude, _maxDisplayAltitude;
  int lineColor;
  public CustomVertex.PositionColored[] linePoints;
  ArrayList sphericalCoordinates;
  bool m_needsUpdate = false;
  public int NumPoints
  {
   get
   {
    if (sphericalCoordinates != null)
     return sphericalCoordinates.Count;
    else
     return 0;
   }
  }
  public JHU_TerrainPath(
   string name,
   World parentWorld,
   double minDisplayAltitude,
   double maxDisplayAltitude,
   string terrainFileName,
   float heightAboveSurface,
   System.Drawing.Color lineColor,
   TerrainAccessor terrainAccessor)
   : base(name, parentWorld.Position, Quaternion.RotationYawPitchRoll(0,0,0))
  {
   this._parentWorld = parentWorld;
   this._minDisplayAltitude = minDisplayAltitude;
   this._maxDisplayAltitude = maxDisplayAltitude;
   this.terrainFileName = terrainFileName;
   this.heightAboveSurface = heightAboveSurface;
   this.terrainMapped = terrainMapped;
   this.lineColor = lineColor.ToArgb();
   this._terrainAccessor = terrainAccessor;
   this.RenderPriority = RenderPriority.LinePaths;
   this.sphericalCoordinates = new ArrayList();
  }
  public JHU_TerrainPath(
   string name,
   World parentWorld,
   double minDisplayAltitude,
   double maxDisplayAltitude,
   BinaryReader dataArchiveReader,
   long fileOffset,
   long fileSize,
   double north,
   double south,
   double east,
   double west,
   float heightAboveSurface,
   System.Drawing.Color lineColor,
   TerrainAccessor terrainAccessor)
   : base(name, parentWorld.Position, Quaternion.RotationYawPitchRoll(0,0,0))
  {
   this._parentWorld = parentWorld;
   this._minDisplayAltitude = minDisplayAltitude;
   this._maxDisplayAltitude = maxDisplayAltitude;
   this._dataArchiveReader = dataArchiveReader;
   this._fileOffset = fileOffset;
   this._fileSize = fileSize;
   this.heightAboveSurface = heightAboveSurface;
   this.terrainMapped = terrainMapped;
   this.lineColor = lineColor.ToArgb();
   this._terrainAccessor = terrainAccessor;
   this.sphericalCoordinates = new ArrayList();
   this.north = (float)north;
   this.south = (float)south;
   this.west = (float)west;
   this.east = (float)east;
   this.RenderPriority = RenderPriority.LinePaths;
   this.boundingBox = new BoundingBox( this.south, this.north, this.west, this.east,
    (float)this._parentWorld.EquatorialRadius,
    (float)(this._parentWorld.EquatorialRadius + this.verticalExaggeration * heightAboveSurface));
  }
  public override void Initialize(DrawArgs drawArgs)
  {
   this.verticalExaggeration = World.Settings.VerticalExaggeration;
   this.isInitialized = true;
  }
  public void Load(DrawArgs drawArgs)
  {
   try
   {
    if (this.sphericalCoordinates == null)
     this.sphericalCoordinates = new ArrayList();
    if(this._dataArchiveReader == null)
    {
     if(this.terrainFileName == null)
     {
      this.isInitialized = true;
      return;
     }
     FileInfo inFile = new FileInfo(this.terrainFileName);
     if(!inFile.Exists)
     {
      this.isInitialized = true;
      return;
     }
     using( BufferedStream fs = new BufferedStream(inFile.OpenRead()) )
     using( BinaryReader br = new BinaryReader(fs) )
     {
      int numCoords = br.ReadInt32();
      Vector3 newCoord = new Vector3();
      newCoord.X = br.ReadSingle();
      newCoord.Y = br.ReadSingle();
      sphericalCoordinates.Add(newCoord);
      for (int i = 1; i < numCoords; i++)
      {
       newCoord.X = br.ReadSingle();
       newCoord.Y = br.ReadSingle();
       sphericalCoordinates.Add(newCoord);
       if (this.north < newCoord.X)
        this.north = newCoord.X;
       if (this.east < newCoord.Y)
        this.east = newCoord.Y;
       if (this.south > newCoord.X)
        this.south = newCoord.X;
       if (this.west > newCoord.Y)
        this.west = newCoord.Y;
      }
     }
     this.boundingBox = new BoundingBox( this.south, this.north, this.west, this.east,
      (float)this._parentWorld.EquatorialRadius,
      (float)(this._parentWorld.EquatorialRadius + this.verticalExaggeration * heightAboveSurface));
    }
    else
    {
     this._dataArchiveReader.BaseStream.Seek(this._fileOffset, SeekOrigin.Begin);
     int numCoords = this._dataArchiveReader.ReadInt32();
     byte numElements = this._dataArchiveReader.ReadByte();
     Vector3 newCoord = new Vector3();
     for(int i = 0; i < numCoords; i++)
     {
      newCoord.X = (float)this._dataArchiveReader.ReadDouble();
      newCoord.Y = (float)this._dataArchiveReader.ReadDouble();
      if(numElements == 3)
       newCoord.Z = this._dataArchiveReader.ReadInt16();
      sphericalCoordinates.Add(newCoord);
     }
    }
   }
   catch(Exception caught)
   {
    Utility.Log.Write( caught );
   }
   this.isLoaded = true;
  }
  public override void Dispose()
  {
   this.isLoaded = false;
   this.linePoints = null;
   this.sphericalCoordinates = null;
   this.isInitialized = false;
  }
  public void SaveToFile(string fileName)
  {
   using (BinaryWriter output = new BinaryWriter(new FileStream(fileName, FileMode.Create)))
   {
    output.Write(this.sphericalCoordinates.Count);
    for (int i = 0; i < this.sphericalCoordinates.Count; i++)
    {
     Vector3 outCoord = (Vector3) this.sphericalCoordinates[i];
     output.Write(outCoord.X);
     output.Write(outCoord.Y);
    }
   }
  }
  public override void Update(DrawArgs drawArgs)
  {
   try
   {
    if(!drawArgs.WorldCamera.ViewFrustum.Intersects(boundingBox))
     return;
    if(!isLoaded)
     Load(drawArgs);
    if(linePoints != null)
     if((lastUpdatedPosition - drawArgs.WorldCamera.Position).LengthSq() < 10*10)
      if(Math.Abs(this.verticalExaggeration - World.Settings.VerticalExaggeration) < 0.01)
       return;
    verticalExaggeration = World.Settings.VerticalExaggeration;
    ArrayList renderablePoints = new ArrayList();
    Vector3 lastPointProjected = Vector3.Empty;
    Vector3 currentPointProjected;
    Vector3 currentPointXyz = Vector3.Empty;
    for(int i = 0; i < sphericalCoordinates.Count; i++)
    {
     double altitude = 0;
     Vector3 curCoord = (Vector3) sphericalCoordinates[i];
     if(_parentWorld.TerrainAccessor != null && drawArgs.WorldCamera.Altitude < 300000)
      altitude = _terrainAccessor.GetElevationAt(
       curCoord.X,
       curCoord.Y,
       (100.0 / drawArgs.WorldCamera.ViewRange.Degrees));
     currentPointXyz = MathEngine.SphericalToCartesian(
      curCoord.X,
      curCoord.Y,
      this._parentWorld.EquatorialRadius + this.heightAboveSurface +
      this.verticalExaggeration * altitude );
     currentPointProjected = drawArgs.WorldCamera.Project(currentPointXyz);
     float dx = lastPointProjected.X - currentPointProjected.X;
     float dy = lastPointProjected.Y - currentPointProjected.Y;
     float distanceSquared = dx*dx + dy*dy;
     const float minimumPointSpacingSquaredPixels = 2*2;
     if(distanceSquared > minimumPointSpacingSquaredPixels)
     {
      renderablePoints.Add(currentPointXyz);
      lastPointProjected = currentPointProjected;
     }
    }
    int pointCount = renderablePoints.Count;
    if(pointCount>0 && (Vector3)renderablePoints[pointCount-1] != currentPointXyz)
    {
     renderablePoints.Add(currentPointXyz);
     pointCount++;
    }
    CustomVertex.PositionColored[] newLinePoints = new CustomVertex.PositionColored[pointCount];
    for(int i = 0; i < pointCount; i++)
    {
     currentPointXyz = (Vector3)renderablePoints[i];
     newLinePoints[i].X = currentPointXyz.X;
     newLinePoints[i].Y = currentPointXyz.Y;
     newLinePoints[i].Z = currentPointXyz.Z;
     newLinePoints[i].Color = this.lineColor;
    }
    this.linePoints = newLinePoints;
    lastUpdatedPosition = drawArgs.WorldCamera.Position;
    System.Threading.Thread.Sleep(1);
   }
   catch(Exception caught)
   {
    Utility.Log.Write( caught );
    Debug.WriteLine(this.name + ": " + caught);
   }
  }
  public override bool PerformSelectionAction(DrawArgs drawArgs)
  {
   return false;
  }
  public void AddPointToPath(float lat, float lon, float alt)
  {
   Vector3 newCoord = new Vector3(lat, lon, alt);
   this.sphericalCoordinates.Add(newCoord);
   if (this.north < newCoord.X)
    this.north = newCoord.X;
   if (this.east < newCoord.Y)
    this.east = newCoord.Y;
   if (this.south > newCoord.X)
    this.south = newCoord.X;
   if (this.west > newCoord.Y)
    this.west = newCoord.Y;
   this.boundingBox = new BoundingBox( this.south, this.north, this.west, this.east,
    (float)this._parentWorld.EquatorialRadius,
    (float)(this._parentWorld.EquatorialRadius + this.verticalExaggeration * alt));
   lastUpdatedPosition.X = lat;
   this.isLoaded = true;
   this.isInitialized = false;
   this.m_needsUpdate = true;
  }
  public Vector3 GetPoint(int index)
  {
   if ((sphericalCoordinates != null) &&
    (index >= 0) &&
    (index < sphericalCoordinates.Count) )
    return (Vector3) this.sphericalCoordinates[index];
   else
    return new Vector3(0,0,0);
  }
  public override void BuildContextMenu( ContextMenu menu )
  {
   MenuItem topMenuItem = new MenuItem(Name);
   menu.MenuItems.Add("Properties", new System.EventHandler(OnPropertiesClick));
   menu.MenuItems.Add("Delete", new System.EventHandler(MyDeleteClick));
  }
  protected virtual void MyDeleteClick(object sender, System.EventArgs e)
  {
   if(ParentList == null)
   {
    MessageBox.Show("Unable to delete root layer list.", "Error", MessageBoxButtons.OK, MessageBoxIcon.Exclamation);
    return;
   }
   string message = "Permanently Delete Object '" + name + "'?";
   if(DialogResult.Yes != MessageBox.Show( message, "Delete object", MessageBoxButtons.YesNo, MessageBoxIcon.Warning,
    MessageBoxDefaultButton.Button2 ))
    return;
   try
   {
    Delete();
   }
   catch(Exception ex)
   {
    MessageBox.Show(ex.Message);
   }
  }
  public override void Delete()
  {
   ParentList.ChildObjects.Remove(this);
   Dispose();
  }
  public override void Render(DrawArgs drawArgs)
  {
   try
   {
    if(!this.isLoaded)
     Load(drawArgs);
    if(drawArgs.WorldCamera.Altitude > _maxDisplayAltitude)
     return;
    if(drawArgs.WorldCamera.Altitude < _minDisplayAltitude)
     return;
    if (m_needsUpdate)
     this.Update(drawArgs);
    if(this.linePoints == null)
     return;
    if(!drawArgs.WorldCamera.ViewFrustum.Intersects(this.boundingBox))
     return;
    drawArgs.numBoundaryPointsRendered += this.linePoints.Length;
    drawArgs.numBoundaryPointsTotal += this.sphericalCoordinates.Count;
    drawArgs.numBoundariesDrawn++;
    drawArgs.device.VertexFormat = CustomVertex.PositionColored.Format;
    drawArgs.device.TextureState[0].ColorOperation = TextureOperation.Disable;
    drawArgs.device.DrawUserPrimitives( PrimitiveType.LineStrip, this.linePoints.Length - 1, this.linePoints );
   }
   catch(Exception caught)
   {
    Utility.Log.Write( caught );
    Debug.WriteLine(this.name + ": " + caught);
   }
  }
 }
}
