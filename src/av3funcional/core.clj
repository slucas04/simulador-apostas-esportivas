(ns av3funcional.core
  (:require [clj-http.client :as client]
            [cheshire.core :as json]))

(defn sacar [saldo] (do 
  (println "Qual o valor do saque?")
  (def saque (double (read)))
  (if (>= @saldo saque) (do 
    (swap! saldo - saque)
    (println "Seu saldo atual e:" @saldo)
  ) (println "Sua conta nao possui esse valor! Seu saldo atual e:" @saldo))
  
))

(defn depositar [saldo] (do 
  (println "Qual o valor do deposito?")
  (def deposito (double (read)))
  (swap! saldo + deposito)
  (println "Seu saldo atual e:" @saldo)
))

(defn checarsaldo [saldo] 
  (println "Seu saldo atual e:" @saldo))  

(defn gerenciaconta [saldo] 
  (println " ")
  (println ". . . . . . . . . . .")
  (println " ")
  (println "1. Realizar saque")
  (println "2. Realizar deposito")
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
      :else (do (println "Opcao invalida!") (gerenciaconta saldo)))))

(defn aposta [] 
  (println "Apostando..."))

(defn buscareventos []
  (try
    (let [response (client/get "https://betano.p.rapidapi.com/events" {:headers {:x-rapidapi-key "a82847654amshdaa6268292881d1p123cbejsndef37e063aad"
                                                              :x-rapidapi-host "betano.p.rapidapi.com"}
                                                    :query-params {:tournamentId "325"}})
          data (json/parse-string (:body response) true)]

      (println "Informacoes do Torneio:")
      (println "Categoria:" (:categoryName data))
      (println "Nome:" (:name data))
      (println "ID do Torneio:" (:tournamentId data))

      (println "\nEventos:")
      (doseq [event (vals (:events data))]
        (println "-------------------")
        (println "Participante 1:" (:participant1 event))
        (println "Participante 2:" (:participant2 event))
        (println "Data:" (:date event))
        (println "Horario:" (:time event))
        (println "Status:" (:eventStatus event))
        (println "ID do Evento:" (:eventId event))))
    (catch Exception e
      (println "Erro ao acessar a API:" (.getMessage e)))))

"COISAS A FAZER: colocar outro torneio de outro esporte. cada torneio precisa de dois mercados. o usuário escolhe o torneio, vê os eventos em pré-jogo (os únicos que podem ser apostados) e escolhe o evento, depois vê os dois mercados e suas respectivas odds e escolhe em qual vai apostar. a aposta dele fica salva num arquivo de texto para ser lido quando o jogo acabar e ver se ele ganhou ou perdeu. quando você for consultar resultados, o programa vai ler as apostas, ver se o jogo acabou e checar quanto você ganhou de saldo ao saldo antigo (de quando apostou)"


(defn buscarodds []
  (try
    (let [response (client/get "https://betano.p.rapidapi.com/odds_betano" {:headers {:x-rapidapi-key "a82847654amshdaa6268292881d1p123cbejsndef37e063aad"
                                                                   :x-rapidapi-host "betano.p.rapidapi.com"}
                                                         :query-params {:eventId "id100001750850059"
                                                                        :oddsFormat "decimal"
                                                                        :raw "false"}})
          data (json/parse-string (:body response) true)]

"MUDAR A LÓGICA AQUI PRA SE ADAPTAR ÀS ODDS"

      (println "Informacoes do Torneio:")
      (println "Categoria:" (:categoryName data))
      (println "Nome:" (:name data))
      (println "ID do Torneio:" (:tournamentId data))

      (println "\nEventos:")
      (doseq [event (vals (:events data))]
        (println "-------------------")
        (println "Participante 1:" (:participant1 event))
        (println "Participante 2:" (:participant2 event))
        (println "Data:" (:date event))
        (println "Horario:" (:time event))
        (println "Status:" (:eventStatus event))
        (println "ID do Evento:" (:eventId event))))
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
  (println "4. Buscar informacoes do torneio via API")
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
      :else (do (println "Opcao invalida!") (main saldo)))))

(def saldo (atom 0.0))

(main saldo)
