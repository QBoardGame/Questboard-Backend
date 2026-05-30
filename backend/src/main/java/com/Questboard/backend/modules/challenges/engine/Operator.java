package com.Questboard.backend.modules.challenges.engine;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public enum Operator {
    EQUALS,
    NOT_EQUALS,
    GT,
    GTE,
    LT,
    LTE,
    IN,
    EXISTS;

    public boolean evaluate(JsonNode actual, JsonNode expected) {
        switch (this) {
            case EQUALS:
                return compareEquals(actual, expected);
            case NOT_EQUALS:
                return !compareEquals(actual, expected);
            case GT:
                return compareNumeric(actual, expected) > 0;
            case GTE:
                return compareNumeric(actual, expected) >= 0;
            case LT:
                return compareNumeric(actual, expected) < 0;
            case LTE:
                return compareNumeric(actual, expected) <= 0;
            case IN:
                return contains(actual, expected);
            case EXISTS:
                return exists(actual, expected);
            default:
                return false;
        }
    }

    private boolean compareEquals(JsonNode actual, JsonNode expected) {
        if (actual == null || actual.isMissingNode() || actual.isNull()) {
            return expected == null || expected.isNull();
        }

        if (expected == null || expected.isMissingNode() || expected.isNull()) {
            return false;
        }

        if (actual.isNumber() && expected.isNumber()) {
            return actual.decimalValue().compareTo(expected.decimalValue()) == 0;
        }

        if (actual.isBoolean() && expected.isBoolean()) {
            return actual.asBoolean() == expected.asBoolean();
        }

        return actual.asText().equalsIgnoreCase(expected.asText());
    }

    private int compareNumeric(JsonNode actual, JsonNode expected) {
        if (actual == null || actual.isMissingNode() || expected == null || expected.isMissingNode()) {
            throw new IllegalArgumentException("Numeric comparison requires both values to exist");
        }

        if (!actual.isNumber() || !expected.isNumber()) {
            throw new IllegalArgumentException("Numeric comparison requires numeric values");
        }

        return actual.decimalValue().compareTo(expected.decimalValue());
    }

    private boolean contains(JsonNode actual, JsonNode expected) {
        if (actual == null || actual.isMissingNode() || actual.isNull() || expected == null || expected.isMissingNode() || expected.isNull()) {
            return false;
        }

        if (expected.isArray()) {
            ArrayNode array = (ArrayNode) expected;
            for (JsonNode candidate : array) {
                if (compareEquals(actual, candidate)) {
                    return true;
                }
            }
            return false;
        }

        if (actual.isArray()) {
            for (JsonNode candidate : actual) {
                if (compareEquals(candidate, expected)) {
                    return true;
                }
            }
            return false;
        }

        return compareEquals(actual, expected);
    }

    private boolean exists(JsonNode actual, JsonNode expected) {
        boolean shouldExist = expected == null || expected.isMissingNode() || expected.isNull() || expected.asBoolean(true);
        boolean isPresent = actual != null && !actual.isMissingNode() && !actual.isNull();
        return shouldExist == isPresent;
    }
}
