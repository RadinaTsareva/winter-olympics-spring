# Database

The persisted model is declared through JPA entities in `BE/src/main/java/com/example/winter_olympics/entity/`. The enum types `CompetitionType`, `Gender`, `Medal`, and `Role` are stored as enum values and are not tables.

```mermaid
erDiagram
    COUNTRIES ||--o{ ATHLETES : represents
    ATHLETES ||--o{ USERS : linked_account
    ATHLETES ||--o{ COMPETITION_REGISTRATIONS : enters
    COMPETITIONS ||--o{ COMPETITION_REGISTRATIONS : includes
    COMPETITION_REGISTRATIONS ||--o| SLALOM_RESULTS : has
    COMPETITION_REGISTRATIONS ||--o| BIATHLON_RESULTS : has

    COUNTRIES {
        bigint id PK
        string name UK
    }
    ATHLETES {
        bigint id PK
        string name
        bigint country_id FK
        string gender
        date date_of_birth
    }
    COMPETITIONS {
        bigint id PK
        string name
        string type
        string gender
        int minimum_age
        int number_of_laps
        int shooting_after_laps
    }
    COMPETITION_REGISTRATIONS {
        bigint id PK
        bigint athlete_id FK
        bigint competition_id FK
    }
    SLALOM_RESULTS {
        bigint id PK
        bigint registration_id FK
        decimal first_run_time
        decimal second_run_time
        boolean first_run_finished
        boolean second_run_finished
    }
    BIATHLON_RESULTS {
        bigint id PK
        bigint registration_id FK
        decimal ski_time
        int misses
        decimal penalty_per_miss
        boolean finished
    }
    USERS {
        bigint id PK
        string username UK
        string password
        string role
        bigint athlete_id FK
    }
```

The diagram shows relationships represented by entity mappings. `User.athlete` is an optional many-to-one link; the model does not enforce one user per athlete. `Athlete.country`, `CompetitionRegistration.athlete`, and `CompetitionRegistration.competition` are required many-to-one links. Each result entity has a required one-to-one link to a registration, with a unique `registration_id`.

## Tables and Fields

| Table | Purpose and important fields |
| --- | --- |
| `countries` | Country directory; `id`, unique non-null `name`. |
| `athletes` | Athlete identity; `id`, non-null `name`, required `country_id`, `gender`, and `date_of_birth`. |
| `competitions` | Event definition; `id`, `name`, `type`, `gender`, `minimum_age`, optional `number_of_laps`, `shooting_after_laps`. |
| `competition_registrations` | Athlete participation; required athlete and competition foreign keys. A unique constraint prevents duplicate athlete/competition pairs. |
| `slalom_results` | One Slalom result per registration; first/second run times and finish flags. Times use `numeric(10,3)` mapping. |
| `biathlon_results` | One Biathlon result per registration; ski time, aggregate misses, penalty per miss, finish flag. Ski/penalty times use `numeric(10,3)` mapping. |
| `users` | Login account; unique username, BCrypt password hash, role, optional athlete foreign key. |

No separate medal table exists. Medal standings are derived from current result rankings. There is no database column for a registration status or individual Biathlon shooting stages.

The current statistics service identifies medalists by matching medal response athlete names against athlete records rather than joining on athlete IDs. If multiple athlete records share a name, age statistics can include multiple same-name records.

## Relationship and Deletion Notes

The entity mappings do not specify cascade-delete behavior. Related rows can prevent deletion through foreign-key constraints; the global exception handler maps database integrity violations to HTTP 409. Remove dependent registrations/results through the API before deleting related records.

## Schema Management

Local Hibernate schema mode defaults to `update`. The current production profile also temporarily uses `update` for the empty-database bootstrap commit. After the Railway schema has been created and verified, production should return to `validate`. No Flyway, Liquibase, or other versioned migration setup is present in the repository.
