(ns somnium.congomongo
  (:import com.mongodb.Bytes))

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
        n-limit (if limit (- 0 (Math/abs limit)) 0)]
    (cond
      count? (.getCount n-col n-where n-only)
      one?   (when-let [m (.findOne
                         #^DBCollection n-col
                         #^DBObject n-where
                         #^DBObject n-only)]
               (coerce m [:mongo as]))
      :else  (when-let [m (.find #^DBCollection n-col
                               #^DBObject n-where
                               #^DBObject n-only
                               (int skip)
                               (int n-limit))]
               (when no-timeout? (.addOption m Bytes/QUERYOPTION_NOTIMEOUT))
               (coerce m [:mongo as] :many :true)))))