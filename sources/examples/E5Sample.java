package examples;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events

/**
* Example demonstrating the use of the class SmallSample to compute confidence limits on the ratio of signal and reference events in the presence of background
*/
public class E5Sample {
   AuxJNumberInput[] ni = new AuxJNumberInput[4];
   DatanFrame df;
   NumberFormat numForm;
   String[] ac, actionCommands;
   public E5Sample(){
      String s = "Example demonstrating the use of the class SmallSample to compute confidence limits on the ratio of signal and reference events in the presence of background";
      df = new DatanFrame(getClass().getName(), s);
// parameter input
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("k", "number of observed events", errorLabel);
      ni[0].setProperties("k", true);
      ni[0].setMinimum(0);
      ni[0].setNumberInTextField(0);
      ig.add(ni[0]);
      ni[1] = new AuxJNumberInput("d", "number of reference events", errorLabel);
      ni[1].setProperties("d", true);
      ni[1].setMinimum(1);
      ni[1].setNumberInTextField(10);
      ig.add(ni[1]);
      ni[2] = new AuxJNumberInput("beta", "confidence level", errorLabel);
      ni[2].setProperties("beta", false);
      ni[2].setMinimum(0.);
      ni[2].setMaximum(1.);
      ni[2].setNumberInTextField(.9);
      ig.add(ni[2]);
      ni[3] = new AuxJNumberInput("r_background", "expected ratio of background to reference events", errorLabel);
      ni[3].setProperties("r_background", false);
      ni[3].setMinimum(0.0);
      ni[3].setNumberInTextField(0.);
      ig.add(ni[3]);
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
      int d, k;
      double beta, ratioback, ratiominus, ratioplus, ratioupper;
      k = (int)ni[0].parseInput(); 
      d = (int)ni[1].parseInput();   
      beta = ni[2].parseInput();
      ratioback = ni[3].parseInput();
      ratiominus = SmallSample.ratioSignalMinus(k, d, beta, ratioback);
      ratioplus = SmallSample.ratioSignalPlus(k, d, beta, ratioback);
      ratioupper = SmallSample.ratioSignalUpper(k, d, beta, ratioback);  
      numForm = NumberFormat.getNumberInstance(Locale.US);
	   numForm.setMaximumFractionDigits(5);
  	   numForm.setMinimumFractionDigits(5);
      df.writeLine("For k = " + k +  ", d = " + d + ", beta = " + beta + ", ratio_background = " + ratioback + ":");
      df.writeLine("r_signal- = " + numForm.format(ratiominus));
      df.writeLine("r_signal+ = " + numForm.format(ratioplus));
      df.writeLine("r_signal(up) = " + numForm.format(ratioupper));
      df.writeLine("-----------------------------------");
   }
    
   private class GoButtonListener implements ActionListener { 
       public void actionPerformed(ActionEvent e) {
          if (inputOk()) compute();
      }
   }

   public static void main(String s[]) {
      new E5Sample();
   }

}
