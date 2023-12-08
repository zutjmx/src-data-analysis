package examples;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events

/**
* Example demonstrating the fit of a linear function to data
*/
public class E2Lsq {
   int n;
   DatanFrame df;
   DatanVector c, t, x, y, deltay;
   DatanMatrix a, cx;

   public E2Lsq(){
      String s = "Example demonstrating the fit of a linear function to data";
      df = new DatanFrame(getClass().getName(), s);
// set up data
      double[] tt ={-.9, -.7, -.5, -.3, -.1, .1, .3, .5, .7, .9};
      double[] yy = {-1.6 , -1.5 , -.8 , -.9 , -.4 , 0. , .5 ,1. ,1.2 , 1.4};
      double[] deltayy = {.2 , .3 , .1 , .2 , .1 , .2 , .2 , .3 , .2 , .15};
      t = new DatanVector(tt);
      y = new DatanVector(yy);
      deltay = new DatanVector(deltayy);
      n = t.getNumberOfElements();
// write out data
      df.writeLine("         t" + "           y"   + "      deltay");         
      for(int i = 0; i < n; i++){
         df.writeLine(String.format(Locale.US, "%10.5f", t.getElement(i))
         + "  " + String.format(Locale.US, "%10.5f", y.getElement(i)) +
         "  " + String.format(Locale.US, "%10.5f", deltay.getElement(i)));
      } 
// set up vector c and matrix a
      c = y.multiply(-1.);
      a = new DatanMatrix(t);
      a = a.multiply(-1.);
// perform fit to proportionality
      LsqLin ll = new LsqLin(t, c, deltay, a);
      df.writeLine("\n Fit to proportionality. Minimum function M = " + String.format(Locale.US, "%10.5f", ll.getChiSquare()));
      x = ll.getResult();
      df.writeLine("Solution x = " + x.toString());
      cx = ll.getCovarianceMatrix();
      df.writeLine("Variance cx = " + cx.toString());
   }

   public static void main(String s[]) {
      new E2Lsq();
   }

}
