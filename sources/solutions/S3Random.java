package solutions;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events

/**
* Points fluctuating about a straight line, having errors of different sizes
*/
public class S3Random {
   AuxJNumberInput[] ni = new AuxJNumberInput[7];
   DatanFrame df;
   int n;
   double a, b, t0, deltat, sigmaymin, sigmaymax;
   double xa, xb, ya, yb;
   double[] datx, daty, rho, sigmax, sigmay;
   double t;
   NumberFormat numForm;
   String caption, header;
   String[] ac, actionCommands;
   

   public S3Random(){
      String s = "Points fluctuating about a straight line, having errors of different sizes";
      df = new DatanFrame(getClass().getName(), s);
// parameter input
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("N", "number of points (> 1, <= 100)", errorLabel);
      ig.add(ni[0]);
      ni[0].setProperties("N", true, 2, 100);
      ni[0].setNumberInTextField(20);
      ig.add(ni[0]);
      ni[1] = new AuxJNumberInput("a", "slope", errorLabel);
      ni[1].setProperties("a", false);
      ni[1].setNumberInTextField(2);
      ig.add(ni[1]);
      ni[2] = new AuxJNumberInput("b", "intercept", errorLabel);
      ni[2].setProperties("b", false);
      ni[2].setNumberInTextField(5);
      ig.add(ni[2]);
      ni[3] = new AuxJNumberInput("t_0", "abscissa value of first point", errorLabel);
      ni[3].setProperties("t_0", false);
      ni[3].setNumberInTextField(0);
      ig.add(ni[3]);
      ni[4] = new AuxJNumberInput("delta_t", "interval in t between points", errorLabel);
      ni[4].setProperties("delta_t", false);
      ni[4].setNumberInTextField(1);
      ig.add(ni[4]);
      ni[5] = new AuxJNumberInput("min(sigma_y)", "Gaussian error in ordinate value (>= 0)", errorLabel);
      ni[5].setProperties("min(sigma_y)", false, 0.);
      ni[5].setNumberInTextField(1);
      ig.add(ni[5]);
      ni[6] = new AuxJNumberInput("max(sigma_y)", "Gaussian error in ordinate value (>= 0)", errorLabel);
      ni[6].setProperties("max(sigma_y)", false, 0.);
      ni[6].setNumberInTextField(4);
      ig.add(ni[6]);
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
      n = (int)ni[0].parseInput();  // number of points
      a = ni[1].parseInput();   // intercept
      b = ni[2].parseInput();   // slope
      t0 = ni[3].parseInput();   // abscissa value of first point
      deltat = ni[4].parseInput();   // interval in t between points
      sigmaymin = ni[5].parseInput();   // minimum Gaussian error in ordinate value
      sigmaymax = ni[6].parseInput();   // maximum Gaussian error in ordinate value
      datx = new double[n];
      daty = new double[n];
      sigmay = new double[n];
// create points
      createPointsAboutLine(a, b, t0, deltat, sigmaymin, sigmaymax);
// write output
      numForm = NumberFormat.getNumberInstance(Locale.US);
	   numForm.setMaximumFractionDigits(6);
  	   numForm.setMinimumFractionDigits(0);
      header = "y = " + numForm.format(a) + " t + " +  numForm.format(b)
         + "; min(sigma_y) = " + numForm.format(sigmaymin)
         + ", max(sigma_y) = " + numForm.format(sigmaymax);
      df.writeLine(header);
      df.writeLine("              t               y          sigmay");
      for(int i = 0; i < n; i++){
         df.writeLine(String.format(Locale.US,"%15.5f %15.5f %15.5f", datx[i], daty[i], sigmay[i]) );
      }       
      df.writeLine("------------------------------------------------");
// produce graphics
      sigmax = new double[n];
      rho = new double[n];      
      for(int i = 0; i < n; i++){
         sigmax[i] = 0.;
         rho[i] = 0.;
      }
      double[] xpl = new double[2];
      double[] ypl = new double[2];
      xpl[0] = datx[0];
      xpl[1] = datx[n - 1];
      ypl[0] = a * xpl[0] + b;
      ypl[1] = a * xpl[1] + b;
	   numForm.setMaximumFractionDigits(5);
  	   numForm.setMinimumFractionDigits(0); 
      caption = "y = " + numForm.format(a) + " t + " +  numForm.format(b) + ", min(&s@_y#) = " + numForm.format(sigmaymin)
         + ", max(&s@_y#) = " + numForm.format(sigmaymax); 
      new GraphicsWithDataPointsAndPolyline(getClass().getName(), "",
         xpl, ypl, 1, .3, datx, daty, sigmax, sigmay, rho, "t", "y", caption);
    }

    public void createPointsAboutLine(double a, double b, double t0, double deltat, double sigmaymin, double sigmaymax){
      double[] r;
      for(int i = 0; i < n; i++){
         datx[i] = t0 + (double)i * deltat;
         daty[i] = a * datx[i] + b;
         r = DatanRandom.ecuy(1);
         sigmay[i] = sigmaymin + r[0] * (sigmaymax - sigmaymin);
         r = DatanRandom.standardNormal(1);
         daty[i] = daty[i] + r[0] * sigmay[i];
      }
    }

   public static void main(String s[]) {
      new S3Random();
   }

}
