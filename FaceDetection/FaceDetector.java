/*
 * FaceDetector.java
 *
 * © andresmtz@gmail.com, 2008
 * Confidential and proprietary.
 */

// Bitmap & Graphics
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
// Encoded images used to store images in files
import net.rim.device.api.system.PNGEncodedImage;
// Write files
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
// Utils
import net.rim.device.api.util.IntVector;
import java.util.Vector;

/**
 * Identifies a face in a Bitmap image
 */
public class FaceDetector {
            
    // Bitmaps & Graphics
    private Bitmap bitmap_original, bitmap_processed;
    private Graphics graphics;            
    
    // Orginal 
    int[] argb_original, argb_process;
            
    // Parameters for image downscale
    private final int target_width = 100;
    private int scale;        
    
    // Parameters for K-Means
    private final int MAX_KMEANS_ITER = 10000;
    private final int K = 3, SUB_K = 9;    
    
    // Used to write images in files
    private FileConnection file_conn;
    private OutputStream output_stream;
    private String file_base_name;
    private final String file_ext = ".png";    
    
    public FaceDetector(byte image_data[], int image_size, String file_base_name) {        
        // Save base name of file (used to store images in debug mode)
        this.file_base_name = file_base_name;
        file_conn = null;
        output_stream = null;
        
        // 1. Read image as bitmap
        bitmap_original = Bitmap.createBitmapFromBytes(image_data,0,image_size,1); 
        
        // 2. Downscale original bmp image
        scale= bitmap_original.getWidth()/target_width;
        bitmap_original = null;
        bitmap_original = Bitmap.createBitmapFromBytes(image_data,0,image_size,scale);               
        
        // 3. Create a copy of bmp used to display partial results                         
        bitmap_processed = new Bitmap(bitmap_original.getType(), bitmap_original.getWidth(), bitmap_original.getHeight());
        graphics = new Graphics(bitmap_processed);
        
        // 4. Retrieve a vector with ARGB pixel dat
        argb_original = new int[bitmap_original.getWidth() * bitmap_original.getHeight()];
        argb_process = new int[argb_original.length];
        
        int offset = 0, scanlength = bitmap_original.getWidth();
        int xx = 0, yy = 0;
        bitmap_original.getARGB(argb_original, offset, scanlength, xx, yy, bitmap_original.getWidth(), bitmap_original.getHeight());                
        // arraycopy(Object src, int srcPos, Object dest, int destPos, int length) 
        System.arraycopy(argb_original,0,argb_process,0,argb_original.length);
        
                
        // 5. Clear Non-Skin color pixels
        int pix=0;
        // Reserve memory for max number of skin color points
        double points[][] = new double [argb_process.length][2];
        int skin[] = new int[argb_process.length];
        int n_skin_points = 0;                
        for(int i=0; i < argb_process.length; i++) {
            pix = argb_process[i];
            if (!Algorithms.isSkinColor(pix,Algorithms.RGB2)){                    
                // Isn't skin color
                argb_process[i] = 0x00FFFFFF;
                
                ////////////////////////
                //System.out.print(0);
                ////////////////////////
            } else {
                // store in skin color points X & Y, and position in argb array                        
                points[n_skin_points][Algorithms.X] = xx;
                points[n_skin_points][Algorithms.Y] = yy;
                skin[n_skin_points++] = i;
                ////////////////////////
                //System.out.print(1);
                ////////////////////////
            }
            xx++;
            if (xx == bitmap_original.getWidth()) {
                yy++;
                xx = 0;
                ////////////////////////
                //System.out.print("\n");
                ////////////////////////
            }                      
        }
        
        
        // Save processed image to file                                        
        imageToFile("skin", argb_process);
        
        // Use K-Means & segment face(s) [44] RanjaniBalaji.
        int cluster_assign[] = new int[n_skin_points];
        double centroids[][] = new double[K][2]; 
        //kMeans(int k, int dim, double points[][], int n_points, int cluster_assign[], double centroids[][], int max_iter)               
        Algorithms.kMeans(K,2,points,n_skin_points,cluster_assign,centroids,MAX_KMEANS_ITER);
        
        // Paint cluster points                
        for(int p=0;p<n_skin_points;p++) {
            switch(cluster_assign[p]){
                case 0: pix = 0x00FF0000; break;
                case 1: pix = 0x0000FF00; break;
                case 2: pix = 0x000000FF; break;
                case 3: pix = 0x00FFFF00; break;                
            }
            argb_process[skin[p]]=pix;
        }
                
        // Save processed image to file                                        
        //imageToFile("klusters", argb_process);
        
    }
    
