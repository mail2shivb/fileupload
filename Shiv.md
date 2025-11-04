Excellent question — this is one of the most practical yet misunderstood aspects of how file uploads work in AI assistants and LLM-powered copilots (like ChatGPT, GitHub Copilot, Microsoft Copilot, etc.).

Let’s break it down clearly — from architecture to data flow 👇

⸻

🧠 1. Concept: “File Upload” in AI Assistants

When you “upload” a file to an AI copilot, you’re not just sending a file to a storage system — you’re giving the model context.
	•	The file (PDF, DOCX, CSV, code, etc.) is:
	1.	Parsed — converted into plain text or structured tokens.
	2.	Chunked — split into manageable sections (e.g., 1,000 tokens each).
	3.	Embedded — converted into vector embeddings (numerical representations).
	4.	Indexed — stored temporarily in a retrieval database or session memory.
	5.	Referenced — when you ask a question, the system retrieves the most relevant chunks and passes them into the model’s context window.

⸻

⚙️ 2. Behind the Scenes (Step-by-Step Flow)

Here’s the technical data flow that happens during upload:

User → Copilot Interface → File Upload API → Pre-Processor → Embedding Store → LLM Context

Step 1: Upload & Pre-Processing
	•	Your file is uploaded to a secure temporary storage (e.g., Azure Blob, AWS S3, or OpenAI’s internal cache).
	•	The file is scanned (for safety & compliance), then parsed:
	•	PDF → text via PDF parser
	•	DOCX → text via python-docx
	•	CSV → structured data
	•	Code → syntax tree + text

Step 2: Chunking & Embedding
	•	Large documents are split into smaller chunks.
	•	Each chunk is transformed into a vector embedding (e.g., via OpenAI’s text-embedding-3-large).
	•	These embeddings are stored in a temporary vector store (like FAISS, Pinecone, or internal memory).

Step 3: Query-Time Retrieval

When you ask, “Summarize the report I uploaded,”:
	1.	Your query is embedded in the same vector space.
	2.	The system finds the top-N relevant chunks by cosine similarity.
	3.	These chunks are attached to the model’s prompt (in-context learning).
	4.	The LLM answers as if it “knows” the file content.

⸻

🧩 3. File Upload Architecture in LLM Copilots

Here’s a conceptual architecture diagram (text version):

                ┌────────────────────┐
                │   User Interface   │
                │ (Chat / Copilot UI)│
                └───────┬────────────┘
                        │
                        ▼
                ┌────────────────────┐
                │ File Upload Service│
                │ (Temporary Storage)│
                └───────┬────────────┘
                        │
                        ▼
          ┌────────────────────────────┐
          │ Preprocessor & Chunker     │
          │ (extract text / metadata)  │
          └───────────┬────────────────┘
                      │
                      ▼
           ┌─────────────────────────────┐
           │ Embedding Model             │
           │ (e.g., text-embedding-3)    │
           └───────────┬─────────────────┘
                       │
                       ▼
           ┌─────────────────────────────┐
           │ Vector Store (FAISS/Pinecone│
           │ or session memory)          │
           └───────────┬─────────────────┘
                       │
                       ▼
           ┌─────────────────────────────┐
           │ Query Relevance Retriever   │
           │ (similarity search)         │
           └───────────┬─────────────────┘
                       │
                       ▼
           ┌─────────────────────────────┐
           │ LLM (GPT / Claude / Gemini) │
           │ with context window          │
           └─────────────────────────────┘


⸻

🔐 4. Security & Privacy
	•	Files are stored ephemerally (typically deleted after session).
	•	The model itself doesn’t permanently “learn” from your uploads.
	•	Enterprise copilots (like Microsoft 365 Copilot) use tenant-isolated storage (your data stays inside your org’s compliance boundary).

⸻

💡 5. GitHub Copilot vs ChatGPT File Upload

Tool	File Upload Purpose	Processing Method	Data Lifetime
GitHub Copilot	Reads your code context (open files)	Tokenizes local code buffer	Only in-memory
ChatGPT (File Upload)	Lets model “read” document	Parses + embeds	Temporary (session)
Microsoft 365 Copilot	Reads Office files (SharePoint/OneDrive)	Graph + Semantic Index	Persistent per tenant
Custom AI Agent / RAG	Reads any uploaded data	Custom embedding + retrieval	Configurable (e.g., S3 + Pinecone)


⸻

🧠 6. In Developer Terms (If You Build It Yourself)

If you’re coding your own AI assistant or copilot (FastAPI, LangChain, etc.):

from langchain.document_loaders import PyPDFLoader
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain.embeddings import OpenAIEmbeddings
from langchain.vectorstores import FAISS

# 1️⃣ Load
loader = PyPDFLoader("report.pdf")
docs = loader.load()

# 2️⃣ Chunk
splitter = RecursiveCharacterTextSplitter(chunk_size=1000, chunk_overlap=200)
chunks = splitter.split_documents(docs)

# 3️⃣ Embed & Index
embeddings = OpenAIEmbeddings()
vectorstore = FAISS.from_documents(chunks, embeddings)

# 4️⃣ Retrieve during Q&A
query = "Summarize financial performance"
results = vectorstore.similarity_search(query)


⸻

⚡ 7. Summary

Step	What Happens	Purpose
Upload	File → temporary storage	Receive data
Parse	Extract text/content	Convert to model-readable form
Embed	Vector representation	Enable semantic understanding
Retrieve	Find relevant context	Provide accurate answers
Generate	LLM uses chunks in prompt	Produce final output


⸻

Would you like me to show how Microsoft Copilot (Word/Excel/Outlook) specifically handles uploaded or referenced files internally (via Graph and semantic index)? That’s quite interesting — it ties directly to enterprise data indexing and compliance.

Perfect 👍 let’s go deeper — here’s how Microsoft 365 Copilot (Word, Excel, Outlook, Teams, etc.) handles your file uploads and document context under the hood.

⸻

🏢 1. The Enterprise Setup

Microsoft Copilot isn’t just a chatbot — it’s a semantic retrieval + LLM orchestration layer over your organization’s Microsoft Graph (SharePoint, OneDrive, Exchange, Teams, etc.).

When you open or reference a document, Copilot never directly uploads it to OpenAI or another model.
Instead, it does all of this inside your Microsoft 365 tenant boundary.

⸻

🔄 2. End-to-End Flow (When You “Upload” or Reference a File)

User → Word/Excel/Outlook → Microsoft 365 Copilot → Microsoft Graph → Semantic Index → Orchestrator → LLM

Step-by-step
	1.	User context capture
	•	You open a Word doc, Excel sheet, or email.
	•	Copilot captures metadata (file ID, site ID, drive ID) from Graph.
	2.	Data retrieval
	•	The content is fetched directly from SharePoint or OneDrive via Microsoft Graph APIs.
	•	Copilot never transmits raw files outside Microsoft 365.
	3.	Pre-processing & vectorization
	•	Text is chunked and embedded using an internal vectorization service inside Microsoft’s secure cloud (Azure Cognitive Search + Semantic Index for Copilot).
	4.	Semantic Index lookup
	•	When you ask a question, Copilot runs a vector search across this index.
	•	It finds the most relevant passages, paragraphs, or spreadsheet ranges.
	5.	Prompt orchestration
	•	The retrieved snippets (not the whole file) are composed into a structured prompt.
	•	The orchestrator ensures data-minimization — only what’s relevant is passed to the LLM.
	6.	LLM invocation
	•	The request is sent to an Azure-hosted, Microsoft-controlled GPT instance (in your region, under your compliance boundary).
	•	The model generates text but does not store or fine-tune on your data.
	7.	Response return
	•	Copilot injects the answer back into Word, Excel, or Outlook — e.g. “summarize this doc,” “draft a reply,” “analyze trends.”

⸻

🧩 3. Architecture View

┌────────────────────────────┐
│ Microsoft 365 App (Word)   │
└─────────────┬──────────────┘
              ▼
