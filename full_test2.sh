#!/bin/bash
# Script Completo: Setup (Cadastro e Auth) + Fluxo de Teste CRUD + Limpeza
# Versão 3: Utiliza e-mails dinâmicos para garantir unicidade e evitar falhas de setup.

# Configurações
BASE_URL="http://localhost:8080"
TEST_SUFFIX=$(date +%s) # Sufixo único para esta execução (baseado no timestamp)

# --- Variáveis Globais de Usuários ---
# Usuário admin (criado por DataInitializer)
ADMIN_EMAIL="admin@admin.com"
ADMIN_PASSWORD="admin"
ADMIN_TOKEN=""

# Usuário Locador (E-mail Dinâmico)
LOCADOR_EMAIL="locador_$TEST_SUFFIX@teste.com"
LOCADOR_PASSWORD="locador123"
LOCADOR_TOKEN=""
LOCADOR_ID=""

# Usuário Locatário (E-mail Dinâmico)
LOCATARIO_EMAIL="locatario_$TEST_SUFFIX@teste.com"
LOCATARIO_PASSWORD="locatario123"
LOCATARIO_ID=""
LOCATARIO_TOKEN=""

# Variáveis para IDs de recursos criados
CASA_ID=""
INTERESSE_ID=""
ALUGUEL_ID=""

# --- Funções Auxiliares ---

# Variável global para armazenar o corpo da última resposta para extração de ID
RESPONSE_BODY=""

# Função para extrair o ID da resposta JSON
function extract_id() {
    echo "$RESPONSE_BODY" | jq -r '.id'
}

# Função genérica de requisição e checagem de sucesso
function make_request() {
    local method=$1
    local endpoint=$2
    local token=$3
    local data=$4
    local expected_status_code=$5

    echo -e "\n>>> $method $endpoint (Esperado: $expected_status_code)"

    local AUTH_HEADER="Authorization: Bearer $token"
    if [ -z "$token" ]; then
        AUTH_HEADER=""
    fi

    # Limpa a variável global
    RESPONSE_BODY=""

    # Executa a requisição, salva o corpo e imprime o status
    local RESPONSE_FULL
    if [ -z "$data" ]; then
        RESPONSE_FULL=$(curl -s -o /tmp/response_body.json -w "%{http_code}" -X "$method" "$BASE_URL$endpoint" \
            -H "$AUTH_HEADER")
    else
        RESPONSE_FULL=$(curl -s -o /tmp/response_body.json -w "%{http_code}" -X "$method" "$BASE_URL$endpoint" \
            -H "Content-Type: application/json" \
            -H "$AUTH_HEADER" \
            -d "$data")
    fi

    local HTTP_STATUS=$(echo "$RESPONSE_FULL" | tail -n 1)
    RESPONSE_BODY=$(cat /tmp/response_body.json)

    if [ "$HTTP_STATUS" == "$expected_status_code" ] || \
       ( [ "$expected_status_code" == "200" ] && [ "$HTTP_STATUS" == "201" ] ); then
        echo "   [SUCESSO] Status HTTP $HTTP_STATUS"
        if [ "$method" != "DELETE" ] && [ "$method" != "POST" ]; then
            echo "$RESPONSE_BODY" | jq '.'
        fi
        return 0
    else
        echo "   [FALHA] Status HTTP $HTTP_STATUS (Esperado: $expected_status_code)"
        echo "   Corpo da Resposta de ERRO:"
        echo "$RESPONSE_BODY" | jq '.'
        return 1
    fi
}

# Função para registrar um usuário e obter o ID (Função melhorada)
function register_user() {
    local email=$1
    local password=$2
    local role=$3
    local nome=$4

    echo -e "\n--- Registrando Usuário: $email ($role) ---"

    local USER_DATA="{
        \"nome\": \"$nome\",
        \"email\": \"$email\",
        \"telefone\": \"5583998765432\",
        \"role\": \"$role\",
        \"senha\": \"$password\"
    }"

    if make_request POST "/usuarios/" "" "$USER_DATA" 200; then
        local USER_ID=$(extract_id)
        if [ -n "$USER_ID" ] && [ "$USER_ID" != "null" ]; then
            echo "Usuário $email registrado com sucesso. ID: $USER_ID"
            echo "$USER_ID"
            return 0
        else
            echo "Falha ao extrair ID da resposta de sucesso."
            return 1
        fi
    else
        echo "Falha no registro de $email. Consulte o erro acima."
        return 1
    fi
}

