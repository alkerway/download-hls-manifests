(ns download-hls-manifest.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-body]]
            [download-hls-manifest.core :as core]))

(defn manifestRequest [request]
  (core/downloadMaster (get-in request [:body "url"])))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (POST "/dl" [r] manifestRequest)
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
      (wrap-json-body)))
