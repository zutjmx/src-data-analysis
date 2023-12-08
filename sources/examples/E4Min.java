package examples;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events
   
/**
* Example performing the fit of a Gaussian to a histogram
*/
public class E4Min {
   int nevents, nbins, methodNumber, seed1, seed2;
   AuxJNumberInput[] ni = new AuxJNumberInput[4];
   DatanFrame df;  
   NumberFormat numForm;
   DatanVector x, x0;
   DatanMatrix cx;
   Histogram hist;
   double fcont, t0, deltat, xmin, xmax;
   double[] y;
   double[][] dxasy;
   String caption;
   String[] ac, actionCommands;
   DatanUserFunction muf;
   String[] methodString = {"Likelihood minimization with Poisson statistics", "Minimization of sum of squares"};

   public E4Min(){  
      numForm = NumberFormat.getNumberInstance(Locale.US);
	   numForm.setMaximumFractionDigits(12);
  	   numForm.setMinimumFractionDigits(12);
      String s = "Example performing the fit of a Gaussian to a histogram";
      df = new DatanFrame(getClass().getName(), s);
// parameter input
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("N", "number of events (> 1, <= 10000)", errorLabel);
      ig.add(ni[0]);
      ni[0].setProperties("N", true, 2, 10000);
      ni[0].setNumberInTextField(100);
      ni[1] = new AuxJNumberInput("n_bins", "number of histogram bins", errorLabel);
      ni[1].setProperties("n_bins", true, 2, 1000);
      ni[1].setNumberInTextField(10);
      ni[2] = new AuxJNumberInput("seed_1", "seed_1 for DatanRandom.ecuy", errorLabel);
      ni[2].setProperties("seed_1", true, 1, 1000000);
      ni[2].setNumberInTextField(1);
      ni[3] = new AuxJNumberInput("seed_1", "seed_1 for DatanRandom.ecuy", errorLabel);
      ni[3].setProperties("seed_2", true, 1, 1000000);
      ni[3].setNumberInTextField(2);
      ig.add(ni[0]);
      ig.add(ni[1]);
      ig.add(ni[2]);
      ig.add(ni[3]);
      df.add(ig);
      df.add(errorLabel);
// radio-button group for choice of method
      ac = new String[2];
      ac[0] = methodString[0];
      ac[1] = methodString[1];
      RadioListener rl = new RadioListener();
      AuxJRButtonGroup rbg = new AuxJRButtonGroup("Select Minimization Method", "", ac, rl);
      df.add(rbg); 
      JButton goButton = new JButton("Go");
      GoButtonListener gl = new GoButtonListener();
      goButton.addActionListener(gl);
      df.add(goButton);
   }

    protected boolean inputOk(){
       boolean ok = true;
       for(int i = 0; i < ni.length; i++){
          if(ni[i].isEnabled()) ok = ok && ni[i].parseOk();
       }
       return ok;
    }

    protected void compute(){
      nevents = (int)ni[0].parseInput();
      df.writeLine("Number of events is: n = " + nevents);
      nbins = (int)ni[1].parseInput();
      df.writeLine("Number of histogram bins is: n_bins = " + nbins);
      df.writeLine("Method used is: " + methodString[methodNumber]);      
      seed1 = (int)ni[2].parseInput();
      seed2 = (int)ni[3].parseInput();
      int seeds[] = {seed1, seed2};
      DatanRandom.setSeedsEcuy(seeds);
      y = DatanRandom.standardNormal(nevents);
// prepare and fill histogram
      t0 = -5.25;
      deltat = 2. * Math.abs(t0) / (double) nbins;
      hist = new Histogram(t0, deltat, nbins);
      for(int i = 0; i < nevents; i++){
         hist.enter(y[i], 1.);
      }
// set first approximation
      x0 = new DatanVector(2);
      x0.setElement(0, 1.);
      x0.setElement(1, 2.);
// introduce user function
      if(methodNumber == 0) muf = (DatanUserFunction) new MinLogLikeHistPoisson(hist, nevents);
      else muf = (DatanUserFunction) new MinHistSumOfSquares(hist, nevents);
      MinSim ms = new MinSim(x0, muf);
      x = ms.getMinPosition();
      fcont = ms.getMinimum();
      df.writeLine("Minimization with MinSim yields x = ");
      df.writeLine(x.toString());
// covariance matrix
      MinCov mc = new MinCov(x, muf);
      cx = mc.getCovarianceMatrix(1.);
      df.writeLine("Covariance Matrix cx = ");
      df.writeLine(cx.toString());
// asymmetric errors
      fcont = fcont + 0.5;
      MinAsy ma = new MinAsy(x, cx, muf);
      dxasy = ma.getAsymmetricErrors(fcont);
      DatanMatrix as = new DatanMatrix(dxasy);
      df.writeLine("Asymmetic errors:");
      df.writeLine(as.toString());
      plotHistogramAndFittedCurve();
      plotParameterPlane();
    }

