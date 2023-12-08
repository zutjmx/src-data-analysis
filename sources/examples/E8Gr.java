package examples;

import datan.*;
import datangraphics.*;

import java.util.*;
import java.awt.*;
   
/**
* Example for the use of classes GraphicsWithDataPointsAndPolyline and GraphicsWithDataPointsAndMultiplePolylines
*/
public class E8Gr {
      double[][] xpl;
      double[][] ypl;
      int ncurves, npl, ndat;
      double[] datx;
      double[] daty;
      double[] datsx;
      double[] datsy;
      double[] datrho;
      double[] r;
      double s;
      int[] colorindex;

   public E8Gr(){
      System.out.println("Example for the use of classes GraphicsWithDataPointsAndPolyline");
      System.out.println("and GraphicsWithDataPointsAndMultiplePolylines");

      npl = 500;
      s = 0.5;
      DatanGraphics.setStandardPSColors();

      String caption;
      for(int iplot = 1; iplot <= 3; iplot++){
         if(iplot == 1){
            ndat = 21;
            ncurves = 0;
            generateDataPoints(ndat);
            generateCurves(ncurves);
            caption = "Data Points";
         }
         else if(iplot == 2){
            ndat = 0;
            ncurves = 3;
            generateDataPoints(ndat);
            generateCurves(ncurves);
            caption = "Gaussians of different widths";
         }
         else{
            ndat = 21;
            ncurves = 3;
            generateDataPoints(ndat);
            generateCurves(ncurves);
            caption = "Data Points and Gaussians of different widths";
         }
         String frameTitle = getClass().getName() + " - plot " + iplot;
         String fileName = getClass().getName() + "_" + iplot + ".ps";
         new GraphicsWithDataPointsAndMultiplePolylines(frameTitle, fileName,
            xpl, ypl, colorindex, 5, s, datx, daty, datsx, datsy, datrho, "x", "f(x)", caption);      
      }
      }

      private void generateDataPoints(int ndat){
         datx = new double[ndat];
         daty = new double[ndat];
         datsx = new double[ndat];
         datsy = new double[ndat];
         datrho = new double[ndat];
         if(ndat > 0){
            r = DatanRandom.standardNormal(ndat);
            for(int i = 0; i < ndat; i++){
               datx[i] = -3. + (double)i * 0.3;
               daty[i] = StatFunct.standardNormal(datx[i]) + 0.05 * r[i];
               datsx[i] = 0.;
               datsy[i] = 0.05;
               datrho[i] = 0.;
            }
         }
      }

      
      private void generateCurves(int ncurves){
         if(ncurves == 0){
            xpl = new double[0][0];
            ypl = new double[0][0];
            colorindex = new int[0];
         }
         else{
            xpl = new double[ncurves][npl];
            ypl = new double[ncurves][npl];
            colorindex = new int[ncurves];
         }
         if(ncurves > 0){
            for(int i = 0; i < ncurves; i++){
               double x0 = -10.;
               double delx = 2. * Math.abs(x0) / (double)(npl - 1);
               double sigma = (double)(i + 1) * 0.5;
               colorindex[i] = 2 + i;
               if(i != 1) colorindex[i] = - colorindex[i];
               for(int j = 0; j < npl; j++){
                  xpl[i][j] = x0 + (double)j * delx;
                  ypl[i][j] = StatFunct.normal(xpl[i][j], 0., sigma);

               }
            }
         }
      }

   public static void main(String s[]) {
      new E8Gr();
   }

}