┌────────────────────────────┐
│ Copilot Orchestration Layer│
│ (Semantic Kernel + Planner)│
└─────────────┬──────────────┘
              ▼
┌────────────────────────────┐
│ Microsoft Graph API        │
│  - Files, Emails, Chats    │
└─────────────┬──────────────┘
              ▼
┌────────────────────────────┐
│ Semantic Index for Copilot │
│  (Vectorized org content)  │
└─────────────┬──────────────┘
              ▼
┌────────────────────────────┐
│ Azure-hosted LLM (GPT-4)   │
│  - Regionally isolated     │
└─────────────┬──────────────┘
              ▼
┌────────────────────────────┐
│ Response → App UI          │
└────────────────────────────┘


⸻

🔐 4. Security & Compliance Highlights

Aspect	How It Works
Data location	All processing stays inside your Microsoft 365 tenant region (e.g., EU/UK/US).
Storage	Files remain in SharePoint/OneDrive; Copilot never stores copies.
Transmission	Only text snippets → encrypted (TLS 1.2+) → Azure OpenAI endpoint.
Retention	No prompts or completions are stored beyond the session.
Compliance	Honors M365 E5 compliance features — Purview, DLP, Sensitivity Labels, etc.


⸻

⚙️ 5. Difference vs. “ChatGPT File Upload”

Feature	ChatGPT (File Upload)	Microsoft Copilot
Storage	Ephemeral (temp cache)	SharePoint/OneDrive
Scope	Personal session	Org-wide data graph
Embedding	Session-local vector store	Semantic Index (tenant-wide)
Security	OpenAI managed	Microsoft tenant boundary
Learning	Not persistent	Not persistent
Context	Uploaded chunks	Graph-retrieved snippets


⸻

💡 Example Scenarios

In Word

“Summarize this 20-page proposal and extract key risks.”

Copilot:
	1.	Reads doc text via Graph.
	2.	Embeds & retrieves key sections.
	3.	Sends concise prompt to GPT.
	4.	Inserts summary inline — no file leaves Microsoft 365.

In Excel

“Analyze quarterly revenue trends.”

Copilot:
	1.	Uses Excel DataFrame interface.
	2.	Queries your data model through the semantic index.
	3.	Returns plain-language insights.

In Outlook

“Draft a reply using the attached project plan.”

Copilot:
	1.	Retrieves attachment metadata.
	2.	Summarizes attachment content.
	3.	Writes suggested response inline.

⸻

🚀 6. Summary

Step	What Happens	Where
File Reference	You open/upload a doc	Word/Excel/Outlook
Retrieval	Graph API fetches content	Microsoft 365 cloud
Embedding	Text → vectors	Semantic Index
Query	Vector search for relevant parts	Tenant scope
Generation	Prompt → GPT (Azure OpenAI)	Same region
Delivery	Answer → App	Local UI


⸻

Would you like me to show a visual architecture diagram (Visio/draw.io-style) that you can embed in your Confluence or system-design doc — e.g. “Copilot file-upload workflow architecture”?

You’re right — that Copilot note is accurate. Here’s the practical way to wire it:

What Copilot APIs do (and don’t) do
	•	No direct file upload + chunking to Copilot.
	•	Do: Put files in SharePoint/OneDrive (within your M365 tenant). Then call the Retrieval API to get semantic chunks (grounding passages) for prompts.  ￼

⸻

Reference architecture (Java backend)

Client → Your Java API
      → Graph: upload file → SharePoint/OneDrive
      → (M365 indexing/semantic index)
      → Retrieval API: query for relevant chunks
      → Compose LLM prompt with chunks (Azure OpenAI, etc.)

1) Upload the file to M365 (Graph)
	•	For files > 4 MB, use a resumable upload session; PUT byte ranges until complete.
	•	Store where your users already have permissions (site drive, user drive, or shared drive).  ￼

Sketch (Java):

// Pseudocode (Microsoft Graph SDK for Java)
UploadSession session = graphClient.drives(driveId)
    .items().byId(parentFolderItemId)
    .itemWithPath(fileName)
    .createUploadSession()
    .buildRequest()
    .post();

byte[] chunk = ...
String contentRange = "bytes " + start + "-" + end + "/" + total;
HttpRequest req = new HttpRequest(session.uploadUrl, "PUT")
    .header("Content-Range", contentRange)
    .body(chunk);
httpClient.send(req);

(You can also download/stream files or partial ranges later using /content and Range.)  ￼

2) Let M365 build/refresh its Semantic Index
	•	Microsoft 365 builds vectors over your tenant content (SharePoint/OneDrive, etc.).
	•	This is what the Retrieval API taps to return relevant text chunks.  ￼

3) Ask for chunks via the Retrieval API
	•	You send a natural-language query, optionally scoped with KQL (URLs, file types, date ranges).
	•	Response = grounding passages (snippets + citations) you can inject into your LLM prompt.  ￼

Sketch (HTTP):

POST https://graph.microsoft.com/beta/copilot/retrieval/query
Content-Type: application/json
Authorization: Bearer {token}

{
  "query": "Summarize risks in the Q3 proposal",
  "kql": "site:https://contoso.sharepoint.com/sites/Deals filetype:docx",
  "topN": 6
}

Response (trimmed):

{
  "chunks": [
    {
      "content": "Identified risks: vendor lock-in, SLA gaps...",
      "source": {
        "driveItemId": "01ABC...",
        "url": "https://contoso.sharepoint.com/.../Q3-Proposal.docx"
      },
      "offsets": { "start": 10234, "length": 980 }
    }
  ]
}

￼

4) Call your LLM with those chunks
	•	Build your system/user messages with the retrieved passages (RAG).
	•	If you’re on Azure, this is typically Azure OpenAI; your data stays in-region.  ￼

⸻

Security/ops notes
	•	Files remain in SharePoint/OneDrive; you’re not shipping them to the model.
	•	Retrieval runs inside the M365 trust boundary and respects permissions (no over-sharing).  ￼
	•	For custom stores (non-M365), use Azure AI Search (vector + keyword) and run your own upload→parse→embed→retrieve pipeline.  ￼

⸻

TL;DR for your Java service
	1.	Upload to SharePoint via Graph createUploadSession (chunked PUT).  ￼
	2.	Query Copilot Retrieval API for semantic chunks (optionally scoped with KQL).  ￼
	3.	Prompt your LLM with those chunks (RAG).  ￼

If you want, I can drop in working Java code (Graph SDK upload + Retrieval API call + Azure OpenAI prompt) as a minimal template.


Awesome — here’s a minimal, production-ready Spring Boot (Java 17 + Lombok) template that does exactly what you asked:
	1.	Upload a file to SharePoint/OneDrive via Microsoft Graph (resumable upload)
	2.	Retrieve semantic chunks via the Copilot Retrieval API
	3.	Call Azure OpenAI with those chunks (RAG-style)

I kept dependencies light and used Spring’s WebClient so you can see the raw HTTP clearly.

⸻

1) pom.xml

<project>
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.example</groupId>
  <artifactId>copilot-rag</artifactId>
  <version>0.0.1</version>
  <properties>
    <java.version>17</java.version>
    <spring-boot.version>3.3.3</spring-boot.version>
  </properties>

  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-dependencies</artifactId>
        <version>${spring-boot.version}</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>

  <dependencies>
    <!-- Web + JSON -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- Lombok -->
    <dependency>
      <groupId>org.projectlombok</groupId>
      <artifactId>lombok</artifactId>
      <optional>true</optional>
    </dependency>

    <!-- OAuth2 client creds for Graph and Retrieval API -->
    <dependency>
      <groupId>com.azure</groupId>
      <artifactId>azure-identity</artifactId>
      <version>1.13.2</version>
    </dependency>

    <!-- Microsoft Graph SDK (for upload session helper not strictly required) -->
    <dependency>
      <groupId>com.microsoft.graph</groupId>
      <artifactId>microsoft-graph</artifactId>
      <version>6.14.0</version>
    </dependency>

    <!-- YAML config -->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter</artifactId>
    </dependency>
  </dependencies>

  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>
