# Coriander Ingress API

A comprehensive API control system that provides unified permission management and gateway orchestration for organizational information systems integration.

Created by a Taiwanese bastard who favors coriander a lot.

## Overview

Coriander Ingress API (CIA) is designed to integrate and unify organizational information systems through a centralized framework for management, authorization, and information provisioning. It ensures different systems can provide APIs through a common gateway channel, enhancing system availability within organizations and preventing information silos.

The system serves as foundational infrastructure for automation workflow integrations like MCP, N8N, and other enterprise automation tools, further enhancing organizational efficiency and innovation possibilities.

## Architecture

Coriander Ingress API supports two deployment modes:

### CIA (Coriander Ingress API - Host Mode)
- **Central Control Node**: Acts as the master controller managing all API data and permissions
- **Database**: Uses Oracle DB by default for persistent storage
- **Responsibilities**:
    - API resource management
    - Permission and authorization control
    - Configuration distribution to CGC nodes
    - Central monitoring and analytics

### CGC (Coriander Gateway Control - Client Mode)
- **Gateway Control Node**: Works with API Gateway (default: Apache APISIX) in containerized Pod deployment
- **Database**: Uses embedded H2 database for local caching
- **Responsibilities**:
    - Runtime API usage monitoring
    - Local gateway configuration management
    - Real-time API traffic control
    - Data synchronization with CIA

## Key Features

### 🔄 **Unified API Management**
- Centralized API resource registration and discovery
- Consistent API provisioning across organizational systems
- Prevention of information silos through standardized interfaces

### 🛡️ **Comprehensive Permission Control**
- Role-based access control (RBAC)
- API key management with admin/viewer permissions
- Fine-grained authorization policies

### 📊 **Real-time Monitoring & Analytics**
- Runtime API usage tracking
- Performance metrics collection
- Integration with Coriander Log Center (CLC) for advanced analytics

### 🏗️ **Scalable Deployment Architecture**
- Site/Fab hierarchical deployment management
- Support for 1:1 and 1:N CGC-to-Gateway configurations
- Flexible proxy and network configuration

### 🔌 **Extensible Gateway Support**
- Strategy pattern implementation for API Gateway abstraction
- Default Apache APISIX integration
- Pluggable architecture for additional gateway implementations

## Target Users

- **System Developers**: Developers who want to publish their APIs for organizational use
- **API Consumers**: Teams and systems that need to consume APIs from various organizational services
- **Resource Coordinators**: Decision makers who need visibility into system usage patterns for resource optimization
- **DevOps Teams**: Infrastructure teams managing API lifecycle and deployment processes

## Quick Start

### Prerequisites
- Java 11 or higher
- Docker (for CGC deployment)
- Apache APISIX (default gateway)
- Oracle Database (for CIA) or H2 (for CGC)

### CIA (Host Mode) Deployment

1. **Configure Environment Variables**
```bash
export CIA_APP_CONFIG=sample-host
export CIA_SCHEME=http
export PROJECT_EXPOSE_PORT=80
```

2. **Database Setup**
   Configure Oracle database connection in your environment or use H2 for testing.

3. **Start CIA**
```bash
java -jar coriander-ingress-api.jar --spring.profiles.active=sample-host
```

### CGC (Client Mode) Deployment

1. **Configure Environment Variables**
```bash
export CIA_APP_CONFIG=sample-client
export CIA_HOST_DNS=<cia-host>
export CIA_HOST_PORT=80
export CIA_HOST_ADMIN_KEY=admin-key-cia
```

2. **Deploy with API Gateway**
```bash
# Using Docker Compose (recommended)
docker-compose up cgc apisix
```

3. **Verify Deployment**
```bash
curl http://localhost/health
```

## Configuration Guide

### Host Configuration (`application-sample-host.yml`)
Key configuration sections for CIA deployment:

- **Database Settings**: Oracle DB configuration for persistent storage
- **API Keys**: Admin and viewer keys for API access control
- **Default Gateway Settings**: Default ports and schemes for CGC nodes
- **CLC Integration**: Coriander Log Center connection settings

### Client Configuration (`application-sample-client.yml`)
Key configuration sections for CGC deployment:

- **CIA Connection**: Host DNS, port, and authentication settings
- **Gateway Integration**: APISIX configuration and proxy settings
- **Site/Fab Deployment**: Organizational hierarchy definition
- **Log Collection**: HTTP log collection and forwarding settings

## API Gateway Integration

### Default: Apache APISIX
Coriander Ingress API comes with built-in Apache APISIX integration:

- **Admin API Control**: Automatic route and service configuration
- **Real-time Monitoring**: Traffic metrics and performance tracking
- **Security Integration**: Request verification and authentication
- **Log Collection**: Structured logging to CLC

### Custom Gateway Implementation
The system uses a strategy pattern for gateway abstraction, allowing custom implementations:

```java
// Example gateway strategy interface
public interface GatewayStrategy {
    void configureRoute(RouteConfig config);
    void updatePermissions(PermissionConfig permissions);
    MetricsData collectMetrics();
}
```

## Integration with Coriander Log Center (CLC)

CLC is a satellite project that captures and analyzes gateway and management data:

- **Data Collection**: Automatic log forwarding from CGC nodes
- **Analytics**: Usage patterns and performance analysis
- **Alerting**: Configurable monitoring and alerting rules
- **Dashboards**: Grafana integration for visualization

## Deployment Patterns

### Single Site Deployment
```yaml
deploy:
  - site-name: "PRODUCTION"
    fab: ["MAIN"]
    deploy-list:
      - identify: prod-cgc
        client-dns: cgc.prod.local
        client-port: 80
```

### Multi-Site Deployment
```yaml
deploy:
  - site-name: "TAOYUAN"
    fab: ["FAB1", "FAB2", "FAB3"]
  - site-name: "VIRTUAL"
    fab: ["SANDBOX"]
```

## Security Considerations

- **API Key Management**: Secure storage and rotation of API keys
- **Network Security**: Support for proxy configurations and HTTPS
- **Access Control**: Role-based permissions with fine-grained control
- **Audit Logging**: Comprehensive activity logging through CLC

## Monitoring & Observability

### Built-in Metrics
- API request/response times
- Success/failure rates
- Gateway performance metrics
- Resource utilization tracking

### Grafana Dashboards
Pre-configured dashboards for:
- APISIX performance monitoring
- API usage analytics
- System health overview
- Resource utilization trends

## Contributing

We welcome contributions to improve Coriander Ingress API:

1. Fork the repository
2. Create a feature branch
3. Submit a pull request with detailed description
4. Ensure all tests pass and documentation is updated
## License

MIT License.

## Support

For technical support and questions:
- Create an issue in the repository
- Contact the development team
- Refer to the configuration examples and documentation

---

*Coriander Ingress API - Unifying organizational systems through intelligent API management*
