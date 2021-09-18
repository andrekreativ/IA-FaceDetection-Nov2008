/*
 * Face Detection 0.8b
 * © Andres Martinez Andrade (andresmtz), 2008
 */
 
// Read files
import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
// Bitmap & Graphics
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;
// User Interface
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;

/* The ColorKMeansDetector.java sample retrieves raw data from a predefined bitmap
   image, and then draws a new bitmap using the data. It then displays 
   the original_bitmap and bitmap_ycrcb images. */
public class ColorKMeansDetector extends UiApplication {
     
    public static void main(String[] args) {
        try {
            ColorKMeansDetector app = new ColorKMeansDetector();
            app.enterEventDispatcher();
            //SnapperMIDlet app = new SnapperMIDlet();
        } catch(Exception e) {
            System.out.print("Exception: " + e.toString() + "\n");
        }
    }
    
    public ColorKMeansDetector() {
        pushScreen(new ColorKMeansDetectorScreen());        
    } 
}

final class ColorKMeansDetectorScreen extends MainScreen {                 
    // Rules to identify Skin Color Pixels
    private final int RGB = 0;
    private final int YCRCB = 1;
    private final int RGB2 = 2;
    // Variables used in skin color identification
    private byte pixel_byte;
    private int r,g,b;        
    // Variables used in RGB rules
    private int min,max;
    private double gb;
    // Variables used in YCRCB rule
    private double y,cr,cb;
    // Parameters to convert RGB to YCRCB using  601 and NTSC 709 Standards
    private final double CA = 0.2568;
    private final double CB = 0.0979;       
    // Parameters for K-Means
    private final int MAX_KMEANS_ITER = 10000;
    private final int K = 3;
               
    /**
    * Determines if a pixel has skin color
    * @param pixel: an int argb data
    * @param rule: an int with the rule to use
    * @return true if pixel is skin color */           
    private boolean isSkinColor(int pixel, int rule) {
        // Get RGB components of color
        r = pixel & 0x00FF0000;                
        g = pixel & 0x0000FF00;                         
        b = pixel & 0x000000FF;
        
        r = r >>> 16;
        g = g >>> 8;                             
                                                                                
        if (rule==YCRCB) { 
            // calc Luminance component, Chrominance component (Red), Chrominance component (Blue)
            //y       = 0.2989*r      + 0.5866*g + 0.1145*b;
            //cb      = -0.1688*r     - 0.3312*g + 0.5*b;
            //cr      = 0.5*r         - 0.4184*g - 0.0816*b;
            y = CA*r + (1-CA-CB)*g + CB*b;
            cb = -CA*r + (CA+CB-1)*g + (1-CB)*b;
            cr = (1-CA)*r + (CA+CB-1)*g - CB*b;
            /* Apply Skin Rule YCRCB [6] D. Chai and K. N. Ngan, */                         
            return cr >= 133 && cr <= 173.00 && cb >= 77 && cb <= 127.00; 
        } else if (rule==RGB2){
            if (r>g && r>b && g>b){
                    return true;
            }
            return false;
        } else {
            min = Math.min(r,Math.min(g,b));
            max = Math.max(r,Math.max(g,b));
            gb = g/(double)b;
            if ((r>95 && g>40 && b>20 && r>g && r>b && Math.abs(r-g)>15 && max-min>15)||(r>100 && g>100 && b>100 && gb>=1.1 && gb <= 2.5)){                    
                    /* Apply 'Skin Color RGB Rule' [11]PEER, P., KOVAC, J., AND SOLINA, F. 2003.  */
                    return true;
            }
            return false;
        }
    }
    
