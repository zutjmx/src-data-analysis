package examples;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events

/**
* Example simulating experiments with few events and background
*/
public class E6Sample {
   AuxJNumberInput[] ni = new AuxJNumberInput[5];
   DatanFrame df;
   NumberFormat numForm;
   String[] ac, actionCommands;
   public E6Sample(){
      String s = "Example simulating experiments with few events and background";
      df = new DatanFrame(getClass().getName(), s);
// parameter input
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("n_exp", "number of experiments (>0)", errorLabel);
      ni[0].setProperties("nexp", true, 1);
      ni[0].setNumberInTextField(20);
      ig.add(ni[0]);
      ni[1] = new AuxJNumberInput("n", "number of studied objects (>>1)", errorLabel);
      ni[1].setProperties("n", true, 1);
      ni[1].setNumberInTextField(1000);
      ig.add(ni[1]);
      ni[2] = new AuxJNumberInput("lambda_signal", "Poisson parameter for signal events (>0)", errorLabel);
      ni[2].setProperties("lambda_signal", false, 0.);
      ni[2].setNumberInTextField(5);
      ig.add(ni[2]);
      ni[3] = new AuxJNumberInput("lambda_background", "Poisson parameter for background events", errorLabel);
      ni[3].setProperties("lambda_background", false, 0.);
      ni[3].setNumberInTextField(1);
      ig.add(ni[3]);
      ni[4] = new AuxJNumberInput("beta", "confidence level", errorLabel);
      ni[4].setProperties("beta", false, 0., 1.);
      ni[4].setNumberInTextField(.9);
      ig.add(ni[4]);
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
      int n, nexp, ks, kb, k;
      double beta, lambdasig, lambdaback, lambdasminus, lambdasplus, lambdasup, p, psig, pback;
      double[] r;
      nexp = (int)ni[0].parseInput(); 
      n = (int)ni[1].parseInput();
      lambdasig = ni[2].parseInput();
      lambdaback = ni[3].parseInput();   
      beta = ni[4].parseInput();
      psig = lambdasig / (double)n;
      pback = lambdaback / (double)n;
      p = psig + pback;  
      numForm = NumberFormat.getNumberInstance(Locale.US);
	   numForm.setMaximumFractionDigits(5);
  	   numForm.setMinimumFractionDigits(0);
      df.writeLine(nexp + " experiments are simulated with");
      df.writeLine("lambda_signal = " + numForm.format(lambdasig) + ", i. e., P_signal = " + numForm.format(psig) + ",");
      df.writeLine("lambda_back = " + numForm.format(lambdaback) + ", i. e., P_back = " + numForm.format(pback));
      df.writeLine("limits for lambda_signal are obtained at a confidence level of" + numForm.format(beta));
      df.writeLine("    i   k_s   k_b     k  lambda_s-  lambda_s+  lambda_s(up)");
// loop over simulated experiments
      for(int i = 1; i <= nexp; i++){
         k = 0;
         ks = 0;
         kb = 0;
         for(int j = 0; j < n; j++){
            r = DatanRandom.ecuy(1);
            if(r[0] < p){
               k++;
               if(r[0] < psig)ks++;
               else kb++;
            }
         }
//compute confidence limits
         lambdasminus = SmallSample.lambdaSignalMinus(k, beta, lambdaback);
         lambdasplus = SmallSample.lambdaSignalPlus(k, beta, lambdaback);
         lambdasup = SmallSample.lambdaSignalUpper(k, beta, lambdaback);
         String outline = String.format(Locale.US, "%5d %5d %5d %5d %10.5f %10.5f %10.5f",
            i, ks, kb, k, lambdasminus, lambdasplus, lambdasup);
         df.writeLine(outline);
      }
      df.writeLine("-----------------------------------");
   }
    
   private class GoButtonListener implements ActionListener { 
       public void actionPerformed(ActionEvent e) {
          if (inputOk()) compute();
      }
   }

   public static void main(String s[]) {
      new E6Sample();
   }

}