# Função para autenticar e obter o token JWT (Função melhorada)
function authenticate() {
    local email=$1
    local password=$2

    echo -e "\n--- Autenticando: $email ---"

    local LOGIN_DATA="{
        \"username\": \"$email\",
        \"password\": \"$password\"
    }"

    if make_request POST "/api/login" "" "$LOGIN_DATA" 200; then
        local TOKEN=$(echo "$RESPONSE_BODY" | jq -r '.token')
        if [ -z "$TOKEN" ] || [ "$TOKEN" == "null" ]; then
            echo "Falha na autenticação: Token não encontrado na resposta."
            return 1
        else
            echo "Token para $email obtido com sucesso."
            echo "$TOKEN"
            return 0
        fi
    else
        echo "Falha na autenticação para $email. Consulte o erro acima."
        return 1
    fi
}

# =========================================================================
# FASE 1: LIMPEZA E POPULAÇÃO INICIAL (SETUP)
# =========================================================================

echo "================================================="
echo "FASE 1: CADASTRO E AUTENTICAÇÃO (POPULAÇÃO INICIAL)"
echo "================================================="

# --- 1.1 Tenta obter o token ADMIN ---
ADMIN_TOKEN=$(authenticate $ADMIN_EMAIL $ADMIN_PASSWORD)

# --- 1.2 Limpar e cadastrar Locador e Locatário ---
LOCADOR_ID=$(register_user $LOCADOR_EMAIL $LOCADOR_PASSWORD LOCADOR "Lucas Locador $TEST_SUFFIX") || { echo "Falha no cadastro do Locador. Abortando."; exit 1; }
LOCATARIO_ID=$(register_user $LOCATARIO_EMAIL $LOCATARIO_PASSWORD LOCATARIO "Maria Locataria $TEST_SUFFIX") || { echo "Falha no cadastro do Locatario. Abortando."; exit 1; }


# --- 1.3 Obter Tokens ---
LOCADOR_TOKEN=$(authenticate $LOCADOR_EMAIL $LOCADOR_PASSWORD) || { echo "Falha na autenticação do Locador. Abortando."; exit 1; }
LOCATARIO_TOKEN=$(authenticate $LOCATARIO_EMAIL $LOCATARIO_PASSWORD) || { echo "Falha na autenticação do Locatário. Abortando."; exit 1; }


# =========================================================================
# FASE 2: FLUXO CRUD COMPLETO
# =========================================================================

echo -e "\n=========================================="
echo "FASE 2: FLUXO CRUD (Locador -> Casa/Aluguel | Locatário -> Interesse)"
echo "=========================================="


# --- TESTE CASA (LOCADOR) ---

# --- 2.1 Criar Casa (POST /casas) ---
CASA_DATA='{
    "nome": "Apartamento Conforto",
    "descricao": "Apartamento 2 quartos, mobiliado, no centro.",
    "endereco": "Rua Principal, 123",
    "tipo": "Apartamento",
    "quartos": 2,
    "mobiliada": true,
    "restricoes": ["Não aceita animais"]
}'
make_request POST "/casas" "$LOCADOR_TOKEN" "$CASA_DATA" 200
CASA_ID=$(extract_id)
if [ -z "$CASA_ID" ] || [ "$CASA_ID" == "null" ]; then echo "ERRO FATAL: Falha ao obter CASA_ID."; exit 1; fi

# --- 2.2 Buscar Casa (GET /casas/{id}) ---
make_request GET "/casas/$CASA_ID" "$LOCATARIO_TOKEN" "" 200

# --- 2.3 Atualizar Casa (PATCH /casas/{id}) ---
UPDATE_CASA_DATA='{
    "nome": "Apartamento Conforto Plus",
    "quartos": 3
}'
make_request PATCH "/casas/$CASA_ID" "$LOCADOR_TOKEN" "$UPDATE_CASA_DATA" 200


# --- TESTE INTERESSE (LOCATARIO) ---

# --- 2.4 Criar Interesse (POST /interesses/) ---
INTERESSE_DATA='{
    "casaId": '$CASA_ID',
    "status": "ATIVO"
}'
make_request POST "/interesses/" "$LOCATARIO_TOKEN" "$INTERESSE_DATA" 200
INTERESSE_ID=$(extract_id)
if [ -z "$INTERESSE_ID" ] || [ "$INTERESSE_ID" == "null" ]; then echo "ERRO FATAL: Falha ao obter INTERESSE_ID."; exit 1; fi

