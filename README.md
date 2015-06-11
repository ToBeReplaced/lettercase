# lettercase

The aim is to create a declarative, simple API for manipulating the case convention of keywords, symbols, and strings.

This solves the problem of manipulating case when communicating between different technologies. For example, converting your lisp-cased keywords into lowercase strings separated by underscores for your relational database.

This project is similar to [camel-snake-kebab]. It differs in a few primary areas:

- It does not enable or allow implicit conversion between named types.
- It is extensible.
- It does not intend to support clojurescript.

## Supported Clojure Versions

lettercase is tested on Clojure 1.5.1 only. It may work on other Clojure versions.

## Maturity

This is production-ready software.

## Installation

lettercase is available as a Maven artifact from [Clojars]:
```clojure
[org.tobereplaced/lettercase "1.0.0"]
```
lettercase follows [Semantic Versioning]. Please note that this means the public API for this library is considered stable, and any breaking changes must occur after a 2.0.0 release.

## Documentation

The [Codox API Documentation] is extensive and is the best way of understanding the library. If you are looking for a quick example:

```clojure
(ns lettercase-usage
    (:require [org.tobereplaced.lettercase :refer [lower-hyphen-keyword]]))

(lower-hyphen-keyword "Foo the BAR")
;; :foo-the-bar
```

## Support

Please post any comments, concerns, or issues to the Github issues page. I welcome any and all feedback. If you have a use-case that is not currently supported, but that you believe belongs in this library, let me know!

## License

Copyright © 2015 ToBeReplaced

Distributed under the Eclipse Public License, the same as Clojure. The license can be found at epl-v10.html in the root of this distribution.

[camel-snake-kebab]: https://github.com/qerub/camel-snake-kebab
[Clojars]: http://clojars.org/org.tobereplaced/lettercase
[Semantic Versioning]: http://semver.org
[Codox API Documentation]: http://ToBeReplaced.github.com/lettercase
