(ns org.tobereplaced.lettercase.internal
  (:require [clojure.string :refer [join split-lines trim trimr]]))

(def ^:private docstrings
  "A map from case words to corresponding documentation fragments."
  {:capitalized "Each word will be capitalized."
   :mixed "The first word will be converted to lowercase, and the rest will be
  capitalized."
   :upper "Each word will be converted to all uppercase letters."
   :lower "Each word will be converted to all lowercase letters."})

(defn- clean-docstring
  "Cleans a clojure docstring by removing existing line breaks and
  adding breaks at 70 characters."
  [s]
  (let [trimmed-s (->> s split-lines (map trim) (join \space))]
    (loop [index 0
           [[match word] :as words] (re-seq #"(\S+)\s*" trimmed-s)
           output []]
      (if (seq words)
        (if (< (+ index (count word)) 69)
          (recur (+ index (count match)) (rest words) (conj output match))
          (recur 0 words (conj output \newline "  ")))
        (->> output
             (apply str)
             split-lines
             (map trimr)
             (join \newline))))))

(defn docstring
  "Returns a customized docstring for f with casings and space-string."
  [fn-symbol fn-impl casing space-string]
  (-> "Returns a new string by manipulating the lettercase of s.  %s Each
  word will be separated by \"%s\".  Word boundaries will be
  determined by re if provided, otherwise they will be determined by
  the default word separation regex."
      (format (casing docstrings) space-string)
      clean-docstring
      (str \newline \newline
           (format "  Example: (%s \"Foo the bar\") => \"%s\""
                   fn-symbol (fn-impl "Foo the bar")))))
