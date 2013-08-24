(ns org.tobereplaced.lettercase
  "Declarative case conversion."
  (:require [clojure.string
             :refer [join split capitalize lower-case upper-case]]
            [org.tobereplaced.lettercase.internal :refer [docstring]]
            [org.tobereplaced.lettercase.protocols :as protocols])
  (:import [java.util.regex Pattern]))

(defn separator-pattern
  "Returns a composite pattern that can be used to break a string into
  words.  The resulting pattern will match any space character, an
  upper letter immediately followed by an upper letter immediately
  followed by a lower letter, a lower letter immediately followed by
  an upper letter, any letter immediately followed by a number, and
  any number immediately followed by a number."
  [& {:keys [space upper letter number]
      :or {space #"[\- _,/|]"
           upper #"\p{Lu}"
           letter #"\p{L}"
           number #"\p{N}"}}]
  (let [lower (format "[%s&&[^%s]]" letter upper)
        pair (fn [before after] (format "(?<=%s)(?=%s)" before after))]
    (->> [space
          (pair upper (str upper lower))
          (pair lower upper)
          (pair letter number)
          (pair number letter)]
         (join \|)
         re-pattern)))

(def ^:private default-separator-pattern
  "The default separator pattern returned by separator-pattern."
  (separator-pattern))

(def ^:private case-functions
  "A map from case words to a pair with a function for its first word
  and function for the rest of its words."
  {:capitalized [capitalize capitalize]
   :sentence [capitalize lower-case]
   :mixed [lower-case capitalize]
   :upper [upper-case upper-case]
   :lower [lower-case lower-case]})

(def ^:private space-strings
  "A map from spacing words to their corresponding strings."
  {:space " "
   :underscore "_"
   :hyphen "-"
   :no-separator ""})

(def ^:private return-type-configuration
  "A map from the return type to a pair containing arglists metadata
  and a function that can be called to wrap the underlying string
  conversion accordingly."
  (let [symbol-or-keyword-fn (fn [ctor]
                               (fn [f]
                                 (fn
                                   ([s] (ctor (f s)))
                                   ([x y]
                                      (if (instance? Pattern y)
                                        (ctor (f x y))
                                        (ctor x (f y))))
                                   ([ns s re] (ctor ns (f s re))))))]
    {:keyword ['([s] [s re] [ns s] [ns s re]) (symbol-or-keyword-fn keyword)]
     :symbol ['([s] [s re] [ns s] [ns s re]) (symbol-or-keyword-fn symbol)]
     :name ['([x] [x re]) (fn [f]
                            (fn
                              ([x] (f (name x)))
                              ([x re] (f (name x) re))))]
     :string ['([s] [s re]) identity] }))

(doseq
    [[casing [first-fn rest-fn]] case-functions
     [spacing space-string] space-strings
     :let [string-fn-impl (fn impl
                            ([s]
                               (impl s default-separator-pattern))
                            ([s re]
                               (let [[word & more] (split s re)]
                                 (join space-string
                                       (cons (first-fn word)
                                             (map rest-fn more))))))]
     [return-key [arglists wrapper]] return-type-configuration
     :let [identifiers [casing spacing return-key]
           fn-symbol (->> identifiers
                          (remove #(contains? #{:no-separator :string} %))
                          (map name)
                          (join "-")
                          symbol)
           fn-impl (wrapper string-fn-impl)]]
  (intern *ns*
          (with-meta fn-symbol
            {:doc (docstring fn-symbol fn-impl identifiers)
             :arglists arglists})
          fn-impl))

(defn alter-name
  "Returns a new symbol, keyword, or string by applying f to the name
  of x and any additional args.

  Example: (alter-name :foo/bar upper) => :foo/BAR"
  [x f & args]
  (protocols/alter-name x #(apply f % args)))

(defn alter-namespace
  "Returns a new symbol or keyword by applying f to the namespace of x
  and any additional args.

  Example: (alter-namespace :foo/bar upper) => :FOO/bar"
  [x f & args]
  (protocols/alter-namespace x #(apply f % args)))
