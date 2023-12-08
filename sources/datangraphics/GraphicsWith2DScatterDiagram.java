package datangraphics;


import datan.*;
import java.lang.Math;
import java.text.*;


/**
* A class producing a graphics with a two-dimensional scatter diagram.
* @author  Siegmund Brandt.
*/
public class GraphicsWith2DScatterDiagram{
   private String textX, textY, caption, filename, frametitle;
   private double[] x, y;
   private double xa, xb, ya, yb;

/**
* Produces a two-dimensional scatter diagram.
* @param frametitle title of DatanGraphicsFrame; if frameTitle is blank the no DatanGraphicsFrame is initialized.
* @param filename name of Postscript file - has to end with either ".ps" (for postscript) or ".eps" (for encapsulated postscript).
* If filename is blank, no file is initialized.
* @param x x coordinates of points to be plotted
* @param y y coordinates of points to be plotted
* @param xa lower limit in x of computing coordinates
* @param xb upper limit in x of computing coordinates
* @param ya lower limit in y of computing coordinates
* @param yb upper limit in y of computing coordinates
* @param textX text for x axis
* @param textY text for y axis
* @param caption text for caption of plot
*/
   public GraphicsWith2DScatterDiagram(String frametitle, String filename, double[] x, double[] y,
                            double xa, double xb, double ya, double yb, String textX, String textY, String caption){
      this.x = x;
      this.y = y;
      this.xa = xa;
      this.xb = xb;
      this.ya = ya;
      this.yb = yb;
      this.textX = textX;
      this.textY = textY;
      this.caption = caption;
      this.filename = filename;
      this.frametitle = frametitle;
      
      produceGraphics();
   }

   private void produceGraphics(){
      DatanGraphics.openWorkstation(frametitle, filename);
      DatanGraphics.setWindowInComputingCoordinates(xa, xb, ya, yb);
      DatanGraphics.setViewportInWorldCoordinates(.2, .9, .16, .86);
      DatanGraphics.setWindowInWorldCoordinates(-.414, 1., 0., 1.);
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
      int n = x.length;
      for(int i = 0; i < n; i++){
         DatanGraphics.drawMark(1, .1, x[i], y[i]);
      }
      DatanGraphics.closeWorkstation();
   }

}
