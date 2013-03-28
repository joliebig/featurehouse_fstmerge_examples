

package org.jmol.g3d;




final class Shade3D {

  
  
  
  static final int shadeMax = 64;
  static final int shadeLast = shadeMax - 1;

  static byte shadeNormal = 52;

  
  private static final float xLightsource = -1;
  private static final float yLightsource = -1;
  private static final float zLightsource = 2.5f;
  private static final float magnitudeLight =
    (float)Math.sqrt(xLightsource * xLightsource +
                     yLightsource * yLightsource +
                     zLightsource * zLightsource);
  
  static final float xLight = xLightsource / magnitudeLight;
  static final float yLight = yLightsource / magnitudeLight;
  static final float zLight = zLightsource / magnitudeLight;
  
  

  

  
  static int SPECULAR_ON = 0; 
  static int SPECULAR_PERCENT = 1;
  static int SPECULAR_EXPONENT = 2;
  static int SPECULAR_POWER = 3;
  static int DIFFUSE_PERCENT = 4;
  static int AMBIENT_PERCENT = 5;
  
  static int INTENSITY_SPECULAR = 6;
  static int INTENSE_FRACTION = 7;
  static int INTENSITY_DIFFUSE = 8;
  static int AMBIENT_FRACTION = 9;

  final static float[] lighting = new float[] {
      
      1f,       
      22f,      
      6f,       
      40f,      
      84f,      
      45f,      
      
      0.22f,    
      0.4f,     
      0.84f,    
      0.45f,    
      }; 
  
  static int[] getShades(int rgb, boolean greyScale) {
    int[] shades = new int[shadeMax];
    if (rgb == 0)
      return shades;
    
    int red = (rgb >> 16) & 0xFF;
    int grn = (rgb >>  8) & 0xFF;
    int blu = rgb         & 0xFF;
    float ambientFraction = lighting[AMBIENT_FRACTION];
    float ambientRange = 1 - ambientFraction;
    float intenseFraction = lighting[INTENSE_FRACTION];
    
    shades[shadeNormal] = rgb(red, grn, blu);
    for (int i = 0; i < shadeNormal; ++i) {
      float fraction = ambientFraction + ambientRange*i/shadeNormal;
      shades[i] = rgb((int)(red*fraction + 0.5f),
                      (int)(grn*fraction + 0.5f),
                      (int)(blu*fraction + 0.5f));
    }

    int nSteps = shadeMax - shadeNormal - 1;
    float redRange = (255 - red) * intenseFraction;
    float grnRange = (255 - grn) * intenseFraction;
    float bluRange = (255 - blu) * intenseFraction;

    for (int i = 1; i <= nSteps; ++i) {
      shades[shadeNormal + i] = rgb(red + (int)(redRange * i / nSteps + 0.5f),
                                    grn + (int)(grnRange * i / nSteps + 0.5f),
                                    blu + (int)(bluRange * i / nSteps + 0.5f));
    }
    if (greyScale)
      for (int i = shadeMax; --i >= 0; )
        shades[i] = Graphics3D.calcGreyscaleRgbFromRgb(shades[i]);
    return shades;
  }

  private final static int rgb(int red, int grn, int blu) {
    return 0xFF000000 | (red << 16) | (grn << 8) | blu;
  }

  

  final static byte intensitySpecularSurfaceLimit = (byte)(shadeNormal + 4);

  static byte calcIntensity(float x, float y, float z) {
    
    
    double magnitude = Math.sqrt(x*x + y*y + z*z);
    return (byte)(calcFloatIntensityNormalized((float)(x/magnitude),
                                               (float)(y/magnitude),
                                               (float)(z/magnitude))
                  * shadeLast + 0.5f);
  }

  static byte calcIntensityNormalized(float x, float y, float z) {
    
    return (byte)(calcFloatIntensityNormalized(x, y, z)
                  * shadeLast + 0.5f);
  }

  static int calcFp8Intensity(float x, float y, float z) {
    
    
    double magnitude = Math.sqrt(x*x + y*y + z*z);
    return (int)(calcFloatIntensityNormalized((float)(x/magnitude),
                                              (float)(y/magnitude),
                                              (float)(z/magnitude))
                 * shadeLast * (1 << 8));
  }

  

  private static float calcFloatIntensityNormalized(float x, float y, float z) {
    float cosTheta = x * xLight + y * yLight + z * zLight;
    if (cosTheta <= 0)
      return 0;
    float intensity = cosTheta * lighting[INTENSITY_DIFFUSE]; 
    if (lighting[SPECULAR_ON] != 0) {
      
      
      float dotProduct = z * 2 * cosTheta - zLight;
      if (dotProduct > 0) {
        for (int n = (int) lighting[SPECULAR_EXPONENT]; --n >= 0
            && dotProduct > .0001f;)
          dotProduct *= dotProduct;
        
        intensity += dotProduct * lighting[INTENSITY_SPECULAR];
      }
    }
    if (intensity > 1)
      return 1;
    return intensity;
  }

  

  static byte calcDitheredNoisyIntensity(float x, float y, float z, float r) {
    
    
    int fp8Intensity = (int) (calcFloatIntensityNormalized(x / r, y / r, z / r)
        * shadeLast * (1 << 8));
    int intensity = fp8Intensity >> 8;
    
    
    
    if ((fp8Intensity & 0xFF) > nextRandom8Bit())
      ++intensity;
    int random16bit = seed & 0xFFFF;
    if (random16bit < 65536 / 3 && intensity > 0)
      --intensity;
    else if (random16bit > 65536 * 2 / 3 && intensity < shadeLast)
      ++intensity;
    return (byte) intensity;
  }

  

  
  
  
  

  static boolean sphereShadingCalculated = false;
  final static byte[] sphereIntensities = new byte[256 * 256];

  synchronized static void calcSphereShading() {
    
    float xF = -127.5f;
    for (int i = 0; i < 256; ++xF, ++i) {
      float yF = -127.5f;
      for (int j = 0; j < 256; ++yF, ++j) {
        byte intensity = 0;
        float z2 = 130 * 130 - xF * xF - yF * yF;
        if (z2 > 0) {
          float z = (float) Math.sqrt(z2);
          intensity = calcDitheredNoisyIntensity(xF, yF, z, 130);
        }
        sphereIntensities[(j << 8) + i] = intensity;
      }
    }
    sphereShadingCalculated = true;
  }
  
  
  
 
  
  
  
  private static int seed = 0x12345679; 
  
  static int nextRandom8Bit() {
    int t = seed;
    seed = t = ((t << 16) + (t << 1) + t) & 0x7FFFFFFF;
    return t >> 23;
  }

  
  
}
