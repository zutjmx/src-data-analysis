package examples;

import datan.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events
   
/**
* Example simulating 2 Gaussian samples and performing an F-test for equality of variances
*/
public class E1Test {
   AuxJNumberInput[] ni = new AuxJNumberInput[6];
   DatanFrame df;
   int n1, n2, nexp, ng, nk;
   double beta, c, f, q, sigma1, sigma2, s1sq, s2sq, sgsq, sksq;
   double[] r1, r2;
   Sample sm;
   NumberFormat numForm;
   String[] ac, actionCommands;

   public E1Test(){
      String s = "Example simulating 2 Gaussian samples and performing an F-Test for equality of variances";
      df = new DatanFrame(getClass().getName(), s);
// parameter input
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("n_exp", "number of experiments (>0)", errorLabel);
      ni[0].setProperties("n_exp", true, 1);
      ni[0].setNumberInTextField(20);
      ig.add(ni[0]);
      ni[1] = new AuxJNumberInput("n_1", "number of elements in 1st sample (>=2, <=1000)", errorLabel);
      ni[1].setProperties("n_1", true, 2, 1000);
      ni[1].setNumberInTextField(100);
      ig.add(ni[1]);
      ni[2] = new AuxJNumberInput("n_2", "number of elements in 2nd sample (>=2, <=1000)", errorLabel);
      ni[2].setProperties("n_2", true, 2, 1000);
      ni[2].setNumberInTextField(50);
      ig.add(ni[2]);
      ni[3] = new AuxJNumberInput("sigma1", "standard deviation for 1st sample (>= 0.)", errorLabel);
      ni[3].setProperties("sigma1", false, 0.);
      ni[3].setNumberInTextField(1);
      ig.add(ni[3]);
      ni[4] = new AuxJNumberInput("sigma2", "standard deviation for 2ns sample (>= 0.)", errorLabel);
      ni[4].setProperties("sigma2", false, 0.);
      ni[4].setNumberInTextField(0.8);
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
      n1 = (int)ni[1].parseInput();  // size of 1st sample
      n2 = (int)ni[2].parseInput();  // size of 1st sample
      sigma1 = ni[3].parseInput();
      sigma2 = ni[4].parseInput();
      beta = ni[5].parseInput();
      c = 0.5 * (1. + beta);
      df.writeLine("   ng    nk       sgsq       sksq          F          Q");
// loop over all experiments
      for(int k = 0; k < nexp; k++){
// generate samples
         r1 = DatanRandom.standardNormal(n1);
         for(int i = 0; i < n1; i++){
            r1[i] = r1[i] * sigma1;
         }
         r2 = DatanRandom.standardNormal(n2);
         for(int i = 0; i < n2; i++){
            r2[i] = r2[i] * sigma2;
         }
// compute variances and F quotient
         sm = new Sample(r1);
         s1sq = sm.getVariance();
         sm = new Sample(r2);
         s2sq = sm.getVariance();
         if(s1sq > s2sq){
            sgsq = s1sq;
            sksq = s2sq;
            ng = n1;
            nk = n2;
         }
         else{
            sgsq = s2sq;
            sksq = s1sq;
            ng = n2;
            nk = n1;
         }
         f = sgsq / sksq;
         q = StatFunct.quantileFDistribution(c, ng - 1, nk - 1);
         String outline = String.format(Locale.US, "%5d %5d %10.5f %10.5f %10.5f %10.5f", ng, nk, sgsq, sksq, f, q);
         if(f > q) outline = outline + " F-test failed";
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
      new E1Test();
   }

}
