import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Random;
import net.rim.device.api.math.Fixed32;

/*
 * Algorithms.java
 *
 * © andresmtz@gmail.com, Nov. 2008
 * Confidential and proprietary.
 */


public final class Algorithms {
    
    /////////////////////////////////////////////////////////////////////////
    // K - Means
    /////////////////////////////////////////////////////////////////////////
    
    // Defines which position of array is X & Y
    public static final int X = 0, Y = 1;    
    // aux variables for euclidean distance
    private static double _x, _y;
    
    
    /**
     * Groups the given points in the given cluster_assign
     * @param k: the number of cluster_assign
     * @param dim: the number of dimensions
     * @param points: a matrix with points
     * @param n_points: number of points (limit the rows of the matrix 'points')
     * @param cluster_assign: an array to save cluster assignation     
     * @param max_iter: max number of iterations
     */
    public static int[] kMeans(int k, int dim, double points[][], int n_points, int cluster_assign[], double centroids[][], int max_iter) {        
        
        //////////////////
        //System.out.print("\n- - K-Means - -\n");
        //////////////////
        
        
        // counts iterations
        int iter = 0;
        // counts changes in cluster assignments
        int changes;
        
        // aux. to store min distance of point to a centroid
        double min_distance, distance;
        // aux. previous cluster
        int prev_cluster, nearest_cluster;
        
        // Stores sum of all the points in a cluster
        double cluster_sum[][] = new double[k][dim];
        // Stores the number of points assigned to each cluster_assign
        int cluster_size[]= new int[k];
        
        //////////////////
        //System.out.print("\n1. Asignacion inicial a cluster_assign:\n");
        //////////////////
        
        
        // 1. Assign points to a cluster
        int cluster=0;
        for(int p=0; p < n_points; p++) {
            cluster_assign[p] = cluster;
            //////////////////
            //System.out.print("p"+ p + " -> c"+ cluster);
            //////////////////
            
            // Add all dimensions of the point
            for(int d=0; d<dim; d++) {
                cluster_sum[cluster][d] += points[p][d];    
                
                //////////////////
                //System.out.print("\t" + points[p][d]);
                //////////////////            
            }
            
            //////////////////
            //System.out.print("\n");
            //////////////////
            
            // update cluster size
            cluster_size[cluster]++;
            
            
            cluster++;
            if (cluster==k) cluster = 0;
        }        
        
        //////////////////
        //System.out.print("\n2. Elegir centroides aleatorios:\n");
        //////////////////
        
        // 2. Select pseudo random centroids...
        Random r = new Random(System.currentTimeMillis());
        Hashtable random_index = new Hashtable(k);                        
        Integer center;
        for(int i=0; i < k; i++){
            do center = new Integer ((int) (n_points * r.nextDouble()));
            while (random_index.contains(center));
            random_index.put(center, Boolean.TRUE);
        }
        
        // Copy coords of random centroids
        Enumeration centroid_ran_indexes = random_index.keys();
        int cent=0;        
        while(centroid_ran_indexes.hasMoreElements()){
            center = (Integer) centroid_ran_indexes.nextElement();
            //////////////////
            //System.out.print("c" + cent + ": p" + center);
            //////////////////
            for(int d=0; d<dim; d++) {
                centroids[cent][d] = points[center.intValue()][d];
                //////////////////
                //System.out.print("\t" + centroids[cent][d]);
                //////////////////
            }             
            cent++;
            //////////////////
            //System.out.print("\n");
            //////////////////
        }
                
                        
        //////////////////
        //System.out.print("\n3. Iteraciones: (calcular distancias y reasignar a cluster_assign)\n");
        //////////////////
        
        do {
            
            //////////////////
            //System.out.print("\n\nIteracion " + iter + ":\n");            
            //System.out.print("\n    ");
            //for(int c=0; c < k; c++)
                //System.out.print("\t c"+c);
            //System.out.print("\tsAsignar");
            //////////////////
                                    
            
            // reset changes
            changes = 0;
            // for each point ...
            for(int p=0; p < n_points; p++) {
                
                ///////////////////
                //System.out.print("\np"+p);
                //////////////////
                
                // reset min distance of point
                min_distance = Double.MAX_VALUE;   
                nearest_cluster = cluster_assign[p];             
                // for each centroid ...
                for(int c=0; c < k; c++) {
                                                            
                    // 4. calc distance to centroid                    
                    distance = euclideanDistance(points[p],centroids[c]);
                    
                    //////////////////                    
                    //System.out.print("\t"+distance);
                    //////////////////
                    
                    if (distance < min_distance){
                        // is the neareast centroid to point p ..
                        nearest_cluster = c;                        
                        // update min distance of point p
                        min_distance = distance;                        
                    }
                }
                
                // if assigned cluster is different from previous cluster...
                if (cluster_assign[p] != nearest_cluster) {
                    // the prev cluster 
                    prev_cluster = cluster_assign[p];
                    
                    // update size of prev cluster                    
                    cluster_size[prev_cluster]--;
                    
                    // update sum in prev cluster
                    for(int d=0; d<dim; d++)
                        cluster_sum[prev_cluster][d] -= points[p][d];                    
                    
                    // update size of new cluster                    
                    cluster_size[nearest_cluster]++;
                    // update sum in nearest cluster
                    for(int d=0; d<dim; d++)
                        cluster_sum[nearest_cluster][d] += points[p][d];
                    
                    // assign new cluster
                    cluster_assign[p] = nearest_cluster;     
                    changes++;                       
                }
                
                //////////////////
                //System.out.print("\tc" + cluster_assign[p]);
                //////////////////
                
            } // end for points
            
            //////////////////
            //System.out.print("\n\nCambios: " + changes);
            //////////////////
            
            //////////////////
            //System.out.print("\n\nRecalcular Centroides:\n");
            //////////////////
                                    
            // 5. locate new centroids using avg
            for(int c=0; c < k; c++) {
                //////////////////
                //System.out.print("\nc" + c);
                //////////////////
                for(int d=0; d<dim; d++) {  
                    //////////////////
                    //System.out.print("\t" + cluster_sum[c][d] + " / " + ((double) cluster_size[c]) + " = ");
                    //////////////////
                    centroids[c][d] = cluster_sum[c][d] / (double) cluster_size[c];
                    //////////////////
                    //System.out.print("\t" + centroids[c][d]);
                    //////////////////
                }
            }                         
            //////////////////
            //System.out.print("\n");
            //////////////////
        } while (changes > 0 && ++iter < max_iter);
        // iterate while having changes in cluster assignation and iterations is smaller than max
        
        return cluster_size;
    }
    
