package datangraphics;


import datan.*;
import java.lang.Math;
import java.text.*;


/**
* A class producing a graphics with histogram and polyline.
* @author  Siegmund Brandt.
*/
public class GraphicsWithHistogramAndPolyline {
   private double[] xpl, ypl;
   private Histogram hist;
   private String textX, textY, caption, filename, frametitle;
   private double x0, x1, hmax;
/**
 Produces a plot with a histogram and a polyline.
* @param frametitle title of DatanGraphicsFrame; if frameTitle is blank the no DatanGraphicsFrame is initialized.
* @param filename name of Postscript file - has to end with either ".ps" (for postscript) or ".eps" (for encapsulated postscript).
* If filename is blank, no file is initialized.
* @param xpl x coordinates of polyline
* @param ypl y coordinates of polyline
* @param hist histogram
* @param textX text for x axis
* @param textY text for y axis
* @param caption text for caption of plot
*/
   public GraphicsWithHistogramAndPolyline(String frametitle, String filename, double[] xpl, double[] ypl, Histogram hist, String textX, String textY,
                                                String caption){
      
      this.xpl = xpl;
      this.ypl = ypl;
      this.hist = hist;
      this.textX = textX;
      this.textY = textY;
      this.caption = caption;
      this.filename = filename;
      this.frametitle = frametitle;
      
      produceGraphics();
   }

   private void produceGraphics(){
      DatanGraphics.openWorkstation(frametitle, filename);
      hmax = 0.;
      for(int i = 0; i < hist.getNumberOfBins(); i++){
         hmax = Math.max(hmax, hist.getContentsAt(i));
      }
      x0 = hist.getLowerBoundary();
      x1 = x0 + hist.getNumberOfBins() * hist.getBinSize();
      hmax = DatanGraphics.roundUp(hmax);
      DatanGraphics.setWindowInComputingCoordinates(x0, x1, 0., hmax); //!!!!!!!!!!!
      DatanGraphics.setWindowInWorldCoordinates(-0.214, 1.2, -0.1, 0.9);
      DatanGraphics.setViewportInWorldCoordinates(0.1, 1.1, 0.1, 0.75);
      DatanGraphics.setFormat(0., 0.);
      DatanGraphics.setBigClippingWindow();
      DatanGraphics.chooseColor(2);
      DatanGraphics.drawFrame();
      DatanGraphics.drawScaleX(textX);
      DatanGraphics.drawScaleY(textY);
      DatanGraphics.drawCaption(0., caption);
      DatanGraphics.drawBoundary();
      DatanGraphics.setSmallClippingWindow();
      DatanGraphics.chooseColor(1);
      DatanGraphics.drawHistogram(hist);
      DatanGraphics.setSmallClippingWindow();
      DatanGraphics.chooseColor(5);
      DatanGraphics.drawPolyline(xpl, ypl);
      DatanGraphics.closeWorkstation();
   }

}
