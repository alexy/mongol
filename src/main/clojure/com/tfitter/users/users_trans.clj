(ns tfitter (:use somnium.congomongo))
(mongo! :db "twitter")

(def twits '[{:id 1, :user_id 10} {:id 2, :user_id 20} {:id 3, :user_id 10}])

(defn scan-users [[users counter] twit]
  (let [grane 1000000]
  (if (= (mod counter grane) 0) (.print System/err (format " %d" (quot counter grane)))))
  [(assoc! users (:user_id twit) (inc (users (:user_id twit) 0))) (inc counter)])

(time (def users (persistent! ((reduce scan-users [(transient {}) 0] (fetch :hose :only [:user_id])) 0))))
(.println System/err)