# lettercase

The aim is to create a declarative, simple API for manipulating the case convention of keywords, symbols, and strings.

This solves the problem of manipulating case when communicating between different technologies.  For example, converting your lisp-cased keywords into snake_cased strings for your relational database.

This project is similar to [camel-snake-kebab].  It differs in two primary areas:

- It does not enable or allow implicit conversion between named types.
- It has aspirations of much wider scope.

## Supported Clojure Versions

lettercase is tested on Clojure 1.5.1 only.  It may work on other Clojure versions.

## Maturity

This is alpha quality software.

<!-- ## Installation -->

<!-- lettercase is available as a Maven artifact from [Clojars]: -->
<!-- ```clojure -->
<!-- [org.tobereplaced/lettercase "0.1.0"] -->
<!-- ``` -->
<!-- lettercase follows [Semantic Versioning].  Please note that this means the public API for this library is not considered stable. -->

## Documentation

The [Codox API Documentation] is extensive and is the best way of understanding the library.  For working with strings, see the many conversion functions, like `lower-hyphen`.  For working with keywords or symbols, see the functions `alter-name` and `alter-namespace`.

## TODO

- Update tests and create many more
- Begin work on a declarative namespace for a style guide, like PEP8.

## Support

Please post any comments, concerns, or issues to the Github issues page.  I welcome any and all feedback.  If you have a use-case that is not currently supported, but that you believe belongs in this library, let me know!

## License

Copyright © 2013 ToBeReplaced

Distributed under the Eclipse Public License, the same as Clojure.  The license can be found at epl-v10.html in the root of this distribution.

[camel-snake-kebab]: https://github.com/qerub/camel-snake-kebab
[Clojars]: http://clojars.org/org.tobereplaced/mapply
[Semantic Versioning]: http://semver.org
[Codox API Documentation]: http://ToBeReplaced.github.com/lettercase
