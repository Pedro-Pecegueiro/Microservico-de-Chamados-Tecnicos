# ThinDesk — Sistema de chamados técnicos

Aplicação acadêmica baseada no ThinDesk fornecido na disciplina. Mantém a interface e o login do projeto original e acrescenta painel de acompanhamento, API REST para chamados e persistência em MongoDB.

## Versão

**Versão atual: 1.0.0**

O projeto usa versionamento semântico no formato `MAJOR.MINOR.PATCH`:

- `MAJOR`: mudança incompatível com versões anteriores;
- `MINOR`: funcionalidade nova compatível com o que já existia;
- `PATCH`: correção compatível, sem adicionar funcionalidade incompatível.

Uma versão de pré-lançamento pode usar um sufixo, como `1.1.0-beta.1`. A versão da aplicação é definida no `pom.xml`.

## Funcionalidades

- Login por sessão com Spring Security.
- Dashboard com totais de chamados abertos, resolvidos e recentes, carregados do MongoDB.
- Abertura de chamado com título e descrição; solicitante, data e status inicial são definidos pelo servidor.
- Consulta individual e listagem de chamados por solicitante.
- Resolução por técnico, com solução e data da resolução.
- Validação dos dados e bloqueio de alterações depois da resolução.

## Tecnologias

- Java 21
- Spring Boot 3.4
- Spring MVC, Spring Security e Thymeleaf
- Spring Data MongoDB
- Maven Wrapper
- Postman

## Requisitos para executar localmente

- JDK 21 ou superior
- MongoDB ativo em `localhost:27017`, ou uma URI MongoDB configurada na variável `MONGODB_URI`

O banco padrão é `thindesk`; a coleção de chamados é `chamados`.

### Configuração

O arquivo `.env.example` mostra os valores de demonstração. Configure as variáveis no terminal antes de iniciar, se precisar usar valores diferentes:

```powershell
$env:MONGODB_URI = "mongodb://localhost:27017/thindesk"
$env:THINDESK_USER = "admin"
$env:THINDESK_PASSWORD = "thindesk"
```

As credenciais padrão são apenas para demonstração local. Em produção, configure `THINDESK_USER` e `THINDESK_PASSWORD` no serviço de hospedagem. Não coloque credenciais privadas ou URI com senha no GitHub.

### Iniciar

Na pasta do projeto, execute:

```powershell
.\mvnw.cmd spring-boot:run
```

A aplicação ficará disponível em `http://localhost:8080`. Entre com o usuário e a senha configurados. O padrão local é `admin` / `thindesk`.

Para gerar o pacote executável:

```powershell
.\mvnw.cmd -B package
```

## Dashboard

A tela inicial mantém o layout do ThinDesk e apresenta dados consultados do MongoDB.

![Dashboard do ThinDesk em execução](docs/dashboard.png)

## API REST

Os endpoints de chamados exigem login. Para resolver um chamado, a conta precisa ter o perfil de técnico.

| Método | Endpoint | Descrição |
| --- | --- | --- |
| `POST` | `/api/chamados` | Abre um chamado. Retorna `201 Created`. |
| `GET` | `/api/chamados/usuario/{id}` | Lista chamados do solicitante autenticado. |
| `GET` | `/api/chamados/{id}` | Consulta os dados e o status do chamado. |
| `PUT` | `/api/chamados/{id}/resolver` | Registra a solução e conclui o chamado. |

Exemplo de abertura:

```json
{
  "titulo": "Computador reiniciando",
  "descricao": "O computador do setor de RH reinicia sozinho durante o uso."
}
```

Exemplo de resolução:

```json
{
  "solucao": "Verificada a memória e a temperatura; substituído o módulo com falha e validado o funcionamento."
}
```

O servidor preenche automaticamente o solicitante, a data de abertura e o status `ABERTO`. Na resolução, registra o técnico, a solução, a data e o status `RESOLVIDO`. Uma segunda tentativa de alteração retorna `409 Conflict`.

### Postman

Importe `postman/ThinDesk.postman_collection.json` pelo menu **Import → Files**. A coleção inclui login, abertura, listagem, consulta e resolução. Após abrir um chamado, ela guarda o ID retornado para as requisições seguintes.

## Banco de dados

Para conferir a persistência local pelo MongoDB Compass, conecte-se a `mongodb://localhost:27017`, abra o banco `thindesk` e a coleção `chamados`. Os documentos incluem título, descrição, solicitante, status e datas; depois da resolução também incluem a solução e o técnico.

## Publicação

O projeto está preparado para construção pelo `Dockerfile` e usa a porta fornecida pela variável `PORT`. No Railway, configure:

- `MONGODB_URI`: URI do MongoDB persistente;
- `THINDESK_USER`: usuário público de demonstração;
- `THINDESK_PASSWORD`: senha pública de demonstração.

O login de demonstração é compartilhado e permite acessar a aplicação publicada. Use-o somente para a demonstração acadêmica; não reutilize senha pessoal. O link público será registrado aqui depois que o deploy for publicado e verificado.

## Repositório

[Microserviço de Chamados Técnicos no GitHub](https://github.com/Pedro-Pecegueiro/Microservico-de-Chamados-Tecnicos)
