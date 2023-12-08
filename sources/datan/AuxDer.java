package datan;

import java.text.*;
import java.util.*;
import java.lang.*;

/**
* A class computing the derivative f'(x) of f(x) at x = X. Based on
* H. Rutishauser, Ausdehnung des Rombergschen Prinzips Extension of Romberg's Principle), Numer. Math. 5 (1963) 48-54.
* @author  Siegmund Brandt.
*/

public class AuxDer{

private static double[] dx = { .0256,.0192,.0128,.0096,.0064,.0048,.0032,.0024,.0016,.0012 };
private static boolean[] lev = { true, false, true, false, true, false, true, false, true, false };
private static double[][] w = { {0.0,1.3333333333333333,.33333333333333333,1.0666666666666667,.066666666666666667,
   1.0158730158730159,.015873015873015873,1.003921568627451,.0039215686274509804,0.0},
   {2.2857142857142857,1.2857142857142857,1.1636363636363636,.16363636363636364,1.0364372469635628,
   .036437246963562753,1.0088669950738916,.0088669950738916256,1.0022021042329337,.0022021042329336922},
   {1.8,.8,1.125,.125,1.0285714285714286,.028571428571428571,1.006993006993007,
   .006993006993006993,1.0017391304347826,.0017391304347826087} };
private static double EPS = 5.E-8, EPSI = 1.E-10, DELTA = 10., S = 0.1;

DatanUserFunction f;
private boolean converged, lmt;
double x, dfdx, del, h;
double[] a = new double[10];
double[][] t = new double[10][10];

   
/**
* @param df user function which must be an extension of the abstract class DatanUserFunction.
*/
   public AuxDer(DatanUserFunction df){
      f = df;
   }
      
/**
* @return derivative.
* @param x argument of the user function at which the derivative is computed.
*/
   public double getDerivative(double x){
      del = DELTA;
      converged = true;
      loop:
      for(int i = 0; i < 10; i++){
         del = S * del;
         if(i == 9 || (x + del * dx[9] == x)){
            converged = false;
            break loop;
         }
         for(int k = 0; k < 10; k++){
            h = del * dx[k];
            t[k][0] = (f.getValue(x + h) - f.getValue(x - h)) / (h + h);
            a[k] = t[k][0];
         }
         if(a[0] >= a[9]){
            for(int k = 0; k < 10; k++){
               a[k] = -a[k];
            }
         }
         lmt = true;
         for(int k = 1; k < 10; k++){
            h = a[k - 1] - a[k];
            lmt = lmt && (h <= 0. || Math.abs(h) <= Math.abs(a[k]) * EPSI);
         }
         if(lmt) break loop; 
      }
      if(converged){
         for(int m = 1; m < 10; m++){
            for(int k = 0; k < 10 - m; k++){
               if(lev[m]){
                  t[k][m] = w[0][m - 1] * t[k + 1][m - 1] - w[0][m] * t[k][m - 1];
               }
               else if (lev[k]){
                  t[k][m] = w[1][m - 1] * t[k + 1][m - 1] - w[1][m] * t[k][m - 1];
               }
               else{
                  t[k][m] = w[2][m - 1] * t[k + 1][m - 1] - w[2][m] * t[k][m - 1];
               }
            }
         }
         dfdx = t[0][9];
      }
      else{
         dfdx = 0.;
         System.out.println("AuxDer failed for x = " + x);
      }
      return dfdx;
   }

/**
* @return true if iteration successful, false otherwise. (To be called only after "getDerivative(x)".)
*/
   public boolean hasConverged(){
      return converged;
   }


}
