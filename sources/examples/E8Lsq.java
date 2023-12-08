package examples;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events

/**
* Example demonstrating the the problem of constrained measurements
*/
public class E8Lsq {
   int ncase, m, n, nred, r;
   double mf;
   DatanFrame df;
   DatanVector x, xresult, y;
   DatanMatrix  cx, cy, f, e;
   DatanUserFunction uf;
   String caption;
// data
      double[] t = {0.2, 0.5, 0.8, 1.3};
      double[] s = {0.15, 0.4, 0.7, 0.8};
      double[] dt = {0.1, 0.1, 0.1, 0.05};
      double[] ds = {0.05, 0.1, 0.1, 0.1};
      double[] rho = {0., 0., 0.5, 0.};

   public E8Lsq(){
      String str = "Example demonstrating the the problem of constrained measurements";
      df = new DatanFrame(getClass().getName(), str);
// write table of data
      df.writeLine("         t" + "           s"   + "          dt"   + "          ds"   + "         rho");
      for(int i = 0; i < 4; i++){
         df.writeLine(String.format(Locale.US, "%10.5f", t[i])
         + "  " + String.format(Locale.US, "%10.5f", s[i]) +
         "  " + String.format(Locale.US, "%10.5f", dt[i]) +
         "  " + String.format(Locale.US, "%10.5f", ds[i]) +
         "  " + String.format(Locale.US, "%10.5f", rho[i]));  
      }
// set up data for input to LsqGen
      n = 8;
      m = 4;
      r = 2;
      for(ncase = 1; ncase <=2 ; ncase++){
         cy = new DatanMatrix(n);
         y = new DatanVector(n);
         for(int i = 0; i < m; i++){
            y.setElement(2 * i, t[i]);
            y.setElement(2 * i + 1, s[i]);
            cy.setElement(2 * i, 2 * i, dt[i] * dt[i]);
            cy.setElement(2 * i + 1, 2 * i + 1, ds[i] * ds[i]);
            cy.setElement(2 * i, 2 * i + 1, rho[i] * dt[i] * ds[i]);
            cy.setElement(2 * i + 1, 2 * i, rho[i] * dt[i] * ds[i]);
         }
// set first approximation of unknowns
         x = new DatanVector(r);
         x.setElement(1, (s[3] - s[0]) / (t[3] - t[0]));
         x.setElement(0, s[0] - x.getElement(1) * t[0]);
         DatanVector xfirstappr = new DatanVector(x);
         int[] list = {0, 0};
         if(ncase == 1){
// artificial: fix x at the values that were previously obtained by fitting them
            x.setElement(0, 0.067);
            x.setElement(1, 0.643);
         }
         else{
// fix x at roughly guessed values
            x.setElement(0, 0.);
            x.setElement(1, 0.5);
         }
         df.writeLine("performing fit to straight line in s,t-plane with LsqGen");
         df.writeLine("n = " + n + ", r = " + r + ", nred = " + nred + ", list = {" + list[0] + ", " + list[1] + "}");
         df.writeLine("first approx.: x = " + x.toString());
         df.writeLine("measurements: y = " + y.toString());
         df.writeLine("cov. mat.: cy = ");
         df.writeLine(cy.toString());
         uf = new StraightLine();
         LsqGen lg = new LsqGen(y, cy, x, list, m, uf);
         if(lg.hasConverged()){
            xresult = lg.getResult();
            mf = lg.getChiSquare();
            cx = lg.getCovarianceMatrix();
            y = lg.getImprovedMeasurements();
            cy = lg.getCovarianceMatrixOfImprovedMeasurements();
            df.writeLine("x = " + xresult.toString());
            df.writeLine("minimum function = " +  String.format(Locale.US, "%10.5f", mf));
            df.writeLine("vector of improved measurements y = ");
            df.writeLine(y.toString());
            df.writeLine("covariance matrix of improved measurements cy = ");
            df.writeLine(cy.toString());
         }
         plotDataAndFittedCurve();
      }
   }


protected void plotDataAndFittedCurve(){
// prepare data and fitted curve for plotting
   int npl = 2;
   double[] xpl = new double[npl];
   double[] ypl = new double[npl];
   double[] datx = new double[2 * m];
   double[] daty = new double[2 * m];
   double[] datsx = new double[2 * m];
   double[] datsy = new double[2 * m];
   double[] datrho = new double[2 * m];
   for(int i = 0; i < m; i++){
// original data points
      datx[i] = t[i];
      daty[i] = s[i];
      datsx[i] = dt[i];
      datsy[i] = ds[i];
      datrho[i] = rho[i];
   }
   for(int i = m; i < 2 * m; i++){
// "improved" points
      int k = i - m;
      datx[i] = y.getElement(2 * k);
      daty[i] = y.getElement(2 * k + 1);
      datsx[i] = Math.sqrt(cy.getElement(2 * k, 2* k));
      datsy[i] = Math.sqrt(cy.getElement(2 * k + 1, 2* k + 1));
      datrho[i] = cy.getElement(2 * k, 2* k + 1) / (datsx[i] * datsy[i]);
   }
   xpl[0] = 0.;
   xpl[1] = 2.;
   ypl[0] = xresult.getElement(0) + xresult.getElement(1) * xpl[0];
   ypl[1] = xresult.getElement(0) + xresult.getElement(1) * xpl[1];
   double deltax = Math.sqrt(cx.getElement(0, 0));
// produce graphics
      NumberFormat numForm = NumberFormat.getNumberInstance(Locale.US);
	   numForm.setMaximumFractionDigits(3);
	   numForm.setMinimumFractionDigits(3);
      caption = "x_1#=" + numForm.format(xresult.getElement(0)) + ", x_2#=" +  numForm.format(xresult.getElement(1));
      caption = caption + ", M=" +  numForm.format(mf);
      double scale = .2;
      GraphicsWithDataPointsAndPolyline gdp = new GraphicsWithDataPointsAndPolyline(getClass().getName(),
      getClass().getName() + "-" + ncase + ".ps",
      xpl, ypl, -1, scale, datx, daty, datsx, datsy, datrho, "t", "y", caption);
   } 


   public static void main(String s[]) {
      new E8Lsq();
   }

   private class StraightLine extends DatanUserFunction{
      public double getValue(DatanVector eta, DatanVector x, int k){
         return eta.getElement(2 * k + 1) - x.getElement(0) - x.getElement(1) * eta.getElement(2 * k);
	   }
   }

}
