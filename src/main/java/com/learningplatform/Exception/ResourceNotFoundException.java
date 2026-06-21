package com.learningplatform.Exception;
//
//public class ResourceNotFoundException extends RuntimeException {
//
//    public ResourceNotFoundException(String resourceName,
//                                     String fieldName,
//                                     Object fieldValue) {
//
//        super(String.format(
//                "%s not found with %s : %s",
//                resourceName,
//                fieldName,
//                fieldValue));
//    }
//}

public class ResourceNotFoundException extends RuntimeException {

    private int statusCode = 404;

    public ResourceNotFoundException(
            String resource,
            String field,
            Object value) {

        super(resource + " not found with "
                + field + " : " + value);
    }

    public int getStatusCode() {
        return statusCode;
    }
}