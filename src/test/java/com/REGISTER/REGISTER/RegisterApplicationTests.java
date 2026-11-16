package com.REGISTER.REGISTER;

import com.REGISTER.REGISTER.model.Course;
import com.REGISTER.REGISTER.model.Department;
import com.REGISTER.REGISTER.model.Lecturer;
import com.REGISTER.REGISTER.model.Registration;
import com.REGISTER.REGISTER.model.RegistrationStatus;
import com.REGISTER.REGISTER.model.Student;
import com.REGISTER.REGISTER.model.StudentProfile;
import com.REGISTER.REGISTER.repository.CourseRepository;
import com.REGISTER.REGISTER.repository.DepartmentRepository;
import com.REGISTER.REGISTER.repository.LecturerRepository;
import com.REGISTER.REGISTER.repository.RegistrationRepository;
import com.REGISTER.REGISTER.repository.StudentRepository;
import com.REGISTER.REGISTER.repository.StudentProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class RegisterApplicationTests {

	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	private LecturerRepository lecturerRepository;

	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private StudentProfileRepository studentProfileRepository;

	@Autowired
	private RegistrationRepository registrationRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void persistsEntitiesAndGeneratesRelationshipTables() {
		Department department = new Department();
		department.setName("Computer Science");
		department.setCode("CS");
		departmentRepository.save(department);

		Lecturer lecturer = new Lecturer();
		lecturer.setStaffNumber("L-100");
		lecturer.setFirstName("Ada");
		lecturer.setLastName("Lovelace");
		lecturer.setEmail("ada@example.edu");
		lecturer.setDepartment(department);
		lecturerRepository.save(lecturer);

		department.setHeadLecturer(lecturer);
		departmentRepository.save(department);

		Course course = new Course();
		course.setCourseCode("CS101");
		course.setTitle("Introduction to Computing");
		course.setCreditHours(3);
		course.setDepartment(department);
		courseRepository.save(course);
		lecturer.getCourses().add(course);
		lecturerRepository.save(lecturer);

		StudentProfile profile = new StudentProfile();
		profile.setDateOfBirth(LocalDate.of(2003, 5, 12));
		profile.setGender("Female");
		profile.setPhoneNumber("+15555550123");
		profile.setResidentialAddress("1 University Road");

		Student student = new Student();
		student.setStudentNumber("S-2025-001");
		student.setFirstName("Grace");
		student.setLastName("Hopper");
		student.setEmail("grace@example.edu");
		student.setDepartment(department);
		student.setProfile(profile);
		studentRepository.save(student);

		Registration registration = new Registration();
		registration.setStudent(student);
		registration.setCourse(course);
		registration.setRegistrationDate(LocalDate.of(2025, 9, 1));
		registration.setAcademicYear("2025/2026");
		registration.setSemester("Fall");
		registration.setRegistrationStatus(RegistrationStatus.APPROVED);
		registrationRepository.save(registration);

		assertEquals(1, studentRepository.count());
		assertEquals(1, registrationRepository.count());
		assertEquals(1, lecturerRepository.findById(lecturer.getId()).orElseThrow().getCourses().size());
		assertEquals(lecturer.getId(), departmentRepository.findById(department.getId())
				.orElseThrow().getHeadLecturer().getId());
		assertEquals(1, jdbcTemplate.queryForObject("select count(*) from course_lecturers", Integer.class));
		assertEquals(1, studentProfileRepository.count());
	}
}
