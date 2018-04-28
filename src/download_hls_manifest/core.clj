(ns download-hls-manifest.core
  (:require [clj-http.client :as client]
            [download-hls-manifest.parser :as parser]
            [download-hls-manifest.config :as config]
            [clojure.string :as cljstr]
            [clojure.java.io :as io]))

(defn saveRemoteToFile [remoteUrl filePath]
  (let [body (:body (client/get remoteUrl))]
    (with-open [wrtr (io/writer (str config/basePath "/" filePath))]
      (.write wrtr body))
    body))
  
  
(defn getFrags [levelUrl masterUrl]
  (let [filePath (parser/getLocalPath levelUrl masterUrl)
        remotePath (parser/getRemotePath levelUrl masterUrl)
        fragUrls (parser/getFragUrls (saveRemoteToFile remotePath filePath))]
    (println (count fragUrls))
    ))


(defn downloadMaster [masterUrl]
  (let [master (saveRemoteToFile masterUrl "master.m3u8")
        levelUrls (parser/getStreamUrls master)]
    (loop [levels levelUrls]
      (let [eachUrl (first levels)]
        (if eachUrl (do (getFrags eachUrl masterUrl)
          (recur (rest levels))))))
    {:status 200
     :body master}))
