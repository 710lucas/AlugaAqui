import requests
import json
import time

# --- Configurações ---
BASE_URL = "http://localhost:8080"
TEST_SUFFIX = str(int(time.time())) # Sufixo único para garantir e-mails não repetidos

# --- Variáveis Globais de Usuários ---
ADMIN_CREDS = {"username": "admin@admin.com", "password": "admin"}
LOCADOR_CREDS = {"username": f"locador_{TEST_SUFFIX}@teste.com", "password": "locador123", "role": "LOCADOR", "nome": f"Lucas Locador {TEST_SUFFIX}"}
LOCATARIO_CREDS = {"username": f"locatario_{TEST_SUFFIX}@teste.com", "password": "locatario123", "role": "LOCATARIO", "nome": f"Maria Locataria {TEST_SUFFIX}"}

# Armazenamento de IDs e Tokens (Inicializados para evitar NameError)
TOKENS = {}
USER_IDS = {}
ADMIN_TOKEN = None
LOCADOR_TOKEN = None
LOCADOR_ID = None
LOCATARIO_TOKEN = None
LOCATARIO_ID = None

CASA_ID = None
INTERESSE_ID = None
ALUGUEL_ID = None

# --- Funções Auxiliares ---

def log_result(stage, success, message, details=None):
    """Função para logar o resultado de cada etapa."""
    status = "[SUCESSO]" if success else "[FALHA]"
    print(f"\n[{status}] {stage}: {message}")
    if details:
        if isinstance(details, dict):
            print(json.dumps(details, indent=4, ensure_ascii=False))
        elif isinstance(details, str):
            print(details)
    if not success:
        print("!!! O TESTE FALHOU NESTE PONTO !!!")
        # exit(1) # Descomentar para interromper no primeiro erro

def make_request(method, endpoint, token=None, data=None, expected_status_codes=(200, 201, 204)):
    """Função genérica para fazer requisições HTTP."""
    url = f"{BASE_URL}{endpoint}"
    headers = {"Content-Type": "application/json"}
    if token:
        headers["Authorization"] = f"Bearer {token}"
    
    try:
        # Lógica para determinar se 'data' deve ser JSON ou PARAM
        if data and method in ["POST", "PATCH"]:
            response = requests.request(method, url, headers=headers, json=data)
        elif data and method == "GET":
             response = requests.request(method, url, headers=headers, params=data)
        else:
            response = requests.request(method, url, headers=headers)
        
        is_success = response.status_code in expected_status_codes
        
        details = None
        if response.content:
            try:
                details = response.json()
            except json.JSONDecodeError:
                details = response.text
        
        log_message = f"{method} {endpoint} -> Status HTTP {response.status_code} (Esperado: {expected_status_codes})"
        
        log_result(f"{method} {endpoint}", is_success, log_message, details if not is_success else None)

        return is_success, response, details
        
    except requests.exceptions.RequestException as e:
        log_result(f"{method} {endpoint}", False, f"Erro de Conexão/Requisição: {e}")
        return False, None, None

def authenticate(email, password):
    """Autentica um usuário e armazena o token."""
    login_data = {"username": email, "password": password}
    
    # Ajustando o log para ser mais conciso no sucesso
    is_success, response, details = make_request("POST", "/api/login", data=login_data, expected_status_codes=(200,))
    
    if is_success:
        token = details.get("token")
        if token:
            TOKENS[email] = token
            log_result("Autenticação", True, f"Token obtido para {email}")
            return token
        else:
            log_result("Autenticação", False, f"Token não encontrado na resposta para {email}", details)
            return None
    else:
        # O make_request já logou a falha HTTP
        return None

def register_user(email, password, role, nome):
    """Registra um novo usuário e armazena o ID."""
    user_data = {
        "nome": nome,
        "email": email,
        "telefone": "5583998765432",
        "role": role,
        "senha": password
    }
    
    # CORREÇÃO: Adicionando 201 aos códigos de sucesso esperados.
    is_success, response, details = make_request("POST", "/usuarios/", data=user_data, expected_status_codes=(200, 201))
    
    if is_success:
        user_id = details.get("id")
        if user_id:
            USER_IDS[email] = user_id
            log_result("Cadastro de Usuário", True, f"Usuário {email} ({role}) cadastrado com sucesso. ID: {user_id}")
            return user_id
        else:
            log_result("Cadastro de Usuário", False, f"ID de usuário não encontrado na resposta de sucesso para {email}", details)
            return None
    else:
        log_result("Cadastro de Usuário", False, f"Falha no cadastro de {email}", details)
        return None

