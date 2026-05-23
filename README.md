<div align="center">

<img src="https://img.shields.io/badge/MailCraft-Backend-6366f1?style=for-the-badge&logo=springboot&logoColor=white" />

# MailCraft Backend

**Production-grade REST API powering AI email reply generation**  
Built with Spring Boot 4.0 · Google Gemini 2.5 Flash · Java 25

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0-6DB33F?style=flat-square&logo=springboot&logoColor=white)](https://spring.io/)
[![Java](https://img.shields.io/badge/Java-25-ED8B00?style=flat-square&logo=openjdk&logoColor=white)](https://www.java.com/)
[![Gemini AI](https://img.shields.io/badge/Gemini-2.5%20Flash-8B5CF6?style=flat-square&logo=google&logoColor=white)](https://ai.google.dev/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow?style=flat-square)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen?style=flat-square)](https://github.com/omshewalegit/MailCraft-backend/pulls)

</div>

---

## 📌 Overview

MailCraft Backend is a lightweight, high-performance REST API that serves as the AI engine for the [MailCraft Chrome Extension](https://github.com/omshewalegit/MailCraft-extension).

It accepts raw email content + tone preference, constructs a precision-engineered prompt, calls the **Google Gemini 2.5 Flash** model, and returns a clean, professional email reply — no markdown, no filler, no subject lines. Just a reply that's ready to send.

---

## ⚡ API Reference

### `POST /api/email/generate`

Generate an AI-powered email reply.

**Request Body**
```json
{
  "emailContent": "Dear Om, We are pleased to inform you...",
  "tone": "professional"
}
```

**Response** `200 OK`
```
Dear [Sender],

Thank you for reaching out. I appreciate the update regarding...

Best regards,
[Your Name]
```

**Available Tones**

| Tone | Description |
|------|-------------|
| `professional` | Polished, structured, business-appropriate |
| `formal` | Respectful and measured — for executives or legal matters |
| `friendly` | Warm and approachable, conversational |
| `apologetic` | Empathetic, sincere, takes responsibility |
| `assertive` | Direct and confident, states position clearly |
| `concise` | Maximum 2 paragraphs, every word earns its place |

---

## 🏗️ Architecture

```
Chrome Extension (content-script.js)
         │
         │  POST /api/email/generate
         ▼
┌─────────────────────────────────┐
│   EmailGeneratorController      │  ← @RestController, @CrossOrigin
│   /api/email                    │
└──────────────┬──────────────────┘
               │
               ▼
┌─────────────────────────────────┐
│   EmailGeneratorService         │  ← Core business logic
│                                 │
│   1. buildPrompt()              │  ← Prompt engineering
│   2. WebClient POST             │  ← Gemini API call
│   3. extractResponseContent()   │  ← JSON parsing
│   4. cleanResponse()            │  ← Output sanitization
└──────────────┬──────────────────┘
               │
               ▼
┌─────────────────────────────────┐
│   Google Gemini 2.5 Flash API   │
│   /v1beta/models/gemini-2.5-    │
│   flash:generateContent         │
└─────────────────────────────────┘
```

---

## 🛠️ Tech Stack

| Component | Technology | Purpose |
|-----------|-----------|---------|
| Framework | Spring Boot 4.0 | REST API |
| Language | Java 25 | Core language |
| HTTP Client | Spring WebFlux WebClient | Async Gemini API calls |
| AI Model | Google Gemini 2.5 Flash | Email generation |
| JSON Parsing | Jackson ObjectMapper | Parse Gemini response |
| Build Tool | Maven | Dependency management |

---

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven
- Gemini API Key → [Get it free here](https://ai.google.dev/)

---

### 1. Clone the repository

```bash
git clone https://github.com/omshewalegit/MailCraft-backend.git
cd MailCraft-backend/email-writer-sb
```

---

### 2. Configure environment

Create `src/main/resources/application.properties`:

```properties
gemini.api.url=https://generativelanguage.googleapis.com
gemini.api.key=YOUR_GEMINI_API_KEY_HERE
server.port=8080
```

> ⚠️ Never commit your API key. `application.properties` is in `.gitignore`.  
> Use `application.properties.example` as reference.

---

### 3. Run the server

```bash
./mvnw spring-boot:run
```

Server starts at → `http://localhost:8080`

---

### 4. Test the API

```bash
curl -X POST http://localhost:8080/api/email/generate \
  -H "Content-Type: application/json" \
  -d '{
    "emailContent": "Hi, can we schedule a meeting tomorrow?",
    "tone": "professional"
  }'
```

---

## 📁 Project Structure

```
email-writer-sb/
├── src/
│   └── main/
│       ├── java/com/email_writer_sb/
│       │   ├── EmailGeneratorController.java   ← REST endpoint + CORS
│       │   ├── EmailGeneratorService.java       ← Prompt + Gemini logic
│       │   ├── EmailRequest.java                ← Request DTO
│       │   ├── EmailWriterSbApplication.java    ← Spring Boot entry point
│       │   └── WebClientConfiguration.java      ← WebClient bean
│       └── resources/
│           ├── application.properties           ← (gitignored — add manually)
│           └── application.properties.example   ← Template
├── pom.xml                                      ← Maven dependencies
└── mvnw / mvnw.cmd                              ← Maven wrapper
```

---

## 🔐 Security Notes

- `application.properties` is **gitignored** — API key is never exposed
- CORS configured for local development — restrict origins before production deployment
- No user data is stored — email content is processed in-memory and discarded

---

## 🔗 Related Repositories

| Repo | Description |
|------|-------------|
| [MailCraft Extension](https://github.com/omshewalegit/MailCraft-extension) | Chrome Extension — Gmail UI injection |

---

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch `git checkout -b feature/amazing-feature`
3. Commit your changes `git commit -m 'Add amazing feature'`
4. Push to the branch `git push origin feature/amazing-feature`
5. Open a Pull Request

---

## 👤 Author

**Om Shewale**  
[![GitHub](https://img.shields.io/badge/GitHub-omshewalegit-181717?style=flat-square&logo=github)](https://github.com/omshewalegit)

---

<div align="center">

**If this project helped you, give it a ⭐**

*Built with ❤️ by Om Shewale*

</div>
