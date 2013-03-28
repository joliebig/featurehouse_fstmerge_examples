

package org.jmol.g3d;

final class Rgb16 {
  int rScaled;
  int gScaled;
  int bScaled;
    
  Rgb16() {
  }

  Rgb16(int argb) {
    set(argb);
  }

  void set(int argb) {
    rScaled = ((argb >> 8) & 0xFF00) | 0x80;
    gScaled = ((argb     ) & 0xFF00) | 0x80;
    bScaled = ((argb << 8) & 0xFF00) | 0x80;
  }

  void set(Rgb16 other) {
    rScaled = other.rScaled;
    gScaled = other.gScaled;
    bScaled = other.bScaled;
  }

  void diffDiv(Rgb16 rgb16A, Rgb16 rgb16B, int divisor) {
    rScaled = (rgb16A.rScaled - rgb16B.rScaled) / divisor;
    gScaled = (rgb16A.gScaled - rgb16B.gScaled) / divisor;
    bScaled = (rgb16A.bScaled - rgb16B.bScaled) / divisor;
  }

  
  
  
  
  void setAndIncrement(Rgb16 base, Rgb16 other) {
    rScaled = base.rScaled;
    base.rScaled += other.rScaled;
    gScaled = base.gScaled;
    base.gScaled += other.gScaled;
    bScaled = base.bScaled;
    base.bScaled += other.bScaled;
  }

  int getArgb() {
    return (                 0xFF000000 |
           ((rScaled << 8) & 0x00FF0000)|
           (gScaled        & 0x0000FF00)|
           (bScaled >> 8));
  }

  public String toString() {
    return (new StringBuffer("Rgb16(")).append(rScaled).append(',')
    .append(gScaled).append(',')
    .append(bScaled).append(" -> ")
    .append((rScaled >> 8) & 0xFF).append(',')
    .append((gScaled >> 8) & 0xFF).append(',')
    .append((bScaled >> 8) & 0xFF).append(')').toString();
  }
}

