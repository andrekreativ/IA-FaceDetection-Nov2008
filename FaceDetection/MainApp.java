/*
 * Face Detection 0.8b
 * © Andres Martinez Andrade (andresmtz), 2008
 */
 
// Read & Write files
import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
// User Interface
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;


public class MainApp extends UiApplication {
    
    /**
     * Starts the application
     * @param args <description>
     */     
    public static void main(String[] args) {
        try {
            MainApp app = new MainApp();
            app.enterEventDispatcher();
        } catch(Exception e) {
            System.out.print("Exception: " + e.toString() + "\n");
        }
    }
    
    /**
     * Opens main screen.
     */
    public MainApp() {
        pushScreen(new ColorKMeansDetectorScreen());        
    } 
}

final class ColorKMeansDetectorScreen extends MainScreen {                 
    
    private FaceDetector face_detector;
                       
    public ColorKMeansDetectorScreen() {
        super();
        // Config UI
        LabelField title = new LabelField("Face Detection 0.8b", LabelField.USE_ALL_WIDTH);
        setTitle(title);        
                  
        // File pointers
        FileConnection file_conn = null;
        InputStream input_stream = null;
        
        
        // Buffer to store image data
        byte image_data[] = new byte[120000];
        int image_size;        
        // File name
        String file_name = "foto.jpg";                       
        
        try {
            // Open image file
            file_conn = (FileConnection)Connector.open("file:///SDCard/blackberry/pictures/" + file_name);
            if (file_conn.exists()) {
                // Open & read file
                input_stream = file_conn.openInputStream();
                image_size = input_stream.read(image_data);
                
                // Create a new face detector of the input image
                face_detector = new FaceDetector(image_data, image_size, "foto");                                                                
                
                // Show original_bitmap & skin color image
                
                //Bitmap bitmap_clusters = face_detector.getOnlySkinBitmap();                
                
                BitmapField field_original_bitmap = new BitmapField(face_detector.getOriginalBitmap());                                
                BitmapField field_rgb = new BitmapField(face_detector.getProcessedBitmap());                 
                
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
        } finally {
            // always close file
            try {
                if (input_stream!=null) {
                    input_stream.close();
                    input_stream = null;
                }
                if (file_conn!=null) {
                    file_conn.close();                
                    file_conn = null;
                }
            } catch(Exception e){
                System.out.println("Exception: " + e.toString());
            }
        }
    }// end Draw Demo
}
