using System;
using Microsoft.DirectX;
using Microsoft.DirectX.Direct3D;
using System.Runtime.InteropServices;
using System.Drawing;
namespace WorldWind.Renderable
{
 class Water : ModelFeature
 {
  private const string meshFilename = "Data/water.x";
  private const string textureFilename = "Data/water.dds";
        private const string cloudFilename = "Data/cloud.dds";
  private const string effectFilename = "Shaders/water.fx";
  private CubeTexture texCube;
  private static float waterTime = 0;
        protected Effect effect;
        protected Matrix matWorld;
        protected Matrix matView;
        protected Matrix matProj;
        protected Texture texture;
  public Water(string name,World parentWorld,float lat,float lon,float alt,float scaleFactor) :
            base(name,parentWorld,meshFilename,lat,lon,alt,scaleFactor,90.0f,0.0f,0.0f)
  {
  }
        public override void Initialize(DrawArgs drawArgs)
        {
            base.Initialize(drawArgs);
            Device device = drawArgs.device;
            VertexElement[] elements = new VertexElement[]
   {
    new VertexElement(0, 0, DeclarationType.Float3, DeclarationMethod.Default, DeclarationUsage.Position, 0),
    new VertexElement(0, 12, DeclarationType.Float3, DeclarationMethod.Default, DeclarationUsage.Normal, 0),
    new VertexElement(0, 24, DeclarationType.Float3, DeclarationMethod.Default, DeclarationUsage.TextureCoordinate, 0),
    new VertexElement(0, 36, DeclarationType.Float3, DeclarationMethod.Default, DeclarationUsage.Tangent, 0),
    new VertexElement(0, 48, DeclarationType.Float3, DeclarationMethod.Default, DeclarationUsage.BiNormal, 0),
    VertexElement.VertexDeclarationEnd,
   };
            VertexDeclaration decl = new VertexDeclaration(device, elements);
            Mesh tempMesh = mesh.Clone(MeshFlags.Managed, elements, device);
            mesh.Dispose();
            mesh = tempMesh;
            mesh.ComputeTangentFrame(0);
            effect = Effect.FromFile(device, effectFilename, null, null, ShaderFlags.Debug | ShaderFlags.PartialPrecision, null);
            texCube = TextureLoader.FromCubeFile(device, cloudFilename);
            texture = TextureLoader.FromFile(device, textureFilename);
            isInitialized = true;
        }
  public override void Render(DrawArgs drawArgs)
  {
            if (errorMsg != null)
            {
                errorMsg = null;
                IsOn = false;
                isInitialized = false;
                return;
            }
            if (!IsVisible(drawArgs.WorldCamera))
            {
                if (isInitialized)
                    Dispose();
                return;
            }
            if (!isInitialized)
                return;
            drawArgs.device.RenderState.CullMode = Cull.None;
            drawArgs.device.RenderState.Lighting = true;
            drawArgs.device.RenderState.AmbientColor = 0x808080;
            drawArgs.device.RenderState.NormalizeNormals = true;
            drawArgs.device.Lights[0].Diffuse = Color.FromArgb(255, 255, 255);
            drawArgs.device.Lights[0].Type = LightType.Directional;
            drawArgs.device.Lights[0].Direction = new Vector3(1f, 1f, 1f);
            drawArgs.device.Lights[0].Enabled = true;
            drawArgs.device.SamplerState[0].AddressU = TextureAddress.Wrap;
            drawArgs.device.SamplerState[0].AddressV = TextureAddress.Wrap;
            drawArgs.device.RenderState.AlphaBlendEnable = true;
            drawArgs.device.TextureState[0].ColorArgument1 = TextureArgument.TextureColor;
            drawArgs.device.TextureState[0].ColorOperation = TextureOperation.SelectArg1;
            drawArgs.device.Lights[0].Position = new Vector3(
                (float)worldXyz.X * 2f,
                (float)worldXyz.Y * 1f,
                (float)worldXyz.Z * 1.5f);
            Matrix currentWorld = drawArgs.device.Transform.World;
            drawArgs.device.Transform.World = Matrix.RotationX((float)MathEngine.DegreesToRadians(Rotx));
            drawArgs.device.Transform.World *= Matrix.RotationZ((float)MathEngine.DegreesToRadians(Roty));
            drawArgs.device.Transform.World *= Matrix.RotationZ((float)MathEngine.DegreesToRadians(Rotz));
            drawArgs.device.Transform.World *= Matrix.Scaling(Scale, Scale, Scale);
            if (jpVertExaggerable == 1)
                jpVertExaggeration = World.Settings.VerticalExaggeration;
            else jpVertExaggeration = 1;
            drawArgs.device.Transform.World *= Matrix.Translation(0, 0, (float)drawArgs.WorldCamera.WorldRadius + Altitude * jpVertExaggeration);
            drawArgs.device.Transform.World *= Matrix.RotationY((float)MathEngine.DegreesToRadians(90 - Latitude));
            drawArgs.device.Transform.World *= Matrix.RotationZ((float)MathEngine.DegreesToRadians(Longitude));
            drawArgs.device.Transform.World *= Matrix.Translation(
                (float)-drawArgs.WorldCamera.ReferenceCenter.X,
                (float)-drawArgs.WorldCamera.ReferenceCenter.Y,
                (float)-drawArgs.WorldCamera.ReferenceCenter.Z
                );
            Device device = drawArgs.device;
   float time = (float)(Environment.TickCount * 0.001);
   waterTime += 0.002f;
            Matrix modelViewProj = drawArgs.device.Transform.World * drawArgs.device.Transform.View * drawArgs.device.Transform.Projection;
            Matrix modelViewIT = drawArgs.device.Transform.World * drawArgs.device.Transform.View * drawArgs.device.Transform.Projection;
   modelViewIT.Invert();
   modelViewIT = Matrix.TransposeMatrix(modelViewIT);
   effect.Technique = "water";
   effect.SetValue("texture0", texture);
   effect.SetValue("texture1", texCube);
   effect.SetValue("ModelViewProj", modelViewProj);
            effect.SetValue("ModelWorld", drawArgs.device.Transform.World);
   effect.SetValue("eyePos", new Vector4(450.0f, 250.0f, 750.0f, 1.0f));
   effect.SetValue("lightPos", new Vector4((float)(300 * Math.Sin(time)), 40.0f, (float)(300 * Math.Cos(time)), 1.0f));
   effect.SetValue("time", waterTime);
   device.RenderState.AlphaBlendEnable = true;
   effect.Begin(0);
   effect.BeginPass(0);
   mesh.DrawSubset(0);
   effect.EndPass();
   effect.End();
   device.RenderState.AlphaBlendEnable = false;
            drawArgs.device.Transform.World = currentWorld;
            drawArgs.device.RenderState.Lighting = false;
  }
  public void Render(Device device, Texture waterTexture)
  {
   float time = (float)(Environment.TickCount * 0.001);
   waterTime += 0.002f;
   Matrix modelViewProj = matWorld * matView * matProj;
   Matrix modelViewIT = matWorld * matView * matProj;
   modelViewIT.Invert();
   modelViewIT = Matrix.TransposeMatrix(modelViewIT);
   effect.Technique = "water";
   effect.SetValue("texture0", waterTexture);
   effect.SetValue("texture1", texCube);
   effect.SetValue("ModelViewProj", modelViewProj);
   effect.SetValue("ModelWorld", matWorld);
   effect.SetValue("eyePos", new Vector4(450.0f, 250.0f, 750.0f, 1.0f));
   effect.SetValue("lightPos", new Vector4((float)(300 * Math.Sin(time)), 40.0f, (float)(300 * Math.Cos(time)), 1.0f));
   effect.SetValue("time", waterTime);
   effect.Begin(0);
   effect.BeginPass(0);
   mesh.DrawSubset(0);
   effect.EndPass();
   effect.End();
  }
        public override void Dispose()
        {
            this.isInitialized = false;
            if (this.mesh != null)
            {
                this.mesh.Dispose();
                this.mesh = null;
            }
            if (this.texCube != null)
            {
                this.texCube.Dispose();
                this.texCube = null;
            }
            if (this.texture != null)
            {
                this.texture.Dispose();
                this.texture = null;
            }
            if (this.effect != null)
            {
                this.effect.Dispose();
                this.effect = null;
            }
            base.Dispose();
        }
        public override bool PerformSelectionAction(DrawArgs drawArgs)
        {
            return false;
        }
 }
}