# =========================================================================
# FASE 1: CADASTRO E AUTENTICAÇÃO
# =========================================================================

print("="*60)
print("FASE 1: CADASTRO E AUTENTICAÇÃO")
print("="*60)

# 1.1 Autenticar ADMIN (usuário pré-existente)
ADMIN_TOKEN = authenticate(ADMIN_CREDS["username"], ADMIN_CREDS["password"])

# 1.2 Cadastrar e Autenticar LOCADOR
LOCADOR_ID = register_user(LOCADOR_CREDS["username"], LOCADOR_CREDS["password"], LOCADOR_CREDS["role"], LOCADOR_CREDS["nome"])
if LOCADOR_ID:
    LOCADOR_TOKEN = authenticate(LOCADOR_CREDS["username"], LOCADOR_CREDS["password"])

# 1.3 Cadastrar e Autenticar LOCATARIO
LOCATARIO_ID = register_user(LOCATARIO_CREDS["username"], LOCATARIO_CREDS["password"], LOCATARIO_CREDS["role"], LOCATARIO_CREDS["nome"])
if LOCATARIO_ID:
    LOCATARIO_TOKEN = authenticate(LOCATARIO_CREDS["username"], LOCATARIO_CREDS["password"])

# CORREÇÃO: Variáveis agora são inicializadas globalmente para evitar NameError
if not all([ADMIN_TOKEN, LOCADOR_TOKEN, LOCATARIO_TOKEN, LOCADOR_ID, LOCATARIO_ID]):
    log_result("SETUP GERAL", False, "Não foi possível concluir o setup inicial (Tokens ou IDs ausentes). Abortando testes.")
    exit(1)

# =========================================================================
# FASE 2: FLUXO CRUD (CASA -> INTERESSE -> ALUGUEL)
# =========================================================================

print("\n" + "="*60)
print("FASE 2: FLUXO CRUD (Locador -> Casa/Aluguel | Locatário -> Interesse)")
print("="*60)

# --- TESTE CASA (LOCADOR) ---

# 2.1 Criar Casa (POST /casas)
casa_create_data = {
    "nome": "Apartamento Conforto",
    "descricao": "Apartamento 2 quartos, mobiliado, no centro.",
    "endereco": "Rua Principal, 123",
    "tipo": "Apartamento",
    "quartos": 2,
    "mobiliada": True,
    "restricoes": ["Não aceita animais"]
}
is_success, response, details = make_request("POST", "/casas", LOCADOR_TOKEN, casa_create_data, (200, 201))
if is_success:
    CASA_ID = details.get("id")
    log_result("2.1 Criar Casa", True, f"Casa criada com sucesso. ID: {CASA_ID}", details)

# 2.2 Buscar Casa (GET /casas/{id})
if CASA_ID:
    is_success, response, details = make_request("GET", f"/casas/{CASA_ID}", LOCATARIO_TOKEN, expected_status_codes=(200,))
    log_result("2.2 Buscar Casa", is_success, f"Busca de Casa ID {CASA_ID} pelo Locatário.", details if not is_success else None)

# 2.3 Atualizar Casa (PATCH /casas/{id})
casa_update_data = {
    "nome": "Apartamento Conforto Plus",
    "quartos": 3
}
if CASA_ID:
    is_success, response, details = make_request("PATCH", f"/casas/{CASA_ID}", LOCADOR_TOKEN, casa_update_data, (200,))
    log_result("2.3 Atualizar Casa", is_success, f"Atualização de Casa ID {CASA_ID}.", details if not is_success else None)


# --- TESTE INTERESSE (LOCATARIO) ---

# 2.4 Criar Interesse (POST /interesses/)
interesse_create_data = {
    "casaId": CASA_ID,
    "status": "ATIVO"
}
if CASA_ID:
    is_success, response, details = make_request("POST", "/interesses/", LOCATARIO_TOKEN, interesse_create_data, (200, 201))
    if is_success:
        INTERESSE_ID = details.get("id")
        log_result("2.4 Criar Interesse", True, f"Interesse criado com sucesso. ID: {INTERESSE_ID}", details)

# 2.5 Listar Interesses (LOCADOR - deve ver o interesse)
if LOCADOR_TOKEN:
    params = {"status": "ATIVO"}
    is_success, response, details = make_request("GET", "/interesses/", LOCADOR_TOKEN, data=params, expected_status_codes=(200,))
    log_result("2.5 Listar Interesses (Locador)", is_success, f"Listando interesses como Locador.", details if not is_success else f"Interesses encontrados: {len(details.get('content', []))}")


