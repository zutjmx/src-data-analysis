package examples;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events

/**
* Example simulating empirical frequency and demonstrating statistical fluctuations using method DatanRandom.ecuy
*/
public class E1Distrib {
   AuxJNumberInput[] ni = new AuxJNumberInput[3];
   DatanFrame df;
   int n, na, nf;
   double fa, p;
   double[] r;
   String[] ac, actionCommands;
   public E1Distrib(){
      String s = "Example simulating empirical frequency and demonstrating statistical fluctuations using method DatanRandom.ecuy";
      df = new DatanFrame(getClass().getName(), s);
// parameter input
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("n_f", "number of flies per experiment (> 0, <= 10000)", errorLabel);
      ig.add(ni[0]);
      ni[0].setProperties("n_f", true);
      ni[0].setMinimum(1);
      ni[0].setMaximum(10000);
      ni[0].setNumberInTextField(500);
      ig.add(ni[0]);
      ni[1] = new AuxJNumberInput("N", "number of experiments (> 0, <= 500)", errorLabel);
      ni[1].setProperties("N", true);
      ni[1].setMinimum(1);
      ni[1].setMaximum(500);
      ni[1].setNumberInTextField(10);
      ig.add(ni[1]);
      ni[2] = new AuxJNumberInput("P", "Probability to observe property A in individual fly (>= 0., <= 1.)", errorLabel);
      ni[2].setProperties("P", false);
      ni[2].setMinimum(0.);
      ni[2].setMaximum(1.);
      ni[2].setNumberInTextField(.005);
      ig.add(ni[2]);
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
      nf = (int)ni[0].parseInput();  // number of flies
      n = (int)ni[1].parseInput();   // number of experiments
      p = ni[2].parseInput();        // probability for propertiy A in a fly
      df.writeLine("Results for " + n + " experiments with " + nf + " flies having property A with probability P = " + p + ":");
      df.writeLine("    i    na    fa=na/nf");
      for(int i = 1; i <= n; i++){
         r = datan.DatanRandom.ecuy(nf);
         na = 0;
         for(int j = 0; j < nf; j++){
            if(r[j] < p) na++;
         }
         fa = (double)na / (double) nf;
         String outline = String.format(Locale.US, "%5d %5d %10.5f", i, na, fa);
         df.writeLine(outline);
      }
      df.writeLine("------------------------------------------------");
    }

   public static void main(String s[]) {
      new E1Distrib();
   }

}
