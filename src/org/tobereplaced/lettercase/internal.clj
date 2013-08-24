(ns org.tobereplaced.lettercase.internal
  (:require [clojure.edn :as edn]
            [clojure.java.io :refer [resource]]
            [clojure.string :refer [join split-lines trim trimr]]))

(def ^:private doc-fragments
  "Documentation fragments."
  (-> "doc-fragments.edn" resource slurp edn/read-string))

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
  [fn-symbol fn-impl [casing spacing return-key]]
  (let [example (if (= return-key :name) :Foo-the-Bar "Foo the Bar")]
    (-> (join "  " ((juxt return-key casing spacing :boundaries) doc-fragments))
        clean-docstring
        (str \newline \newline
             (format "  Example: (%s %s) => %s"
                     fn-symbol (pr-str example) (pr-str (fn-impl example)))))))
