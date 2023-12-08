package examples; 

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events
   
/**
* Example producing a random sample from a bivariate Gaussian distribution and presenting it in a 2D scatter diagram
*/
public class E3Sample {
   AuxJNumberInput[] ni = new AuxJNumberInput[6];
   DatanFrame df;
   int n;
   double x0, y0, sigmax, sigmay, rho, xa, xb, ya, yb;
   double[] x, y;
   DatanVector a;
   DatanMatrix c;
   NumberFormat numForm;
   String[] ac, actionCommands;
   String frametitle, filename, caption;

   public E3Sample(){
      String s = "Example producing a 2D scatter diagram of a random sample from a bivariate Gaussian distribution";
      df = new DatanFrame(getClass().getName(), s);
      c = new DatanMatrix(2);
      a = new DatanVector(2);
// parameter input
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("x_0", "mean in x", errorLabel);
      ni[0].setProperties("x_0", false);
      ni[0].setNumberInTextField(0);
      ig.add(ni[0]);
      ni[1] = new AuxJNumberInput("y_0", "mean in y", errorLabel);
      ni[1].setProperties("y_0", false);
      ni[1].setNumberInTextField(0);
      ig.add(ni[1]);
      ni[2] = new AuxJNumberInput("sigma_x", "standard deviation in x (>= 0.)", errorLabel);
      ni[2].setProperties("sigma_x", false);
      ni[2].setMinimum(0.1);
      ni[2].setNumberInTextField(1);
      ig.add(ni[2]);
      ni[3] = new AuxJNumberInput("sigma_y", "standard deviation in y (>= 0.)", errorLabel);
      ni[3].setProperties("sigma_y", false);
      ni[3].setMinimum(0.1);
      ni[3].setNumberInTextField(1);
      ig.add(ni[3]);
      ni[4] = new AuxJNumberInput("rho", "correlation (> -1, < 1)", errorLabel);
      ni[4].setProperties("rho", false);
      ni[4].setMinimum(-.999999);
      ni[4].setMaximum(.999999);
      ni[4].setNumberInTextField(0.8);
      ig.add(ni[4]);
      ni[5] = new AuxJNumberInput("n", "size of sample (>=1. <=1000)", errorLabel);
      ni[5].setProperties("n", true);
      ni[5].setMinimum(1);
      ni[5].setMaximum(1000);
      ni[5].setNumberInTextField(50);
      ig.add(ni[5]);
      df.add(ig);
      df.add(errorLabel);
      JButton goButton = new JButton("Go");
      GoButtonListener gl = new GoButtonListener();
      goButton.addActionListener(gl);
      df.add(goButton);
      df.pack();
      df.toFront();
//      df.repaint();
   }

    protected boolean inputOk(){
       boolean ok = true;
       for(int i = 0; i < ni.length; i++){
          if(ni[i].isEnabled()) ok = ok && ni[i].parseOk();
       }
       return ok;
    }

    protected void compute(){
      n = (int)ni[5].parseInput();  // sample size
      x0 = ni[0].parseInput();
      y0 = ni[1].parseInput();
      sigmax = ni[2].parseInput();
      sigmay = ni[3].parseInput();
      rho = ni[4].parseInput();
      a.setElement(0, x0);
      a.setElement(1, y0);
      c.setElement(0, 0, sigmax * sigmax);
      c.setElement(1, 1, sigmay * sigmay);
      c.setElement(0, 1, sigmax * sigmay * rho);
      c.setElement(1, 0, sigmax * sigmay * rho);
      df.writeLine("Sample of size n = " + n + " drawn from a bivariate Gaussian.");
      df.writeLine("Vector of mean values is: " + a.toString());
      df.writeLine("Covariance matrix is is: \n" + c.toString());
// generate sample
      DatanRandom.setCovarianceMatrixForMultivariateNormal(c);
      x = new double[n];
      y = new double[n];
      for(int i = 0; i < n; i++){
         DatanVector xvec = DatanRandom.multivariateNormal(a);
         x[i] = xvec.getElement(0);
         y[i] = xvec.getElement(1);
      }
      df.writeLine("-------------------------------------------------------------------------------");
// produce scatter diagram
      xa = x0 - 4. * sigmax;
      xb = x0 + 4. * sigmax;
      ya = y0 - 4. * sigmay;
      yb = y0 + 4. * sigmay;      
      frametitle =  getClass().getName();
      filename = getClass().getName() + ".ps";
      caption = "Sample from bivariate Gaussian: n=" + n + ", x_0#=" + x0 + ", y_0#=" + y0 + ", &s@_x#=" + sigmax;
      caption = caption + ", &s@_y#=" + sigmay + ", &r@=" + rho; 
      new GraphicsWith2DScatterDiagram(frametitle, filename , x, y, xa, xb, ya, yb, "x", "y", caption);
    }
    
    private class GoButtonListener implements ActionListener { 
        public void actionPerformed(ActionEvent e) {
           if (inputOk()) compute();
       }
    }

   public static void main(String s[]) {
      new E3Sample();
   }

}
