# Veracode Security Integration

This document explains the Veracode security scanning integration for the Spring Boot CRUD application, including setup, configuration, and usage instructions.

## 🔒 Overview

Veracode integration provides comprehensive security scanning capabilities:

- **Static Application Security Testing (SAST)** - Source code vulnerability analysis
- **Software Composition Analysis (SCA)** - Third-party dependency vulnerability scanning
- **Pipeline Integration** - Fast feedback for CI/CD workflows
- **OWASP Dependency Check** - Local vulnerability scanning
- **Software Bill of Materials (SBOM)** - Component inventory and tracking

## 🚀 Quick Start

### Prerequisites

1. **Veracode Account**: Active Veracode subscription with API access
2. **API Credentials**: Veracode API ID and API Key
3. **Java 17+**: Required for building the application
4. **Maven 3.6+**: Build tool

### Local Setup

1. **Set Environment Variables**:
   ```bash
   export VERACODE_API_ID="your-api-id"
   export VERACODE_API_KEY="your-api-key"
   ```

2. **Run Local Security Scan**:
   ```bash
   # Make script executable (first time only)
   chmod +x scripts/veracode-scan.sh
   
   # Run complete security workflow
   ./scripts/veracode-scan.sh full-scan
   ```

3. **View Results**:
   - OWASP Report: `target/dependency-check-report/`
   - SBOM: `target/bom.json`

## 📋 Configuration Files

### 1. Maven Configuration (pom.xml)

The project includes three security-related Maven plugins:

#### Veracode Static Analysis Plugin
```xml
<plugin>
    <groupId>com.veracode.vosp.api.wrappers</groupId>
    <artifactId>vosp-api-wrappers-java</artifactId>
    <version>23.8.12.0</version>
    <configuration>
        <vid>${veracode.vid}</vid>
        <vkey>${veracode.vkey}</vkey>
        <appname>${veracode.app.name}</appname>
        <createprofile>true</createprofile>
        <criticality>VeryHigh</criticality>
        <sandboxname>development</sandboxname>
    </configuration>
</plugin>
```

#### CycloneDX SBOM Plugin
```xml
<plugin>
    <groupId>org.cyclonedx</groupId>
    <artifactId>cyclonedx-maven-plugin</artifactId>
    <version>2.7.9</version>
    <configuration>
        <projectType>library</projectType>
        <schemaVersion>1.4</schemaVersion>
        <outputFormat>json</outputFormat>
    </configuration>
</plugin>
```

#### OWASP Dependency Check Plugin
```xml
<plugin>
    <groupId>org.owasp</groupId>
    <artifactId>dependency-check-maven</artifactId>
    <version>8.4.0</version>
    <configuration>
        <format>ALL</format>
        <suppressionFiles>
            <suppressionFile>owasp-dependency-check-suppressions.xml</suppressionFile>
        </suppressionFiles>
    </configuration>
</plugin>
```

### 2. Veracode Configuration (veracode.properties)

Application-specific Veracode settings:

```properties
# Application Configuration
veracode.app.name=spring-crud-h2-app
veracode.business.criticality=High
veracode.policy=Veracode Recommended SCA + SAST

# Scan Configuration
veracode.scan.include.pattern=*.jar,*.war
veracode.scan.exclude.pattern=*test*.jar,*javadoc*.jar,*sources*.jar
veracode.sandbox.name=development

# Pipeline Integration
veracode.pipeline.scan=true
veracode.pipeline.timeout=30
veracode.pipeline.fail.on.severity=Medium
```

### 3. OWASP Suppressions (owasp-dependency-check-suppressions.xml)

Manage false positives in vulnerability scanning:

```xml
<suppressions xmlns="https://jeremylong.github.io/DependencyCheck/dependency-suppression.1.3.xsd">
    <!-- H2 Database - Development/Testing Only -->
    <suppress>
        <notes>H2 database is used only for development and testing, not in production</notes>
        <packageUrl regex="true">^pkg:maven/com\.h2database/h2@.*$</packageUrl>
        <vulnerabilityName regex="true">.*H2.*</vulnerabilityName>
    </suppress>
</suppressions>
```

## 🛠️ Usage

### Command Line Interface

The `scripts/veracode-scan.sh` script provides easy access to all security scanning features:

```bash
# Available commands
./scripts/veracode-scan.sh help

# Build application
./scripts/veracode-scan.sh build

# Run OWASP dependency check
./scripts/veracode-scan.sh dependency-check

# Generate Software Bill of Materials
./scripts/veracode-scan.sh sbom

# Upload for Veracode static analysis
./scripts/veracode-scan.sh static-scan -v YOUR_VID -k YOUR_VKEY

# Run Veracode pipeline scan
./scripts/veracode-scan.sh pipeline-scan -v YOUR_VID -k YOUR_VKEY

# Check scan results
./scripts/veracode-scan.sh check-results -v YOUR_VID -k YOUR_VKEY

# Run complete security workflow (local only)
./scripts/veracode-scan.sh full-scan
```

