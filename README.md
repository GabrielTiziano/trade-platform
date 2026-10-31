# Plataforma de Trading

## Signup

Cria a conta do usuário a partir de nome, email, documento e senha.
Retorna o identificador da conta. Em caso de erro de validação, retorna uma mensagem de erro.

POST /signup
Input: name, email, document, password
Output: accountId

- O nome deve ser válido: precisa ter nome e sobrenome
- O email deve ser válido: precisa ter @ e terminar com .com ou .com.br
- O documento deve ser válido, seguindo a regra do CPF
- A senha deve ser válida: no mínimo 8 caracteres, com letra minúscula, maiúscula e números