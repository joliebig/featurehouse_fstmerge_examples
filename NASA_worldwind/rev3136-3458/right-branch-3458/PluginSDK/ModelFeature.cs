using System;
using System.IO;
using System.Drawing;
using WorldWind;
using System.Xml;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using Utility;
namespace WorldWind.Renderable
{
    class ModelFeature:WorldWind.Renderable.RenderableObject
    {
  public float Latitude;
  public float Longitude;
  public float Scale = 1;
  public float Altitude;
        public float Rotx = 0, Roty = 0, Rotz = 0;
  public float jpVertExaggeration=1;
  public int jpVertExaggerable = 1;
  Vector3 worldXyz;
  string meshFileName;
  Mesh mesh;
  Texture[] meshTextures;
  static Material[] meshMaterials;
  string errorMsg;
        public ModelFeature(string name,World parentWorld,string fileName,float Latitude,
            float Longitude,float Altitude,float Scale,float rotX,float rotY,float rotZ)
            : base(name,parentWorld)
  {
   meshFileName = fileName;
            this.Latitude = Latitude;
            this.Longitude = Longitude;
            this.Altitude = Altitude;
            this.Scale = Scale;
            this.Rotx = rotX;
            this.Roty = rotY;
            this.Rotz = rotZ;
  }
  bool IsVisible(WorldWind.Camera.CameraBase camera)
  {
   if(worldXyz == Vector3.Empty)
    worldXyz = MathEngine.SphericalToCartesian(Latitude, Longitude, camera.WorldRadius );
   return camera.ViewFrustum.ContainsPoint(worldXyz);
  }
  public override void Render(DrawArgs drawArgs)
  {
   if(errorMsg != null)
   {
    errorMsg = null;
    IsOn = false;
    isInitialized = false;
    return;
   }
   if(!IsVisible(drawArgs.WorldCamera))
   {
    if(isInitialized)
     Dispose();
    return;
   }
   if(!isInitialized)
    return;
   drawArgs.device.RenderState.CullMode = Cull.None;
   drawArgs.device.RenderState.Lighting = true;
   drawArgs.device.RenderState.AmbientColor = 0x808080;
   drawArgs.device.RenderState.NormalizeNormals = true;
   drawArgs.device.Lights[0].Diffuse = Color.FromArgb(255, 255, 255);
   drawArgs.device.Lights[0].Type = LightType.Directional;
   drawArgs.device.Lights[0].Direction = new Vector3(1f,1f,1f);
   drawArgs.device.Lights[0].Enabled = true;
   drawArgs.device.SamplerState[0].AddressU = TextureAddress.Wrap;
   drawArgs.device.SamplerState[0].AddressV = TextureAddress.Wrap;
   drawArgs.device.RenderState.AlphaBlendEnable = true;
   drawArgs.device.TextureState[0].ColorArgument1 = TextureArgument.TextureColor;
   drawArgs.device.TextureState[0].ColorOperation = TextureOperation.SelectArg1;
   drawArgs.device.Lights[0].Position = new Vector3(
    (float)worldXyz.X*2f,
    (float)worldXyz.Y*1f,
    (float)worldXyz.Z*1.5f);
   Matrix currentWorld = drawArgs.device.Transform.World;
   drawArgs.device.Transform.World = Matrix.RotationX((float)MathEngine.DegreesToRadians(Rotx));
            drawArgs.device.Transform.World *= Matrix.RotationZ((float)MathEngine.DegreesToRadians(Roty));
   drawArgs.device.Transform.World *= Matrix.RotationZ((float)MathEngine.DegreesToRadians(Rotz));
   drawArgs.device.Transform.World *= Matrix.Scaling(Scale, Scale, Scale);
   if (jpVertExaggerable==1)
    jpVertExaggeration=World.Settings.VerticalExaggeration;
   else jpVertExaggeration=1;
   drawArgs.device.Transform.World *= Matrix.Translation(0,0,(float)drawArgs.WorldCamera.WorldRadius + Altitude*jpVertExaggeration);
   drawArgs.device.Transform.World *= Matrix.RotationY((float)MathEngine.DegreesToRadians(90-Latitude));
   drawArgs.device.Transform.World *= Matrix.RotationZ((float)MathEngine.DegreesToRadians(Longitude));
            drawArgs.device.Transform.World *= Matrix.Translation(
                (float)-drawArgs.WorldCamera.ReferenceCenter.X,
                (float)-drawArgs.WorldCamera.ReferenceCenter.Y,
                (float)-drawArgs.WorldCamera.ReferenceCenter.Z
                );
   for( int i = 0; i < meshMaterials.Length; i++ )
   {
    drawArgs.device.Material = meshMaterials[i];
    drawArgs.device.SetTexture(0, meshTextures[i]);
    mesh.DrawSubset(i);
   }
   drawArgs.device.Transform.World = currentWorld;
   drawArgs.device.RenderState.Lighting = false;
  }
  public override void Initialize(DrawArgs drawArgs)
  {
   if(!IsVisible(drawArgs.WorldCamera))
    return;
   try
   {
                if(meshFileName.EndsWith(".x"))
                    LoadDirectXMesh(drawArgs);
                else if(meshFileName.EndsWith(".dae")||meshFileName.EndsWith(".xml"))
                    LoadColladaMesh(drawArgs);
                if (mesh == null)
                    throw new InvalidMeshException();
    jpVertExaggeration = World.Settings.VerticalExaggeration;
    isInitialized = true;
   }
   catch(Exception caught)
   {
    Utility.Log.Write( caught );
    errorMsg = "Failed to read mesh from " + meshFileName;
   }
  }
        private void LoadDirectXMesh(DrawArgs drawArgs)
        {
            ExtendedMaterial[] materials;
   GraphicsStream adj;
   mesh = Mesh.FromFile(meshFileName, MeshFlags.Managed, drawArgs.device, out adj, out materials );
   meshTextures = new Texture[materials.Length];
   meshMaterials = new Material[materials.Length];
   string xFilePath = Path.GetDirectoryName(meshFileName);
   for(int i = 0; i < materials.Length; i++)
   {
    meshMaterials[i] = materials[i].Material3D;
    meshMaterials[i].Ambient = meshMaterials[i].Diffuse;
    if(materials[i].TextureFilename!=null)
    {
     string textureFilePath = Path.Combine(xFilePath, materials[i].TextureFilename);
     meshTextures[i] = TextureLoader.FromFile(drawArgs.device, textureFilePath);
    }
   }
        }
        private void LoadColladaMesh(DrawArgs drawArgs)
        {
            XmlDocument colladaDoc = new XmlDocument();
            colladaDoc.Load(meshFileName);
            XmlNodeList geometryNodes = colladaDoc.SelectNodes("geometry");
            if (geometryNodes != null)
            {
                foreach (XmlNode geometryNode in geometryNodes)
                {
                    XmlNodeList sourceNodes = geometryNode.SelectNodes("mesh/source");
                    XmlNode vertexNode = geometryNode.SelectSingleNode("mesh/vertices");
                    XmlNode lineNode = geometryNode.SelectSingleNode("mesh/lines");
                    XmlNodeList triangleNodes = geometryNode.SelectNodes("mesh/triangles");
                }
            }
        }
  public override void Update(DrawArgs drawArgs)
  {
   if(!isInitialized)
    Initialize(drawArgs);
   else if ((jpVertExaggeration != World.Settings.VerticalExaggeration)&&(jpVertExaggerable==1))
    Initialize(drawArgs);
  }
  public override void Dispose()
  {
   isInitialized = false;
   if(mesh!=null)
    mesh.Dispose();
   if(meshTextures!=null)
    foreach(Texture t in meshTextures)
     if(t!=null)
      t.Dispose();
  }
  public override bool PerformSelectionAction(DrawArgs drawArgs)
  {
   return false;
  }
    }
}
