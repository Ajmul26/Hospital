#!/bin/bash

# Full Stack User Management Application Startup Script
# This script starts both the Spring Boot backend and React frontend

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_header() {
    echo ""
    echo "=============================================="
    echo -e "${BLUE}  User Management Full Stack Application${NC}"
    echo "=============================================="
    echo ""
}

# Function to check prerequisites
check_prerequisites() {
    print_info "Checking prerequisites..."
    
    # Check Java
    if ! command -v java &> /dev/null; then
        print_error "Java is not installed or not in PATH"
        exit 1
    fi
    
    # Check Maven
    if ! command -v mvn &> /dev/null; then
        print_error "Maven is not installed or not in PATH"
        exit 1
    fi
    
    # Check Node.js
    if ! command -v node &> /dev/null; then
        print_error "Node.js is not installed or not in PATH"
        exit 1
    fi
    
    # Check npm
    if ! command -v npm &> /dev/null; then
        print_error "npm is not installed or not in PATH"
        exit 1
    fi
    
    print_success "All prerequisites are installed"
}

# Function to setup frontend dependencies
setup_frontend() {
    print_info "Setting up frontend dependencies..."
    
    if [ ! -d "frontend/user-management/node_modules" ]; then
        print_info "Installing npm dependencies..."
        cd frontend/user-management
        npm install
        cd ../../
        print_success "Frontend dependencies installed"
    else
        print_info "Frontend dependencies already installed"
    fi
}

# Function to build backend
build_backend() {
    print_info "Building Spring Boot backend..."
    mvn clean compile package -DskipTests
    print_success "Backend built successfully"
}

# Function to start backend
start_backend() {
    print_info "Starting Spring Boot backend..."
    print_info "Backend will be available at: http://localhost:8080"
    print_info "API Documentation: http://localhost:8080/swagger-ui/index.html"
    print_info "H2 Console: http://localhost:8080/h2-console"
    
    # Start backend in background
    nohup mvn spring-boot:run > backend.log 2>&1 &
    BACKEND_PID=$!
    echo $BACKEND_PID > backend.pid
    
    print_success "Backend started with PID: $BACKEND_PID"
}

# Function to start frontend
start_frontend() {
    print_info "Starting React frontend..."
    print_info "Frontend will be available at: http://localhost:3000"
    
    cd frontend/user-management
    
    # Start frontend in background
    nohup npm start > ../../frontend.log 2>&1 &
    FRONTEND_PID=$!
    echo $FRONTEND_PID > ../../frontend.pid
    
    cd ../../
    print_success "Frontend started with PID: $FRONTEND_PID"
}

# Function to wait for services
wait_for_services() {
    print_info "Waiting for services to start..."
    
    # Wait for backend
    print_info "Waiting for backend to be ready..."
    for i in {1..30}; do
        if curl -s http://localhost:8080/api/users/health > /dev/null 2>&1; then
            print_success "Backend is ready!"
            break
        fi
        if [ $i -eq 30 ]; then
            print_warning "Backend took longer than expected to start"
        fi
        sleep 2
    done
    
    # Wait for frontend
    print_info "Waiting for frontend to be ready..."
    for i in {1..30}; do
        if curl -s http://localhost:3000 > /dev/null 2>&1; then
            print_success "Frontend is ready!"
            break
        fi
        if [ $i -eq 30 ]; then
            print_warning "Frontend took longer than expected to start"
        fi
        sleep 2
    done
}

# Function to display access information
show_access_info() {
    echo ""
    echo "=============================================="
    print_success "Full Stack Application is Running!"
    echo "=============================================="
    echo ""
    echo "🎯 Frontend (React):"
    echo "   📱 Dashboard: http://localhost:3000"
    echo "   👥 Users: http://localhost:3000/users"
    echo "   ➕ Add User: http://localhost:3000/users/new"
    echo ""
    echo "🔧 Backend (Spring Boot):"
    echo "   🌐 API Base: http://localhost:8080/api/users"
    echo "   📚 Swagger UI: http://localhost:8080/swagger-ui/index.html"
    echo "   🔍 H2 Console: http://localhost:8080/h2-console"
    echo ""
    echo "📊 Sample Users Available:"
    echo "   • John Doe (john.doe@example.com)"
    echo "   • Jane Smith (jane.smith@example.com)"
    echo "   • Bob Johnson (bob.johnson@example.com)"
    echo ""
    echo "🔒 Security Scanning:"
    echo "   ./scripts/veracode-scan.sh full-scan"
    echo ""
    echo "📋 Logs:"
    echo "   Backend: tail -f backend.log"
    echo "   Frontend: tail -f frontend.log"
    echo ""
    echo "🛑 To stop services:"
    echo "   ./stop-full-stack.sh"
    echo ""
}

# Function to handle cleanup on exit
cleanup() {
    print_info "Cleaning up..."
    if [ -f backend.pid ]; then
        BACKEND_PID=$(cat backend.pid)
        if kill -0 $BACKEND_PID 2>/dev/null; then
            print_info "Stopping backend (PID: $BACKEND_PID)..."
            kill $BACKEND_PID
        fi
        rm -f backend.pid
    fi
    
    if [ -f frontend.pid ]; then
        FRONTEND_PID=$(cat frontend.pid)
        if kill -0 $FRONTEND_PID 2>/dev/null; then
            print_info "Stopping frontend (PID: $FRONTEND_PID)..."
            kill $FRONTEND_PID
        fi
        rm -f frontend.pid
    fi
}

# Parse command line arguments
COMMAND=""
BUILD_ONLY=false
FRONTEND_ONLY=false
BACKEND_ONLY=false

while [[ $# -gt 0 ]]; do
    case $1 in
        --build-only)
            BUILD_ONLY=true
            shift
            ;;
        --frontend-only)
            FRONTEND_ONLY=true
            shift
            ;;
        --backend-only)
            BACKEND_ONLY=true
            shift
            ;;
        --help|-h)
            echo "Usage: $0 [OPTIONS]"
            echo ""
            echo "Options:"
            echo "  --build-only     Only build the backend, don't start services"
            echo "  --frontend-only  Only start the frontend"
            echo "  --backend-only   Only start the backend"
            echo "  --help, -h       Show this help message"
            echo ""
            echo "Examples:"
            echo "  $0                    # Start full stack"
            echo "  $0 --build-only       # Just build backend"
            echo "  $0 --frontend-only    # Start only frontend"
            echo "  $0 --backend-only     # Start only backend"
            exit 0
            ;;
        *)
            print_error "Unknown option: $1"
            exit 1
            ;;
    esac
done

# Set up signal handlers
trap cleanup EXIT INT TERM

# Main execution
print_header

check_prerequisites

if [ "$FRONTEND_ONLY" = false ]; then
    build_backend
    
    if [ "$BUILD_ONLY" = true ]; then
        print_success "Backend build completed"
        exit 0
    fi
fi

if [ "$BACKEND_ONLY" = false ]; then
    setup_frontend
fi

if [ "$FRONTEND_ONLY" = false ]; then
    start_backend
fi

if [ "$BACKEND_ONLY" = false ]; then
    start_frontend
fi

wait_for_services
show_access_info

# Keep script running
print_info "Press Ctrl+C to stop all services..."
while true; do
    sleep 1
done