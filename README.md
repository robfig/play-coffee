# Coffee script Play! plugin.

## Background

[CoffeeScript](http://jashkenas.github.com/coffee-script/) is a better way to produce JavaScript.  It provides some great language features that JS lacks (e.g. list comprehensions, multiline strings, string interpolation, destructuring assignment, etc), while cleaning up the syntax at the same time.  It compiles down to debuggable JS. 

## Objective

The goal is to make [CoffeeScript](http://jashkenas.github.com/coffee-script/) frictionless to use with [Play!](http://www.playframework.org) web framework, including both flavors (Java and Scala).

## Overview 

This module integrates [Play!](http://www.playframework.org) with [Coffee script](http://jashkenas.github.com/coffee-script/).  It uses [JCoffeeScript](https://github.com/yeungda/jcoffeescript) to run the Coffee compiler (which is written in Coffee) on [Rhino](http://www.mozilla.org/rhino/).  It provides both Java and Scala support.

This module provides two ways to write Coffee: 

*   Inline - write coffee right in your template
*   Included - keep your coffee in separate resource files

## Inline Coffee

You can write Coffee script directly in your Play! templates using the `#{coffee.inline}` tag.

For example (Java):

    #{coffee.inline}
    # Array comprehensions:
    cubes = (math.cube num for num in [1, 2, 3])
    #{/}

or (Scala): 

    @import tags.coffee

    @coffee.inline() {
    # Array comprehensions:
    cubes = (math.cube num for num in [1, 2, 3])
    }

will produce the compiled javascript directly into your page:

    <script type="text/javascript">
    (function() {
      var cubes;
      cubes = (function() {
        var _i, _len, _results;
        _results = [];
        for (_i = 0, _len = list.length; _i < _len; _i++) {
          num = list[_i];
          _results.push(math.cube(num));
        }
        return _results;
      })();
    }).call(this);
    </script>

### String interpolation (Java only)

Both CoffeeScript and Groovy templates use the syntax `#{...}`.  To differentiate them, use the syntax `#\{variable}` when you want inline Coffee string interpolation.  Note that this is only necessary when using inline Coffee in Groovy templates.  In other contexts, the usual syntax should be followed.

For example:

    #{coffee.inline}
    x = 5
    y = 8
    console.log "The value of x and y are #\{x} and #\{y}"
    #{/}

### Play! tags

All of the usual Play! tags may be used within your Coffee.

For example:

    #{coffee.inline}
    query = "${query.raw()}"
    #{if logQuery} 
    console.log "The query was #\{query}"
    #{/if}
    #{/}

or (Scala):

    @coffee.inline() {
    query = "@query.raw"
    @if (logQuery) { 
    console.log "The query was #\{query}"
    }
    }


### Compilation errors

If there is an error compiling your Coffee, a 500 will be returned, and in development mode you will see the pretty Play! "Template compilation error" screen.  As usual, it shows you the error message plus the line of source code that caused the error.  

## Included Coffee

You can also write your Coffee in a separate file, including it via a script tag.

For example (Java),

    <script type="text/javascript" href="@{'/public/javascripts/sample.coffee'}"></script>

or (Scala),

    <script type="text/javascript" href="@asset("/public/javascripts/sample.coffee")"></script>

The module handles the request and compiles the coffee on the fly.  

Note that Play! template tags may not be used in included coffee files, and String interpolation works according to the Coffee spec (e.g. `#{variable}`).  

### Caching

In general, Coffee is only re-compiled if it has changed since the last request (based on the file's last-modified date).  In the absence of changes to the source, the compiled Coffee is cached forever.

In production mode, a cache header is also set telling the client to cache the file for 1 hour.

### Compilation errors

A compilation error in an included file will cause the error to be logged and will return 500.  If the developer visits the resource link directly, he can see the useful compilation error screen showing the offending line.

## Getting started

This module is on www.playframework.org, so follow the usual steps.  

To use it in your Play! project:

1. Add `- play -> coffee 1.0` in your dependencies.yml.
2. Run 'play dependencies' in your app's directory to download it.

The *sample* application included in the module exercises all of the functionality in the module, making it a good reference.  It may be run by running 'play test' in the 'play-coffee/sample' directory.

A *sample-scala* application is also included, to demonstrate and test usage with the Scala module.

## Feedback welcome. 

All feedback is very welcome.  Feel free to create Issues for feature requests or bugs.  