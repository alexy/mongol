(ns somnium.congomongo
  (:import com.mongodb.Bytes))
  
; defn doesn't work for java methods, non-functions:  
;(defn methornil [x f a] (if a (f x a) x))
;chouser
;(defmacro methornil [x f a] `(if ~a (~f ~x ~a) ~x))
;arohner
(defmacro methornil [x f a] `(let [a# ~a x# ~x] (if a# (~f x# a#) x#)))

(defunk fetch 
  "Fetches objects from a collection.
   Note that MongoDB always adds the _id and _ns
   fields to objects returned from the database.
   Optional arguments include
   :where  -> takes a query map
   :only   -> takes an array of keys to retrieve
   :as     -> what to return, defaults to :clojure, can also be :json or :mongo
   :from   -> argument type, same options as above
   :skip   -> number of records to skip
   :limit  -> number of records to return
   :one?   -> defaults to false, use fetch-one as a shortcut
   :count? -> defaults to false, use fetch-count as a shortcut
   :no-timeout? -> defaults to false, i.e. do timeout"
  {:arglists
   '([collection :where :only :limit :skip :as :from :one? :count? :no-timeout?])}
  [coll :where {} :only [] :as :clojure :from :clojure
   :one? false :count? false :limit 0 :skip 0 :no-timeout? false]
  (let [n-where (coerce where [from :mongo])
        n-only  (coerce-fields only)
        n-col   (get-coll coll)
        n-limit (if limit (- 0 (Math/abs limit)) 0)
        n-limit (int n-limit) ; TODO is it really encessary after ^^?
        skip    (int skip)
        ]
    (cond
      count? (.getCount n-col n-where n-only)
      one?   (when-let [m (.findOne
                         #^DBCollection n-col
                         #^DBObject n-where
                         #^DBObject n-only)]
               (coerce m [:mongo as]))
      :else  (when-let [m (->  #^DBCollection n-col
      						   (.find 
                               #^DBObject n-where
                               #^DBObject n-only)
                               (methornil .skip  skip)
                               (methornil .limit n-limit))]
               (when no-timeout?
               	(.addOption m Bytes/QUERYOPTION_NOTIMEOUT)
               	(.println System/err (str "set no-timeout on " coll)))
               (coerce m [:mongo as] :many :true)))))