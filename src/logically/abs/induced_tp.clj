(ns logically.abs.induced_tp
  (:refer-clojure :exclude [==])
  (:use [clojure.core.logic :exclude [is] :as l]
        [clojure.core.logic.nominal :exclude [fresh hash] :as nom])
  (:use [logically.abs.db] :reload)
  (:use [logically.abs.lub] :reload))

(defn prove [db goals]
  (conde
   [(fresh [b bs]
           (conso b bs goals)
           (conda
            [(all (set-union db [:call b]) fail)]
            [(all (db-get-fact db [:ans b])
                  (prove db bs))]))]
   [(== goals ())]))

(defn operatoro [db c]
  (fresh [head body head]
         (c head body)
         (db-get-fact db [:call head])
         (prove db body)
         (set-union db [:ans head])))

(defn iterateo [db c]
  (conda
   [(all (operatoro db c) fail)]
   [(all (db-retract-fact! db :flag)) (iterateo db c)]
   [succeed]))

(defn go [c g q]
  (let [db (db-new)]
    (all
     (db-add-fact! db [:call g])
     (iterateo db c)
     (db-get-fact db q))))
