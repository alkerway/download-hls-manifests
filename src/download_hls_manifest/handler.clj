(ns download-hls-manifest.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-body]]
            [download-hls-manifest.core :as core]))

(defn manifestRequest [request]
  (try (do (core/getHls (get-in request [:body "url"])) {:status 200 :body "success"})
       (catch Exception e {:status 400 :body (str "error: " (.getMessage e))})))

(defroutes app-routes
  (POST "/dl" [req] manifestRequest)
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
      (wrap-json-body)))
