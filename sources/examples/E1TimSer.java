package examples;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;        //for action and window events
/**
* Example demonstrating a time series analysis
*/
public class E1TimSer {
   int k, l, n;
   DatanFrame df;
   double p;
   double[] coneta, eta, y;
   TimeSeries ts;

   public E1TimSer(){
      String s = "Example demonstrating a time series analysis";
      df = new DatanFrame(getClass().getName(), s, getClass().getName() + ".txt");
// set up data
      double[] y ={
      38.7,50.3,45.6,46.4,43.7,42.0,
      21.8,21.8,51.3,39.5,26.9,23.2,
      19.8,24.4,17.1,29.3,43.0,35.9,
      19.6,33.2,38.8,35.3,23.4,14.9,
      15.3,17.7,16.5, 6.6, 9.5, 9.1,
       3.1, 9.3, 4.7, 6.1, 7.4,15.1      
      };
// perform analysis
      n = y.length;
      k = 2;
      l = 2;
      p = 0.9;
      ts = new TimeSeries(y, k, l, p);
      eta = ts.getMovingAverages();
      coneta = ts.getConfidenceLimits();
// write out data and results
      df.writeLine("Time series analysis for N = " + n + ", k = " + k + ", l = " + l + ", P = " + String.format(Locale.US, "%6.3f", p));
      df.writeLine("    i          y        eta     coneta");
      for(int i = -k; i < n + k; i++){
         if(i < 0 || i > n-1){
            df.writeLine(String.format(Locale.US, "%5d", i) + "            " +
                         String.format(Locale.US, "%10.5f %10.5f" , eta[i + k], coneta[i + k]));
         }
         else{
            df.writeLine(String.format(Locale.US, "%5d %10.5f %10.5f %10.5f " , i, y[i], eta[i + k], coneta[i + k]));
         }
      }
   }

   public static void main(String s[]) {
      new E1TimSer();
   }

}