    public void getFaceRegions() {
        // Store x & y coords. of skin pixels
        double skin_pixels[][] = new double [argb_original.length][2];      
                        
        getSkinPixelList(this.argb_original, skin_pixels);
        
        // Store x & y coords. of skin regions
        IntVector skin_regions_x_min = new IntVector();
        IntVector skin_regions_y_min = new IntVector();
        IntVector skin_regions_x_max = new IntVector();
        IntVector skin_regions_y_max = new IntVector();
        
    }
    
    private void getPossibleSkinRegions(double skin_pixels[][], int n_skin_points, 
                                        IntVector skin_regions_x_min, IntVector skin_regions_y_min,
                                        IntVector skin_regions_x_max, IntVector skin_regions_y_max) {
        
        // Use K-Means & segment face(s) [44] RanjaniBalaji.
        
        // Main 3 cluster_assign and 3 main centroids
        int cluster_assign[] = new int[n_skin_points];
        double centroids[][] = new double[K][2];
                                 
        //          kMeans(int k, int dim, double points[][], int n_points, int cluster_assign[], double centroids[][], int max_iter)               
        int cluster_size[] = Algorithms.kMeans(K,2,skin_pixels,n_skin_points,cluster_assign,centroids,MAX_KMEANS_ITER);
                                
        // Stores the assigned points to each 'SUB_K sub-clusters' of the 'K Main clusters'
        int cluster_sub_assign[][] = new int[K][];
        double centroids_sub[][][]  = new double[K][SUB_K][2];
                
        // Paint cluster points                
        int pix=0, i=0; 
        int width = this.bitmap_original.getWidth();
        for(int p=0;p<n_skin_points;p++) {
            switch(cluster_assign[p]){
                case 0: 
                    pix = 0x00FF0000; 
                    break;
                case 1: 
                    pix = 0x0000FF00; 
                    break;
                case 2: 
                    pix = 0x000000FF; 
                    break;             
            }
            i = (int)skin_pixels[p][1]*width + (int)skin_pixels[p][0];
            argb_process[i]=pix;
        }
                
        // Save processed image to file                                        
        imageToFile("klusters", argb_process);
    }
    
    private void getSkinPixelList(int argb_original[], double skin_pixels[][]) {        
        // Copy pixels to show partial results image              
        //int argb_process[] = new int[argb_original.length];        
        //System.arraycopy(argb_original,0,argb_process,0,argb_original.length);
                
        // Identify skin color pixels
        int x=0, y=0;  
        int n_skin_points = 0;             
        for(int i=0; i < argb_process.length; i++) {           
            if (!Algorithms.isSkinColor(argb_process[i],Algorithms.RGB2)){                    
                // Isn't skin color
                argb_process[i] = 0x00FFFFFF;                
            } else {
                // store in skin color coords
                skin_pixels[n_skin_points][0] = x;
                skin_pixels[n_skin_points][1] = y;  
                n_skin_points++;
            }
            x++;
            if (x == bitmap_original.getWidth()) { y++; x = 0; }
        }
                
        // Save processed image to file                                        
        imageToFile("skin", argb_process);        
    }
    
    public void imageToFile(String file_postfix, int argb[]){        
        try {
            // open file
            file_conn = (FileConnection) Connector.open( "file:///SDCard/blackberry/pictures/" + this.file_base_name + "_" + file_postfix +this.file_ext, Connector.READ_WRITE);        
            if (!file_conn.exists()){
                //create the file first
                file_conn.create();  
            } 
            file_conn.setWritable(true);                                  
            output_stream = file_conn.openOutputStream();                        
            
            // Clean graphics
            graphics.clear();
            
            // Draw Bitmap
            graphics.drawRGB(argb, 0, bitmap_processed.getWidth(), 0, 0, bitmap_processed.getWidth(), bitmap_processed.getHeight());
            
            // Encode image as png
            PNGEncodedImage png_enc_img = PNGEncodedImage.encode(this.bitmap_processed);            
            // Write image to file
            output_stream.write(png_enc_img.getData());                          
                  
        } catch(Exception e) {                
            System.out.println("Error: " + e.toString());
        } finally {
            // Always close file
            try {
                if (output_stream!=null) {
                    output_stream.close();
                    output_stream = null;
                }
                if (file_conn!=null) {
                    file_conn.close();
                    file_conn = null;
                }
            } catch(Exception e) {
                System.out.println("Error: " + e.toString());
            }
        }
    }
    
    public Bitmap getOriginalBitmap() {
        return this.bitmap_original;
    }
    
    public Bitmap getProcessedBitmap() {
        return this.bitmap_processed;
    }
} 
