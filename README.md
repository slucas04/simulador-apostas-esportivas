# ⚽ Simulador de Apostas Esportivas  

## 📜 Descrição  
O **Simulador de Apostas Esportivas** é uma aplicação web que permite ao usuário **simular apostas** em eventos esportivos.  
Com funcionalidades como:  
- Gerenciamento de saldo 💰  
- Busca de eventos esportivos via **API Betano** 🏆  
- Simulação de apostas 🎰  

O sistema é dividido em:  
- **Front-end**: Interface visual usando HTML, CSS (Bootstrap) e JavaScript.  
- **Back-end**: API robusta construída em **Clojure** com integração à **API Betano** para obter eventos e odds.  

---  

## ✨ Funcionalidades  

### **Front-end**  
✅ Exibição do saldo atual do usuário.  
✅ Realização de **depósitos** e **saques**.  
✅ Consulta de **eventos esportivos** por torneios.  
✅ Busca de odds (probabilidades) para cada evento.  
✅ Simulação de **apostas**, escolhendo evento, mercado e resultado.  
✅ Listagem de **apostas realizadas**.  

### **Back-end**  
- 🔧 Gestão financeira: depósitos, saques e saldo do usuário.  
- ⚽ Busca de eventos esportivos e odds usando a **API Betano**.  
- 📄 Registro e listagem das apostas realizadas.  

---  

## 🛠️ Tecnologias Utilizadas  

### **Front-end**  
- **HTML5** e **CSS3**: Estruturação e estilização.  
- **Bootstrap 5**: Design moderno e responsivo.  
- **JavaScript**: Dinamismo e integração com a API.  

### **Back-end**  
- **Clojure**: Linguagem funcional para o servidor.  
- **Ring**: Servidor HTTP para Clojure.  
- **Compojure**: Roteamento da API.  
- **clj-http**: Requisições HTTP para API externas.  
- **Cheshire**: Manipulação de JSON.  
- **API Betano**: Fonte de dados para eventos e odds.  

---  

## 🚀 Como Executar  

### **Pré-requisitos**  
1. Obtenha uma **API Key** da Betano.  
2. Instale **Java 8+**.  
3. Instale o [Leiningen](https://leiningen.org/) para gerenciar o back-end.  

### **Passos**  

#### **Back-end**  
1. Navegue até a pasta `back/`.  
2. Inicie o servidor com:  
   ```bash
   lein run
3. A API estará disponível em **http://localhost:8081.**

#### **Front-end**
1. Abra o arquivo index.html localizado na pasta front/ no navegador.
2. Garanta que o servidor back-end está rodando.

   ### **🌐 Endpoints da API**

| **Método** | **Endpoint**              | **Descrição**                            |
|------------|---------------------------|------------------------------------------|
| GET        | `/saldo`                  | Retorna o saldo atual do usuário.        |
| POST       | `/depositar`              | Realiza um depósito no saldo.            |
| PUT        | `/sacar`                  | Realiza um saque do saldo.               |
| POST       | `/apostar`                | Registra uma aposta simulada.            |
| GET        | `/consultar-apostas`      | Lista todas as apostas realizadas.       |
| GET        | `/eventos/:torneio-id`    | Retorna eventos de um torneio.           |
| GET        | `/odds/:evento-id`        | Retorna odds de um evento esportivo.     |

---

### **📂 Estrutura de Pastas**

```bash
simulador-apostas/  
├── front/               # Código do front-end  
│   ├── index.html       # Interface principal  
│   └── assets/          # Estilos, fontes e scripts  
├── back/                # Código do back-end  
│   ├── src/             # Código-fonte em Clojure  
│   └── project.clj      # Configurações do projeto Leiningen  
└── README.md            # Documentação do projeto
```

### **📂📄 Licença**
Este projeto é de uso livre para fins educativos.
Sinta-se à vontade para usar e modificar como preferir.
