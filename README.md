# Copilot Retrieval + Graph Upload + Azure OpenAI (Java 17, Spring Boot 3, Lombok)

A minimal, production-ready skeleton that:
1) Uploads a file to SharePoint/OneDrive using Microsoft Graph (resumable upload)
2) Queries Copilot **Retrieval API** for semantic chunks
3) Calls **Azure OpenAI** with those chunks (RAG)

## Requirements
- Java 17
- Maven 3.9+
- An Azure AD app with Client Credentials (app registration)
- Microsoft Graph permissions (Application): `Files.ReadWrite.All`, `Sites.Read.All`
- Access to Copilot Retrieval API (in Graph **beta**)
- Azure OpenAI resource + model deployment (e.g., `gpt-4o-mini`)

## Configure
Edit `src/main/resources/application.yml`:

```yaml
app:
  azure:
    tenant-id: "<TENANT>"
    client-id: "<CLIENT_ID>"
    client-secret: "<CLIENT_SECRET>"

  graph:
    drive-id: "<DRIVE_ID>"
    parent-path: "/Shared Documents/Uploads"

  retrieval:
    endpoint: "https://graph.microsoft.com/beta/copilot/retrieval/query"

  openai:
    endpoint: "https://<your-aoai>.openai.azure.com"
    api-key: "<AOAI_API_KEY>"
    deployment: "<gpt-4o-mini>"
```

**Get driveId**: via Graph Explorer or API (`/sites/{siteId}/drives` or `/me/drive`).  
Ensure the `parent-path` exists.

## Run
```bash
./mvnw spring-boot:run
# or
mvn spring-boot:run
```

## Test (HTTPie / curl)
```bash
http -f POST :8080/api/ask file@sample.pdf question=="Summarize key risks"
# or curl
curl -F "file=@sample.pdf" -F "question=Summarize key risks" http://localhost:8080/api/ask
```

## Notes
- For large files, convert `GraphUploadService` to loop chunks (5–10MB) and handle `nextExpectedRanges`.
- Switch to user-delegated (On-Behalf-Of) flow if you need per-user trimming.
- Replace AOAI API key with AAD/Managed Identity when running on Azure.