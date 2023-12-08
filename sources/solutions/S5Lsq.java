package solutions;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events
   
/**
* Data following a Breit-Wigner function are fitted to BW or Gaussian; asym. errors and confid. region shown
*/
public class S5Lsq {
   AuxJNumberInput[] ni = new AuxJNumberInput[1];
   DatanFrame df;
   int n, r, methodNumber;
   double m, x1, x2, sigma, t0, deltat, dpl, delx1, delx2, rho, p;
   double[] rand, xpl, ypl, datx, daty, datsx, datsy, datrho;
   double[][] dxasy;
   DatanVector c, t, y, dy, x;
   DatanMatrix a, cov;
   DatanUserFunction bw, gs, cuf;
   LsqNon ln;
   LsqAsm la;
   String caption;
   String[] ac, actionCommands;
   String[] methodString = {"Breit-Wigner function", "Gaussian"};

   public S5Lsq(){
      String s = "Data following a Breit-Wigner function are fitted to BW or Gaussian; asym. errors and confid. region shown";
      df = new DatanFrame(getClass().getName(), s);
// parameter input
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("sigma", "size of measurement errors (>0)", errorLabel);
      ni[0].setProperties("sigma", false, 0.00001);
      ni[0].setNumberInTextField(.2);
      ig.add(ni[0]);
      df.add(ig);
      df.add(errorLabel);
// radio-button group for choice of method
      ac = new String[2];
      ac[0] = methodString[0];
      ac[1] = methodString[1];
      RadioListener rl = new RadioListener();
      AuxJRButtonGroup rbg = new AuxJRButtonGroup("Choice of function to be fitted", "", ac, rl);
      rbg.setSelectedIndex(1);
      methodNumber=0;
      df.add(rbg);
      JButton goButton = new JButton("Go");
      GoButtonListener gl = new GoButtonListener();
      goButton.addActionListener(gl);
      df.add(goButton);
      df.pack();
      df.toFront();
   }

    protected boolean inputOk(){
       boolean ok = true;
       for(int i = 0; i < ni.length; i++){
          if(ni[i].isEnabled()) ok = ok && ni[i].parseOk();
       }
       return ok;
    }

    protected void compute(){
      sigma = ni[0].parseInput();
      bw = new BreitWignerFunction();
      gs = new Gaussian();
// generate data points corresponding to Breit-Wigner
      n = 21; // number of points
      r = 2;  // number of unknowns
      t0 = -3.;
      deltat = .3;
      t = new DatanVector(n);
      y = new DatanVector(n);
      dy = new DatanVector(n);
      rand = DatanRandom.standardNormal(n);
      x = new DatanVector(r);
      x.setElement(0, 0.);
      x.setElement(1, 1.);
      df.writeLine("         t" + "           y"   + "      deltay");
      for(int i = 0; i < n; i ++){
         t.setElement(i, t0 + (double)i * deltat);
         y.setElement(i, bw.getValue(x, t.getElement(i)) + sigma * rand[i]);
         dy.setElement(i, sigma);
         df.writeLine(String.format(Locale.US, "%10.5f", t.getElement(i))
         + "  " + String.format(Locale.US, "%10.5f", y.getElement(i)) +
         "  " + String.format(Locale.US, "%10.5f", dy.getElement(i)));
      }
// set 1st approximation of unknowns
      x.setElement(0, 0.5);
      x.setElement(1, 0.5);
// perform fit and write out results
      int[] list = {1, 1};
      if(methodNumber == 0){
         df.writeLine("Fit to Breit-Wigner");
         df.writeLine("First Approximation x = " + x.toString());
         ln = new LsqNon(t, y, dy, x, list, bw);
      }
      else{
         df.writeLine("Fit to Gaussian");
         ln = new LsqNon(t, y, dy, x, list, gs);
         df.writeLine("First Approximation x = " + x.toString());
      }
      x = ln.getResult();
      x1 = x.getElement(0);
      x2 = x.getElement(1);
      cov = ln.getCovarianceMatrix();
      delx1 = Math.sqrt(cov.getElement(0, 0));
      delx2 = Math.sqrt(cov.getElement(1, 1));
      rho = cov.getElement(1, 0) /(delx1 * delx2);
      m = ln.getChiSquare();
      p = 1. - StatFunct.cumulativeChiSquared(m, n - r);
      df.writeLine("Minimum function M = " + String.format(Locale.US, "%10.5f", m));
      df.writeLine("Fit probability P = " + String.format(Locale.US, "%7.5f", p));
      df.writeLine("Result x = " + x.toString());
      df.writeLine("CovarianceMatrix cx = ");
      df.writeLine(cov.toString());
      plotDataAndFittedCurve();
// asymmetric errors
      if(methodNumber == 0) la = new LsqAsm(t, y, dy, x, list, cov, m, bw);
      else la = new LsqAsm(t, y, dy, x, list, cov, m, gs);
      dxasy = la.getAsymmetricErrors(0.);
      DatanMatrix as = new DatanMatrix(dxasy);
      df.writeLine("Asymmetic errors:");
      df.writeLine(as.toString());
      plotParameterPlane();
    }

