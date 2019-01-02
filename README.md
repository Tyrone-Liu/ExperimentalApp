# Long Shoot (alpha) - Camera for Stacking Photography, Time Lapse and Long Exposure
[**Long Shoot**](https://github.com/Tyrone-Liu/LongShoot) is an Android camera application using the [`camera2`](https://developer.android.com/reference/android/hardware/camera2/package-summary) API.  It will give you the ability to manually control every parameters of the camera on your phone.  With the *Capture Sequence* function and RAW support, it can produce photos for advanced *stacking photography* and *time lapse*.

**Before you try to install, please check the [Requirements](#requirements) section.**
- **Latest signed APK file(s): [GitHub Release Page](https://github.com/Tyrone-Liu/LongShootAlpha/releases/latest) (do not try any `pre-release` build if you do not know what it is)**
- **Telegram group for discussion and technical support: [Long Shoot - Development](https://t.me/LongShootDev)**
- **Telegram channel for release news: [Long Shoot - Distribution](https://t.me/LongShootDist)**

If you want to report a bug, you can [create an issue](https://github.com/Tyrone-Liu/LongShootAlpha/issues/new/choose), then please follow the corresponding issue template to provide information.  Also check the [Frequently Asked Questions](#frequently-asked-questions) and [Known Bugs](#known-bugs) section first.

## Content
- [Requirements](#requirements)
- [Features](#features)
- [TODO List](#todo-list)
- [Frequently Asked Questions](#frequently-asked-questions)
- [Known Bugs](#known-bugs)
- [How to Get the Logcat](#how-to-get-the-logcat)


## Requirements
These requirements can be checked using another application [Camera Characteristics](https://github.com/Tyrone-Liu/CameraCharacteristics).
- Mandatory
    - Android API Level `>=` `26` (`>=` `28` will enable more feature)
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


## Frequently Asked Questions
#### **Q:** Why the app crashes?
**A:** Um, this can be complicated, you can [create an issue](https://github.com/Tyrone-Liu/LongShootAlpha/issues/new/choose) and provide more information there.  The problem can be a bug I didn't discover or just compatibility.

#### **Q:** Why there is big block of text in the viewfinder?
**A:** That is information about every frame been captured, for some debug purpose.  I will add an option in settings to let user disable it.

#### **Q:** Why the `Sequence` button in the main screen leads to an empty interface?
**A:** `Capture Sequence` function is still under construction.  I'm afraid it will take some time to arrive.

#### **Q:** After enabled `Consecutive Capture` in settings, the preview freezes after press the `CAPTURE` button.
**A:** This is expected, that's why the option is under `Experimental & Temporary` section.  This is a temporary replacement of `Capture Sequence`.

#### **Q:** Why the `About` button in the menu of settings does nothing?
**A:** Expected, I didn't write the about interface yet.


## Known Bugs
These bugs been documented here because I do not know what caused them or how to solve them.
- [ ] The focus distance used for capture can not match the one used for request.  Seems to have hyper focal distance be the furthest focus distance.
- [ ] The first capture in every capture request (both repeating and capture) will take more time than the exposure time.  The extended time depends on the previous capture's exposure time, usually triple of it.
- [ ] If the capture format is `JPEG`, image can not be saved after called `captureSession.abortCapture()`.
- [ ] When the image format is `RAW_SENSOR`, the `captureSize` acquired from `cameraCharacteristics` will be slightly bigger than then actual result.
- [ ] When the Focus Assistant center was set to some point, the magnification will be smaller, even if the `CaptureResult.SCALER_CROP_REGION` is exactly what we want.
- [ ] When switching between bottom sheets too fast, the `viewingControlBottomSheet` will be set to `0`.  Because when close the bottom sheet, there is a `STATE_SETTLING` before `STATE_HIDDEN`, if user open a new bottom sheet before `STATE_HIDDEN` callback (this is quite easy), the `viewingControlBottomSheet` will be set to `0` and date in adjust pannel will not be synchronized with 3A results.

- [ ] On `OnePlus 6 (enchilada)`, when the image format is `RAW_SENSOR`, the preview viewfinder will get anomalously brighter, but the captured image still have normal brightness.
- [ ] On `OnePlus 6 (enchilada)`, when the image format is `JPEG` and the `captureSize` is the max available value `4k x 3x`, the edge of result image will be cut out.
- [ ] On `OnePlus 6 (enchilada)`, after captured single image several times, the `imageReader` will suddenly not recceive the capture result.  This will not happen in `repeatingRequest` or after stopped the repeatingRequest of preview.


## How to Get the Logcat
- What device you want to use
    - [Computer](#computer)
    - [Just the device used to run the application](#just-the-device-used-to-run-the-application)

### Computer
1. Get access to `Android Debug Bridge (adb)`.  (If you already have, you can skip this part)
    - If you have `Android Studio` installed, follow [this page](https://developer.android.com/studio/intro/update#sdk-manager) to install `Android SDK Platform-Tools`.
    - If you do not have `Android Studio`, download `Android SDK Platform-Tools` [here](https://developer.android.com/studio/releases/platform-tools).  (If you are on Linux, you can also get it from the package manager)

2. Enable `USB debugging`.  (If you already have, you can skip this part)
    - Go to `System Settings` > `System` > `Developer Options` > `USB debugging` (Under the `Debugging` section).
    - If you did not see `Developer Options` under `System`, first go to `About phone` under `System`, then tap on `Build number` several times.

3. Connect the device to the computer
    - Via USB cable: this is the simple way, just connect it, and tap `OK` in the `Allow USB debugging?` dialog.
    - Via `adb over network`: this is the wireless way if you prefer, please follow [this part](https://developer.android.com/studio/command-line/adb#wireless) of the official guide.

4. **Start getting the logcat**
    - If the application can be launched (not crash right after you launch it)
        1. Launche the application
        2. Run the following command in your computer's terminal *(Be sure to change the file path)*:

        ```
        adb shell 'logcat --pid=$(pidof -s com.tyroneil.longshootalpha)' > 'path_format_depending_on_system/LSA_LOGCAT.TXT'
        ```

        3. Reproduce the bug.

    - If the application can not launch
        1. Run the following command in your computer's terminal *(Be sure to change the file path)*:

        ```
        adb logcat *:E > 'path_format_depending_on_system/LSA_LOGCAT.TXT'
        ```

        2. Launch the application (let it crash).

5. **Stop getting the logcat**
    - After collected the logcat, you need to manually stop it.  You can:
        - When you are in the terminal, press `CTRL + C` on your keyboard.
        - Or you can just close the terminal window.
    - The logcat will be stored in the path you used above.

### Just the device used to run the application
- **It seems like you need root access to perform this method**, or you can not store the logcat to a file.

1. Get access to a terminal (using a terminal application)

2. **Start getting the logcat**
    - If the application can be launched (not crash right after you launch it)
        1. Launche the application
        2. Run the following command line by line in the terminal *(Be sure to change the file path, usually you can use `/storage/emulated/0/LSA_LOGCAT.TXT`)*:

        ```
        su
        logcat -f 'path_format_depending_on_system/LSA_LOGCAT.TXT' --pid=$(pidof -s com.tyroneil.longshootalpha)
        ```

        3. Reproduce the bug.

    - If the application can not launch
        1. Run the following command line by line in the terminal *(Be sure to change the file path, usually you can use `/storage/emulated/0/LSA_LOGCAT.TXT`)*:

        ```
        su
        logcat -f 'path_format_depending_on_system/LSA_LOGCAT.TXT' *:E
        ```

        2. Launch the application (let it crash).

3. **Stop getting the logcat**
    - After collected the logcat, you need to manually stop it.  You can:
        - Just close the terminal app (be sure to stop it, do not just press the `Home` button).
        - Or you can try to send `CTRL + C` into the terminal, if you know how to do that.
    - The logcat will be stored in the path you used above.


