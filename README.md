# clim

**clim** is a handy Java module for building various menus (ordinal, nominal, parametric).  
Please note, every release are alpha, so using this library will be sometimes funky...  
*(...or annoying.)*  

Well, I'll try my best *(and every contribution is appreciated)*,  
so some day **clim** will be as good  as I hope it will.  
  
  
## Import clim

This is pretty easy thanks to [JitPack](https://jitpack.io/).  
You find handy guides for *gradle*, *maven*, *sbt* and *leiningen* too.  
First of all, click on the badge of the current release:  
[![Release](https://jitpack.io/v/hu.zza/clim.svg)](https://jitpack.io/#hu.zza/clim)  
*(...and the site will help you to import it.)*  


## Building the menu

There are builders to make it quick and easy. The main steps:  

- Building the structure of the menu
- *Only for parametric menus*
  - Defining the enum for parameter names
  - Defining Parameter objects
  - Building the parameter matcher
- Building the menu with built components

### Building the structure of the menu
```java
import java.time.*;

MenuStructure menuStructure =
    new MenuStructureBuilder()
        .setRawMenuStructure("{\"Flat Menu\" : [\"Date\", \"Time\", \"Help\", \"Exit\"]}")
        .setInitialPosition("Flat Menu")
        .setLeaf("Date", e -> {System.out.println(LocalDate.now()); return 0;}, "Flat Menu")
        .setLeaf("Time", e -> {System.out.println(LocalTime.now()); return 0;}, "Flat Menu")
        .setLeaf("Help", Console::help, "Flat Menu")
        .setLeaf("Exit", Console::exit, "Flat Menu")
        .build();
```  
  
The code snippet above represents a very basic menu: one *node* and four *leaves*:  
  
* `Flat Menu`
  - Date
  - Time
  - Help
  - Exit

**Please note, in *clim* a leaf is not a node:**  
*Nodes* are "walkable" points without functionality.  
*Leaves* are function representations without "real" position.  
  
`setRawMenuStructure` accepts either a `com.google.gson.JsonObject` or a `java.lang.String`.  
(The String should be a valid JSON text which is processable by `com.google.gson.JsonParser`.)  
In both cases the argument should represent a `com.google.gson.JsonObject`.  
The keys of the JsonObject are the nodes, and the primitive values are leaves.
  
`setInitialPosition` accepts a `java.lang.String` which should be the name of a node.  
If there is only one node (like in the snippet), this method can be omitted.
  
`setLeaf` parameters are:  
  - `java.lang.String` name // the name of the leaf  
  - `java.util.function.Function<hu.zza.clim.menu.ProcessedInput, java.lang.Integer>` function // the functionality  
  - `java.lang.String...` links // one or more **node** name -> the possible forwarding destination(s)  

If the user choose a leaf, *clim* calls its function (with the processed input),  
and in according to the functions result (returning integer), *clim* chooses  
the n-th forwarding node and navigates to it.  

In the code snippet *Date* and *Time* leaves' lambdas (after `println`) return with zero,  
so *clim* chooses the one and only element from the forwarding list, *Flat Menu*.  

A bit more complex example:
  
```java
import java.time.*;

MenuStructure menuStructure =
    new MenuStructureBuilder()
        .setRawMenuStructure("{\"Double-decker\" : [
            {\"Date & Time\" : [\"Date\", \"Time\", \"Double-decker\"]},
            \"Help\",
            \"Exit\"
          ]}")
        .setInitialPosition("Double-decker")
        .setLeaf("Date", e -> {System.out.println(LocalDate.now()); return 0;}, "Double-decker", "Date & Time")
        .setLeaf("Time", e -> {System.out.println(LocalTime.now()); return 1;}, "Double-decker", "Date & Time")
        .setLeaf("Help", Console::help, "Flat Menu")
        .setLeaf("Exit", Console::exit, "Flat Menu")
        .build();
```  
* `Double-decker`
  - `Date & Time`
    + Date
    + Time
    + Double-decker (it's a pseudo-leaf, a link to the node)  
  - Help
  - Exit

Our initial position is the *Double-decker* node, and we have three choices:  
*Date & Time*, *Help*, *Exit*  
The last two are leaves and set with function references. Without examining *Console::help* and *Console::exit*  
we can assume this two do their job and return with 0. (So the menu stays at the initial position after them.)  
However *Date & Time* is a node. If we choose this one, the menu navigates to it without any extra job.  
In this case there is three additional choice:  
*Date*, *Time*, *Double-decker*  
The first one prints the date and returns with 0. So the menu navigates to *Double-decker*.  
The second one prints the time and returns with 1. So the menu navigates to *Date & Time*.  
The last one is a bit odd: Because we have a node with the exact same name (Double-decker),  
this will be autoconfigured.  

### *Only for parametric menus*  
Coming soon...  

### Building the menu with built components

```java
Menu menu =
  new MenuBuilder()
    .setMenuStructure(menuStructure)
    .setClimOptions(UserInterface.NOMINAL, HeaderStyle.STANDARD)
    .build();
```

Building the *Flat Menu* or the *Double-decker* is as easy as this snippet shows.  
After setting the built `hu.zza.clim.menu.MenuStructure`, you should specify the options.  
In this case the menu is nominal, the user should type the whole name of the node / leaf.


## Using the menu

The `hu.zza.clim.Menu` has the following public methods:
  - chooseOption(String)
  - listOptions()
  - printLicense()
  - printShortLicense()

## clim in action
You can check the initialisation of **clim** in the project 'Shared Bills Splitter (Hyperskill)': [hu.zza.hyperskill.splitter.config](https://github.com/hu-zza/Shared_Bills_Splitter_-Hyperskill-/tree/master/src/main/java/hu/zza/hyperskill/splitter/config)
