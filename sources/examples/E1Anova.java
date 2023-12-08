package examples;

import datan.*;
import datangraphics.*;

import java.text.*;
import java.util.*;
import javax.swing.*;

/**
* Example demonstrating the use of class AnanysisOfVariance
*/
public class E1Anova {
   DatanFrame df;
   AnalysisOfVariance av;
   String[] out;
   double[][][] x = {{
            {34., 40., 38., 36.},
            {54., 57., 40., 43.},
            {44., 52., 53., 51.},
            {51., 46., 51., 49.},
            {62., 61., 54., 60.},
            {61., 70., 64., 68.},
            {59., 67., 58., 66.},
            {66., 59., 67., 58.},
            {52., 63., 60., 59.},
            {52., 50., 44., 52.}
         },
         { 
            {28., 32., 34., 27.},
            {23., 23., 29., 30.},
            {42., 41., 34., 39.},
            {43., 48., 36., 43.},
            {31., 45., 41., 37.},
            {32., 38., 32., 34.},
            {25., 27., 27., 28.},
            {24., 26., 32., 30.},
            {26., 31., 25., 26.},
            {26., 27., 27., 30.}
         }};
   public E1Anova(){
      String s = "Example demonstrating the use of class AnanysisOfVariance";
      df = new DatanFrame(getClass().getName(), s);
      av = new AnalysisOfVariance(x);
      df.writeLines(av.displayData());
      df.writeLines(av.tableCrossed());
   }

    

   public static void main(String s[]) {
      new E1Anova();
   }

}
