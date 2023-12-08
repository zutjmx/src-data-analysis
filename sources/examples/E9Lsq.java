package examples;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events

/**
* Example demonstrating the general case of least squares fitting, including plot of parameter plane with confidence region and asymmetric errors.
*/
public class E9Lsq {
   int ncase, m, n, nred, r;
   double mf;
   double[][] dxasy;
   DatanFrame df;
   DatanVector x, xresult, y, ysav;
   DatanMatrix  cx, cy, f, e;
   DatanUserFunction uf;
   String caption;
// data
      double[] t = {0.2, 0.2, 0.8, 0.85};
      double[] s = {0.15, 0.7, 1.1, 0.8};
      double[] dt = {0.4, 0.4, 0.4, 0.2};
      double[] ds = {0.2, 0.4, 0.4, 0.4};
      double[] rho = {0., 0., 0.5, 0.};

   public E9Lsq(){
      String str = "Example demonstrating the general case of least squares fitting, ";
      str = str + "including plot of parameter plane with confidence region and asymmetric errors.";
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
      ysav = new DatanVector(y);
// set first approximation of unknowns
         x = new DatanVector(r);
         x.setElement(1, (s[3] - s[0]) / (t[3] - t[0]));
         x.setElement(0, s[0] - x.getElement(1) * t[0]);
         DatanVector xfirstappr = new DatanVector(x);
         int[] list = {1, 1};
         df.writeLine("performing fit to straight line in s,t-plane with LsqGen");
         df.writeLine("n = " + n + ", r = " + r + ", nred = " + nred + ", list = {" + list[0] + ", " + list[1] + "}");
         df.writeLine("first approx.: x = " + x.toString());
         df.writeLine("measurements: y = " + y.toString());
         df.writeLine("cov. mat.: cy = ");
         df.writeLine(cy.toString());
         uf = new StraightLine();
// perform fit
         LsqGen lg = new LsqGen(y, cy, x, list, m, uf);
         if(lg.hasConverged()){
// write results
            xresult = lg.getResult();
            mf = lg.getChiSquare();
            cx = lg.getCovarianceMatrix();
            df.writeLine("x = " + xresult.toString());
            df.writeLine("minimum function = " + String.format(Locale.US, "%10.5f", mf));
            df.writeLine("covariance matrix cx = ");
            df.writeLine(cx.toString());
            df.writeLine("vector of improved measurements y = ");
            df.writeLine(lg.getImprovedMeasurements().toString());
            df.writeLine("covariance matrix of improved measurements cy = ");
            df.writeLine(lg.getCovarianceMatrixOfImprovedMeasurements().toString());
         }
// asymmetric errors
         LsqAsg la = new LsqAsg(ysav, cy, xresult, list, cx, mf, m, uf);
         dxasy = la.getAsymmetricErrors(0.);
         DatanMatrix as = new DatanMatrix(dxasy);
         df.writeLine("Asymmetric errors:");
         df.writeLine(as.toString());
// producd plots
         plotDataAndFittedCurve();
         plotParameterPlane();
   }


protected void plotDataAndFittedCurve(){
// prepare data and fitted curve for plotting
   int npl = 2;
   double[] xpl = new double[npl];
   double[] ypl = new double[npl];
   double[] datx = new double[m];
   double[] daty = new double[m];
   double[] datsx = new double[m];
   double[] datsy = new double[m];
   double[] datrho = new double[m];
   for(int i = 0; i < m; i++){
      datx[i] = t[i];
      daty[i] = s[i];
      datsx[i] = dt[i];
      datsy[i] = ds[i];
      datrho[i] = rho[i];
   }
   xpl[0] = -.5;
   xpl[1] = 2.;
   ypl[0] = xresult.getElement(0) + xresult.getElement(1) * xpl[0];
   ypl[1] = xresult.getElement(0) + xresult.getElement(1) * xpl[1];
   double deltax0 = Math.sqrt(cx.getElement(0, 0));
   double deltax1 = Math.sqrt(cx.getElement(1, 1));
   double correl = cx.getElement(0, 1) / (deltax0 * deltax1);
// produce graphics
      NumberFormat numForm = NumberFormat.getNumberInstance(Locale.US);
	   numForm.setMaximumFractionDigits(3);
	   numForm.setMinimumFractionDigits(3);
      caption = "x_1#=" + numForm.format(xresult.getElement(0)) + ", x_2#=" +  numForm.format(xresult.getElement(1));
      caption = caption + ", M=" +  numForm.format(mf);
      caption = caption + ", &D@x_1#=" +  numForm.format(deltax0);
      caption = caption + ", &D@x_2#=" +  numForm.format(deltax1);
      caption = caption + ", &r@=" +  numForm.format(correl);
      double scale = .2;
      GraphicsWithDataPointsAndPolyline gdp = new GraphicsWithDataPointsAndPolyline(getClass().getName(),
      getClass().getName() + "-" + ncase + ".ps",
      xpl, ypl, -1, scale, datx, daty, datsx, datsy, datrho, "t", "y", caption);
   }
 