</project>


⸻

2) application.yml (fill your values)

server:
  port: 8080

app:
  azure:
    tenant-id: "<YOUR_TENANT_ID>"
    client-id: "<YOUR_APP_CLIENT_ID>"
    client-secret: "<YOUR_APP_CLIENT_SECRET>"

  graph:
    # Use either a user driveId or a SharePoint driveId (site drive).
    drive-id: "<TARGET_DRIVE_ID>"
    parent-path: "/Shared Documents/Uploads"   # folder path in the drive (create if needed)

  # Copilot Retrieval API (Graph beta) – we’ll call it with Graph token
  retrieval:
    endpoint: "https://graph.microsoft.com/beta/copilot/retrieval/query"

  # Azure OpenAI
  openai:
    endpoint: "https://<your-aoai-resource>.openai.azure.com"
    api-key: "<AZURE_OPENAI_API_KEY>"   # or use AAD with MSI if preferred
    deployment: "<gpt-4o-mini-or-gpt-4o>" # your chat deployment name

Why these endpoints?
• Graph resumable upload uses createUploadSession + chunked PUT to the session URL.  ￼
• Copilot Retrieval API (Graph beta) returns grounding passages from SharePoint/OneDrive with security trimming.  ￼
• Azure OpenAI chat completions for final answer generation.  ￼

⸻

3) Config: token factory

// src/main/java/com/example/copilotrag/config/AuthConfig.java
package com.example.copilotrag.config;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AuthConfig {
  @Value("${app.azure.tenant-id}") String tenantId;
  @Value("${app.azure.client-id}") String clientId;
  @Value("${app.azure.client-secret}") String clientSecret;

  @Bean
  public ClientSecretCredential clientSecretCredential() {
    return new ClientSecretCredentialBuilder()
        .tenantId(tenantId)
        .clientId(clientId)
        .clientSecret(clientSecret)
        .build();
  }
}


⸻

4) Utilities: Graph & HTTP helpers

// src/main/java/com/example/copilotrag/util/TokenUtils.java
package com.example.copilotrag.util;

import com.azure.core.credential.AccessToken;
import com.azure.identity.ClientSecretCredential;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class TokenUtils {
  private final ClientSecretCredential cred;

  // For Graph (includes Copilot Retrieval API)
  public String getGraphBearer() {
    AccessToken t = cred.getToken(new com.azure.core.credential.TokenRequestContext()
        .addScopes("https://graph.microsoft.com/.default"))
        .block(Duration.ofSeconds(30));
    return "Bearer " + t.getToken();
  }
}

// src/main/java/com/example/copilotrag/config/WebClientConfig.java
package com.example.copilotrag.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {
  @Bean
  public WebClient webClient() {
    return WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(HttpClient.create()))
        .exchangeStrategies(ExchangeStrategies.builder()
            .codecs(c -> c.defaultCodecs().maxInMemorySize(32 * 1024 * 1024))
            .build())
        .build();
  }
}


⸻

5) Service: Upload to SharePoint/OneDrive (resumable)

// src/main/java/com/example/copilotrag/service/GraphUploadService.java
package com.example.copilotrag.service;

import com.example.copilotrag.util.TokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class GraphUploadService {
  private final WebClient http;
  private final TokenUtils tokens;

  @Value("${app.graph.drive-id}") String driveId;
  @Value("${app.graph.parent-path}") String parentPath;

  public Mono<String> upload(String fileName, byte[] bytes) {
    // 1) Create upload session
    // POST https://graph.microsoft.com/v1.0/drives/{driveId}/root:{parentPath}/{fileName}:/createUploadSession
    String createUrl = "https://graph.microsoft.com/v1.0/drives/" + driveId
        + "/root:" + url(parentPath + "/" + fileName) + ":/createUploadSession";

    return http.post()
        .uri(createUrl)
        .header(HttpHeaders.AUTHORIZATION, tokens.getGraphBearer())
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue("{\"item\": {\"@microsoft.graph.conflictBehavior\": \"replace\"}}")
        .retrieve()
        .bodyToMono(CreateSessionResponse.class)
        .flatMap(sess -> {
          String uploadUrl = sess.uploadUrl();
          // 2) PUT chunk(s) to uploadUrl with Content-Range
          // For brevity, single-shot if file <= 60MB; otherwise loop in 5-10MB chunks.
          int total = bytes.length;
          String range = "bytes 0-" + (total - 1) + "/" + total;
          return http.put()
              .uri(uploadUrl)
              .header("Content-Range", range)
              .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(total))
              .contentType(MediaType.APPLICATION_OCTET_STREAM)
              .bodyValue(bytes)
              .retrieve()
              .bodyToMono(UploadComplete.class)
              .map(done -> done.id()); // return driveItem id
        });
  }

  private String url(String s) {
    return URLEncoder.encode(s, StandardCharsets.UTF_8);
  }

  // Minimal record types for JSON
  public record CreateSessionResponse(String uploadUrl) {}
  public record UploadComplete(String id, String name, String webUrl) {}
}

Notes
• createUploadSession + PUT with Content-Range is the official pattern for files >4 MB. See header format examples.  ￼

⸻

6) Service: Copilot Retrieval API (get semantic chunks)

// src/main/java/com/example/copilotrag/service/RetrievalService.java
package com.example.copilotrag.service;

import com.example.copilotrag.util.TokenUtils;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RetrievalService {
  private final WebClient http;
  private final TokenUtils tokens;

  @Value("${app.retrieval.endpoint}") String retrievalEndpoint;

  public Mono<List<Chunk>> retrieve(String query, String kql, int topN) {
    Request req = Request.builder().query(query).kql(kql).topN(topN).build();

    return http.post()
        .uri(retrievalEndpoint)
        .header(HttpHeaders.AUTHORIZATION, tokens.getGraphBearer())
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(req)
        .retrieve()
        .bodyToMono(Response.class)
        .map(Response::chunks);
  }

  @Data @Builder
  static class Request {
    private String query;
    private String kql;  // e.g., site:..., filetype:docx
    private Integer topN;
  }

  @Data
  static class Response {
    private List<Chunk> chunks;
  }

  @Data
  public static class Chunk {
    private String content;
    private Source source;
    @Data public static class Source {
      private String url;
      private String driveItemId;
    }
  }
}

The Retrieval API returns grounding passages (content + citations) scoped to the caller’s permissions (security trimming). Filter with KQL if desired.  ￼

⸻

7) Service: Azure OpenAI (chat with retrieved chunks)

// src/main/java/com/example/copilotrag/service/OpenAIService.java
package com.example.copilotrag.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenAIService {
  private final WebClient http;

  @Value("${app.openai.endpoint}") String endpoint;
  @Value("${app.openai.api-key}") String apiKey;
  @Value("${app.openai.deployment}") String deployment;

  public Mono<String> chat(List<String> chunks, String question) {
    // Simple RAG prompt: put chunks in system content
    String system = "You are a helpful assistant. Use ONLY the following context to answer.\n\n"
        + String.join("\n---\n", chunks);

    var payload = new ChatRequest(
        List.of(
            new Msg("system", system),
            new Msg("user", question)
        )
    );

    String url = String.format("%s/openai/deployments/%s/chat/completions?api-version=2024-08-01-preview",
        endpoint, deployment);

    return http.post()
        .uri(url)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey) // or "api-key" header depending on your AOAI setup
        .header("api-key", apiKey) // keep both for compatibility
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(payload)
        .retrieve()
        .bodyToMono(ChatResponse.class)
        .map(r -> r.choices.getFirst().message.content);
  }

  /* ----- DTOs ----- */
  public record ChatRequest(List<Msg> messages) {}
  public record Msg(String role, String content) {}

  @Data
  public static class ChatResponse {
    public List<Choice> choices;
    @Data public static class Choice { public Message message; }
    @Data public static class Message { public String role; public String content; }
  }
}

