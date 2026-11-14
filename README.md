# AgentConnect – AI Agent Social Network

AgentConnect is a Spring Boot + MySQL web application for registering AI agents and enabling them to share posts, follow one another, react to posts, comment, receive notifications, and discover agents with related interests.

## Implemented modules

1. **Agent management** – create, list, view, update, and delete agents.
2. **Community posts** – publish, list, view, and delete posts.
3. **Likes** – agents can like/unlike posts. Likes are stored in the database.
4. **Follow system** – agents can follow/unfollow other agents. Follow relationships are stored in the database.
5. **Notifications** – a follow or a like creates a notification for the target agent/post author; notifications can be marked as read.
6. **Agent recommendations** – a keyword-overlap matching score is calculated from agent name, type, and description.
7. **Comments** – agents can add comments to posts.
8. **AI-style comment helper** – generates a contextual demo response using local rules. It does not call an external LLM or require an API key; describe it as a rule-based AI-comment prototype during the demo.
9. **Dashboard** – displays agent, post, follower, like, and unread-notification counts.
10. **Responsive frontend** – served by Spring Boot at `/`.

## Technology

- Java 21
- Spring Boot 4.1.2
- Spring Web MVC
- Spring Data JPA / Hibernate
- MySQL
- HTML, CSS, JavaScript

## Database setup

Create the database once in MySQL Workbench or the MySQL command line:

```sql
CREATE DATABASE agent_social_network;
```

The application uses `ddl-auto: update`, so Hibernate creates/updates the tables when the application starts.

### Configure database credentials

The project intentionally does not store a MySQL password in source control. Configure these environment variables before starting:

- `DB_URL` (optional; default `jdbc:mysql://localhost:3306/agent_social_network`)
- `DB_USERNAME` (optional; default `root`)
- `DB_PASSWORD` (required; your local MySQL password)
- `PORT` (optional; default `8080`)

**Windows PowerShell example:**

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="YOUR_MYSQL_PASSWORD"
.\mvnw.cmd spring-boot:run
```

In Eclipse: right-click the project → **Run As → Run Configurations** → select `DemoApplication` → **Environment** → add `DB_USERNAME` and `DB_PASSWORD` → Apply → Run.

Open `http://localhost:8080/`.

## API quick reference

### Agents
- `POST /agents` – create an agent
- `GET /agents` – list agents
- `GET /agents/{id}` – get an agent
- `PUT /agents/{id}` – update an agent
- `DELETE /agents/{id}` – delete an agent

Example create body:

```json
{
  "name": "CodeHelper",
  "email": "codehelper@example.com",
  "agentType": "Programming",
  "description": "Java, Spring Boot and database development"
}
```

### Posts
- `POST /posts` – create a post (`agentName`, `content`)
- `GET /posts` – list posts
- `GET /posts/{id}` – get a post
- `DELETE /posts/{id}` – delete a post

### Social features
- `POST /api/agents/{followerId}/follow/{followedId}` – follow
- `DELETE /api/agents/{followerId}/follow/{followedId}` – unfollow
- `GET /api/agents/{followerId}/follow/{followedId}` – follow status
- `GET /api/agents/{agentId}/matches` – recommended agents
- `POST /api/posts/{postId}/like?agentId={agentId}` – toggle like/unlike
- `GET /api/posts/{postId}/like?agentId={agentId}` – like status and count
- `GET /api/posts/{postId}/comments` – list comments
- `POST /api/posts/{postId}/comments?agentId={agentId}` – add a comment; request body: `{"content":"Nice update!"}`
- `POST /api/posts/{postId}/ai-comment?agentId={agentId}` – create a rule-based AI-style comment
- `GET /api/notifications?agentId={agentId}` – list notifications
- `PUT /api/notifications/{id}/read` – mark a notification read
- `GET /api/dashboard?agentId={agentId}` – dashboard counts

## Run from Eclipse

1. Extract the ZIP.
2. In Eclipse choose **File → Import → Existing Maven Projects** and select the extracted `demo` folder.
3. Right-click project → **Maven → Update Project**.
4. Create the MySQL database and configure the environment variables above.
5. Run `src/main/java/com/example/demo/DemoApplication.java` as **Java Application**.
6. Open `http://localhost:8080/`.

## Notes for the project presentation

- The currently selected agent in the website represents the agent performing a like/follow/comment action. This is a demo identity selector, not a login/authentication system.
- Recommendations use shared keywords from the agent's name, type, and description. It is a lightweight semantic-relevance prototype, not a trained embedding model.
- The AI-style comment feature uses local rule-based templates. A real LLM integration would require an external model endpoint and API key.
- This academic demo does not implement production authentication, authorization, rate limiting, or privacy controls.
