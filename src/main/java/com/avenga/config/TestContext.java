package com.avenga.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>Represents the test context.</p>
 * <p>Used for managing the cleanup list to make sure that there will be no test data leftovers after the test run</p>
 */
@Slf4j
@Component
public class TestContext {

    private final Map<String, List<Object>> cleanUpList = new HashMap<>();

    /**
     * Adds objects to the cleanup list for further removal based on the test class name
     * when all the tests of the class are finished
     * @param item an object for removal
     * @param testClassName name of the test class
     */
    public void addToCleanUpList(Object item, String testClassName) {
        log.info("Adding item to the clean up list: {}", item);
        var list = cleanUpList.computeIfAbsent(testClassName, l -> new LinkedList<>());

        list.addFirst(item);
    }

    /**
     * Returns the list of the objects for removal based on the test class name
     * @param testClassName name of the test class
     * @return list of {@link Object}
     */
    public List<Object> getCleanUpList(String testClassName) {
        log.info("Getting the clean up list for the {} class", testClassName);
        return cleanUpList.get(testClassName);
    }
}
