# Taller Cloud Native: RabbitMQ + Spring Boot + Azure B2C + Docker + CI/CD

Proyecto de ejemplo para una clase de **Desarrollo Cloud Native**.

Incluye:

- `producer-service`: microservicio Spring Boot protegido con **Spring Security Resource Server** y JWT de **Azure AD B2C**.
- `consumer-service`: microservicio Spring Boot que consume mensajes desde RabbitMQ con `@RabbitListener`.
- `rabbitmq`: contenedor RabbitMQ separado, con consola de administración.
- Despliegue en **EC2** usando Docker Compose.
- CI/CD con GitHub Actions:
  - `deploy-rabbitmq.yml`: workflow manual para construir y desplegar RabbitMQ.
  - `deploy-microservices.yml`: workflow automático al hacer push a `main` para construir y desplegar productor y consumidor.

## Arquitectura

```text
Usuario / Postman / Frontend
        |
        | Authorization: Bearer <JWT Azure B2C>
        v
+-----------------------------+
| producer-service            |
| Spring Security             |
| Valida JWT Azure B2C        |
| Publica evento en RabbitMQ  |
+-----------------------------+
        |
        v
+-----------------------------+
| RabbitMQ                    |
| Exchange + Queue + DLQ      |
+-----------------------------+
        |
        v
+-----------------------------+
| consumer-service            |
| @RabbitListener             |
| Procesa evento              |
+-----------------------------+
```

El JWT se valida en el **producer-service**, porque es el borde HTTP del sistema. El `consumer-service` no autentica usuarios finales; procesa eventos internos que fueron publicados por un servicio que ya validó el token.

## Estructura

```text
.
├── .github/workflows
│   ├── deploy-microservices.yml
│   └── deploy-rabbitmq.yml
├── consumer-service
├── deploy
│   ├── compose.microservices.yml
│   └── compose.rabbitmq.yml
├── docker-compose.local.yml
├── producer-service
└── rabbitmq
```

## Configuración de Azure B2C

El producer usa estas variables:

```env
AZURE_B2C_ISSUER_URI=https://<tenant>.b2clogin.com/<tenant-id>/v2.0/
AZURE_B2C_JWK_SET_URI=https://<tenant>.b2clogin.com/<tenant>.onmicrosoft.com/discovery/v2.0/keys?p=<policy>
AZURE_B2C_AUDIENCE=<client-id-o-app-id-uri-de-la-api>
```

Ejemplo basado en tu proyecto anterior:

```env
AZURE_B2C_ISSUER_URI=https://taller2cloudnative1ggrobier.b2clogin.com/3522b943-1091-496c-bbf3-f3b85571fc26/v2.0/
AZURE_B2C_JWK_SET_URI=https://taller2cloudnative1ggrobier.b2clogin.com/taller2cloudnative1ggrobier.onmicrosoft.com/discovery/v2.0/keys?p=b2c_1_flujo_usuario_taller
AZURE_B2C_AUDIENCE=<aud-del-access-token>
```

> Importante: usa un **access token** para llamar a la API, no un ID token.

## Ejecución local

Copia el archivo de ejemplo:

```bash
cp .env.example .env
```

Edita `.env` con tus valores reales de Azure B2C.

Levanta todo:

```bash
docker compose -f docker-compose.local.yml --env-file .env up --build
```

RabbitMQ Management:

```text
http://localhost:15672
usuario: admin
password: admin123
```

Endpoint del producer:

```bash
curl -X POST http://localhost:8081/api/pedidos \
  -H "Authorization: Bearer <ACCESS_TOKEN_AZURE_B2C>" \
  -H "Content-Type: application/json" \
  -d '{
    "pedidoId": "PED-001",
    "producto": "Notebook",
    "cantidad": 1
  }'
```

Respuesta esperada:

```json
{
  "mensaje": "Pedido recibido y enviado a procesamiento asincrono",
  "pedidoId": "PED-001",
  "eventId": "..."
}
```

En los logs del `consumer-service` deberías ver el mensaje procesado.

Para probar la DLQ por error simulado, habilita antes la variable:

