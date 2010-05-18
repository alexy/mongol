(def get-in-or data keys default
  "should be in core as (get-in data keys :default default)"
  (or (get-in data keys) default))  

(defstruct s-graph :dreps :dments :dcaps :ustats)
(defstruct user-stats :soc :day :ins :outs :tot :bal)  

(def soc-run [dreps dments days sc-init alpha beta gamma]
  (let [params [alpha beta gamma]
    dcaps {}
    ;; TODO we have to initialize each users as he appears with 1
    ustats {}
    sgraph (struct s-graph dreps dments dcaps ustats)
    ]))
  
  
(def soc-day [sgraph [alpha beta gamma :as params] day]
  (let [
    ;; TODO have sgraph as 
    ustats (:ustats sgraph)
    ;; TODO does it make sense to carry users separately from ustats,
    ;; perhaps with the first day they appear in our data?
    users  (map first ustats)
    
    prestats (map (partial soc-user-day-sum sgraph params day) users)
    prenorms (map first prestats)
    
    [out-norm ins-norm-back ins-norm-all :as norms] (reduce (fn [sums terms]
             (map + sums terms)) prenorms)
             
    ustats (->> ustats (map (fn [numers [user {:keys [soc] :as stats}]]
      (let [[outs* ins-back* ins-all*] (map / numers norms)
        soc (+ (* alpha soc) (+ (* beta outs*) 
          (* (- 1. beta) (+ (* gamma ins-back*) (* (- 1. gamma) ins-all*)))))
        stats (assoc stats :soc soc)
        ]
        [user stats])) prenorms) (into {}))
        
    ;; day in fn is the sam day as soc-day param day
    dcaps (->> ustats (reduce (fn [res [user {:keys [day soc]}]] 
      (assoc! res user (assoc (or (res user) (sorted-map)) day soc))) 
      (transient dcaps)) persistent!) 
    ]
    (assoc sgraph :ustats ustats :dcaps dcaps)))


(defn get-soccap [ustats user]
  (let [stats (ustats user)]
  (if stats (:soc stats) 0.)))
  
(def soc-user-day-sum [sgraph day user]
  "NB acc is a list, conj prepends to it"
  (let [
    {:keys [dreps dments ustats]} sgraph
    stats (ustats user)
    reps  (get-in dreps [user day])
    ments (get-in dments [user day])
    ]
    (if (not (or reps ments))
      [nil stats]
      ;; we had edges this cycle -- now let's dance and compute the change!
      (let [
        reps  (or reps {})
        ments (or ments {})
        
        {:keys [day ins outs tot bal]} stats
        
        dr   (reps day) ; or do (get-in [user day]) instead of binding reps, same w/ments
			  dm   (ments day)
			  _	   (assert (or dr dm))
        
        ;; find all those who talked to us in the past to whom we replied now
        out-sum (if dr (->> dr (map (fn [[to num]]
          (let [to-bal (get bal to 0)] (if (>= to-bal 0) 0.
            (let [to-soc (get-soccap ustats to)] (if (zero? to-soc) 0.
            ;; TODO if previous tot is 0, we'll zero the multiplication
            ;; should we update tot first, or assume 1 for multiplier as below?
            ;; guess for repayment default will never be needed, can assert that
            (let [to-tot (get tot to 1.)]
              (* num to-bal to-tot to-soc)))))))) sum) 0.)
              
        in-sum-back (if dm (->> dm (map (fn [[from num]]
          (let [from-bal (get bal from 0)] (if (<= from-bal 0) 0.
            (let [from-soc (get-soccap ustats from)] (if (zero? from-soc) 0.
            (let [from-tot (get tot from 1.)]
              (* num from-bal from-tot from-soc)))))))) sum) 0.)

        in-sum-all (if dm (->> dm (map (fn [[from num]] 
            (let [from-soc (get-soccap ustats from)] (if (zero? from-soc) 0.
            (let [from-tot (get tot from 1.)]
                (* num from-tot from-soc)))))) sum) 0.)
        
        normalize [out-sum in-sum-back in-sum-all]
      
      	dm-  (when dm (->> dm (map (fn [[k v]] [k (- v)])) (into {})))
      	
			  ins  (if dr (merge-with + ins  dr) ins)
			  outs (if dm (merge-with + outs dm) outs)
			  tot  (merge-with + tot dr dm)
			  bal  (merge-with + bal dr dm-)
			  
			  stats (struct user-stats soc day ins outs tot bal)
			  ]

      [normalize stats]))))