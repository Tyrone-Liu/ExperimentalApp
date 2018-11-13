# Long Shoot Alpha
This is the alpha repository of **[Long Shoot](https://github.com/Tyrone-Liu/LongShoot)**, which is currently empty.🙄  


## Features
+ Full control of shutter parameters
    * Focus
        - [x] Auto focus
        - [x] Manual focus
        - [ ] Touch to focus
    * [x] Exposure time (Shutter speed)
    * [x] ISO
    * [ ] GPS record for shots
    * [x] White balance (When you have RAW output, white balance is not that demanded)
    * [x] Flash control (Flash lights on phones may not be a good choice for great lighting)
+ Photo output
    * [ ] Choice between RAW (DNG) / JPEG output
    * [ ] Write correct metadata into files or embed in shot
    * [ ] Custom output file name with patterns
+ Capture Sequence (Replacement for BRK and Burst)
    * [ ] Basic elements
        - [ ] Single capture (Basic element)
        - [ ] Pause length (Pause time between previous and next capture)
    * [ ] Special elements
        - [ ] Repeat interval (a start and an end entry) (Repeat basic elements within the interval for a specified time)
        - [ ] Time condition (proceed next element not before a apecific time)
    * Both basic and special elements (except 'Time condition') have their own 'repeat count', can be endless


## TODO
+ [x] A preview interface that matches capture parameters
+ [x] UI of capture parameters controller
+ Control of capture parameters
    * [x] Exposure Time
    * [x] Sensitivity
    * [x] Aperture
    * [x] Auto White Balance
    * [x] Optical Stabilization
    * [x] Focal Length
    * [x] Focus Distance
+ [x] Handle App sent to background, closed, killed or camera crashed
+ [x] Capture and store the photo
+ [ ] Controller in UI for:
    * [ ] Switch camera
    * [x] Open torch
    * [ ] Change magnification
    * [ ] Gallery
    * [ ] Preview parameters match capture
+ [ ] Another thread for storing image file
+ [ ] Different capture sequences for stacking or time-lapse
+ [ ] More miscellaneous settings in setting interface
+ [ ] Better handle of auto capture mode capture parameters
+ [ ] Handle Android permission request


## Known Bugs (from oldest to newest)
+ [ ] The focus distance used for capture can not match the one used for request.  Seems to have hyper focal distance be the furthest focus distance.  
+ [ ] The first capture request go into pipeline will take triple times of exposure time.  
+ [ ] If the capture format is JPEG, image can not be saved after called 'captureSession.abortCapture()'.  

