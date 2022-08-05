# KSW-ToolKit-Service

This is the background service to go along with [KSW-ToolKit](https://github.com/Snaggly/KSW-ToolKit). It will install itself as a background system service and hijacks the original McuService of the KSW Android screens. When hijacked, this program serves as a full replacement to the original software which you can highly tweak and customize using the KSW-ToolKit GUI frontend.


## Building

Android Studio was used to write and compile this App. For the core functionalities to work (eg. invoking KeyEvents on Instrumentation), this App requires System Level priviledges. When compiling, you will need to sign the APK with the same KeyStore as the Android OS was signed with.

## Contributing

Contributions of any sorts is highly welcome! To contribute open a new pull request or open an issue to discuss new ideas.

## Credits

[McuCommunicator](https://github.com/KswCarProject/McuCommunicator) - To communicate to Mcu
[AdbLib](https://github.com/cgutman/AdbLib) - To access the Adb shell

## License

[GPL v3.0](https://choosealicense.com/licenses/gpl-3.0/)
