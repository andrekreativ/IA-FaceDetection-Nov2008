# IA-FaceDetection-Nov2008
Proyecto de la clase de Inteligencia Artificial, noviembre 2008. El objetivo del proyecto era investigar algoritmos de inteligencia artificial para reconocer rostros en imágenes y que dichos algoritmos pudieran ser ejecutados con los recursos disponibles en un BlackBerry Pearl de ese tiempo.

MIDlet-Version: 0.8b<br />
MIDlet-Name: FaceDetection<br />
MIDlet-Description: Mobile Application for automatic face detection in Images<br />
MIDlet-Vendor: andresmtz@gmail.com<br />

Notes:
The entry point of the app is the file MainApp.java.
The file with the main algorithm is FaceDetector.java.

Main flow:
The MainApp class starts the app by creating the main screen of the app with the class ColorKMeansDetectorScreen. 
Then the ColorKMeansDetectorScreen class opens the picture and uses the FaceDetector class to run the alrogithms and detect faces in the picture.
