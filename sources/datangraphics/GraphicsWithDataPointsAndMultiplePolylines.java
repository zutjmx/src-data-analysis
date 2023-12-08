package datangraphics;


import datan.*;
import java.lang.Math;
import java.text.*;


/**
* A class producing a plot with data points and several polylines.
* @author  Siegmund Brandt.
*/
public class GraphicsWithDataPointsAndMultiplePolylines {

   private int nmark, nws;
   int[] colorindex;
   private double[][] xpl, ypl;
   double[] datx, daty, datsx, datsy, datrho;
   private String textX, textY, caption, filename, frametitle;
   private double  scalef, xa, xb, ya, yb, xd, yd;
/**
 Produces a plot with data points and severalpolylines.
* @param frametitle title of DatanGraphicsFrame; if frameTitle is blank the no DatanGraphicsFrame is initialized.
* @param filename name of Postscript file - has to end with either ".ps" (for postscript) or ".eps" (for encapsulated postscript).
* If filename is blank, no file is initialized.
* @param xpl x coordinates of polyline (first index: number of polyline, second index: number of point along polyline)
* @param ypl y coordinates of polyline (first index: number of polyline, second index: number of point along polyline)
* @param colorindex absolute value determines the colorindeces used for the various polylines; if negative, polyline is drawn as broken line 
* @param nmark form of polymarker symbolizing the data point (1: open circle, 2: closed circle, 3: open sqare, 4: closed sqare, 5: open diamond, 6: closed diamond, 7: +, 8: x. 9: *)
* @param scalef scale factor; the diameter of the polymarker is 1% of the picture diagonal multiplied by this factor
* @param datx x coordinate of data points
* @param daty y coordinate of data points
* @param datsx errors in x; error bar is drawn only if deltax > 0 and if its size would exceed the radius if the polymarker
* @param datsy errors in y; error bar is drawn only if deltax > 0 and if its size would exceed the radius if the polymarker
* @param datrho correlations between errors; covariance ellipse is not drawn if rho = 0 (drawing can be forced by giving nmark a negative sign)
* @param textX text for x axis
* @param textY text for y axis
* @param caption text for caption of plot
*/
   public GraphicsWithDataPointsAndMultiplePolylines(String frametitle, String filename,
         double[][] xpl, double[][] ypl, int[] colorindex,
         int nmark, double scalef,
         double[] datx,  double[] daty, double[] datsx, double[] datsy, double[] datrho,
         String textX, String textY, String caption){
      
      this.xpl = xpl;
      this.ypl = ypl;
      this.colorindex = colorindex;
      this.nmark = nmark;
      this.scalef = scalef;
      this.datx = datx;
      this.daty = daty;
      this.datsx = datsx;
      this.datsy = datsy;
      this.datrho = datrho;
      this.textX = textX;
      this.textY = textY;
      this.caption = caption;
      this.filename = filename;
      this.frametitle = frametitle;
      
      produceGraphics();

   }

   private void produceGraphics(){
      DatanGraphics.openWorkstation(frametitle, filename);
// set scales
      xa = 0.;
      xb = 1.;
      ya = 0.;
      yb = 1.;
      if(datx.length > 0){
         xa = datx[0] - datsx[0];
         xb = datx[0] + datsx[0];
         ya = daty[0] - datsy[0];
         yb = daty[0] + datsy[0];
         for(int i = 0; i < datx.length; i++){
            xa = Math.min(xa, datx[i] - datsx[i]);
            xb = Math.max(xb, datx[i] + datsx[i]);
            ya = Math.min(ya, daty[i] - datsy[i]);
            yb = Math.max(yb, daty[i] + datsy[i]);
         }
      }
      else if(xpl.length > 0 && xpl[0].length >0){
         xa = xpl[0][0];
         xb = xpl[0][0];
         ya = ypl[0][0];
         yb = xpl[0][0];
         for(int i = 0; i < xpl.length; i++){
               for(int j = 0; j < xpl[0].length; j++){
                  xa = Math.min(xa, xpl[i][j]);
                  xb = Math.max(xb, xpl[i][j]);
                  ya = Math.min(ya, ypl[i][j]);
                  yb = Math.max(yb, ypl[i][j]);                  
               }
         }
      }
      xd = xb - xa;
      yd = yb - ya;
      xa =  DatanGraphics.roundDown(xa - 0.1 * xd);
      xb =  DatanGraphics.roundUp(xb + 0.1 * xd);
      ya =  DatanGraphics.roundDown(ya - 0.1 * yd);
      yb =  DatanGraphics.roundUp(yb + 0.1 * yd);
// draw background
      DatanGraphics.chooseColor(2);
      DatanGraphics.setWindowInComputingCoordinates(xa, xb, ya, yb);
      DatanGraphics.setWindowInWorldCoordinates(-0.214, 1.2, -0.1, 0.9);
      DatanGraphics.setViewportInWorldCoordinates(0.1, 1.1, 0.1, 0.75);
      DatanGraphics.setFormat(0., 0.);
      DatanGraphics.setBigClippingWindow();
      DatanGraphics.drawFrame();
      DatanGraphics.drawScaleX(textX);
      DatanGraphics.drawScaleY(textY);
      DatanGraphics.drawCaption(0., caption);
      DatanGraphics.drawBoundary();
      DatanGraphics.setSmallClippingWindow();
// draw data points
      DatanGraphics.chooseColor(1);
      for(int i = 0; i < datx.length; i++){
         DatanGraphics.drawDatapoint(nmark, scalef, datx[i], daty[i], datsx[i], datsy[i], datrho[i]);
      }
// draw polylines
      DatanGraphics.setSmallClippingWindow();

      for(int i = 0; i < xpl.length; i++){
         int col = Math.abs(colorindex[i]);
         DatanGraphics.chooseColor(col);
         if(colorindex[i] > 0) DatanGraphics.drawPolyline(xpl[i], ypl[i]);
         else DatanGraphics.drawBrokenPolyline(1, 1., xpl[i], ypl[i]);
      }
      DatanGraphics.closeWorkstation();
   }

}
