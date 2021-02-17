# KSW-ToolKit

![screenshot](/images/screenshot-eventmanager.png)

KSW-ToolKit is capable of reading and understanding your Mcu. For example it can detect the Telefon button on your steering wheel and you determine to start whatever App you wish. Aside from starting apps, you can also invoke a set of global Android KeyEvents or McuCommands. This way you could remap your car buttons with just a few clicks! To check what Car button causes what Event, an Mcu Listener is included to show live every Event the Mcu posts. Additionally this App has easy tweaks, like being able to fully hide your TopBar with just a toggle. A built in ADB Shell is also included.

To achieve the remapping of your existing buttons, this App will have to hijack the Mcu by shutting down the internal KSW McuService and starting its own reader service. Once shut down, only this app will be able to receive and send data to your Mcu!

## Example

* 1- Install the Apk and start it.
* 2- Go to System Tweaks and disable "KSW Service". After this point you'll be no longer able to navigate with your iDrive as the mcu has been hijacked by this app.
* 3- Navigate to Mcu Event Manager and Scroll down to "Drive Knob Buttons".
* 4- Sequentially assign for each button the correct DPAD KeyEvent.

Optional in System Tweaks:
* 5a - Enable Log CarData to wrap CarData to the System for Dashboards and Climate Control (Benz only).
* 5b - Enable Log McuEvent if you want to capture Mcu Events with Tasker via Logcat.
* 5c - Enable Intercept McuCommands to redirect the commands to Mcu from the hijacked KSW-Service.


## Building

Android Studio was used to write and compile this App. For the core functionalities to work (eg. invoking KeyEvents on Instrumentation), this App requires System Level priviledges. When compiling, you will need to sign the APK with the same KeyStore as the Android OS was signed with.

## Contributing

As the App is still in development, contributions of any sorts is highly welcome! To contribute open a new pull request or open an issue to discuss new ideas.

## Credits

[McuCommunicator](https://github.com/KswCarProject/McuCommunicator) - To communicate to Mcu
[AdbLib](https://github.com/cgutman/AdbLib) - To access the Adb shell

## License

[GPL v3.0](https://choosealicense.com/licenses/gpl-3.0/)
