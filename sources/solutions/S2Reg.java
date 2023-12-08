package solutions;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events

/**
* Polynomial regression on created data. Display of regression line and confidence limits
*/

public class S2Reg {
   int n, r, rfit;
   double sigma, prob;
   double[] x, t, y, deltay, rand;
   AuxJNumberInput[] ni = new AuxJNumberInput[10];
   DatanFrame df;  
   NumberFormat numForm;

   public S2Reg(){  
      numForm = NumberFormat.getNumberInstance(Locale.US);
	   numForm.setMaximumFractionDigits(12);
  	   numForm.setMinimumFractionDigits(12);
      String s = "Polynomial regression on created data. Display of regression line and confidence limits";
      df = new DatanFrame(getClass().getName(), s);
      AuxJInputGroup ig = new AuxJInputGroup("Enter parameters", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("n", "number of data points (>=5, <=25)", errorLabel);
      ni[0].setProperties("n", true, 5, 25);
      ni[0].setNumberInTextField(11);
      ig.add(ni[0]);
      ni[1] = new AuxJNumberInput("r", "number of terms in polynomial (>=1, <=5)", errorLabel);
      ni[1].setProperties("r", true, 1, 5);
      ni[1].setNumberInTextField(3);
      ig.add(ni[1]);
      ni[2] = new AuxJNumberInput("r_fit", "number of parameters for plot of fitted polynomial (>=1, <=n)", errorLabel);
      ni[2].setProperties("r_fit", true, 1);
      ni[2].setNumberInTextField(3);
      ig.add(ni[2]);
      ni[3] = new AuxJNumberInput("x_0", "coefficient of contant term in polynomial", errorLabel);
      ni[3].setProperties("x_0", false);
      ni[3].setNumberInTextField(1);
      ig.add(ni[3]);
      ni[4] = new AuxJNumberInput("x_1", "coefficient of linear term in polynomial", errorLabel);
      ni[4].setProperties("x_1", false);
      ni[4].setNumberInTextField(1);
      ig.add(ni[4]);
      ni[5] = new AuxJNumberInput("x_2", "coefficient of quadratic term in polynomial", errorLabel);
      ni[5].setProperties("x_2", false);
      ni[5].setNumberInTextField(1);
      ig.add(ni[5]);
      ni[6] = new AuxJNumberInput("x_3", "coefficient of cubic term in polynomial", errorLabel);
      ni[6].setProperties("x_3", false);
      ni[6].setNumberInTextField(1);
      ig.add(ni[6]);
      ni[7] = new AuxJNumberInput("x_4", "coefficient of quartic term in polynomial", errorLabel);
      ni[7].setProperties("x_4", false);
      ni[7].setNumberInTextField(1);
      ig.add(ni[7]);
      ni[8] = new AuxJNumberInput("sigma", "error on data points", errorLabel);
      ni[8].setProperties("sigma", false);
      ni[8].setNumberInTextField(.1);
      ig.add(ni[8]);
      ni[9] = new AuxJNumberInput("P", "error on data points (>0, <1)", errorLabel);
      ni[9].setProperties("P", false, 1.E-10, 1. - 1.E-10);
      ni[9].setNumberInTextField(.95);
      ig.add(ni[9]);
      df.add(ig);
      df.add(errorLabel);
      JButton goButton = new JButton("Go");
      GoButtonListener gl = new GoButtonListener();
      goButton.addActionListener(gl);
      df.add(goButton);
      df.repaint();
   }

   protected void compute(){
      n = (int)ni[0].parseInput();
      r = (int)ni[1].parseInput();
      rfit = (int)ni[2].parseInput();
      x = new double[5];
      for(int i = 0; i < 5; i++){
         x[i] = ni[i + 3].parseInput();
      }
      sigma = ni[8].parseInput();
      prob = ni[9].parseInput();
// create data
      t = new double[n];
      y = new double[n];
      deltay = new double[n];
      rand = DatanRandom.standardNormal(n);
      double deltat = 2. / (double)(n - 1);
      for(int i = 0; i < n; i++){
         t[i] = - 1. + (double)i * deltat;
         deltay[i] = sigma;
         y[i] = x[0];
         for(int j = 1; j < r; j++){
            y[i] = y[i] + x[j] * Math.pow(t[i], (double)j); 
         }
         y[i] = y[i] + sigma * rand[i];
      }
// write out data
      df.writeLine("controlled variables t are");
      df.writeLine(new DatanVector(t).toString());
      df.writeLine("measurements y are");
      df.writeLine(new DatanVector(y).toString());
      df.writeLine("measurement errors deltay are");
      df.writeLine(new DatanVector(deltay).toString());
// perform polynomial regression
      Regression reg = new Regression(t, y, deltay, 10);
      df.writeLine("Results of polynomial regression:\n");
      df.writeLine("vector of chi2 values is");
      df.writeLine(reg.getChiSquare().toString());
      df.writeLine("vector of parameter values is");
      df.writeLine(reg.getParameters().toString());
      df.writeLine("---------------------------------------------------------------------------");
      producePlot();
   }

   protected void producePlot(){
// prepare data and fitted curve for plotting
      int npl = 1000;
      double[][] xpl = new double[3][npl];
      double[][] ypl = new double[3][npl];
      double[] datx = new double[n];
      double[] daty = new double[n];
      double[] datsx = new double[n];
      double[] datsy = new double[n];
      double[] datrho = new double[n];
      for(int i = 0; i < n; i++){
         datx[i] = t[i];
         daty[i] = y[i];
         datsx[i] = 0.;
         datsy[i] = deltay[i];
         datrho[i] =0.;
      }
      double del = (t[n - 1] - t[0]) / (double)(npl - 1);
      int[] colorindex = new int[3];
      for(int j = 0; j < 3; j++){
         colorindex[j] = 3;
         if(j ==0) colorindex[j] = 5;
         Regression reg = new Regression(t, y, deltay, rfit);
         for(int i = 0; i < npl; i++){
            xpl[j][i] = t[0] + (double)i * del;
            if(j == 0) ypl[j][i] = reg.regressionLine(xpl[j][i]);
            else if(j == 1) ypl[j][i] = ypl[0][i] + reg.confidenceLimit(xpl[j][i], prob);
            else ypl[j][i] = ypl[0][i] - reg.confidenceLimit(xpl[j][i], prob);
         }
      }
// produce graphics
      String caption = "Polynomial regression, r_fit#=" + rfit + ", P=" + prob;
      double scale = .3;
      String frameTitle = getClass().getName();
      String fileName = getClass().getName() + ".ps";
      new GraphicsWithDataPointsAndMultiplePolylines(frameTitle, fileName, xpl, ypl,
            colorindex, 1, scale, datx, daty, datsx, datsy, datrho, "t", "y", caption);
   }

   

    protected boolean inputOk(){
       boolean ok = true;
       for(int i = 0; i < ni.length; i++){
          if(ni[i].isEnabled()) ok = ok && ni[i].parseOk();
       }
       return ok;
    }

    
    private class GoButtonListener implements ActionListener { 
        public void actionPerformed(ActionEvent e) {
           if (inputOk()) compute();
       }
    }

   public static void main(String s[]) {
      new S2Reg();
   }

}
