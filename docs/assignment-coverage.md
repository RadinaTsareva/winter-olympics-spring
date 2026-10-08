# Assignment Coverage

The official Java Web Services assignment brief/rubric is not present in this repository. It is therefore not possible to make a truthful row-by-row compliance claim against the complete course requirements. The table below records the project areas evidenced in the current code and marks their implementation status.

| Requirement area | Implementation evidence | Status |
| --- | --- | --- |
| Java web service / REST API | Spring MVC controllers under `/api`, JSON request/response handling | Implemented |
| Relational persistence | Seven JPA entity tables mapped to PostgreSQL | Implemented |
| Domain CRUD | Athlete, country, and competition APIs; registration create/read/delete | Implemented in backend |
| Competition disciplines | `SKI_SLALOM` and `BIATHLON` competition types and settings | Implemented |
| Slalom rules and ranking | First/second run, top-30 first-run qualification, reverse start order, completed final-time ranking | Implemented in backend |
| Biathlon result and ranking | Ski time, aggregate misses, per-miss penalty, DNF flag, completed final-time ranking | Implemented in backend |
| Authentication and authorization | JWT, BCrypt, ADMIN/ATHLETE roles, API-level access/ownership checks | Implemented |
| Athlete registration and self-service | Backend enforces ownership; demo seeder creates linked ATHLETE accounts, while public signup has no account-linking workflow | Partially implemented |
| Public results information | Ranking, medals, statistics APIs and React pages | Implemented |
| Administrator functionality | Athlete, competition, registration pages; dashboard and demo-data action | Partially implemented (result pages are placeholders) |
| Account registration UI | Backend endpoint exists; frontend `/register` is a placeholder | Partially implemented |
| Assignment-specific requirements | No official rubric file found in repository | Not assessable from repository |

Statuses describe the implementation found in source, not final course grading. A completed rubric mapping requires the official assignment brief.
