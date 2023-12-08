package examples;

import datan.*;
import datangraphics.*;

import java.util.*;

import java.awt.*;
   
/**
* Example demonstrating the use of methods DatanGrphics.drawCoordinateCross and DatanGrphics.setParametersForScale
*/
public class E5Gr {

   public E5Gr(){
      double xa, xb, ya, yb;
      double root2 = Math.sqrt(2.);
      double[] tx = new double[201];
      double[] ty = new double[201];

      System.out.println("Example demonstrating the use of methods");
      System.out.println("drawCoordinateCross() and setParametersForScale(...)");

      
      DatanGraphics.openWorkstation(getClass().getName(), "E5Gr.eps");
      DatanGraphics.setFormat(0., 0.);
      DatanGraphics.setWindowInComputingCoordinates(-10., 10., -0.1, .5);
      DatanGraphics.setWindowInWorldCoordinates(0., 1.414, 0., 1.);
      DatanGraphics.setBigClippingWindow();
      DatanGraphics.chooseColor(2);
      DatanGraphics.drawFrame();
// loop over 4 plots
      for(int iplot = 1; iplot <=4; iplot++){
// set view port in world coordinates for different plots
         xa = .25;
         if(iplot == 2 || iplot == 4) xa = 0.25 + .5 * root2;
         xb = xa + .4;
         ya = .65;
         if(iplot == 3 || iplot == 4) ya = 0.15;
         yb = ya + .3;
         DatanGraphics.setViewportInWorldCoordinates(xa, xb, ya, yb);
         DatanGraphics.chooseColor(2);
         DatanGraphics.drawBoundary();
         DatanGraphics.drawCoordinateCross();
         if(iplot == 1){
// plot 1 (upper left): take defaults for designing scales
            DatanGraphics.drawScaleX("x");
            DatanGraphics.drawScaleY("f(x)");
         }
         else if(iplot == 2){
// plot 2 (upper right):
// change design of scales
            DatanGraphics.setParametersForScale(0., 5., 0, 1., 1., true);
            DatanGraphics.drawScaleX("x");
            DatanGraphics.setParametersForScale(0., .2, 2, 1., 1., true);
            DatanGraphics.drawScaleY("f(x)");
         }
         else if(iplot == 3){
// plot 3 (lower left):
// change design of scales
            DatanGraphics.setParametersForScale(0., 5., 0, .8, .8, true);
            DatanGraphics.drawScaleX("x");
            DatanGraphics.setParametersForScale(0., .2, 2, .8, .8, true);
            DatanGraphics.drawScaleY("f(x)");
         }
         else if(iplot == 4){
// plot 4 (lowerr right):
// change design of scales
            DatanGraphics.setParametersForScale(0., 5., 5, .8, .8, false);
            DatanGraphics.drawScaleX("x");
            DatanGraphics.setParametersForScale(0., .2, 1, .8, .8, false);
            DatanGraphics.drawScaleY("f(x)");
         }
// prepare and draw polyline
         for(int i = 0; i < 201; i++){
            tx[i] = -10. + (double)i * 0.1;
            ty[i] = StatFunct.standardNormal(tx[i]);
         }
         DatanGraphics.chooseColor(1);
         DatanGraphics.drawPolyline(tx, ty);         
      }
      DatanGraphics.closeWorkstation();
   }


   public static void main(String s[]) {
      new E5Gr();
   }

}
