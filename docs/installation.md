# Academic Platform for ONline Experiments (APONE)
## INSTALLATION GUIDE
### TU Delft, February 2018

The Academic Platform for ONline Experiments (APONE) is a Maven dynamic web project developed using  Eclipse Oxygen Release (4.7.0). It requires access to running instances of MongoDB and RabbitMQ Server. The project has been developed in Java, so a Java-compliant server is required for its deployment. The server currently used is Tomcat v.8.5. Below you can see the steps to install and run the platform.

## 1. Tomcat

Download and install Tomcat Server: [http://tomcat.apache.org](http://tomcat.apache.org)

Tested version 8.5.27

## 2. Mongo Database

Download and install Mongo Database: [https://www.mongodb.com](https://www.mongodb.com)

Tested version 2.6.10

The port by default is 27017, although this can be changed in the configuration file (step 4).

Currently, the platform works with a predefined user which has access to a database which contains two collections: *experiment* and *event*. The database and the user must be set in the configuration file (see step 4). The database and collections may be created dynamically by the platform, but the user must be created previously, with *read/write* permissions on the database.

## 3. RabbitMQ Server

Download  and install RabbitMQ Server: [https://www.rabbitmq.com](https://www.rabbitmq.com)

Tested version 3.6.10

The port by default is 5672, although this can be changed in the configuration file (step 4).

A web management tool can be activated with the command:

```bash
rabbitmq-plugins enable rabbitmq_management
```

It is listening to port 15672, and you can access (only from localhost) by using the following credentials: user:guest, password:guest. Check [https://www.rabbitmq.com/management.html](https://www.rabbitmq.com/management.html) for more and maybe more updated information.

## 4. Check Configuration

Download or clone the project APONE from [github](https://github.com/marrerom/APONE) and check the file *config.properties* located in WEB-INF. Set the appropriate configuration for Mongo and RabbitMQ, and save the changes:

<a href="" id="t.84ac78255f151322362bd85d853525d09fddda2a"></a><a href="" id="t.0"></a>

<table>
<colgroup>
<col width="33%" />
<col width="33%" />
<col width="33%" />
</colgroup>
<tbody>
<tr class="odd">
<th align="left"><p><span class="c11 c1">Property</span></p></th>
<th align="left"><p><span class="c11 c1">Predefined Value</span></p></th>
<th align="left"><p><span class="c11 c1">Comment</span></p></th>
</tr>
<tr class="even">
<td align="left"><p><span class="c1">MONGO</span><span class="c1">HOST</span></p></td>
<td align="left"><p><span class="c4">localhost</span></p></td>
<td align="left"><p><span class="c4">Host of MongoDB</span></p></td>
</tr>
<tr class="odd">
<td align="left"><p><span class="c1">MONGO</span><span class="c1">PORT</span></p></td>
<td align="left"><p><span class="c4">27017</span></p></td>
<td align="left"><p><span class="c4">Port of MongoDB</span></p></td>
</tr>
<tr class="even">
<td align="left"><p><span class="c1">MONGO</span><span class="c1">DB</span></p></td>
<td align="left"><p><span class="c4">irep</span></p></td>
<td align="left"><p><span class="c4">Name of the database (it will be created if it does not exist)</span></p></td>
</tr>
<tr class="odd">
<td align="left"><p><span class="c1">MONGO</span><span class="c1">USER</span></p></td>
<td align="left"><p><span class="c4">irepuser</span></p></td>
<td align="left"><p><span class="c4">User of the Mongo database</span></p></td>
</tr>
<tr class="even">
<td align="left"><p><span class="c1">MONGOUSER</span><span class="c1">PWD</span></p></td>
<td align="left"><p><span class="c4">0000</span></p></td>
<td align="left"><p><span class="c4">Password of the user</span></p></td>
</tr>
<tr class="odd">
<td align="left"><p><span class="c1">RABBITHOST</span></p></td>
<td align="left"><p><span class="c4">localhost</span></p></td>
<td align="left"><p><span class="c4">Host of RabbitMQ</span></p></td>
</tr>
<tr class="even">
<td align="left"><p><span class="c11 c1">RABBITPORT</span></p></td>
<td align="left"><p><span class="c11 c12 c4">5672</span></p></td>
<td align="left"><p><span class="c11 c12 c4">Port of RabbitMQ</span></p></td>
</tr>
<tr class="odd">
<td align="left"><p><span class="c11 c1">RABBITEXCHANGE</span></p></td>
<td align="left"><p><span class="c11 c12 c4">irep-exchange</span></p></td>
<td align="left"><p><span class="c11 c12 c4">RabbitMQ Exchange Name</span></p></td>
</tr>
</tbody>
</table>


## 5. Generate WAR

Generate the WAR file to deploy in Tomcat. You can get it directly from the repository (release) or you can generate it easily with Maven, by writing the following command in the project folder:

```bash
mvn clean install -DskipTests
```
It will generate the WAR file in the *target* folder of the project. Make sure you skip the tests (-DskipTest) to generate the WAR file, as they requires the WAR file already deployed in a server in order to test the functionality. 

## 6. Deploy the platform

With Tomcat running (as well as Mongo and RabbitMQ servers), copy that file in the webapps folder in Tomcat to have it automatically deployed. After some seconds, you should be able to access it from [http://localhost:8080/APONE](http://localhost:8080/APONE), assuming you are running Tomcat in localhost with the default port.

## 7. Test

A test designed to test the main services of the platform is included. You can run it with maven:

```bash
mvn test
```

Previously, you have to make sure that the platform is deployed, and the *config.properties* file for testing, located at */src/test/java* contains the proper host, port and context where APONE is deployed:

<a href="" id="t.00404fe88dc5972c4432cca52e51a10535e3519c"></a><a href="" id="t.1"></a>

<table>
<colgroup>
<col width="33%" />
<col width="33%" />
<col width="33%" />
</colgroup>
<tbody>
<tr class="odd">
<th align="left"><p><span class="c1 c11">Property</span></p></th>
<th align="left"><p><span class="c11 c1">Predefined Value</span></p></th>
<th align="left"><p><span class="c11 c1">Comment</span></p></th>
</tr>
<tr class="even">
<td align="left"><p><span class="c11 c12 c4">HOST</span></p></td>
<td align="left"><p><span class="c11 c12 c4">localhost</span></p></td>
<td align="left"><p><span class="c11 c12 c4">Server host where APONE is deployed</span></p></td>
</tr>
<tr class="odd">
<td align="left"><p><span class="c11 c12 c4">PORT</span></p></td>
<td align="left"><p><span class="c11 c12 c4">8080</span></p></td>
<td align="left"><p><span class="c11 c12 c4">Server port where APONE is deployed</span></p></td>
</tr>
<tr class="even">
<td align="left"><p><span class="c11 c12 c4">CONTEXT</span></p></td>
<td align="left"><p><span class="c11 c12 c4">APONE</span></p></td>
<td align="left"><p><span class="c11 c12 c4">Name of the context where APONE is deployed</span></p></td>
</tr>
<tr class="odd">
<td align="left"><p><span class="c11 c12 c4">TIMEBEFORETEST</span></p></td>
<td align="left"><p><span class="c11 c12 c4">60000</span></p></td>
<td align="left"><p><span class="c11 c12 c4">Time (in ms) before testing the events have been properly registered by Rabbit</span></p></td>
</tr>
</tbody>
</table>


You can also run the test from the browser by requesting the following service to the platform:
```bash
GET /service/test
```
After some time, you should see 'Test completed' in the browser if it was successful.

