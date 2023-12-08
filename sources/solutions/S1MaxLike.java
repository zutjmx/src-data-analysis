package solutions;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events

/**
* Maximum-likelihood estimate of mean life from few decays
*/
public class S1MaxLike {
   AuxJNumberInput[] ni = new AuxJNumberInput[2];
   DatanFrame df;
   int n, nexp, nx;
   double tbar, x0, delx;
   double[] r;
   Histogram histmean, histcumul;
   String line, caption;
   String[] ac, actionCommands;

   public S1MaxLike(){
      String s = "Maximum-likelihood estimate of mean life from few decays";
      df = new DatanFrame(getClass().getName(), s);
// parameter input
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("n_exp", "number of experiments (>= 10)", errorLabel);
      ni[0].setProperties("n_exp", true, 10);
      ni[0].setNumberInTextField(100);
      ig.add(ni[0]);
      ni[1] = new AuxJNumberInput("n", "number of decays per experiment (>=1)", errorLabel);
      ni[1].setProperties("n", true, 1);
      ni[1].setNumberInTextField(5);
      ig.add(ni[1]);
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
      nexp = (int)ni[0].parseInput(); 
      n = (int)ni[1].parseInput();
// initialize histograms
      nx = 100;
      delx = 5. / nx;
      x0 = 0.;
      histmean = new Histogram(x0, delx, nx);
      histcumul = new Histogram(x0, delx, nx);
// loop over experiments; compute tbar for each experiment
      for(int i = 0; i < nexp; i++){
         tbar = 0.;
         r = DatanRandom.ecuy(n);
         for(int j = 0; j < n; j++){
// transform from uniform to exponential
            r[j] = - Math.log(r[j]);
            tbar = tbar + r[j];            
         }
         tbar = tbar / (double)n;
// enter tbar in histogram
         histmean.enter(tbar, 1.);
      }
// construct histogram of cumulative frequency
      double c = 0.;
      for(int i = 0; i < nx; i++){
         c += histmean.getContentsAt(i);
         histcumul.setContentsAt(c, i);
      }
// display histograms
      caption = "Frequency of mean; " + "n_exp#=" + nexp + ", n=" + n;
      new GraphicsWithHistogram(getClass().getName() + " - frequency", "",
         histmean, "t^^\"-", "N(t^^\"-#)", caption);
      caption = "Cumulative frequency of mean; " + "n_exp#=" + nexp + ", n=" + n;
      new GraphicsWithHistogram(getClass().getName() + " - cumulative frequency", "",
         histcumul, "t^^\"-", "F(t^^\"-#)", caption);
    }

   public static void main(String s[]) {
      new S1MaxLike();
   }

}
