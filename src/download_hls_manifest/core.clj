(ns download-hls-manifest.core
  (:require [clj-http.client :as client]
            [download-hls-manifest.parser :as parser]))

(defn downloadMaster [url]
  (let [master (:body (client/get url))]
    (println (parser/getStreamUrls master))
    {:status 200
     :body master}))
  
