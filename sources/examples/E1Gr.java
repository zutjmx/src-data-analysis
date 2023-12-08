package examples;

import datan.*;
import datangraphics.*;

import java.util.*;

import java.awt.*;
   
/**
* Example demonstrating the basic methods of class DatanGrphics
*/
public class E1Gr {
   double[] tx = new double[201];
   double[] ty = new double[201];
   double[] lx = new double[2];
   double[] ly = new double[2];

   public E1Gr(){
      DatanGraphics.openWorkstation(getClass().getName(), "E1Gr.ps");
      DatanGraphics.setFormat(0., 0.);
      DatanGraphics.setWindowInComputingCoordinates(-10, 10, 0., .5);
      DatanGraphics.setWindowInWorldCoordinates(0., 1.414, 0., 1.);
      DatanGraphics.setViewportInWorldCoordinates(0.25, 1.35, 0.2, 0.8);
      DatanGraphics.chooseColor(2);
// draw frame (given by window in WC)
      DatanGraphics.setBigClippingWindow();
      DatanGraphics.drawFrame();
// draw boundary (around window in CC)
      DatanGraphics.drawBoundary();
      DatanGraphics.setBigClippingWindow();
      DatanGraphics.drawScaleX("x");
      DatanGraphics.drawScaleY("f(x)");
      DatanGraphics.chooseColor(5);
      DatanGraphics.drawCaption(0., "f(x)=(2&ps^2#@)^-1/2#exp(-(x-a)^2#/2&s^2#@)");
      DatanGraphics.setSmallClippingWindow();
      for(int i = 0; i < 201; i++){
         tx[i] = -10. + (double)i * 0.1;
         ty[i] = StatFunct.standardNormal(tx[i]);
      }
      DatanGraphics.chooseColor(1);
      DatanGraphics.drawPolyline(tx, ty);
      for(int i = 0; i < 201; i++){
         tx[i] = -10. + (double)i * 0.1;
         ty[i] = StatFunct.normal(tx[i], 2., 3.);
      }
      DatanGraphics.drawBrokenPolyline(1, 1., tx, ty);
 // compute and draw short straight continuous polyline
      lx[0] = -8.;
      lx[1] = -5.;
      ly[0] = 0.4;
      ly[1] = 0.4;
      DatanGraphics.drawPolyline(lx, ly);
// place text to the right of short polyline
      DatanGraphics.chooseColor(3);
      DatanGraphics.drawText(-4.5, 0.4, 1., "a=0, &s=1");
// compute and draw short straight broken polyline
      ly[0] = .3;
      ly[1] = .3;
      DatanGraphics.chooseColor(1);
      DatanGraphics.drawBrokenPolyline(1, 1., lx, ly);
// place text to its right
      DatanGraphics.chooseColor(4);
      DatanGraphics.drawText(-4.5, 0.3, 1., "a=2, &s=3");
      DatanGraphics.closeWorkstation();
   }


   public static void main(String s[]) {
      new E1Gr();
   }

}
