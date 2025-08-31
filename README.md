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
- **Camera:** 1.3 MP (8100) or 2.0 MP (8110/8120 models) with LED flash :contentReference[oaicite:1]{index=1}  
- **Battery:** ~900 mAh (Li-Ion), ~3.5 h talk time / ~360 h standby :contentReference[oaicite:2]{index=2}  
- **Connectivity:** Bluetooth 2.0; no Wi-Fi (in earliest models) :contentReference[oaicite:3]{index=3}  
- **Weight & Size:** ~90 g, 107 × 50 × 14 mm :contentReference[oaicite:4]{index=4}  

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

## Summary

Faced with significant hardware limitations of early BlackBerry Pearl models—particularly limited CPU speed, RAM, and absence of Wi-Fi—we bypassed traditional resource-heavy face detection techniques. Instead, we devised and implemented a novel, lean algorithm that detects faces extremely quickly (sub-second), making it practical for the handheld constraints of the time.


