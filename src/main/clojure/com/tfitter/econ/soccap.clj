(defn merge-keys [ma mb & [sorted?]] 
 	"merge just the keys of two maps, deduped"
 	(let [all-keys (mapcat keys [ma mb])]
 	(if sorted?
 		(apply sorted-set all-keys)
 		(set all-keys))))

(defn balance [dreps dments]
  "for every user, get his froms and tos, and compute daily balances for them 
   -- who said more than he heard back"
  (let [ ; progress (or progress 10000)
  users (merge-keys dreps dments)]
 	(->> users (pmap (fn [user] (let [
      reps  (get dreps  user {})
      ments (get dments user {})
      days  (merge-keys reps ments :sorted)
      dummy-day (- 1)
      [day curs stats]   
		  (->> days (reduce (fn [[day* curs* stats*] day]
			(let [
			  days-of-our-lives (map #(assoc %1 day* %2) stats* curs*)
			  [ins* outs* tot* bal*] curs*
			  dr   (reps day) ; or do (get-in [user day]) instead of binding reps, same w/ments
			  dm   (ments day)
			  _	   (assert (or dr dm))
			  dm-  (when dm (->> dm (map (fn [[k v]] [k (- v)])) (into {})))
			  ins  (merge-with + ins*  dr)
			  outs (merge-with + outs* dm)
			  tot  (merge-with + tot*  dr dm)
			  bal  (merge-with + bal*  dr dm-)
			  ]
			  [day [ins outs tot bal] days-of-our-lives]
			  )) [dummy-day (repeat 4 {}) (repeat 4 (sorted-map))]))
     stats (map #(-> %1 (assoc day %2) (dissoc dummy-day))
     			  		stats curs)]
     [user stats])))
     (into {}))))
     
     
(defn socstats [dreps dments f-curs]
  (let [
  users (merge-keys dreps dments)]
  (println "merged keys")
 	(->> users (map (fn [user] (let [
      reps  (get dreps  user {})
      ments (get dments user {})
      days  (merge-keys reps ments :sorted)
      dummy-day (- 1)
      [day curs stats]   
		  (->> days (reduce (fn [[day* curs* stats*] day]
			(let [
			  stats (assoc stats* day* curs*)
			  curs  (f-curs reps ments day curs*)
			  ]
			  [day curs stats]
			  )) [dummy-day {} (sorted-map)]))
     stats (-> stats (assoc day curs) (dissoc dummy-day))]
     [user stats])))
     (into {})
     )))
     
  
 (defn f-balance [reps ments day curs*]
 	(let [
		  dr   (reps day)
		  dm   (ments day)
		  _	   (assert (or dr dm))
		  dm-  (when dm (->> dm (map (fn [[k v]] [k (- v)])) (into {})))
 		]
 		(merge-with + curs* dr dm-)))

 (defn f-total [reps ments day curs*]
 	(let [
		  dr   (reps day)
		  dm   (ments day)
		  _	   (assert (or dr dm))
 		]
 		(merge-with + curs* dr dm)))

 (defn f-ins [reps ments day curs*]
 	(let [
		  dm   (ments day)
 		]
 		(merge-with + curs* dm)))

 (defn f-outs [reps ments day curs*]
 	(let [
		  dr  (reps day)
 		]
 		(merge-with + curs* dr)))
