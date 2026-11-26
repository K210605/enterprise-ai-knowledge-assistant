# Enterprise AI Knowledge Assistant using RAG

An AI-powered document question-answering application built using **Java 17, Spring Boot, Google Gemini API, and ChromaDB**.

This application enables users to upload multiple PDF documents and ask natural-language questions based on their content. It uses Retrieval-Augmented Generation (RAG) to retrieve relevant information from uploaded documents and generate context-aware responses using Google's Gemini model.

## Key Features

- Upload and process multiple PDF documents.
- Extract PDF text using Apache PDFBox.
- Split extracted text into overlapping chunks.
- Generate vector embeddings using Gemini Embedding API.
- Store and retrieve embeddings using ChromaDB.
- Perform semantic similarity search.
- Generate context-aware answers using Gemini.
- Display retrieved document sources and chunk references.
- Maintain chat history and previous conversations.
- Store documents, chunks, and chat history in PostgreSQL.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 17 | Backend programming |
| Spring Boot | REST API development |
| Google Gemini API | AI-generated responses |
| Gemini Embedding API | Text embeddings |
| ChromaDB | Vector database |
| PostgreSQL | Relational database |
| Apache PDFBox | PDF text extraction |
| HTML, CSS, JavaScript | Frontend |
| Maven | Dependency management |
| Docker | Database and service containers |
| Postman | API testing |

## System Architecture

The application follows a Retrieval-Augmented Generation (RAG) workflow.

### Document Ingestion Pipeline

1. **PDF Upload:** User uploads a PDF document.
2. **Text Extraction:** Apache PDFBox extracts text from the PDF.
3. **Text Chunking:** Extracted text is divided into smaller overlapping chunks.
4. **Embedding Generation:** Gemini Embedding API converts text chunks into numerical vectors.
5. **Vector Storage:** ChromaDB stores embeddings along with document metadata.

### Question Answering Pipeline

1. **User Query:** User submits a natural-language question.
2. **Query Embedding:** Gemini Embedding API converts the question into a vector.
3. **Semantic Search:** ChromaDB retrieves relevant document chunks based on vector similarity.
4. **Context Preparation:** Retrieved chunks are combined into context.
5. **Response Generation:** Gemini generates an answer using the retrieved context.
6. **Source References:** Relevant document sources are returned with the answer.
7. **Chat History:** Conversations, messages, and source references are persisted in PostgreSQL.

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
├── .env.example
├── .gitignore
├── mvnw
├── mvnw.cmd
└── README.md
```

## Prerequisites

Before running the application, install:

- Java 17 or higher
- Docker Desktop
- Git
- Google Gemini API key

## Environment Configuration

Create a `.env` file in the project root directory.

Configure the following environment variables:

```properties
POSTGRES_DB=knowledge_assistant
POSTGRES_USER=rag_user
POSTGRES_PASSWORD=your_password
POSTGRES_PORT=5433

CHROMA_PORT=8000

GEMINI_API_KEY=your_gemini_api_key
```

**Security Note:** Never commit your actual `.env` file or Gemini API key to GitHub. Use `.env.example` as a configuration template.

## How to Run

### 1. Clone the repository

```bash
git clone https://github.com/K210605/enterprise-ai-knowledge-assistant.git
```

### 2. Navigate to the project directory

```bash
cd enterprise-ai-knowledge-assistant
```

### 3. Configure environment variables

Create your `.env` file using the configuration shown above.

### 4. Start Docker services

```bash
docker compose up -d
```

### 5. Run the Spring Boot application

**Windows:**

```powershell
.\mvnw.cmd spring-boot:run
```

### 6. Open the application

Visit:

```text
http://localhost:8080
```

## REST API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/pdf/upload` | Upload and index a PDF document |
| POST | `/api/search` | Ask questions using RAG |
| POST | `/api/chat` | Generate an AI response |
| GET | `/api/conversations` | Retrieve conversations |
| GET | `/api/conversations/{id}/messages` | Retrieve conversation messages |
| DELETE | `/api/conversations/{id}` | Delete a conversation |
| POST | `/api/vectors/store` | Store document vectors |
| POST | `/api/vectors/recover/{documentId}` | Recover missing document vectors |

## Future Enhancements

- User authentication and authorization.
- Document-level access control.
- Improved retrieval ranking and relevance filtering.
- Automated testing.
- Cloud deployment.
- Enhanced error handling and monitoring.

## Author

**Khushi Sehgal**

B.Tech – Computer Science Engineering  
Cybersecurity & Privacy

- GitHub: https://github.com/K210605
- LinkedIn: https://www.linkedin.com/in/khushi-sehgal-72a9aa286/