# --- TESTE ALUGUEL (LOCADOR/LOCATÁRIO) ---

# 2.6 Criar Aluguel (POST /alugueis)
aluguel_create_data = {
    "casaId": CASA_ID,
    "locatarioId": LOCATARIO_ID,
    "valor": 1500.00,
    "status": "ATIVO",
    "contratoUrl": "http://contrato.com/aluguel1"
}
if CASA_ID and LOCATARIO_ID:
    is_success, response, details = make_request("POST", "/alugueis", LOCADOR_TOKEN, aluguel_create_data, (200, 201))
    if is_success:
        ALUGUEL_ID = details.get("id")
        log_result("2.6 Criar Aluguel", True, f"Aluguel criado com sucesso. ID: {ALUGUEL_ID}", details)

# 2.7 Buscar Aluguel (GET /alugueis/{id})
if ALUGUEL_ID:
    is_success, response, details = make_request("GET", f"/alugueis/{ALUGUEL_ID}", LOCATARIO_TOKEN, expected_status_codes=(200,))
    log_result("2.7 Buscar Aluguel (Locatário)", is_success, f"Busca de Aluguel ID {ALUGUEL_ID} pelo Locatário.", details if not is_success else None)

# 2.8 Atualizar Aluguel (PATCH /alugueis/{id})
aluguel_update_data = {
    "valor": 1600.00
}
if ALUGUEL_ID:
    is_success, response, details = make_request("PATCH", f"/alugueis/{ALUGUEL_ID}", LOCADOR_TOKEN, aluguel_update_data, (200,))
    log_result("2.8 Atualizar Aluguel", is_success, f"Atualização de Aluguel ID {ALUGUEL_ID}.", details if not is_success else None)


# =========================================================================
# FASE 3: LIMPEZA DE DADOS (DELETE)
# =========================================================================

print("\n" + "="*60)
print("FASE 3: LIMPEZA DE DADOS")
print("="*60)

EXPECTED_DELETE_STATUS = (204,)

# 3.1 Deletar Aluguel (DELETE /alugueis/{id})
if ALUGUEL_ID:
    is_success, response, details = make_request("DELETE", f"/alugueis/{ALUGUEL_ID}", LOCADOR_TOKEN, expected_status_codes=EXPECTED_DELETE_STATUS)
    log_result("3.1 Deletar Aluguel", is_success, f"Deletando Aluguel ID {ALUGUEL_ID} (Locador).")

# 3.2 Deletar Interesse (DELETE /interesses/{id})
if INTERESSE_ID:
    is_success, response, details = make_request("DELETE", f"/interesses/{INTERESSE_ID}", LOCATARIO_TOKEN, expected_status_codes=EXPECTED_DELETE_STATUS)
    log_result("3.2 Deletar Interesse", is_success, f"Deletando Interesse ID {INTERESSE_ID} (Locatário).")

# 3.3 Deletar Casa (DELETE /casas/{id})
if CASA_ID:
    is_success, response, details = make_request("DELETE", f"/casas/{CASA_ID}", LOCADOR_TOKEN, expected_status_codes=EXPECTED_DELETE_STATUS)
    log_result("3.3 Deletar Casa", is_success, f"Deletando Casa ID {CASA_ID} (Locador).")

# 3.4 Deletar Usuários de Teste (DELETE /usuarios/{id}) - Requer ADMIN
if LOCADOR_ID and ADMIN_TOKEN:
    is_success, response, details = make_request("DELETE", f"/usuarios/{LOCADOR_ID}", ADMIN_TOKEN, expected_status_codes=EXPECTED_DELETE_STATUS)
    log_result("3.4 Deletar Locador", is_success, f"Deletando Locador ID {LOCADOR_ID} (Admin).")

if LOCATARIO_ID and ADMIN_TOKEN:
    is_success, response, details = make_request("DELETE", f"/usuarios/{LOCATARIO_ID}", ADMIN_TOKEN, expected_status_codes=EXPECTED_DELETE_STATUS)
    log_result("3.5 Deletar Locatário", is_success, f"Deletando Locatário ID {LOCATARIO_ID} (Admin).")

print("\n" + "="*60)
print("FLUXO DE TESTE CONCLUÍDO. Verifique os logs.")
print("="*60)

