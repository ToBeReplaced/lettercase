(ns org.tobereplaced.lettercase.protocols
  (:import [clojure.lang Keyword Symbol]))

(defprotocol AlterName
  (alter-name [this f] "Alters the name of this with f."))

(extend-protocol AlterName
  Keyword (alter-name [this f] (->> this name f (keyword (namespace this))))
  Symbol (alter-name [this f] (->> this name f (symbol (namespace this)))))

(defprotocol AlterNamespace
  (alter-namespace [this f] "Alters the namespace of this with f."))

(extend-protocol AlterNamespace
  Keyword (alter-namespace [this f] (-> this namespace f (keyword (name this))))
  Symbol (alter-namespace [this f] (-> this namespace f (symbol (name this)))))
