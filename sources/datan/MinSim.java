package datan;

/**
* A class determining the minimum of a function in n-dimensional space by the simplex method.
* @author  Siegmund Brandt.
*/
public final class MinSim {
   static double EPSDEF = 1.E-25, TT = 1.E-30, ALPHA = 1., BETA = 0.5, GAMMA = 2., EDGEDEF = 1.E-5;
   static int MAXSTP = 1000;
   DatanUserFunction muf;
   double d, eps, yr, yr2, ymin;
   double[] y;
   int ired, istep, n, ne, nst, nred, m, mn, mx;
   DatanVector x0, x1, x2, xmx, xmn, xr, xr2, xexp, xprime;
   DatanMatrix x;
   boolean converged;
   int[] list;

   public MinSim(DatanVector x0, int[] list, DatanUserFunction muf) {   
      this(x0, list, 0, 0., 0., muf);
   }

   public MinSim(DatanVector x0,  DatanUserFunction muf) {
      list = new int[x0.getNumberOfElements()];
      for(int i = 0; i < x0.getNumberOfElements(); i++){
         list[i] = 1;
      }
      this.x0 = new DatanVector(x0);
      this.muf = muf;
      eps = EPSDEF;
      d = EDGEDEF;
      nst = MAXSTP;
      initialize();
      if(nred == 0){
         trivial();
      }
      else{
         compute();
      }
   }

/**
* @param x0 initial point in n-space.
* @param list array containing the elements of a list specifying which of the n variables are fixed (list element = 0) and
* which are adjustable (list element = 1).
* @param nstep maximal step number. Default: if a value <= 0 is used, then 1000 is taken instead.
* @param epsilon accuracy. Default if a value <= 0. is used, then 1,E-8 is taken instead.
* @param edge typical side length of the initial simplex. Default: if a value <= 0. is used, then 1,E-5 is taken instead.
* @param muf user function which must be an extension of the abstract class DatanUserFunction.
*/
   public MinSim(DatanVector x0, int[] list, int nstep, double epsilon, double edge, DatanUserFunction muf) {
      eps = epsilon;
      this.x0 = new DatanVector(x0);
      nst = nstep;
      d = edge;
      this.muf = muf;
      this.list = list;
      eps = epsilon;
      if(eps <= 0.)eps = EPSDEF;
      if(d <= 0.) d = EDGEDEF;
      if(nst <= 0) nst = MAXSTP;
      initialize();
      if(nred == 0){
         trivial();
      }
      else{
         compute();
      }
   }

   

   private void initialize(){
// initialize n (number of variables), nred (number of adjustable variables), and m (number of simplex corner points)
      n = x0.getNumberOfElements();
      nred = 0;
      for(int i = 0; i < n; i++){
         if(list[i] == 1) nred++;
      }
      m = nred + 1;
   }

   private void compute(){
// construct initial simplex and compute function at its corners
      y = new double[m];
      y[0] = muf.getValue(x0);
      x1 = x0.getSubvector(list);
      x = new DatanMatrix(nred, m);
// System.out.println("n = " + n + ", m = " + m);
      x.putColumn(0,x1);
      ired = -1;
      for(int i = 0; i < n; i++){
         if(list[i] == 1){
            ired++;
            x2 = new DatanVector(x1);
            x2.setElement(ired, x2.getElement(ired) + d);
            x.putColumn(ired+1, x2);
            xexp = new DatanVector(x0);
            xexp.putSubvector(x2, list);
            y[ired+1] = muf.getValue(xexp);
         }
      }
      findProminentCorners();
      istep = 0;
      while(Math.abs(y[mx] - y[mn]) >= eps * Math.abs(y[mn]) + TT && istep < nst){
         istep++;
// compute xprime, i.e., point in center of gravity of hyperplane
// opposite to point with largest function value
         xmx = x.getColumn(mx);
         xmn = x.getColumn(mn);
         xprime = new DatanVector(nred);
         for(int i = 0; i < nred + 1; i++){
            if(i != mx){
               x1 = x.getColumn(i);
               xprime = xprime.add(x1);
            }
         }
         xprime = xprime.multiply(1. / (double) nred);

//         if(istep < 10) System.out.println("xprime : " + xprime.toString());

// construct points by reflection (xr) and extension (xr2)
         x1 = xprime.multiply(1. + ALPHA);
         x2 = xmx.multiply(ALPHA);
         xr = x1.sub(x2);
         xexp = new DatanVector(x0);
         xexp.putSubvector(xr, list);
         yr = muf.getValue(xexp);
         if(yr <= y[mn]){
            x1 = xr.multiply(GAMMA);
            x2 = xprime.multiply(1. - GAMMA);
            xr2 = x1.add(x2);
            xexp = new DatanVector(x0);
            xexp.putSubvector(xr2, list);
            yr2 = muf.getValue(xexp);
            if(yr2 < y[mn]){
// perform extension
               x.putColumn(mx, xr2);
               y[mx] = yr2;
            }
            else{
// perform reflection
               x.putColumn(mx, xr);
               y[mx] = yr;
            }
         }
         else if(yr >= y[ne]){
            if(yr < y[mx]){
// perform reflection
               x.putColumn(mx, xr);
               y[mx] = yr;               
            }
            x1 = xmx.multiply(BETA);
            x2 = xprime.multiply(1. - BETA);
            xr2 = x1.add(x2);
            xexp = new DatanVector(x0);
            xexp.putSubvector(xr2, list);
            yr2 = muf.getValue(xexp);
            if(yr2 < y[mx]){
// perform compression
               x.putColumn(mx, xr2);
               y[mx] = yr2;
            }
            else{
// perform contraction
               for(int i = 1; i < nred + 1; i++){
                  if(i != mn){
                     x1 = x.getColumn(i);
                     x2 = x1.add(xmn);
                     x2 = x2.multiply(0.5);
                     x.putColumn(i, x2);
                     xexp = new DatanVector(x0);
                     xexp.putSubvector(x2, list);
                     y[i] = muf.getValue(xexp);
                  }
               }
            }
         }
         else{
// perform reflection
            x.putColumn(mx, xr);
            y[mx] = yr;
         }         
         findProminentCorners();
      }
      ymin = y[mn];
      x1 = x.getColumn(mn);
      x0.putSubvector(x1, list);
      if(istep == nst){
         nst = -1;
         converged = false;
      }
      else{
         nst = istep;
         converged = true;
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
      return ymin;
   }   


   private void findProminentCorners(){
// compute indices of corner with largest (mx), next to largest (ne) and smallest (mn) function value
      mn = 0;
      if(y[0] < y[1]){ mx = 0; ne = 1;}
      else { mx = 1; ne = 0;}
      for(int i = 0; i < nred + 1; i++){
         if (y[i] < y[mn]) mn = i;
         if (y[i] > y[mx]){
            ne = mx;
            mx = i;
         }
         else if (y[i] > y[ne]){
            if(i != mx) ne = i;
         }
      }

//      if(istep < 10) System.out.println("mx = " + mx + ", ne = " + ne + ", mn = " + mn);

//      if(istep < 100) System.out.println("y[mn] = " + y[mn]); 
   }

   private void trivial(){
// deals with trivial case (nred = 0)
      ymin = muf.getValue(x0);
      nst = 0;
      converged = true;
   }

   
}