# --- 2.5 Listar Interesses (LOCADOR - deve ver o interesse) ---
make_request GET "/interesses/?status=ATIVO" "$LOCADOR_TOKEN" "" 200


# --- TESTE ALUGUEL (LOCADOR/LOCATÁRIO) ---

# --- 2.6 Criar Aluguel (POST /alugueis) ---
ALUGUEL_DATA='{
    "casaId": '$CASA_ID',
    "locadorId": '$LOCADOR_ID',
    "locatarioId": '$LOCATARIO_ID',
    "valor": 1500.00,
    "status": "ATIVO",
    "contratoUrl": "http://contrato.com/aluguel1"
}'
make_request POST "/alugueis" "$LOCADOR_TOKEN" "$ALUGUEL_DATA" 200
ALUGUEL_ID=$(extract_id)
if [ -z "$ALUGUEL_ID" ] || [ "$ALUGUEL_ID" == "null" ]; then echo "ERRO FATAL: Falha ao obter ALUGUEL_ID."; exit 1; fi

# --- 2.7 Buscar Aluguel (GET /alugueis/{id}) ---
make_request GET "/alugueis/$ALUGUEL_ID" "$LOCATARIO_TOKEN" "" 200

# --- 2.8 Atualizar Aluguel (PATCH /alugueis/{id}) ---
UPDATE_ALUGUEL_DATA='{
    "valor": 1600.00
}'
make_request PATCH "/alugueis/$ALUGUEL_ID" "$LOCADOR_TOKEN" "$UPDATE_ALUGUEL_DATA" 200


# =========================================================================
# FASE 3: LIMPEZA DE DADOS (DELETE)
# =========================================================================

echo -e "\n=========================================="
echo "FASE 3: LIMPEZA DE DADOS (DELETES)"
echo "=========================================="

EXPECTED_DELETE_STATUS=204

# --- 3.1 Deletar Aluguel (DELETE /alugueis/{id}) ---
if [ -n "$ALUGUEL_ID" ] && [ "$ALUGUEL_ID" != "null" ]; then
    echo -e "\n[3.1] DELETAR ALUGUEL: $ALUGUEL_ID (LOCADOR)"
    make_request DELETE "/alugueis/$ALUGUEL_ID" "$LOCADOR_TOKEN" "" $EXPECTED_DELETE_STATUS
fi

# --- 3.2 Deletar Interesse (DELETE /interesses/{id}) ---
if [ -n "$INTERESSE_ID" ] && [ "$INTERESSE_ID" != "null" ]; then
    echo -e "\n[3.2] DELETAR INTERESSE: $INTERESSE_ID (LOCATARIO)"
    make_request DELETE "/interesses/$INTERESSE_ID" "$LOCATARIO_TOKEN" "" $EXPECTED_DELETE_STATUS
fi

# --- 3.3 Deletar Casa (DELETE /casas/{id}) ---
if [ -n "$CASA_ID" ] && [ "$CASA_ID" != "null" ]; then
    echo -e "\n[3.3] DELETAR CASA: $CASA_ID (LOCADOR)"
    make_request DELETE "/casas/$CASA_ID" "$LOCADOR_TOKEN" "" $EXPECTED_DELETE_STATUS
fi

# --- 3.4 Deletar Usuários de Teste (DELETE /usuarios/{id}) ---
if [ -n "$LOCADOR_ID" ] && [ "$LOCADOR_ID" != "null" ] && [ -n "$ADMIN_TOKEN" ] && [ "$ADMIN_TOKEN" != "null" ]; then
    echo -e "\n[3.4] DELETAR LOCADOR DE TESTE: $LOCADOR_ID (ADMIN)"
    make_request DELETE "/usuarios/$LOCADOR_ID" "$ADMIN_TOKEN" "" $EXPECTED_DELETE_STATUS
fi

if [ -n "$LOCATARIO_ID" ] && [ "$LOCATARIO_ID" != "null" ] && [ -n "$ADMIN_TOKEN" ] && [ "$ADMIN_TOKEN" != "null" ]; then
    echo -e "\n[3.5] DELETAR LOCATARIO DE TESTE: $LOCATARIO_ID (ADMIN)"
    make_request DELETE "/usuarios/$LOCATARIO_ID" "$ADMIN_TOKEN" "" $EXPECTED_DELETE_STATUS
fi


echo -e "\n=========================================="
echo "FLUXO DE TESTE CONCLUÍDO. Verifique os logs de SUCESSO (200/204) e FALHA."
echo "=========================================="