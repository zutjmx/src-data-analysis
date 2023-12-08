package examples;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events
/**
* Example demonstrating the fit of polynomials of different degrees to dat
*/
public class E1Lsq {
   int nny;
   AuxJNumberInput[] ni = new AuxJNumberInput[1];
   DatanFrame df;
   DatanVector t, x, y, deltay;
   DatanMatrix cx;

   public E1Lsq(){
      String s = "Example demonstrating the fit of polynomials of different degrees to data";
      df = new DatanFrame(getClass().getName(), s);
// set up data
      double[] tt ={-.9, -.7, -.5, -.3, -.1, .1, .3, .5, .7, .9};
      double[] yy = {81., 50., 35., 27., 26., 60. ,106., 189. ,318., 520.};
      t = new DatanVector(tt);
      y = new DatanVector(yy);
      int n = t.getNumberOfElements();
      deltay = new DatanVector(n);
      for(int i = 0; i < n; i++){
         deltay.setElement(i, Math.sqrt(y.getElement(i)));
      }
// write out data
      df.writeLine("         t" + "           y"   + "      deltay");      
      for(int i = 0; i < n; i++){
         df.writeLine(String.format(Locale.US, "%10.5f", t.getElement(i))
         + "  " + String.format(Locale.US, "%10.5f", y.getElement(i)) +
         "  " + String.format(Locale.US, "%10.5f", deltay.getElement(i)));
      } 
// perform fits with polynomials of different degree
      for(int nvar = 1; nvar <= 4; nvar++){
         LsqPol lp = new LsqPol(t, y, deltay, nvar);
         int degree = nvar - 1;
         df.writeLine("\n Fit to polynomial of degree "
            + degree + ". Minimum function M = " + String.format(Locale.US, "%10.5f", lp.getChiSquare()));
         x = lp.getResult();
         df.writeLine("Solution vector x =");
         df.writeLine(x.toString());
         cx = lp.getCovarianceMatrix();
         df.writeLine("Covariance matrix cx =");
         df.writeLine(cx.toString());
      }
   }

   public static void main(String s[]) {
      new E1Lsq();
   }

}
