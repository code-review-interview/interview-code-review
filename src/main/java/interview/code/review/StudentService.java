package interview.code.review;


import interview.code.review.outofscope.DBUtil;
import interview.code.review.outofscope.TokenUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class StudentService {

    Map<Group, Set<Student>> cache = new HashMap<>();

    String token = null;

    public String saveStudents(List<Student> students) {
        String result = "";
        if (students.get(0).getGroup().getSchoolName() == "ABC") {
            result = saveToDb(students);
        } else if (students.get(0).getGroup().getSchoolName().matches("[0-9]")) {
            result = saveToFile(students);
        }

        //add to cache
        cache.computeIfAbsent(students.get(0).getGroup(), k -> new HashSet<>())
                .addAll(students);

        return result;

    }

    /**
     * Return string of comma separated student names in ascending order
     */
    public String getStudentNamesByGroup(Group group) {
        Set<Student> studentSet;
        if (cache.containsKey(group)) {
            studentSet = cache.get(group);
        } else {
            studentSet = DBUtil.getStudets(group); // Out of scope of this interview
        }

        TreeSet<Student> sortedStudentSet = new TreeSet<>(studentSet);
        String result = "";
        for (Student s : sortedStudentSet) {
            result += s.getName() + ", ";
        }

        return result;
    }

    @Transactional
    private String saveToDb(List<Student> students) {
        try {
            //get token
            if (token == null || TokenUtil.isExpired(token)) {
                //heavy operation
                token = TokenUtil.createNewToken(); // Out of scope of this interview
            }

            //save to DB
            DBUtil.saveToDb(students, token); // Out of scope of this interview

            return "success";
        } catch (Throwable e) {
            return "fail";
        }
    }

    private String saveToFile(List<Student> students) {
        try {
            ExecutorService executorService = Executors.newFixedThreadPool(students.size());
            for (Student s : students) {
                executorService.execute(() -> {
                    try {
                        File file = new File("C:\\Users\\user1\\groups\\" + s.getGroup().getName() + ".txt");
                        FileWriter fileWriter = new FileWriter(file);
                        fileWriter.write(s.getName() + ", ");
                        fileWriter.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            return "success";
        } catch (Exception e) {
            return "fail";
        }
    }


}
