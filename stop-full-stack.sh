#!/bin/bash

# Full Stack Application Stop Script
# This script stops both the Spring Boot backend and React frontend

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
    echo -e "${BLUE}  Stopping Full Stack Application${NC}"
    echo "=============================================="
    echo ""
}

# Function to stop backend
stop_backend() {
    if [ -f backend.pid ]; then
        BACKEND_PID=$(cat backend.pid)
        if kill -0 $BACKEND_PID 2>/dev/null; then
            print_info "Stopping backend (PID: $BACKEND_PID)..."
            kill $BACKEND_PID
            
            # Wait for graceful shutdown
            for i in {1..10}; do
                if ! kill -0 $BACKEND_PID 2>/dev/null; then
                    break
                fi
                sleep 1
            done
            
            # Force kill if still running
            if kill -0 $BACKEND_PID 2>/dev/null; then
                print_warning "Forcing backend shutdown..."
                kill -9 $BACKEND_PID
            fi
            
            print_success "Backend stopped"
        else
            print_warning "Backend PID found but process not running"
        fi
        rm -f backend.pid
    else
        print_info "No backend PID file found"
    fi
    
    # Kill any remaining Spring Boot processes
    pkill -f "spring-boot:run" 2>/dev/null || true
    pkill -f "SpringCrudH2Application" 2>/dev/null || true
}

# Function to stop frontend
stop_frontend() {
    if [ -f frontend.pid ]; then
        FRONTEND_PID=$(cat frontend.pid)
        if kill -0 $FRONTEND_PID 2>/dev/null; then
            print_info "Stopping frontend (PID: $FRONTEND_PID)..."
            kill $FRONTEND_PID
            
            # Wait for graceful shutdown
            for i in {1..10}; do
                if ! kill -0 $FRONTEND_PID 2>/dev/null; then
                    break
                fi
                sleep 1
            done
            
            # Force kill if still running
            if kill -0 $FRONTEND_PID 2>/dev/null; then
                print_warning "Forcing frontend shutdown..."
                kill -9 $FRONTEND_PID
            fi
            
            print_success "Frontend stopped"
        else
            print_warning "Frontend PID found but process not running"
        fi
        rm -f frontend.pid
    else
        print_info "No frontend PID file found"
    fi
    
    # Kill any remaining React processes
    pkill -f "react-scripts start" 2>/dev/null || true
    pkill -f "npm start" 2>/dev/null || true
}

# Function to clean up additional processes
cleanup_processes() {
    print_info "Cleaning up any remaining processes..."
    
    # Kill processes on specific ports
    if lsof -ti:8080 >/dev/null 2>&1; then
        print_info "Stopping process on port 8080..."
        kill $(lsof -ti:8080) 2>/dev/null || true
    fi
    
    if lsof -ti:3000 >/dev/null 2>&1; then
        print_info "Stopping process on port 3000..."
        kill $(lsof -ti:3000) 2>/dev/null || true
    fi
}

# Function to clean up log files
cleanup_logs() {
    if [ "$1" = "--clean-logs" ]; then
        print_info "Cleaning up log files..."
        rm -f backend.log frontend.log
        print_success "Log files cleaned"
    fi
}

# Main execution
print_header

# Parse command line arguments
CLEAN_LOGS=false
while [[ $# -gt 0 ]]; do
    case $1 in
        --clean-logs)
            CLEAN_LOGS=true
            shift
            ;;
        --help|-h)
            echo "Usage: $0 [OPTIONS]"
            echo ""
            echo "Options:"
            echo "  --clean-logs     Also remove log files"
            echo "  --help, -h       Show this help message"
            echo ""
            echo "Examples:"
            echo "  $0                    # Stop all services"
            echo "  $0 --clean-logs       # Stop all services and clean logs"
            exit 0
            ;;
        *)
            print_error "Unknown option: $1"
            exit 1
            ;;
    esac
done

stop_backend
stop_frontend
cleanup_processes

if [ "$CLEAN_LOGS" = true ]; then
    cleanup_logs --clean-logs
fi

echo ""
print_success "All services stopped successfully!"
echo ""
print_info "To start the application again, run: ./start-full-stack.sh"
echo ""