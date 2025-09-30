#!/bin/bash
# Script de Configuração Inicial e Geração de Tokens para Testes

# Configurações
BASE_URL="http://localhost:8080"

# --- Variáveis Globais de Usuários ---
# Usuário admin (criado por DataInitializer)
ADMIN_EMAIL="admin@admin.com"
ADMIN_PASSWORD="admin"
ADMIN_TOKEN=""
ADMIN_ID=""

# Usuário Locador
LOCADOR_EMAIL="locador@teste.com"
LOCADOR_PASSWORD="locador123"
LOCADOR_TOKEN=""
LOCADOR_ID=""

# Usuário Locatário
LOCATARIO_EMAIL="locatario@teste.com"
LOCATARIO_PASSWORD="locatario123"
LOCATARIO_ID=""
LOCATARIO_TOKEN=""


# Função para registrar um usuário e obter o ID
function register_user() {
    local email=$1
    local password=$2
    local role=$3
    local nome=$4

    echo "--- Registrando Usuário: $email ($role) ---"
    RESPONSE=$(curl -s -X POST "$BASE_URL/usuarios/" \
        -H "Content-Type: application/json" \
        -d "{
            \"nome\": \"$nome\",
            \"email\": \"$email\",
            \"telefone\": \"5583998765432\",
            \"role\": \"$role\",
            \"senha\": \"$password\"
        }")

    if echo "$RESPONSE" | grep -q "id"; then
        USER_ID=$(echo "$RESPONSE" | jq -r '.id')
        echo "Usuário $email registrado com sucesso. ID: $USER_ID"
        echo "$USER_ID"
    else
        echo "Falha ao registrar $email. Mensagem: $RESPONSE"
        # Se o usuário já existir, tentamos buscá-lo (requer autenticação, pulamos por enquanto)
        return 1
    fi
}

# Função para autenticar e obter o token JWT
function authenticate() {
    local email=$1
    local password=$2

    echo "--- Autenticando: $email ---"
    TOKEN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/login" \
        -H "Content-Type: application/json" \
        -d "{
            \"username\": \"$email\",
            \"password\": \"$password\"
        }")

    TOKEN=$(echo "$TOKEN_RESPONSE" | jq -r '.token')

    if [ "$TOKEN" == "null" ] || [ -z "$TOKEN" ]; then
        echo "Falha na autenticação para $email."
        return 1
    else
        echo "Token para $email obtido com sucesso."
        echo "$TOKEN"
    fi
}

# --- 1. Limpar e criar Locador/Locatário ---
# Este passo de "limpar" é simulado. Em um ambiente real, você faria um TRUNCATE via SQL.
# Aqui, tentamos registrar e capturamos o ID (que será usado para autenticação no full_test2.sh)

# Registro do Locador
LOCADOR_ID=$(register_user $LOCADOR_EMAIL $LOCADOR_PASSWORD LOCADOR "Lucas Locador") || exit 1

# Registro do Locatário
LOCATARIO_ID=$(register_user $LOCATARIO_EMAIL $LOCATARIO_PASSWORD LOCATARIO "Maria Locataria") || exit 1

# --- 2. Obter Tokens ---
ADMIN_TOKEN=$(authenticate $ADMIN_EMAIL $ADMIN_PASSWORD) || exit 1
LOCADOR_TOKEN=$(authenticate $LOCADOR_EMAIL $LOCADOR_PASSWORD) || exit 1
LOCATARIO_TOKEN=$(authenticate $LOCATARIO_EMAIL $LOCATARIO_PASSWORD) || exit 1

# --- 3. Exportar variáveis para o próximo script (ou usar o mesmo script) ---
echo -e "\n--- Variáveis Exportadas (Para uso no full_test.sh) ---"
echo "export BASE_URL=$BASE_URL"
echo "export LOCADOR_ID=$LOCADOR_ID"
echo "export LOCATARIO_ID=$LOCATARIO_ID"
echo "export ADMIN_TOKEN=$ADMIN_TOKEN"
echo "export LOCADOR_TOKEN=$LOCADOR_TOKEN"
echo "export LOCATARIO_TOKEN=$LOCATARIO_TOKEN"

# Cria um arquivo temporário com as variáveis exportadas
echo "export BASE_URL=$BASE_URL" > env_vars.sh
echo "export LOCADOR_ID=$LOCADOR_ID" >> env_vars.sh
echo "export LOCATARIO_ID=$LOCATARIO_ID" >> env_vars.sh
echo "export ADMIN_TOKEN=$ADMIN_TOKEN" >> env_vars.sh
echo "export LOCADOR_TOKEN=$LOCADOR_TOKEN" >> env_vars.sh
echo "export LOCATARIO_TOKEN=$LOCATARIO_TOKEN" >> env_vars.sh
echo -e "\nVariáveis de ambiente salvas em env_vars.sh. Use 'source env_vars.sh' antes de rodar o full_test.sh."