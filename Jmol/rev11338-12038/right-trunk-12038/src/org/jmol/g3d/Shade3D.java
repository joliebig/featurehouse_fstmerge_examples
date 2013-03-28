

package org.jmol.g3d;



final class Shade3D {

  
  
  
  
  
  static final int shadeIndexMax = 64;
  static final int shadeIndexLast = shadeIndexMax - 1;
  static final byte shadeIndexNormal = 52;
  final static byte shadeIndexNoisyLimit = 56;


  

  
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
  
  static boolean specularOn = true; 
  static boolean usePhongExponent = false;
  
  
  static int ambientPercent = 45;
  
  
  static int diffusePercent = 84;

  
  
  static int specularExponent = 6;

  
  
  static int specularPercent = 22;
  
  
  static int specularPower = 40;

  
  static int phongExponent = 64;
  
  static float ambientFraction = ambientPercent / 100f;
  static float diffuseFactor = diffusePercent / 100f;
  static float intenseFraction = specularPower / 100f;
  static float specularFactor = specularPercent / 100f;
  
  
  
  static int[] getShades(int rgb, boolean greyScale) {
    int[] shades = new int[shadeIndexMax];
    if (rgb == 0)
      return shades;
    
    float red = ((rgb >> 16) & 0xFF);
    float grn = ((rgb >>  8) & 0xFF);
    float blu = (rgb         & 0xFF);

    float f = (1 - ambientFraction) / shadeIndexNormal;

    float redStep = red * f;
    float grnStep = grn * f;
    float bluStep = blu * f;

    red = red * ambientFraction + 0.5f;
    grn = grn * ambientFraction + 0.5f;
    blu = blu * ambientFraction + 0.5f;
        
    int i;
    for (i = 0; i < shadeIndexNormal; ++i) {
      shades[i] = rgb((int) red, (int) grn, (int) blu);
      red += redStep;
      grn += grnStep;
      blu += bluStep;
    }

    shades[i++] = rgb;    

    f = intenseFraction / (shadeIndexMax - i);
    redStep = (255.5f - red) * f;
    grnStep = (255.5f - grn) * f;
    bluStep = (255.5f - blu) * f;

    for (; i < shadeIndexMax;) {
      red += redStep;
      grn += grnStep;
      blu += bluStep;
      shades[i++] = rgb((int) red, (int) grn, (int) blu);
    }
    
    if (greyScale)
      for (; --i >= 0;)
        shades[i] = Graphics3D.calcGreyscaleRgbFromRgb(shades[i]);
    return shades;
  }

  final static int rgb(int red, int grn, int blu) {
    return 0xFF000000 | (red << 16) | (grn << 8) | blu;
  }

  static int getShadeIndex(float x, float y, float z) {
    
    
    double magnitude = Math.sqrt(x*x + y*y + z*z);
    return (int) (getFloatShadeIndexNormalized((float)(x/magnitude),
                                               (float)(y/magnitude),
                                               (float)(z/magnitude))
                  * shadeIndexLast + 0.5f);
  }

  static int getShadeIndexNormalized(float x, float y, float z) {
    
    return (int)(getFloatShadeIndexNormalized(x, y, z)
                  * shadeIndexLast + 0.5f);
  }

  static int getFp8ShadeIndex(float x, float y, float z) {
    
    
    double magnitude = Math.sqrt(x*x + y*y + z*z);
    return (int)(getFloatShadeIndexNormalized((float)(x/magnitude),
                                              (float)(y/magnitude),
                                              (float)(z/magnitude))
                 * shadeIndexLast * (1 << 8));
  }

  private static float getFloatShadeIndexNormalized(float x, float y, float z) {
    float NdotL = x * xLight + y * yLight + z * zLight;
    if (NdotL <= 0)
      return 0;
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    float intensity = NdotL * diffuseFactor;
    if (specularOn) {
      float k_specular = 2 * NdotL * z - zLight;
      if (k_specular > 0) {
        if (usePhongExponent) {
          k_specular = (float) Math.pow(k_specular, phongExponent);
        } else {
          for (int n = specularExponent; --n >= 0
              && k_specular > .0001f;)
            k_specular *= k_specular;
        }
        intensity += k_specular * specularFactor;
      }
    }
    if (intensity > 1)
      return 1;
    return intensity;
  }

  

  static byte getDitheredNoisyShadeIndex(float x, float y, float z, float r) {
    
    
    int fp8ShadeIndex = (int) (getFloatShadeIndexNormalized(x / r, y / r, z / r)
        * shadeIndexLast * (1 << 8));
    int shadeIndex = fp8ShadeIndex >> 8;
    
    
    
    if ((fp8ShadeIndex & 0xFF) > nextRandom8Bit())
      ++shadeIndex;
    int random16bit = seed & 0xFFFF;
    if (random16bit < 65536 / 3 && shadeIndex > 0)
      --shadeIndex;
    else if (random16bit > 65536 * 2 / 3 && shadeIndex < shadeIndexLast)
      ++shadeIndex;
    return (byte) shadeIndex;
  }

  

  
  
  
  

  static boolean sphereShadingCalculated = false;
  final static byte[] sphereShadeIndexes = new byte[256 * 256];

  synchronized static void calcSphereShading() {
    
    float xF = -127.5f;
    for (int i = 0; i < 256; ++xF, ++i) {
      float yF = -127.5f;
      for (int j = 0; j < 256; ++yF, ++j) {
        byte shadeIndex = 0;
        float z2 = 130 * 130 - xF * xF - yF * yF;
        if (z2 > 0) {
          float z = (float) Math.sqrt(z2);
          shadeIndex = getDitheredNoisyShadeIndex(xF, yF, z, 130);
        }
        sphereShadeIndexes[(j << 8) + i] = shadeIndex;
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
