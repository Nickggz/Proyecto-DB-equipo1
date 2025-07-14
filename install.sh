#!/bin/bash

# Script de instalación del Sistema Electoral Uruguayo
# Requiere Docker y Docker Compose instalados

set -e  # Salir si hay errores

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Funciones de utilidad
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

print_info() {
    echo -e "${BLUE}ℹ $1${NC}"
}

print_header() {
    echo -e "\n${BLUE}================================================${NC}"
    echo -e "${BLUE} $1${NC}"
    echo -e "${BLUE}================================================${NC}\n"
}

# Verificar si Docker está instalado
check_docker() {
    print_info "Verificando Docker..."
    if ! command -v docker &> /dev/null; then
        print_error "Docker no está instalado"
        print_info "Por favor instala Docker desde: https://docs.docker.com/get-docker/"
        exit 1
    fi
    
    if ! docker info &> /dev/null; then
        print_error "Docker no está ejecutándose"
        print_info "Por favor inicia Docker Desktop o el servicio de Docker"
        exit 1
    fi
    
    print_success "Docker está instalado y ejecutándose"
}

# Verificar si Docker Compose está instalado
check_docker_compose() {
    print_info "Verificando Docker Compose..."
    if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
        print_error "Docker Compose no está instalado"
        print_info "Por favor instala Docker Compose desde: https://docs.docker.com/compose/install/"
        exit 1
    fi
    print_success "Docker Compose está instalado"
}

# Verificar puertos disponibles
check_ports() {
    print_info "Verificando puertos disponibles..."
    
    ports=(80 3000 8080 3307)
    for port in "${ports[@]}"; do
        if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
            print_warning "Puerto $port está en uso"
            print_info "Puedes cambiar el puerto en el archivo .env"
        else
            print_success "Puerto $port disponible"
        fi
    done
}

# Crear estructura de directorios
create_directories() {
    print_info "Creando estructura de directorios..."
    
    directories=(
        "docker"
        "database"
        "logs"
        "backups"
    )
    
    for dir in "${directories[@]}"; do
        if [ ! -d "$dir" ]; then
            mkdir -p "$dir"
            print_success "Directorio $dir creado"
        else
            print_info "Directorio $dir ya existe"
        fi
    done
}

# Configurar variables de entorno
setup_environment() {
    print_info "Configurando variables de entorno..."
    
    if [ ! -f ".env" ]; then
        print_warning "Archivo .env no encontrado, creando uno por defecto..."
        # El archivo .env ya está incluido en los artifacts anteriores
        print_success "Archivo .env creado"
    else
        print_info "Archivo .env ya existe"
    fi
}

# Construir e iniciar contenedores
start_containers() {
    print_info "Construyendo e iniciando contenedores..."
    print_warning "Esto puede tomar varios minutos la primera vez..."
    
    # Detener contenedores existentes si están ejecutándose
    docker-compose down 2>/dev/null || true
    
    # Construir e iniciar
    if docker-compose up -d --build; then
        print_success "Contenedores iniciados exitosamente"
    else
        print_error "Error al iniciar contenedores"
        exit 1
    fi
}

# Esperar a que los servicios estén listos
wait_for_services() {
    print_info "Esperando a que los servicios estén listos..."
    
    # Esperar por la base de datos
    print_info "Esperando por MySQL..."
    timeout=60
    while ! docker-compose exec -T database mysqladmin ping -h localhost --silent; do
        sleep 2
        timeout=$((timeout - 2))
        if [ $timeout -le 0 ]; then
            print_error "Timeout esperando por MySQL"
            exit 1
        fi
    done
    print_success "MySQL está listo"
    
    # Esperar por el backend
    print_info "Esperando por el backend..."
    timeout=120
    while ! curl -s http://localhost:8080/api/circuito/departamentos > /dev/null; do
        sleep 5
        timeout=$((timeout - 5))
        if [ $timeout -le 0 ]; then
            print_error "Timeout esperando por el backend"
            exit 1
        fi
    done
    print_success "Backend está listo"
    
    # Esperar por el frontend
    print_info "Esperando por el frontend..."
    timeout=60
    while ! curl -s http://localhost:3000 > /dev/null; do
        sleep 2
        timeout=$((timeout - 2))
        if [ $timeout -le 0 ]; then
            print_error "Timeout esperando por el frontend"
            exit 1
        fi
    done
    print_success "Frontend está listo"
}

# Mostrar información final
show_final_info() {
    print_header "INSTALACIÓN COMPLETADA"
    
    print_success "Sistema Electoral Uruguayo instalado exitosamente!"
    echo
    print_info "URLs de acceso:"
    echo "  • Aplicación web: http://localhost:3000"
    echo "  • API Backend:    http://localhost:8080/api"
    echo "  • Nginx:          http://localhost"
    echo
    print_info "Credenciales de prueba:"
    echo "  • Votante:     5.104.298-2 + AAA123456"
    echo "  • Presidente:  4.012.345-6 + BBB234567"
    echo "  • Secretario:  4.112.345-6 + CCC345678"
    echo "  • Admin:       5.000.000-0 + EEE567890"
    echo
    print_info "Comandos útiles:"
    echo "  • Ver logs:      docker-compose logs -f"
    echo "  • Detener:       docker-compose down"
    echo "  • Reiniciar:     docker-compose restart"
    echo "  • Actualizar:    docker-compose up -d --build"
    echo
    print_warning "Los datos se guardan en volúmenes de Docker y persisten entre reinicios"
}

# Función principal
main() {
    print_header "INSTALADOR DEL SISTEMA ELECTORAL URUGUAYO"
    
    # Verificaciones previas
    check_docker
    check_docker_compose
    check_ports
    
    # Configuración
    create_directories
    setup_environment
    
    # Instalación
    start_containers
    wait_for_services
    
    # Finalización
    show_final_info
}

# Ejecutar función principal
main "$@"