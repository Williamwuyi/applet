package com.jiebao.platfrom.common.domain;

import java.util.HashMap;

public class JiebaoResponse extends HashMap<String, Object> {

    private static final long serialVersionUID = -8713837118340960775L;

    public JiebaoResponse message(String message) {
        this.put("message", message);
        return this;
    }

    public JiebaoResponse data(Object data) {
        this.put("data", data);
        return this;
    }

    @Override
    public JiebaoResponse put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
