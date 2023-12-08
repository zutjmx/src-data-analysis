package datan; 
/** 
* A class providing methods to compute confidence limits for small samples
* @author  Siegmund Brandt.
*/
public final class SmallSample{
	    static double BIG = 1.E30;
		 static double EPSILON = 1.E-8;
       static double alpha, calpha, lbback, lbmin, x0, x1, denominator, rback;
       static int kk, dd;
       static boolean computelambda;
 

/**
* Returns the confidence limit lambda_signal- for a small sample in the presence of background.
* @param k number of observed events
* @param beta confidence level.
* @param lambdaback Poisson parameter of background.
* @return lambda_signal-.
*/
   public static double lambdaSignalMinus(int k, double beta, double lambdaback){
      double lambdaminus;
      computelambda = true;
      lbback = lambdaback;
      alpha = 1. - beta;
      if(k == 0){
         lambdaminus = 0.;
      }
      else{
         if(lbback <= 0.){
            denominator = 1.;   
         }
         else{
            denominator = StatFunct.cumulativePoisson(k, lbback); 
         }
         calpha = 1. - 0.5 * alpha;
         x0 = 0.;
         x1 = 1.;
         if(k > 1) x1 = (double)k;
         kk = k;

         lambdaminus = findZero();
      }
      return lambdaminus;
   }
   
/**
* Returns the confidence limit lambda_signal+ for a small sample in the presence of background.
* @param k number of observed events
* @param beta confidence level.
* @param lambdaback Poisson parameter of background.
* @return lambda_signal+.
*/   
      public static double lambdaSignalPlus(int k, double beta, double lambdaback){
      double lambdaplus;
      computelambda = true;
      lbback = lambdaback;
      alpha = 1. - beta;
      kk = k + 1;
      if(lbback <= 0.){
         denominator = 1.;   
      }
      else{
         denominator = StatFunct.cumulativePoisson(kk, lbback); 
      }
      calpha = 0.5 * alpha;
      x0 = 0.;
      x1 = 1.;
      if(k > 1) x1 = (double)k;
      lambdaplus = findZero();
      return lambdaplus;
   }

/**
* Returns the confidence limit lambda_signal(up) for a small sample in the presence of background.
* @param k number of observed events
* @param beta confidence level.
* @param lambdaback Poisson parameter of background.
* @return lambda_signal(up).
*/   
      public static double lambdaSignalUpper(int k, double beta, double lambdaback){
      double lambdaupper;
      computelambda = true;
      lbback = lambdaback;
      alpha = 1. - beta;
      kk = k + 1;
      if(lbback <= 0.){
         denominator = 1.;   
      }
      else{
         denominator = StatFunct.cumulativePoisson(kk, lbback); 
      }
      calpha = alpha;
      x0 = 0.;
      x1 = 1.;
      if(k > 1) x1 = (double)k;
      lambdaupper = findZero();
      return lambdaupper;
   }

/**
* Returns the confidence limit r_signal- for the ratio of signal and reference events in the presence of background.
* @param k number of observed events..
* @param d number of reference events.
* @param beta confidence level.
* @param ratioback expected ratio of background to reference events.
* @return r_signal-.
*/   
   public static double ratioSignalMinus(int k, int d, double beta, double ratioback){
      double rsminus;
      computelambda = false;
      rback = ratioback;
      dd = d;
      alpha = 1. - beta;
      
      if(k <= 0){
         rsminus = 0.;
      }
      else{
         kk = k;
         calpha = 1. - 0.5 * alpha;
         x0 = 0.;
         x1 = (double)k;
         rsminus = findZero();
      }
      return rsminus;
   }

/**
* Returns the confidence limit r_signal+ for the ratio of signal and reference events in the presence of background.
* @param k number of observed events..
* @param d number of reference events.
* @param beta confidence level.
* @param ratioback expected ratio of background to reference events.
* @return r_signal+.
*/   
   public static double ratioSignalPlus(int k, int d, double beta, double ratioback){
      double rsplus;
      computelambda = false;
      rback = ratioback;
      dd = d;
      alpha = 1. - beta;
      calpha = 0.5 * alpha;
      kk = k + 1;
      x0 = 0.;
      x1 = (double)k;
      rsplus = findZero();
      return rsplus;
   }

/**
* Returns the confidence limit r_signal(up) for the ratio of signal and reference events in the presence of background.
* @param k number of observed events..
* @param d number of reference events.
* @param beta confidence level.
* @param ratioback expected ratio of background to reference events.
* @return r_signal(up).
*/   
   public static double ratioSignalUpper(int k, int d, double beta, double ratioback){
      double rsupper;
      computelambda = false;
      rback = ratioback;
      dd = d;
      alpha = 1. - beta;
      calpha = alpha;
      kk = k + 1;
      x0 = 0.;
      x1 = (double)k;
      rsupper = findZero();
      return rsupper;
   }


    private static double findZero(){
       double res, xm, f0, f1, fm;
       // bracket zero
       if(x0 == x1) x1 = x0 + 1.;
       f1 = userFunction(x1);
       while (f1 < 0.){
          x1 = 2. * x1;
          f1 = userFunction(x1);
       }
       // find zero by interval halving
       res = 0.;
       loop:
       for(int i = 0; i < 2000; i++){
          f0 = userFunction(x0);
          f1 = userFunction(x1);
          if(f0 == 0.){
             res = x0;
             break loop;
          }
          else if(f1 == 0.){
             res = x1;
             break loop;
          }
          else{
             xm = 0.5 * (x0 + x1);
             if(Math.abs(x0 - x1) > EPSILON){
                fm = userFunction(xm);
                if(f0 * fm < 0.) x1 = xm;
                else x0 = xm;
             }
             else{
                res = xm;
                break loop;
             }             
          }
       }       
       return res;
    }

    private static double userFunction(double x){
      if(computelambda) return zeroSmall(x);
      else return zeroRatio(x);
    }

    private static double zeroSmall(double lb){
       return calpha - StatFunct.cumulativePoisson(kk, lb + lbback) / denominator;
    }

    private static double zeroRatio(double rs){
      int n;
      double den, e, eb, es, g, f, pb, ps;
      n = kk - 1 + dd;
      ps = rs / (1. + rs + rback);
      pb = rback / (1. + rs + rback);
      if(pb < 1.E-6) den = 1.;
      else den = StatFunct.cumulativeBinomial(kk, n, pb);
      g = Gamma.logGamma((double)(n + 1));
      f = 0.;
      for(int nb = 0; nb < kk; nb++){
         for(int ns = 0; ns < kk - nb; ns++){
            if(ns == 0) es = 1.;
            else es = Math.pow(ps, (double)ns);
            if(nb == 0){
               eb = 1.;
            }
            else{
               if(pb < 1.E-6) eb = 0.;
               else eb = Math.pow(pb, (double)nb);
            }
            if(n - ns - nb == 0) e = 1.;
            else e = Math.pow(1. - ps - pb, (double)(n - ns - nb));
            f = f + es * eb * e * 
               Math.exp(g - Gamma.logGamma((double)(ns + 1)) 
               - Gamma.logGamma((double)(nb + 1)) - Gamma.logGamma((double)(n - ns - nb + 1)));
         }
      }
      return calpha - f / den;
    }


 }
