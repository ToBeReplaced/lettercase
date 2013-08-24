(ns org.tobereplaced.lettercase-test
  (:require [clojure.test :refer [deftest is are]]
            [org.tobereplaced.lettercase
             :refer [lower lower-space lower-hyphen lower-underscore
                     lower-hyphen-keyword lower-hyphen-symbol lower-hyphen-name
                     capitalized-space sentence-space mixed-space upper-space]])
  (:import [clojure.lang ArityException]))

(deftest separator-pattern-stress-test
  (is (= (lower-hyphen "foo/Bar_baz|spamIsHTMLFunny,or maybe2-Not10")
         "foo-bar-baz-spam-is-html-funny-or-maybe-2-not-10")
      "should separate at expected points"))

(deftest casing-test
  (let [s "Foo-the-Bar"]
    (is (= (capitalized-space s) "Foo The Bar")
        "capitalized should have each word capitalized")
    (is (= (sentence-space s) "Foo the bar")
        "sentence should capitalize the first word")
    (is (= (mixed-space s) "foo The Bar")
        "mixed should capitalize everything but the first word")
    (is (= (upper-space s) "FOO THE BAR")
        "upper should uppercase each word")
    (is (= (lower-space s) "foo the bar")
        "lower should lowercase each word")))

(deftest separator-test
  (let [s "FOO-THE-BAR"]
    (is (= (lower s) "foothebar")
        "no identifier should have no separator")
    (is (= (lower-space s) "foo the bar")
        "space should have spaces")
    (is (= (lower-hyphen s) "foo-the-bar")
        "hyphen should have hyphens")
    (is (= (lower-underscore s) "foo_the_bar")
        "underscore should have underscores")))

(deftest custom-pattern-test
  (is (= (lower-space "FOO--THE--BAR") "foo  the  bar")
      "multiple separators should yield blank words")
  (is (= (lower-space "FOO--THE--BAR" #"--") "foo the bar")
      "should be able to override separator"))

(deftest keyword-test
  (let [s "Foo the Bar"]
    (is (= (lower-hyphen-keyword s) :foo-the-bar)
        "should convert basic string to keyword")
    (is (= (lower-hyphen-keyword "namespace" s) :namespace/foo-the-bar)
        "should accept an optional namespace")
    (is (= (lower-hyphen-keyword s #" the ") :foo-bar)
        "should accept an optional separator pattern")
    (is (= (lower-hyphen-keyword "namespace" s #" the ") :namespace/foo-bar)
        "should accept a namespace and separator pattern")))

(deftest symbol-test
  (let [s "Foo the Bar"]
    (is (= (lower-hyphen-symbol s) 'foo-the-bar)
        "should convert basic string to symbol")
    (is (= (lower-hyphen-symbol "namespace" s) 'namespace/foo-the-bar)
        "should accept an optional namespace")
    (is (= (lower-hyphen-symbol s #" the ") 'foo-bar)
        "should accept an optional separator pattern")
    (is (= (lower-hyphen-symbol "namespace" s #" the ") 'namespace/foo-bar)
        "should accept a namespace and separator pattern")))

(deftest name-test
  (let [x :fake-namespace/FooTheBar]
    (is (= (lower-hyphen-name x) "foo-the-bar")
        "should return a converted string of the name of the keyword")
    (is (= (lower-hyphen-name x #"The") "foo-bar")
        "should accept an optional separator pattern")))

(deftest bad-arity-text
  (is (thrown? ArityException (lower "foo-bar" #"-" :extra-arg))))

(deftest regex-corner-case-1
  (is (= (lower-hyphen "123ABCdef") "123-ab-cdef")
      "should handle corner case"))
