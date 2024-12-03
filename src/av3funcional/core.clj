(ns av3funcional.core
  (:require [clj-http.client :as client]
            [cheshire.core :as json]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [environ.core :refer [env]]))

(def api-key (:api-key env))

;; Função para carregar o saldo do arquivo
(defn carregar-saldo []
  (try
    (if (.exists (io/file "saldo.txt"))
      (let [conteudo (slurp "saldo.txt")]
        (Double/parseDouble conteudo))
      0.0) ;; Retorna 0.0 se o arquivo não existir
    (catch Exception e
      (println "Erro ao carregar o saldo. Inicializando com 0.")
      0.0)))

;; Função para salvar o saldo no arquivo
(defn salvar-saldo [saldo]
  (spit "saldo.txt" (str saldo))
  (println "Saldo salvo no arquivo saldo.txt."))

;; Funções de saque e depósito
(defn sacar [saldo]
  (println "Qual o valor do saque?")
  (let [input (str/trim (read-line))]
    (try
      (let [saque (Double/parseDouble input)]
        (if (>= @saldo saque)
          (do
            (swap! saldo - saque)
            (println "Saque realizado com sucesso. Seu saldo atual é:" @saldo))
          (println "Saldo insuficiente! Seu saldo atual é:" @saldo)))
      (catch Exception e
        (println "Valor inválido. Por favor, insira um número válido.")))))

(defn depositar [saldo]
  (println "Qual o valor do depósito?")
  (let [input (str/trim (read-line))]
    (try
      (let [deposito (Double/parseDouble input)]
        (if (> deposito 0)
          (do
            (swap! saldo + deposito)
            (println "Depósito realizado com sucesso. Seu saldo atual é:" @saldo))
          (println "O valor do depósito deve ser maior que 0.")))
      (catch Exception e
        (println "Valor inválido. Por favor, insira um número válido.")))))

(defn checar-saldo [saldo]
  (println "Seu saldo atual é:" @saldo))

(defn gerenciar-conta [saldo]
  (println "\n. . . . . . . . . . .")
  (println "1. Realizar saque")
  (println "2. Realizar depósito")
  (println "3. Checar saldo")
  (println "4. Voltar")
  (let [opcao (str/trim (read-line))]
    (cond
      (= opcao "1") (do (sacar saldo) (gerenciar-conta saldo))
      (= opcao "2") (do (depositar saldo) (gerenciar-conta saldo))
      (= opcao "3") (do (checar-saldo saldo) (gerenciar-conta saldo))
      (= opcao "4") nil
      :else (do (println "Opção inválida! Por favor, escolha novamente.") (gerenciar-conta saldo)))))

;; Buscar eventos de futebol
(defn buscar-eventos [url params]
  (try
    (let [response (client/get url {:headers {:x-rapidapi-key api-key
                                              :x-rapidapi-host "betano.p.rapidapi.com"}
                                    :query-params params})
          data (json/parse-string (:body response) true)]
      data)
    (catch Exception e
      (println "Erro ao acessar a API:" (.getMessage e))
      nil)))

(defn listar-eventos [torneio-id]
  (let [url "https://betano.p.rapidapi.com/events"
        params {:tournamentId torneio-id}
        data (buscar-eventos url params)]
    (if data
      (do
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
          (println "Status:" (:eventStatus event))
          (println "ID do Evento:" (:eventId event))))
      (println "Erro ao listar eventos ou nenhum evento encontrado."))))

;; Buscar odds de um evento
(defn buscar-odds [evento-id]
  (let [url "https://betano.p.rapidapi.com/odds_betano"
        headers {:x-rapidapi-key api-key
                 :x-rapidapi-host "betano.p.rapidapi.com"}
        params {:eventId evento-id
                :oddsFormat "decimal"
                :raw "false"}]
    (try
      (let [response (client/get url {:headers headers :query-params params})
            odds-data (json/parse-string (:body response) true)]
        odds-data)
      (catch Exception e
        (println "Erro ao acessar a API:" (.getMessage e))
        nil))))