    protected void plotDataAndFittedCurve(){
// prepare fitted curve for plotting
      xpl = new double[1001];
      ypl = new double[1001];
      dpl = (double)(n - 1) * deltat / 1000.;
      for(int i = 0; i < 1001; i++){
         xpl[i] = t0 + (double)i * dpl;
         if(methodNumber == 0) ypl[i] = bw.getValue(x, xpl[i]);
         else  ypl[i] = gs.getValue(x, xpl[i]);
      }
// prepare data points for plotting
      datx = new double[n];
      daty = new double[n];
      datsx = new double[n];
      datsy = new double[n];
      datrho = new double[n];
      for(int i = 0; i < n; i ++){
         datx[i] = t.getElement(i);
         daty[i] = y.getElement(i);
         datsx[i] = 0.;
         datsy[i] = dy.getElement(i);
         datrho[i] = 0.;
      }
// display data and fitted curve
      caption = "x_1#=" + String.format(Locale.US, "%5.2f", x1)
         + ", x_2#=" + String.format(Locale.US, "%5.2f", x2)
         + ", &D@x_1#=" + String.format(Locale.US, "%5.2f", delx1)
         + ", &D@x_2#=" + String.format(Locale.US, "%5.2f", delx2)
         + ", &r@=" + String.format(Locale.US, "%5.2f", rho)
         +  ", M=" + String.format(Locale.US, "%5.2f", m)
         + ", P=" + String.format(Locale.US, "%6.4f", p);
      new GraphicsWithDataPointsAndPolyline(getClass().getName(), "", xpl, ypl,
         1, .3, datx, daty, datsx, datsy, datrho, "t", "y", caption);
    } 

    
    protected void plotParameterPlane(){  
// prepare size of plot
      double xmin = x1 - 2. * delx1;
      double xmax = x1 + 2. * delx1;
      double ymin = x2 - 2. * delx2;
      double ymax = x2 + 2. * delx2;
      DatanGraphics.openWorkstation(getClass().getName(),"");
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
      DatanGraphics.drawDatapoint(1, 1., x1, x2, delx1, delx2, rho);
      DatanGraphics.chooseColor(2);
      DatanGraphics.drawCaption(1., "Parameter plane");
// draw confidence contour
      double fcont = m + 1.;
      int nx = 100;
      int ny = 100;
      double dx =  (xmax - xmin) / (int) nx;
      double dy =  (ymax - ymin) / (int) ny;
      if(methodNumber ==0) cuf = new bwContourUserFunction();
      else cuf = new gsContourUserFunction();
      DatanGraphics.setBigClippingWindow();
      DatanGraphics.chooseColor(1);
      DatanGraphics.drawContour(xmin, ymin, dx, dy, nx, ny, fcont, cuf);
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

    
    private class GoButtonListener implements ActionListener { 
        public void actionPerformed(ActionEvent e) {
           if (inputOk()) compute();
       }
    }

/** Listens to the radio buttons. */
    
    private class RadioListener implements ActionListener { 
        public void actionPerformed(ActionEvent e) {
           actionCommands = ac;
           int l = actionCommands.length;
	        for(int i = 0; i < actionCommands.length; i++) {
		        if(e.getActionCommand() == actionCommands[i]){
                 methodNumber = i;
              }
          }
       }
    }
    
    private class bwContourUserFunction extends DatanUserFunction{

      public double getValue(double x1, double x2){
         double r;
         double arg[] = {x1, x2};
         DatanVector a = new DatanVector(arg);
         r = 0.;
         for(int i = 0; i < n; i++){
            double d =  (y.getElement(i) - bw.getValue(a, t.getElement(i))) / dy.getElement(i);
            double s = d * d;
            r = r + s;
         }
         return r;
	  }
   }
    
    private class gsContourUserFunction extends DatanUserFunction{

      public double getValue(double x1, double x2){
         double r;
         double arg[] = {x1, x2};
         DatanVector a = new DatanVector(arg);
         r = 0.;
         for(int i = 0; i < n; i++){
            double d =  (y.getElement(i) - gs.getValue(a, t.getElement(i))) / dy.getElement(i);
            double s = d * d;
            r = r + s;
         }
         return r;
	  }
   }



    private class BreitWignerFunction extends DatanUserFunction{
       public double getValue(DatanVector x, double t){
          return 2. * Math.pow(x.getElement(1), 2.) /
             ((Math.PI * x.getElement(1)) * (4. * Math.pow(t - x.getElement(0), 2.) + Math.pow(x.getElement(1), 2.)));
	    }
    }

    private class Gaussian extends DatanUserFunction{
       public double getValue(DatanVector x, double t){
          return StatFunct.normal(t, x.getElement(0), x.getElement(1));
	    }
    }

   public static void main(String s[]) {
      new S5Lsq();
   }

}
