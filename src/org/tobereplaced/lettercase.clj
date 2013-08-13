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

(defn- convert-case [first-fn rest-fn separator s]
  "Converts the case of a string s according to the rule for the first
  word, remaining words, and the separator."
  (-> s
      (split word-separator-pattern)
      ((fn [[word & more]]
         (join separator (cons (first-fn word) (map rest-fn more)))))))

;;; TODO: Set up in loop to prevent mistakes
(def ^:private case-conversion-rules
  "The formatting rules for each case."
  {"camel" [capitalize capitalize ""]
   "camel-space" [capitalize capitalize " "]
   "camel-underscore" [capitalize capitalize "_"]
   "camel-hyphen" [capitalize capitalize "-"]
   "mixed" [lower-case capitalize ""]
   "mixed-space" [lower-case capitalize " "]
   "mixed-underscore" [lower-case capitalize "_"]
   "mixed-hyphen" [lower-case capitalize "-"]
   "upper" [upper-case upper-case ""]
   "upper-space" [upper-case upper-case " "]
   "upper-underscore" [upper-case upper-case "_"]
   "upper-hyphen" [upper-case upper-case "-"]
   "lower" [lower-case lower-case ""]
   "lower-space" [lower-case lower-case " "]
   "lower-underscore" [lower-case lower-case "_"]
   "lower-hyphen" [lower-case lower-case "-"]})

;;; TODO: Add docstring to generated functions.
;;; TODO: The functions should take in an optional word-separator pattern.
;;; TODO: We should expose a builder of word-separator patterns.
(doseq [[case-label [first-fn rest-fn separator]] case-conversion-rules]
  (let [case-converter (partial convert-case first-fn rest-fn separator)]
    (intern *ns* (symbol case-label)
            #(convert-case first-fn rest-fn separator %))))

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
