package examples;

import datan.*;
import datangraphics.*;

import java.util.*;

import java.awt.*;
/**
* Example demonstrating use of method DatanGraphics.drawPoint
*/
public class E3Gr {

   public E3Gr(){

      System.out.println("Example demonstrating the use of method DatanGraphics.drawDataPoint(...)");

      
      DatanGraphics.openWorkstation(getClass().getName(), "E3Gr.ps");
      DatanGraphics.setFormat(0., 0.);
      DatanGraphics.setWindowInComputingCoordinates(-1.2, 1., -1.2, 1.);
      DatanGraphics.setWindowInWorldCoordinates(0., 1.414, 0., 1.);
      DatanGraphics.setViewportInWorldCoordinates(0., 1.4, 0., 1.);
      DatanGraphics.setBigClippingWindow();
      DatanGraphics.chooseColor(2);
      DatanGraphics.drawFrame();
      DatanGraphics.chooseColor(1);

      
// draw different data points
      double s = 1.;
      DatanGraphics.drawDatapoint(1, s, -.8, .5, .3, 0., 0.);
      DatanGraphics.drawDatapoint(2, s, 0., .5, 0., 0.2, 0.);
      DatanGraphics.drawDatapoint(3, s, .7, .5, .2, 0.3, 0.);
      DatanGraphics.drawDatapoint(4, s, -.6, -.5, .4, .4, -.4);
      DatanGraphics.drawDatapoint(5, s, .5, -.5, .45, 0.3, 0.6);
      DatanGraphics.closeWorkstation();
   }


   public static void main(String s[]) {
      new E3Gr();
   }

}
