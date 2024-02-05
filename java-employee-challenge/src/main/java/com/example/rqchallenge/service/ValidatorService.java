package com.example.rqchallenge.service;

import com.example.rqchallenge.constants.MessageConstants;
import com.example.rqchallenge.exceptions.InvalidRequestException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class ValidatorService {
    public void validateEmployeeNameSearchString(String searchString) {
        if (StringUtils.isBlank(searchString))
            throw new InvalidRequestException(MessageConstants.INVALID_INPUT_PARAMETER_VALIDATION_MESSAGE);
    }

    public void validateEmployeeId(long employeeId) {
        if (employeeId < 0)
            throw new InvalidRequestException(MessageConstants.INVALID_INPUT_PARAMETER_VALIDATION_MESSAGE);
    }

    public void validateCreateEmployeeInputPayload(Map<String, Object> employeeInputData) {
        if (Objects.isNull(employeeInputData.get("name"))) {
            throw new InvalidRequestException(MessageConstants.NAME_FIELD_MANDATORY_VALIDATION_MESSAGE);
        }

        if (Objects.isNull(employeeInputData.get("salary"))) {
            throw new InvalidRequestException(MessageConstants.SALARY_FIELD_MANDATORY_VALIDATION_MESSAGE);
        }else if ((int) employeeInputData.getOrDefault("salary", -1) < 0) {
            throw new InvalidRequestException(MessageConstants.INVALID_SALARY_FIELD_VALIDATION_MESSAGE);
        }

        if (Objects.isNull(employeeInputData.get("age"))) {
            throw new InvalidRequestException(MessageConstants.AGE_FIELD_MANDATORY_VALIDATION_MESSAGE);
        } else if ((int) employeeInputData.getOrDefault("age", -1) < 0) {
            throw new InvalidRequestException(MessageConstants.INVALID_AGE_FIELD_VALIDATION_MESSAGE);
        }
    }
}
