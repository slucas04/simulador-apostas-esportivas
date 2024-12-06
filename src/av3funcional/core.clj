(ns av3funcional.core
  (:require [clj-http.client :as client]
            [cheshire.core :as json]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [ring.middleware.cors :refer [wrap-cors]]
            [compojure.core :refer [defroutes POST GET PUT]]
            [clojure.string :as str]
            [environ.core :refer [env]]))

(def saldo (atom 0.0))
(def apostas (atom []))
(def api-key (env :api-key))

(defn buscar-eventos [torneio-id]
  (let [url "https://betano.p.rapidapi.com/events"
        headers {:x-rapidapi-key api-key
                 :x-rapidapi-host "betano.p.rapidapi.com"}
        params {:tournamentId torneio-id}]
    (try
      (let [response (client/get url {:headers headers :query-params params})
            data (json/parse-string (:body response) true)]
        data)
      (catch Exception e
        (println "Erro ao acessar a API de eventos:" (.getMessage e))
        nil))))

(defn buscar-odds [evento-id]
  (let [url "https://betano.p.rapidapi.com/odds_betano"
        headers {:x-rapidapi-key api-key
                 :x-rapidapi-host "betano.p.rapidapi.com"}
        params {:eventId evento-id
                :oddsFormat "decimal"
                :raw "false"}]
    (try
      (let [response (client/get url {:headers headers :query-params params})]
        (println "Resposta completa:" response)
        (json/parse-string (:body response) true))
      (catch Exception e
        (println "Erro ao acessar a API de odds:" (.getMessage e))
        nil))))

(defroutes app-routes
  (GET "/saldo" []
       (json/generate-string {:saldo @saldo}))

(POST "/depositar" {body :body}
  (try
    (println "Mapa completo recebido no /depositar:" body)
    (println "Chaves disponíveis no body (com detalhes):" (map #(str %) (keys body)))


    (let [valor (try
                  (let [v (or (:valor body)
                              (get body (keyword "valor"))
                              (get body (keyword "namespace/valor")))] 
                    (println "Valor extraído do mapa:" v "Tipo do valor:" (type v))
                    (Double/parseDouble (str v))) 
                  (catch Exception e
                    (println "Erro ao converter valor para número:" (.getMessage e))
                    nil))]
      (println "Valor processado para depósito:" valor)
      (if (and valor (> valor 0))
        (do
          (swap! saldo + valor)
          (println "Depósito realizado com sucesso. Novo saldo:" @saldo)
          (json/generate-string {:mensagem "Depósito realizado com sucesso" :saldo @saldo}))
        (json/generate-string {:erro "Valor inválido para depósito"})))
    (catch Exception e
      (println "Erro ao processar o corpo da requisição:" (.getMessage e))
      (json/generate-string {:erro "Erro ao processar requisição"}))))






  (PUT "/sacar" {body :body}
       (let [valor (try
                     (Double/parseDouble (str (:valor body)))
                     (catch Exception _ nil))]
         (if (and valor (pos? valor) (>= @saldo valor))
           (do
             (swap! saldo - valor)
             (json/generate-string {:mensagem "Saque realizado com sucesso" :saldo @saldo}))
           (json/generate-string {:erro "Saldo insuficiente ou valor inválido para saque"}))))


(POST "/apostar" {body :body}
      (println "Body recebido em /apostar:" body)
      (let [evento-id (or (:evento-id body) (:eventoId body)) 
            mercado-id (or (:mercado-id body) (:mercadoId body))
            resultado (or (:resultado body) (get body "resultado"))
            valor (try
                    (Double/parseDouble (str (or (:valor body) (get body "valor"))))
                    (catch Exception _ nil))]
        (println "Dados processados: evento-id:" evento-id ", mercado-id:" mercado-id ", resultado:" resultado ", valor:" valor)
        (if (and evento-id mercado-id resultado (number? valor) (> valor 0) (>= @saldo valor))
          (do
            (swap! saldo - valor)
            (swap! apostas conj {:evento-id evento-id
                                 :mercado-id mercado-id
                                 :resultado resultado
                                 :valor valor
                                 :saldo-restante @saldo})
            (println "Aposta realizada com sucesso. Novo saldo:" @saldo)
            (json/generate-string {:mensagem "Aposta realizada com sucesso" :saldo @saldo}))
          (do
            (println "Erro na validação dos dados da aposta.")
            (json/generate-string {:erro "Saldo insuficiente, valor inválido ou dados incompletos"})))))



  (GET "/eventos/:torneio-id" [torneio-id]
       (let [data (buscar-eventos torneio-id)]
         (if data
           (json/generate-string data)
           (json/generate-string {:erro "Erro ao buscar eventos"}))))


  (GET "/odds/:evento-id" [evento-id]
       (let [odds (buscar-odds evento-id)]
         (if odds
           (json/generate-string odds)
           (json/generate-string {:erro "Erro ao buscar odds"}))))


  (GET "/consultar-apostas" []
       (json/generate-string {:apostas @apostas})))

(def app
  (-> app-routes
      (wrap-json-body {:keywords? true})
      wrap-json-response
      (wrap-cors :access-control-allow-origin [#".*"]
                 :access-control-allow-methods [:get :put :post :options]
                 :access-control-allow-headers ["Content-Type"])))




(defn -main []
  (jetty/run-jetty app {:port 8081 :join? false}))
