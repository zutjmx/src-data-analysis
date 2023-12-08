package datan;

/**
* A class determining the minimum of a function in n-dimensional space by Powell's method of selected directions.
* @author  Siegmund Brandt.
*/
public final class MinPow {
   static double EPSDEF = 1.E-12, T = 1.E-15;
   static int MAXSTP = 1000;
   double d, deltaf, eps, f, fe, fl, fold, test;
   int imax, ired, istep, n, ne, nst, nstep, nstdir, nred;
   boolean converged;
   int[] list;
   DatanVector x0, xold, xe, dir, dirn;
   DatanMatrix alldir;
   DatanUserFunction muf;
   MinDir md;

   public MinPow(DatanVector x0, int[] list, DatanUserFunction muf) {   
      this(x0, list, 0, 0., muf);
   }

   public MinPow(DatanVector x0,  DatanUserFunction muf) {
      list = new int[x0.getNumberOfElements()];
      for(int i = 0; i < x0.getNumberOfElements(); i++){
         list[i] = 1;
      }
      this.x0 = new DatanVector(x0);
      this.muf = muf;
      eps = EPSDEF;
      nst = MAXSTP;
      compute();
   }

/**
* @param x0 initial point in n-space.
* @param list array containing the elements of a list specifying which of the n variables are fixed (list element = 0) and
* which are adjustable (list element = 1).
* @param nstep maximal step number. Default: if a value <= 0 is used, then 1000 is taken instead.
* @param epsilon accuracy. Default if a value <= 0. is used, then 1,E-8 is taken instead.
* @param muf user function which must be an extension of the abstract class DatanUserFunction.
*/
   public MinPow(DatanVector x0, int[] list, int nstep, double epsilon, DatanUserFunction muf) {
      eps = epsilon;
      this.x0 = new DatanVector(x0);
      nst = nstep;
      this.muf = muf;
      this.list = list;
      eps = epsilon;
      if(eps <= 0.)eps = EPSDEF;
      if(nst <= 0) nst = MAXSTP;
      compute();
   }

   private void compute(){
// Initialize n (number of variables) and nred (number of adjustable variables).
      converged = true;
      n = x0.getNumberOfElements();
      nred = 0;
      for(int i = 0; i < n; i++){
         if(list[i] == 1) nred++;
      }
// Initialize directions along coordinate axes
      alldir = new DatanMatrix(n, nred);
      f = muf.getValue(x0);
//System.out.println("x0 = " + x0.toString());
//System.out.println(" f = " + f);
      ired = -1;
      for(int i = 0 ; i < n; i++){
         if(list[i] == 1){
            ired++;
            dir = new DatanVector(n);
            dir.setElement(i, 1.);
            alldir.putColumn(ired, dir);
         }
      }
//System.out.println("alldir = " + alldir.toString());
      loop:
      for(istep = 1; istep <= nst; istep++){
//System.out.println("istep = " + istep);         
         fold = f;
         fl = fold;
         xold = new DatanVector(x0);
         deltaf = 0.;
         imax = -1;
// Loop over complete direction set and find direction 
// which yields largest decrease. Its index is imax.
         for(int i = 0; i < nred; i++){
            dir = alldir.getColumn(i);
            md = new MinDir(x0, dir, nst, eps, muf);
// test
//System.out.println("nst = " + nst + ", nstep = " + nstep + ", nstdir = " + nstdir);
//test
            if(! md.hasConverged()){
               converged = false;
               break loop;
            }
            x0 = md.getMinPosition();
            f = md.getMinimum();
//System.out.println("dir = " + dir.toString());
//System.out.println("min pos at " + md.getMinPosition() + ", f = " + f);  
            d = Math.abs(f - fl);
            if(d > deltaf){
               deltaf = d;
               imax = i;
            }
            fl = f;
         }
// Test for break-off criterion
//System.out.println("fold = " + fold + ", f = " + f + ", fl = " + fl);
         if(Math.abs(fl - fold) >= eps * Math.abs(fl) + T){
// Construct extrpolated point xe and direction dirn from xold and x0
            dirn = x0.sub(xold);
            xe = dirn.add(x0);
            fe = muf.getValue(xe);
// Now there are 3 points (xold, x0, xe)
// and their function values (fold, f, fe).
//System.out.println("fold = " + fold + ", f = " + f + ", fe = " + fe);             
            if(fe < f){
               test = 2. * (fold - 2. * f + fe) * Math.pow(fold - f - deltaf, 2.) - deltaf * Math.pow(fold - fe, 2.);
//System.out.println("test = " + test);  
               if(test < 0.){
// Find minimum along dirn and replace x0 by position of minimum.
// Replace direction with index imax by dirn
                  md = new MinDir(x0, dirn, nst, eps, muf);
                  if(! md.hasConverged()){
                     converged = false;
                     break loop;
                  }
                  x0 = md.getMinPosition();
                  alldir.putColumn(imax, dirn);                  
               }
            }
         }
         else{
            converged = true;
            break loop;
         }
      }
//System.out.println("istep = " + istep + ", nst = " + nst);
      if(istep == nst + 1 || ! converged){
         nst = -1;
      }
      else{
         nst = istep;
      }
   }

/**
* @return the number of iteration steps needed, -1 if iteration failed.
*/
   public int getSteps(){
      return nst;
   }

/**
* @return true if iteration successful, false otherwise.
*/
   public boolean hasConverged(){
      return converged;
   }
   
/**
* @return the postion of the minimum.
*/
   public DatanVector getMinPosition(){
      return x0;
   }
 
/**
* @return the function value at the minimum.
*/
   public double getMinimum(){  
      return fl;
   }   
   
}