Use Azure OpenAI Chat Completions (or Responses API) with your deployment name. Pick the latest API version your tenant supports.  ￼

⸻

8) Orchestrator: upload → retrieve → chat

// src/main/java/com/example/copilotrag/service/RagOrchestrator.java
package com.example.copilotrag.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RagOrchestrator {
  private final GraphUploadService graphUpload;
  private final RetrievalService retrieval;
  private final OpenAIService openai;

  public Mono<String> ingestAndAsk(String fileName, byte[] bytes, String question) {
    return graphUpload.upload(fileName, bytes)
        .flatMap(itemId -> {
          // Optionally scope Retrieval to this file + folder using KQL:
          String kql = "driveItemId:" + itemId;
          return retrieval.retrieve(question, kql, 6);
        })
        .flatMap(chunks -> openai.chat(
            chunks.stream().map(RetrievalService.Chunk::getContent).toList(),
            question));
  }
}


⸻

9) Controller: /ask (multipart upload + question)

// src/main/java/com/example/copilotrag/api/RagController.java
package com.example.copilotrag.api;

import com.example.copilotrag.service.RagOrchestrator;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RagController {
  private final RagOrchestrator orchestrator;

  @PostMapping(path = "/ask", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Mono<Answer> ask(
      @RequestPart("file") MultipartFile file,
      @RequestPart("question") @NotBlank String question) throws Exception {

    return orchestrator.ingestAndAsk(file.getOriginalFilename(), file.getBytes(), question)
        .map(Answer::new);
  }

  @Data
  public static class Answer { private final String text; }
}


⸻

How it works (quick recap)
	1.	Upload: createUploadSession + chunked PUT (with Content-Range) to Graph → file lands in your chosen Drive (SharePoint/OneDrive).  ￼
	2.	Retrieve: call Copilot Retrieval API with your query (optionally scoped with KQL like driveItemId:<id> or site:<url> filetype:pdf) → get chunks.  ￼
	3.	Generate: pass chunks to Azure OpenAI as context → return answer.  ￼

⸻

Security/tenant model in this flow
	•	Files stay in SharePoint/OneDrive; we only move the bytes once via Graph upload.  ￼
	•	Retrieval API security-trims to the caller’s permissions, so results match what that user is allowed to see.  ￼
	•	Your app authenticates to Graph & Retrieval with client credentials (or OBO if you want user-delegated).
	•	Azure OpenAI call uses your deployment in your region; no training occurs on your data.  ￼

⸻

Next steps you might want
	•	Chunked loop for very large files (5–10MB pieces, handle 202 responses & nextExpectedRanges).  ￼
	•	Switch to user-delegated flow (OBO) so Retrieval honors the end-user identity.
	•	Add retry + backoff + circuit breaker (Resilience4j).
	•	Cache Retrieval chunks per file/version to cut latency.
	•	Replace API-key auth to AOAI with AAD (Managed Identity) if you’re deploying on Azure.

If you want, I can add:
	•	Resilience4j + logging around Content-Range chunk loop, and
	•	A KQL filter helper (e.g., by site URL, filetype, modified date).

Excellent — let’s go field-by-field so you know exactly where each value comes from and how to verify it (whether you’re using Microsoft Entra ID, Graph Explorer, or the Azure Portal).

⸻

🔹 Section 1 – Azure AD / Microsoft Entra ID app registration

These three identify your application to Microsoft Graph and the Copilot Retrieval API.

Key	Where to find	Steps
tenant-id	Entra ID → Overview	In Azure Portal: Home → Microsoft Entra ID > Overview > Tenant ID
client-id	Entra ID → App Registrations → your app → Overview → Application (-client-) ID	
client-secret	Entra ID → App Registrations → your app → Certificates & Secrets → New client secret → copy Value column (once).	

⚠️ You need Application permissions granted & admin-consented in this app:
Files.ReadWrite.All, Sites.Read.All, Directory.Read.All (for upload + retrieval).

⸻

🔹 Section 2 – Microsoft Graph (SharePoint / OneDrive)

Key	Meaning	How to obtain
drive-id	The ID of the drive (OneDrive or SharePoint document library) where you want to upload.	1️⃣ Use Graph Explorer￼ logged in as you.2️⃣ Query:GET https://graph.microsoft.com/v1.0/me/drive → your personal OneDrive.GET https://graph.microsoft.com/v1.0/sites/{site-id}/drives → SharePoint site drives.Copy the "id" value.
parent-path	The folder path inside that drive.	Example /Shared Documents/Uploads – must already exist (or create it in SharePoint UI).


⸻

🔹 Section 3 – Copilot Retrieval API

Key	Description	Notes
retrieval.endpoint	Always https://graph.microsoft.com/beta/copilot/retrieval/query	It’s still in Graph beta, so you must call it with the same token scope as Graph: https://graph.microsoft.com/.default.

✅ Make sure your tenant actually has Copilot Retrieval API access (part of Microsoft 365 Copilot or Semantic Index for Copilot). If not, you can replace this with Azure AI Search for testing.

⸻

🔹 Section 4 – Azure OpenAI Service

Key	Where to find	Steps
endpoint	Your resource’s endpoint URL.	In Azure Portal → Azure OpenAI > Your Resource > Keys & Endpoint → copy “Endpoint”, e.g. https://mycompany-openai.openai.azure.com.
api-key	API Key for that resource.	Same page, under “Keys & Endpoint” → Key 1 or Key 2.
deployment	Name of the model deployment you created.	In Azure OpenAI Studio → Deployments → copy the “Deployment name” column (e.g., gpt-4o-mini, gpt-35-turbo).


⸻

🧩 Quick sanity-check using CLI

# Azure CLI – verify identity & OpenAI endpoint
az account show --query tenantId
az cognitiveservices account show \
  --name my-openai-resource \
  --resource-group my-rg \
  --query "endpoint"

# Graph – check drive info
curl -H "Authorization: Bearer $(az account get-access-token --resource https://graph.microsoft.com --query accessToken -o tsv)" \
     https://graph.microsoft.com/v1.0/me/drive | jq .


⸻

✅ Once you’ve filled them in

Your application.yml will look like:

app:
  azure:
    tenant-id: "72f988bf-86f1-41af-91ab-2d7cd011db47"
    client-id: "8df2c1ab-25b5-4b7d-b6b2-1cbd95c0a045"
    client-secret: "*****************"

  graph:
    drive-id: "b!0DqPq4vE7UydH3hAGrOX9pQwYoM5kFlFr2yG7iEj1yKxM4SktBGF5g3..."
    parent-path: "/Shared Documents/Uploads"

  retrieval:
    endpoint: "https://graph.microsoft.com/beta/copilot/retrieval/query"

  openai:
    endpoint: "https://mycompany-openai.openai.azure.com"
    api-key: "sk-****************"
    deployment: "gpt-4o-mini"


⸻

Would you like me to create a step-by-step Azure Portal setup guide (with screenshots placeholders) for:
	•	registering the Entra ID app (client ID + secret)
	•	granting Graph permissions
	•	creating an Azure OpenAI deployment
so you can follow it visually?


Perfect question — this is exactly where most people get confused.
Let’s make it super clear 👇

⸻

🧩 What “your app” means

In Microsoft Entra ID (formerly Azure Active Directory),
“your app” refers to a custom application registration you create to allow your backend (Spring Boot service) to call Microsoft APIs — like Graph and Copilot Retrieval — securely using OAuth2 client credentials.

You are not expected to see one already there.
You need to create one once, and then you’ll have your own App (Client) ID, Tenant ID, and Client Secret.

⸻

🪜 How to create it step-by-step

1️⃣ Sign in to Azure Portal￼
Use the same account/tenant where you want your Graph & SharePoint access.

⸻

2️⃣ Go to
Microsoft Entra ID → App registrations → + New registration

⸻

3️⃣ Fill in registration details:

Field	Example
Name	Copilot-RAG-FileUpload-App
Supported account types	“Accounts in this organizational directory only”
Redirect URI	(none needed for server-to-server)

Click Register.

⸻

4️⃣ After registration → you’ll land on the app’s Overview page.
Here you’ll see:

Field	Copy this into your application.yml
Application (client) ID	→ app.azure.client-id
Directory (tenant) ID	→ app.azure.tenant-id


⸻

5️⃣ Next: Certificates & secrets → New client secret
	•	Give it a name, e.g. server-secret
	•	Choose expiry (6, 12, 24 months)
	•	Click Add
	•	Copy the Value immediately (that’s your client-secret in application.yml)

⚠️ You cannot retrieve it again later — you’d have to regenerate if lost.

⸻

6️⃣ API permissions

Go to API permissions → + Add a permission → Microsoft Graph → Application permissions, then select:
	•	Files.ReadWrite.All
	•	Sites.Read.All
	•	(optional) Directory.Read.All

Click Add permissions, then Grant admin consent.

This gives your backend app the right to upload files and use the Retrieval API.

⸻

7️⃣ (Optional) Restrict to your organization’s SharePoint or OneDrive if you only want it to access certain sites.

⸻

✅ Summary

After this, you’ll have:

YML key	Example value
tenant-id	72f988bf-86f1-41af-91ab-2d7cd011db47
client-id	8df2c1ab-25b5-4b7d-b6b2-1cbd95c0a045
client-secret	abcd1234-supersecret-xyz

And that’s what you paste into your application.yml under:

app:
  azure:
    tenant-id: "..."
    client-id: "..."
    client-secret: "..."


⸻

Would you like me to generate a visual step-by-step guide (PDF with screenshots placeholders) for this “App Registration for Copilot-RAG backend” setup?
It’s very useful to share internally or upload to Confluence.


Excellent — since you’re brand new to Azure, I’ll walk you through exactly how to set up your first Azure App Registration for your Spring Boot Copilot/Graph integration.

After this, you’ll have all the IDs and secrets you need for your application.yml.

⸻

☁️ Guide Overview

We’ll cover these 5 steps:

1️⃣ Create an App Registration in Microsoft Entra ID
2️⃣ Generate a Client Secret
3️⃣ Grant Microsoft Graph API Permissions
4️⃣ Note your Tenant ID, Client ID, Client Secret
5️⃣ Test your credentials

⸻

📘 1. Sign in to Azure Portal
	1.	Go to 👉 https://portal.azure.com￼
	2.	Sign in using your Microsoft account (the one you used to create your Azure subscription).
	3.	Once logged in, click the menu (☰) in the top-left and search for “Microsoft Entra ID” (previously called Azure Active Directory).

⸻

🧩 2. Register your backend app

Inside Microsoft Entra ID → App registrations
	1.	Click “+ New registration”
	2.	Fill in:
	•	Name: Copilot-RAG-FileUpload-App
	•	Supported account types: Choose
→ “Accounts in this organizational directory only (Default Directory only)”
	•	Redirect URI: Leave empty (not needed for backend app)
	3.	Click Register

✅ You’ve now created your app registration.

⸻

🧾 3. Copy key identifiers

You’ll see these fields on the app’s Overview tab:

Field	Description	Example	Goes into
Application (client) ID	Unique ID of your app	d3b13b11-a5c1-4e87-a4a2-0f5d87a8d91c	app.azure.client-id
Directory (tenant) ID	Your Entra tenant (organization) ID	91af4247-8a3a-4a4a-bbcf-64b8a95cdd0b	app.azure.tenant-id

Keep these handy — you’ll paste them later into your Spring Boot application.yml.

⸻

🔐 4. Create a Client Secret

You now need a “password” that your Java app will use to authenticate.
	1.	In your app registration → left menu → Certificates & secrets
	2.	Under Client secrets, click + New client secret
	3.	Add a name (e.g. spring-boot-backend)
	4.	Choose Expires in 12 months (or 24 if you prefer)
	5.	Click Add
	6.	Copy the Value immediately (not the Secret ID).

✅ Paste this value into your application.yml as client-secret.

⚠️ You can’t see the secret again later — if you forget it, create a new one.

⸻

🔑 5. Add Graph API permissions

Your app needs rights to call Microsoft Graph endpoints (for file upload + Retrieval API).
	1.	In your app’s left menu → API permissions
	2.	Click + Add a permission → Microsoft Graph → Application permissions
	3.	Expand and tick:
	•	Files.ReadWrite.All
	•	Sites.Read.All
	•	(optional but helpful) Directory.Read.All
	4.	Click Add permissions
	5.	Click Grant admin consent for 
Confirm when prompted.

✅ This step ensures your backend app can access SharePoint/OneDrive via Graph.

⸻

🧠 6. Your credentials summary

Now you can fill in your application.yml:

app:
  azure:
    tenant-id: "91af4247-8a3a-4a4a-bbcf-64b8a95cdd0b"
    client-id: "d3b13b11-a5c1-4e87-a4a2-0f5d87a8d91c"
    client-secret: "<copied secret value>"

  graph:
    drive-id: "<we’ll get this next>"
    parent-path: "/Shared Documents/Uploads"

  retrieval:
    endpoint: "https://graph.microsoft.com/beta/copilot/retrieval/query"

  openai:
    endpoint: "https://<your-aoai-resource>.openai.azure.com"
    api-key: "<your Azure OpenAI key>"
    deployment: "gpt-4o-mini"


⸻

📂 7. Find your Drive ID (for file uploads)

After app setup, log in to Graph Explorer￼.

Run this query:

GET https://graph.microsoft.com/v1.0/me/drive

	•	The response includes "id": "b!0DqPq4vE7UydH3hAGrOX9pQwYoM5kFlFr2yG7iEj1yKxM4SktBGF5g3..."
	•	Copy that value → use it as drive-id in your config.

If you want to upload into a SharePoint site instead of your personal OneDrive, run:

GET https://graph.microsoft.com/v1.0/sites?search=<your-site-name>
GET https://graph.microsoft.com/v1.0/sites/{site-id}/drives


⸻

🧪 8. Test that your credentials work

In a terminal (with Azure CLI):

az login
az account get-access-token --resource https://graph.microsoft.com

Or with curl:

curl -H "Authorization: Bearer <your_access_token>" https://graph.microsoft.com/v1.0/me

If you get JSON user info — you’re ready 🎯

⸻

📘 Optional Next: Azure OpenAI setup

If you don’t have it yet:
	1.	In Azure Portal → Create a Resource → AI + Machine Learning → Azure OpenAI Service￼
	2.	Create it in a region that supports GPT-4o
	3.	Once deployed → go to Keys and Endpoint → copy both for your application.yml
	4.	In Azure OpenAI Studio, click Deployments → + Create new deployment → GPT-4o mini and name it e.g. gpt-4o-mini.

⸻

📄 Downloadable PDF setup guide

Would you like me to generate a step-by-step PDF version with screenshot placeholders (titled
“Azure App Registration for Copilot-RAG Java Backend”)?
It’ll include:
	•	Each portal step
	•	Table for YML values
	•	“Where to click” screenshots (placeholders so you can annotate later)
and a final checklist page you can print or share.


Excellent — let’s go deep and clear:
You’ve just created an Azure account, so let’s not only show how to click each step but why each step exists behind the scenes.
This will help you truly understand how Azure, Entra ID, and Microsoft Graph fit together when your Spring Boot Copilot/Graph app authenticates and calls APIs.

⸻

🌐 Background: Why do we even need an “App Registration”?

Think of Microsoft Entra ID (formerly Azure Active Directory) as your company’s identity provider — it keeps track of users, apps, and their access rights.

When your Java backend (Spring Boot app) wants to call a Microsoft API — like Graph or Copilot Retrieval — Azure must know who it is and whether it’s trusted.
That’s the job of an App Registration.

So “registering an app” means creating a service identity in Azure so your backend can log in programmatically using OAuth2.

⸻

🔹 Step 1 — Sign in to Azure Portal

Purpose:
To access the Azure management interface and your tenant’s identity directory (Entra ID).

Background:
Every Azure account sits inside a tenant, which is an isolated identity boundary — think of it as your organization’s identity container (like a company directory).
Your new account automatically comes with a default tenant.

Goal:
You’ll open this tenant to create an App Registration (service identity) that can securely call Graph APIs.

⸻

🔹 Step 2 — Register your backend application in Entra ID

Purpose:
Create an identity record for your Spring Boot app inside your tenant.

What happens technically:
	•	Azure creates an Application Object (global definition of your app).
	•	It also creates a Service Principal (instance of the app in your tenant).
	•	These hold your app’s metadata (name, permissions, redirect URIs, etc.)

Why you need it:
Without this, Azure has no idea who’s trying to call its APIs.
This registration gives your Java service its own “username” (client-id) and “home directory” (tenant-id).

When you choose “Accounts in this organizational directory only”, you’re saying:

“Only my own company (tenant) can issue tokens for this backend.”

This keeps your app private to your organization.

⸻

🔹 Step 3 — Note your Application (Client) ID and Directory (Tenant) ID

Purpose:
These two IDs uniquely identify your app and your directory.

Field	Purpose	Who uses it
tenant-id	Identifies your Entra tenant (your Azure org boundary).	Azure OAuth server
client-id	Identifies your app inside that tenant.	Your Spring Boot backend

Background:
When your backend asks for a token (/token endpoint), Azure checks the tenant-id to find your directory and the client-id to find your app registration record.

Without these, your backend cannot get access tokens.

⸻

🔹 Step 4 — Create a Client Secret

Purpose:
This is the app’s password, used to prove its identity when requesting tokens.

Background:
In OAuth2 terms, your backend is a confidential client (not a public SPA or mobile app).
Such clients authenticate with client credentials (client ID + secret) in a “Client Credentials Flow”.

When your Spring Boot app starts, it uses these credentials to request a JWT access token from Azure AD like:

POST https://login.microsoftonline.com/{tenant-id}/oauth2/v2.0/token
grant_type=client_credentials
client_id={client-id}
client_secret={client-secret}
scope=https://graph.microsoft.com/.default

Azure validates the secret and returns an access token, which your app uses in API headers (Authorization: Bearer <token>).

Why we store it securely:
If someone steals the secret, they can impersonate your app.
So store it in Azure Key Vault or environment variables, never in GitHub.

⸻

🔹 Step 5 — Grant Microsoft Graph API Permissions

Purpose:
Define what your app is allowed to do after it authenticates.

Background:
Azure AD and Microsoft Graph follow the principle of least privilege — no app can read or write anything unless explicitly granted.

When you add Application permissions like:
	•	Files.ReadWrite.All → allows upload/download of files in OneDrive/SharePoint
	•	Sites.Read.All → allows your app to read site metadata (needed for Retrieval API)
	•	Directory.Read.All → optional, lets you look up users or groups

You are telling Graph:

“Even after I log in successfully, only let me call endpoints within these scopes.”

Then you must Grant Admin Consent, meaning an admin confirms these permissions for the entire organization.

Behind the scenes:
	•	Azure AD issues your access tokens containing these permissions in the roles claim.
	•	Graph checks these claims before fulfilling requests.

⸻

🔹 Step 6 — Find your Drive ID

Purpose:
You must tell Graph which drive (document library) to upload into.

Background:
Each OneDrive or SharePoint library is a Drive resource with its own unique ID.

Your app uses:

POST https://graph.microsoft.com/v1.0/drives/{drive-id}/root:/path/to/file:/createUploadSession

to initiate uploads.

How to get it:
	•	For your personal OneDrive: GET https://graph.microsoft.com/v1.0/me/drive
	•	For a SharePoint site:
	1.	Find site → GET https://graph.microsoft.com/v1.0/sites?search=<site-name>
	2.	Then → GET https://graph.microsoft.com/v1.0/sites/{site-id}/drives

The id field returned is your drive-id.

⸻

🔹 Step 7 — Retrieval API

Purpose:
Let your backend search and extract semantic chunks from indexed M365 files.

Background:
The Copilot Retrieval API is a layer over Graph’s Semantic Index for Copilot, which holds embeddings of your M365 documents.
When you query it, it returns relevant passages (like a vector store).
It requires Graph tokens (hence same App Registration).

Your app posts to:

POST https://graph.microsoft.com/beta/copilot/retrieval/query

with JSON:

{ "query": "Summarize project risks", "kql": "site:<url>", "topN": 5 }

Graph replies with semantic chunks that your app feeds into Azure OpenAI.

⸻

🔹 Step 8 — Azure OpenAI Setup

Purpose:
Provide your private LLM endpoint (GPT-4o) to process text and generate answers.

Background:
Azure OpenAI is a managed version of OpenAI models running inside Microsoft’s cloud boundary.
You create a resource and deploy a model (GPT-4o, GPT-35-Turbo, etc.).
It provides:
	•	An endpoint URL
	•	An API key
	•	A deployment name

Your app calls it just like OpenAI API, but through your private Azure endpoint:

POST https://<your-aoai>.openai.azure.com/openai/deployments/<deployment>/chat/completions

This ensures all data stays in your region and under Microsoft compliance (unlike public ChatGPT).

⸻

🔹 Step 9 — Why Spring Boot needs these in application.yml

All these IDs and keys tell your backend how to authenticate and where to send requests.

Section	Purpose
azure	Identity info (used by Azure SDK to get access tokens).
graph	File upload info (drive, folder path).
retrieval	Endpoint for semantic chunking.
openai	Endpoint + credentials for AI completion.

When your service starts, it calls:
	•	ClientSecretCredential to authenticate
	•	GraphUploadService to upload file
	•	RetrievalService to query semantic chunks
	•	OpenAIService to generate answer

All these depend on the config you provide here.

⸻

🔹 Step 10 — The Flow in One Picture

(Spring Boot)
   |
   | 1️⃣ Get Token (Tenant ID + Client ID + Secret)
   v
Azure Entra ID (OAuth2)
   |
   | 2️⃣ Access Token
   v
Microsoft Graph
   ├── Upload file → SharePoint/OneDrive
   ├── Query Retrieval API → semantic chunks
   v
Azure OpenAI
   └── Generate answer → return to user


⸻

⚙️ Recap: Purpose of Each Credential

Config Key	Type	Purpose
tenant-id	Identity scope	tells Azure which directory to authenticate against
client-id	App identity	identifies your backend app
client-secret	Credential	authenticates your app securely
drive-id	Resource ID	identifies the target SharePoint/OneDrive drive
parent-path	Path	folder where uploads are stored
retrieval.endpoint	API endpoint	entry point for Copilot Retrieval API
openai.endpoint	API endpoint	your Azure OpenAI endpoint
openai.api-key	Key	authenticates LLM calls
openai.deployment	Model	identifies which model version (e.g., GPT-4o-mini)


⸻

Would you like me to generate a PDF version of this guide (with a visual diagram, tables, and screenshot placeholders) titled
“Understanding and Configuring Azure App Registration for Spring Boot Copilot Integration”?
It’ll be perfect for saving or sharing in Confluence.


You’re getting that because the request isn’t authenticated (or it’s using the wrong type of token for the endpoint you called).

There are two valid ways to do this—pick one:

⸻

A) Fastest (delegated) — use Graph Explorer or a user token

