(import [org.joda.time DateTime])
(import [org.joda.time.format DateTimeFormat])
(def dtFmt (DateTimeFormat/forPattern "YYYY-MM-dd HH:mm:ss"))
(def t "2009-06-15 04:01:00")
(.parseDateTime dtFmt t)
;; (. dtFmt parseDateTime "2009-06-15 04:01:00")
;; (-> dtFmt (.parseDateTime "2009-06-15 04:01:00"))
;; turns out you don't have to do full format-parse:
(require '[clojure.contrib.str-utils2 :as s2])
(DateTime. (str (s2/replace t " " "T") "Z"))

; measuring how fast I get the collection back
(time (reduce (fn [n _] (let [n (inc n)] (when (= 0 (mod n 100000)) (print ".")) n)) 0 (take 1000000 (fetch :reps))))

(System/getProperty "java.class.path")
(add-claspath "file:///...")

(format "There's %s bottles of beer on the wall..." (clojure.contrib.str-utils2/join " " (range 100 90 -1)))
(format "There's %s bottles of beer on the wall..." (reduce #(str %1 " " %2) (range 100 90 -1)))

(ns-unmap *ns* '<<)
(refer 'commons.clojure.strint) 


(<< "hello ~(<< \"nested ~(str \\\"cemerick\\\")\")!")

(->> {:a {:b [1 2 3] :c [5 6]} :b {:c [7] :d [9 10]}} (vals) (map vals) (map count) (sort) (reverse))
(->> reps (vals) (map vals) (map count) (sort) (reverse) (take 10))

(reduce (fn [r0 [from repliers]] (concat r0 (reduce (fn [r1 [to dates]] (conj r1 [[from to] dates])) [] repliers))) [] tree)

(reduce (fn [r0 [from repliers]] (concat r0 (reduce (fn [r1 [to dates]] (conj r1 [[from to] dates])) [] repliers))) [] {:a {:b [1 2 3], :c [5 6]}, :b {:c [7], :d [9 10]}}) 
;;  => ([[:a :b] [1 2 3]] [[:a :c] [5 6]] [[:b :c] [7]] [[:b :d] [9 10]])

;; from tomoj 
(defn alex [m] (apply concat (for [[k1 v1] m] (for [[k2 v2] v1] [[k1 k2] v2]))))

(doseq [x '({:user "a" :reps {:a 1 :b 2}})] (let [{ a :user rs :reps} x] (doseq [to (keys rs)] (print to))))
(doseq [x (take 5 reps)] (let [{ from :user rs :reps} x] (doseq [to (keys rs)] (println [from (name to)]))))

;; build graph from mongo reps, form in-memory would require [k v] instead of {from :user rs :reps}:
(doseq [x reps] (let [{from :user rs :reps} x] (doseq [to (keys rs)] (let [to (name to)] 
  (.addEdge g (str from " " to) from to)))))
 