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
- Building the parameter matcher *(optional)*
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
  - `java.lang.String` // the name of the leaf  
  - `java.util.function.Function<hu.zza.clim.menu.ProcessedInput, java.lang.Integer>` // the functionality  
  - `java.lang.String...` // one or more node name -> the possible forwarding destination(s)  

If the user choose a leaf, *clim* calls its function (with the processed input),  
and in according to the functions result (returning integer), *clim* chooses  
the n-th forwarding node and navigates to it.  

In the code snippet *Date* and *Time* leaves' lambda returns with zero,  
so *clim* chooses the one and only element from the forwarding list, *Flat Menu*.  
  
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

### Building the parameter matcher *(optional)*

### Building the menu with built components

## clim in action
You can check the initialisation of **clim** in the project 'Shared Bills Splitter (Hyperskill)': [hu.zza.hyperskill.splitter.config](https://github.com/hu-zza/Shared_Bills_Splitter_-Hyperskill-/tree/master/src/main/java/hu/zza/hyperskill/splitter/config)