/me/drive only works with a user-delegated token (i.e., you’re signed in as a user).
	1.	Go to Graph Explorer → sign in (top-right).
	2.	Run:

GET https://graph.microsoft.com/v1.0/me/drive

If signed in, you’ll get your OneDrive’s id (the driveId you can use).

Or with Azure CLI (delegated token for your signed-in user):

az login
TOKEN=$(az account get-access-token --resource https://graph.microsoft.com --query accessToken -o tsv)
curl -H "Authorization: Bearer $TOKEN" https://graph.microsoft.com/v1.0/me/drive


⸻

B) App-only (client credentials) — no user, target a specific resource

With application permissions (client credentials), /me won’t work (there is no “me”).
Use one of these instead:

Option B1: Target a specific user’s OneDrive
	•	Requires Files.ReadWrite.All (Application) + admin consent.
	•	Call /users/{userPrincipalName or id}/drive:

# get app-only token
TENANT="<tenant_id>"
CLIENT="<client_id>"
SECRET="<client_secret>"
TOKEN=$(curl -s -X POST "https://login.microsoftonline.com/$TENANT/oauth2/v2.0/token" \
  -d "grant_type=client_credentials&client_id=$CLIENT&client_secret=$SECRET&scope=https%3A%2F%2Fgraph.microsoft.com%2F.default" \
  | jq -r .access_token)

