(defn clis-drank
  "segment drank graph into clises by ranks"
  [drank & args]
  (let [mapargs (apply hash-map args)
    ;; TODO devise a better syntax for passung subparameters, e.g.
    ;; :growfall [:twice-higher :twice-wider]
    {:keys [roll? pair? clis nosort? minsublen invert?
      twice-wider? twice-higher?] 
     :or {clis clis-decr minsublen 3}} mapargs
   ;; (print roll? pair? clis tough? nosort?)
      maxxel?   (= roll? :maxxel)
      growfall? (= roll? :growfall)
      valfn 
        (if pair? 
          (if (= pair? :first) 
            (fn [[_ [x y]]] x) 
            (fn [[_ [x y]]] y))
          (fn [[_ x]] x))
      clens (->> drank (map (fn [[user days]] 
      (let [ranks (map valfn days)
            res 
            ;; TODO factor out (clis ranks)
            (cond 
            (not roll?)
              (do
                (println ranks)
                (clis ranks))
            (= roll? :ratio)
                (if (and (seq ranks) (> (first ranks) 0))
                (double (/ (last ranks) (first ranks))) 0.0)
            (= roll? :maxlen)
                (->> (clis ranks) (map count) (apply max))
            maxxel?
                (maxxel ranks clis minsublen invert?)
            growfall?
                (growfall ranks twice-wider? twice-higher?)
            )]
            [user res]))))
      clens (cond
        maxxel? 
          (filter second clens)
        ;; growfall used to return nil for indeterminate, now 0.0,
        ;; so it always returns numbers meaningfully sortable:
        ;; positive is growth, negative decline, zero hard to tell
        ;; growfall? (remove (comp nil? second) clens) 
        :else
          clens)
      ]
      (if (or (not roll?) nosort?) 
        clens
        (if maxxel? 
          (sort-by (fn [[_ [x y]]] 
            ;; (let [x (or x Integer/MIN_VALUE) y (or y Integer/MIN_VALUE)])
              [(- x) (- y)]) clens)
        (sort-by second > clens)))
        ))
