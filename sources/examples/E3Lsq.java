package examples;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events

/**
* Example demonstrating the fit of a nonlinear function to data
*/
public class E3Lsq {
   int n, r;
   int[] seeds;
   double delt, sig;
   DatanFrame df;
   DatanVector c, t, x, y, deltay;
   DatanMatrix a, cx;
   DatanUserFunction lsqgss;

   public E3Lsq(){
      String s = "Example demonstrating the fit of a nonlinear function to data";
      df = new DatanFrame(getClass().getName(), s);
// simulate data
      double[] xx = {1., 1.2, .4};
      x = new DatanVector(xx);
      n = 20;
      int[] seeds = {15, 215};
      DatanRandom.setSeedsEcuy(seeds);
      double[] tt = new double[n];
      double[] deltayy = DatanRandom.standardNormal(n);
      double[] yy = DatanRandom.ecuy(n);
      delt = 2. / 21;
      lsqgss = new LsqGauss();
      df.writeLine("         t" + "           y"   + "      deltay");
      for(int i = 0; i < n; i++){
         tt[i] = (double)(i + 1) * delt;
         sig = .1 * (.5 + yy[i]);
         yy[i] = lsqgss.getValue(x, tt[i]) + deltayy[i] * sig;
         deltayy[i] = sig;
         df.writeLine(String.format(Locale.US, "%10.5f", tt[i])
         + "  " + String.format(Locale.US, "%10.5f", yy[i]) +
         "  " + String.format(Locale.US, "%10.5f", deltayy[i]));
      }
      t = new DatanVector(tt);
      y = new DatanVector(yy);
      deltay = new DatanVector(deltayy);
      int[] list = {1, 1, 1};
      for(int nred = 3; nred >= 0; nred--){
         if(nred == 3){
            x.setElement(0, .8);
            x.setElement(1, .8);
            x.setElement(2, .8);
         }
         else if(nred == 2){
            list[1] = 0;
            x.setElement(0, .8);
            x.setElement(1, 1.2);
            x.setElement(2, .8);
         }
         else if(nred == 1){
            list[2] = 0;
            x.setElement(0, .8);
            x.setElement(1, 1.2);
            x.setElement(2, .4);
         }
         else{
            list[0] = 0;
            x.setElement(0, 1.);
            x.setElement(1, 1.2);
            x.setElement(2, .4);
         }
         LsqNon ln = new LsqNon(t, y, deltay, x, list, lsqgss);
         df.writeLine("\n Fit to Gaussian: First Approximation x = " + x.toString());
         df.writeLine("list = {" + list[0] + ", " + list[1] + ", " + list[2] + "}");
         df.writeLine("Minimum function M = " + String.format(Locale.US, "%10.5f", ln.getChiSquare()));
         DatanVector xres = ln.getResult();
         df.writeLine("Result x = " + xres.toString());
         DatanMatrix cx = ln.getCovarianceMatrix();
         if(nred != 0) df.writeLine("Covariance matrix cx = \n" + cx.toString());
      }

   }

   private class LsqGauss extends DatanUserFunction{
      public double getValue(DatanVector x, double t){
         double arg = Math.pow((x.getElement(1) - t), 2.) / (2. * Math.pow(x.getElement(2), 2.));
         return x.getElement(0) * Math.exp(- arg);
	 }

   }

   public static void main(String s[]) {
      new E3Lsq();
   }

}