    public ColorKMeansDetectorScreen() {
        super();
        // Config UI
        LabelField title = new LabelField("Face Detection 0.8b", LabelField.USE_ALL_WIDTH);
        setTitle(title);        
        // Resize values
        // int width = 128, height=96, scale =1;
        int width = 100, height=75, scale =1;        
        // File pointers
        FileConnection fconn;
        InputStream isfile = null;
        // Buffer to store image data
        byte data[] = new byte[60000];
        int size;        
        // File name
        String file_name = "foto.jpg";        
        try {
            // Get Pointer to the file
            fconn = (FileConnection)Connector.open("file:///SDCard/blackberry/pictures/" + file_name);
            if (fconn.exists()) {
                // Open & read file
                isfile = fconn.openInputStream();
                size = isfile.read(data);
                fconn.close();
                fconn = null;                
                // 1. Convert to bmp            
                Bitmap original_bitmap = Bitmap.createBitmapFromBytes(data,0,size,1);
                // 2. Downscale bmp image
                scale= original_bitmap.getWidth()/width;
                original_bitmap = null;
                original_bitmap = Bitmap.createBitmapFromBytes(data,0,size,scale);                
                // 3. Create a copies of bmp                                
                Bitmap bitmap_rgb = new Bitmap(original_bitmap.getType(), original_bitmap.getWidth(), original_bitmap.getHeight());
                Graphics graphics_rgb = new Graphics(bitmap_rgb);
                // 4. Retrieve a vector with raw data from original_bitmap image.
                int[] argb  = new int[original_bitmap.getWidth() * original_bitmap.getHeight()];     
                // public void getRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height)           
                int offset = 0, scanlength = original_bitmap.getWidth();
                int xx = 0, yy = 0;
                original_bitmap.getARGB(argb, offset, scanlength, xx, yy, original_bitmap.getWidth(), original_bitmap.getHeight());                
                
                // 5. Clear Non-Skin color pixels
                int pix=0;
                // Reserve memory for max number of skin color points
                double points[][] = new double [argb.length/2][2];
                int skin[] = new int[argb.length/2];
                int n_skin_points = 0;                
                for(int i=0; i < argb.length; i++) {
                    pix = argb[i];
                    if (!isSkinColor(pix,RGB)){                    
                        // Isn't skin color
                        argb[i] = 0x00FFFFFF;
                        
                        ////////////////////////
                        System.out.print(0);
                        ////////////////////////
                    } else {
                        // store in skin color points X & Y, and position in argb array                        
                        points[n_skin_points][Algorithms.X] = xx;
                        points[n_skin_points][Algorithms.Y] = yy;
                        skin[n_skin_points++] = i;
                        ////////////////////////
                        System.out.print(1);
                        ////////////////////////
                    }
                    xx++;
                    if (xx == original_bitmap.getWidth()-1) {
                        yy++;
                        xx = 0;
                        ////////////////////////
                        System.out.print("\n");
                        ////////////////////////
                    }                      
                }
                
                // Use K-Means & segment face(s) [44] RanjaniBalaji.
                int clusters[] = new int[n_skin_points];
                double centroids[][] = new double[K][2]; 
                //kMeans(int k, int dim, double points[][], int n_points, int clusters[], double centroids[][], int max_iter)               
                Algorithms.kMeans(K,2,points,n_skin_points,clusters,centroids,MAX_KMEANS_ITER);
                
                // Paint cluster points                
                for(int p=0;p<n_skin_points;p++) {
                    switch(clusters[p]){
                        case 0: pix = 0x00FF0000; break;
                        case 1: pix = 0x0000FF00; break;
                        case 2: pix = 0x000000FF; break;
                    }
                    argb[skin[p]]=pix;
                }
                        
                // Draw new image using skin color pixels                                
                graphics_rgb.drawRGB(argb, 0, bitmap_rgb.getWidth(), 0, 0, bitmap_rgb.getWidth(), bitmap_rgb.getHeight());

                // Show original_bitmap & skin color image
                BitmapField field_original_bitmap = new BitmapField(original_bitmap);                
                                BitmapField field_rgb = new BitmapField(bitmap_rgb);                 
                //add(new LabelField("original_bitmap bitmap: "));
                add(field_original_bitmap);                
                //add(new LabelField("RGB: "));
                add(field_rgb);
            } else {
                // file not found
                add(new LabelField("File not found: " + file_name));
            }                
                                  
        } catch (Exception ioe) {
            add(new LabelField("Exception: " + ioe.toString()));
        }     
    }// end Draw Demo
}
