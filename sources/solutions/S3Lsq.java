package solutions;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events
   
/**
* Data fitted to power law (nonlinear case)
*/
public class S3Lsq {
   AuxJNumberInput[] ni = new AuxJNumberInput[6];
   DatanFrame df;
   int n;
   double m, x1, x2, sigma, t0, deltat, dpl, delx1, delx2, rho, p;
   double[] rand, xpl, ypl, datx, daty, datsx, datsy, datrho, tlog, ylog, dellog;
   DatanVector c, t, y, dy, x;
   DatanMatrix a, cov;
   DatanUserFunction powerlaw;
   String caption;
   String[] ac, actionCommands;

   public S3Lsq(){
      String s = "Data fitted to power law (nonlinear case)";
      df = new DatanFrame(getClass().getName(), s);
// parameter input
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("n", "number of data points (>=3)", errorLabel);
      ni[0].setProperties("n", true, 3);
      ni[0].setNumberInTextField(20);
      ig.add(ni[0]);
      ni[1] = new AuxJNumberInput("t_0", "first value of controlled variable (>=0)", errorLabel);
      ni[1].setProperties("t_0", false, 0.);
      ni[1].setNumberInTextField(0);
      ig.add(ni[1]);
      ni[2] = new AuxJNumberInput("delta_t", "step width of controlled variable (>=0)", errorLabel);
      ni[2].setProperties("delta_t", false, 0.);
      ni[2].setNumberInTextField(.1);
      ig.add(ni[2]);
      ni[3] = new AuxJNumberInput("x_1", "coefficient in front of exponential term (>0)", errorLabel);
      ni[3].setProperties("x_1", false, .000001);
      ni[3].setNumberInTextField(1.);
      ig.add(ni[3]);
      ni[4] = new AuxJNumberInput("x_2", "exponent (>=0)", errorLabel);
      ni[4].setProperties("x_2", false, 0.);
      ni[4].setNumberInTextField(2.);
      ig.add(ni[4]);
      ni[5] = new AuxJNumberInput("sigma", "size of measurement errors (>0)", errorLabel);
      ni[5].setProperties("sigma", false, 0.00001);
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
      x1 = ni[3].parseInput();
      x2 = ni[4].parseInput();
      sigma = ni[5].parseInput();
// generate data points
      t = new DatanVector(n);
      y = new DatanVector(n);
      dy = new DatanVector(n);
      rand = DatanRandom.standardNormal(n);
      for(int i = 0; i < n; i ++){
         t.setElement(i, t0 + (double)i * deltat);
         y.setElement(i, x1 * Math.pow(t.getElement(i), x2) + sigma * rand[i]);
         dy.setElement(i, sigma);
      }
// find 1st approximation of unknowns by method of log-log plot
      tlog = new double[n];
      ylog = new double[n];
      dellog = new double[n];
      int npos = 0;
      for(int i = 0; i < n; i ++){
         if(t.getElement(i) > 0. && y.getElement(i) > 0.){
            tlog[npos] = Math.log(t.getElement(i));
            ylog[npos] = Math.log(y.getElement(i));
            dellog[npos] =1.;
            npos++;
         }
      }
      DatanVector vtlog = new DatanVector(npos);
      DatanVector vylog = new DatanVector(npos);
      DatanVector vdellog = new DatanVector(npos);
      for(int j = 0; j < npos; j ++){
         vtlog.setElement(j, tlog[j]);
         vylog.setElement(j, ylog[j]);
         vdellog.setElement(j, dellog[j]);
      }
      LsqPol lp = new LsqPol(vtlog, vylog, vdellog, 2);
      x = lp.getResult();
      x.setElement(0, Math.exp(x.getElement(0)));
      df.writeLine(" x = " + x.toString());
// perform fit
      int[] list = {1, 1};
      powerlaw = new Powerlaw();
      LsqNon ln = new LsqNon(t, y, dy, x, list, powerlaw);
      x = ln.getResult();
      x1 = x.getElement(0);
      x2 = x.getElement(1);
      cov = ln.getCovarianceMatrix();
      delx1 = Math.sqrt(cov.getElement(0, 0));
      delx2 = Math.sqrt(cov.getElement(1, 1));
      rho = cov.getElement(1, 0) /(delx1 * delx2);
      m = ln.getChiSquare();
      p = 1. - StatFunct.cumulativeChiSquared(m, n - 1);
// curve of fitted exponential
      xpl = new double[1001];
      ypl = new double[1001];
      dpl = (double)(n - 1) * deltat / 1000.;
      for(int i = 0; i < 1001; i++){
         xpl[i] = t0 + (double)i * dpl;
         ypl[i] = x1 * Math.pow(xpl[i], x2);
      }
// prepare data points for plotting
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
      caption = "x_1#=" + String.format(Locale.US, "%5.2f", x1)
         + ", x_2#=" + String.format(Locale.US, "%5.2f", x2)
         + ", &D@x_1#=" + String.format(Locale.US, "%5.2f", delx1)
         + ", &D@x_2#=" + String.format(Locale.US, "%5.2f", delx2)
         + ", &r@=" + String.format(Locale.US, "%5.2f", rho)
         +  ", M=" + String.format(Locale.US, "%5.2f", m)
         + ", P=" + String.format(Locale.US, "%6.4f", p);
      new GraphicsWithDataPointsAndPolyline(getClass().getName(), "", xpl, ypl,
         1, .3, datx, daty, datsx, datsy, datrho, "t", "y", caption);
    }
    
    private class GoButtonListener implements ActionListener { 
        public void actionPerformed(ActionEvent e) {
           if (inputOk()) compute();
       }
    }

    private class Powerlaw extends DatanUserFunction{
       public double getValue(DatanVector x, double t){
          return x.getElement(0) * Math.pow(t, x.getElement(1));
	    }
    }

   public static void main(String s[]) {
      new S3Lsq();
   }

}
