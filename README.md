# KSW-ToolKit

![screenshot](/images/screenshot-eventmanager.png)

KSW-ToolKit is the swiss army knife for any KSW SD625 Android Screen. This project is part of the greater goal to fully replace all of the built in KSW applications. 

This app is capable of reading and understanding your Mcu's inputs which can be translated to your desire, by invoking a global Android KeyEvent or starting of Apps. This way you can fully remap your car buttons from just a few clicks! Plus you'll be able to view live every single Input the Mcu does, if you wish to configure something new in Tasker. Additionally you will have easy tweaks like being able to fully hide your TopBar with just a toggle and a built in ADB Shell! 

To achieve the remapping of buttons, this App hijacks the Mcu reading by shutting down the internal KSW McuService! Once shut down the built in Apps won't be receive any new CarData. For example, the built in Dashboard App won't work anymore! However 3rd Party Apps like [StandaloneDashboard](https://github.com/KswCarProject/StandaloneDashboard) will keep working when enabling the logging those CarDatas.

## Example

1- Install the Apk and start it.
2- Go to System Tweaks and disable "KSW Service". At this point you'll be no longer able to navigate with your iDrive as the service to mcu has been hijacked by this app.
3- Navigate to Mcu Event Manager and Scroll down to "Drive Knob Buttons".
4- Sequentially assign for each button the correct DPAD KeyEvent.

Optional in System Tweaks:
5a - Enable Log CarData, if you wish to use 3rd party Dashboard Apps.
5b - Enable Log McuEvent, if you want to capture Mcu Events with Tasker.
5c - Enable Intercept McuCommands, to make Factory Settings and basic commands from the built in Launcher work while KSW-Service is off.


## Building

Android Studio was using to build and compile this App. For the core functionalities to work (eg. invoking KeyEvents on Instrumentation), this App requires System Level priviledges. When compiling, you will need to sign the APK with the same KeyStore as the Android OS was signed with.

## Contributing

As the App is still in development, contributions of any sorts is highly welcome! To contribute open a new pull request or open an issue to discuss new ideas.

## Credits

[McuCommunicator](https://github.com/KswCarProject/McuCommunicator) - To communicate to Mcu
[AdbLib](https://github.com/cgutman/AdbLib) - To access the Adb shell

## License

[GPL v3.0](https://choosealicense.com/licenses/gpl-3.0/)
