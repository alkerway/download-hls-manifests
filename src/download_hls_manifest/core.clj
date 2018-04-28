(ns download-hls-manifest.core
  (:require [compojure.core :refer :all]))

(defn downloadMaster [url]
  (println url)
  {:status 200
   :body "aaayy"})
  
