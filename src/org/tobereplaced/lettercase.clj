(ns org.tobereplaced.lettercase
  "Declarative case conversion."
  (:require [clojure.string
             :refer [join split capitalize lower-case upper-case]]
            [org.tobereplaced.lettercase.internal :refer [docstring]])
  (:import [clojure.lang Keyword Symbol]))


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

(def ^:private case-functions
  "A map from case words to a pair with a function for its first word
  and function for the rest of its words."
  {:capitalized [capitalize capitalize]
   :mixed [lower-case capitalize]
   :upper [upper-case upper-case]
   :lower [lower-case lower-case]})

(def ^:private space-strings
  "A map from spacing words to their corresponding strings."
  {:space " "
   :underscore "_"
   :hyphen "-"
   nil ""})

(doseq [[casing [first-fn rest-fn]] case-functions
        [spacing space-string] space-strings]
  (let [fn-impl (fn impl
                  ([s]
                     (impl s word-separator-pattern))
                  ([s re]
                     (let [[word & more] (split s re)]
                       (join space-string
                             (cons (first-fn word) (map rest-fn more))))))
        fn-symbol (->> [casing spacing]
                       (remove nil?)
                       (map name)
                       (join "-")
                       symbol)]
    (intern *ns*
            (with-meta fn-symbol
              {:doc (docstring fn-symbol fn-impl casing space-string)
               :arglists '([s] [s re])})
            fn-impl)))

;;; TODO: Create a variadic function for this.
(defprotocol AlterName
  (alter-name [this f] "Alters the name of this with f."))

(extend-protocol AlterName
  Keyword (alter-name [this f] (->> this name f (keyword (namespace this))))
  Symbol (alter-name [this f] (->> this name f (symbol (namespace this)))))
