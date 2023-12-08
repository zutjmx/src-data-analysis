package examples;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events

public class E1Reg {
   int nny;
   AuxJNumberInput[] ni = new AuxJNumberInput[1];
   DatanFrame df;  
   NumberFormat numForm;

   public E1Reg(){  
      numForm = NumberFormat.getNumberInstance(Locale.US);
	   numForm.setMaximumFractionDigits(12);
  	   numForm.setMinimumFractionDigits(12);
      String s = "Example demonstrating polynomial regression";
      df = new DatanFrame(getClass().getName(), s);
// set up data
      double[] t ={-.9, -.7, -.5, -.3, -.1, .1, .3, .5, .7, .9};
      double[] y = {81., 50., 35., 27., 26., 60. ,106., 189. ,318., 520.};
      int n = t.length;
      double[] deltay = new double[n];
      for(int i = 0; i < n; i++){
         deltay[i] = Math.sqrt(y[i]);
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
   }

   public static void main(String s[]) {
      new E1Reg();
   }

}
