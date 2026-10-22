# ThinDesk — Chamados Técnicos

API REST integrada ao projeto ThinDesk para abertura, consulta e resolução de chamados técnicos. A aplicação usa Java, Spring Boot, Spring Security e MongoDB. O fluxo de login existente é mantido e os endpoints trabalham com a sessão autenticada.

## Tecnologias e requisitos

- Java 21
- Spring Boot 3.4
- Maven Wrapper
- MongoDB local ativo na porta `27017`
- Postman para enviar as requisições

O banco usado por padrão é `thindesk`, e os documentos de chamados ficam na coleção `chamados`.

## Executar a aplicação

1. Inicie o serviço do MongoDB.
2. Abra o PowerShell na pasta do projeto.
3. Execute:

```powershell
.\mvnw.cmd spring-boot:run
```

A API ficará disponível em `http://localhost:8080`.

A conexão padrão é `mongodb://localhost:27017/thindesk`. Para usar outro endereço, configure a variável de ambiente `MONGODB_URI`.

## Autenticação

Os endpoints de chamados exigem uma sessão autenticada. A aplicação mantém o login por formulário do Spring Security em `/login`.

Para a execução local, o usuário padrão é `admin` e a senha é `thindesk`. Esses valores podem ser substituídos por `THINDESK_USER` e `THINDESK_PASSWORD`. Na configuração local, esse usuário possui os perfis `USER` e `TECNICO`, permitindo demonstrar a abertura e a resolução.

No Postman, execute primeiro a requisição **Login**. Ela envia usuário e senha como formulário e o Postman guarda o cookie de sessão para as requisições seguintes. O login pode terminar em um redirecionamento para a página inicial; as chamadas da API é que retornam os dados JSON dos chamados.

## Importar e usar no Postman

A coleção está em `postman/ThinDesk.postman_collection.json`.

No Postman, selecione **Import**, escolha **Files** e abra esse arquivo. A coleção contém as requisições **Login**, **Abrir chamado**, **Listar chamados do usuário**, **Consultar chamado** e **Resolver chamado**. A variável `baseUrl` usa `http://localhost:8080`; após uma abertura com resposta HTTP `201`, a coleção guarda o ID retornado para as consultas e a resolução.

Ordem para demonstrar o fluxo:

1. Execute **Login**.
2. Execute **Abrir chamado** enviando título e descrição.
3. Consulte a lista do usuário e os detalhes do chamado.
4. Execute **Resolver chamado** com a solução aplicada.
5. Confira o documento atualizado na coleção `chamados` do MongoDB.

## Endpoints

| Método | Endpoint | Acesso e resultado |
| --- | --- | --- |
| `POST` | `/api/chamados` | Usuário autenticado; abre um chamado e retorna HTTP `201`. |
| `GET` | `/api/chamados/usuario/{id}` | Lista chamados. `{id}` é o nome do usuário autenticado; técnicos podem consultar outro usuário. |
| `GET` | `/api/chamados/{id}` | Consulta os dados e o status; disponível ao solicitante ou a um técnico. |
| `PUT` | `/api/chamados/{id}/resolver` | Exige o perfil `TECNICO`; grava solução, técnico, data e status `RESOLVIDO`. |

### Abrir chamado

O corpo recebe somente título e descrição. O servidor registra o solicitante da sessão, a data de abertura e o status inicial `ABERTO`.

```json
{
  "titulo": "Título do chamado",
  "descricao": "Descrição do problema"
}
```

O título é obrigatório e aceita até 120 caracteres. A descrição é obrigatória e aceita até 5.000 caracteres.

### Resolver chamado

A URL recebe o ID que foi retornado ao abrir o chamado. O corpo da requisição é:

```json
{
  "solucao": "Descrição da solução aplicada"
}
```

A solução é obrigatória e aceita até 5.000 caracteres. Somente chamados com status `ABERTO` podem ser resolvidos. O servidor registra o técnico autenticado, a data da resolução e o status `RESOLVIDO`. Uma nova tentativa de alterar um chamado resolvido retorna HTTP `409`.

## Validações e respostas

- HTTP `400`: JSON inválido ou campo obrigatório ausente.
- HTTP `401`: sessão não autenticada.
- HTTP `403`: usuário sem permissão para a operação.
- HTTP `404`: chamado não encontrado.
- HTTP `409`: regra de negócio impede a alteração, como resolver novamente um chamado concluído.

## Conferir os dados no MongoDB

No MongoDB Compass, conecte-se a `mongodb://localhost:27017`, abra o banco `thindesk` e depois a coleção `chamados`.

Ao abrir um chamado, o documento contém `titulo`, `descricao`, `solicitante`, `status` e `dataAbertura`. Após a resolução, também contém `solucao`, `tecnico` e `dataResolucao`.
