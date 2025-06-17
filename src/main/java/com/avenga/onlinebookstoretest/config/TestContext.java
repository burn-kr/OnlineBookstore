package com.avenga.onlinebookstoretest.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class TestContext {

    private final Map<String, List<Object>> cleanUpList = new HashMap<>();

    public void addToCleanUpList(Object item, String testClassName) {
        log.info("Adding item to the clean up list: {}", item);
        var list = cleanUpList.computeIfAbsent(testClassName, l -> new LinkedList<>());

        list.addFirst(item);
    }

    public List<Object> getCleanUpList(String testClassName) {
        log.info("Getting the clean up list for the {} class", testClassName);
        return cleanUpList.get(testClassName);
    }
}