# call a user's drive (replace email)
curl -H "Authorization: Bearer $TOKEN" \
  https://graph.microsoft.com/v1.0/users/user.name@yourorg.com/drive

Option B2: Use a SharePoint Site drive (typical for team docs)
	1.	Resolve the siteId from your SharePoint site URL:

curl -H "Authorization: Bearer $TOKEN" \
  "https://graph.microsoft.com/v1.0/sites/{hostname}:/sites/{siteName}"
# example: hostname = contoso.sharepoint.com, siteName = Sales

	2.	List its drives (document libraries):

curl -H "Authorization: Bearer $TOKEN" \
  "https://graph.microsoft.com/v1.0/sites/{siteId}/drives"

Pick the library you want; copy its "id" → that’s your drive-id.

⸻

Common reasons for unauthenticated
	•	No Authorization header (must send Authorization: Bearer <access_token>).
	•	Wrong audience/scope (token must be for Graph: scope/resource = https://graph.microsoft.com/.default).
	•	Expired token (they expire quickly; fetch a fresh one).
	•	Using app-only token with /me/... (won’t work; use /users/{id}/... or /sites/...).
	•	Permissions not granted (add Application perms in your App Registration and Grant admin consent).

⸻

Quick decision guide
	•	Want your own OneDrive quickly? → A) Graph Explorer or CLI with delegated token → /me/drive.
	•	Building a backend service (no user) that uploads to SharePoint? → B2) app-only → /sites/{siteId}/drives.
	•	Need to act on a specific user’s OneDrive from the backend? → B1) app-only → /users/{UPN}/drive.

