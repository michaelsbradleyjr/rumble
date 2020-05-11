(ns rumble.util
  (:import [java.nio.file Paths])
  (:require [clojure.core.async :as async :refer [<! go thread]]
            [clojure.java.io :as io]))

(defn asyncify
  ([f] (asyncify f 8))
  ([f n]
   (if (> n 7)
     (fn [& args] (thread (apply f args)))
     (case n
       0 (fn [] (thread (f)))
       1 (fn [a] (thread (f a)))
       2 (fn [a b] (thread (f a b)))
       3 (fn [a b c] (thread (f a b c)))
       4 (fn [a b c d] (thread (f a b c d)))
       5 (fn [a b c d e] (thread (f a b c d e)))
       6 (fn [a b c d e f] (thread (f a b c d e f)))
       7 (fn [a b c d e f g] (thread (f a b c d e f g)))))))

;; maybe can use try/catch and impl error-first Node.js style callbacks?
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
