package com.jafin.excel;

import com.jafin.excel.test.Student;
import com.jafin.excel.test.TestActivity;

import org.junit.Test;

import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        List<Student> data = TestActivity.createData();
        int sum = data.parallelStream().mapToInt(Student::getAge).sum();
        int size = data.parallelStream().filter(student -> student.getAge() % 2 == 0).collect(Collectors.toList()).size();
        OptionalDouble sum1 =data.parallelStream().mapToInt(student -> student.getAge() * 2).average();
        assertEquals(90,OptionalDouble.of(0));
        System.out.println(sum1);
    }
}