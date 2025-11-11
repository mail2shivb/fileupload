Excellent — you’re now in the real evaluation zone 🔍

Since you’ve uploaded Meta, Apple, and Dell 2024 Annual Reports, we can design targeted retrieval scenarios that mirror realistic business, analyst, and AI-assistant use-cases.

Below is a categorized set of search/evaluation scenarios you can directly run against:
	•	Microsoft Graph Retrieval API (if stored in SharePoint/OneDrive)
	•	Your Vector DB baseline (using the same uploaded files)

⸻

🧾 1️⃣ Financial performance & key metrics

Scenario ID	Query	Purpose / What to Observe
F1	“What was Meta’s total revenue in 2024?”	Numeric accuracy; ability to locate tables or narrative.
F2	“Apple operating income 2024 vs 2023”	Comparison and cross-year reasoning.
F3	“Dell’s gross margin percentage for FY2024”	Extraction from financial statement tables.
F4	“What was Apple’s EPS (earnings per share)?”	Precision in retrieving inline financial metrics.
F5	“Which segment contributed most to Meta’s 2024 revenue?”	Section recognition — should surface “Family of Apps” or similar.

Evaluation focus:
✅ Table parsing quality, ✅ numerical snippet accuracy, ✅ section navigation (MD&A vs tables).

⸻

💼 2️⃣ Business strategy & outlook

Scenario ID	Query	Goal
S1	“What are Apple’s strategic priorities for 2025?”	Semantic understanding of forward-looking statements.
S2	“Meta 2024 cost reduction or restructuring plans”	Find risk/expense management narrative.
S3	“Dell’s growth opportunities in AI or Edge computing”	Semantic matching of technology trends.
S4	“Apple’s capital allocation strategy”	Retrieval of financial policy or cash-return notes.
S5	“Meta management’s view on advertising market”	Evaluate text comprehension of qualitative discussions.


⸻

⚙️ 3️⃣ Technology & innovation highlights

Scenario ID	Query	Description
T1	“Apple innovation in silicon or chip design”	Should find sections about M3, A17, etc.
T2	“Meta’s advancements in AI or Metaverse in 2024”	Keyword vs concept recall.
T3	“Dell’s R&D expenditure”	Numeric retrieval + section title understanding.
T4	“How does Apple describe its sustainability in product design?”	Multi-word semantic comprehension.


⸻

🌍 4️⃣ Sustainability, ESG, and corporate responsibility

Scenario ID	Query	Goal
E1	“Apple carbon footprint reduction efforts”	Concept recall (ESG context).
E2	“Meta’s data center sustainability goals”	Retrieves non-financial section.
E3	“Dell’s diversity and inclusion initiatives”	Phrase expansion (“diversity”, “inclusion”, “equal opportunity”).
E4	“Apple renewable energy investments”	Keyword + concept retrieval.

Compare:
Does Retrieval API surface “Environmental Responsibility” sections automatically?
Does your Vector DB require chunk-level metadata to find it?

⸻

⚖️ 5️⃣ Risk factors and management discussion

Scenario ID	Query	What to test
R1	“Major risks mentioned by Apple in 2024 report”	Ability to return risk summary.
R2	“Meta cybersecurity or data privacy risks”	Term sensitivity and synonyms.
R3	“Dell supply chain disruptions or dependencies”	Topic recall.
R4	“Apple litigation or regulatory risks”	Keyword + semantic sensitivity.


⸻

👩‍💼 6️⃣ Governance and management

Scenario ID	Query	Goal
G1	“Who are Meta’s board members?”	Named entity extraction.
G2	“Apple’s executive compensation discussion”	Multi-page section retrieval.
G3	“Dell audit committee responsibilities”	Section recognition.
G4	“CEO letter highlights in Apple 2024 report”	Page position recall (front matter).


⸻

📊 7️⃣ Cross-company comparisons (multi-document RAG)

Scenario ID	Query	Intent
C1	“Compare Meta and Apple total revenue growth in 2024”	Multi-doc summarization.
C2	“Which company spent more on R&D?”	Retrieve both metrics from each report.
C3	“Which company had higher operating margin?”	Structured reasoning test.
C4	“Which of Meta, Apple, Dell emphasized AI in their 2024 report?”	Semantic clustering.

Measure:
🔹 Multi-document merging quality
🔹 Latency when retrieving across multiple sources
🔹 Need for deduplication or re-ranking

⸻

🧪 8️⃣ Analyst-style composite queries

Scenario ID	Query	Testing aspect
A1	“Summarize Apple’s 2024 financial highlights and risk factors”	Long semantic query; test summarization readiness.
A2	“List all references to artificial intelligence in all reports”	High recall test.
A3	“Find every mention of privacy regulation”	Keyword + context.
A4	“Show every table containing revenue breakdown”	Structural retrieval precision.


⸻

🧩 9️⃣ Non-English / variant phrasing (for robustness)

Scenario ID	Query	Why it matters
L1	“ingresos totales de Apple 2024” (Spanish for “total income”)	Multilingual embeddings test.
L2	“Meta chiffre d’affaires 2024” (French for revenue)	Cross-language semantic search.
L3	“Dell bénéfices net 2024”	Check translation normalization.


⸻

🧮 10️⃣ Evaluation sheet (recommended columns)

Scenario ID	Query	Source(s)	Retrieval Top-3 Match	Vector DB Top-3 Match	Correct? (Y/N)	Latency (ms)	Notes / Observations


This structure lets you score both systems (1–5 scale for relevance, accuracy, freshness, explainability).

⸻

⚙️ Optional metrics to record
	•	Precision@3: how many of top 3 hits truly relevant
	•	Snippet clarity: short, human-readable vs long text block
	•	Latency: average over 5 runs
	•	Coverage: fraction of unique queries that yield at least one result
	•	Security: if access-controlled files hidden (if relevant)

⸻

Would you like me to generate a ready-to-download Excel (.xlsx) file pre-filled with all these scenarios and scoring columns so you can log results while testing both Retrieval API and your Vector DB?
