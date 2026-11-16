# Relational Database Schema

Database: `university_academic_db` (PostgreSQL)  
ORM: Spring Data JPA / Hibernate

## Tables

### 1. `departments`
| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | BIGINT (identity) | PRIMARY KEY | Department identifier |
| `name` | VARCHAR | NOT NULL, UNIQUE | Department name |
| `code` | VARCHAR | NOT NULL, UNIQUE | Department code |
| `head_lecturer_id` | BIGINT | FOREIGN KEY → `lecturers(id)`, UNIQUE, NULLABLE at creation | Head of department |

### 2. `lecturers`
| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | BIGINT (identity) | PRIMARY KEY | Lecturer identifier |
| `staff_number` | VARCHAR | NOT NULL, UNIQUE | Staff number |
| `first_name` | VARCHAR | NOT NULL | First name |
| `last_name` | VARCHAR | NOT NULL | Last name |
| `email` | VARCHAR | NOT NULL, UNIQUE | Email address |
| `department_id` | BIGINT | NOT NULL, FOREIGN KEY → `departments(id)` | Employing department |

### 3. `student_profiles`
| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | BIGINT (identity) | PRIMARY KEY | Profile identifier |
| `date_of_birth` | DATE | NOT NULL | Date of birth |
| `gender` | VARCHAR | NOT NULL | Gender |
| `phone_number` | VARCHAR | NOT NULL | Phone number |
| `residential_address` | VARCHAR | NOT NULL | Residential address |

### 4. `students`
| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | BIGINT (identity) | PRIMARY KEY | Student identifier |
| `student_number` | VARCHAR | NOT NULL, UNIQUE | Student number |
| `first_name` | VARCHAR | NOT NULL | First name |
| `last_name` | VARCHAR | NOT NULL | Last name |
| `email` | VARCHAR | NOT NULL, UNIQUE | Email address |
| `department_id` | BIGINT | NOT NULL, FOREIGN KEY → `departments(id)` | Assigned department |
| `profile_id` | BIGINT | NOT NULL, FOREIGN KEY → `student_profiles(id)`, UNIQUE | Personal profile (1:1) |

### 5. `courses`
| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | BIGINT (identity) | PRIMARY KEY | Course identifier |
| `course_code` | VARCHAR | NOT NULL, UNIQUE | Course code |
| `title` | VARCHAR | NOT NULL | Course title |
| `credit_hours` | INTEGER | NOT NULL | Credit hours |
| `department_id` | BIGINT | NOT NULL, FOREIGN KEY → `departments(id)` | Offering department |

### 6. `course_lecturers` (Join Table)
| Column | Type | Constraints | Description |
|---|---|---|---|
| `lecturer_id` | BIGINT | NOT NULL, FOREIGN KEY → `lecturers(id)` | Teaching lecturer |
| `course_id` | BIGINT | NOT NULL, FOREIGN KEY → `courses(id)` | Taught course |

**Unique constraint:** (`lecturer_id`, `course_id`) prevents duplicate teaching assignments.  
**Purpose:** Implements Lecturer–Course many-to-many without a separate entity.

### 7. `registrations` (Join Entity Table)
| Column | Type | Constraints | Description |
|---|---|---|---|
| `id` | BIGINT (identity) | PRIMARY KEY | Registration identifier |
| `student_id` | BIGINT | NOT NULL, FOREIGN KEY → `students(id)` | Registered student |
| `course_id` | BIGINT | NOT NULL, FOREIGN KEY → `courses(id)` | Registered course |
| `registration_date` | DATE | NOT NULL | Date of registration |
| `academic_year` | VARCHAR | NOT NULL | Academic year |
| `semester` | VARCHAR | NOT NULL | Semester |
| `registration_status` | VARCHAR | NOT NULL | `PENDING`, `APPROVED`, or `REJECTED` (enum stored as string) |
| `grade_obtained` | NUMERIC(5,2) | NULLABLE | Grade obtained |

**Unique constraint:** (`student_id`, `course_id`, `academic_year`, `semester`) to prevent duplicate registrations for the same offering period.

## Relationship Mapping Summary
- **Department–Student (1:N):** FK `students.department_id`
- **Department–Lecturer (1:N):** FK `lecturers.department_id`
- **Department–Course (1:N):** FK `courses.department_id`
- **Department–Head Lecturer (1:1):** FK `departments.head_lecturer_id` (unique)
- **Student–StudentProfile (1:1):** FK `students.profile_id` (unique)
- **Lecturer–Course (M:N):** join table `course_lecturers`
- **Student–Course (M:N with attributes):** join entity/table `registrations`

## JPA/Hibernate Mapping Artifacts
- Entities: `Department`, `Student`, `StudentProfile`, `Lecturer`, `Course`, `Registration`, `RegistrationStatus` (enum)
- Repositories: `DepartmentRepository`, `StudentRepository`, `StudentProfileRepository`, `LecturerRepository`, `CourseRepository`, `RegistrationRepository`
- Configuration: `src/main/resources/application.properties` (`spring.datasource.*`, `spring.jpa.hibernate.ddl-auto=update`)
