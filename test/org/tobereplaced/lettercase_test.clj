(ns org.tobereplaced.lettercase-test
  (:require (clojure (test :refer [deftest is]))
            (org.tobereplaced (lettercase :refer [lower-hyphen]))))

;;; Example test... tests not yet written.
(deftest separator-stress-test
  (is (= (lower-hyphen "foo/Bar_baz|spamIsHTMLFunny,or maybe2-Not10")
         "foo-bar-baz-spam-is-html-funny-or-maybe-2-not-10")
      "should separate at expected points"))
