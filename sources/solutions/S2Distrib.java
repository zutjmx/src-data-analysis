package solutions;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events

/**
* Convolution of uniform distribution with Gaussian
*/
public class S2Distrib {
   AuxJNumberInput[] ni = new AuxJNumberInput[4];
   DatanFrame df;
   int nexp, nx;
   double a, b, sigma, x0, delx;
   double[] r;
   Histogram hist;
   NumberFormat numForm;
   String line, caption;
   String[] ac, actionCommands;

   public S2Distrib(){
      String s = "Convolution of uniform distribution with Gaussian";
      df = new DatanFrame(getClass().getName(), s);
// parameter input
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("n_exp", "number of experiments (>= 100)", errorLabel);
      ni[0].setProperties("n_exp", true, 100);
      ni[0].setNumberInTextField(100);
      ig.add(ni[0]);
      ni[1] = new AuxJNumberInput("a", "lower limit of unif. distr.", errorLabel);
      ni[1].setProperties("a", false);
      ni[1].setNumberInTextField(-10.);
      ig.add(ni[1]);
      ni[2] = new AuxJNumberInput("b", "upper limit of unif. distr.", errorLabel);
      ni[2].setProperties("b", false);
      ni[2].setNumberInTextField(10);
      ig.add(ni[2]);
      ni[3] = new AuxJNumberInput("sigma", "width of Gaussian (> 0)", errorLabel);
      ni[3].setProperties("sigma", false, 1.E-10);
      ni[3].setNumberInTextField(3.);
      ig.add(ni[3]);
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
      a = ni[1].parseInput(); 
      b = ni[2].parseInput();     
      sigma = ni[3].parseInput();        
      r = convolute(nexp, a, b, sigma);
// create histogram
      nx = 100;
      delx = (b - a + 6. * sigma) / nx;
      x0 = a - 3. * sigma;
      hist = new Histogram(x0, delx, nx);
      for(int i = 0; i < nexp; i++){
         hist.enter(r[i], 1.);
      }
// create polyline
      int npl = 1001;
      double fact = (double)nexp * delx / (b - a);
      double dx = ((double)nx * delx) / (double) (npl - 1);
      double[] xpl = new double[npl];
      double arg1, arg2; 
      double[] ypl = new double[npl];
      for(int i = 0; i < npl; i++){
         xpl[i] = x0 + (double)i * dx;
         arg1 = (b - xpl[i]) / sigma;
         arg2 = (a - xpl[i]) / sigma;
         ypl[i] = fact * (StatFunct.cumulativeStandardNormal(arg1) - StatFunct.cumulativeStandardNormal(arg2));
      }
// display histogram and polyline
      caption = "Convolution of uniform and Gaussian; n_exp#=" + nexp + ", a=" + a + ", b=" + b + ", &s=" + sigma;
      new GraphicsWithHistogramAndPolyline(getClass().getName(), "", xpl, ypl, hist, "x", "N(x)", caption);
    }

/**
* Returns nexp random numbers created by convolution uniform distribution with Gaussian
*/
    public double[] convolute(int nexp, double a, double b, double sigma){
      r = new double[nexp];
      double[] runif = DatanRandom.ecuy(nexp);
      double[] rnorm = DatanRandom.standardNormal(nexp);
// loop over all experiments 
      for(int i = 0; i < nexp; i++){
         r[i] = a + (b - a) * runif[i] + sigma * rnorm[i];
      }
      return r;
    }

   public static void main(String s[]) {
      new S2Distrib();
   }

}