### Maven Commands

Direct Maven integration:

```bash
# OWASP Dependency Check
mvn org.owasp:dependency-check-maven:check

# Generate SBOM
mvn org.cyclonedx:cyclonedx-maven-plugin:makeAggregateBom

# Veracode upload (requires credentials)
mvn com.veracode.vosp.api.wrappers:vosp-api-wrappers-java:uploadandscan \
    -Dveracode.vid="$VERACODE_API_ID" \
    -Dveracode.vkey="$VERACODE_API_KEY" \
    -Dveracode.app.name="spring-crud-h2-app"

# Build with Veracode profile
mvn clean package -P veracode
```

## 🔄 CI/CD Integration

### GitHub Actions Workflow

The project includes a comprehensive GitHub Actions workflow (`.github/workflows/veracode-security-scan.yml`) that provides:

#### Automated Triggers
- **Push to main/develop**: Automatic security scanning
- **Pull Requests**: Security validation before merge
- **Scheduled Scans**: Weekly security audits (Sundays at 2 AM UTC)
- **Manual Dispatch**: On-demand scanning with configurable scan types

#### Scan Types
1. **Pipeline Scan**: Fast feedback for development (default)
2. **Static Analysis**: Comprehensive policy-based scanning
3. **Dependency Check**: OWASP vulnerability analysis
4. **All**: Complete security workflow

#### Workflow Features
- ✅ **Parallel Execution**: Multiple scan types run simultaneously
- ✅ **Artifact Management**: Results stored for 30-90 days
- ✅ **Security Summary**: Automated reporting with GitHub Step Summary
- ✅ **Failure Notifications**: Automatic issue creation for security failures
- ✅ **Flexible Configuration**: Environment-specific settings

### Setting Up GitHub Secrets

Required secrets for GitHub Actions:

```bash
VERACODE_API_ID     # Your Veracode API ID
VERACODE_API_KEY    # Your Veracode API Key
```

Add these in GitHub: Settings → Secrets and variables → Actions → New repository secret

### Manual Workflow Execution

1. Go to **Actions** tab in GitHub
2. Select **Veracode Security Scan** workflow
3. Click **Run workflow**
4. Choose scan type:
   - `pipeline`: Fast development feedback
   - `static`: Full policy scan
   - `dependency-check`: OWASP analysis only
   - `all`: Complete security assessment

## 📊 Scan Types Explained

### 1. Veracode Static Analysis (SAST)
- **Purpose**: Comprehensive source code vulnerability analysis
- **Duration**: 30-60 minutes (depending on codebase size)
- **Output**: Policy compliance report, flaw details, remediation guidance
- **Use Case**: Release validation, compliance audits

### 2. Veracode Pipeline Scan
- **Purpose**: Fast feedback for development workflows
- **Duration**: 2-5 minutes
- **Output**: JSON results with vulnerability details
- **Use Case**: CI/CD integration, developer feedback

### 3. Software Composition Analysis (SCA)
- **Purpose**: Third-party dependency vulnerability scanning
- **Duration**: 1-2 minutes
- **Output**: Component vulnerabilities, license information
- **Use Case**: Supply chain security, compliance

### 4. OWASP Dependency Check
- **Purpose**: Local vulnerability scanning (no cloud upload)
- **Duration**: 2-5 minutes
- **Output**: HTML/JSON/XML reports
- **Use Case**: Local development, offline scanning

### 5. Software Bill of Materials (SBOM)
- **Purpose**: Complete component inventory
- **Duration**: 1 minute
- **Output**: CycloneDX JSON format
- **Use Case**: Supply chain transparency, compliance

## 📈 Results and Reporting

### Local Reports

#### OWASP Dependency Check
Location: `target/dependency-check-report/`
- `dependency-check-report.html` - Detailed HTML report
- `dependency-check-report.json` - Machine-readable results
- `dependency-check-report.xml` - XML format for CI integration

#### Software Bill of Materials
Location: `target/bom.json`
- CycloneDX format
- Complete component inventory
- License information
- Vulnerability mappings

### Veracode Platform Reports

#### Static Analysis Results
- **Veracode Platform**: https://analysiscenter.veracode.com/
- **Application Profile**: spring-crud-h2-app
- **Sandbox**: development (for development scans)
- **Report Formats**: PDF, XML, JSON

#### Pipeline Scan Results
- **Local File**: `results.json` (generated after scan)
- **GitHub Artifacts**: Available in workflow runs
- **Integration**: Can be processed by security tools

### GitHub Actions Artifacts

All scan results are automatically uploaded as artifacts:
- **dependency-check-report**: OWASP vulnerability reports
- **pipeline-scan-results**: Veracode pipeline scan JSON
- **sbom**: Software Bill of Materials
- **spring-crud-h2-jar**: Built application for verification

## 🔧 Customization

### Application-Specific Configuration

#### Modify Scan Scope
Edit `pom.xml` to customize what gets scanned:

