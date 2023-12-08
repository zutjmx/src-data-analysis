package solutions;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events
   
/**
* Fit of a Breit-Wigner function to a histogram
*/
public class S6Lsq {
   AuxJNumberInput[] ni = new AuxJNumberInput[2];
   DatanFrame df;
   int nev, nt, r;
   double m, x1, x2, x3, t0, deltat, delx1, delx2, delx3, dpl, p, fact;
   double[] rand, xpl, ypl;
   DatanVector c, t, y, dy, x;
   DatanMatrix cov;
   Histogram hist;
   DatanUserFunction bwf;
   String caption;

   public S6Lsq(){
      String s = "Fit of a Breit-Wigner function to a histogram";
      df = new DatanFrame(getClass().getName(), s);
// parameter input
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("n_ev", "number of events (>=20)", errorLabel);
      ni[0].setProperties("n_ev", true, 20);
      ni[0].setNumberInTextField(100);
      ig.add(ni[0]);
      ni[1] = new AuxJNumberInput("n_t", "number of histogram bins (>=5, <=100)", errorLabel);
      ni[1].setProperties("n_t", true, 5, 100);
      ni[1].setNumberInTextField(10);
      ig.add(ni[1]);
      df.add(ig);
      df.add(errorLabel);
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
      nev = (int)ni[0].parseInput(); 
      nt = (int)ni[1].parseInput();
// generate data and fill histogram
      rand = randomBreitWigner(0., 1., nev);
      t0 = -3.;
      deltat = 2.* Math.abs(t0) / (double) nt;
      hist = new Histogram(t0, deltat, nt);
      for(int i = 0; i < nev; i ++){
         hist.enter(rand[i]);
      }
// analyze histogram and prepare data for fit
      int npos = 0;
      for(int j = 0; j < nt; j++){
         if(hist.getContentsAt(j) > 0.) npos++;
      }
      t = new DatanVector(npos);
      y = new DatanVector(npos);
      dy = new DatanVector(npos);
      int ipos = 0;
      for(int j = 0; j < nt; j++){
         if(hist.getContentsAt(j) > 0.){
            t.setElement(ipos, t0 + ((double) j + 0.5) * deltat);
            y.setElement(ipos, hist.getContentsAt(j));
            dy.setElement(ipos, Math.sqrt(hist.getContentsAt(j)));
            ipos++;
         }
      }
// set first approximation of unknowns
      r = 3;
      fact = 2. * Math.abs(t0) * hist.getTotalContents() / (double)nt;
      x = new DatanVector(r);
      x.setElement(0, 0.5);
      x.setElement(1, 0.5);
      x.setElement(2, 0.5);
// perform fit
      int[] list = {1, 1, 1};
      bwf = new BWFunct();
      LsqNon ln = new LsqNon(t, y, dy, x, list, bwf);
      x = ln.getResult();
      if(ln.hasConverged()){
      cov = ln.getCovarianceMatrix();
      m = ln.getChiSquare();
      x1 = x.getElement(0);
      x2 = x.getElement(1);
      x3 = x.getElement(2);
      delx1 = Math.sqrt(cov.getElement(0, 0));
      delx2 = Math.sqrt(cov.getElement(1, 1));
      delx3 = Math.sqrt(cov.getElement(2, 2));
      p = 1. - StatFunct.cumulativeChiSquared(m, npos - r);
// curve of fitted Breit-Wigner
      xpl = new double[1001];
      ypl = new double[1001];
      dpl = (double)nt * deltat / 1000.;
      for(int i = 0; i < 1001; i++){
         xpl[i] = t0 + (double)i * dpl;
         ypl[i] = bwf.getValue(x, xpl[i]);
      }
// display data and fitted curve
      caption = "x_1#=" + String.format(Locale.US, "%4.2f", x1)
         + ", x_2#=" + String.format(Locale.US, "%4.2f", x2)
         + ", x_3#=" + String.format(Locale.US, "%4.2f", x3)
         + ", &D@x_1#=" + String.format(Locale.US, "%4.2f", delx1)
         + ", &D@x_2#=" + String.format(Locale.US, "%4.2f", delx2)
         + ", &D@x_3#=" + String.format(Locale.US, "%4.2f", delx3)
         +  ", M=" + String.format(Locale.US, "%4.2f", m)
         + ", P=" + String.format(Locale.US, "%6.4f", p);
      new GraphicsWithHistogramAndPolyline(getClass().getName(), "", xpl, ypl,
         hist, "t", "y", caption);
      }
      else{
         df.writeLine("No convergence!!!!!!!!!!!!");
      }
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
    
    private class GoButtonListener implements ActionListener { 
        public void actionPerformed(ActionEvent e) {
           if (inputOk()) compute();
       }
    }

    private class BWFunct extends DatanUserFunction{
       public double getValue(DatanVector x, double t){
          return (2. * fact * x.getElement(2) * x.getElement(1) / Math.PI)
             / (4. * Math.pow((t - x.getElement(0)), 2.) + Math.pow(x.getElement(1), 2.));
	    }
    }

   public static void main(String s[]) {
      new S6Lsq();
   }

}
