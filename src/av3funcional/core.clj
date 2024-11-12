(ns av3funcional.core
  (:require [clj-http.client :as client]
            [cheshire.core :as json]))

(defn sacar [saldo] 
  (println "Qual o valor do saque?")
  (let [saque (double (read))]
    (swap! saldo - saque)
    (println "Seu saldo atual é:" @saldo)))

(defn depositar [saldo] 
  (println "Qual o valor do depósito?")
  (let [deposito (double (read))]
    (swap! saldo + deposito)
    (println "Seu saldo atual é:" @saldo)))

(defn checarsaldo [saldo] 
  (println "Seu saldo atual é:" @saldo))  

(defn gerenciaconta [saldo] 
  (println " ")
  (println ". . . . . . . . . . .")
  (println " ")
  (println "1. Realizar saque")
  (println "2. Realizar depósito")
  (println "3. Checar saldo")
  (println "4. Voltar")
  (println " ")
  (println ". . . . . . . . . . .")
  (println " ")
  (let [opcao (read)]
    (cond 
      (= opcao 1) (do (sacar saldo) (gerenciaconta saldo))
      (= opcao 2) (do (depositar saldo) (gerenciaconta saldo))
      (= opcao 3) (do (checarsaldo saldo) (gerenciaconta saldo))
      (= opcao 4) nil
      :else (do (println "Opção inválida!") (gerenciaconta saldo)))))

(defn aposta [] 
  (println "Apostando..."))

(defn buscareventos []
  (try
    (let [response (client/get "https://betano.p.rapidapi.com/events"
                               {:headers {"x-rapidapi-key" "a82847654amshdaa6268292881d1p123cbejsndef37e063aad"
                                          "x-rapidapi-host" "betano.p.rapidapi.com"}
                                :query-params {"tournamentId" "325"}})
          data (json/parse-string (:body response) true)]

      (println "Informações do Torneio:")
      (println "Categoria:" (:categoryName data))
      (println "Nome:" (:name data))
      (println "ID do Torneio:" (:tournamentId data))

      (println "\nEventos:")
      (doseq [event (vals (:events data))]
        (println "-------------------")
        (println "Participante 1:" (:participant1 event))
        (println "Participante 2:" (:participant2 event))
        (println "Data:" (:date event))
        (println "Horário:" (:time event))
        (println "Status:" (:eventStatus event))))
    (catch Exception e
      (println "Erro ao acessar a API:" (.getMessage e)))))


(defn resultados [] 
  (println "Resultando..."))

(defn main [saldo] 
  (println " ")
  (println ". . . . . . . . . . .")
  (println " ")
  (println "1. Gerenciar sua conta")
  (println "2. Fazer apostas")
  (println "3. Consultar resultados")
  (println "4. Buscar informações do torneio via API")
  (println "5. Sair")
  (println " ")
  (println ". . . . . . . . . . .")
  (println " ")
  (let [opcao (read)]
    (cond 
      (= opcao 1) (do (gerenciaconta saldo) (main saldo))
      (= opcao 2) (do (aposta) (main saldo))
      (= opcao 3) (do (resultados) (main saldo))
      (= opcao 4) (do (buscareventos) (main saldo))
      (= opcao 5) nil
      :else (do (println "Opção inválida!") (main saldo)))))

(def saldo (atom 0.0))

(main saldo)
