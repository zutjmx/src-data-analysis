package solutions;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events
   
/**
* Fitting a circle to data points
*/
public class S7Lsq {
   AuxJNumberInput[] ni = new AuxJNumberInput[4];
   DatanFrame df;
   int n, r, methodNumber;
   double m, x1, x2, x3, sigmat, sigmas, rho, sigmatin, sigmasin, rhoin,  delx1, delx2, delx3, p;
   double phi, delphi, ta, sa, tb, sb, slopea, slopeb, t0, s0, r0;
   double[] rand, dats, datt, datss, datst, datrho;
   DatanVector t, y, dy, x, point;
   DatanMatrix c, cx, cy;
   DatanUserFunction circle;
   LsqNon ln;
   LsqAsm la;
   String caption;
   String[] ac, actionCommands;
   String[] methodString = {"identical for all points", "somewhat ranomized"};

   public S7Lsq(){
      String s = "Fitting a circle to data points";
      df = new DatanFrame(getClass().getName(), s);
// parameter input
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("n", "number of data points(>=4, <=20)", errorLabel);
      ni[0].setProperties("n", true, 4);
      ni[0].setNumberInTextField(10);
      ig.add(ni[0]);
      ni[1] = new AuxJNumberInput("sigma_t", "measurement errors in abscissa (>0, <=0.4)", errorLabel);
      ni[1].setProperties("sigma_t", false, 0.00001);
      ni[1].setNumberInTextField(.15);
      ig.add(ni[1]);
      ni[2] = new AuxJNumberInput("sigma_s", "measurement errors in ordinate (>0, <=0.4)", errorLabel);
      ni[2].setProperties("sigma_s", false, 0.00001);
      ni[2].setNumberInTextField(.15);
      ig.add(ni[2]);
      ni[3] = new AuxJNumberInput("rho", "correlation coefficient (>-1, <1)", errorLabel);
      ni[3].setProperties("rho", false, -0.999, 0.999);
      ni[3].setNumberInTextField(.7);
      ig.add(ni[3]);
      df.add(ig);
      df.add(errorLabel);
// radio-button group for choice of method
      ac = new String[2];
      ac[0] = methodString[0];
      ac[1] = methodString[1];
      RadioListener rl = new RadioListener();
      AuxJRButtonGroup rbg = new AuxJRButtonGroup("Errors and correlation", "", ac, rl);
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
      r = 3;
      x = new DatanVector(r);
      n = (int)ni[0].parseInput();
      sigmatin = ni[1].parseInput();
      sigmasin = ni[2].parseInput();
      rhoin = ni[3].parseInput();
      y = new DatanVector(2 * n);
      cy = new DatanMatrix(2 * n, 2 * n);
// generate data points
      datt = new double[n];
      dats = new double[n];
      datst = new double[n];
      datss = new double[n];
      datrho = new double[n];
      delphi = 2. * Math.PI / (double)n;
      for(int i=0; i < n; i++){
         if(methodNumber == 0){
// take predetermined errors and correlation
            sigmas = sigmasin;
            sigmat = sigmatin;
            rho = rhoin;
         }
         else{
// randomize errors and correlation
            rand = DatanRandom.ecuy(3);
            sigmat = (0.5 + 0.5 * rand[0]) * sigmatin;
            sigmas = (0.5 + 0.5 * rand[1]) * sigmasin;
            rho = Math.abs(rhoin) * (-1. + 2. * rand[2]);
         }
// compose covariance matrix for one point
         c = new DatanMatrix(2);
         c.setElement(0, 0, sigmat * sigmat);
         c.setElement(1, 1, sigmas * sigmas);
         c.setElement(0, 1, sigmat * sigmas * rho);
         c.setElement(1, 0, sigmat * sigmas * rho);
// generate and store coordinates for one point
         double phi = (double)i * delphi;
         point = new DatanVector(2);
         point.setElement(0, Math.cos(phi));
         point.setElement(1, Math.sin(phi));
         DatanRandom.setCovarianceMatrixForMultivariateNormal(c);
         point = DatanRandom.multivariateNormal(point);
         datt[i] = point.getElement(0);
         dats[i] = point.getElement(1);
// store errors and correlation for use in graphics
         datss[i] = sigmas;
         datst[i] = sigmat;
         datrho[i] = rho;
// enter coordinates into vector y of measurement for input to LsqGen
         y.setElement(2 * i, datt[i]);
         y.setElement(2 * i + 1, dats[i]);
// enter covariance matrix of single point into covariance matrix cy of measurements
         cy.putSubmatrix(2 * i, 2 * i, c);
      }
// determine 1st approx. of unknowns
// set 1st approximation of unknowns
         ta = 0.5 * (y.getElement(0) + y.getElement(2));
         sa = 0.5 * (y.getElement(1) + y.getElement(3));
         tb = 0.5 * (y.getElement(2) + y.getElement(4));
         sb = 0.5 * (y.getElement(3) + y.getElement(5));
         slopea = - (y.getElement(3) - y.getElement(1)) / (y.getElement(2) - y.getElement(0));
         slopeb = - (y.getElement(5) - y.getElement(3)) / (y.getElement(4) - y.getElement(2));
         s0 = (ta - tb - slopea * sa + slopeb * sb) / (slopeb - slopea);
         t0 = ta + slopea * (s0 - sa);
         r0 = Math.sqrt(Math.pow(t0 - y.getElement(0), 2) + Math.pow(s0 - y.getElement(1), 2));
         x.setElement(0, t0);
         x.setElement(1, s0);
         x.setElement(2, r0);
// perform fit
         circle = new Circle();
         int list[] = {1, 1, 1}; 
         LsqGen lg = new LsqGen(y, cy, x, list, n, circle);
         if(lg.hasConverged()){
            x = lg.getResult();
            x1 = x.getElement(0);
            x2 = x.getElement(1);
            x3 = Math.abs(x.getElement(2));
            cx = lg.getCovarianceMatrix();
            delx1 = Math.sqrt(cx.getElement(0, 0));
            delx2 = Math.sqrt(cx.getElement(1, 1));
            delx3 = Math.sqrt(cx.getElement(2, 2));
            m = lg.getChiSquare();
            p = 1. - StatFunct.cumulativeChiSquared(m, n - r);
            plotParameterPlane();
         }
         else{
            df.writeLine("No convergence");
         }         
    }

    
    protected void plotParameterPlane(){  
// prepare size of plot
      double xmin = -1.5;
      double xmax = 1.5;
      double ymin = -1.5;
      double ymax = 1.5;
      DatanGraphics.openWorkstation(getClass().getName(),"");
      DatanGraphics.setFormat(0., 0.);
      DatanGraphics.setWindowInComputingCoordinates(xmin, xmax, ymin, ymax);
      DatanGraphics.setViewportInWorldCoordinates(.2, .9, .16, .86);
      DatanGraphics.setWindowInWorldCoordinates(-.414, 1., 0., 1.);
      DatanGraphics.setBigClippingWindow();
      DatanGraphics.chooseColor(2);
      DatanGraphics.drawFrame();
      DatanGraphics.drawScaleX("t");
      DatanGraphics.drawScaleY("s");
      DatanGraphics.drawBoundary();
      DatanGraphics.chooseColor(1);
// draw data points with errors and correlations
      for(int i = 0; i < n; i++){
         DatanGraphics.drawDatapoint(1, .3, datt[i], dats[i], datst[i], datss[i], datrho[i]);
      }
// draw circle corresponding to 1st appr.
      int npl = 100;
      double[] xpl = new double[npl];
      double[] ypl = new double[npl];
      double dang = 2. * Math.PI / (double)(npl - 1);
      for(int i = 0; i < npl; i++){
         double ang = (double)i * dang;
         xpl[i] = t0 + r0 * Math.cos(ang);
         ypl[i] = s0 + r0 * Math.sin(ang);  
      }
      DatanGraphics.chooseColor(5);
      DatanGraphics.drawBrokenPolyline(1, .3, xpl, ypl);
// draw circle corresponding to solution
      for(int i = 0; i < npl; i++){
         double ang = (double)i * dang;
         xpl[i] = x1 + x3 * Math.cos(ang);
         ypl[i] = x2 + x3 * Math.sin(ang);  
      }
      DatanGraphics.drawPolyline(xpl, ypl);
      caption = "x_1#=" + String.format(Locale.US, "%4.2f", x1)
         + ", x_2#=" + String.format(Locale.US, "%4.2f", x2)
         + ", x_3#=" + String.format(Locale.US, "%4.2f", x3)
         + ", &D@x_1#=" + String.format(Locale.US, "%4.2f", delx1)
         + ", &D@x_2#=" + String.format(Locale.US, "%4.2f", delx2)
         + ", &D@x_3#=" + String.format(Locale.US, "%4.2f", delx3)
         +  ", M=" + String.format(Locale.US, "%5.2f", m)
         + ", P=" + String.format(Locale.US, "%6.4f", p);
      DatanGraphics.setBigClippingWindow();
      DatanGraphics.chooseColor(2);
      DatanGraphics.drawCaption(1., caption);
      DatanGraphics.closeWorkstation();
    }

   private class Circle extends DatanUserFunction{
      public double getValue(DatanVector eta, DatanVector x, int k){
         return Math.pow(eta.getElement(2 * k) - x.getElement(0), 2.)
              + Math.pow(eta.getElement(2 * k + 1) - x.getElement(1), 2.)
              - Math.pow(x.getElement(2), 2.);
	   }
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




   public static void main(String s[]) {
      new S7Lsq();
   }

}