```env
CONSUMER_SIMULATE_ERROR_FOR_PRODUCT=true
```

Luego envía un producto con el texto `ERROR`:

```bash
curl -X POST http://localhost:8081/api/pedidos \
  -H "Authorization: Bearer <ACCESS_TOKEN_AZURE_B2C>" \
  -H "Content-Type: application/json" \
  -d '{
    "pedidoId": "PED-ERROR",
    "producto": "ERROR",
    "cantidad": 1
  }'
```

Después de los reintentos, el mensaje debería ir a `pedidos.dlq`.

## Preparar EC2

En una EC2 Ubuntu, instala Docker:

```bash
sudo apt-get update
sudo apt-get install -y ca-certificates curl gnupg
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
sudo chmod a+r /etc/apt/keyrings/docker.gpg

echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo $VERSION_CODENAME) stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
sudo usermod -aG docker $USER
```

Cierra sesión y vuelve a entrar para aplicar el grupo `docker`.

Puertos recomendados para el Security Group:

- `8081`: API del producer, idealmente restringida a tu IP o al balanceador.
- `8082`: consumer, opcional; solo si quieres exponer `/actuator/health`.
- `15672`: consola RabbitMQ, solo temporalmente y restringida a tu IP.
- `5672`: no debería exponerse públicamente; los microservicios se conectan por red Docker interna.

## Secrets de GitHub Actions

Crea estos secrets en el repositorio:

```text
DOCKERHUB_USERNAME
DOCKERHUB_TOKEN

EC2_HOST
EC2_USER
EC2_SSH_KEY

RABBITMQ_USERNAME
RABBITMQ_PASSWORD

AZURE_B2C_ISSUER_URI
AZURE_B2C_JWK_SET_URI
AZURE_B2C_AUDIENCE
```

`deploy-rabbitmq.yml` ahora solo necesita `EC2_HOST`, `EC2_USER`, `EC2_SSH_KEY`, `RABBITMQ_USERNAME` y `RABBITMQ_PASSWORD`. Los secretos de DockerHub siguen siendo necesarios para `deploy-microservices.yml`.

## Flujo de CI/CD

### 1. Desplegar RabbitMQ manualmente

Ir a:

```text
Actions → Deploy RabbitMQ → Run workflow
```

Ese workflow:

1. Copia `deploy/compose.rabbitmq.yml` a EC2.
2. Crea la red Docker `cloudnative-net`.
3. Descarga la imagen oficial `rabbitmq:4-management` desde Docker Hub.
4. Despliega RabbitMQ.
5. Usa la carpeta `/home/ec2-user/rabbitmq` en la instancia.
6. Copia `compose.rabbitmq.yml` directamente dentro de esa carpeta.

### 2. Desplegar microservicios automáticamente

Cada push a `main` ejecuta:

```text
Build Producer Service
Build Consumer Service
Deploy Microservices on EC2
```

El workflow:

1. Compila y prueba el producer.
2. Construye y sube la imagen `producer-service`.
3. Compila y prueba el consumer.
4. Construye y sube la imagen `consumer-service`.
5. Crea en la EC2 las carpetas `/home/ec2-user/micro-servicio1`, `/home/ec2-user/micro-servicio2` y `/home/ec2-user/rabbitmq`.
6. Copia `compose.microservices.yml` directamente dentro de `/home/ec2-user/micro-servicio1`.
7. Despliega ambos microservicios con Docker Compose desde `/home/ec2-user/micro-servicio1`.

## Endpoints útiles

Producer:

```text
GET  /actuator/health
POST /api/pedidos
```

Consumer:

```text
GET /actuator/health
```

RabbitMQ:

```text
http://<EC2_PUBLIC_IP>:15672
```

## Notas de seguridad

- No publiques tokens JWT dentro de RabbitMQ.
- El producer valida el token y publica un evento interno con los datos mínimos del usuario.
- Usa usuarios y contraseñas fuertes para RabbitMQ.
- No expongas `5672` públicamente.
- Restringe `15672` a tu IP.
- En producción, usa TLS para RabbitMQ.
