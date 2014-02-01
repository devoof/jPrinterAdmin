jPrinterAdmin
=============

About

jPrinterAdmin is a pure hobby-project under development.

It should be able to retrieve printer-data from printers in the local network via

- snmp and

- http

and store them in a database. You can choose between

- local (sqlite)

- MYSQL and

- Microsoft SQL-Server.

You can create reports and output them as

- HTML

- OpenDocument Spreadsheet (odt) or

- comma-separated-values (csv)

to the local file-system or an email-address via smtp (also secured).

You can add following jobs to schedule which can be automated:

- reading printer data

- creating and sending reports

- backup the database



It is written in Java in the netbeans ide and published under then gnu public license.

It uses the following 3rd Party libraries and partly modified sample code.

snmp4j 2.1

ini4j 0.5.2

sqlitjdbc v0.56

jsoup 1.7.2

mysql-connector 5.1.7

jtds 1.2

jOpenDocument 1.3



Ways to start the application

1. Executing the jar file

Copy the jar-file and the lib-folder to a folder the user has write access to
E.g. /home/<username>/jprinteradmin or c:\users\<username>\jprinteradmin.
Make a shortcut e.g. a jprinteradmin.bat file with the content “javaw -jar <pathToJarFile>”.

2. Using Web-Start
Place the whole folder to a web-server.
Add the certificate to signer-ca in the java control panel under “security” → “certificates”
Navigate your Browser to the place where you put the folder in.
Start the launch.jnlp file.
Trust the application, if you wants to start it
You can make short-cut from the java control panel by navigate to “Basic” → “View temporary files” → Right Click on jprinteradmin → install shortcut