    protected void plotHistogramAndFittedCurve(){
// prepare polyline
      int npl = 100;
      xmin = -5.25;
      xmax = 5.25;
      double[] xpl = new double[npl];
      double[] ypl = new double[npl];
      double fact = 1./ (Math.sqrt(2. * Math.PI) * x.getElement(1));
      double dpl = (xmax - xmin) / (double)(npl - 1);
      double fnorm = (double)nevents * fact * deltat;
      for(int i = 0; i < npl; i++){
         xpl[i] = xmin + (double)i * dpl;
         ypl[i] = fnorm * Math.exp(-.5 * Math.pow((xpl[i] - x.getElement(0)) / x.getElement(1), 2.));
      };
// prepare caption
      String sn = "N = " + nevents;
	   numForm.setMaximumFractionDigits(3);
  	   numForm.setMinimumFractionDigits(3);
      String sx1 = ", x_1# = " + numForm.format(x.getElement(0));
      String sx2 = ", x_2# = " + numForm.format(x.getElement(1));
      String sdx1 = ", &D@x_1# = " + numForm.format(Math.sqrt(cx.getElement(0,0)));
      String sdx2 = ", &D@x_2# = " + numForm.format(Math.sqrt(cx.getElement(1,1)));
      caption = sn + sx1 + sx2 + sdx1 + sdx2;
      new GraphicsWithHistogramAndPolyline(getClass().getName(),
      getClass().getName() + "_Hist.ps", xpl, ypl, hist, "y", "N(y)", caption);
        
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
      DatanGraphics.drawCaption(1., caption);
// draw confidence contour
      double fcont = muf.getValue(x) + .5;
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
    
    private class GoButtonListener implements ActionListener { 
        public void actionPerformed(ActionEvent e) {
           if (inputOk()) compute();
       }
    }

    
    private class myContourUserFunction extends DatanUserFunction{

      public double getValue(double x, double y){
         double result;
         double arg[] = {x, y};
         DatanVector a = new DatanVector(arg);
         result = muf.getValue(a);
         return result;
	  }
   }



    private class MinLogLikeHistPoisson extends DatanUserFunction{
         Histogram hist;
         double t0, binwidth, fnorm;
         int nbins;

         MinLogLikeHistPoisson(Histogram hist, int nevents){
            this.hist = hist;
            nbins = hist.getNumberOfBins();
            binwidth = hist.getBinSize();
            t0 = hist.getLowerBoundary() + 0.5 * binwidth;
            fnorm = (double)nevents * binwidth;
         }

         public double getValue(DatanVector x){
            double result = 0.;
            for(int i = 0; i < nbins; i++){
               double ti = t0 + (double)i * binwidth;
// gi is the value of the probability density of the population at ti.
// By replacing the following statement it can be changed from normal to any desired distribution.
               double gi = StatFunct.normal(ti, x.getElement(0),  x.getElement(1));
               gi = fnorm * gi;
               double lambda = gi;
               if(lambda > 0.){
                  double loglambda = Math.log(lambda);
                  double ni = hist.getContentsAt(i);
                  double logfactorial = Gamma.logGamma(ni + 1.);
                  result = result + logfactorial - ni * loglambda + lambda;
               }
            }
            return result;
         }
    }
    
    private class MinHistSumOfSquares extends DatanUserFunction{
         Histogram hist;
         double t0, binwidth, fnorm;
         int nbins;

         MinHistSumOfSquares(Histogram hist, int nevents){
            this.hist = hist;
            nbins = hist.getNumberOfBins();
            binwidth = hist.getBinSize();
            t0 = hist.getLowerBoundary() + 0.5 * binwidth;
            fnorm = (double)nevents * binwidth;
         }

         public double getValue(DatanVector x){
            double result = 0.;
            for(int i = 0; i < nbins; i++){
               double ti = t0 + (double)i * binwidth;
               double gi = StatFunct.normal(ti, x.getElement(0),  x.getElement(1));
               gi = fnorm * gi;
               if(hist.getContentsAt(i) > 0.){
                  result = result + Math.pow((hist.getContentsAt(i) - gi), 2.) / hist.getContentsAt(i);
               }
            }
            return result;
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
      new E4Min();
   }

}
