# Enterprise AI Knowledge Assistant using RAG

An AI-powered document question-answering application built using Java, Spring Boot, Gemini API, and ChromaDB.

The application allows users to upload PDF documents and ask questions based on their content. It uses Retrieval-Augmented Generation (RAG) to retrieve relevant document information and generate context-aware answers using Google's Gemini model.

## Features

- Upload and process multiple PDF documents.
- Extract text from PDFs using Apache PDFBox.
- Split extracted text into overlapping chunks.
- Generate vector embeddings using Gemini Embedding API.
- Store and retrieve embeddings using ChromaDB.
- Perform semantic similarity search.
- Generate context-aware answers using Gemini.
- Display retrieved document sources with chunk references.
- Maintain chat history and previous conversations.
- Store documents, chunks, and chat history in PostgreSQL.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 17 | Backend programming |
| Spring Boot | REST API development |
| Gemini API | AI-generated responses |
| Gemini Embedding API | Text embeddings |
| ChromaDB | Vector database |
| PostgreSQL | Relational database |
| Apache PDFBox | PDF text extraction |
| HTML, CSS, JavaScript | Frontend |
| Maven | Dependency management |
| Docker | Database and service containers |
| Postman | API testing |

## Application Architecture

1. **PDF Upload:** User uploads a PDF document.
2. **Text Extraction:** Apache PDFBox extracts the document text.
3. **Text Chunking:** Extracted text is divided into smaller overlapping chunks.
4. **Embedding Generation:** Gemini Embedding API converts chunks into numerical vectors.
5. **Vector Storage:** ChromaDB stores embeddings with document metadata.
6. **Semantic Search:** User questions are converted into embeddings and matched against relevant document chunks.
7. **Response Generation:** Gemini generates an answer using the retrieved context.
8. **Source References:** The application returns relevant document sources with the answer.
9. **Chat History:** Conversations and messages are stored in PostgreSQL.

## Project Structure

```text
enterprise-ai-knowledge-assistant/
│
├── src/
│   ├── main/
│   │   ├── java/com/khushi/knowledgeassistant/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── entity/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   │
│   │   └── resources/
│   │       ├── static/
│   │       ├── db/
│   │       └── application.properties
│   │
│   └── test/
│
├── docker-compose.yml
├── pom.xml
├── .env
├── .gitignore
└── README.md
```

## Prerequisites

- Java 17 or higher
- Docker Desktop
- Git
- Gemini API Key

## Configuration

Create a `.env` file in the project root directory and configure:

```properties
POSTGRES_DB=knowledge_assistant
POSTGRES_USER=rag_user
POSTGRES_PASSWORD=your_password
POSTGRES_PORT=5433

CHROMA_PORT=8000

GEMINI_API_KEY=your_gemini_api_key
```

Never commit your actual API key or `.env` file to GitHub.

## How to Run

**1. Clone the repository**

```bash
git clone YOUR_GITHUB_REPOSITORY_URL
```

**2. Navigate to the project directory**

```bash
cd enterprise-ai-knowledge-assistant
```

**3. Start Docker services**

```bash
docker compose up -d
```

**4. Run the Spring Boot application**

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

**5. Open the application**

```text
http://localhost:8080
```

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/pdf/upload` | Upload and index a PDF |
| POST | `/api/search` | Ask questions using RAG |
| POST | `/api/chat` | Generate an AI response |
| GET | `/api/conversations` | Retrieve chat history |
| GET | `/api/conversations/{id}/messages` | Retrieve conversation messages |
| DELETE | `/api/conversations/{id}` | Delete a conversation |
| POST | `/api/vectors/store` | Store document vectors |
| POST | `/api/vectors/recover/{documentId}` | Recover missing vectors |

## Future Improvements

- User authentication and authorization.
- Improved document-level access control.
- Better retrieval ranking and relevance filtering.
- Automated testing.
- Cloud deployment.
- Improved error handling and monitoring.

## Author

**Khushi Sehgal**

B.Tech Computer Science Engineering  
Cybersecurity & Privacy

GitHub: Add your GitHub profile link  
LinkedIn: https://www.linkedin.com/in/khushi-sehgal-72a9aa286/