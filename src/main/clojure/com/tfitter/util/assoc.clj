(defn assoc-in-with
  "supply a default-map"
  [m default-map [k & ks] v]
  (if ks
    (assoc m k (assoc-in-with (get m k default-map) default-map ks v))
    (assoc m k v)))

(defn assoc-in-with!
  "supply a default-map"
  [m default-map [k & ks] v]
  (if ks
    (assoc! m k (assoc-in-with! (get m k default-map) default-map ks v))
    (assoc! m k v)))
