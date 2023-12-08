package examples;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events

/**
* Example simulating decay times for a radioactive source with two components using the method DatanRandom.radio
*/
public class E3Random {
   AuxJNumberInput[] ni = new AuxJNumberInput[4];
   DatanFrame df;
   int n, ntau;
   double a, deltatau, t, tau0, tau1, tau2, taumax;
   Histogram hist;
   NumberFormat numForm;
   String caption;
   String[] ac, actionCommands;

   public E3Random(){
      String s = "Example simulating decay times for a radioactive source with two components using the method DatanRandom.radio";
      df = new DatanFrame(getClass().getName(), s);
// parameter input
      AuxJInputGroup ig = new AuxJInputGroup("Parameter input", "");
      JLabel errorLabel = new JLabel();
      ni[0] = new AuxJNumberInput("N", "number of decays (> 1, <= 100000)", errorLabel);
      ig.add(ni[0]);
      ni[0].setProperties("N", true, 2, 100000);
      ni[0].setNumberInTextField(1000);
      ig.add(ni[0]);
      ni[1] = new AuxJNumberInput("a", "fraction of nuclei with mean life tau_1", errorLabel);
      ni[1].setProperties("a", false, 0., 1.);
      ni[1].setNumberInTextField(.5);
      ig.add(ni[1]);
      ni[2] = new AuxJNumberInput("tau_1", "first mean life (> 0)", errorLabel);
      ni[2].setProperties("tau_1", false, 0.);
      ni[2].setNumberInTextField(1);
      ig.add(ni[2]);
      ni[3] = new AuxJNumberInput("tau_2", "second mean life (> 0)", errorLabel);
      ni[3].setProperties("tau_2", false, 0.);
      ni[3].setNumberInTextField(10);
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
      n = (int)ni[0].parseInput();  // number of decayss
      a = ni[1].parseInput();   // fraction of nuclei with mean life tau_1
      tau1 = ni[2].parseInput();   // mean life 1
      tau2 = ni[3].parseInput();   // mean life 2
//    create histogram
      taumax = Math.max(tau1, tau2);
      ntau = 100;
      deltatau = (4. * taumax) / (double)ntau;
      hist = new Histogram(0., deltatau, ntau);
// loop over number of decays
      for(int i = 1; i < n; i++){
// create decay time and enter in histogram
         t = DatanRandom.radio(a, tau1, tau2);
         hist.enter(t, 1.);
      }
// produce graphics with histogram
      numForm = NumberFormat.getNumberInstance(Locale.US);
	   numForm.setMaximumFractionDigits(5);
  	   numForm.setMinimumFractionDigits(0);
      caption = "N = " + n + " a = " +  numForm.format(a)
          + ", &t@_1# = " + numForm.format(tau1) + ", &t@_2# = " + numForm.format(tau2);
      new GraphicsWithHistogram(getClass().getName(), "", hist, "t", "n(t)", caption);
    }

   public static void main(String s[]) {
      new E3Random();
   }

}
