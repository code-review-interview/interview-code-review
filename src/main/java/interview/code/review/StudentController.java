package interview.code.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StudentController {

    @Autowired
    StudentService studentService;

    @GetMapping("/saveStudents")
    public String saveStudents(List<Student> students) {
        return studentService.saveStudents(students);
    }

    /**
     * Return string of comma separated student names in ascending order
     */
    @GetMapping("/get-student-names-by-group")
    public String getStudentNamesByGroup(Group group) {
        return studentService.getStudentNamesByGroup(group);
    }

}
