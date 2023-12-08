package examples;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events
   
/**
* Example demonstrating the use of class MinAsy by fitting a Gaussian to small sample and determining the asymmetric errors of parameters by MinAs
*/
public class E3Min {
   int nny;
   AuxJNumberInput[] ni = new AuxJNumberInput[1];
   DatanFrame df;  
   NumberFormat numForm;
   DatanVector x, x0;
   DatanMatrix cx;
   double fcont;
   double[] y;
   double[][] dxasy;
   String caption;
   MinLogLikeGauss mllg;

   public E3Min(){  
      numForm = NumberFormat.getNumberInstance(Locale.US);
	   numForm.setMaximumFractionDigits(12);
  	   numForm.setMinimumFractionDigits(12);
      String s = "Example demonstrating the use of class MinAsy by fitting a Gaussian to small sample"
                  + " and determining the asymmetric errors of parameters by MinAsy";
      df = new DatanFrame(getClass().getName(), s);
      AuxJInputGroup ig = new AuxJInputGroup("Enter number N of events (>= 2, <= 10000)", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("N", "number of events", errorLabel);
      ig.add(ni[0]);
      ni[0].setProperties("N", true);
      ni[0].setMinimum(2);
      ni[0].setMaximum(10000);
      ni[0].setNumberInTextField(10);
      df.add(ig);
      df.add(errorLabel);
      JButton goButton = new JButton("Go");
      GoButtonListener gl = new GoButtonListener();
      goButton.addActionListener(gl);
      df.add(goButton);
      df.repaint();
   }

    protected boolean inputOk(){
       boolean ok = true;
       for(int i = 0; i < ni.length; i++){
          if(ni[i].isEnabled()) ok = ok && ni[i].parseOk();
       }
       return ok;
    }

    protected void compute(){
      nny = (int)ni[0].parseInput();
      df.writeLine("Number of events is: n = " + nny);
      y = DatanRandom.standardNormal(nny);
      out(y);
      mllg = new MinLogLikeGauss(y);
      x0 = new DatanVector(2);
      x0.setElement(0, 1.);
      x0.setElement(1, 2.);
      MinSim ms = new MinSim(x0, mllg);
      x = ms.getMinPosition();
      fcont = ms.getMinimum();
      df.writeLine("Minimization with MinSim yields x = ");
      df.writeLine(x.toString());
// covariance matrix
      MinCov mc = new MinCov(x, mllg);
      cx = mc.getCovarianceMatrix(1.);
      df.writeLine("Covariance Matrix cx = ");
      df.writeLine(cx.toString());
// asymmetric errors
      fcont = fcont + 0.5;
      MinAsy ma = new MinAsy(x, cx, mllg);
      dxasy = ma.getAsymmetricErrors(fcont);
      DatanMatrix as = new DatanMatrix(dxasy);
      df.writeLine("Asymmetic errors:");
      df.writeLine(as.toString());
      df.writeLine("ma.hasConverged() = " + ma.hasConverged());
      plotScatterDiagram();
      plotParameterPlane();
    }

    protected void plotScatterDiagram(){
// plot sample as one dimensional scatter plot and Gaussian
      double xmax = 5.;
      double xmin = -5.;
      DatanGraphics.openWorkstation(getClass().getName(), "E3Min_1.ps");
      DatanGraphics.setFormat(0., 0.);
      DatanGraphics.setWindowInComputingCoordinates(xmin, xmax, 0., .5);
      DatanGraphics.setViewportInWorldCoordinates(-.15, .9, .16, .86);
      DatanGraphics.setWindowInWorldCoordinates(-.414, 1., 0., 1.);
      DatanGraphics.setBigClippingWindow();
      DatanGraphics.chooseColor(2);
      DatanGraphics.drawFrame();
      DatanGraphics.drawScaleX("y");
      DatanGraphics.drawScaleY("f(y)");
      DatanGraphics.drawBoundary();
      double xpl[] = new double[2];
      double ypl[] = new double[2];
// plot scatter diagram
      DatanGraphics.chooseColor(1);
      for(int i = 0; i < y.length; i++){
         xpl[0] = y[i];
         xpl[1] = y[i];
         ypl[0] = 0.;
         ypl[0] = .1;
         DatanGraphics.drawPolyline(xpl, ypl);   
      }
// draw Gaussian corresponding to solution
      int npl = 100;
      xpl = new double[npl];
      ypl = new double[npl];
      double fact = 1./ (Math.sqrt(2. * Math.PI) * x.getElement(1));
      double dpl = (xmax - xmin) / (double)(npl - 1);
      for(int i = 0; i < npl; i++){
         xpl[i] = xmin + (double)i * dpl;
         ypl[i] = fact * Math.exp(-.5 * Math.pow((xpl[i] - x.getElement(0)) / x.getElement(1), 2.));
      }
      DatanGraphics.chooseColor(5);
      DatanGraphics.drawPolyline(xpl, ypl);
// draw caption
      String sn = "N = " + nny;
	   numForm.setMaximumFractionDigits(3);
  	   numForm.setMinimumFractionDigits(3);
      String sx1 = ", x_1# = " + numForm.format(x.getElement(0));
      String sx2 = ", x_2# = " + numForm.format(x.getElement(1));
      String sdx1 = ", &D@x_1# = " + numForm.format(Math.sqrt(cx.getElement(0,0)));
      String sdx2 = ", &D@x_2# = " + numForm.format(Math.sqrt(cx.getElement(1,1)));
      caption = sn + sx1 + sx2 + sdx1 + sdx2;
      DatanGraphics.setBigClippingWindow();
      DatanGraphics.chooseColor(2);
      DatanGraphics.drawCaption(1., caption);
      DatanGraphics.closeWorkstation();

    }

    
    protected void plotParameterPlane(){
      double x1 = x.getElement(0);
      double x2 = x.getElement(1);
      double dx1 = Math.sqrt(cx.getElement(0,0));
      double dx2 = Math.sqrt(cx.getElement(1,1));
      double rho = (cx.getElement(1,0)) / (dx1 * dx2);      
// prepare size of plot
      double xmin = x1 - 2. * dx1;
      double xmax = x1 + 2. * dx1;
      double ymin = x2 - 2. * dx2;
      double ymax = x2 + 2. * dx2;
      DatanGraphics.openWorkstation(getClass().getName(), "E3Min_2.ps");
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
      DatanGraphics.drawCaption(1., caption);
// draw confidence contour
      double fcont = mllg.getValue(x) + .5;
      int nx = 100;
      int ny = 100;
      double dx =  (xmax - xmin) / (int) nx;
      double dy =  (ymax - ymin) / (int) ny;
      MinLogLikeGaussCont mllgc = new MinLogLikeGaussCont();
//      System.out.println(" x = " + x.toString() + ", mllgc.getValue(x) = " + mllgc.getValue(x.getElement(0), x.getElement(1)));
      DatanGraphics.setBigClippingWindow();
      DatanGraphics.chooseColor(1);
      DatanGraphics.drawContour(xmin, ymin, dx, dy, nx, ny, fcont, mllgc);
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

    protected void out(double[] y){
      String s, blanks, blanksp, blanksm;
      blanksp = "    ";
      blanksm = "   ";
      int ny = y.length;
      int nlines = ny / 5;
      int nrest = ny % 5;
      for(int i = 0; i < nlines; i++){
         s = "";
         for(int j = 0; j < 5; j++){
            int k = i * 5 + j;
            if(y[k] >=0.)blanks = blanksp;
            else blanks = blanksm; 
            s = s + blanks + numForm.format(y[k]);
         }
         df.writeLine(s);
      }
      if(nrest > 0){
         s = "";
         for(int j = 0; j < nrest; j++){
            int k = nlines * 5 + j;
            if(y[k] >=0.)blanks = blanksp;
            else blanks = blanksm; 
            s = s + blanks + numForm.format(y[k]);
         }
         df.writeLine(s);
      }

    }
    
    private class GoButtonListener implements ActionListener { 
        public void actionPerformed(ActionEvent e) {
           if (inputOk()) compute();
       }
    }

    
    private class MinLogLikeGaussCont extends DatanUserFunction{

      public double getValue(double x, double y){
         double result;
         double arg[] = {x, y};
         DatanVector a = new DatanVector(arg);
         result = mllg.getValue(a);
         return result;
	  }
}

    private class MinLogLikeGauss extends DatanUserFunction{
         double[] y;
         double sqrt2pi = Math.sqrt(2. * Math.PI);
         double big = 1.E10, small = 1.E-10;
         double f;
         MinLogLikeGauss(double[] y){
            this.y = y;
         }
         public double getValue(DatanVector x){
            double result;
            double a = sqrt2pi * x.getElement(1);
            if(a < small) f = big;
            else f = Math.log(a);
            result = (double) y.length * f;
            for(int i = 0; i < y.length; i++){
               f = Math.pow((y[i] - x.getElement(0)), 2.) / (2. * x.getElement(1) * x.getElement(1));
               result += f;
            }
            return result;
         }
    }


   public static void main(String s[]) {
      new E3Min();
   }

}
