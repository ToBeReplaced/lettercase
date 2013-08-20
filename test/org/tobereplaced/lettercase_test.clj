(ns org.tobereplaced.lettercase-test
  (:require [clojure.test :refer [deftest is are]]
            [org.tobereplaced.lettercase
             :refer [capitalized capitalized-space capitalized-underscore
                     capitalized-hyphen
                     mixed mixed-space mixed-underscore mixed-hyphen
                     upper upper-space upper-underscore upper-hyphen
                     lower lower-space lower-underscore lower-hyphen]])
  (:import [clojure.lang ArityException]))

(deftest separator-stress-test
  (is (= (lower-hyphen "foo/Bar_baz|spamIsHTMLFunny,or maybe2-Not10")
         "foo-bar-baz-spam-is-html-funny-or-maybe-2-not-10")
      "should separate at expected points"))

(deftest some-examples
  (let [src "helloWorldWhat,is-up_withYou"]
    (are [x y] (= x y)
         (capitalized src) "HelloWorldWhatIsUpWithYou"
         (capitalized-space src) "Hello World What Is Up With You"
         (capitalized-underscore src) "Hello_World_What_Is_Up_With_You"
         (capitalized-hyphen src) "Hello-World-What-Is-Up-With-You"
         (lower-space src) "hello world what is up with you"
         (mixed src) "helloWorldWhatIsUpWithYou"
         (mixed-space src) "hello World What Is Up With You"
         (mixed-underscore src) "hello_World_What_Is_Up_With_You"
         (mixed-hyphen src) "hello-World-What-Is-Up-With-You"
         (upper src) "HELLOWORLDWHATISUPWITHYOU"
         (upper-space src) "HELLO WORLD WHAT IS UP WITH YOU"
         (upper-underscore src) "HELLO_WORLD_WHAT_IS_UP_WITH_YOU"
         (upper-hyphen src) "HELLO-WORLD-WHAT-IS-UP-WITH-YOU"
         (lower src) "helloworldwhatisupwithyou"
         (lower-space src) "hello world what is up with you"
         (lower-underscore src) "hello_world_what_is_up_with_you"
         (lower-hyphen src) "hello-world-what-is-up-with-you")))

(deftest some-examples-with-regex
  (let [src "abc@def@GHijKL"
        re (re-pattern "@")]
    (are [x y] (= x y)
         (lower-space src re) "abc def ghijkl"
         (lower-underscore src re) "abc_def_ghijkl"
         (upper-space src re) "ABC DEF GHIJKL"
         (upper-hyphen src re) "ABC-DEF-GHIJKL")))

(deftest too-many-parameters
  (is (thrown? ArityException (capitalized "foo-bar" #"-" :extra-arg))))

(deftest regex-corner-case-1
  (is (= (lower-hyphen "123ABCdef") "123-ab-cdef")))
