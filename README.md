
                          Railsimulator

  What is it?
  -----------
  Railsimulator is a simple, distributed rail-road simulator. Designed with client-server
  architecture in mind, Railsimulator is modular, fast and reliable. Because of the modular
  approach, each component of the system can be replaced with new one's.

  The Latest Version
  ------------------

  Details of the latest version can be found on my github account: https://github.com/fioan89/railsimulator

  Documentation
  -------------

  For a more in-depth and up to date documentation, please consult the ``` /documentation``` folder
  inside the project source directory.

  Installation
  ------------

  In order to install Railsimulator you need to install maven and of course Java.
  After this two dependencies are installed, please clone https://github.com/fioan89/railsimulator ,
  open a console, change your location to where you cloned the project.
  
  Now we need to build four components. This four modules represent Railsimulator. 
       1. railmonitor (this a graphical user interface that allows you to monitor your railway )
       -----------------------------------------------------------------------------------------
       ```bash
       mvn package -Ponly-railmonitor
       ```
       
       2. railsimulator (this will simulate train vehicles on a railroad)
       -----------------------------------------------------------------
       ```bash
       mvn package -Ponly-railsimulator
       ```

       3. centralcontroller (build a schedule for every train and send it to the railsimulator  )
       -----------------------------------------------------------------------------------------
       ```bash
       mvn package -Ponly-centralcontroller
       ```

       4. synchronizationserver (synchronize every simulator instance)
       -----------------------------------------------------------------------------------------
       ```bash
       mvn package -Ponly-serversynchronizer
       ```
  Running
  -------

  Open up a console and change your location to where your project is cloned. There must be a ``` target ```
  folder, which was created by maven. Change your location to the target directory. 
  Run this command: ```bash cp ./classes/commons-cli-1.2.jar ./ ```
  
       1. Start synchronization server
       -------------------------------
       ```bash java -jar railsimulator-0.0.1-serversynchronizer.jar port 10000 -threshold 10000 -signaltime 1500
       ```
     
       2. Start railmonitor
       --------------------
       ```bash java -jar railsimulator-0.0.1-railmonitor.jar
       ```
       Then load the railway map and a list with every train id the monitor should handle. Then just press ``` Start Monitoring ```

      3. Start centralcontroller
      --------------------------
      ```bash java -jar railsimulator-0.0.1-centralcontroller.jar -port 16000 -file /path/to/routes.csv
      ```

      4. Start railsimulator
      ----------------------
      ```bash java -jar railsimulator-0.0.1-railsimulator.jar -controllerPort 16000 -controllerAddress localhost 
              -syncServerPort 10000 -syncServerAddress localhost -monitorPort 5000 -monitorAddress localhost
      ```
  

  Licensing
  ---------

  Please see the file called LICENSE.

  

  Contacts
  --------

     o If you have anything to discuss with me, please contact me
      at fioan89@gmail.com

     o If you have a concrete bug report for Railsimulator please submit you report
       at https://github.com/fioan89/railsimulator/issues
