package examples;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events
   
/**
* Example simulating chi-squared tests of measurements
*/
public class E3Test {
   AuxJNumberInput[] ni = new AuxJNumberInput[6];
   DatanFrame df;
   int n, nexp, nh;
   double a, a0, delh, fact, h0, h1chi2, h1p, sigma, sigma0, chi2, p;
   double[] r, xpl, ypl;
   Sample sm;
   Histogram histchi2, histp;
   NumberFormat numForm;
   String[] ac, actionCommands;

   public E3Test(){
      String s = "Example simulating chi-squared tests of measurements";
      df = new DatanFrame(getClass().getName(), s);
// parameter input
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("n_exp", "number of experiments (>0)", errorLabel);
      ni[0].setProperties("n_exp", true, 1);
      ni[0].setNumberInTextField(1000);
      ig.add(ni[0]);
      ni[1] = new AuxJNumberInput("n", "number of measurements per expreiment (>1)", errorLabel);
      ni[1].setProperties("n", true, 2);
      ni[1].setNumberInTextField(100);
      ig.add(ni[1]);
      ni[2] = new AuxJNumberInput("a", "mean for generation", errorLabel);
      ni[2].setProperties("a", false);
      ni[2].setNumberInTextField(1.);
      ig.add(ni[2]);
      ni[3] = new AuxJNumberInput("a_0", "hypothesis for mean", errorLabel);
      ni[3].setProperties("a0", false);
      ni[3].setNumberInTextField(1.);
      ig.add(ni[3]);
      ni[4] = new AuxJNumberInput("sigma", "standard deviation for generation (> 0.)", errorLabel);
      ni[4].setProperties("sigma", false, 0.);
      ni[4].setNumberInTextField(1.);
      ig.add(ni[4]);
      ni[5] = new AuxJNumberInput("sigma_0", "hypothesis for standard deviation (> 0.)", errorLabel);
      ni[5].setProperties("sigma_0", false, 0.);
      ni[5].setNumberInTextField(0.95);
      ig.add(ni[5]);
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
      nexp = (int)ni[0].parseInput();  // number of experiments
      n = (int)ni[1].parseInput();  // size of sample
      a = ni[2].parseInput();  // mean for generation
      a0 = ni[3].parseInput(); // hypothesis for mean
      sigma = ni[4].parseInput(); // standard deviation for generation
      sigma0 = ni[5].parseInput();  // hypothesis for standard deviation
// initialize histograms for chi squared and for probability
      h0 = 0.;
      h1chi2 = (double)(4 * n);
      nh = 100;
      delh = (h1chi2 - h0) / (double) nh;
      histchi2 = new Histogram(h0, delh, nh);
      h1p = 1.;      
      delh = (h1p - h0) / (double) nh;
      histp = new Histogram(h0, delh, nh);
// loop over al experiments
      for(int k = 0; k < nexp; k++){
// generate samples
         r = DatanRandom.standardNormal(n);
         for(int i = 0; i < n; i++){
            r[i] = a + r[i] * sigma;
         }
// compute chi squared
         chi2 = 0.;
         for(int i = 0; i < n; i++){
            chi2 = chi2 + Math.pow((r[i] - a0) / sigma0, 2.);
         }
// compute probability
         p = 1. - StatFunct.cumulativeChiSquared(chi2, n);
// fill in histograms
         histchi2.enter(chi2, 1.);
         histp.enter(p, 1.);
      }
// prepare polyline showing expectation for probability
      fact = (double)nexp / (double)nh;
      xpl = new double[2];
      ypl = new double[2];
      xpl[0] = h0;
      xpl[1] = h1p;
      ypl[0] = fact;
      ypl[1] = fact;
      numForm = NumberFormat.getNumberInstance(Locale.US);
	   numForm.setMaximumFractionDigits(3);
  	   numForm.setMinimumFractionDigits(0);
      String caption = "n_exp#=" + nexp + ", n=" + n + ", &s=" + numForm.format(sigma)
         + ", &s@_0#=" + numForm.format(sigma0) + ", a=" + numForm.format(a) + ", a_0#=" + numForm.format(a0);
      new GraphicsWithHistogramAndPolyline(getClass().getName(), "", xpl, ypl, histp, "P(&c@^2#)", "N(P)", caption);
// prepare polyline showing expectation for chi squared
      fact = (h1chi2 - h0) * (double)nexp / (double)nh;
      xpl = new double[1001];
      ypl = new double[1001];
      double del = .001 * (h1chi2 - h0);
      for(int i = 0; i < 1001; i++){
         xpl[i] = (double)i * del;
         ypl[i] = fact * StatFunct.chiSquared(xpl[i], n);         
      }
      new GraphicsWithHistogramAndPolyline(getClass().getName(), "", xpl, ypl, histchi2, "&c@^2", "N(&c@^2#)", caption);
    }
    
    private class GoButtonListener implements ActionListener { 
        public void actionPerformed(ActionEvent e) {
           if (inputOk()) compute();
       }
    }

   public static void main(String s[]) {
      new E3Test();
   }

}
