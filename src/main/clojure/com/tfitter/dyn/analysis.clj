;; used to be (sort (vec m)) before we switched to our new assoc-in-with,
;; which uses (sorted-map)
;; (defn nondecreasing-map-pairs? [m] (nondecreasing-sparse? (sort (vec m))))

(def ipr (filter-incr-days dpr))
(def ipr-hist (map (partial count-n-days ipr) (drop 3 (range 22))))
;; ipr-hist
;; (2529 821 317 125 66 41 28 14 8 4 1 0 0 0 0 0 0 0 0)

(def inm (filter-incr-days dnm))
(time (def inm-hist (map (partial count-n-days inm) (drop 3 (range 22)))))
;; inm-hist
;; (14016 12093 11221 10737 10478 10317 10149 10003 9902 9787 9702 9620 9553 9493 9423 9367 9292 9165 8868)

(def inr (filter-incr-days dnr))
(def inr-hist (map (partial count-n-days inr) (drop 3 (range 22))))
;; inr-hist
;; (65246 58310 54425 52025 50443 49145 47916 47016 46210 45475 44630 44031 43584 43078 42545 42133 41665 40853 39518)

(def pipr10 (keys ipr10))

(defn check-other-increasing [peeps omap]
  (->> peeps (map (fn [k] [k (omap k)])) (map (fn [[k v]] [k (increasing-map-pairs? v)]))))

(check-other-increasing pipr10 dnm)  
;; (["_sweb" true] ["hiroto_bot" true] ["bexbb9" true] ["bubbleyuuum" true] ["pikachu_bot" true] ["luulybieber" false] ["nao_yukishima" true] ["mechanicalgirly" true] ["tanabe1969" true] ["butlerbeliebers" true] ["oclebermachado" true] ["oficialfollow" true] ["simpsoncody" true] ["programamarcia" true])
;; -- only luulybieber is false!

(check-other-increasing pipr10 dnr)  
;; (["_sweb" true] ["hiroto_bot" true] ["bexbb9" true] ["bubbleyuuum" true] ["pikachu_bot" true] ["luulybieber" true] ["nao_yukishima" true] ["mechanicalgirly" true] ["tanabe1969" false] ["butlerbeliebers" true] ["oclebermachado" true] ["oficialfollow" true] ["simpsoncody" true] ["programamarcia" false])
;; -- only [tanabe1969 programamarcia] are false!

(check-other-increasing pipr10 dnt)  
;; (["_sweb" true] ["hiroto_bot" true] ["bexbb9" true] ["bubbleyuuum" true] ["pikachu_bot" true] ["luulybieber" true] ["nao_yukishima" true] ["mechanicalgirly" true] ["tanabe1969" false] ["butlerbeliebers" true] ["oclebermachado" true] ["oficialfollow" true] ["simpsoncody" true] ["programamarcia" false])
;; -- only programamarcia is false!

;; TODO IDEAS
;; acceleration, end/start, min 3-10 days
;; replier graph annotated by the pagerank of people, at-the-moment and final!

(def cnm (map (fn [[k v]] [k (max-clis-len (vals v))]) dnm))
(count cnm)

(def anm (->> dnm (map (fn [[k v]] [k (maxxel (vals v))])) (filter (fn [[_ s]] (seq s))) doall))
(count anm)

(def sanm (sort-by (fn [[_ [x y]]] [(- x) (- y)]) anm))

(def tanm (->> dnm (map (fn [[k v]] [k (maxxel (vals v) 3 :tough)])) (filter (fn [[_ s]] (seq s)))))
