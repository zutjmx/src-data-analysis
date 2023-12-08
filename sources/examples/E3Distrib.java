package examples;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events

/**
*  Example simulating simulating Galton's board and demonstratng statistical fluctuation
*/
public class E3Distrib {
   AuxJNumberInput[] ni = new AuxJNumberInput[3];
   DatanFrame df;
   int n, nstep;
   double p;
   double[] sample;
   String[] ac, actionCommands;
   String frametitle, filename;

   public E3Distrib(){
      String s = "Example simulating simulating Galton's board and demonstratng statistical fluctuations";
      df = new DatanFrame(getClass().getName(), s);
// parameter input
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("n_trials", "number of trials (> 0, <= 100000)", errorLabel);
      ig.add(ni[0]);
      ni[0].setProperties("n_trials", true);
      ni[0].setMinimum(1);
      ni[0].setMaximum(100000);
      ni[0].setNumberInTextField(1000);
      ig.add(ni[0]);
      ni[1] = new AuxJNumberInput("n_steps", "number of steps per trial (> 0, <= 100)", errorLabel);
      ni[1].setProperties("n_steps", true);
      ni[1].setMinimum(1);
      ni[1].setMaximum(100);
      ni[1].setNumberInTextField(10);
      ig.add(ni[1]);
      ni[2] = new AuxJNumberInput("P", "Probability of deviation to the right (>= 0., <= 1.)", errorLabel);
      ni[2].setProperties("P", false);
      ni[2].setMinimum(0.);
      ni[2].setMaximum(1.);
      ni[2].setNumberInTextField(.5);
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
      n = (int)ni[0].parseInput();  // number of trials
      nstep = (int)ni[1].parseInput();   // number of steps per trial
      p = ni[2].parseInput();        // probability of deviation to the right
      Histogram hist = new Histogram(-0.5, 1., nstep +1);
// loop over experiments
      for(int i = 0; i < n; i++){
// generate sample
         sample = datan.DatanRandom.ecuy(nstep);
// create, fill, and display first histogram
         int k = 0;
         for(int j = 0; j < nstep; j++){
            if(sample[j] < p) k++;
         }
         hist.enter((double)k, 1.);
      }
      frametitle =  getClass().getName();
      filename = getClass().getName() + ".ps";
      String caption = "Galton board: N_trials#=" + n + ", n_step#=" + nstep + ", P=" + p;
      new GraphicsWithHistogram(frametitle, filename, hist, "k", "N(k)", caption);
    }
    
    private class GoButtonListener implements ActionListener { 
        public void actionPerformed(ActionEvent e) {
           if (inputOk()) compute();
       }
    }

   public static void main(String s[]) {
      new E3Distrib();
   }

}
