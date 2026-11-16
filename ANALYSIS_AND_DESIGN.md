# Problem Analysis and Design

## 1. Problem Analysis
The university needs a system to replace manual tracking of academic structure and student course registrations. The system must capture:
- Departments and their offered courses, employed lecturers, and department head.
- Students admitted into departments, each with exactly one personal profile.
- Teaching assignments where lecturers teach courses and courses are taught by lecturers (many-to-many) without creating a separate assignment entity.
- Course registrations where students register for courses and each registration stores registration date, academic year, semester, registration status (`PENDING`, `APPROVED`, `REJECTED`), and grade.

The implementation scope for this stage is the **Model** and **Repository** layers using Spring Boot, Spring Data JPA/Hibernate, and PostgreSQL (`university_academic_db`).

## 2. Identified Entities
1. **Department** – academic unit of the university.
2. **Student** – student admitted into the university and assigned to a department.
3. **StudentProfile** – personal details of exactly one student.
4. **Lecturer** – academic staff member employed by a department.
5. **Course** – course offered by a department.
6. **Registration** – join entity representing a student's registration in a course with registration-specific attributes.

Supporting type:
- **RegistrationStatus** (enum) – `PENDING`, `APPROVED`, `REJECTED`.

## 3. Attributes by Entity

### Department
- `id` (PK, Long, generated)
- `name` (String, unique, required)
- `code` (String, unique, required)
- `headLecturer` (reference to Lecturer, one department head)

### Student
- `id` (PK, Long, generated)
- `studentNumber` (String, unique, required)
- `firstName` (String, required)
- `lastName` (String, required)
- `email` (String, unique, required)
- `department` (FK to Department, required)
- `profile` (FK to StudentProfile, unique, required)

### StudentProfile
- `id` (PK, Long, generated)
- `dateOfBirth` (LocalDate, required)
- `gender` (String, required)
- `phoneNumber` (String, required)
- `residentialAddress` (String, required)

### Lecturer
- `id` (PK, Long, generated)
- `staffNumber` (String, unique, required)
- `firstName` (String, required)
- `lastName` (String, required)
- `email` (String, unique, required)
- `department` (FK to Department, required)

### Course
- `id` (PK, Long, generated)
- `courseCode` (String, unique, required)
- `title` (String, required)
- `creditHours` (Integer, required)
- `department` (FK to Course's owning Department, required)

### Registration (Join Entity)
- `id` (PK, Long, generated)
- `student` (FK to Student, required)
- `course` (FK to Course, required)
- `registrationDate` (LocalDate, required)
- `academicYear` (String, required)
- `semester` (String, required)
- `registrationStatus` (RegistrationStatus enum, required)
- `gradeObtained` (BigDecimal, nullable until awarded)

## 4. Relationships and Cardinalities
1. **Department — Student**: One-to-Many / Many-to-One  
   - One department has many students; each student belongs to exactly one department.
2. **Department — Lecturer**: One-to-Many / Many-to-One  
   - One department employs many lecturers; each lecturer belongs to exactly one department.
3. **Department — Course**: One-to-Many / Many-to-One  
   - One department offers many courses; each course belongs to exactly one department.
4. **Department — Lecturer (Head of Department)**: One-to-One  
   - Each department has exactly one head lecturer; a lecturer can head at most one department.
5. **Student — StudentProfile**: One-to-One  
   - Each student has exactly one profile; each profile belongs to exactly one student.
6. **Lecturer — Course**: Many-to-Many  
   - A lecturer can teach multiple courses and a course can be taught by multiple lecturers. No separate assignment entity is created.
7. **Student — Course via Registration**: Many-to-Many resolved by join entity (One-to-Many/Many-to-One)  
   - A student can register for multiple courses and a course can have multiple students. Registration stores required registration details.

## 5. Foreign Keys
- `students.department_id` → `departments.id`
- `students.profile_id` → `student_profiles.id` (unique)
- `lecturers.department_id` → `departments.id`
- `courses.department_id` → `departments.id`
- `departments.head_lecturer_id` → `lecturers.id` (unique)
- `registrations.student_id` → `students.id`
- `registrations.course_id` → `courses.id`
- `course_lecturers.lecturer_id` → `lecturers.id` (join table)
- `course_lecturers.course_id` → `courses.id` (join table)

## 6. Join Table
- **`course_lecturers`** implements the Lecturer–Course many-to-many teaching assignment without a separate entity.
  - Columns: `lecturer_id` (FK), `course_id` (FK)
  - Defined with `@JoinTable` on `Lecturer.courses`.

## 7. Join Entity
- **`Registration`** is required because the student–course association carries attributes: registration date, academic year, semester, registration status, and grade obtained.
  - Mapped as an entity/table `registrations` with `@ManyToOne` to `Student` and `Course`.
  - A uniqueness constraint on (`student_id`, `course_id`, `academic_year`, `semester`) prevents duplicate registration of the same student in the same course offering period.

## 8. Entity Selection Rationale
- **Department**: central organizational unit referenced by students, lecturers, and courses; required for ownership and head assignment.
- **Student**: core actor being admitted and registered; separated from profile to satisfy the explicit one-to-one personal profile requirement.
- **StudentProfile**: isolates personal/contact data (DOB, gender, phone, address) as a distinct entity belonging to exactly one student.
- **Lecturer**: represents teaching staff, department membership, and eligibility as department head and course instructor.
- **Course**: academic offering owned by a department and linked to lecturers and registrations.
- **Registration**: necessary join entity because the association has mandatory business attributes; a plain join table cannot store them.
- **RegistrationStatus enum**: constrains status values to the required domain (`PENDING`, `APPROVED`, `REJECTED`).

## 9. Implementation Notes
- Primary keys use `@Id` and `@GeneratedValue(strategy = GenerationType.IDENTITY)`.
- Relationships use `@OneToOne`, `@ManyToOne`, `@OneToMany`, and `@ManyToMany` with `@JoinColumn`/`@JoinTable` as appropriate.
- Repositories extend `JpaRepository` for each entity (`DepartmentRepository`, `StudentRepository`, `StudentProfileRepository`, `LecturerRepository`, `CourseRepository`, `RegistrationRepository`).
- PostgreSQL database name: `university_academic_db` (configured in `src/main/resources/application.properties`).
- Database credentials are read from the `DB_USERNAME` and `DB_PASSWORD` environment variables rather than stored in source.
- Hibernate DDL auto-update is enabled for schema generation during development/testing.
- The `departments.head_lecturer_id` FK is nullable so a department can be created before its lecturers; assign its head after creating an eligible lecturer.

## 10. Verification / Screenshots Guidance
To demonstrate table generation and relationship mappings:
1. Ensure PostgreSQL is running, database `university_academic_db` exists, and `DB_USERNAME`/`DB_PASSWORD` are set in the process environment.
2. Run `.\mvnw.cmd spring-boot:run` (or run `RegisterApplication`).
3. Capture application logs showing Hibernate DDL statements (`spring.jpa.show-sql=true`).
4. In PostgreSQL/pgAdmin/psql, capture screenshots of generated tables and constraints for:
   - `departments`, `students`, `student_profiles`, `lecturers`, `courses`, `registrations`, `course_lecturers`
   - Foreign keys and unique constraints (e.g. `students.profile_id`, `departments.head_lecturer_id`, registration unique constraint, join table keys).
