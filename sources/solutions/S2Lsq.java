package solutions;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events
   
/**
* Data fitted to power law (linear case)
*/
public class S2Lsq {
   AuxJNumberInput[] ni = new AuxJNumberInput[6];
   DatanFrame df;
   int n;
   double m, x, w, sigma, t0, deltat, dpl;
   double[] rand, xpl, ypl, datx, daty, datsx, datsy, datrho;
   DatanVector c, t, y, dy, xres;
   DatanMatrix a, cov;
   String caption;
   String[] ac, actionCommands;

   public S2Lsq(){
      String s = "Data fitted to power law (linear case)";
      df = new DatanFrame(getClass().getName(), s);
// parameter input
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("n", "number of data points (>=3)", errorLabel);
      ni[0].setProperties("n", true, 3);
      ni[0].setNumberInTextField(20);
      ig.add(ni[0]);
      ni[1] = new AuxJNumberInput("t_0", "first value of controlled variable", errorLabel);
      ni[1].setProperties("t_0", false);
      ni[1].setNumberInTextField(0);
      ig.add(ni[1]);
      ni[2] = new AuxJNumberInput("delta_t", "step width of controlled variable", errorLabel);
      ni[2].setProperties("delta_t", false);
      ni[2].setNumberInTextField(.1);
      ig.add(ni[2]);
      ni[3] = new AuxJNumberInput("x", "coefficient in front of exponential term (>0)", errorLabel);
      ni[3].setProperties("x", false, .000001);
      ni[3].setNumberInTextField(1.);
      ig.add(ni[3]);
      ni[4] = new AuxJNumberInput("w", "exponent (>=0)", errorLabel);
      ni[4].setProperties("w", false, 0.);
      ni[4].setNumberInTextField(2.);
      ig.add(ni[4]);
      ni[5] = new AuxJNumberInput("sigma", "size of measurement errors", errorLabel);
      ni[5].setProperties("sigma", false);
      ni[5].setNumberInTextField(.2);
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
      n = (int)ni[0].parseInput(); 
      t0 = ni[1].parseInput();  
      deltat = ni[2].parseInput();
      x = ni[3].parseInput();
      w = ni[4].parseInput();
      sigma = ni[5].parseInput();
// generate data points
      t = new DatanVector(n);
      y = new DatanVector(n);
      dy = new DatanVector(n);
      rand = DatanRandom.standardNormal(n);
      for(int i = 0; i < n; i ++){
         t.setElement(i, t0 + (double)i * deltat);
         y.setElement(i, x * Math.pow(t.getElement(i), w) + sigma * rand[i]);
         dy.setElement(i, sigma);
      }
// set up a and c
      a = new DatanMatrix(n, 1);
      c = new DatanVector(n);
      for(int i = 0; i < n; i ++){
         c.setElement(i, - y.getElement(i));
         a.setElement(i, 0, - Math.pow(t.getElement(i), w));
      }
// perform fit
      LsqLin ll = new LsqLin(t, c, dy, a);
      xres = ll.getResult();
      double xfit = xres.getElement(0);
      cov = ll.getCovarianceMatrix();
      double deltaxfit = Math.sqrt(cov.getElement(0, 0));
      m = ll.getChiSquare();
      double p = 1. - StatFunct.cumulativeChiSquared(m, n - 1);
// curve of fitted exponential
      xpl = new double[1001];
      ypl = new double[1001];
      dpl = (double)(n - 1) * deltat / 1000.;
      for(int i = 0; i < 1001; i++){
         xpl[i] = t0 + (double)i * dpl;
         ypl[i] = xfit * Math.pow(xpl[i], w);
      }
// prpare data points for plotting
      datx = new double[n];
      daty = new double[n];
      datsx = new double[n];
      datsy = new double[n];
      datrho = new double[n];
      for(int i = 0; i < n; i ++){
         datx[i] = t.getElement(i);
         daty[i] = y.getElement(i);
         datsx[i] = 0.;
         datsy[i] = dy.getElement(i);
         datrho[i] = 0.;
      }
// display data and fitted curve
      caption ="x=" + String.format(Locale.US, "%6.2f", xfit)
         + ", &D@x=" + String.format(Locale.US, "%6.2f", deltaxfit)
         +  ", M=" + String.format(Locale.US, "%6.2f", m)
         + ", P=" + String.format(Locale.US, "%6.4f", p);
      new GraphicsWithDataPointsAndPolyline(getClass().getName(), "", xpl, ypl,
         1, .3, datx, daty, datsx, datsy, datrho, "t", "y", caption);
    }
    
    private class GoButtonListener implements ActionListener { 
        public void actionPerformed(ActionEvent e) {
           if (inputOk()) compute();
       }
    }

   public static void main(String s[]) {
      new S2Lsq();
   }

}
