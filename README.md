# Sistema de Gestión Médica

Backend REST académico en Java 17 y Spring Boot. Aplica una arquitectura por capas y el patrón Facade: `ClinicaFacade` coordina pacientes, agenda, historia clínica, prescripciones y laboratorio. Los datos clínicos y las cuentas se guardan en PostgreSQL mediante Spring Data JPA; los tokens de sesión son temporales y se invalidan al reiniciar.

## Requisitos y ejecución local

- Java 17
- Conexión a Internet en la primera ejecución del wrapper para descargar Maven y las dependencias

No necesitas instalar Maven globalmente. El Maven Wrapper está configurado en el repositorio para usar Maven 3.9.9:

```powershell
.\mvnw.cmd spring-boot:run
```

```bash
./mvnw spring-boot:run
```

Configura una base PostgreSQL y proporciona `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD` en el entorno. Por ejemplo, en desarrollo local puedes usar `jdbc:postgresql://localhost:5432/gestion_medica`. No guardes credenciales reales en el repositorio. Hibernate usa `ddl-auto=update` para incorporar el esquema sin recrear las tablas ni borrar los datos al iniciar.

La API queda en `http://localhost:8080`. Para ejecutar las pruebas, usa `.\mvnw.cmd test` en Windows o `./mvnw test` en Linux. Si al copiar el proyecto a Linux se pierde el permiso ejecutable del script, configúralo con `chmod +x mvnw`.

El wrapper está en `mvnw`, `mvnw.cmd` y `.mvn/wrapper/`; `maven-wrapper.properties` fija la versión de Maven 3.9.9. Para regenerarlo o completar los archivos sin Maven global, descarga y descomprime una distribución temporal de Maven, y ejecuta el plugin oficial desde esa distribución:

```powershell
.\ruta\apache-maven\bin\mvn.cmd org.apache.maven.plugins:maven-wrapper-plugin:3.3.4:wrapper -Dmaven=3.9.9 -Dtype=bin
```

```bash
./ruta/apache-maven/bin/mvn org.apache.maven.plugins:maven-wrapper-plugin:3.3.4:wrapper -Dmaven=3.9.9 -Dtype=bin
```

El tipo `bin` incluye el JAR de arranque del wrapper. Después de generarlo, se construye y prueba con los scripts `mvnw` del proyecto y ya no hace falta conservar esa distribución temporal de Maven.

La aplicación crea cinco médicos en Cardiología, Pediatría y Dermatología, y tres pacientes de demostración (`p-demo`, `p-demo-2`, `p-demo-3`) con citas, consultas, prescripciones y resultados de laboratorio ficticios. La carga es idempotente: no vuelve a insertar las claves demo existentes. Para obtener acceso de demostración, registra la cuenta con `POST /api/clinica/auth/register`, usando `patientId: "p-demo"`, `email: "demo@clinica.test"` y una contraseña propia de al menos ocho caracteres.

## Autenticación

El registro de pacientes y la consulta de médicos son públicos. Para acceder a historia, citas, prescripciones, laboratorio y horarios se requiere `Authorization: Bearer <token>`. El token se devuelve al registrar o iniciar sesión. Las operaciones de paciente validan que el identificador del cuerpo o la ruta coincida con el paciente autenticado. Las contraseñas se guardan usando PBKDF2-HMAC-SHA256 con sal aleatoria; los tokens son opacos y se mantienen en memoria.

## Endpoints

| Método | Ruta | Acceso |
|---|---|---|
| POST | `/api/clinica/paciente` | Público |
| POST | `/api/clinica/auth/register` | Público |
| POST | `/api/clinica/auth/login` | Público |
| GET | `/api/clinica/medicos?especialidad=Cardiología` | Público |
| GET | `/api/clinica/medicos/{doctorId}/horarios` | Autenticado |
| POST | `/api/clinica/cita` | Propietario autenticado |
| DELETE | `/api/clinica/citas/{id}` | Propietario autenticado |
| GET | `/api/clinica/historia/{pacienteId}` | Propietario autenticado |
| POST | `/api/clinica/prescripcion` | Propietario autenticado |
| POST | `/api/clinica/laboratorio` | Propietario autenticado |
| GET | `/actuator/health` | Público |

Las respuestas de error usan JSON con `message` y `status`. Se aplican validaciones de campos, documentos duplicados, agenda, medicamentos incompatibles y exámenes disponibles.

## Ejemplos

Crear paciente:

```json
{"name":"María Ejemplo","document":"CC-10001","email":"maria@example.test","age":34,"allergies":["penicilina"]}
```

Registrar acceso (se usa el `id` devuelto al crear el paciente):

```json
{"patientId":"<patient-id>","email":"maria@example.test","password":"una-clave-segura"}
```

Agendar cita (requiere token y una fecha ISO 8601 devuelta por el endpoint de horarios del médico):

```json
{"patientId":"<patient-id>","specialty":"Pediatría","date":"<fecha-hora-disponible>"}
```

Crear prescripción:

```json
{"patientId":"<patient-id>","medications":[{"name":"Paracetamol","dose":"500 mg","durationDays":3}]}
```

Solicitar laboratorios:

```json
{"patientId":"<patient-id>","exams":["hemograma","glicemia","perfil lipídico"]}
```

## Railway

Railway puede construir el proyecto con Maven y ejecutar el artefacto Spring Boot. Configura `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD` con las variables de la base PostgreSQL (sin incorporar sus valores en el código). Si Railway proporciona `DATABASE_URL` con esquema `postgres://`, configura una variable `SPRING_DATASOURCE_URL` con formato JDBC (`jdbc:postgresql://...`) y sus credenciales separadas. El servidor lee `PORT` (por defecto 8080) y `CORS_ALLOWED_ORIGINS` (por defecto `http://localhost:3000,http://localhost:5173`). Configura `CORS_ALLOWED_ORIGINS` con el origen de Vercel, por ejemplo `https://tu-frontend.vercel.app`; no agregues rutas a ese valor. La configuración de arranque está en `Procfile`; el endpoint de salud es `/actuator/health`.

Los tokens se invalidan al reiniciar. Los resultados de laboratorio y pacientes incluidos son ficticios y sirven únicamente para pruebas y demostración.
