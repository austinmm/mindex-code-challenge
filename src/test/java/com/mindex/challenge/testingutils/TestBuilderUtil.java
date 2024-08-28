package com.mindex.challenge.testingutils;

import com.mindex.challenge.model.Employee;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static com.mindex.challenge.testingutils.TestFunctionUtil.extractObjectFromJsonFile;

public class TestBuilderUtil {

    public static Employee buildEmployee() {
        return extractObjectFromJsonFile("test_employee.json", Employee.class);
    }

    public static HttpHeaders buildHttpHeadersWithJsonContentType() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
