/*
 * FaceDetector.java
 *
 * © andresmtz@gmail.com, 2008
 * Confidential and proprietary.
 */
 
// Bitmap Support
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Graphics;          // Draw in Bitmaps    
// RGB Images Support & Applied Algorithms
import jjil.core.RgbImage;                      
import jjil.algorithm.RgbShrink;                // Downscale
import jjil.algorithm.RgbVertGaussSmooth;       // Gauss Filter
import jjil.algorithm.RgbHorizGaussSmooth;      // Gauss Filter
// Error in JJIL Process
import jjil.core.Error;

  
/**
 * Identifies a faces in an image
 */
public class FaceDetector2 {
            
    // Bitmaps & Graphics
    private Bitmap bitmap_original, bitmap_processed;
    
    
    /**
     * Creates a Face Detector instance
     * @param image_data : an array with image bytes
     * @param image_size : the size of the image bytes' array
     * @param gauss_nSigma: the sigma value of window to blur over in gaussian filter
     */
    public FaceDetector2(byte image_data[], int image_size, int scale_width, int gauss_nSigma) throws Error {    
        
        // 1. Read image as bitmap
        bitmap_original = Bitmap.createBitmapFromBytes(image_data,0,image_size,1);
        
        // 2. Retrieve a vector with ARGB pixel data
        int[] argb = new int[bitmap_original.getWidth() * bitmap_original.getHeight()];             
        int offset = 0, scanlength = bitmap_original.getWidth();
        int xx = 0, yy = 0;
        bitmap_original.getARGB(argb, offset, scanlength, xx, yy, bitmap_original.getWidth(), bitmap_original.getHeight());
        
        // 3. Downscale original bitmap image using ARGB pixel data and save it as RGB Image
        RgbImage rgb_image = new RgbImage(bitmap_original.getWidth(), bitmap_original.getHeight());
        // 3.1 Copy original image's data        
        System.arraycopy(argb, 0,rgb_image.getData(), 0, argb.length);        
        // 3.2 Calculate the resize height
        double scale = bitmap_original.getWidth()/(double)scale_width;
        int scale_height = (int)(bitmap_original.getHeight() / scale);
        // 3.3 Resize the image
        RgbShrink rgb_shrink = new RgbShrink(scale_width, scale_height);
        //rgb_shrink.push(rgb_image);
                
        // 4. Apply Gauss Filters ...
        RgbVertGaussSmooth rgb_vert_gauss = new RgbVertGaussSmooth(gauss_nSigma);
        RgbHorizGaussSmooth rgb_horiz_gauss = new RgbHorizGaussSmooth(gauss_nSigma);
        //rgb_vert_gauss.push(rgb_image);
        //rgb_horiz_gauss.push(rgb_image);
        
        // Save processed image in bitmap
        bitmap_processed = new Bitmap(bitmap_original.getType(), scale_width, scale_height);
        Graphics graphics_processed = new Graphics(bitmap_processed);
        graphics_processed.drawRGB(rgb_image.getData(), 0, scale_width, 0, 0, scale_width, scale_height);             
    }
    
    public Bitmap getOriginalBitmap() {
        return this.bitmap_original;
    }
    
    public Bitmap getProcessedBitmap() {
        return this.bitmap_processed;
    }
} 
