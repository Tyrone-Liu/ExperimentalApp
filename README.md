# Long Shoot (alpha) - Camera for Stacking Photography, Time Lapse and Long Exposure
[**Long Shoot**](https://github.com/Tyrone-Liu/LongShoot) is an Android camera application using the [`camera2`](https://developer.android.com/reference/android/hardware/camera2/package-summary) API.  It will give you the ability to manually control every parameters of the camera on your phone.  With the *Capture Sequence* function and RAW support, it can produce photos for advanced *stacking photography* and *time lapse*.

**Before you try to install, please check the [Requirements](#requirements) section down below.**
- **Latest signed APK file(s): [GitHub Release Page](https://github.com/Tyrone-Liu/LongShootAlpha/releases/latest)**
- **Telegram group for technical support and advice: [Long Shoot - Development](https://t.me/LongShootDev)**

When you try to report a bug, you can [create an issue](https://github.com/Tyrone-Liu/LongShootAlpha/issues/new/choose), then please follow the issue template to provide information.

## Content
- [Requirements](#requirements)
- [Features](#features)
- [TODO List](#todo-list)
- [Known Bugs](#known-bugs)


## Requirements
These requirements can be checked using another application [Camera Characteristics](https://github.com/Tyrone-Liu/CameraCharacteristics).
- Mandatory
    - Android API Level `>=` 26 (`>=` 28 will enable more feature)
    - Device has `android.hardware.camera2` support
- Optional
    - `CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL` `>=` `FULL` (have not tested much on devices with lower `INFO_SUPPORTED_HARDWARE_LEVEL`, highly possible to run into bugs or crash)

Should works great on *Pixel* and *Nexus* devices, or any manufacturer that doesn't do too much weird modifications to stock Android.

I personally have Nexus 6P and OnePlus 6 to test on, can not guarantee to be fully funtional on other devices.  (BTW, OP6 with OOS is already weird, can't imagine how highly modified Android would be.)


## Features
- Full control of shutter parameters
    - Focus
        - [x] Auto focus
        - [x] Manual focus
        - [x] Manual Focus Assistant (zoom in when adjust the SeekBar)
        - [x] Touch to set Focus Assistant center
        - [ ] Touch to focus
    - [x] Exposure time (Shutter speed)
    - [x] ISO
    - White balance
        - [x] Auto White Balance
        - [ ] Manual White Balance
    - [x] Flash control (Flash lights on phones may not be a good choice for great lighting)
- Photo output
    - [x] Choice between RAW (DNG) / JPEG output
    - [x] GPS record for shots
    - [x] Write correct metadata into files or embed in shot
    - [ ] Custom output file name with patterns
- Capture Sequence (Replacement for BRK and Burst)
    - [ ] Basic elements
        - [ ] Single capture (Basic element)
        - [ ] Pause length (Pause time between previous and next capture)
    - [ ] Special elements
        - [ ] Repeat interval (a start and an end entry) (Repeat basic elements within the interval for a specified time)
        - [ ] Time condition (proceed next element not before a apecific time)
    - Both basic and special elements (except 'Time condition') have their own 'repeat count', can be endless
- Additional Tools for Capture
    - [ ] On Screen Display (OSD) of Elevation Angle and Azimuth
    - [ ] OSD of GPS information (Longtitude, Latitude, Altitude)
    - [ ] Information as a Reflected Light Meter
    - [ ] Bluetooth or Network remote control (reduce vibration)
    - [x] Alternative seekbar mode, improve accuracy within small range


## TODO List
- [x] A preview interface that matches capture parameters
- [x] UI of capture parameters controller
- Control of capture parameters
    - [x] Exposure Time
    - [x] Sensitivity
    - [x] Aperture
    - [x] Auto White Balance
    - [x] Optical Stabilization
    - [x] Focal Length
    - [x] Focus Distance
- [x] Handle App sent to background, closed, killed or camera crashed
- [x] Capture and store the photo
- [ ] Controller in UI for:
    - [ ] Switch camera
    - [x] Open torch
    - [ ] Gallery
- [x] Zoom in SeekBar for preciser adjust
- [x] Change text in adjust panel according to parameters
- [ ] Another thread for storing image file
- [ ] Different capture sequences for stacking or time-lapse
- [ ] More miscellaneous settings in setting interface
- [ ] Better handle of auto capture mode capture parameters
- [x] Handle Android permission request
- [ ] Redo the Icon (not a camera pleace, should be like a phone)


## Known Bugs
From oldest to newest.  
These bugs been documented here because I do not know what caused them or how to solve them.
- [ ] The focus distance used for capture can not match the one used for request.  Seems to have hyper focal distance be the furthest focus distance.
- [ ] The first capture in every capture request (both repeating and capture) will take more time than the exposure time.  The extended time depends on the previous capture's exposure time, usually triple of it.
- [ ] If the capture format is JPEG, image can not be saved after called `captureSession.abortCapture()`.
- [ ] On `OnePlus 6 (enchilada)`, when the image format is RAW_SENSOR, the preview viewfinder will get anomalously brighter, but the captured image still have normal brightness.
- [ ] On `OnePlus 6 (enchilada)`, when the image format is JPEG and the `captureSize` is the max available value `4k x 3x`, the edge of result image will be cut out.
- [ ] When the image format is RAW_SENSOR, the `captureSize` acquired from `cameraCharacteristics` will be slightly bigger than then actual result.
- [ ] On `OnePlus 6 (enchilada)`, after captured single image several times, the `imageReader` will suddenly not recceive the capture result.  This will not happen in `repeatingRequest` or after stopped the repeatingRequest of preview.
- [x] Settings will not be initialized the first time opening the APP, needs to set at least once.
- [ ] When the Focus Assistant center was set to some point, the magnification will be smaller, even if the `CaptureResult.SCALER_CROP_REGION` is exactly what we want.
- [ ] When switching between bottom sheets too fast, the `viewingControlBottomSheet` will be set to `0`.  Because when close the bottom sheet, there is a `STATE_SETTLING` before `STATE_HIDDEN`, if user open a new bottom sheet before `STATE_HIDDEN` callback (this is quite easy), the `viewingControlBottomSheet` will be set to `0` and date in adjust pannel will not be synchronized with 3A results.


