(ns tfitter (:use somnium.congomongo)
  (:use [clojure.contrib.seq-utils :only [partition-all]]))
  
(mongo! :db "twitter")

(def twits '[{:id 1, :user_id 10} {:id 2, :user_id 20} {:id 3, :user_id 10}])

(defn scan-users [[users counter] twit]
  (let [grane 1000000]
  (if (= (mod counter grane) 0) (.print System/err (format " %d" (quot counter grane)))))
  [(assoc! users (:screen_name twit) (inc (users (:screen_name twit) 0))) (inc counter)])

(time (def users (persistent! ((reduce scan-users [(transient {}) 0] (fetch :hose :only [:screen_name])) 0))))
(.println System/err)

;; we used to keep uid instead of a user
;; (def iusers (map (fn [[a b]] [(int a) b]) users))

(time 
  (doseq [x (partition-all 10000 
    (map (fn [[uid num]] { :user uid :numtwits num }) users))] 
    (.print System/err ".")
    (mass-insert! :usernumtwits x)))
(.println System/err)
