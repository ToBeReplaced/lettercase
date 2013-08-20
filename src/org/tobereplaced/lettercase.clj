(ns org.tobereplaced.lettercase
  "Declarative case conversion."
  (:require (clojure (string :refer [join split capitalize
                                     lower-case upper-case])))
  (:import (clojure.lang Keyword Symbol)))


;;; TODO: Break out in to submatches.  Allow configuration of space,
;;; uppercase, letter, number.
(def ^:private word-separator-pattern
  "A pattern that matches all known word separators."
  (->> [;; Match any "space" character.
        #"[\- _,/|]"
        ;; Uppercase letter followed by uppercase and lowercase.
        #"(?<=\p{Lu})(?=\p{Lu}[\p{L}&&[^\p{Lu}]])"
        ;; Lowercase letter followed by uppercase letter.
        #"(?<=[\p{L}&&[^\p{Lu}]])(?=\p{Lu})"
        ;; Letter followed by number.  Ex: area51 -> area 51.
        #"(?<=\p{L})(?=\p{N})"
        ;; Number followed by letter
        #"(?<=\p{N})(?=\p{L})"]
       (join \|)
       re-pattern))

(defn- convert-case [first-fn rest-fn separator [word & more]]
  "Converts the case of a string s according to the rule for the first
  word, remaining words, and the separator. Optionally, accepts a pattern
  for word separation."
  (join separator (cons (first-fn word) (map rest-fn more))))

;;; TODO: Set up in loop to prevent mistakes
(def ^:private case-conversion-rules
  "The formatting rules for each case."
  (let [cptlz [capitalize "clojure.string/capitalize"]
        lcase [lower-case "clojure.string/lower-case"]
        ucase [upper-case "clojure.string/upper-case"]]
    {"camel" [cptlz cptlz ""]
     "camel-space" [cptlz cptlz " "]
     "camel-underscore" [cptlz cptlz "_"]
     "camel-hyphen" [cptlz cptlz "-"]
     "mixed" [lcase cptlz ""]
     "mixed-space" [lcase cptlz " "]
     "mixed-underscore" [lcase cptlz "_"]
     "mixed-hyphen" [lcase cptlz "-"]
     "upper" [ucase ucase ""]
     "upper-space" [ucase ucase " "]
     "upper-underscore" [ucase ucase "_"]
     "upper-hyphen" [ucase ucase "-"]
     "lower" [lcase lcase ""]
     "lower-space" [lcase lcase " "]
     "lower-underscore" [lcase lcase "_"]
     "lower-hyphen" [lcase lcase "-"]}))

(defn- case-fn-doc [first-fn-name rest-fn-name separator]
  (format "With one arg, splits s according to default word separation regex.
  With two args, uses re instead of default word separation regex.
  In both cases applies the functions listed below to the list of
  split words and joins the results using the indicated separator:

  first word:    %s
  rest of words: %s
  separator:     \"%s\"" first-fn-name rest-fn-name separator))

;;; TODO: We should expose a builder of word-separator patterns.
(doseq [[case-label [[fst-fn fst-fn-name]
                     [rest-fn rest-fn-name] sep]] case-conversion-rules]
  (let [fn-doc (case-fn-doc fst-fn-name rest-fn-name sep)
        fn-meta {:doc fn-doc :arglists '([s] [s re])}
        fn-sym (with-meta (symbol case-label) fn-meta)
        fn-impl (fn thefn
                  ([s] (thefn s word-separator-pattern))
                  ([s re] (let [words (split s re)]
                            (convert-case fst-fn rest-fn sep words))))]
    (intern *ns* fn-sym fn-impl)))

;;; TODO: Is this the correct thing to do?
;;; Ex: (alter-name :fooBar lower-hyphen) -> :foo-bar
(defprotocol AlterName
  (alter-name [this f] "Alters the name of this with f."))

(extend-protocol AlterName
  String (alter-name [this f] (-> this f))
  Keyword (alter-name [this f] (-> this name f keyword))
  Symbol (alter-name [this f] (-> this name f symbol)))

(comment
  ;; "foo-bar-is-baz-no-way-html"
  (lower-hyphen "foo/barIsBaz,NoWayHTML"))
