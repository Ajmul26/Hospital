#!/bin/bash

# Veracode Security Scanning Script for Spring Boot CRUD Application
# This script provides easy commands for various Veracode operations

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_NAME="spring-crud-h2"
APP_NAME="spring-crud-h2-app"
VERACODE_SANDBOX="development"

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

# Function to check prerequisites
check_prerequisites() {
    print_info "Checking prerequisites..."
    
    # Check if Maven is installed
    if ! command -v mvn &> /dev/null; then
        print_error "Maven is not installed or not in PATH"
        exit 1
    fi
    
    # Check if Veracode credentials are set
    if [[ -z "$VERACODE_API_ID" || -z "$VERACODE_API_KEY" ]]; then
        print_warning "Veracode credentials not found in environment variables"
        print_info "Please set VERACODE_API_ID and VERACODE_API_KEY environment variables"
        print_info "Or use the -v and -k options to provide credentials"
    fi
    
    print_success "Prerequisites check completed"
}

# Function to build the application
build_application() {
    print_info "Building Spring Boot application..."
    mvn clean package -DskipTests
    print_success "Application built successfully"
}

# Function to run OWASP dependency check
run_dependency_check() {
    print_info "Running OWASP Dependency Check..."
    mvn org.owasp:dependency-check-maven:check
    print_success "Dependency check completed. Report available in target/dependency-check-report/"
}

# Function to generate SBOM (Software Bill of Materials)
generate_sbom() {
    print_info "Generating Software Bill of Materials (SBOM)..."
    mvn org.cyclonedx:cyclonedx-maven-plugin:makeAggregateBom
    print_success "SBOM generated. Available in target/bom.json"
}

# Function to upload to Veracode for static analysis
upload_for_static_scan() {
    local vid="$1"
    local vkey="$2"
    
    print_info "Uploading application for Veracode Static Analysis..."
    
    if [[ -n "$vid" && -n "$vkey" ]]; then
        mvn com.veracode.vosp.api.wrappers:vosp-api-wrappers-java:uploadandscan \
            -Dveracode.vid="$vid" \
            -Dveracode.vkey="$vkey" \
            -Dveracode.app.name="$APP_NAME" \
            -Dveracode.create.profile=true \
            -Dveracode.version="$(date +%Y%m%d-%H%M%S)" \
            -Dveracode.filepath="target/${PROJECT_NAME}-*.jar"
    else
        mvn com.veracode.vosp.api.wrappers:vosp-api-wrappers-java:uploadandscan \
            -Dveracode.vid="$VERACODE_API_ID" \
            -Dveracode.vkey="$VERACODE_API_KEY" \
            -Dveracode.app.name="$APP_NAME" \
            -Dveracode.create.profile=true \
            -Dveracode.version="$(date +%Y%m%d-%H%M%S)" \
            -Dveracode.filepath="target/${PROJECT_NAME}-*.jar"
    fi
    
    print_success "Application uploaded for static analysis"
}

# Function to run Veracode Pipeline Scan
run_pipeline_scan() {
    local vid="$1"
    local vkey="$2"
    
    print_info "Running Veracode Pipeline Scan..."
    
    # Download Pipeline Scanner if not exists
    if [[ ! -f "pipeline-scan.jar" ]]; then
        print_info "Downloading Veracode Pipeline Scanner..."
        curl -sSO https://downloads.veracode.com/securityscan/pipeline-scan-LATEST.zip
        unzip -o pipeline-scan-LATEST.zip
        rm pipeline-scan-LATEST.zip
    fi
    
    # Run pipeline scan
    java -jar pipeline-scan.jar \
        --veracode_api_id="${vid:-$VERACODE_API_ID}" \
        --veracode_api_key="${vkey:-$VERACODE_API_KEY}" \
        --file="target/${PROJECT_NAME}-*.jar" \
        --app_id="$APP_NAME" \
        --project_name="$PROJECT_NAME" \
        --project_url="https://github.com/example/spring-crud-h2" \
        --project_ref="main" \
        --issue_details=true \
        --summary_display=true \
        --json_display=true
        
    print_success "Pipeline scan completed"
}

# Function to check scan results
check_scan_results() {
    local vid="$1"
    local vkey="$2"
    
    print_info "Checking scan results..."
    
    mvn com.veracode.vosp.api.wrappers:vosp-api-wrappers-java:detailedreport \
        -Dveracode.vid="${vid:-$VERACODE_API_ID}" \
        -Dveracode.vkey="${vkey:-$VERACODE_API_KEY}" \
        -Dveracode.app.name="$APP_NAME"
        
    print_success "Scan results retrieved"
}

# Function to display help
show_help() {
    echo "Veracode Security Scanning Script"
    echo ""
    echo "Usage: $0 [COMMAND] [OPTIONS]"
    echo ""
    echo "Commands:"
    echo "  build                    Build the Spring Boot application"
    echo "  dependency-check         Run OWASP dependency vulnerability check"
    echo "  sbom                     Generate Software Bill of Materials"
    echo "  static-scan              Upload for Veracode static analysis"
    echo "  pipeline-scan            Run Veracode pipeline scan"
    echo "  check-results            Check scan results"
    echo "  full-scan                Run complete security scanning workflow"
    echo "  help                     Show this help message"
    echo ""
    echo "Options:"
    echo "  -v, --vid VID           Veracode API ID"
    echo "  -k, --vkey VKEY         Veracode API Key"
    echo "  -h, --help              Show this help message"
    echo ""
    echo "Environment Variables:"
    echo "  VERACODE_API_ID         Veracode API ID (alternative to -v)"
    echo "  VERACODE_API_KEY        Veracode API Key (alternative to -k)"
    echo ""
    echo "Examples:"
    echo "  $0 build"
    echo "  $0 dependency-check"
    echo "  $0 static-scan -v YOUR_VID -k YOUR_VKEY"
    echo "  $0 full-scan"
}

# Parse command line arguments
COMMAND=""
VID=""
VKEY=""

while [[ $# -gt 0 ]]; do
    case $1 in
        build|dependency-check|sbom|static-scan|pipeline-scan|check-results|full-scan|help)
            COMMAND="$1"
            shift
            ;;
        -v|--vid)
            VID="$2"
            shift 2
            ;;
        -k|--vkey)
            VKEY="$2"
            shift 2
            ;;
        -h|--help)
            show_help
            exit 0
            ;;
        *)
            print_error "Unknown option: $1"
            show_help
            exit 1
            ;;
    esac
done

# Main execution logic
case "$COMMAND" in
    build)
        check_prerequisites
        build_application
        ;;
    dependency-check)
        check_prerequisites
        run_dependency_check
        ;;
    sbom)
        check_prerequisites
        build_application
        generate_sbom
        ;;
    static-scan)
        check_prerequisites
        build_application
        upload_for_static_scan "$VID" "$VKEY"
        ;;
    pipeline-scan)
        check_prerequisites
        build_application
        run_pipeline_scan "$VID" "$VKEY"
        ;;
    check-results)
        check_prerequisites
        check_scan_results "$VID" "$VKEY"
        ;;
    full-scan)
        check_prerequisites
        print_info "Starting full security scanning workflow..."
        build_application
        run_dependency_check
        generate_sbom
        print_info "Local scanning completed. For Veracode cloud scanning, run:"
        print_info "$0 static-scan -v YOUR_VID -k YOUR_VKEY"
        ;;
    help|"")
        show_help
        ;;
    *)
        print_error "Unknown command: $COMMAND"
        show_help
        exit 1
        ;;
esac