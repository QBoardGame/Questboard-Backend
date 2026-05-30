package com.Questboard.backend.modules.challenges.engine;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;

@Component
public class RuleEngine {

    public boolean evaluate(JsonNode normalizedEvent, JsonNode conditions) {
        if (conditions == null || conditions.isEmpty()) {
            return true;
        }

        JsonNode rules = conditions.get("rules");
        if (rules != null && rules.isArray()) {
            for (JsonNode rule : rules) {
                if (!evaluateRule(normalizedEvent, rule)) {
                    return false;
                }
            }
            return true;
        }

        if (conditions.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = conditions.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                JsonNode actual = resolveField(normalizedEvent, entry.getKey());
                if (!Operator.EQUALS.evaluate(actual, entry.getValue())) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    private boolean evaluateRule(JsonNode normalizedEvent, JsonNode rule) {
        if (rule == null || !rule.isObject()) {
            return false;
        }

        String field = rule.has("field") ? rule.get("field").asText(null) : null;
        String opValue = rule.has("op") ? rule.get("op").asText(null) : null;
        JsonNode value = rule.has("value") ? rule.get("value") : null;

        if (field == null || opValue == null) {
            return false;
        }

        Operator operator;
        try {
            operator = Operator.valueOf(opValue.toUpperCase());
        } catch (IllegalArgumentException e) {
            return false;
        }

        JsonNode actual = resolveField(normalizedEvent, field);
        return operator.evaluate(actual, value);
    }

    private JsonNode resolveField(JsonNode node, String fieldPath) {
        if (node == null || fieldPath == null) {
            return MissingNode.getInstance();
        }

        if (fieldPath.contains(".")) {
            String pointer = "/" + fieldPath.replace(".", "/");
            return node.at(pointer);
        }

        JsonNode value = node.get(fieldPath);
        return value != null ? value : MissingNode.getInstance();
    }
}
