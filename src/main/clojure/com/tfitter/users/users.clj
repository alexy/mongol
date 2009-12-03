; (time (reduce (fn [count _] (inc count)) 0 (fetch :hose)))

(ns tfitter (:use somnium.congomongo))
(mongo! :db "twitter")

(def twits '[{:id 1, :user_id 10} {:id 2, :user_id 20} {:id 3, :user_id 10}])

(defn scan-users [users twit]
  (update-in users [(long (:user_id twit))] (comp inc #(or % 0))))

(reduce scan-users {} twits) ; test
(time (def users (reduce scan-users {} (fetch :hose))))