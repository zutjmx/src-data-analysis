package examples;

import datan.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events
   
/**
* Example estimating by the maximum-likelihood method the parameters of a bivariate Gaussian
*/
public class E2MaxLike {
   AuxJNumberInput[] ni = new AuxJNumberInput[7];
   DatanFrame df;
   int n, nexp;
   double x10, x20, sigmax1, sigmax2, rho, x1bar, x2bar, sp1sq, sp2sq, sp1, sp2, r;
   double[] x1, x2;
   DatanVector a;
   DatanMatrix c;
   NumberFormat numForm;
   String[] ac, actionCommands;

   public E2MaxLike(){
      String s = "Example estimating by the maximum-likelihood method the parameters of a bivariate Gaussian";
      df = new DatanFrame(getClass().getName(), s);
      c = new DatanMatrix(2);
      a = new DatanVector(2);
// parameter input
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("x_10", "mean in x1", errorLabel);
      ni[0].setProperties("x_10", false);
      ni[0].setNumberInTextField(0);
      ig.add(ni[0]);
      ni[1] = new AuxJNumberInput("x_20", "mean in x2", errorLabel);
      ni[1].setProperties("x_20", false);
      ni[1].setNumberInTextField(0);
      ig.add(ni[1]);
      ni[2] = new AuxJNumberInput("sigma_x1", "standard deviation in x1 (>= 0.)", errorLabel);
      ni[2].setProperties("sigma_x1", false);
      ni[2].setMinimum(0.);
      ni[2].setNumberInTextField(1);
      ig.add(ni[2]);
      ni[3] = new AuxJNumberInput("sigma_x2", "standard deviation in x2 (>= 0.)", errorLabel);
      ni[3].setProperties("sigma_x2", false);
      ni[3].setMinimum(0.);
      ni[3].setNumberInTextField(1);
      ig.add(ni[3]);
      ni[4] = new AuxJNumberInput("rho", "correlation (> -1, < 1)", errorLabel);
      ni[4].setProperties("rho", false);
      ni[4].setMinimum(-.999999);
      ni[4].setMaximum(.999999);
      ni[4].setNumberInTextField(0.8);
      ig.add(ni[4]);
      ni[5] = new AuxJNumberInput("n", "size of sample (>=5 <=10000)", errorLabel);
      ni[5].setProperties("n", true, 5, 10000);
      ni[5].setNumberInTextField(50);
      ig.add(ni[5]);
      ni[6] = new AuxJNumberInput("n_exp", "number of experiments", errorLabel);
      ni[6].setProperties("n_exp", true, 1);
      ni[6].setNumberInTextField(20);
      ig.add(ni[6]);
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
      nexp = (int)ni[6].parseInput();  // number of experiments
      n = (int)ni[5].parseInput();  // sample size
      x10 = ni[0].parseInput();
      x20 = ni[1].parseInput();
      sigmax1 = ni[2].parseInput();
      sigmax2 = ni[3].parseInput();
      rho = ni[4].parseInput();
      a.setElement(0, x10);
      a.setElement(1, x20);
      c.setElement(0, 0, sigmax1 * sigmax1);
      c.setElement(1, 1, sigmax2 * sigmax2);
      c.setElement(0, 1, sigmax1 * sigmax2 * rho);
      c.setElement(1, 0, sigmax1 * sigmax2 * rho);
      df.writeLine(nexp + " samples of size n = " + n + " will be drawn from a bivariate Gaussian.");
      df.writeLine("Vector of mean values is: " + a.toString());
      df.writeLine("Covariance matrix is is: \n" + c.toString());
      df.writeLine("     x1bar      x2bar        sp1        sp2          r");
// loop over al experiments
      for(int k = 0; k < nexp; k++){
// generate sample and compute xbar and ybar
         DatanRandom.setCovarianceMatrixForMultivariateNormal(c);
         x1 = new double[n];
         x2 = new double[n];
         x1bar = 0.;
         x2bar = 0.;
         for(int i = 0; i < n; i++){
            DatanVector xvec = DatanRandom.multivariateNormal(a);
            x1[i] = xvec.getElement(0);
            x2[i] = xvec.getElement(1);
            x1bar = x1bar + x1[i];
            x2bar = x2bar + x2[i];
         }
         x1bar = x1bar / (double) n;
         x2bar = x2bar / (double) n;
// compute variances and correlation
         sp1sq = 0.;
         sp2sq = 0.;
         r = 0.;
         for(int i = 0; i < n; i++){
            sp1sq = sp1sq + Math.pow(x1[i] - x1bar, 2.);
            sp2sq = sp2sq + Math.pow(x2[i] - x2bar, 2.);
            r = r + (x1[i] - x1bar) * (x2[i] - x2bar);
         }
         sp1sq = sp1sq / (double)n;
         sp2sq = sp2sq / (double)n;
         sp1 = Math.sqrt(sp1sq);
         sp2 = Math.sqrt(sp2sq);
         r = r / ((double)n * sp1 * sp2);
         String outline = String.format(Locale.US, "%10.5f %10.5f %10.5f %10.5f %10.5f", x1bar, x2bar, sp1, sp2, r);
         df.writeLine(outline);
      }
      df.writeLine("-------------------------------------------------------------------------------");
    }
    
    private class GoButtonListener implements ActionListener { 
        public void actionPerformed(ActionEvent e) {
           if (inputOk()) compute();
       }
    }

   public static void main(String s[]) {
      new E2MaxLike();
   }

}
