package examples;

import datan.*;
import datangraphics.*;

import java.util.*;

import java.awt.*;

/**
* Example for the use of method DatanGraphics.drawContour
*/
public class E4Gr {
   int nstep = 200;
   int ncont = 19;
   int nx, ny;
   double d, delcnt, dx, dy, fcont, x0, y0;

   public E4Gr(){

      System.out.println("Example for the use of method DatanGraphics.drawContour(...)");
      
      DatanGraphics.openWorkstation(getClass().getName(), "E4Gr.ps");
      DatanGraphics.setFormat(0., 0.);
      DatanGraphics.setWindowInComputingCoordinates(-3.1416, 3.1416, -3.1416, 3.1416);
      DatanGraphics.setWindowInWorldCoordinates(-0.6, 1.4, -.25, 1.2);
      DatanGraphics.setViewportInWorldCoordinates(0., 1., 0., 1.);
      DatanGraphics.setBigClippingWindow();
      DatanGraphics.chooseColor(5);
      DatanGraphics.drawCaption(0., "sin(x+y)cos((x-y)/2)");
      DatanGraphics.chooseColor(2);
      DatanGraphics.drawFrame();
      DatanGraphics.setBigClippingWindow();
      DatanGraphics.drawScaleX("x");
      DatanGraphics.drawScaleY("y");
      DatanGraphics.drawBoundary();
      DatanGraphics.setSmallClippingWindow();



// define pixels
      d = 2. * 3.2 / (double)nstep;
      x0 = - 3.2;
      y0 = - 3.2;
      dx = d;
      dy = d;
      nx = nstep;
      ny = nstep;
      DatanGraphics.chooseColor(1);
// plot contout lines
      delcnt = 2. / (double)(ncont + 1);
      DemoContourUserFunction dcuf = new DemoContourUserFunction();
      for(int i = 1; i <= ncont; i++){
         fcont = 1. - (double)i * delcnt;
         DatanGraphics.drawContour(x0, y0, dx, dy, nx, ny, fcont, dcuf);         
      }



      DatanGraphics.closeWorkstation();
   }

   private class DemoContourUserFunction extends DatanUserFunction {
      public double getValue(double x, double y){
         return Math.sin(x + y) * Math.cos(0.5 * (x - y));
      }
   }


   public static void main(String s[]) {
      new E4Gr();
   }

}