;; Salvar aposta em arquivo
(defn salvar-aposta [evento-id mercado-id resultado valor saldo-atual]
  (let [linha (str "Evento: " evento-id ", Mercado: " mercado-id ", Resultado: " resultado ", Valor Apostado: " valor ", Saldo Após Aposta: " saldo-atual "\n")]
    (spit "apostas.txt" linha :append true)
    (println "Aposta salva com sucesso no arquivo apostas.txt.")))

;; Exibir odds e mercados com opção de apostar
(defn exibir-odds [evento-id saldo]
  (let [odds-data (buscar-odds evento-id)]
    (if odds-data
      (let [mercados (:markets odds-data)]
        (println "-------------------")
        (println "Times:" (:participant1 odds-data) "x" (:participant2 odds-data))
        (println "Data:" (:date odds-data))
        (println "Horário:" (:time odds-data))
        (println "Status:" (:eventStatus odds-data))
        (println "\nMercados Disponíveis:")
        (doseq [[market-id market-data] mercados]
          (println "Mercado ID:" market-id)
          (doseq [[outcome-id outcome] (:outcomes market-data)]
            (println " - Resultado:" (:outcomeName outcome)
                     "Preço:" (get-in outcome [:bookmakers :betano :price]))))
        ;; Perguntar se deseja apostar
        (println "\nDeseja apostar em algum mercado? (s/n)")
        (let [resposta (str/trim (read-line))]
          (if (= resposta "s")
            (do
              (println "Digite o ID do mercado:")
              (let [mercado-id (str/trim (read-line))]
                (println "Digite o resultado desejado:")
                (let [resultado (str/trim (read-line))]
                  (println "Digite o valor da aposta:")
                  (let [input (str/trim (read-line))]
                    (try
                      (let [valor-aposta (Double/parseDouble input)]
                        (if (and (number? valor-aposta) (> valor-aposta 0) (>= @saldo valor-aposta))
                          (do
                            (swap! saldo - valor-aposta)
                            (salvar-aposta evento-id mercado-id resultado valor-aposta @saldo)
                            (println "Aposta realizada com sucesso!"))
                          (println "Saldo insuficiente ou valor inválido para realizar a aposta.")))
                      (catch Exception e
                        (println "Valor inválido. Por favor, insira um número válido.")))))))
          (println "Aposta não realizada.")))
      (println "Erro ao buscar odds ou evento não encontrado.")))))

;; Menu para apostas de futebol
(defn menu-aposta-futebol [saldo]
  (println "\n. . . . . . . . . . .")
  (println "1. Escolher um jogo")
  (println "2. Voltar")
  (let [opcao (str/trim (read-line))]
    (cond
      (= opcao "1") (do
                      (println "Digite o ID do evento que deseja consultar:")
                      (let [evento-id (str/trim (read-line))]
                        (if (not (str/blank? evento-id))
                          (exibir-odds evento-id saldo)
                          (println "ID do evento não pode estar vazio.")))
                      (menu-aposta-futebol saldo))
      (= opcao "2") nil
      :else (do
              (println "Opção inválida! Por favor, escolha novamente.")
              (menu-aposta-futebol saldo)))))

;; Menu principal
(defn menu-principal [saldo]
  (println "\n. . . . . . . . . . .")
  (println "1. Gerenciar sua conta")
  (println "2. Fazer apostas")
  (println "3. Consultar resultados")
  (println "4. Buscar informações do torneio de futebol")
  (println "5. Buscar informações do torneio de basquete")
  (println "6. Sair")
  (let [opcao (str/trim (read-line))]
    (cond
      (= opcao "1") (do (gerenciar-conta saldo) (menu-principal saldo))
      (= opcao "2") (do (menu-aposta-futebol saldo) (menu-principal saldo))
      (= opcao "3") (println "Consultar resultados em desenvolvimento.") ;; Funcionalidade futura
      (= opcao "4") (do (listar-eventos "325") (menu-principal saldo))
      (= opcao "5") (do (listar-eventos "24135") (menu-principal saldo))
      (= opcao "6") (do
                      (salvar-saldo @saldo)
                      (println "Saindo..."))
      :else (do
              (println "Opção inválida! Por favor, escolha novamente.")
              (menu-principal saldo)))))

;; Inicializador do programa
(def saldo (atom (carregar-saldo)))
(menu-principal saldo)
