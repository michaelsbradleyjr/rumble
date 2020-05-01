(ns rumble.util
  (:import [java.nio.file Paths])
  (:require [clojure.core.async :as async :refer [<! go thread]]
            [clojure.java.io :as io]))

(defn asyncify [f]
  (fn [& args]
    (thread (apply f args))))

;; maybe use try/catch and impl error-first Node.js style callbacks?
(defn callbackify [<f]
  (fn [& args]
    (let [callback (last args)
          chan (apply <f (butlast args))]
      (go (callback (<! chan))))
    nil))

(defn delete-recursively [fname]
  (when (.exists (io/file fname))
    (let [func (fn [func f]
                 (when (.isDirectory f)
                   (doseq [f2 (.listFiles f)]
                     (func func f2)))
                 (io/delete-file f))]
      (func func (io/file fname)))))

(defn path-join [p & ps]
  (str (.normalize (Paths/get p (into-array String ps)))))

;; more general purpose: https://github.com/funcool/promesa
;; but not sure if/how it can be used with core.async and clojure.core/promise

;; maybe use try/catch and deliver the error if there is one?
(defn promisify [<f]
  (fn [& args]
    (let [p (promise)]
      (go (let [chan (async/take 1 (apply <f args))]
            (deliver p (<! chan))))
      p)))
