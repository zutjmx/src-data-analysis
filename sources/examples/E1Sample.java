package examples;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events

/**
* Example drawing a sample from the standard noraml distribution and computing various sample quantities
*/
public class E1Sample {
   AuxJNumberInput[] ni = new AuxJNumberInput[1];
   DatanFrame df;
   int n;
   NumberFormat numForm;
   String[] ac, actionCommands;
   public E1Sample(){  
      numForm = NumberFormat.getNumberInstance(Locale.US);
	   numForm.setMaximumFractionDigits(6);
  	   numForm.setMinimumFractionDigits(6);
      String s = "Example drawing a sample from the standard noraml distribution and computing various sample quantities";
      df = new DatanFrame(getClass().getName(), s);
// parameter input
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("n", "sample size (> 0, <= 10000)", errorLabel);
      ig.add(ni[0]);
      ni[0].setProperties("n", true, 1, 10000);
      ni[0].setNumberInTextField(100);
      ig.add(ni[0]);
      df.add(ig);
      df.add(errorLabel);
      JButton goButton = new JButton("Go");
      goButton.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e) {
            if (inputOk()) compute();
         }
      });
      df.add(goButton);
   }

    private boolean inputOk(){
       boolean ok = true;
       for(int i = 0; i < ni.length; i++){
          if(ni[i].isEnabled()) ok = ok && ni[i].parseOk();
       }
       return ok;
    }

    private void compute(){

       double[] data;
       n = (int)ni[0].parseInput();  // sample size
	    data = DatanRandom.standardNormal(n);
	    Sample s = new Sample(data);
	    df.writeLine("For a sample from the srandard Gaussian of size n = " + n + ":");
	    df.writeLine("sample mean = " + String.format(Locale.US, "%10.5f", s.getMean()));
	    df.writeLine("error of sample mean = " + String.format(Locale.US, "%10.5f", s.getErrorOfMean()));
	    df.writeLine("sample variance = " + String.format(Locale.US, "%10.5f", s.getVariance()));
	    df.writeLine("error of sample variance = " + String.format(Locale.US, "%10.5f", s.getErrorOfVariance()));
	    df.writeLine("standard deviation of the sample = " + String.format(Locale.US, "%10.5f", s.getStandardDeviation()));
	    df.writeLine("error of the standard deviation of the sample = " + String.format(Locale.US, "%10.5f", s.getErrorOfStandardDeviation()));
       df.writeLine("------------------------------------------------");
   }

   public static void main(String s[]) {
      new E1Sample();
   }

}
