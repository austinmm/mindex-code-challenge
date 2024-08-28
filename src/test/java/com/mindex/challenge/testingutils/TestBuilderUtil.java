package com.mindex.challenge.testingutils;

import com.mindex.challenge.data.Employee;

import static com.mindex.challenge.testingutils.TestFunctionUtil.extractObjectFromJsonFile;

public class TestBuilderUtil {

    public static Employee buildEmployee(){
        return extractObjectFromJsonFile("test_employee.json", Employee.class);
    }
}