```xml
<configuration>
    <include>*.jar</include>
    <exclude>*test*.jar,*javadoc*.jar</exclude>
</configuration>
```

#### Adjust Security Policies
Update `veracode.properties`:

```properties
# Change business criticality
veracode.business.criticality=Medium

# Modify failure criteria
veracode.pipeline.fail.on.severity=High

# Update scan frequency
veracode.scan.frequency=monthly
```

#### Suppress False Positives
Edit `owasp-dependency-check-suppressions.xml`:

```xml
<suppress>
    <notes>Justification for suppression</notes>
    <packageUrl regex="true">^pkg:maven/group/artifact@.*$</packageUrl>
    <cve>CVE-YYYY-NNNNN</cve>
</suppress>
```

### CI/CD Customization

#### Modify Scan Triggers
Edit `.github/workflows/veracode-security-scan.yml`:

```yaml
on:
  push:
    branches: [ main, develop, release/* ]  # Add release branches
  schedule:
    - cron: '0 6 * * 1'  # Monday 6 AM instead of Sunday 2 AM
```

#### Adjust Failure Handling
```yaml
# Make pipeline scan fail the build on High severity
- name: Veracode Pipeline Scan
  with:
    fail_build: true  # Change from false to true
```

#### Custom Notifications
Add Slack/Teams notifications:

```yaml
- name: Notify Slack
  if: failure()
  uses: 8398a7/action-slack@v3
  with:
    status: failure
    webhook_url: ${{ secrets.SLACK_WEBHOOK }}
```

## 🚨 Security Best Practices

### Credential Management
- ✅ **Never commit credentials** to version control
- ✅ **Use environment variables** for local development
- ✅ **Use GitHub Secrets** for CI/CD
- ✅ **Rotate API keys** regularly
- ✅ **Use least privilege** API permissions

### Scan Configuration
- ✅ **Exclude test artifacts** from production scans
- ✅ **Configure appropriate criticality** levels
- ✅ **Use sandboxes** for development scanning
- ✅ **Set meaningful version names** for tracking
- ✅ **Regular policy reviews** and updates

### Results Management
- ✅ **Review all findings** promptly
- ✅ **Document suppressions** with justifications
- ✅ **Track remediation** progress
- ✅ **Integrate with** issue tracking systems
- ✅ **Regular compliance** audits

## 🆘 Troubleshooting

### Common Issues

#### 1. Authentication Failures
```bash
# Error: Invalid credentials
# Solution: Verify API ID and Key
export VERACODE_API_ID="your-correct-id"
export VERACODE_API_KEY="your-correct-key"
```

#### 2. Build Failures
```bash
# Error: JAR file not found
# Solution: Ensure application builds successfully
mvn clean package -DskipTests
```

#### 3. Dependency Check Timeouts
```bash
# Error: NVD database update timeout
# Solution: Update dependency check database
mvn org.owasp:dependency-check-maven:update-only
```

#### 4. Large Upload Failures
```bash
# Error: File too large for upload
# Solution: Exclude unnecessary files
# Edit pom.xml to refine include/exclude patterns
```

### Debug Mode

Enable verbose logging:

```bash
# Maven debug mode
mvn -X org.owasp:dependency-check-maven:check

# Script debug mode
bash -x scripts/veracode-scan.sh full-scan
```

### Support Resources

- **Veracode Help Center**: https://help.veracode.com/
- **OWASP Dependency Check**: https://owasp.org/www-project-dependency-check/
- **CycloneDX SBOM**: https://cyclonedx.org/
- **GitHub Actions**: https://docs.github.com/en/actions

## 📚 Additional Resources

### Documentation
- [Veracode API Documentation](https://help.veracode.com/r/c_rest_api)
- [Pipeline Scan Documentation](https://help.veracode.com/r/c_about_pipeline_scan)
- [Static Analysis Documentation](https://help.veracode.com/r/c_about_static_analysis)

### Training
- [Veracode University](https://www.veracode.com/veracode-university)
- [Secure Coding Practices](https://owasp.org/www-project-secure-coding-practices-quick-reference-guide/)
- [SBOM Resources](https://www.cisa.gov/sbom)

### Tools Integration
- [IDE Plugins](https://help.veracode.com/r/c_about_ide_plugins)
- [Jenkins Integration](https://help.veracode.com/r/r_jenkins_plugin_overview)
- [Azure DevOps Extension](https://help.veracode.com/r/c_azure_devops_intro)

## 🔗 Related Files

- `pom.xml` - Maven configuration with security plugins
- `veracode.properties` - Veracode application settings
- `owasp-dependency-check-suppressions.xml` - Vulnerability suppressions
- `scripts/veracode-scan.sh` - Command-line security scanning script
- `.github/workflows/veracode-security-scan.yml` - GitHub Actions workflow
- `target/dependency-check-report/` - OWASP scan results (generated)
- `target/bom.json` - Software Bill of Materials (generated)