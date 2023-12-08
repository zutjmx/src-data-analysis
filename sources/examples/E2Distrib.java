package examples; 

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events

/**
* Example simulating the experiment by Rutherford and Geiger on the statistical nature of radioactive decays
*/
public class E2Distrib {
   AuxJNumberInput[] ni = new AuxJNumberInput[2];
   DatanFrame df;
   int n, nint, nf, idealnf, nf0;
   double delf, f0;
   double[] sample;
   String[] ac, actionCommands;
   String frametitle, filename;

   public E2Distrib(){
      String s = "Example simulating the experiment by Rutherford and Geiger on the statistical nature of radioactive decays";
      df = new DatanFrame(getClass().getName(), s);
// parameter input
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("N", "number of observed deays (> 0, <= 100000)", errorLabel);
      ig.add(ni[0]);
      ni[0].setProperties("N", true);
      ni[0].setMinimum(1);
      ni[0].setMaximum(100000);
      ni[0].setNumberInTextField(1000);
      ig.add(ni[0]);
      ni[1] = new AuxJNumberInput("n_int", "number of intervals (> 0, <= 100000)", errorLabel);
      ni[1].setProperties("n_int", true);
      ni[1].setMinimum(1);
      ni[1].setMaximum(100000);
      ni[1].setNumberInTextField(500);
      ig.add(ni[1]);
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
      n = (int)ni[0].parseInput();  // number of events
      nint = (int)ni[1].parseInput();   // number of intervals
// generate sample
      sample = datan.DatanRandom.ecuy(n);
// create, fill, and display first histogram
      Histogram hist = new Histogram(0., 1., nint);
      for(int i = 0; i < n; i++){
         hist.enter(sample[i] * (double)nint, 1.);
      }
      frametitle =  getClass().getName();
      filename = getClass().getName() + "-1.ps";
      String caption = "N =" + n + ", n_int#=" + nint;
      new GraphicsWithHistogram(frametitle, filename, hist, "t", "N(t)=k", caption);
// analyze first histogram and create second histogram
      nf = (int)((double)n / (double)nint);
      idealnf = (int)Math.sqrt((double)nf) + 1;
      nf0 = Math.max(0, nf - 5 * idealnf);
      nf = nf + 5 * idealnf;
      f0 = (double)nf0 -.5;
      delf = 1.;
      nf = Math.min(nf, 1000);
      Histogram hist1 = new Histogram(f0, delf, nf);
      for(int k = 0; k < nf; k++){
         double fk = 0.;
         for(int j = 0; j < nint; j++){
            if(hist.getContentsAt(j) == k) fk++;  
         }
         hist1.enter((double)k, fk);
      }
      filename = getClass().getName() + "-2.ps";
      new GraphicsWithHistogram(frametitle, filename, hist1, "k", "N(k)", caption);
    }
    
    private class GoButtonListener implements ActionListener { 
        public void actionPerformed(ActionEvent e) {
           if (inputOk()) compute();
       }
    }

   public static void main(String s[]) {
      new E2Distrib();
   }

}
