/*
 * Face Detection 0.6b
 * © Andres Martinez Andrade (andresmtz), 2008
 */
//package com.rim.samples.docs.drawing;
import java.io.*;
import javax.microedition.io.*;
import javax.microedition.io.file.*;
import net.rim.device.api.system.*; 
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;

/* The DrawDemo.java sample retrieves raw data from a predefined bitmap
   image, and then draws a new bitmap using the data. It then displays 
   the original and restored images. */
public class DrawDemo extends UiApplication {
     
    public static void main(String[] args) {
        DrawDemo app = new DrawDemo();
        app.enterEventDispatcher();
    }
    
    public DrawDemo() {
        pushScreen(new DrawDemoScreen());
    } 
}

final class DrawDemoScreen extends MainScreen { 
    
    // Rules for Skin Color Discrimination
    private final int USE_RGB       = 0;
    private final int USE_RGB_MOD   = 1;
    private final int USE_YCRCB     = 2;
    // Variables used in skin color discrimination
    private byte pixel_byte;
    private int r,g,b;      
    private int min,max;        // used in RGB    
    private double y,cr,cb;     // used in YCRCB
    
    /**
     * Determines if a pixel has a skin color
     * @param an int with pixel argb data<description>
     * @param an int with the rule to use<description>
     * @return <description>
     */
    private bool isSkinColor(int pixel, int rule){
        // Get RGB components of color    
        pixel_byte = (byte)((argb[i] << 24) >> 24 );
        b = (int)pixel_byte+127;
        pixel_byte = (byte)((argb[i] << 16) >> 24 );
        g = (int)pixel_byte+127;
        pixel_byte = (byte)((argb[i] << 8) >> 24 );
        r = (int)pixel_byte+127;
        
        if (rule == USE_RGB) {        
            min = Math.min(r,Math.min(g,b));
            max = Math.max(r,Math.max(g,b));                    
            /**
            * 
            * Apply 'Skin Color RGB Rule'
            * [11]PEER, P., KOVAC, J., AND SOLINA, F. 2003. 
            * Human skin colour clustering for face detection. 
            * In submitted to EUROCON 2003 - InternationalConference on Computer as a Tool.
            * */
            return r>95 && g>40 && b>20 && (max-min>15) && Math.abs(r-g)>15 && r>g && r>b;
        } else if (rule == USE_RGB_MOD) {
            return true;
        } else {
            // Calc YCRCB components...
            // Luminance component (Y)
            y = 0.299*r + 0.587*g + 0.114*b;
            // Chrominance component (Red)
            cr = -0.1688*r - 0.3312*g + 0.5*b;
            // Chrominance component (Blue)
            cb = 0.5*r - 0.4184*g - 0.0816*b;
            return cr >= 133 && cr <= 173 &&  cb >= 77 && cb <= 127;
        }        
    }
    
    public DrawDemoScreen() {
        super();
        // Config UI
        LabelField title = new LabelField("Face Detection 0.6b", LabelField.USE_ALL_WIDTH);
        setTitle(title);        
        // Resize values
        int width = 128, height=96, scale =1;
        // File pointers
        FileConnection fconn;
        InputStream isfile = null;
        // Buffer to store image data
        byte data[] = new byte[1600*1200];
        int size;        
        // File name
        String file_name = "foto.jpg";        
        try {
            // Get Pointer to the file
            fconn = (FileConnection)Connector.open("file:///SDCard/" + file_name);                       
            if (fconn.exists()) {
                // Open & read file
                isfile = fconn.openInputStream();
                size = isfile.read(data);                
                // 1. Convert to bmp            
                Bitmap original = Bitmap.createBitmapFromBytes(data,0,size,1);
                // 2. Downscale bmp image
                scale= original.getWidth()/width;
                original = null;
                original = Bitmap.createBitmapFromBytes(data,0,size,scale);
                // 3. Create a copy of bmp
                Bitmap restored = new Bitmap(original.getType(), original.getWidth(), original.getHeight());
                Graphics graphics = new Graphics(restored);
                // 4. Retrieve a vector with raw data from original image.
                int[] argb = new int[original.getWidth() * original.getHeight()];
                original.getARGB(argb, 0, original.getWidth(), 0, 0, original.getWidth(), original.getHeight());                       
                // 5. Clear Non-Skin color pixels
                
                for(int i=0; i < argb.length; i++) {                    
                    if (!isSkinColor(argb[i], USE_YCRCB)) {
                        // Isn't skin color
                        argb[i] = 0x00FFFFFF;
                    }
                }
                
                // TODO: use K-Means & segment face(s)                
                        
                // Draw new image using skin color pixels                
                graphics.drawRGB(argb, 0, restored.getWidth(), 0, 0, restored.getWidth(), restored.getHeight());
                
                // Show original & skin color image
                BitmapField field1 = new BitmapField(original);
                BitmapField field2 = new BitmapField(restored);                
                add(new LabelField("Original bitmap: "));
                add(field1);
                add(new LabelField("Skin color: "));
                add(field2);                
            } else {
                // file not found
                add(new LabelField("File not found: " + file_name));
            }                
            fconn.close();                       
        } catch (Exception ioe) {
            add(new LabelField("Exception: " + ioe.toString()));
        }                        
    }// end Draw Demo
    
    /*
    TextField txt_file = new TextField("File:", "foto.jpg",16,TextField.EDITABLE);
        add(txt_file);
        FieldChangeListener listener = new FieldChangeListener() {
            public void fieldChanged(Field field, int context) {
                ButtonField buttonField = (ButtonField) field;
                System.out.println("Button pressed: " + buttonField.getLabel());
            }
        };
        ButtonField buttonField = new ButtonField("Detect!");
        buttonField.setChangeListener(listener);
        add(buttonField);
    private class FieldListener implements FieldChangeListener {
        public void fieldChanged(Field field, int context) {
            if (context != FieldChangeListener.PROGRAMMATIC) {
            // Perform action if user changed field.
                
            
            } else {
            // Perform action if application changed field.
            }
        }
    }*/
}
