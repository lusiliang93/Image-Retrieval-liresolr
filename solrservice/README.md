# LIRESolr Server Docs

## Project Structure

```
solrservice
├── libs    // third party jars
│   ├── LIRE-1.0_b04.jar
│   └── LireSolr-6.4.0_b01.jar
├── pom.xml     // maven description file
├── README.md
├── solrservice.iml
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── pingan
    │   │           ├── servlet
    │   │           │   ├── ImgAddServlet.java      // add image servlet
    │   │           │   ├── ImgDeleteServlet.java   // delete image servlet
    │   │           │   └── ImgRepeatServlet.java   // query repeat servlet
    │   │           └── utils   // some utility methods
    │   │               ├── ImgUtil.java
    │   │               ├── ServletUtil.java
    │   │               └── SolrUtil.java
    │   ├── resources
    │   │   ├── config.properties   // some common configurations
    │   │   └── sql.md      // this file is not used, just ignore 
    │   └── webapp
    │       └── WEB-INF
    │           └── web.xml
    └── test    // there are some test code, just ignore
        └── java
            ├── file
            │   ├── TestFileAPI.java
            │   └── TestUUID.java
            ├── jdbc
            │   └── TestJDBC.java
            ├── json
            │   ├── TestJSONArray.java
            │   └── TestURLEncode.java
            ├── solr
            │   ├── TestNetRequest.java
            │   ├── TestSolrQuery.java
            │   └── TestSolrUpdate.java
            └── solrlire
                └── TestIndexer.java
```

## How to use

 This project is written using Intellij with maven, it's a java web project, you can clone the code to idea, and config maven central repository properly, after maven downloads all the depencies for you, you can use `Build Artifacts` in idea to build this project, if everything is done correctly, idea will generate a folder called `out`, and you can find a file ends with `.war`, this war file is your web application, you can deploy it to tomcat. 

## About the configuration

As you can see in `Project Structure`, there's a file called `config.properties`, there are some configurations used in the project, for example, there is a property `dowloadcenterURL`, it's the url used to query image url from image database, the url is different in develop envirenment and product envirenment, so before deploy the project to product envirenment, you should change the value of this property.

## About maven central repository

Some jars used in this project is in PingAn's central repository, so you have to config your maven central repository to PingAn's repository in `settings.xml`