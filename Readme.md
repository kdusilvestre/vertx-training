**Welcome to the vert.x training. I hope you have fun.**

Import this as a gradle project (version 5+) and run it by using the available tasks:
* runEventloop
* runWorker

In the file structure, you'll find three folders:
* jmeter: Your jmeter script will be there to hammer our vert.x application
* lib: the alpn library you'll need to get jmeter working with http2
* postman: the postman collection for you to play around

How to get Jmeter 5.2.1 working with http2? Follow the steps below:

1. Download jmeter 5.2.1
2. Download the JMeter plugins manager jar from this link: https://jmeter-plugins.org/get/
3. Place the downloaded jar under the libs/ext/ folder in your JMeter directory
4. In the terminal, under the JMeter directory navigate to bin/ folder and start JMeter with the proxy enabled like this:
./jmeter.sh -P 8080 -N localhost
5. Once JMeter has opened, under Options menu there should be option for 'Plugins Manager'
6. Go to 'Available Plugins' tab and search for 'HTTP'
7. Tick the plugin for 'HTTP/2 Sampler' , then click button to 'Apply Changes and Restart Jmeter'


After restarting, now we have to make it work with the ALPN library:
1. Get openJDK - https://adoptopenjdk.net/
2. Install the JDK package to JavaVirtualMachines in /Library/ (make sure the folder folder is called adoptopenjdk-8.jdk)
3. Set $JAVA_HOME to the path of the jdk /Contents/Home
4. Get the alpn-boot jar in your home directory (under */Users/adsid* )
5. Open the jmeter shell (jmeter.sh) script and add these 2 lines towards the top of the script:
JVM_ARGS="-Xbootclasspath/p:*/Users/adsid*/alpn-boot-8.1.13.v20181017.jar"
export JAVA_HOME="/Library/Java/JavaVirtualMachines/adoptopenjdk-8.jdk/Contents/Home/"

How to make sure it's working?

1. Start the vert.x application
2. Start jmeter
3. Open the script, and start the test
4. You shouldn't see any errors and the aggregated tree should have successful HTTP calls