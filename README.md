# Coffee script Play! plugin.

## Objective

This module integrates [Play!](http://www.playframework.org) with [Coffee script](http://jashkenas.github.com/coffee-script/).  It uses [JCoffeeScript](https://github.com/yeungda/jcoffeescript) to run the Coffee compiler (which is written in Coffee) on [Rhino](http://www.mozilla.org/rhino/).

## Features

There are two ways to include Coffee in your page.

### Inline Coffee

You can write Coffee script directly in your Play! templates.

For example:
    #{coffee.inline}

    # Assignment:
    number   = 42
    opposite = true

    # Conditions:
    number = -42 if opposite

    # Functions:
    square = (x) -> x * x

    # Arrays:
    list = [1, 2, 3, 4, 5]

    # Objects:
    math =
      root:   Math.sqrt
      square: square
      cube:   (x) -> x * square x

    # Splats:
    race = (winner, runners...) ->
      print winner, runners

    # Existence:
    alert "I knew it!" if elvis?

    # Array comprehensions:
    cubes = (math.cube num for num in list)

    #{/}


produces the following output on your page

    <script type="text/javascript">
    (function() {
      var cubes, list, math, num, number, opposite, race, square;
      var __slice = Array.prototype.slice;
      number = 42;
      opposite = true;
      if (opposite) {
        number = -42;
      }
      square = function(x) {
        return x * x;
      };
      list = [1, 2, 3, 4, 5];
      math = {
        root: Math.sqrt,
        square: square,
        cube: function(x) {
          return x * square(x);
        }
      };
      race = function() {
        var runners, winner;
        winner = arguments[0], runners = 2 <= arguments.length ? __slice.call(arguments, 1) : [];
        return print(winner, runners);
      };
      if (typeof elvis != "undefined" && elvis !== null) {
        alert("I knew it!");
      }
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

### Included Coffee

You can also write your Coffee in a separate file, including it via a script tag.

For example,
    <script type="text/javascript" href="@{'/public/javascripts/sample.coffee'}"></script>

The module directly handles this request and compiles the coffee on the fly.

## Known issues

1. It is currently impossible to use String interpolation in Inline Coffee (since the Play! template syntax clashes, and it is evaluated first)
2. Coffee compilation errors presently write their error message in place of the compiled javascript.  Instead, they should result in a helpful error page (showing the offending line of code)just like other errors.
