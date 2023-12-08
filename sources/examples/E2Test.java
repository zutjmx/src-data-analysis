package examples;

import datan.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events
   
/**
* Example simulating a Gaussian sample and performing a t-test on its mean
*/
public class E2Test {
   AuxJNumberInput[] ni = new AuxJNumberInput[6];
   DatanFrame df;
   int n, nexp;
   double beta, c, x0, lambda0, sigma, t, xmean, xvar, q;
   double[] r;
   Sample sm;
   NumberFormat numForm;
   String[] ac, actionCommands;

   public E2Test(){
      String s = "Example simulating a Gaussian sample and performing a t-test on its mean";
      df = new DatanFrame(getClass().getName(), s);
// parameter input
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("n_exp", "number of experiments (>0)", errorLabel);
      ni[0].setProperties("n_exp", true, 1);
      ni[0].setNumberInTextField(10);
      ig.add(ni[0]);
      ni[1] = new AuxJNumberInput("n", "size of sample (>1)", errorLabel);
      ni[1].setProperties("n", true, 2);
      ni[1].setNumberInTextField(100);
      ig.add(ni[1]);
      ni[2] = new AuxJNumberInput("x_0", "mean of Gaussian", errorLabel);
      ni[2].setProperties("x_0", false);
      ni[2].setNumberInTextField(0.);
      ig.add(ni[2]);
      ni[3] = new AuxJNumberInput("sigma", "standard deviation of Gaussian (> 0)", errorLabel);
      ni[3].setProperties("sigma", false, 0.);
      ni[3].setNumberInTextField(1.);
      ig.add(ni[3]);
      ni[4] = new AuxJNumberInput("lambda_0", "hypothesis for sample mean", errorLabel);
      ni[4].setProperties("lambda_0", false, 0.);
      ni[4].setNumberInTextField(0.2);
      ig.add(ni[4]);
      ni[5] = new AuxJNumberInput("beta", "confidence limit (> 0., < 1.)", errorLabel);
      ni[5].setProperties("beta", false, 0.);
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
      x0 = ni[2].parseInput();  // mean of Gaussian
      sigma = ni[3].parseInput(); // standard deviation of Gaussian
      lambda0 = ni[4].parseInput(); // hypothesis for mean
      beta = ni[5].parseInput();  // confidence level
      c = 0.5 * (1. + beta);
      df.writeLine("    n      xmean          t          Q");
// loop over al experiments
      for(int k = 0; k < nexp; k++){
// generate samples
         r = DatanRandom.standardNormal(n);
         for(int i = 0; i < n; i++){
            r[i] = x0 + r[i] * sigma;
         }
// compute sample mean and variance and t
         sm = new Sample(r);
         xmean = sm.getMean();
         xvar = sm.getVariance();
         t = (xmean - lambda0) * Math.sqrt((double)n / xvar);
         q = StatFunct.quantileStudent(c, n - 1);
         String outline = String.format(Locale.US, "%5d %10.5f %10.5f %10.5f", n, xmean, t, q);
         if(Math.abs(t) > q) outline = outline + " t-test failed";
         df.writeLine(outline);
      }
      df.writeLine("-------------------------------------------------------------------------------");
    }
    
    private class GoButtonListener implements ActionListener { 
        public void actionPerformed(ActionEvent e) {
           if (inputOk()) compute();
       }
    }

   public static void main(String s[]) {
      new E2Test();
   }

}
