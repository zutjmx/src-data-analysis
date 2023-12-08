package solutions;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events

/**
* Convolution of uniform distributions 
*/
public class S1Distrib {
   AuxJNumberInput[] ni = new AuxJNumberInput[2];
   DatanFrame df;
   int n, nexp, nx;
   double x0, delx;
   double[] r;
   Histogram hist;
   NumberFormat numForm;
   String line, caption;
   String[] ac, actionCommands;

   public S1Distrib(){
      String s = "Convolution of uniform distributions";
      df = new DatanFrame(getClass().getName(), s);
// parameter input
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("n_exp", "number of experiments (>= 100)", errorLabel);
      ig.add(ni[0]);
      ni[0].setProperties("n_exp", true, 100);
      ni[0].setNumberInTextField(100);
      ig.add(ni[0]);
      ni[1] = new AuxJNumberInput("n", "number of random numbers per experiment (>= 1)", errorLabel);
      ni[1].setProperties("n", true, 1);
      ni[1].setNumberInTextField(3);
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
      r = convolute(nexp, n);
// create histogram
      nx = 100;
      delx = 10. / nx;
      x0 = -5.;
      hist = new Histogram(x0, delx, nx);
      for(int i = 0; i < nexp; i++){
         hist.enter(r[i], 1.);
      }
// create polyline of standard Gaussian
      int npl = 1001;
      double fact = (double)nexp * delx;
      double dx = ((double)nx * delx) / (double) (npl - 1);
      double[] xpl = new double[npl];
      double[] ypl = new double[npl];
      for(int i = 0; i < npl; i++){
         xpl[i] = x0 + (double)i * dx;
         ypl[i] = fact * StatFunct.standardNormal(xpl[i]);
      }
// display histogram and polyline
      caption = "Convolution of uniform distributions; n_exp#=" + nexp + ", n=" + n;
      new GraphicsWithHistogramAndPolyline(getClass().getName(), "", xpl, ypl, hist, "x", "N(x)", caption);
    }

/**
* Returns nexp random numbers created by convolution
*/
    public double[] convolute(int nexp, int n){
      r = new double[nexp];
      double[] rr;
      double b = Math.sqrt(3. / (double)n);
// loop over all experiments 
      for(int i = 0; i < nexp; i++){
         rr = DatanRandom.ecuy(n);
         r[i] = 0.;
// loop over random numbers for one experiment
         for(int j = 0; j < n; j++){
            r[i] = r[i] + b * (2. * rr[j] - 1.);
         }
      }
      return r;
    }

   public static void main(String s[]) {
      new S1Distrib();
   }

}
