package examples;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events

/**
* Example simulating decay times and determining mean life by maximum likelihood
*/
public class E1MaxLike {
   AuxJNumberInput[] ni = new AuxJNumberInput[1];
   DatanFrame df;
   int n;
   double fmin, tbar, delta, deltaminus, deltaplus;
   UserFunction uf;
   double[] t, xpl, ypl;
   NumberFormat numForm;
   String[] ac, actionCommands;
   public E1MaxLike(){
      String s = "Example simulating decay times and determining mean life by maximum likelihood";
      df = new DatanFrame(getClass().getName(), s);
// parameter input
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("N", "number of decays (> 0, <= 1000)", errorLabel);
      ig.add(ni[0]);
      ni[0].setProperties("N", true, 1, 1000);
      ni[0].setNumberInTextField(10);
      ig.add(ni[0]);
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
      n = (int)ni[0].parseInput();  // number of decays
// prepare graphics      
      DatanGraphics.openWorkstation(getClass().getName(), "");
      DatanGraphics.setFormat(5., 0.);
      DatanGraphics.setWindowInComputingCoordinates(0., 5., 0., .6);
      DatanGraphics.setWindowInWorldCoordinates(-.214, 1.2, 0., 1.);
      DatanGraphics.setViewportInWorldCoordinates(.15, 1.1, 0.2, 0.8);
      DatanGraphics.chooseColor(2);
      DatanGraphics.setBigClippingWindow();
      DatanGraphics.drawFrame();
      DatanGraphics.drawBoundary();
      DatanGraphics.setBigClippingWindow();
      DatanGraphics.drawScaleX("t, &t");
      DatanGraphics.drawScaleY("-(l - l_max#)");
      tbar = 0.;
// draw sample from uniform distribution
      t = datan.DatanRandom.ecuy(n);
      xpl = new double[2];
      ypl = new double[2];
      ypl[0] = 0.;
      ypl[1] = 0.1;
      DatanGraphics.chooseColor(1);
// trnsform sample to exponential distribution and draw 1D scatter diagram
      for(int i = 0; i < n; i++){
         t[i] = - Math.log(t[i]);
         tbar = tbar + t[i];
         xpl[0] =t[i];
         xpl[1] =t[i];
         DatanGraphics.drawPolyline(xpl, ypl);
      }
      tbar = tbar / (double)n;
// draw line - (l - lmax) = .5
      xpl[0] = 0.;
      xpl[1] = 5.;
      ypl[0] = .5;
      ypl[1] = .5;
      DatanGraphics.drawPolyline(xpl, ypl);
      
// draw short line at tbar
      xpl[0] = tbar;
      xpl[1] = tbar;
      ypl[0] = .48;
      ypl[1] = .52;
      DatanGraphics.drawPolyline(xpl, ypl);
// draw likelihood function - (l - lmax)
      fmin = (double)n * (1. + Math.log(tbar));
      xpl = new double[500];
      ypl = new double[500];
      for(int i = 0; i < 500; i ++){
         xpl[i] = (double)(i + 1) * 0.01;
         ypl[i] =  (double)n * (tbar / xpl[i] + Math.log(xpl[i])) - fmin;
      }
      DatanGraphics.chooseColor(4);
      DatanGraphics.drawPolyline(xpl, ypl);
// compute asymmetric errors
      uf = new UserFunction();
      AuxZero az = new AuxZero(uf, 0.0001, tbar);
      deltaminus = az.getZero();
      deltaminus = tbar - deltaminus;
      az = new AuxZero(uf, tbar, tbar + 1.);
      deltaplus = az.getZero();
      deltaplus = deltaplus - tbar;
// compute symmetric errors
      delta = tbar / Math.sqrt((double)n);
      numForm = NumberFormat.getNumberInstance(Locale.US);
	   numForm.setMaximumFractionDigits(3);
  	   numForm.setMinimumFractionDigits(0);
      String caption = "n=" + n + ", t^^\"-#=&t^\"~#=" + numForm.format(tbar)
         + ", &D_-#=" + numForm.format(deltaminus) + ", &D_+#="
         + numForm.format(deltaplus) + ", &Dt^\"~#=" + numForm.format(delta);
      DatanGraphics.setBigClippingWindow();
      DatanGraphics.drawCaption(0., caption);
      DatanGraphics.closeWorkstation();
    }

 private class UserFunction extends DatanUserFunction{
    public double getValue(double tau){
      return (double) n * (tbar / tau + Math.log(tau / tbar) - 1.) - 0.5;
   }
 }

   public static void main(String s[]) {
      new E1MaxLike();
   }

}
