package examples;

import datan.*;
import datangraphics.*;

import java.util.*;
   
/**
* Example for the use of class GraphicsWithHistogramAndPolyline
*/
public class E6Gr {
   Histogram hist;
   double x0, dx, delx, lambda, sigma;
   int nx, npl;
   String caption, tx, ty;

   public E6Gr(){
      System.out.println("Example for the use of class GraphicsWithHistogramAndPolyline");

// prepare histogram
      x0 = -.5;
      delx = 1.;
      nx = 30;
      lambda = 10.;
      hist = new Histogram(x0, delx, nx);
// fill histogram with Poisson probabilities
      for(int i = 0; i < nx; i++){
         hist.enter((double)i, StatFunct.poisson(i, lambda));
//         System.out.println("i = " + i + ", p = " + StatFunct.poisson(i, lambda) + ", contents = " + hist.getContentsAt(i));
      }


// prepare polyline
      npl = 1000;
      double[] xpl = new double[npl];
      double[] ypl = new double[npl];
      dx = (double)nx * delx / ((double)npl - 1.);
      sigma = Math.sqrt(lambda);
      for(int i = 0; i < npl; i++){
         xpl[i] = x0 + (double)i * dx;
         ypl[i] = StatFunct.normal(xpl[i], lambda, sigma);
      }


// prepare texts
      caption = "Poisson (histogram) and Gaussian (cont. line)";
      tx = "k";
      ty = "P(k)";
      GraphicsWithHistogramAndPolyline ghp = new GraphicsWithHistogramAndPolyline(getClass().getName(), "E6Gr.ps", xpl, ypl, hist, tx, ty, caption);
   }


   public static void main(String s[]) {
      new E6Gr();
   }

}
