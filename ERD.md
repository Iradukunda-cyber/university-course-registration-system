# Entity Relationship Diagram (ERD)

This ERD reflects the entities, attributes, and relationships implemented in the JPA model.

```mermaid
erDiagram
    DEPARTMENT ||--o{ STUDENT : "admits"
    DEPARTMENT ||--o{ LECTURER : "employs"
    DEPARTMENT ||--o{ COURSE : "offers"
    DEPARTMENT ||--o| LECTURER : "headed by"

    STUDENT ||--|| STUDENT_PROFILE : "has"
    STUDENT ||--o{ REGISTRATION : "makes"
    COURSE ||--o{ REGISTRATION : "receives"

    LECTURER }o--o{ COURSE : "teaches (course_lecturers)"

    DEPARTMENT {
        BIGINT id PK
        VARCHAR name UK
        VARCHAR code UK
        BIGINT head_lecturer_id FK,UK
    }

    STUDENT {
        BIGINT id PK
        VARCHAR student_number UK
        VARCHAR first_name
        VARCHAR last_name
        VARCHAR email UK
        BIGINT department_id FK
        BIGINT profile_id FK,UK
    }

    STUDENT_PROFILE {
        BIGINT id PK
        DATE date_of_birth
        VARCHAR gender
        VARCHAR phone_number
        VARCHAR residential_address
    }

    LECTURER {
        BIGINT id PK
        VARCHAR staff_number UK
        VARCHAR first_name
        VARCHAR last_name
        VARCHAR email UK
        BIGINT department_id FK
    }

    COURSE {
        BIGINT id PK
        VARCHAR course_code UK
        VARCHAR title
        INT credit_hours
        BIGINT department_id FK
    }

    REGISTRATION {
        BIGINT id PK
        BIGINT student_id FK
        BIGINT course_id FK
        DATE registration_date
        VARCHAR academic_year
        VARCHAR semester
        VARCHAR registration_status "PENDING|APPROVED|REJECTED"
        DECIMAL grade_obtained
    }

    COURSE_LECTURERS {
        BIGINT lecturer_id PK,FK
        BIGINT course_id PK,FK
    }
```

## Cardinality Notes
- **Department 1:N Student**: each student is assigned to exactly one department.
- **Department 1:N Lecturer**: each lecturer is employed by exactly one department.
- **Department 1:N Course**: each course belongs to exactly one department.
- **Department 1:1 Lecturer (head)**: each department has one head lecturer; `head_lecturer_id` is unique.
- **Student 1:1 StudentProfile**: `profile_id` is unique and required.
- **Lecturer M:N Course**: implemented with join table `course_lecturers` (no separate entity).
- **Student M:N Course via Registration**: implemented as join entity `registrations` because it stores registration date, academic year, semester, status, and grade.