If you tell me your SharePoint site URL (e.g., https://contoso.sharepoint.com/sites/Projects), I’ll give you the exact two curl commands to get the siteId and then the drive-id you should paste into application.yml.


Gotcha—being “signed in” to Graph Explorer isn’t always enough for /me/drive. Here are the most common reasons it still fails and the exact fixes. Do these in order; one of them almost always resolves it.

Quick root causes (and fixes)
	1.	No Microsoft 365/OneDrive license (new Azure account ≠ M365 tenant)

	•	Azure subscription alone does not give you OneDrive for Business.
	•	If your account has no M365 license, /me/drive can’t resolve your OneDrive.
	•	Fix: Use an org account with an M365 license (E3/E5, Business), or create a free Microsoft 365 Developer tenant and use that account. After the license is assigned, open OneDrive once in the browser to provision it, then retry /me/drive.

	2.	OneDrive not provisioned yet (first-time use)

	•	Even with a license, the personal drive isn’t created until you visit OneDrive at least once.
	•	Fix: Log in to Microsoft 365 portal, click OneDrive (first run/provision), wait ~1–2 minutes, then retry /me/drive.

	3.	Wrong token type (app-only vs user-delegated)

	•	/me/drive requires a user-delegated token. If you got an application token (client credentials), /me will fail.
	•	Fix: In Graph Explorer, you’re fine (it uses delegated). If you’re calling from scripts, ensure you’re using a user token (e.g., az login → az account get-access-token --resource https://graph.microsoft.com).

	4.	Missing delegated permissions in Graph Explorer

	•	Delegated token must include Files.Read or Files.ReadWrite.
	•	Fix: In Graph Explorer:
	•	Click “Modify permissions (Preview)”.
	•	Find Files.Read (or Files.ReadWrite) and click Consent (grant for your user).
	•	Re-run GET https://graph.microsoft.com/v1.0/me/drive.

	5.	Signed into the wrong directory/tenant

	•	You might be signed into Graph Explorer with your account, but viewing a different tenant than where your license/app lives.
	•	Fix: In Graph Explorer settings (gear icon), switch to the correct directory. Also confirm top bar shows the intended tenant name.

	6.	Using a personal Microsoft account without OneDrive consumer enabled

	•	If you’re using an @outlook/@hotmail account: /me/drive should work for consumer OneDrive, but only if the OneDrive is active.
	•	Fix: Open onedrive.live.com with that account once, then retry.

⸻

Do this 3-minute checklist
	1.	Open Graph Explorer, confirm you’re signed in (top right).
	2.	Click Modify permissions → Consent to Files.Read (or Files.ReadWrite).
	3.	Click the Access token tab (left pane) and verify it includes the scope Files.Read (or Files.ReadWrite).
	4.	In a new browser tab, open OneDrive for the same account to ensure provisioning finishes.
	5.	Back in Graph Explorer, run:
	•	GET https://graph.microsoft.com/v1.0/me/drive
	•	If that’s still unhappy, try GET https://graph.microsoft.com/v1.0/me/drives (lists drives—sometimes this returns data even when single-drive resolution is still settling).

If you still get an error, copy the error code and message—that pinpoints which item above is the culprit (e.g., 401 = token/scopes, 404 = no drive provisioned).

⸻

If you’re building a backend right now (app-only token)

Remember: /me/... won’t work app-only. Use one of these instead:
	•	Target a SharePoint site drive
	1.	Resolve siteId from your site URL.
	2.	GET /sites/{siteId}/drives → pick an id → that’s your drive-id.
	•	Target a specific user’s OneDrive
GET /users/{userPrincipalName}/drive

(Both require Application permissions plus admin consent.)

⸻

Why this matters for your project
	•	For your Spring Boot service, you’ll typically upload to a SharePoint library (team location) using app-only permissions. You don’t need /me/drive for that—use /sites/{siteId}/drives.
	•	/me/drive is just a quick way in Graph Explorer (delegated) to discover a driveId while testing.

⸻

If you tell me:
	•	whether you have a Microsoft 365 license on the account you’re using, and
	•	the SharePoint site URL you intend to upload into,

I’ll give you the exact two Graph calls you can run (delegated or app-only) to fetch the drive-id you need for application.yml, plus a fallback path if OneDrive isn’t provisioned.

You just need to escape the quotes in that JSON string (or, nicer, send a Map). Here are two clean fixes—pick one.

✅ Minimal fix (escape quotes)

.bodyValue("{\"item\": {\"@microsoft.graph.conflictBehavior\": \"replace\"}}")

✅ Cleaner fix (type-safe)

Map<String, Object> body = Map.of(
    "item", Map.of("@microsoft.graph.conflictBehavior", "replace")
);

return http.post()
    .uri(createUrl)
    .header(HttpHeaders.AUTHORIZATION, tokens.getGraphBearer())
    .contentType(MediaType.APPLICATION_JSON)
    .bodyValue(body)
    .retrieve()
    .bodyToMono(CreateSessionResponse.class)
    // ...

Tip: your url(parentPath + "/" + fileName) will percent-encode slashes. If your parentPath already contains /, encode only the file name:

String createUrl = "https://graph.microsoft.com/v1.0/drives/" + driveId
    + "/root:" + parentPath + "/" + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + ":/createUploadSession";


Here’s the fully corrected GraphUploadService.java (uses a type-safe JSON body and only URL-encodes the file name, not the whole path):

package com.shiv.fileupload.service;

import com.shiv.fileupload.util.TokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GraphUploadService {
    private final WebClient http;
    private final TokenUtils tokens;

    @Value("${app.graph.drive-id}") String driveId;
    @Value("${app.graph.parent-path}") String parentPath;

    public Mono<String> upload(String fileName, byte[] bytes) {
        // Encode only the file name (keep the parentPath slashes intact)
        String encodedName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);

        // POST https://graph.microsoft.com/v1.0/drives/{driveId}/root:{parentPath}/{fileName}:/createUploadSession
        String createUrl = "https://graph.microsoft.com/v1.0/drives/" + driveId
                + "/root:" + parentPath + "/" + encodedName + ":/createUploadSession";

        // Type-safe JSON body (equivalent to {"item": {"@microsoft.graph.conflictBehavior": "replace"}})
        Map<String, Object> body = Map.of(
            "item", Map.of("@microsoft.graph.conflictBehavior", "replace")
        );

        return http.post()
                .uri(createUrl)
                .header(HttpHeaders.AUTHORIZATION, tokens.getGraphBearer())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(CreateSessionResponse.class)
                .flatMap(sess -> {
                    String uploadUrl = sess.uploadUrl();
                    int total = bytes.length;
                    String range = "bytes 0-" + (total - 1) + "/" + total;

                    // Single-shot upload (fine for <= ~60MB). For larger files, loop in 5–10MB chunks.
                    return http.put()
                            .uri(uploadUrl)
                            .header("Content-Range", range)
                            .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(total))
                            .contentType(MediaType.APPLICATION_OCTET_STREAM)
                            .bodyValue(bytes)
                            .retrieve()
                            .bodyToMono(UploadComplete.class)
                            .map(UploadComplete::id);
                });
    }

    public record CreateSessionResponse(String uploadUrl) {}
    public record UploadComplete(String id, String name, String webUrl) {}
}

