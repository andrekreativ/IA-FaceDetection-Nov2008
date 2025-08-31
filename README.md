# AI-FaceDetection (Nov 2008)

This project was developed as part of an Artificial Intelligence course in November 2008 during college at the Tec de Monterrey, Campus Queretaro.

It aimed to explore AI algorithms for face detection in images and optimize them to run within the constrained resources of the **BlackBerry Pearl** device of that era. 

Traditional algorithms requiring extensive processing power and memory—as found on laptops or desktops—could not be used. That limitation compelled the development of a brand-new algorithm designed to detect faces efficiently, using minimal CPU and memory, and capable of recognizing faces in less than a second on the BlackBerry Pearl.

---

## Application Metadata

- **MIDlet-Version:** 0.8b  
- **MIDlet-Name:** FaceDetection  
- **MIDlet-Description:** Mobile application for automatic face detection in images  

---

## BlackBerry Pearl (circa 2006–2008) Specifications

- **CPU:** 32-bit Intel XScale PXA272 at 312 MHz  
- **RAM:** 32 MB (some models up to 64 MB)  
- **Storage:** 64 MB internal + microSD expansion  
- **Screen:** ~2.2" TFT LCD, 240×260 px resolution (~161 ppi), 65K colors  
- **Camera:** 1.3 MP (8100) or 2.0 MP (8110/8120 models) with LED flash
- **Battery:** ~900 mAh (Li-Ion), ~3.5 h talk time / ~360 h standby
- **Connectivity:** Bluetooth 2.0; no Wi-Fi (in earliest models) 
- **Weight & Size:** ~90 g, 107 × 50 × 14 mm  

These specs illustrate the highly limited computational and memory environment available at the time, which is why existing desktop-class face detection methods were not feasible.

---

## Project Structure

- **MainApp.java** – Entry point that initializes the user interface.  
- **ColorKMeansDetectorScreen.java** – Handles image loading and delegates detection tasks.  
- **FaceDetector.java** – Contains the custom face detection algorithm optimized for performance.

---

## Main Flow

1. **MainApp** initializes the application and displays the main UI screen via **ColorKMeansDetectorScreen**.  
2. **ColorKMeansDetectorScreen** handles image input and passes the data to **FaceDetector**.  
3. **FaceDetector** runs the optimized algorithm to detect faces, returning results in well under one second.

---

## Notes & Considerations

- Designed for **J2ME/MIDP** environment, compatible with early mobile platforms.  
- Prioritized careful balancing of **performance**, **memory efficiency**, and **accuracy**, tailored to very limited hardware.

---

## Summary of Algorithm & Innovation

At the time (2008), most face detection techniques (e.g., Viola–Jones with Haar cascades) were too resource-intensive to run on mobile hardware like the BlackBerry Pearl. These traditional methods required desktop-class CPUs and far more memory than was available on consumer devices.  

To overcome this, I designed a lightweight **color-based face detection algorithm** that was optimized for limited CPU and memory, yet still capable of detecting faces in **sub-second time** on the Pearl.

Key innovations included:

1. **Downscaling for efficiency**  
   Images were resized to a manageable width (≈100px) to dramatically reduce the number of pixels processed, while still preserving enough structure for detection.

2. **Skin color filtering**  
   Each pixel was analyzed using a custom **skin-color classifier** (via RGB thresholds and transformations). Non-skin pixels were discarded immediately, cutting down the search space and memory usage.

3. **K-Means clustering of skin regions**  
   Remaining skin-color pixels were clustered using **K-Means** to group potential face regions. This allowed the algorithm to identify candidate face regions without scanning the entire image.

4. **Hierarchical refinement with sub-clusters**  
   To improve accuracy, each cluster was further segmented into sub-clusters (SUB_K), helping separate actual face regions from noise (e.g., background skin-tone pixels).

5. **Lightweight visualization & debugging**  
   Intermediate results (skin masks, clusters) were stored and visualized as debug PNGs, enabling rapid testing on device while keeping the implementation minimal.

---

### Why This Was Novel in 2008
- Running **any kind of real-time face detection** on a mobile device was rare at the time.  
- The BlackBerry Pearl had only **312 MHz CPU** and **32 MB RAM**, with no GPU acceleration.  
- By combining **color filtering + clustering**, this approach avoided heavy Haar classifiers or neural networks, achieving **sub-second detection** on early mobile hardware.  
- This made it one of the earliest demonstrations of practical **on-device face detection** for consumer phones.


