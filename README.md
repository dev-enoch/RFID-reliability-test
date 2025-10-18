# RFID Reliability Test Application

## Description

A Java desktop application for managing RFID-enabled entities using a UHF reader. Uses MySQL for database connectivity.

## Folder Structure

```
src/
  com/dgreat/resources/
    icon.png
    sensor.png
    mysql-connector-java-8.0.17.jar
  com/dgreat/rfid/
    CustomOutputStream.java
    DbCon.java
    LicenseFrame.java
    MainFrame.java
    MyFunctions.java
    Props.java
    RFID.java
    Tag.java
    Test.java
  com/rfid/uhf/
    Device.java
  .classpath
  .project
  config.properties
  com_rfid_uhf_Device.dll
  UHFReader288MP.dll
```

## Build Instructions

1. **Compile the Java source code**

   ```
   javac -encoding UTF-8 -d out -cp ".;src;mysql-connector-java-8.0.17.jar" src/com/dgreat/rfid/*.java src/com/rfid/uhf/*.java
   ```

2. **Copy resources**

   ```
   xcopy src\com\dgreat\resources out\com\dgreat\resources /E /I
   copy config.properties out\
   ```

3. **Create executable JAR**

   ```
   jar cfm RFIDApp.jar manifest.txt -C out .
   ```

## Running the Application

1. Ensure both `.dll` files are in the same folder as the JAR:

   ```
   com_rfid_uhf_Device.dll
   UHFReader288MP.dll
   ```

2. Run the application using:

   ```
   java "-Djava.library.path=." -cp "RFIDApp.jar;mysql-connector-java-8.0.17.jar" com.dgreat.rfid.MainFrame
   ```

3. The main window (`MainFrame`) should launch.

   - Connect your UHF reader as configured.
   - The status label will indicate the RFID reader state.
   - Scan tags to add items automatically to the application.

## Notes

- Ensure the MySQL connector JAR is present in `resources` or classpath.
- The application uses UTF-8 encoding; source files with non-UTF-8 characters may need conversion.
- The `-Djava.library.path=.` argument ensures the application can access the required `.dll` files.