If you later want true chunked uploads (recommended for large files), I can drop in a loop that sends 5–10MB ranges and handles nextExpectedRanges.

Nice! With the app running, here’s exactly what to do next to prove the end-to-end flow (upload → retrieve → answer):

1) Finalise config

Open src/main/resources/application.yml and make sure these are real values:
	•	app.azure.tenant-id, client-id, client-secret (from your App Registration)
	•	app.graph.drive-id (OneDrive/SharePoint drive you’ll upload to)
	•	app.graph.parent-path (folder in that drive, e.g. /Shared Documents/Uploads)
	•	app.openai.endpoint, api-key, deployment (your Azure OpenAI)

Tip: if your SharePoint folder has spaces, keep them as spaces in parent-path (we only URL-encode the file name in code).

2) Permissions sanity check (one-time)

In Entra ID → App registrations → Your app → API permissions:
	•	Application perms: Files.ReadWrite.All, Sites.Read.All (optional: Directory.Read.All)
	•	Click Grant admin consent (must show “Granted for ”)

3) Make sure the target drive/folder exists
	•	If using SharePoint: open the site → Documents → create Uploads folder if it’s missing
	•	If using OneDrive: open OneDrive once (provisions the drive)

4) Smoke test the API

Use curl (or Postman) to call your endpoint. Replace the file path and question:

curl -F "file=@/path/to/your.pdf" \
     -F "question=Summarize the key risks from this document" \
     http://localhost:8080/api/ask

You should get JSON back:

{ "text": "…answer generated using retrieved chunks…" }

5) What happens under the hood (so you know it worked)
	1.	GraphUploadService creates an upload session and uploads your file to the driveId + parentPath.
	2.	It gets the driveItemId.
	3.	RetrievalService queries Copilot Retrieval API with KQL driveItemId:<that id> to fetch semantic chunks of the uploaded file.
	4.	OpenAIService sends your question + chunks to Azure OpenAI and returns the answer.

6) Quick troubleshooting
	•	401/403 on upload: check the token scopes → permissions granted? Secret valid?
	•	404 on upload session: wrong drive-id or parent-path doesn’t exist.
	•	Retrieval returns empty: your tenant may not have Copilot Retrieval access yet, or indexing hasn’t caught up. Try without the tight KQL (change RagOrchestrator to use a broader KQL like filetype:pdf) to confirm Retrieval works tenant-wide.
	•	OpenAI 401: wrong endpoint/key/deployment or API version.

7) Useful commands (optional)

If you installed Azure CLI:

# Get a user-delegated Graph token (for manual tests)
az login
TOKEN=$(az account get-access-token --resource https://graph.microsoft.com --query accessToken -o tsv)
curl -H "Authorization: Bearer $TOKEN" https://graph.microsoft.com/v1.0/me/drive

# If you’re using SharePoint, get site -> drives
curl -H "Authorization: Bearer $TOKEN" "https://graph.microsoft.com/v1.0/sites?search=<site-name>"
curl -H "Authorization: Bearer $TOKEN" "https://graph.microsoft.com/v1.0/sites/<site-id>/drives"

8) Next nice upgrades (say the word and I’ll add them)
	•	True chunked uploads (5–10 MB loop + nextExpectedRanges)
	•	Resilience4j retries & circuit breaker around WebClient
	•	On-Behalf-Of (OBO) user-delegated flow (answers tailored to the signed-in user’s permissions)
	•	Structured logging and request/response diagnostics (with PII-safe filters)

If you hit any error now, paste me the HTTP status + message (from your app logs or curl) and I’ll pinpoint the fix.


No worries—here’s exactly how to set up the three Azure OpenAI values your app needs:

1) Create an Azure OpenAI resource (once)
	1.	Go to Azure Portal → Create a resource → search “Azure OpenAI” → Create.
	2.	Pick a Subscription, Resource group (new or existing), Region (choose one that supports GPT-4o, e.g., West Europe/UK South/East US), and a Name (e.g., my-aoai).
	3.	Review + Create → Create.

If your subscription doesn’t have access to Azure OpenAI, you’ll need to request access or use an approved subscription/region.

2) Get your Endpoint & API Key
	1.	Open your new Azure OpenAI resource.
	2.	Left menu → Keys and Endpoint.
	•	Endpoint → copy (looks like https://my-aoai.openai.azure.com)
	•	Key 1 (or Key 2) → copy (this is your api-key)

3) Deploy a model (gives you the “deployment” name)
	1.	Click Azure AI Foundry (or OpenAI Studio) from the resource.
	2.	Go to Deployments → + Create deployment.
	3.	Choose a model (e.g., GPT-4o mini or GPT-4o), keep defaults, and set a Deployment name (e.g., gpt-4o-mini).
	4.	Create/Deploy → wait until status is Succeeded.

That Deployment name is exactly what goes into your config.

4) Fill your application.yml

Replace the placeholders with what you copied:

app:
  openai:
    endpoint: "https://my-aoai.openai.azure.com"   # from Keys & Endpoint
    api-key: "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"     # Key 1 or Key 2
    deployment: "gpt-4o-mini"                       # the deployment name you set

5) Quick test (optional)

From your terminal (to sanity-check the keys/endpoint work):

# macOS/Linux
ENDPOINT="https://my-aoai.openai.azure.com"
APIKEY="xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"
DEPLOYMENT="gpt-4o-mini"

curl -s -H "api-key: $APIKEY" \
     -H "Content-Type: application/json" \
     "$ENDPOINT/openai/deployments/$DEPLOYMENT/chat/completions?api-version=2024-08-01-preview" \
     -d '{"messages":[{"role":"user","content":"Say hello"}]}'

You should get a JSON response with a greeting. If you see 401/404:
	•	401 → wrong key or missing api-key header.
	•	404 → deployment name doesn’t match, or wrong endpoint/region.
	•	429 → capacity/quota; try a smaller model or different region.

6) (Optional) Use Entra ID instead of API Key later

For production, you can switch to Managed Identity / Entra ID:
	•	Assign a User-Assigned Managed Identity to your app service/VM.
	•	Grant it Cognitive Services OpenAI User role on the AOAI resource.
	•	In code, swap to AAD auth (remove api-key header).
If you want, I can refactor the sample to AAD auth when you’re ready.

⸻

If you tell me your Azure OpenAI resource name/region and the deployment name you created, I’ll double-check the exact application.yml lines for you.










