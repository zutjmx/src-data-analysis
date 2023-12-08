package examples;

import datan.*;
import datangraphics.*;

import java.util.*;

import java.awt.*;
   
/**
* Example demonstrating the use of method DatanGraphics.drawMark
*/
public class E2Gr {

   public E2Gr(){

      double s, x, xx, y, yy;

      System.out.println("Example demonstrating the use of method DatanGraphics.drawMark(...)");

      
      DatanGraphics.openWorkstation(getClass().getName(), "E2Gr.eps");
      DatanGraphics.setFormat(13., 4.);
      DatanGraphics.setWindowInComputingCoordinates(-5., 5., 0., .4);
      DatanGraphics.setWindowInWorldCoordinates(0., 1.3, 0., 0.4);
      DatanGraphics.setViewportInWorldCoordinates(0., 1.3, 0., 0.4);
      DatanGraphics.setBigClippingWindow();
      DatanGraphics.chooseColor(2);
      DatanGraphics.drawFrame();
      DatanGraphics.chooseColor(1);

      s = 1.;
      x = -5.;

// loop over different types of mark
      for(int n = 1; n <= 9; n++){
         x += 1.;
         if(n == 1 || n == 3 || n == 5 || n == 7 || n == 9){
            y = .25;
            yy = .3;
         }
         else{
            y = .15;
            yy = .1;
         }
// draw mark
         DatanGraphics.drawMark(n, s, x, y);
// place text near mark
         DatanGraphics.drawText(x, yy, s, new Integer(n).toString());
      }
      DatanGraphics.closeWorkstation();
   }


   public static void main(String s[]) {
      new E2Gr();
   }

}
