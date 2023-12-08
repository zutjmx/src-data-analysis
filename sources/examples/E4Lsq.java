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
public class E4Lsq {
   int n, r;
   int[] list, seeds;
   double delt, sig;
   DatanFrame df;
   DatanVector c, t, x, y, deltay;
   DatanMatrix a, cx;
   DatanUserFunction lp2g;

   public E4Lsq(){
      String s = "Example demonstrating the fit of a nonlinear function to data";
      df = new DatanFrame(getClass().getName(), s);
// simulate data
      double[] xx = {5., -.005, -.001, 10., 10., 2., 10., 30., 2.};
      x = new DatanVector(xx);
      n = 50;
      int[] seeds = {15, 135};
      DatanRandom.setSeedsEcuy(seeds);
      double[] tt = new double[n];
      double[] deltayy = DatanRandom.standardNormal(n);
      double[] yy = DatanRandom.ecuy(n);
      delt = 1.;
      lp2g = new LsqP2G();
      df.writeLine("         t" + "           y"   + "      deltay");
      for(int i = 0; i < n; i++){
         tt[i] = (double)(i + 1) * delt;
         sig = .4 * (.5 + yy[i]);
         yy[i] = lp2g.getValue(x, tt[i]) + deltayy[i] * sig;
         deltayy[i] = sig;
         df.writeLine(String.format(Locale.US, "%10.5f", tt[i])
         + "  " + String.format(Locale.US, "%10.5f", yy[i]) +
         "  " + String.format(Locale.US, "%10.5f", deltayy[i]));
      }
// perform fit
      t = new DatanVector(tt);
      y = new DatanVector(yy);
      deltay = new DatanVector(deltayy);      
      double[] x1 = {.0, .0, .0, 8., 12., 3., 12., 28., 1.5};  // first approximation
      x = new DatanVector(x1);
      int[] list = {1, 1, 1, 1, 1, 1, 1, 1, 1};
      LsqMar lm = new LsqMar(t, y, deltay, x, list, lp2g);
      df.writeLine("\n Fit to polynomial + 2 Gaussians: First Approximation x = " + x.toString());
      df.writeLine("Minimum function M = " + String.format(Locale.US, "%10.5f", lm.getChiSquare()));
      DatanVector xres = lm.getResult();
      df.writeLine("Result x = " + xres.toString());
// prepare data and fitted curve for plotting
   int npl = 1000;
   double[] xpl = new double[npl];
   double[] ypl = new double[npl];
   double[] datx = new double[n];
   double[] daty = new double[n];
   double[] datsx = new double[n];
   double[] datsy = new double[n];
   double[] datrho = new double[n];
   for(int i = 0; i < n; i++){
      datx[i] = t.getElement(i);
      daty[i] = y.getElement(i);
      datsx[i] = 0.;
      datsy[i] = deltay.getElement(i);
      datrho[i] =0.;
   }
   double del = (t.getElement(n - 1) - t.getElement(0) + 10.) / (double)(npl - 1);
   for(int i = 0; i < npl; i++){
      xpl[i] = -5. + t.getElement(0) + (double)i * del;
      ypl[i] = lp2g.getValue(xres, xpl[i]);
   }
// produce graphics
      String caption = "Fit of polynomial + 2 Gaussians";
      double scale = .2;
      GraphicsWithDataPointsAndPolyline gdp = new GraphicsWithDataPointsAndPolyline(getClass().getName(), "E4Lsq.ps",
      xpl, ypl, 1, scale, datx, daty, datsx, datsy, datrho, "t", "y", caption);

   }

   private class LsqP2G extends DatanUserFunction{
      public double getValue(DatanVector x, double t){
         double arg1, arg2, back, gauss1, gauss2;
         back = x.getElement(0) + x.getElement(1) * t + x.getElement(2) * t * t;
         arg1 = Math.pow(x.getElement(4) - t, 2.) / (2 * Math.pow(x.getElement(5), 2.));
         arg2 = Math.pow(x.getElement(7) - t, 2.) / (2 * Math.pow(x.getElement(8), 2.));
         gauss1 = x.getElement(3) * Math.exp(- arg1);
         gauss2 = x.getElement(6) * Math.exp(- arg2);
         return back + gauss1 + gauss2;
	}

   }

   public static void main(String s[]) {
      new E4Lsq();
   }

}
