(defn invert-graph-braver [reps]
(reduce 
  (fn [inv [from reps]] 
    (reduce (fn [inv [to dates]] (update-in inv [to from] #(into (or % []) dates))) inv reps)) 
    {} reps))

;; I invert the map!
(reduce (fn [inv [from reps]] 
  (reduce (fn [inv [to dates]] 
    (update-in inv [to from] #(into (or % []) dates))) inv reps)) 
    {} {"a" {:b [1 2 3],:c [4 5]}, "d" {:b [2 3], :e [5 6 7]}})


;; chouser inverts the map!
(let [g {"a" {:b [1 2 3],:c [4 5]}, "d" {:b [2 3], :e [5 6 7]}}] 
  (reduce (fn [m [k2 k1 v]] (assoc m k2 (assoc (m k2 {}) k1 v))) 
    {} (for [[k1 m] g [k2 v] m] [k2 k1 v])))

;; transient at top level:
(persistent! (let [g {"a" {:b [1 2 3],:c [4 5]}, "d" {:b [2 3], :e [5 6 7]}}] 
  (reduce (fn [m [k2 k1 v]] (assoc! m k2 (assoc (m k2 {}) k1 v))) 
    (transient {}) (for [[k1 m] g [k2 v] m] [k2 k1 v]))))

;; chouser -- abysmal timing
;; (time (def sper-chouser
;;   (->>
;;     (for [[k1 m] reps [k2 v] m] [k2 k1 v]) ; stream of edges
;;     (reduce (fn [m [k2 k1 v]]            ; invert into transient of transients
;;               (assoc! m k2 (assoc! (m k2 (transient {})) k1 v)))
;;             (transient {}))
;;     persistent!                          ; persistent of transients
;;     (reduce (fn [m [k v]]                ; reduce into transient of persistents
;;               (assoc! m k (persistent! v)))
;;             (transient {}))
;;     persistent!))                        ; persistent of persistents
;; )

;; JonSmoth inverts the map!
;; http://paste.lisp.org/display/92238
(let [old-map {"a" {:b [1 2 3], :c [4 5]}, "d" {:a [2 3], :e [5 6 7]}}
      outer-keys (keys old-map)
      new-map (doall (reduce conj (map (fn [outer-key] 
					 (let [inner-map (old-map outer-key)
					       inner-keys (keys inner-map)
					       inner-values (vals inner-map)]
					   (reduce conj 
						   (map 
						    (fn [ik ok v] {ik {ok v}})
						    inner-keys
						    (repeat outer-key)
						    inner-values)))) outer-keys)))]
  new-map)
  
;; parallel:
;; http://paste.lisp.org/display/92238#1
;; 87 secs
(defn invert-graph-jonsmith  ;  BUGGY!
  "givern a graph, {\"from\" {:to1 [t1 t2 t3], :to2 [t4 t5]...}}, invert as
   {:to1 {\"from1\" [t1 2 3], \"from2\"}}"
   [old-map]
   (let [outer-keys (keys old-map)
      new-map (doall (reduce conj (pmap (fn [outer-key] 
					 (let [inner-map (old-map outer-key)
					       inner-keys (keys inner-map)
					       inner-values (vals inner-map)]
					   (reduce conj 
               (map  ; pmap yielded abysmal 600 secs 
						    (fn [ik ok v] {ik {ok v}})
						    inner-keys
						    (repeat outer-key)
						    inner-values)))) outer-keys)))]
  new-map))
