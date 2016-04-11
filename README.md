# Java Challenge: Blog #

This project is designed to test your skills as a back-end web developer,
using the same tools and technologies our team employs on a daily basis.

This project includes a Java-based web service which exposes a REST API for a 
blog, and a Javascript-based web client. The API itself is pretty simple; it 
doesn't even authenticate users. 

Currently, the application is backed by a simple in-memory hashtable. The goal
of this exercise is to replace that implementation with a real database,
capable of storing, retrieving, and deleting blog posts.

You can choose the database (SQLite, MongoDB, PostgreSQL, or whatever) and
the interface method (Hibernate, hand-written SQL, etc).

You can use whatever tools you're most comfortable with. Google anything you
need to look up. Download whatever libraries or frameworks you like. Ask 
questions if you need something clarified. Work like you'd really work.

This exercise is intended to take between one and two hours. It doesn't need
to be perfectly styled or unit tested. We're mostly looking to see if you can 
produce working Java code in a fairly-real-world setting.
    
## Necessary Tools ##

The majority of the software my team develops runs on Java and is built with
Maven. This project is no different. To get started, make sure you have 
[Java version 1.8.45 or later](https://java.com/en/download/) installed on 
your machine. Then, [download](https://maven.apache.org/download.cgi) and
[install](https://maven.apache.org/install.html) Apache Maven 3.3 or later.

When installation is complete, you should be able to run the following 
commands and see something similar to the following output:

```
> java -version
java version "1.8.0_45"
Java(TM) SE Runtime Environment (build 1.8.0_45-b14)
Java HotSpot(TM) 64-Bit Server VM (build 25.45-b02, mixed mode)

> mvn -v
Apache Maven 3.3.9 (bb52d8502b132ec0a5a3f4c09453c07478323dc5; 2015-11-10T11:41:47-05:00)
Maven home: /Users/thomas/Applications/bin/maven/3.3.9
Java version: 1.8.0_45, vendor: Oracle Corporation
Java home: /Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home/jre
Default locale: en_US, platform encoding: UTF-8
OS name: "mac os x", version: "10.11.3", arch: "x86_64", family: "mac"
```

## The Source ##

Most of the projects in this exercise are boilerplate necessary to get the 
application built and running, and can be safely ignored. The `BlogJavaFrontend`
project contains a working Javascript client to the backend, if you want to see
how it's making calls.

The project `BlogJavaBackend` is where you'll be spending your time. The server is
based on [Dropwizard](http://www.dropwizard.io/0.9.2/docs/), which exposes both
the REST API and the web app to the world.

The class you need to worry about is called `interview.blog.BlogDB`. This is the
class the REST API talks to to do the actual CRUD operations. It currently 
contains a hashmap-based implementation, which you will be replacing.

## The API ##

There are four methods you need to worry about:

* `public String store( BlogPost post )` - Creates of updates a blog post, and returns its UUID
* `public void delete( String uuid )` - Deletes any blog post with the given UUID
* `public BlogPost get( String uuid )` - Retrieves a single blog post based on the UUID
* `public List<BlogPost> get()` - Retrieves all blog posts in the database

## The Data ##

The main data class is `interview.blog.BlogPost`, which is a fairly 
straightforward POJO.

`BlogPost.pullQuote` and `BlogPost.body` are 
[Markdown](https://daringfireball.net/projects/markdown/) formatted text. This
is what is displayed in the blog's editor, and what is returned to the server
when a post is saved.

On the server side, `BlogPost.pullQuote` and `BlogPost.body` should be rendered
to HTML using the (included) [Pegdown](https://github.com/sirthias/pegdown)
processor.

`BlogPost.postDate` is a `long` containing a Unix timestamp. You can change that,
if you want, but the web client requires a `long` to be sent back over the wire.


## Building the Project ##

Once you clone the project to your machine, building is as simple as:

```
> cd ~/dev/BlogJavaChallenge
> mvn clean install
```

Maven will then download all of the necessary libraries, compile the Java 
source code, package up the web app, and create a distribution.

## Running the project ##

The project includes a script that will allow you to start the server and
run the web app from the source directory. You can run it by executing the
following commands:

```
> cd BlogJaveChallenge/BlogJavaDist/target/BlogJavaDist-1.0-dist
> chmod a+x dev.sh
> ./dev.sh
```

However, it's probably easier to run this from the debugger. The main class is
`interview.blog.BlogServer`. A note: don't just run this class from inside
Netbeans, or you'll create a process that's hard to track down and kill. The
debugger works correctly, though.

You can access the web app at <http://localhost:8080/>