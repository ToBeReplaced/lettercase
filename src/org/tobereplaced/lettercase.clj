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
        "[\\- _,/|]"
        ;; Uppercase letter followed by uppercase and lowercase.
        "(?<=\\p{Lu})(?=\\p{Lu}[\\p{L}&&[^\\p{Lu}]])"
        ;; Lowercase letter followed by uppercase letter.
        "(?<=[\\p{L}&&[^\\p{Lu}]])(?=\\p{Lu})"
        ;; Letter followed by number.  Ex: area51 -> area 51.
        "(?<=\\p{L})(?=\\p{N})"]
       (join "|")
       re-pattern))

(defn- convert-case [first-fn rest-fn separator s & [p]]
  "Converts the case of a string s according to the rule for the first
  word, remaining words, and the separator. Optionally, accepts a pattern
  for word separation."
  (-> s
      (split (if p p word-separator-pattern))
      ((fn [[word & more]]
         (join separator (cons (first-fn word) (map rest-fn more)))))))

;;; TODO: Set up in loop to prevent mistakes
(def ^:private case-conversion-rules
  "The formatting rules for each case."
  {"camel" [capitalize capitalize ""
            "first: capitalize\nrest: capitalize\nseparator: none"]
   "camel-space" [capitalize capitalize " "
                  "first: capitalize\nrest: capitalize\nseparator: space"]
   "camel-underscore" [capitalize capitalize "_"
                       "first: capitalize\nrest: capitalize\nseparator: underscore"]
   "camel-hyphen" [capitalize capitalize "-"
                   "first: capitalize\nrest: capitalize\nseparator: dash"]
   "mixed" [lower-case capitalize ""
            "first: lower-case\nrest: capitalize\nseparator: none"]
   "mixed-space" [lower-case capitalize " "
                  "first: lower-case\nrest: capitalize\nseparator: space"]
   "mixed-underscore" [lower-case capitalize "_"
                       "first: lower-case\nrest: capitalize\nseparator: underscore"]
   "mixed-hyphen" [lower-case capitalize "-"
                   "first: lower-case\nrest: capitalize\nseparator: dash"]
   "upper" [upper-case upper-case ""
            "first: upper-case\nrest: upper-case\nseparator: none"]
   "upper-space" [upper-case upper-case " "
                  "first: upper-case\nrest: upper-case\nseparator: space"]
   "upper-underscore" [upper-case upper-case "_"
                       "first: upper-case\nrest: upper-case\nseparator: underscore"]
   "upper-hyphen" [upper-case upper-case "-"
                   "first: upper-case\nrest: upper-case\nseparator: dash"]
   "lower" [lower-case lower-case ""
            "first: lower-case\nrest: lower-case\nseparator: none"]
   "lower-space" [lower-case lower-case " "
                  "first: lower-case\nrest: lower-case\nseparator: space"]
   "lower-underscore" [lower-case lower-case "_"
                       "first: lower-case\nrest: lower-case\nseparator: underscore"]
   "lower-hyphen" [lower-case lower-case "-"
                   "first: lower-case\nrest: lower-case\nseparator: dash"]})

;;; DONE: Add docstring to generated functions.
;;; DONE: The functions should take in an optional word-separator pattern.
;;; TODO: We should expose a builder of word-separator patterns.
(doseq [[case-label [first-fn rest-fn separator doc]] case-conversion-rules]
  (intern *ns* (with-meta (symbol case-label)
                 {:doc doc
                  :arglists '([s & re])})
          (fn [s & [re]]
            (convert-case first-fn rest-fn separator s re))))

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