    /**
     * Returns the euclidean distance between 2 points
     * @param x1: x-coordinate of 1st point
     * @param y1: y-coordinate of 1st point
     * @param x2: x-coordinate of 2nd point
     * @param y2: y-coordinate of 2nd point
     * @return a double with the euclidean distance
     */
    public static double euclideanDistance(double x1, double y1, double x2, double y2) {        
        _x = x1 - x2;
        _y = y1 - y2;
        return Math.sqrt(_x*_x + _y*_y);
    }
    
    /*
     * Returns the euclidean distance of two N-dimensional points
     * @param p1: an array with the values for the 1st point
     * @param p2: an array with the values for the 2nd point
     */
    public static double euclideanDistance(double p1[], double p2[]){
        _x = 0.0;
        for(int i=0; i<p1.length; i++){
            _y = p1[i]-p2[i];
            _x += _y*_y;
        }
        return Math.sqrt(_x);
    }
    
    /////////////////////////////////////////////////////////////////////////
    // Skin color
    /////////////////////////////////////////////////////////////////////////
    
    // Rules to identify Skin Color Pixels
    public static final int RGB     = 0;
    public static final int YCRCB   = 1;
    public static final int RGB2    = 2;
    public static final int YUV_YIQ = 3;
    // Variables used in skin color identification
    private static byte pixel_byte;
    private static int r,g,b;        
    // Variables used in RGB rules
    private static int min,max;
    private static double rg, rb, gr, gb, br, bg;
    // Variables used in YCRCB rule
    private static double y,cr,cb;
    // Variables used in YUV-YIQ rule
    private static double u, v;
    private static double I, Q,ch,hue;    
    private static int hue_fix;
    // Parameters to convert RGB to YCRCB using  601 and NTSC 709 Standards
    private static final double CA = 0.2568;
    private static final double CB = 0.0979; 
    
    
    /**
    * Determines if a pixel has skin color
    * @param pixel: an int argb data
    * @param rule: an int with the rule to use
    * @return true if pixel is skin color */           
    public static boolean isSkinColor(int pixel, int rule) {
        // Get RGB components of color
        r = pixel & 0x00FF0000;                
        g = pixel & 0x0000FF00;                         
        b = pixel & 0x000000FF;
        
        r = r >>> 16;
        g = g >>> 8;                             
        
        switch(rule) {
            case YUV_YIQ:
                y   = 0.299*r   + 0.587*g   + 0.114*b;
                u   = -0.147*r  - 0.289*g   + 0.436*b;
                v   = 0.615*r   - 0.515*g   - 0.1*b;
                I   = 0.596*r   - 0.274*g   -0.322*b;
                Q   = 0.211*r   - 0.523*g   +0.312*b;
                // calc hue & saturation (ch)                
                hue_fix = Fixed32.ArcTan(Fixed32.tenThouToFP((int)Math.toRadians(Math.abs(v/u)*10000)));
                hue_fix = Fixed32.toIntTenThou(hue_fix);
                hue = (double)hue_fix/10000;
                hue = Math.toDegrees(hue);
                ch = Math.sqrt(u*u + v*v);
                return hue>=80.0/10.0 &&  hue <=150.00/10.0 && I>= 10.0/10.0 && I <=55.0/10.0;  
             case YCRCB:
                // calc Luminance component, Chrominance component (Red), Chrominance component (Blue)
                //y       = 0.2989*r      + 0.5866*g + 0.1145*b;
                //cb      = -0.1688*r     - 0.3312*g + 0.5*b;
                //cr      = 0.5*r         - 0.4184*g - 0.0816*b;
                cr   = 0.615*r   - 0.515*g   - 0.1*b;
                //y = CA*r + (1-CA-CB)*g + CB*b;
                //cb = -CA*r + (CA+CB-1)*g + (1-CB)*b;
                //cr = (1-CA)*r + (CA+CB-1)*g - CB*b;
                                    
                /* Apply Skin Rule YCRCB [6] D. Chai and K. N. Ngan, */                         
                return cr >= 133/10.0 && cr <= 173.00/10.0;// && cb >= 77/2.0 && cb <= 127.00/2.0; 
              case RGB:
                min = Math.min(r,Math.min(g,b));
                max = Math.max(r,Math.max(g,b));                
                return (r>95 && g>40 && b>20 && r>g && r>b && Math.abs(r-g)>15 && max-min>15);
                        /* Apply 'Skin Color RGB Rule' [11]PEER, P., KOVAC, J., AND SOLINA, F. 2003.  */ 
              case RGB2:
                //"near black, white, red, green, blue, yellow, magenta or cyan"                              
                gr = g/(double)r;
                br = b/(double)r;
                                
                rg = r/(double)g;
                bg = b/(double)g;
                
                rb = r/(double)b;
                gb = g/(double)b;                                
                
                /* Apply 'Skin Color RGB Rule' [44] */                 
                return 
                    // basis rule
                    ((r>g && r>b && g>b) && 
                    // not near red, green or blue, and also not near white
                    r < 225 && g < 225 && b < 225 &&
                    // not near black
                    (r>20 && g>20 && b>20) &&
                    // not near cyan
                    !(Math.abs(g-b)<50.0 && gr>1.6 && br>1.6) &&
                    // not near magenta
                    !(Math.abs(r-b)<20.0 && rg>1.6 && bg >1.6) &&
                    // not near yellow
                    !(Math.abs(r-g)<20.0 && rb>1.6 && gb >1.6) &&
                    // aditional rule ;-)
                    !(rg > 0.91 && rg < 2.24 && gb <2.4 && gr > .91)
                    )
                    // exception rule for bright pixels
                    ||(r>100 && g>100 && b>100 && gb>=1.1 && gb <= 2.5)
                    ;           
              default: return true;
        }
    }
} 
