@echo off
set LIB="..\lib\*"
set BIN="..\bin\*"

REM ???
echo {^
    "type" : "jdbc",^
    "jdbc" : {^
        "url" : "jdbc:mysql://192.168.10.222:3306/farm",^
        "user" : "lzh",^
        "password" : "123456",^
	"schedule" : "0 0/15 * ? * *",^
        "sql" :  [^
             {"statement":"SELECT *,id as _id from farm"}^
	],^
	"autocommit" : true,^
        "treat_binary_as_string" : true,^
        "elasticsearch" : {^
             "cluster" : "elasticsearch",^
             "host" : "localhost",^
             "port" : 9300^
        },^
        "index" : "farm",^
        "type" : "farm"^
      }^
}^ | "%JAVA_HOME%\bin\java" -cp "%LIB%" -Dlog4j.configurationFile="file://%BIN%\log4j2.xml" "org.xbib.tools.Runner" "org.xbib.tools.JDBCImporter"