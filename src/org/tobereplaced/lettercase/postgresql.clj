(ns org.tobereplaced.lettercase.postgresql
  (:require [org.tobereplaced.lettercase :refer [lower-underscore]]))

(defn entities
  "A function usable as the :entities argument in clojure.java.jdbc.
  This assumes you follow the PostgreSQL standard of naming entities
  according to lower-underscore and escapes all entities with double
  quotes."
  [s]
  (str \" (lower-underscore s) \"))
