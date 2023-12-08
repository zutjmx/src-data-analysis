package examples;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events

/**
* Example demonstrating the use of the class SmallSample to compute confidence limits on the Poisson parameter in the presence of background
*/
public class E4Sample {
   AuxJNumberInput[] ni = new AuxJNumberInput[3];
   DatanFrame df;
   NumberFormat numForm;
   String[] ac, actionCommands;

   public E4Sample(){
      String s = "Example demonstrating the use of the class SmallSample to compute confidence limits on the Poisson parameter in the presence of background";
      df = new DatanFrame(getClass().getName(), s);
// parameter input
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("k", "size of sample", errorLabel);
      ni[0].setProperties("k", true);
      ni[0].setMinimum(0);
      ni[0].setNumberInTextField(0);
      ig.add(ni[0]);
      ni[1] = new AuxJNumberInput("beta", "confidence level", errorLabel);
      ni[1].setProperties("beta", false);
      ni[1].setMinimum(0.);
      ni[1].setMaximum(1.);
      ni[1].setNumberInTextField(.9);
      ig.add(ni[1]);
      ni[2] = new AuxJNumberInput("lambda_background", "expected Poisson parameter of background", errorLabel);
      ni[2].setProperties("lambda_background", false);
      ni[2].setMinimum(0.0);
      ni[2].setNumberInTextField(0.);
      ig.add(ni[2]);
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
      int k;
      double beta, lambdaback, lambdaminus, lambdaplus, lambdaupper;
      k = (int)ni[0].parseInput();  // sample size
      beta = ni[1].parseInput();
      lambdaback = ni[2].parseInput();
      lambdaminus = SmallSample.lambdaSignalMinus(k, beta, lambdaback);
      lambdaplus = SmallSample.lambdaSignalPlus(k, beta, lambdaback);
      lambdaupper = SmallSample.lambdaSignalUpper(k, beta, lambdaback);  
      numForm = NumberFormat.getNumberInstance(Locale.US);
	   numForm.setMaximumFractionDigits(5);
  	   numForm.setMinimumFractionDigits(5);
      df.writeLine("For k = " + k + ", beta = " + beta + ", lambda_background = " + lambdaback + ":");
      df.writeLine("lambda_signal- = " + numForm.format(lambdaminus));
      df.writeLine("lambda_signal+ = " + numForm.format(lambdaplus));
      df.writeLine("lambda_signal(up) = " + numForm.format(lambdaupper));
      df.writeLine("-----------------------------------");
   }
    
   private class GoButtonListener implements ActionListener { 
       public void actionPerformed(ActionEvent e) {
          if (inputOk()) compute();
      }
   }

   public static void main(String s[]) {
      new E4Sample();
   }

}
