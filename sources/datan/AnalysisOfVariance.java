package datan;

import java.text.*;
import java.util.*;
import java.lang.*;

/**
* A class performing an analysis of variance
* @author  Siegmund Brandt.
*/

public class AnalysisOfVariance{
   double[][][] x;
   double[][] xbij;
   double[] xbi, xbj, q, df, s, f, a;
   double ai, aj, ak, xb; 
   int[] ndf;
   int ni, nj, nk;
   String[] out;
   
/**
* @param x data
*/
   public AnalysisOfVariance(double[][][] x){
      this.x = x;
      ni = x.length;
      nj = x[0].length;
      nk = x[0][0].length;
      xbij = new double[ni][nj];
      xbi = new double[ni];
      xbj = new double[nj];
      q = new double[6];
      df = new double[6];
      s = new double[6];
      a = new double[4];
      f = new double[4];
      ndf = new int[6];
      ai = (double)ni;
      aj = (double)nj;
      ak = (double)nk;
// compute means
      xb = 0.;
      for(int i = 0; i < ni; i++){
         xbi[i] = 0.;
      }
      for(int j = 0; j < nj; j++){
         xbj[j] = 0;
         for(int i = 0; i < ni; i++){
            xbij[i][j] = 0.;
         }
      }
      for(int i = 0; i < ni; i++){
         for(int j = 0; j < nj; j++){
            for(int k = 0; k < nk; k++){
               xbij[i][j] = xbij[i][j] + x[i][j][k];
            }
            xbij[i][j] = xbij[i][j] / ak;
         }
      }
      for(int i = 0; i < ni; i++){
         for(int j = 0; j < nj; j++){
            xbi[i] = xbi[i] + xbij[i][j];
         }
         xbi[i] = xbi[i] / aj;
      }
      for(int j = 0; j < nj; j++){
         for(int i = 0; i < ni; i++){
            xbj[j] = xbj[j] + xbij[i][j];
         }
         xbj[j] = xbj[j] / ai;
         xb = xb + xbj[j];
      }
      xb = xb / aj;
// compute sums of squares
      for(int l = 0; l < 6; l++){
         q[l] = 0.;
      }
      for(int i = 0; i < ni; i++){
         q[0] = q[0] + (xbi[i] -xb) * (xbi[i] -xb);
      }
      q[0] = aj * ak * q[0];
      for(int j = 0; j < nj; j++){
         q[1] = q[1] + (xbj[j] -xb) * (xbj[j] -xb);
      }
      q[1] = ai * ak * q[1];      
      for(int i = 0; i < ni; i++){
         for(int j = 0; j < nj; j++){
            q[2] = q[2] + Math.pow(xbij[i][j] + xb - xbi[i] - xbj[j], 2.);
            for(int k = 0; k < nk; k++){
               q[4] = q[4] + Math.pow(x[i][j][k] - xbij[i][j], 2.);
               q[5] = q[5] + Math.pow(x[i][j][k] - xb, 2.);
            }
         }
      }
      q[2] = ak * q[2];
      q[3] = q[1] + q[2];
// determine degrees of freedom
      df[0] = ai - 1.;
      df[1] = aj - 1.;
      df[2] = (ai - 1.) * (aj - 1.);
      df[3] = df[1] + df [2];
      df[4] = ai * aj * (ak - 1.);
      df[5] = ai * aj * ak - 1.;
// mean squares
      for(int l = 0; l < 6; l++){
         s[l] = q[l] / df[l];
      }
// F quotients
      for(int l = 0; l < 4; l++){
         f[l] = s[l] / s[4];
      }
// degrees of freedom as integers
      for(int l = 0; l < 6; l++){
         ndf[l] = (int)df[l];
      }
// mean squares
      for(int l = 0; l < 4; l++){
         a[l] = 1. - StatFunct.cumulativeFDistribution(f[l], ndf[l], ndf[4]);
      }

   }

/**
* @return data in form of a table
*/
   public String[] displayData(){
      out = new String[ni * nj + 3];
      out[0] = "                  Data\n";
      out[1] = "    i     j  ";
      for(int k = 0; k < nk; k++){
         out[1] = out[1] + "    k = " + String.format(Locale.US, "%2d", k);  
      }
      out[2] = " ";
      int iline = 2;
      for(int i = 0; i < ni; i++){
         for(int j = 0; j < nj; j++){
            iline++;
            out[iline] = String.format(Locale.US, "%5d %5d", i, j) + "  ";
               for(int k = 0; k < nk; k++){
                  out[iline] = out[iline] + String.format(Locale.US, "%10.5f", x[i][j][k]);  
               }
         }
      }
      return out;
   }

/**
* @return the analysis-of-varince table for a crossed classification
*/
   public String[] tableCrossed(){
      out = new String[9];
      out[0] = "                Analysis of Variance Table (Crossed Classification)\n";
      out[1] = "  Source       Sum of         Degrees of     Mean           F Ratio        Alpha";
      out[2] = "               Squares        Freedom        Square";
      out[3] = " ";
      out[4] = "  A   " + String.format(Locale.US, "%15.2f %15d %15.5f %15.5f %15.12f", q[0], ndf[0], s[0], f[0], a[0]);
      out[5] = "  B   " + String.format(Locale.US, "%15.2f %15d %15.5f %15.5f %15.12f", q[1], ndf[1], s[1], f[1], a[1]);
      out[6] = "  Int." + String.format(Locale.US, "%15.2f %15d %15.5f %15.5f %15.12f", q[2], ndf[2], s[2], f[2], a[2]);
      out[7] = "  W   " + String.format(Locale.US, "%15.2f %15d %15.5f", q[4], ndf[4], s[4]);
      out[8] = "  Ttl." + String.format(Locale.US, "%15.2f %15d %15.5f", q[5], ndf[5], s[5]);
      return out;
   }

/**
* @return the analysis-of-varince table for a nested classification
*/
   public String[] tableNested(){
      out = new String[8];
      out[0] = "                Analysis of Variance Table (Nested Classification)\n";
      out[1] = "  Source       Sum of         Degrees of     Mean           F Ratio        Alpha";
      out[2] = "               Squares        Freedom        Square";
      out[3] = " ";
      out[4] = "  A   " + String.format(Locale.US, "%15.2f %15d %15.5f %15.5f %15.12f", q[0], ndf[0], s[0], f[0], a[0]);
      out[5] = "  B(A)" + String.format(Locale.US, "%15.2f %15d %15.5f %15.5f %15.12f", q[3], ndf[3], s[3], f[3], a[3]);
      out[6] = "  W   " + String.format(Locale.US, "%15.2f %15d %15.5f", q[4], ndf[4], s[4]);
      out[7] = "  Ttl." + String.format(Locale.US, "%15.2f %15d %15.5f", q[5], ndf[5], s[5]);
      return out;
   }




}
