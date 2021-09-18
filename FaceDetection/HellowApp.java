/*
 * HellowApp.java
 *
 * © Andres Martinez Andrade (andresmtz), 2008
 */
 
import net.rim.device.api.ui.*;
import net.rim.device.api.ui.component.*;
import net.rim.device.api.ui.container.*;
import net.rim.device.api.system.*;

public class HellowApp extends UiApplication {
    /*public static void main(String argv[]) {

        HellowApp app = new HellowApp();
        app.enterEventDispatcher();        
                
    }*/

    public HellowApp() {
        pushScreen(new HelloScreen());
    }
}

// Inner class to embed a screen in the app
class HelloScreen extends MainScreen {
    public HelloScreen() {
        super();
        LabelField title = new LabelField
       ("A-FaceDetection 0.5a", LabelField.ELLIPSIS | LabelField.USE_ALL_WIDTH);
        setTitle(title);
        add(new RichTextField("ANN for Automatic Face Detection!"));
        
        // ANN...
        
        // Set number of layes
        int layers[] = new int[3];
        // Set number of nodes per layer
        for(int i=0;i<3;i++) layers[i]=2;        
        // Set weights:   i=0,i=1  j = 0      j = 1        k = 0       k = 1
        double weights[][] = new double[3][4];
        weights[0][0]= 1; weights[0][1]= 1;
        weights[1][0]= 0.33; weights[1][1]= 0.40; weights[1][2]= 0.33; weights[1][3]= -0.06;
        weights[2][0]= 0.77; weights[2][1]= 0.28; weights[2][2]= -0.52; weights[2][3]= 0.89;
        
        // Create a Neural Network
        ANN net = new ANN(0.5, 0.3, layers);
        // Set weights
        net.setWeights(weights);
        // Query network
        double in[] = {0.5,0.2};
        double out[] = net.query(in);
        // Show output
        String output = "";
        for(int i=0; i<out.length;i++)
            output += out[i] + " ";
        add(new RichTextField("Out: " + output));
    }

    public boolean onClose() {
       Dialog.alert("Thanks for using Face Detection!");
       System.exit(0);
       return true;
    }
}
