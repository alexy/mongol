(ns mongol.repliers)

(defn dynpr
  "compute daily numtwits, replies, mentions, pagerank"
  [map-params]
  (let [
    {mongo-names :names 
     :keys [quant limit-days skip-days]
     :or {mongo-names {} quant 10000}} map-params
        
    numtwits {}
    replies  {}
    mentions {}
    
    graph    (DirectedSparseGraph.)
    
    {:keys [twits-mongo] :or {twits-mongo :hose}} mongo-names
    twits    (get-twits twits-mongo)
    progress 0
    zero-daynum (daynum-of-raw-twit (fetch-one twits-mongo))
    prev-day 0
    ]
    (letfn [
      (dynpr-do-twits [numtwits replies mentions graph progress prev-day [twit & twits]]
        ;; internal auxiliary fn to walk the twits for daily stats.
        ;; couldn't do reduce as we need early termination in case of limit-days,
        ;;  and also we need to dump the final day upon exhaustion    
        (let [
          {:keys [screen_name in_reply_to_screen_name created_at]} twit
          ;; _ (.println System/err (str "here, screen_name=>" screen_name))
          user (.toLowerCase screen_name)
          to   (when in_reply_to_screen_name (.toLowerCase in_reply_to_screen_name))
          at   (joda-at created_at)
          progress (inc progress)
          daynum   (daynum-of-dt at)
          day      (- daynum zero-daynum)

          ]
      
        (when (and (< prev-day day) (> day skip-days))
          (day-change mongo-names numtwits replies mentions graph progress prev-day))

        (when (or (not limit-days) (< day limit-days))
          (let [
            numtwits  (update-numtwits numtwits user)
            replies   (update-replies  replies  user to at)
            mentions  (update-mentions mentions user to at)
            graph     (update-graph    graph    user to)
            ]
    
            (when (= (mod progress quant) 0) (.print System/err (str " "(quot progress quant))))
    
            (if (seq twits) (recur numtwits replies mentions graph progress day twits)
                  (day-change mongo-names numtwits replies mentions graph progress day))))))]
        (dynpr-do-twits numtwits replies mentions graph progress prev-day twits))))