package solutions;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events
   
/**
* Fitting a Breit-Wigner function to small sample and determining errors of parameters by MinCov
*/
public class S2Min {
   int nny;
   AuxJNumberInput[] ni = new AuxJNumberInput[3];
   DatanFrame df;  
   NumberFormat numForm;
   DatanVector x, x0;
   DatanMatrix cx;
   double a, gamma;
   double[] y;
   double[][] dxasy;
   String caption;
   MinLogLikeBreitWigner mllbw;
   Sample smpl;

   public S2Min(){  
      numForm = NumberFormat.getNumberInstance(Locale.US);
	   numForm.setMaximumFractionDigits(12);
  	   numForm.setMinimumFractionDigits(12);
      String s = "Fitting a Breit-Wigner function to small sample and determining errors of parameters by MinCov";
      df = new DatanFrame(getClass().getName(), s);
      AuxJInputGroup ig = new AuxJInputGroup("Enter number N of events (>= 2, <= 10000)", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("n", "number of events (>=1, <=10000)", errorLabel);
      ig.add(ni[0]);
      ni[0].setProperties("n", true, 2, 10000);
      ni[0].setNumberInTextField(10);
      ni[1] = new AuxJNumberInput("a", "mean for generation of sample", errorLabel);
      ig.add(ni[1]);
      ni[1].setProperties("a", false);
      ni[1].setNumberInTextField(0.);
      ni[2] = new AuxJNumberInput("Gamma", "full width at half maximum for generation (>0)", errorLabel);
      ig.add(ni[2]);
      ni[2].setProperties("Gamma", true, .00001);
      ni[2].setNumberInTextField(1.);
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
      a = ni[1].parseInput();
      gamma = ni[2].parseInput();
// generate data
      y = randomBreitWigner(a, gamma, nny);
      df.writeLine("Number of events is: n = " + nny);
      mllbw = new MinLogLikeBreitWigner(y);
// determine first approximation
      smpl = new Sample(y);
      x0 = new DatanVector(2);
      x0.setElement(0, smpl.getMean());
      x0.setElement(1, smpl.getStandardDeviation());
      df.writeLine("First approximation of a and gamma is: ");
      df.writeLine(x0.toString());
      MinSim ms = new MinSim(x0, mllbw);
      x = ms.getMinPosition();
      df.writeLine("Minimization with MinSim yields x = ");
      df.writeLine(x.toString());
// covariance matrix
      MinCov mc = new MinCov(x, mllbw);
      cx = mc.getCovarianceMatrix(1.);
      df.writeLine("Covariance Matrix cx = ");
      df.writeLine(cx.toString());
      plotScatterDiagram();
    }

    protected void plotScatterDiagram(){
// plot sample as one dimensional scatter plot and Gaussian
      double xmax = a + 5. * gamma;
      double xmin = a - 5. * gamma;
      DatanGraphics.openWorkstation(getClass().getName(), "");
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
// draw Breit-Wigner corresponding to solution
      int npl = 100;
      xpl = new double[npl];
      ypl = new double[npl];
      double dpl = (xmax - xmin) / (double)(npl - 1);
      for(int i = 0; i < npl; i++){
         xpl[i] = xmin + (double)i * dpl;
         ypl[i] = breitWigner(xpl[i], x.getElement(0),  x.getElement(1));
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
    
/**
* Returns n random numbers following a Breit-Wigner distribution with mean a and FWHM gamma
*/
    public double[] randomBreitWigner(double a, double gamma, int n){
      double[] r = DatanRandom.ecuy(n);
      for(int i = 0; i < n; i++){
         r[i] = a + 0.5 * gamma * Math.tan(Math.PI * (r[i] - 0.5));
      }
      return r;
    }

/**
* Returns a Breit-Wigner function
*/
    public double breitWigner(double arg, double a, double gamma){      
      return (2. * gamma / Math.PI) / (4. * Math.pow(arg - a, 2.) + gamma * gamma);
    }



    
    private class GoButtonListener implements ActionListener { 
        public void actionPerformed(ActionEvent e) {
           if (inputOk()) compute();
       }
    }

    

    private class MinLogLikeBreitWigner extends DatanUserFunction{
         double[] y;
         double f;
         MinLogLikeBreitWigner(double[] y){
            this.y = y;
         }
         public double getValue(DatanVector x){
            double f, result;
            f = (double)y.length * (Math.log(2.) - Math.log(Math.PI) + Math.log(Math.abs(x.getElement(1))));
            for(int i = 0; i < y.length; i++){
               f = f - Math.log(4. * Math.pow(y[i] - x.getElement(0), 2.) + Math.pow(x.getElement(1), 2.));
            }
            return - f;
         }
    }


   public static void main(String s[]) {
      new S2Min();
   }

}
