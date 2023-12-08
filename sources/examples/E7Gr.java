package examples;

import datan.*;
import datangraphics.*;

import java.util.*;
   
/**
* Example for the use of class GraphicsWithDataPointsAndPolyline
*/
public class E7Gr {
   int npl = 2;
   double[] xpl = new double[npl];
   double[] ypl = new double[npl];
   int ndat = 21;
   double a, b, s;
   double[] datx = new double[ndat];
   double[] daty = new double[ndat];
   double[] datsx = new double[ndat];
   double[] datsy = new double[ndat];
   double[] datrho = new double[ndat];

   public E7Gr(){
      System.out.println("Example for the use of class GraphicsWithDataPointsAndPolyline");
      
// generate data points
      a = 1.;
      b = 1.;
      DatanRandom.line(a, b, -1., .1, .1, datx, daty);
      for(int i = 0; i < ndat; i++){
         datsx[i] = 0.;
         datsy[i] = .1;
         datrho[i] = 0.;
      }
// compute points which define polyline
      xpl[0] = -2.;
      ypl[0] = a * xpl[0] + b;
      xpl[1] = 2.;
      ypl[1] = a * xpl[1] + b;
// produce graphics
      s = .5;
      GraphicsWithDataPointsAndPolyline gdp = new GraphicsWithDataPointsAndPolyline(getClass().getName(),
      getClass().getName() + ".ps",
      xpl, ypl, 5, s, datx, daty, datsx, datsy, datrho, "t", "y", "y = at + b");
   }


   public static void main(String s[]) {
      new E7Gr();
   }

}