    protected void plotParameterPlane(){
      double x1 = x.getElement(0);
      double x2 = x.getElement(1);
      double dx1 = Math.sqrt(cx.getElement(0,0));
      double dx2 = Math.sqrt(cx.getElement(1,1));
      double rho = (cx.getElement(1,0)) / (dx1 * dx2);      
// prepare size of plot
      double xmin = - 1.5;
      double xmax = 1.5;
      double ymin = 0.;
      double ymax = 3.;
      DatanGraphics.openWorkstation(getClass().getName(), getClass().getName() + "_ParameterPlane.ps");
      DatanGraphics.setFormat(0., 0.);
      DatanGraphics.setWindowInComputingCoordinates(xmin, xmax, ymin, ymax);
      DatanGraphics.setViewportInWorldCoordinates(.2, .9, .16, .86);
      DatanGraphics.setWindowInWorldCoordinates(-.414, 1., 0., 1.);
      DatanGraphics.setBigClippingWindow();
      DatanGraphics.chooseColor(2);
      DatanGraphics.drawFrame();
      DatanGraphics.drawScaleX("x_1");
      DatanGraphics.drawScaleY("x_2");
      DatanGraphics.drawBoundary();
      DatanGraphics.chooseColor(5);
// draw data point with errors (and correlation)
      DatanGraphics.drawDatapoint(1, 1., x1, x2, dx1, dx2, rho);
      DatanGraphics.chooseColor(2);
      DatanGraphics.drawCaption(1., "Parameter plane");
// draw confidence contour
      double fcont = mf + 1.;
      int nx = 100;
      int ny = 100;
      double dx =  (xmax - xmin) / (int) nx;
      double dy =  (ymax - ymin) / (int) ny;
      myContourUserFunction mcuf = new myContourUserFunction();
      DatanGraphics.setBigClippingWindow();
      DatanGraphics.chooseColor(1);
      DatanGraphics.drawContour(xmin, ymin, dx, dy, nx, ny, fcont, mcuf);
// draw asymmetric errors as horiyontal and vertical bars
      DatanGraphics.chooseColor(3);
      double[] xpl = new double[2];
      double[] ypl = new double[2];
      for(int i = 0; i < 2; i++){
         if(i == 0) xpl[0] = x1 - dxasy[0][0];
         else xpl[0] = x1 + dxasy[0][1];
         xpl[1] = xpl[0];
         ypl[0] = ymin;
         ypl[1] = ymax;
         DatanGraphics.drawPolyline(xpl, ypl);
      }
      for(int i = 0; i < 2; i++){
         if(i == 0) ypl[0] = x2 - dxasy[1][0];
         else ypl[0] = x2 + dxasy[1][1];
         ypl[1] = ypl[0];
         xpl[0] = xmin;
         xpl[1] = xmax;
         DatanGraphics.drawPolyline(xpl, ypl);
      }
      DatanGraphics.closeWorkstation();
   }

       
    private class myContourUserFunction extends DatanUserFunction{

      public double getValue(double x1, double x2){
         double r;
         double arg[] = {x1, x2};
         DatanVector a = new DatanVector(arg);
         y = new DatanVector(ysav);
         int[] list = {0, 0};
         LsqGen lg = new LsqGen(y, cy, a, list, m, uf);
         r = lg.getChiSquare();
         return r;
	  }
   }

   private class StraightLine extends DatanUserFunction{
      public double getValue(DatanVector eta, DatanVector x, int k){
         return eta.getElement(2 * k + 1) - x.getElement(0) - x.getElement(1) * eta.getElement(2 * k);
	   }
   }

   public static void main(String s[]) {
      new E9Lsq();
   }

}
