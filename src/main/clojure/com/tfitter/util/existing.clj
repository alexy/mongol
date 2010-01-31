;; these defns are copied from contrib when it didn't load into repl

;; str-utils2

(defn grep
  "Filters elements of coll by a regular expression.  The String
  representation (with str) of each element is tested with re-find."
  [re coll]
  (filter (fn [x] (re-find re (str x))) coll))
  
