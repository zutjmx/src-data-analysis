package solutions;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events
   
/**
* Data corresponding to 2nd degree polynomial fitted by 1st degree
*/
public class S1Lsq {
   AuxJNumberInput[] ni = new AuxJNumberInput[6];
   DatanFrame df;
   int n, nexp, r;
   double p, x1, x2, x3, sigma, deltay;
   double[] rand, xpl, ypl;
   DatanVector t, y, dy;
   String caption;
   String[] ac, actionCommands;
   Histogram hist;

   public S1Lsq(){
      String s = "Data corresponding to 2nd degree polynomial fitted by 1st degree";
      df = new DatanFrame(getClass().getName(), s);
// parameter input
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("n_exp", "number of experiments (>=10)", errorLabel);
      ni[0].setProperties("n_exp", true, 10);
      ni[0].setNumberInTextField(100);
      ig.add(ni[0]);
      ni[1] = new AuxJNumberInput("x_1", "coefficient of constant term", errorLabel);
      ni[1].setProperties("x_1", false);
      ni[1].setNumberInTextField(1.);
      ig.add(ni[1]);
      ni[2] = new AuxJNumberInput("x_2", "coefficient of linear term", errorLabel);
      ni[2].setProperties("x_2", false);
      ni[2].setNumberInTextField(1);
      ig.add(ni[2]);
      ni[3] = new AuxJNumberInput("x_3", "coefficient of quadratic term term", errorLabel);
      ni[3].setProperties("x_3", false);
      ni[3].setNumberInTextField(.1);
      ig.add(ni[3]);
      ni[4] = new AuxJNumberInput("sigma", "standard deviation in y for generated points (>0)", errorLabel);
      ni[4].setProperties("sigma", false, .000001);
      ni[4].setNumberInTextField(1.);
      ig.add(ni[4]);
      ni[5] = new AuxJNumberInput("deltay", "standard deviation in y used in fit (>0)", errorLabel);
      ni[5].setProperties("deltay", false, .000001);
      ni[5].setNumberInTextField(1.);
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
      x1 = ni[1].parseInput(); 
      x2 = ni[2].parseInput();
      x3 = ni[3].parseInput();
      sigma = ni[4].parseInput();
      deltay = ni[5].parseInput();
      n = 10; // number of data points
      hist = new Histogram(0., .01, 100);
// loop over all experiments
      for(int k = 0; k < nexp; k++){
// generate data points
         t = new DatanVector(n);
         y = new DatanVector(n);
         dy = new DatanVector(n);
         rand = DatanRandom.standardNormal(n);
         for(int i = 0; i < n; i ++){
            t.setElement(i, 1. + (double)i);
            y.setElement(i, x1 + x2 * t.getElement(i) + x3 * t.getElement(i) * t.getElement(i) + sigma * rand[i]);
            dy.setElement(i, deltay);
         }
// perform fit
         LsqPol lp = new LsqPol(t, y, dy, 2);
// compute pobability and enter it in histogram
         if(lp.hasConverged()) p = 1. - StatFunct.cumulativeChiSquared(lp.getChiSquare(), n - 2);
         hist.enter(p);
      }
// curve of constant probability
      xpl = new double[2];
      ypl = new double[2];
      xpl[0] = 0.;
      xpl[1] = 1.;
      ypl[0] = (double) nexp / 100.;
      ypl[1] = ypl[0];
// display histogram
      caption = "Fit pobability for input: x_1#=" + x1 + ", x_2#=" + x2 +  ", x_3#=" + x3 +
         ", &s@=" + sigma +  ", &D@y=" + deltay ;
      new GraphicsWithHistogramAndPolyline(getClass().getName(), "", xpl, ypl, hist, "P", "N(P)", caption);
    }
    
    private class GoButtonListener implements ActionListener { 
        public void actionPerformed(ActionEvent e) {
           if (inputOk()) compute();
       }
    }

   public static void main(String s[]) {
      new S1Lsq();
   }

}
