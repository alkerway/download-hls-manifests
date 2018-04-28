(ns download-hls-manifest.core
  (:require [clj-http.client :as client]
            [download-hls-manifest.parser :as parser]
            [download-hls-manifest.config :as config]
            [clojure.java.io :as io]))

(defn downloadMaster [url]
  (let [master (:body (client/get url))]
    (println (parser/getStreamUrls master))
    (with-open [wrtr (io/writer (str config/basePath "/master.m3u8"))]
      (.write wrtr master))
    {:status 200
     :body master}))
  
